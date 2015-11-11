package com.milaboratory.core.alignment.kaligner2;

import cc.redberry.pipe.CUtils;
import cc.redberry.pipe.Processor;
import com.milaboratory.core.alignment.AlignmentUtils;
import com.milaboratory.core.sequence.NucleotideSequence;
import com.milaboratory.util.RandomUtil;

import java.util.function.Consumer;

public class Benchmark implements Processor<BenchmarkInput, BenchmarkResults> {
    final long maxExecutionTime;
    ExceptionListener exceptionListener;
    Consumer<KAlignmentResult2<Integer>> resultCallback;

    public Benchmark(long maxExecutionTime) {
        this.maxExecutionTime = maxExecutionTime;
    }

    @Override
    public BenchmarkResults process(BenchmarkInput input) {
        RandomUtil.reseedThreadLocal(input.challenge.seed);
        KAligner2Statistics stat = new KAligner2Statistics();
        KAligner2<Integer> aligner = new KAligner2<>(input.params, stat);
        NucleotideSequence[] db = input.challenge.getDB();
        for (int i = 0; i < db.length; i++)
            aligner.addReference(db[i], i);

        long executionTime = 0;
        int processedQueries = 0;
        int mismatched = 0;
        int noHits = 0;

        long start = System.nanoTime();

        OUTER:
        for (KAligner2Query query : CUtils.it(input.challenge.queries())) {
            if (System.nanoTime() - start > maxExecutionTime)
                break;

            long seed = RandomUtil.reseedThreadLocal();
            try {
                long b = System.nanoTime();
                KAlignmentResult2<Integer> result = aligner.align(query.query);
                ++processedQueries;
                executionTime += (System.nanoTime() - b);

                if (resultCallback != null)
                    resultCallback.accept(result);

                if (!result.hasHits()) {
                    ++noHits;
                    continue;
                }

                for (KAlignmentHit2<Integer> hit : result.getHits())
                    if (!query.query.getRange(hit.getAlignment().getSequence2Range())
                            .equals(AlignmentUtils.getAlignedSequence2Part(hit.getAlignment())))
                        throw new RuntimeException("Wrong answer.");

                float topScore = result.getHits().get(0).getAlignment().getScore();
                for (KAlignmentHit2<Integer> hit : result.getHits()) {
                    if (hit.getAlignment().getScore() != topScore)
                        break;
                    if (hit.getRecordPayload().equals(query.targetId))
                        continue OUTER;
                }
                ++mismatched;
            } catch (Exception e) {
                if (exceptionListener != null)
                    exceptionListener.onException(new ExceptionData(seed, e, db, query.query, input));
                else
                    throw e;
            }
        }

        return new BenchmarkResults(input, stat, executionTime, processedQueries, mismatched, noHits);
    }

    public void setExceptionListener(ExceptionListener exceptionListener) {
        this.exceptionListener = exceptionListener;
    }

    public void setResultCallback(Consumer<KAlignmentResult2<Integer>> resultCallback) {
        this.resultCallback = resultCallback;
    }

    public interface ExceptionListener {
        void onException(ExceptionData exceptionData);
    }

    public static final class ExceptionData {
        public final long seed;
        public final Throwable exception;
        public final NucleotideSequence[] db;
        public final NucleotideSequence query;
        public final BenchmarkInput input;

        public ExceptionData(long seed, Throwable exception, NucleotideSequence[] db, NucleotideSequence query, BenchmarkInput input) {
            this.seed = seed;
            this.exception = exception;
            this.db = db;
            this.query = query;
            this.input = input;
        }
    }
}

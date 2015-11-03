/*
 * Copyright 2015 MiLaboratory.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.milaboratory.core.alignment.kaligner2;

import cc.redberry.pipe.CUtils;
import cc.redberry.pipe.Processor;
import com.milaboratory.core.alignment.AlignmentUtils;
import com.milaboratory.core.alignment.KAligner;
import com.milaboratory.core.alignment.KAlignmentHit;
import com.milaboratory.core.alignment.KAlignmentResult;
import com.milaboratory.core.sequence.NucleotideSequence;
import com.milaboratory.util.RandomUtil;

public class Benchmark1 implements Processor<BenchmarkInput1, BenchmarkResults1> {
    final long maxExecutionTime;
    ExceptionListener exceptionListener;

    public Benchmark1(long maxExecutionTime) {
        this.maxExecutionTime = maxExecutionTime;
    }

    @Override
    public BenchmarkResults1 process(BenchmarkInput1 input) {
        RandomUtil.reseedThreadLocal(input.challenge.seed);
        KAligner<Integer> aligner = new KAligner<>(input.params);
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
                KAlignmentResult<Integer> result = aligner.align(query.query);
                ++processedQueries;
                executionTime += (System.nanoTime() - b);

                if (!result.hasHits()) {
                    ++noHits;
                    continue;
                }

                for (KAlignmentHit<Integer> hit : result.getHits())
                    if (!query.query.getRange(hit.getAlignment().getSequence2Range())
                            .equals(AlignmentUtils.getAlignedSequence2Part(hit.getAlignment())))
                        throw new RuntimeException("Wrong answer.");

                float topScore = result.getHits().get(0).getAlignment().getScore();
                for (KAlignmentHit<Integer> hit : result.getHits()) {
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

        return new BenchmarkResults1(input, executionTime, processedQueries, mismatched, noHits);
    }

    public void setExceptionListener(ExceptionListener exceptionListener) {
        this.exceptionListener = exceptionListener;
    }

    public interface ExceptionListener {
        void onException(ExceptionData exceptionData);
    }

    public static final class ExceptionData {
        public final long seed;
        public final Throwable exception;
        public final NucleotideSequence[] db;
        public final NucleotideSequence query;
        public final BenchmarkInput1 input;

        public ExceptionData(long seed, Throwable exception, NucleotideSequence[] db, NucleotideSequence query, BenchmarkInput1 input) {
            this.seed = seed;
            this.exception = exception;
            this.db = db;
            this.query = query;
            this.input = input;
        }
    }
}

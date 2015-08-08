package com.milaboratory.core.alignment.blast;

import cc.redberry.pipe.InputPort;
import cc.redberry.pipe.OutputPort;
import cc.redberry.pipe.OutputPortCloseable;
import cc.redberry.pipe.blocks.Buffer;
import com.milaboratory.core.alignment.batch.*;
import com.milaboratory.core.io.sequence.fasta.FastaWriter;
import com.milaboratory.core.sequence.Sequence;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class BlastAligner<S extends Sequence<S>> implements PipedBatchAligner<S, BlastHitInfo> {
    private static final String OUTFMT = "7 btop sstart send qstart qend score bitscore sseqid qseq sseq";
    private static final String QUERY_ID_PREFIX = "Q";
    final BlastDB database;
    final BlastAlignerParameters parameters;
    final int batchSize;

    public BlastAligner(BlastDB database) {
        this(database, null, -1);
    }

    public BlastAligner(BlastDB database, BlastAlignerParameters parameters, int batchSize) {
        this.database = database;
        this.parameters = parameters;
        this.batchSize = batchSize;
    }

    @Override
    public <Q> OutputPort<PipedAlignmentResult<AlignmentHit<S, BlastHitInfo>, Q>> align(OutputPort<Q> input, SequenceExtractor<Q, S> extractor) {
        return new BlastWorker<>(input, extractor);
    }

    @Override
    public <Q extends HasSequence<S>> OutputPort<PipedAlignmentResult<AlignmentHit<S, BlastHitInfo>, Q>> align(OutputPort<Q> input) {
        return new BlastWorker<>(input, BatchAlignmentUtil.DUMMY_EXTRACTOR);
    }

    private class BlastWorker<S extends Sequence<S>, Q> implements
            OutputPortCloseable<PipedAlignmentResult<AlignmentHit<S, BlastHitInfo>, Q>> {
        final ConcurrentMap<String, Q> queryMapping = new ConcurrentHashMap<>();
        final Buffer<PipedAlignmentResult<AlignmentHit<S, BlastHitInfo>, Q>> resultsBuffer;
        final Process process;
        final BlastSequencePusher<S, Q> pusher;
        final BlastResultsFetcher<S, Q> fetcher;

        public BlastWorker(OutputPort<Q> source, SequenceExtractor<Q, S> sequenceExtractor) {
            this.resultsBuffer = new Buffer<>(32);
            try {
                ProcessBuilder processBuilder = Blast.getProcessBuilder(
                        Blast.toBlastCommand(database.getAlphabet()), "-db", database.getName(), "-outfmt", OUTFMT);

                processBuilder.redirectErrorStream(false);
                if (batchSize != -1)
                    processBuilder.environment().put("BATCH_SIZE", Integer.toString(batchSize));

                this.process = processBuilder.start();
                this.pusher = new BlastSequencePusher<>(source, sequenceExtractor, queryMapping,
                        this.process.getOutputStream());
                this.fetcher = new BlastResultsFetcher<>(this.resultsBuffer.createInputPort(),
                        this.process.getInputStream());

                this.pusher.start();
                this.fetcher.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public PipedAlignmentResult<AlignmentHit<S, BlastHitInfo>, Q> take() {
            return resultsBuffer.take();
        }

        @Override
        public void close() {
            if (pusher.source instanceof OutputPortCloseable)
                ((OutputPortCloseable) pusher.source).close();
        }
    }

    private class BlastResultsFetcher<S extends Sequence<S>, Q> extends Thread {
        final InputPort<PipedAlignmentResult<AlignmentHit<S, BlastHitInfo>, Q>> resultsInputPort;
        final BufferedReader reader;

        public BlastResultsFetcher(InputPort<PipedAlignmentResult<AlignmentHit<S, BlastHitInfo>, Q>> resultsInputPort,
                                   InputStream stream) {
            this.resultsInputPort = resultsInputPort;
            this.reader = new BufferedReader(new InputStreamReader(stream));
        }

        @Override
        public void run() {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    //if (line.contains("hits found")) {
                    //    num = parseInt(line.replace("#", "").replace("hits found", "").trim());
                    //    if (num == 0)
                    //        break;
                    //} else if (num != -1 && !line.startsWith("#")) {
                    //    hits.add(parseLine(line, sequences[i].getAlphabet()));
                    //    if (++done == num)
                    //        break;
                    //}
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                // Closing port
                resultsInputPort.put(null);
            }
        }
    }

    private class BlastSequencePusher<S extends Sequence<S>, Q> extends Thread {
        final AtomicLong counter = new AtomicLong();
        final OutputPort<Q> source;
        final SequenceExtractor<Q, S> sequenceExtractor;
        final ConcurrentMap<String, Q> queryMapping;
        final FastaWriter<S> writer;

        public BlastSequencePusher(OutputPort<Q> source, SequenceExtractor<Q, S> sequenceExtractor,
                                   ConcurrentMap<String, Q> queryMapping,
                                   OutputStream stream) {
            this.source = source;
            this.sequenceExtractor = sequenceExtractor;
            this.queryMapping = queryMapping;
            this.writer = new FastaWriter<S>(stream, FastaWriter.DEFAULT_MAX_LENGTH);
        }

        @Override
        public void run() {
            Q query;

            while ((query = source.take()) != null) {
                S sequence = sequenceExtractor.extract(query);
                String name = QUERY_ID_PREFIX + counter.incrementAndGet();
                queryMapping.put(name, query);
                writer.write(name, sequence);
            }

            writer.close();
        }
    }
}

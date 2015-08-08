package com.milaboratory.core.alignment.batch;

import cc.redberry.pipe.CUtils;
import cc.redberry.pipe.OutputPort;
import cc.redberry.pipe.Processor;
import cc.redberry.pipe.blocks.ParallelProcessor;
import com.milaboratory.core.sequence.Sequence;

/**
 * Created by dbolotin on 07/08/15.
 */
public abstract class AbstractBatchAligner<S extends Sequence<S>, P, H extends AlignmentHit<S, P>>
        implements BatchAligner<S, P>, PipedBatchAligner<S, P> {
    /**
     * 0    -> Runtime.getRuntime().availableProcessors()
     * 1    -> process in the same thread as take() method call
     * 2... -> parallel processor
     */
    protected volatile int threads = 1;

    @Override
    public abstract AlignmentResult<H> align(S sequence);

    @Override
    public <Q> OutputPort<PipedAlignmentResult<H, Q>> align(OutputPort<Q> input, final SequenceExtractor<Q, S> extractor) {
        Processor<Q, PipedAlignmentResult<H, Q>> proc = new Processor<Q, PipedAlignmentResult<H, Q>>() {
            @Override
            public PipedAlignmentResult<H, Q> process(Q input) {
                S seq = extractor.extract(input);
                AlignmentResult<H> result = align(seq);
                return new PipedAlignmentResultImpl<>(result.getHits(), input);
            }
        };

        return wrapPipe(proc, input);
    }

    @Override
    public <Q extends HasSequence<S>> OutputPort<PipedAlignmentResult<H, Q>> align(OutputPort<Q> input) {
        Processor<Q, PipedAlignmentResult<H, Q>> proc = new Processor<Q, PipedAlignmentResult<H, Q>>() {
            @Override
            public PipedAlignmentResult<H, Q> process(Q input) {
                AlignmentResult<H> result = align(input.getSequence());
                return new PipedAlignmentResultImpl<>(result.getHits(), input);
            }
        };

        return wrapPipe(proc, input);
    }

    private <Q> OutputPort<PipedAlignmentResult<H, Q>> wrapPipe(Processor<Q, PipedAlignmentResult<H, Q>> proc, OutputPort<Q> input) {
        if (threads == 1)
            return CUtils.wrap(input, proc);

        int t = (threads == 0 ? Runtime.getRuntime().availableProcessors() : threads);

        return new ParallelProcessor<>(input, proc, t);
    }
}

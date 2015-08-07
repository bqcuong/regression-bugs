package com.milaboratory.core.alignment;

import cc.redberry.pipe.InputPort;
import cc.redberry.pipe.OutputPort;
import com.milaboratory.core.sequence.Sequence;

/**
 * Represents aligner that can align a sequence against a set of other sequences. This type of aligner works only as
 * pipe processor.
 *
 * @param <S> sequence type
 * @param <P> type of record payload, used to store additional information sequence in base to simplify it's subsequent
 *            identification in result (e.g. {@link Integer} to just index sequences, or
 *            {@link com.milaboratory.core.alignment.blast.BlastDBRecord} etc...)
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public interface PipedBatchAligner<S extends Sequence<S>, P> {
    /**
     * Starts processing of input sequences and returns pipe of results.
     *
     * @param input     pipe of queries
     * @param extractor extractor of sequences from query object
     * @param <Q>       type of query object
     * @return pipe of alignment results
     */
    <Q> OutputPort<PipedAlignmentResult<S, P, Q>> align(OutputPort<Q> input, SequenceExtractor<Q, S> extractor);

    /**
     * Starts processing of input sequences and returns pipe of results.
     *
     * @param input pipe of queries
     * @param <Q>   type of query objects
     * @return pipe of alignment results
     */
    <Q extends HasSequence<S>> OutputPort<PipedAlignmentResult<S, P, Q>> align(OutputPort<Q> input);
}

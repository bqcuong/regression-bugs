package com.milaboratory.core.sequence;

import com.milaboratory.core.Range;

/**
 * Common interface for all objects representing sequence-like objects like (parent of all subtypes of {@link
 * com.milaboratory.core.sequence.Sequence}; {@link com.milaboratory.core.sequence.SequenceQuality}; {@link
 * com.milaboratory.core.sequence.SequenceWithQuality})
 *
 * @param <S> type of seq (type of extending class)
 */
public interface Seq<S extends Seq<S>> {
    /**
     * Returns a subsequence of this bounded by specified {@code range}.
     *
     * @param range a range that defines starting (inclusive) and ending (exclusive) points of subsequence
     * @return subsequence of this bounded by specified {@code range}.
     * @throws java.lang.IndexOutOfBoundsException if {@code from} orj {@code to} is out of this sequence range
     */
    S getRange(Range range);

    /**
     * Returns a subsequence of this starting at {@code from} (inclusive) and ending at {@code to} (exclusive).
     *
     * @param from starting point of subsequence (inclusive)
     * @param to   ending point of subsequence (exclusive)
     * @return subsequence of this starting at {@code from} (inclusive) and ending at {@code to} (exclusive)
     * @throws IndexOutOfBoundsException if {@code from} or {@code to} is out of this sequence range
     * @throws IllegalArgumentException  if {@code from >= to}
     */
    S getRange(int from, int to);

    /**
     * Returns size of this sequence
     *
     * @return size of this sequence
     */
    int size();

    /**
     * Returns a builder for corresponding seq type.
     *
     * @return builder for corresponding seq type
     */
    SeqBuilder<S> getBuilder();

    /**
     * Returns a concatenation of this and {@code other} sequence (so this will be followed by {@code other} in the
     * result).
     *
     * @param other other sequence
     * @return concatenation of this and {@code other} sequences
     */
    S concatenate(S other);
}

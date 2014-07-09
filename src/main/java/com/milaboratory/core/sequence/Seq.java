package com.milaboratory.core.sequence;

import com.milaboratory.core.Range;

/**
 * Common class for all objects representing sequence-like objects like (parent of all subtypes of {@link
 * com.milaboratory.core.sequence.Sequence}; {@link com.milaboratory.core.sequence.SequenceQuality}; {@link
 * com.milaboratory.core.sequence.SequenceWithQuality})
 *
 * @param <S> type of seq (type of extending class)
 */
public abstract class Seq<S extends Seq<S>> {
    /**
     * Returns a subsequence of this bounded by specified {@code range}.
     *
     * @param range a range that defines starting (inclusive) and ending (exclusive) points of subsequence
     * @return subsequence of this bounded by specified {@code range}.
     * @throws java.lang.IndexOutOfBoundsException if {@code from} orj {@code to} is out of this sequence range
     */
    public S getRange(Range range) {
        if (range.isReverse())
            throw new IllegalArgumentException("Reverse range not supported.");
        return getRange(range.getFrom(), range.getTo());
    }

    /**
     * Returns a subsequence of this starting at {@code from} (inclusive) and ending at {@code to} (exclusive).
     *
     * @param from starting point of subsequence (inclusive)
     * @param to   ending point of subsequence (exclusive)
     * @return subsequence of this starting at {@code from} (inclusive) and ending at {@code to} (exclusive)
     * @throws java.lang.IndexOutOfBoundsException if {@code from} or {@code to} is out of this sequence range
     * @throws java.lang.IllegalArgumentException  if {@code from >= to}
     */
    public abstract S getRange(int from, int to);

    /**
     * Returns size of this sequence
     *
     * @return size of this sequence
     */
    public abstract int size();

    /**
     * Returns a builder for corresponding seq type.
     *
     * @return builder for corresponding seq type
     */
    public abstract SeqBuilder<S> getBuilder();

    /**
     * Returns a concatenation of this and {@code other} sequence (so this will be followed by {@code other} in the
     * result).
     *
     * @param other other sequence
     * @return concatenation of this and {@code other} sequences
     */
    public S concatenate(S other) {
        return getBuilder()
                .ensureCapacity(other.size() + size())
                .append((S) this).append(other).createAndDestroy();
    }
}

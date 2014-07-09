package com.milaboratory.core.sequence;

/**
 * Interface for classes that build seq objects.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Poslavsky Stanislav (stvlpos@mail.ru)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 * @see com.milaboratory.core.sequence.Seq
 */
public interface SeqBuilder<S extends Seq<S>> {
    /**
     * Size of sequence being created.
     *
     * @return size
     */
    int size();

    /**
     * Ensures capacity of this builder.
     *
     * @param capacity capacity
     * @return this
     */
    SequenceBuilder<S> ensureCapacity(int capacity);

    /**
     * Creates the sequence and destroys this builder.
     *
     * @return created sequence
     */
    S createAndDestroy();

    /**
     * Appends seq.
     *
     * @param seq seq
     * @return this
     */
    SequenceBuilder<S> append(S seq);

    /**
     * Returns a deep copy of this builder
     *
     * @return a deep copy of this builder
     */
    SequenceBuilder<S> clone();
}

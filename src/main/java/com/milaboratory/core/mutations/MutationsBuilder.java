package com.milaboratory.core.mutations;

import com.milaboratory.core.sequence.Alphabet;
import com.milaboratory.core.sequence.Sequence;
import com.milaboratory.util.ArraysUtils;

import java.util.Arrays;

import static com.milaboratory.core.mutations.Mutation.*;

public final class MutationsBuilder<S extends Sequence<? extends S>> {
    private final Alphabet<S> alphabet;
    private final boolean reversed;
    private int[] mutations = null;
    private int size = 0;

    public MutationsBuilder(Alphabet<S> alphabet, boolean reversed, int[] mutations, int size) {
        this.alphabet = alphabet;
        this.reversed = reversed;
        this.mutations = mutations;
        this.size = size;
    }

    public MutationsBuilder(Alphabet<S> alphabet) {
        this(alphabet, false);
    }

    public MutationsBuilder(Alphabet<S> alphabet, boolean reversed) {
        this.alphabet = alphabet;
        this.reversed = reversed;
    }

    public int size() {
        return size;
    }

    private void ensureInternalCapacity(int newSize) {
        if (size == -1)
            throw new IllegalStateException("Destroyed.");
        if (mutations == null && newSize != 0)
            mutations = new int[Math.max(newSize, 10)];
        if (mutations.length < newSize)
            mutations = Arrays.copyOf(mutations, Math.max(newSize, 3 * mutations.length / 2 + 1));
    }

    public MutationsBuilder<S> ensureCapacity(int capacity) {
        if (size == -1)
            throw new IllegalStateException("Destroyed.");
        if (capacity > 0) {
            if (mutations == null)
                mutations = new int[capacity];
            else if (capacity > mutations.length)
                mutations = Arrays.copyOf(mutations, capacity);
        }
        return this;
    }

    public Mutations<S> createAndDestroy() {
        Mutations<S> muts;

        final int[] m;

        if (mutations == null)
            m = new int[0];
        else if (mutations.length == size)
            m = mutations;
        else
            m = Arrays.copyOf(mutations, size);

        mutations = null;
        size = -1;

        if (reversed)
            ArraysUtils.reverse(m);

        return new Mutations<>(alphabet, m,
                true);
    }

    public MutationsBuilder<S> append(int mutation) {
        ensureInternalCapacity(size + 1);

        if (size > 0 &&
                (reversed ?
                        getPosition(mutations[size - 1]) < getPosition(mutation) :
                        getPosition(mutations[size - 1]) > getPosition(mutation)))
            throw new IllegalArgumentException("Mutations must be appended in descending/ascending order (position)" +
                    "depending on the value of reverse flag.");

        mutations[size++] = mutation;
        return this;
    }

    public MutationsBuilder<S> appendSubstitution(int position, int from, int to) {
        return append(createSubstitution(position, from, to));
    }

    public MutationsBuilder<S> appendDeletion(int position, int from) {
        return append(createDeletion(position, from));
    }

    public MutationsBuilder<S> appendInsertion(int position, int to) {
        return append(createInsertion(position, to));
    }

    public MutationsBuilder<S> clone() {
        return new MutationsBuilder<>(alphabet,
                reversed,
                mutations == null ? null : mutations.clone(),
                size);
    }
}

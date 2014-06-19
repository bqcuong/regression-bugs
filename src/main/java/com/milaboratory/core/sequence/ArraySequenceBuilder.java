package com.milaboratory.core.sequence;

import java.util.Arrays;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public final class ArraySequenceBuilder<S extends AbstractArraySequence> implements SequenceBuilder<S> {
    private final AbstractArrayAlphabet<S> alphabet;
    private byte[] data;
    private int size = 0;

    public ArraySequenceBuilder(AbstractArrayAlphabet<S> alphabet) {
        this.alphabet = alphabet;
    }

    private ArraySequenceBuilder(byte[] data, int size, AbstractArrayAlphabet<S> alphabet) {
        this.data = data;
        this.size = size;
        this.alphabet = alphabet;
    }

    @Override
    public int size() {
        return size;
    }

    private void ensureInternalCapacity(int newSize) {
        if (size == -1)
            throw new IllegalStateException("Destroyed.");
        if (data == null && newSize != 0)
            data = new byte[Math.max(newSize, 10)];
        if (data.length < newSize)
            data = Arrays.copyOf(data, Math.max(newSize, 3 * data.length / 2 + 1));
    }

    @Override
    public SequenceBuilder<S> ensureCapacity(int capacity) {
        if (size == -1)
            throw new IllegalStateException("Destroyed.");
        if (capacity > 0) {
            if (data == null)
                data = new byte[capacity];
            else if (capacity > data.length)
                data = Arrays.copyOf(data, capacity);
        }
        return this;
    }

    @Override
    public S createAndDestroy() {
        S seq;

        if(data == null)
            return alphabet.createUnsafe(new byte[0]);

        if (data.length == size)
            seq = alphabet.createUnsafe(data);
        else
            seq = alphabet.createUnsafe(Arrays.copyOf(data, size));
        data = null;
        size = -1;
        return seq;
    }

    @Override
    public SequenceBuilder<S> append(byte letter) {
        ensureInternalCapacity(size + 1);
        data[size++] = letter;
        return this;
    }

    @Override
    public SequenceBuilder<S> append(S sequence) {
        ensureInternalCapacity(size + sequence.size());
        System.arraycopy(sequence.data, 0, data, size, sequence.size());
        size += sequence.size();
        return this;
    }

    @Override
    public SequenceBuilder<S> clone() {
        return new ArraySequenceBuilder(data == null ? null : data.clone(), size, alphabet);
    }
}

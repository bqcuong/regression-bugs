package com.milaboratory.core.sequence;

import java.util.Arrays;

/**
 * Created by poslavsky on 09/07/14.
 */
public abstract class ArraySeqBuilder<S extends Seq<S>> implements SequenceBuilder<S> {
    byte[] data;
    int size = 0;

    ArraySeqBuilder() {
    }

    ArraySeqBuilder(byte[] data, int size) {
        this.data = data;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    private void ensureInternalCapacity(int newSize) {
        if (size == -1)
            throw new IllegalStateException("Destroyed.");
        if (data == null)
            if (newSize != 0)
                data = new byte[Math.max(newSize, 10)];
            else
                return;
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

    abstract S createUnsafe(byte[] b);

    abstract byte[] getUnsafe(S s);

    @Override
    public S createAndDestroy() {
        S seq;

        if (data == null)
            return createUnsafe(new byte[0]);

        if (data.length == size)
            seq = createUnsafe(data);
        else
            seq = createUnsafe(Arrays.copyOf(data, size));
        data = null;
        size = -1;
        return seq;
    }

    @Override
    public SequenceBuilder<S> set(int position, byte letter) {
        if (position < 0 || position >= size)
            throw new IndexOutOfBoundsException();
        data[position] = letter;
        return this;
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
        System.arraycopy(getUnsafe(sequence), 0, data, size, sequence.size());
        size += sequence.size();
        return this;
    }

    public abstract SequenceBuilder<S> clone();
}

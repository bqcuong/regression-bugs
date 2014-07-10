package com.milaboratory.core.sequence;

import java.util.Arrays;

public abstract class ArraySeqBuilder<S extends AbstractSeq<S>> implements SeqBuilder<S> {
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

    protected void ensureInternalCapacity(int newSize) {
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
    public SeqBuilder<S> ensureCapacity(int capacity) {
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
    public ArraySeqBuilder<S> append(S seq) {
        ensureInternalCapacity(size + seq.size());
        System.arraycopy(getUnsafe(seq), 0, data, size, seq.size());
        size += seq.size();
        return this;
    }

    @Override
    public abstract ArraySeqBuilder<S> clone();
}

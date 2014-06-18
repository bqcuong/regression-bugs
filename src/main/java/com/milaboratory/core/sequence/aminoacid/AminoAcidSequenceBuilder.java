package com.milaboratory.core.sequence.aminoacid;

import com.milaboratory.core.sequence.SequenceBuilder;

import java.util.Arrays;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public class AminoAcidSequenceBuilder implements SequenceBuilder<AminoAcidSequence> {
    private byte[] data;
    private int size = 0;

    public AminoAcidSequenceBuilder() {
    }

    public AminoAcidSequenceBuilder(int capacity) {
        ensureCapacity(capacity);
    }

    private AminoAcidSequenceBuilder(byte[] data, int size) {
        this.data = data;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public SequenceBuilder<AminoAcidSequence> ensureCapacity(int capacity) {
        if (data == null && capacity > 0) {
            data = new byte[capacity];
            return this;
        }
        if (capacity > data.length)
            data = Arrays.copyOf(data, capacity);
        return this;
    }

    private void ensureInternalCapacity(int newSize) {
        if (data == null && newSize != 0)
            data = new byte[Math.max(newSize, 10)];
        if (data.length >= newSize)
            return;
        data = Arrays.copyOf(data, Math.max(newSize, 3 * data.length / 2 + 1));
    }

    @Override
    public AminoAcidSequence createAndDestroy() {
        if (size == 0)
            return AminoAcidSequence.EMPTY;
        AminoAcidSequence seq;
        if (data.length == size)
            seq = new AminoAcidSequence(data, true);
        else
            seq = new AminoAcidSequence(Arrays.copyOf(data, size), true);
        data = null;
        return seq;
    }

    @Override
    public SequenceBuilder<AminoAcidSequence> append(byte letter) {
        ensureInternalCapacity(size + 1);
        data[size++] = letter;
        return this;
    }

    @Override
    public SequenceBuilder<AminoAcidSequence> append(AminoAcidSequence sequence) {
        ensureInternalCapacity(size + sequence.size());
        System.arraycopy(sequence.data, 0, data, size, sequence.size());
        size += sequence.size();
        return this;
    }

    @Override
    public SequenceBuilder<AminoAcidSequence> clone() {
        return new AminoAcidSequenceBuilder(data.clone(), size);
    }
}

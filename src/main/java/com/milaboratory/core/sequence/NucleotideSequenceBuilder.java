/*
 * MiTCR <http://milaboratory.com>
 *
 * Copyright (c) 2010-2013:
 *     Bolotin Dmitriy     <bolotin.dmitriy@gmail.com>
 *     Chudakov Dmitriy    <chudakovdm@mail.ru>
 *
 * MiTCR is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.milaboratory.core.sequence;

import com.milaboratory.util.Bit2Array;

/**
 * {@code SequenceBuilder} for nucleotide sequences.
 *
 * @see com.milaboratory.core.sequence.NucleotideSequence
 * @see com.milaboratory.core.sequence.SequenceBuilder
 */
public final class NucleotideSequenceBuilder implements SequenceBuilder<NucleotideSequence> {
    private Bit2Array storage;
    private int size = 0;

    /**
     * Creates {@code SequenceBuilder} with empty initial buffer.
     */
    public NucleotideSequenceBuilder() {
        this.storage = null;
    }

    private NucleotideSequenceBuilder(Bit2Array storage, int size) {
        this.storage = storage;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public SequenceBuilder<NucleotideSequence> ensureCapacity(int capacity) {
        if (size == -1)
            throw new IllegalStateException("Destroyed.");
        if (capacity > 0) {
            if (storage == null)
                storage = new Bit2Array(capacity);
            else if (capacity > storage.size())
                storage = storage.extend(capacity);
        }
        return this;
    }

    private void ensureInternalCapacity(int newSize) {
        if (size == -1)
            throw new IllegalStateException("Destroyed.");
        if (storage == null && newSize != 0)
            storage = new Bit2Array(Math.max(newSize, 10));
        if (storage.size() < newSize)
            storage = storage.extend(Math.max(newSize, 3 * storage.size() / 2 + 1));
    }

    @Override
    public NucleotideSequence createAndDestroy() {
        if (size == 0)
            return NucleotideSequence.EMPTY;
        NucleotideSequence seq;
        if (storage.size() == size)
            seq = new NucleotideSequence(storage, true);
        else
            seq = new NucleotideSequence(storage.getRange(0, size), true);
        storage = null;
        size = -1;
        return seq;
    }

    @Override
    public SequenceBuilder<NucleotideSequence> append(byte[] letters) {
        ensureInternalCapacity(size + letters.length);
        int size = this.size;
        for (byte letter : letters)
            storage.set(size++, letter);
        this.size = size;
        return this;
    }

    @Override
    public NucleotideSequenceBuilder set(int position, byte letter) {
        if (position < 0 || position >= size)
            throw new IndexOutOfBoundsException();
        storage.set(position, letter);
        return this;
    }

    @Override
    public NucleotideSequenceBuilder append(byte letter) {
        ensureInternalCapacity(size + 1);
        storage.set(size++, letter);
        return this;
    }

    @Override
    public NucleotideSequenceBuilder append(NucleotideSequence seq) {
        ensureInternalCapacity(size + seq.size());
        storage.copyFrom(seq.data, 0, size, seq.size());
        size += seq.size();
        return this;
    }

    @Override
    public NucleotideSequenceBuilder clone() {
        return new NucleotideSequenceBuilder(storage == null ? null : storage.clone(), size);
    }
}

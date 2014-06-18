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
package com.milaboratory.core.sequence.nucleotide;

import com.milaboratory.core.sequence.SequenceBuilder;
import com.milaboratory.util.Bit2Array;

/**
 * Creates {@link NucleotideSequence}.
 */
public final class NucleotideSequenceBuilder implements SequenceBuilder<NucleotideSequence> {
    private Bit2Array storage;
    private int size = 0;

    public NucleotideSequenceBuilder() {
        this.storage = null;
    }

    public NucleotideSequenceBuilder(int capacity) {
        ensureCapacity(capacity);
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
        if (storage == null && capacity > 0) {
            storage = new Bit2Array(capacity);
            return this;
        }
        if (capacity > storage.size())
            storage = storage.extend(capacity);
        return this;
    }

    private void ensureInternalCapacity(int newSize) {
        if (storage == null && newSize != 0)
            storage = new Bit2Array(Math.max(newSize, 10));
        if (storage.size() >= newSize)
            return;
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
        return seq;
    }

    @Override
    public SequenceBuilder<NucleotideSequence> append(byte letter) {
        ensureInternalCapacity(size + 1);
        storage.set(size++, letter);
        return this;
    }

    @Override
    public SequenceBuilder<NucleotideSequence> append(NucleotideSequence sequence) {
        ensureInternalCapacity(size + sequence.size());
        storage.copyFrom(sequence.data, 0, size, sequence.size());
        size += sequence.size();
        return this;
    }

    @Override
    public SequenceBuilder<NucleotideSequence> clone() {
        return new NucleotideSequenceBuilder(storage.clone(), size);
    }
}

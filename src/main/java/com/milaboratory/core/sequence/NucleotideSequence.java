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

import com.milaboratory.core.Range;
import com.milaboratory.util.Bit2Array;

import java.io.Serializable;

/**
 * Nucleotide sequence.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public final class NucleotideSequence extends Sequence<NucleotideSequence> implements Serializable {
    public static final NucleotideSequence EMPTY = new NucleotideSequence("");
    public static final NucleotideAlphabet ALPHABET = NucleotideAlphabet.INSTANCE;
    final Bit2Array data;
    private static final long serialVersionUID = 1L;

    public NucleotideSequence(String sequence) {
        data = new Bit2Array(sequence.length());
        for (int i = 0; i < sequence.length(); ++i)
            data.set(i, ALPHABET.codeFromSymbol(sequence.charAt(i)));
    }

    public NucleotideSequence(char[] sequence) {
        data = new Bit2Array(sequence.length);
        for (int i = 0; i < sequence.length; ++i)
            data.set(i, ALPHABET.codeFromSymbol(sequence[i]));
    }

    public NucleotideSequence(Bit2Array data) {
        this.data = data.clone();
    }

    NucleotideSequence(Bit2Array data, boolean unsafe) {
        assert unsafe;
        this.data = data;
    }

    @Override
    public byte codeAt(int position) {
        return (byte) data.get(position);
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public NucleotideSequence getSubSequence(int from, int to) {
        return getSubSequence(new Range(from, to));
    }

    @Override
    public NucleotideSequence getSubSequence(Range range) {
        if (range.getLower() < 0 || range.getUpper() < 0
                || range.getLower() > size() || range.getUpper() > size())
            throw new IndexOutOfBoundsException();

        if (range.length() == 0)
            return EMPTY;

        if (range.isReverse())
            return new NucleotideSequence(
                    transformToRC(data, range.getLower(), range.getUpper()), true);
        else
            return new NucleotideSequence(
                    data.getRange(range.getLower(), range.getUpper()), true);
    }

    /**
     * Returns reverse complement of this sequence.
     *
     * @return reverse complement sequence
     */
    public NucleotideSequence getReverseComplement() {
        return new NucleotideSequence(transformToRC(data, 0, data.size()), true);
    }

    public Bit2Array getInnerData() {
        return data.clone();
    }

    @Override
    public NucleotideAlphabet getAlphabet() {
        return ALPHABET;
    }

    @Override
    public int hashCode() {
        int result = ALPHABET.hashCode();
        result = 31 * result + data.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return data.equals(((NucleotideSequence) o).data);
    }

    public static NucleotideSequence fromSequence(byte[] sequence, int offset, int length) {
        Bit2Array storage = new Bit2Array(length);
        for (int i = 0; i < length; ++i)
            storage.set(i, ALPHABET.codeFromSymbol((char) sequence[offset + i]));
        return new NucleotideSequence(storage, true);
    }

    private static Bit2Array transformToRC(Bit2Array data, int from, int to) {
        Bit2Array newData = new Bit2Array(to - from);
        int reverseCord;
        for (int cord = 0, s = to - from; cord < s; ++cord) {
            reverseCord = to - 1 - cord;
            newData.set(cord, (~data.get(reverseCord)) & 0x3);
        }
        return newData;
    }
}

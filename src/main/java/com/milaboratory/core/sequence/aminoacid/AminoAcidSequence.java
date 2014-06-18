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
package com.milaboratory.core.sequence.aminoacid;

import com.milaboratory.core.sequence.Alphabet;
import com.milaboratory.core.sequence.Sequence;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;

import java.util.Arrays;

/**
 * Main implementation of amino acid sequence.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public final class AminoAcidSequence extends Sequence<AminoAcidSequence> {
    final byte[] data;
    public static final AminoAcidSequence EMPTY = new AminoAcidSequence(new byte[0], true);

    public AminoAcidSequence(byte[] data) {
        this(data.clone(), true);
    }

    public AminoAcidSequence(String sequence) {
        this(dataFromChars(sequence.toCharArray()), true);
    }

    AminoAcidSequence(byte[] data, boolean unsafe) {
        assert unsafe;
        this.data = data;
    }

    @Override
    public Alphabet getAlphabet() {
        return AminoAcidAlphabet.INSTANCE;
    }

    @Override
    public byte codeAt(int position) {
        return data[position];
    }

    @Override
    public int size() {
        return data.length;
    }

    @Override
    public AminoAcidSequence getRange(int from, int to) {
        return new AminoAcidSequence(Arrays.copyOfRange(data, from, to), true);
    }

    @Override
    public byte[] asArray() {
        return data.clone();
    }

    public boolean containStops() {
        for (byte b : data)
            if (b == AminoAcidAlphabet.Stop)
                return true;
        return false;
    }

    public int numberOfStops() {
        int count = 0;
        for (byte b : data)
            if (b == AminoAcidAlphabet.Stop)
                ++count;
        return count;
    }

    private static byte[] dataFromChars(char[] chars) {
        byte[] data = new byte[chars.length];
        for (int i = 0; i < chars.length; ++i)
            data[i] = AminoAcidAlphabet.INSTANCE.codeFromSymbol(chars[i]);
        return data;
    }

    public static int getTriplet(NucleotideSequence nSequence, int tripletStart) {
        int triplet = (nSequence.codeAt(tripletStart) << 4) | (nSequence.codeAt(tripletStart + 1) << 2) | nSequence.codeAt(tripletStart + 2);
        return triplet;
    }

    public static byte getAminoAcid(NucleotideSequence nSequence, int tripletStart) {
        return Translator.getAminoAcid(getTriplet(nSequence, tripletStart));
    }

    public static AminoAcidSequence translate(NucleotideSequence sequence) {
        if (sequence.size() % 3 != 0)
            throw new IllegalArgumentException("Only nucleotide sequences with size multiple " +
                    "of three are supported (in-frame).");
        byte[] aaData = new byte[sequence.size() / 3];
        Translator.translate(aaData, 0, sequence, 0, sequence.size());
        return new AminoAcidSequence(aaData, true);
    }

    public static AminoAcidSequence translate(boolean fromLeft, boolean includeIncomplete, NucleotideSequence ns) {
        byte[] data;
        data = new byte[(ns.size() + (includeIncomplete ? 2 : 0)) / 3];
        if (fromLeft) {
            Translator.translate(data, 0, ns, 0, ns.size() / 3 * 3);
            if (includeIncomplete && ns.size() % 3 != 0)
                data[data.length - 1] = AminoAcidAlphabet.IncompleteCodon;
        } else {
            if (includeIncomplete && ns.size() % 3 != 0)
                data[0] = AminoAcidAlphabet.IncompleteCodon;
            Translator.translate(data,
                    (includeIncomplete && ns.size() % 3 != 0) ? 1 : 0, ns, ns.size() % 3, ns.size() / 3 * 3);
        }
        return new AminoAcidSequence(data, true);
    }

    public static AminoAcidSequence translateFromRight(NucleotideSequence ns) {
        return translate(false, true, ns);
    }

    public static AminoAcidSequence translateFromLeft(NucleotideSequence ns) {
        return translate(true, true, ns);
    }
}

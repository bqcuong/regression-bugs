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

import com.milaboratory.core.sequence.Alphabet;
import com.milaboratory.core.sequence.SequenceBuilder;

/**
 * Nucleotide alphabet.
 * <p/>
 * <table> <tr><td>0</td><td>A</td></tr> <tr><td>1</td><td>G</td></tr> <tr><td>2</td><td>C</td></tr>
 * <tr><td>3</td><td>T</td></tr> </table>
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public final class NucleotideAlphabet extends Alphabet<NucleotideSequence> {
    public static final byte A = 0x00;
    public static final byte G = 0x01;
    public static final byte C = 0x02;
    public static final byte T = 0x03;
    public final static NucleotideAlphabet INSTANCE = new NucleotideAlphabet();
    private static char[] chars = {'A', 'G', 'C', 'T'};

    private NucleotideAlphabet() {
        super("nucleotide");
    }

    public static byte getComplement(byte code) {
        return (code ^= 3);
    }

    public byte codeFromSymbol(char symbol) {
        switch (symbol) {
            case 'a':
            case 'A':
                return (byte) 0x00;
            case 't':
            case 'T':
                return (byte) 0x03;
            case 'g':
            case 'G':
                return (byte) 0x01;
            case 'c':
            case 'C':
                return (byte) 0x02;
        }
        return -1;
    }

    public char symbolFromCode(byte code) {
        if (code < 0 || code >= chars.length)
            throw new RuntimeException("Wrong code.");
        return chars[code];
    }

    @Override
    public byte size() {
        return (byte) 4;
    }

    @Override
    public NucleotideSequenceBuilder getBuilder() {
        return new NucleotideSequenceBuilder();
    }
}

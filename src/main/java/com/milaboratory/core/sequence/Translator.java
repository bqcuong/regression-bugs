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

final class Translator {
    private static byte[] code = null;

    static {
        char[] Base1 = "ttttttttttttttttccccccccccccccccaaaaaaaaaaaaaaaagggggggggggggggg".toCharArray();
        char[] Base2 = "ttttccccaaaaggggttttccccaaaaggggttttccccaaaaggggttttccccaaaagggg".toCharArray();
        char[] Base3 = "tcagtcagtcagtcagtcagtcagtcagtcagtcagtcagtcagtcagtcagtcagtcagtcag".toCharArray();
        char[] Amino = "FFLLSSSSYY**CC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG".toCharArray();
        code = new byte[Base1.length];
        int triplet;
        byte b0, b1, b2;
        for (int i = 0; i < Base1.length; ++i) {
            b0 = NucleotideAlphabet.INSTANCE.codeFromSymbol(Base1[i]);
            b1 = NucleotideAlphabet.INSTANCE.codeFromSymbol(Base2[i]);
            b2 = NucleotideAlphabet.INSTANCE.codeFromSymbol(Base3[i]);
            triplet = (b0 << 4) | (b1 << 2) | b2;
            code[triplet] = AminoAcidAlphabet.INSTANCE.codeFromSymbol(Amino[i]);
        }
    }

    static byte getAminoAcid(int triplet) {
        return code[triplet];
    }

    static void translate(byte[] dest, int offsetInDest, NucleotideSequence sequence, int offsetInSeq, int seqLength) {
        if (seqLength % 3 != 0)
            throw new IllegalArgumentException("Only nucleotide sequences with size multiple " +
                    "of three are supported (in-frame).");

        int size = sequence.size() / 3;
        int triplet;
        for (int i = 0; i < size; i++) {
            triplet = (sequence.codeAt(offsetInSeq + i * 3) << 4) |
                    (sequence.codeAt(offsetInSeq + i * 3 + 1) << 2) |
                    sequence.codeAt(offsetInSeq + i * 3 + 2);
            dest[i + offsetInDest] = code[triplet];
        }
    }
}

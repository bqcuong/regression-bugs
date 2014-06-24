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

/**
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public final class SequencesUtils {
//    public static int mismatchCount(Sequence seq0, Sequence seq1) {
//        if (seq0.getAlphabet() != seq1.getAlphabet())
//            throw new IllegalArgumentException("Different sequene alphabets");
//        if (seq0.size() != seq1.size())
//            return -1;
//        int mm = 0;
//        for (int i = 0; i < seq0.size(); ++i)
//            if (seq0.codeAt(i) != seq1.codeAt(i))
//                ++mm;
//        return mm;
//    }
//
//    public static int mismatchCount(Sequence seq0, int seq0Offset, Sequence seq1, int seq1Offset, int length) {
//        if (seq0.getAlphabet() != seq1.getAlphabet())
//            throw new IllegalArgumentException("Different sequene alphabets");
//
//        if (seq0.size() < seq0Offset + length || seq1.size() < seq1Offset + length)
//            throw new IllegalArgumentException();
//
//        int mm = 0;
//        for (int i = 0; i < length; ++i)
//            if (seq0.codeAt(i + seq0Offset) != seq1.codeAt(i + seq1Offset))
//                ++mm;
//        return mm;
//    }
//
//
//    public static int mismatchCount(Sequence seq0, Sequence seq1, int maxMismatches) {
//        if (seq0.getAlphabet() != seq1.getAlphabet())
//            throw new IllegalArgumentException("Different sequene alphabets");
//        if (seq0.size() != seq1.size())
//            return -1;
//        int mm = 0;
//        for (int i = 0; i < seq0.size(); ++i)
//            if (seq0.codeAt(i) != seq1.codeAt(i))
//                if (++mm > maxMismatches)
//                    return -1;
//        return mm;
//    }
//
//    public static String highlightedMismatches(Sequence sequence, Sequence standard) {
//        final Alphabet alphabet = standard.getAlphabet();
//        if (alphabet != sequence.getAlphabet())
//            throw new IllegalArgumentException();
//        if (standard.size() != sequence.size())
//            throw new IllegalArgumentException();
//        char[] chars = new char[sequence.size()];
//        byte code;
//        for (int i = 0; i < standard.size(); ++i)
//            if (standard.codeAt(i) != (code = sequence.codeAt(i)))
//                chars[i] = Character.toUpperCase(alphabet.symbolFromCode(code));
//            else
//                chars[i] = Character.toLowerCase(alphabet.symbolFromCode(code));
//        return new String(chars);
//    }

    public static <S extends Sequence<S>> S catN(S... sequences) {
        for (S seq : sequences)
            if (seq == null)
                return null;

        return cat(sequences);
    }

    public static <S extends Sequence<S>> S cat(S... sequences) {
        if (sequences.length == 0)
            throw new IllegalArgumentException("Zero arguments");

        if (sequences.length == 1)
            return sequences[0];

        int size = 0;
        for (S s : sequences)
            size += s.size();

        SequenceBuilder<S> builder = sequences[0].getAlphabet().getBuilder().ensureCapacity(size);

        for (S s : sequences)
            builder.append(s);

        return builder.createAndDestroy();
    }
}

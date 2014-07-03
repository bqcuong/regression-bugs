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

/**
 * Parent class for all types of sequences. Each element of sequence (e.g. nucleotide, or amino acid)
 * encoded in byte, so {@code Sequence} is a simple container of ordered bytes; the correspondence between byte codes
 * and particular elements is defined in {@link com.milaboratory.core.sequence.Alphabet} that corresponds to this
 * type of sequence (via {@link #getAlphabet()}).
 *
 * @param <S> type of sequence
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 * @see com.milaboratory.core.sequence.Alphabet
 * @see com.milaboratory.core.sequence.SequenceBuilder
 * @see com.milaboratory.core.sequence.NucleotideSequence
 * @see com.milaboratory.core.sequence.AminoAcidSequence
 */
public abstract class Sequence<S extends Sequence<S>> implements Comparable<S> {
    /**
     * Returns element at specified position.
     *
     * @param position position in sequence
     * @return element at specified position
     */
    public abstract byte codeAt(int position);

    /**
     * Returns size of this sequence
     *
     * @return size of this sequence
     */
    public abstract int size();

    /**
     * Returns the alphabet corresponding to this type of sequence.
     *
     * @return alphabet corresponding to this type of sequence
     */
    public abstract Alphabet<S> getAlphabet();

    /**
     * Returns a subsequence of this starting at {@code from} (inclusive) and ending at {@code to} (exclusive).
     *
     * @param from starting point of subsequence (inclusive)
     * @param to   ending point of subsequence (exclusive)
     * @return subsequence of this starting at {@code from} (inclusive) and ending at {@code to} (exclusive)
     * @throws java.lang.IndexOutOfBoundsException if {@code from} or {@code to} is out of this sequence range
     * @throws java.lang.IllegalArgumentException  if {@code from >= to}
     */
    public abstract S getSubSequence(int from, int to);

    /**
     * Returns a subsequence of this bounded by specified {@code range}.
     *
     * @param range a range that defines starting (inclusive) and ending (exclusive) points of subsequence
     * @return subsequence of this bounded by specified {@code range}.
     * @throws java.lang.IndexOutOfBoundsException if {@code from} orj {@code to} is out of this sequence range
     */
    public S getSubSequence(Range range) {
        if (range.isReverse())
            throw new IllegalArgumentException("Reverse range not supported.");
        return getSubSequence(range.getFrom(), range.getTo());
    }

    /**
     * Returns an array of bytes that encodes this sequence.
     *
     * @return array of bytes that encodes this sequence
     */
    public byte[] asArray() {
        byte[] bytes = new byte[size()];
        for (int i = size() - 1; i >= 0; --i)
            bytes[i] = codeAt(i);
        return bytes;
    }

    /**
     * Returns a character representation of element at specified position.
     *
     * @param position position in this sequence
     * @return character representation of element at specified position
     */
    public char charFromCodeAt(int position) {
        return getAlphabet().symbolFromCode(codeAt(position));
    }

    /**
     * Returns a concatenation of this and {@code other} sequences (so this will be followed by {@code other}
     * in the result).
     *
     * @param other other sequence
     * @return concatenation of this and {@code other} sequences
     */
    public S concatenate(S other) {
        assert other.getAlphabet() == this.getAlphabet();
        return getAlphabet().getBuilder().
                ensureCapacity(other.size() + size())
                .append((S) this).append(other).createAndDestroy();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof Sequence))
            return false;
        final Sequence other = (Sequence) obj;
        if (other.getAlphabet() != getAlphabet())
            return false;
        if (other.size() != this.size())
            return false;
        for (int i = size() - 1; i >= 0; --i)
            if (other.codeAt(i) != codeAt(i))
                return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash += 31 * getAlphabet().hashCode();
        for (int i = size() - 1; i >= 0; --i)
            hash = hash * 7 + codeAt(i);
        return hash;
    }

    @Override
    public String toString() {
        char[] chars = new char[size()];
        for (int i = 0; i < size(); i++)
            chars[i] = getAlphabet().symbolFromCode(codeAt(i));
        return new String(chars);
    }

    @Override
    public int compareTo(S o) {
        if (this.getAlphabet() != o.getAlphabet())
            throw new RuntimeException();
        if (this.size() != o.size())
            if (this.size() < o.size())
                return -1;
            else
                return 1;
        byte b0, b1;
        for (int i = 0; i < size(); i++) {
            b0 = this.codeAt(i);
            b1 = o.codeAt(i);
            if (b0 != b1)
                if (b0 < b1)
                    return -1;
                else
                    return 1;
        }
        return 0;
    }
}

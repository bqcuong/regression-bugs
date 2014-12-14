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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.milaboratory.primitivio.annotations.Serializable;

/**
 * Interface for sequence letters alphabet (amino acid, nucleotide, etc.). {@code Alphabet} is responsible for
 * correspondence between char representation of elements (e.g. 'A', 'T', 'G', 'C' in case of
 * {@link com.milaboratory.core.sequence.NucleotideAlphabet}) and their internal byte representation.
 * <p>Implementation note: all alphabets should be singletons.</p>
 *
 * @param <S> type of sequences that correspond to this alphabet
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 * @see com.milaboratory.core.sequence.Sequence
 * @see com.milaboratory.core.sequence.SequenceBuilder
 * @see com.milaboratory.core.sequence.NucleotideAlphabet
 * @see com.milaboratory.core.sequence.NucleotideSequence
 */
@JsonSerialize(using = Alphabets.Serializer.class)
@JsonDeserialize(using = Alphabets.Deserializer.class)
@Serializable(by = IO.AlphabetSerializer.class)
public abstract class Alphabet<S extends Sequence<S>> {
    private final String alphabetName;
    private final byte id;

    protected Alphabet(String alphabetName, byte id) {
        this.alphabetName = alphabetName;
        this.id = id;
    }

    /**
     * Gets a char from binary code
     *
     * @param code binary code of segment
     * @return corresponding char
     */
    public abstract char symbolFromCode(byte code);

    /**
     * Gets the number of letters in this alphabet
     *
     * @return the number of letters in this alphabet
     */
    public abstract int size();

    /**
     * Gets the binary code corresponding to given symbol (case insensitive) or -1 if there
     * is no such symbol in this alphabet
     *
     * @param symbol symbol to convert
     * @return binary code of the symbol (case insensitive) or -1 if there is no such symbol in the alphabet
     */
    public abstract byte codeFromSymbol(char symbol);

    /**
     * Returns a sequence builder for corresponding sequence type.
     *
     * @return sequence builder for corresponding sequence type
     */
    public abstract SequenceBuilder<S> getBuilder();

    /**
     * Returns the human readable name of this alphabet.
     *
     * <p>This name can be then used to obtain the instance of this alphabet using {@link
     * com.milaboratory.core.sequence.Alphabets#getByName(String)} method if it is registered (see {@link
     * com.milaboratory.core.sequence.Alphabets#register(Alphabet)}).</p>
     */
    public final String getAlphabetName() {
        return alphabetName;
    }

    /**
     * Returns byte id of this alphabet
     *
     * <p>This name can be then used to obtain the instance of this alphabet using {@link
     * com.milaboratory.core.sequence.Alphabets#getById(byte)} method if it is registered (see {@link
     * com.milaboratory.core.sequence.Alphabets#register(Alphabet)}).</p>
     */
    public byte getId() {
        return id;
    }

    /**
     * Parses string representation of sequence.
     *
     * @param string string representation of sequence
     * @return sequence
     */
    public S parse(String string) {
        SequenceBuilder<S> builder = getBuilder().ensureCapacity(string.length());
        for (int i = 0; i < string.length(); ++i)
            builder.append(codeFromSymbol(string.charAt(i)));
        return builder.createAndDestroy();
    }

    /**
     * Convert alphabet to a readable string.
     *
     * @return alphabet as a readable string
     */
    @Override
    public String toString() {
        return "Alphabet{" + alphabetName + '}';
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return obj == this;
    }
}

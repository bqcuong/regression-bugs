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

/**
 * An interface for sequence letters alphabet. (Amino acid, nucleotide, etc...)
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
@JsonSerialize(using = Alphabets.Serializer.class)
@JsonDeserialize(using = Alphabets.Deserializer.class)
public abstract class Alphabet<S extends Sequence<S>> {
    private final String alphabetName;

    protected Alphabet(String alphabetName) {
        this.alphabetName = alphabetName;
    }

    /**
     * Gets a char from binary code
     *
     * @param code binary code of segment
     * @return corresponding char
     */
    public abstract char symbolFromCode(byte code);

    /**
     * Gets the number of letters in the alphabet
     *
     * @return the number of letters in the alphabet
     */
    public abstract int size();

    /**
     * Gets the code corresponding to given symbol or -1 if there is no such symbol in the alphabets
     *
     * @param symbol symbol to convert
     * @return binary code of the symbol or -1 if there is no such symbol in the alphabet
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
     * <p/>
     * <p>This name can be then used to obtain the instance of this alphabet using {@link
     * com.milaboratory.core.sequence.Alphabets#getByName(String)} method if it is registered (see {@link
     * com.milaboratory.core.sequence.Alphabets#register(Alphabet)}).</p>
     */
    public final String getAlphabetName() {
        return alphabetName;
    }

    public S build(String string) {
        SequenceBuilder<S> builder = getBuilder().ensureCapacity(string.length());
        for (int i = 0; i < string.length(); ++i)
            builder.append(codeFromSymbol(string.charAt(i)));
        return builder.createAndDestroy();
    }

    @Override
    public final int hashCode() { return super.hashCode(); }

    @Override
    public final boolean equals(Object obj) { return obj == this; }
}

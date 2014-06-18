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
 * Interface for classes that build sequences from different alphabets.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public interface SequenceBuilder<S extends Sequence> {
    /**
     * Size of sequence being created.
     *
     * @return size
     */
    int size();

    /**
     * Ensures capacity of this builder.
     *
     * @param capacity capacity
     * @return this
     */
    SequenceBuilder<S> ensureCapacity(int capacity);

    /**
     * Creates the sequence and destroys this builder.
     *
     * @return created sequence
     */
    S createAndDestroy();

    /**
     * Appends letter.
     *
     * @param letter letter
     * @return this
     */
    SequenceBuilder<S> append(byte letter);

    /**
     * Appends sequence.
     *
     * @param sequence sequence
     * @return this
     */
    SequenceBuilder<S> append(S sequence);

    /**
     * Returns a deep copy of this builder
     *
     * @return a deep copy of this builder
     */
    SequenceBuilder<S> clone();
}

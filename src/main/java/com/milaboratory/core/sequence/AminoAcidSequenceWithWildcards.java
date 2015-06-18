/*
 * Copyright 2015 MiLaboratory.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.milaboratory.core.sequence;

/**
 * Representation of amino acid sequences with unknown parts (e.g. "AXXXATC", where 'X' is any/unknown amino
 * acid).
 *
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 * @see com.milaboratory.core.sequence.Sequence
 */
public final class AminoAcidSequenceWithWildcards
        extends AbstractSequenceWithWildcards<AminoAcidSequenceWithWildcards, AminoAcidSequence> {
    /**
     * Alphabet for incomplete amino acid sequences
     */
    public static final AminoAcidAlphabetWithWildcards ALPHABET = AminoAcidAlphabetWithWildcards.INSTANCE;

    /**
     * Creates incomplete amino acid sequence from its string representation (case insensitive).
     *
     * @param sequence string representation (case insensitive) of incomplete amino acid sequence
     */
    public AminoAcidSequenceWithWildcards(String sequence) {
        super(sequence);
    }

    /**
     * Creates incomplete amino acid sequence from binary data.
     *
     * @param data binary data
     */
    public AminoAcidSequenceWithWildcards(byte[] data) {
        super(data.clone());
    }

    AminoAcidSequenceWithWildcards(byte[] data, boolean unsafe) {
        super(data);
        assert unsafe;
    }

    @Override
    public AminoAcidAlphabetWithWildcards getAlphabet() {
        return ALPHABET;
    }
}

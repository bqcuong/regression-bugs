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
 * Representation of nucleotide sequences with unknown parts (e.g. "ANNNSATC", where "N" represents any nucleotide, and
 * "S" represents G or C). See wildcards defined in {@link NucleotideAlphabet} for the full list.
 *
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 * @see com.milaboratory.core.sequence.Sequence
 */
public final class NucleotideSequenceWithWildcards
        extends AbstractSequenceWithWildcards<NucleotideSequenceWithWildcards, NucleotideSequence> {
    /**
     * Alphabet for incomplete nucleotide sequences
     */
    public static final NucleotideAlphabetWithWildcards ALPHABET
            = NucleotideAlphabetWithWildcards.INSTANCE;

    /**
     * Creates incomplete nucleotide sequence from its string representation (case insensitive).
     *
     * @param sequence string representation (case insensitive) of incomplete nucleotide sequence
     */
    public NucleotideSequenceWithWildcards(String sequence) {
        super(sequence);
    }

    /**
     * Creates incomplete nucleotide sequence from binary data.
     *
     * @param data binary data
     */
    public NucleotideSequenceWithWildcards(byte[] data) {
        super(data.clone());
    }

    NucleotideSequenceWithWildcards(byte[] data, boolean unsafe) {
        super(data);
        assert unsafe;
    }

    @Override
    public NucleotideAlphabetWithWildcards getAlphabet() {
        return ALPHABET;
    }
}

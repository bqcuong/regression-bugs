package com.milaboratory.core.sequence;

/**
 * Representation of nucleotide sequences with missed or unknown parts (e.g. "AxxxATC", where 'x' is whatever).
 *
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 * @see com.milaboratory.core.sequence.IncompleteAlphabet
 * @see com.milaboratory.core.sequence.Sequence
 * @see com.milaboratory.core.sequence.IncompleteAlphabet
 */
public final class IncompleteNucleotideSequence
        extends IncompleteSequence<IncompleteNucleotideSequence, NucleotideSequence> {
    /**
     * Alphabet for incomplete nucleotide sequences
     */
    public static final IncompleteAlphabet<IncompleteNucleotideSequence, NucleotideSequence> ALPHABET
            = IncompleteAlphabet.INCOMPLETE_NUCLEOTIDE_ALPHABET;
    /**
     * Binary code of unknown letter
     */
    public static final byte UNKNOWN_LETTER_CODE = ALPHABET.unknownLetterCode;

    /**
     * Creates incomplete nucleotide sequence from its string representation (case insensitive).
     *
     * @param sequence string representation (case insensitive) of incomplete nucleotide sequence
     */
    public IncompleteNucleotideSequence(String sequence) {
        super(sequence);
    }

    /**
     * Creates incomplete nucleotide sequence from binary data.
     *
     * @param data binary data
     */
    public IncompleteNucleotideSequence(byte[] data) {
        super(data.clone());
    }

    IncompleteNucleotideSequence(byte[] data, boolean unsafe) {
        super(data);
        assert unsafe;
    }

    @Override
    public IncompleteAlphabet<IncompleteNucleotideSequence, NucleotideSequence> getAlphabet() {
        return ALPHABET;
    }
}

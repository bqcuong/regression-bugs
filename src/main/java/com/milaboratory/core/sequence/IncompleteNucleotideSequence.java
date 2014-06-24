package com.milaboratory.core.sequence;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public final class IncompleteNucleotideSequence
        extends IncompleteSequence<IncompleteNucleotideSequence, NucleotideSequence> {
    public static final IncompleteAlphabet<IncompleteNucleotideSequence, NucleotideSequence> ALPHABET
            = IncompleteAlphabet.INCOMPLETE_NUCLEOTIDE_ALPHABET;
    public static final byte UNKNOWN_LETTER_CODE = ALPHABET.unknownLetterCode;

    public IncompleteNucleotideSequence(byte[] data) {
        super(data.clone());
    }

    public IncompleteNucleotideSequence(String sequence) {
        super(sequence);
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

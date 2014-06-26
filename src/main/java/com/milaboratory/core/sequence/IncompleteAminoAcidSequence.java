package com.milaboratory.core.sequence;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public final class IncompleteAminoAcidSequence
        extends IncompleteSequence<IncompleteAminoAcidSequence, AminoAcidSequence> {
    public static final IncompleteAlphabet<IncompleteAminoAcidSequence, AminoAcidSequence> ALPHABET
            = IncompleteAlphabet.INCOMPLETE_AMINO_ACID_ALPHABET;
    public static final byte UNKNOWN_LETTER_CODE = ALPHABET.unknownLetterCode;

    public IncompleteAminoAcidSequence(String sequence) {
        super(sequence);
    }

    public IncompleteAminoAcidSequence(byte[] data) {
        super(data.clone());
    }

    IncompleteAminoAcidSequence(byte[] data, boolean unsafe) {
        super(data);
        assert unsafe;
    }

    @Override
    public IncompleteAlphabet<IncompleteAminoAcidSequence, AminoAcidSequence> getAlphabet() {
        return ALPHABET;
    }
}

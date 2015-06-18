package com.milaboratory.core.sequence;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public final class AminoAcidAlphabetWithWildcards extends
        AlphabetWithWildcards<AminoAcidSequenceWithWildcards, AminoAcidSequence> {
    public static final AminoAcidAlphabetWithWildcards INSTANCE = new AminoAcidAlphabetWithWildcards();

    private AminoAcidAlphabetWithWildcards() {
        super(AminoAcidAlphabet.INSTANCE);
    }

    @Override
    AminoAcidSequenceWithWildcards createUnsafe(byte[] array) {
        return new AminoAcidSequenceWithWildcards(array, true);
    }
}
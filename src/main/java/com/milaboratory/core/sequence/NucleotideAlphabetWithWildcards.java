package com.milaboratory.core.sequence;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public final class NucleotideAlphabetWithWildcards extends
        AbstractAlphabetWithWildcards<NucleotideSequenceWithWildcards, NucleotideSequence> {
    public static final NucleotideAlphabetWithWildcards INSTANCE = new NucleotideAlphabetWithWildcards();

    private NucleotideAlphabetWithWildcards() {
        super(NucleotideAlphabet.INSTANCE);
    }

    @Override
    NucleotideSequenceWithWildcards createUnsafe(byte[] array) {
        return new NucleotideSequenceWithWildcards(array, true);
    }
}

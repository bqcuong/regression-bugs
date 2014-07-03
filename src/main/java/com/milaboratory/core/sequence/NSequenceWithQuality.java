package com.milaboratory.core.sequence;

/**
 * Container of nucleotide sequence with its quality.
 *
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 * @see com.milaboratory.core.sequence.SequenceWithQuality
 */
public final class NSequenceWithQuality extends SequenceWithQuality<NucleotideSequence> {
    /**
     * Creates nucleotide sequence with its quality
     *
     * @param sequence nucleotide sequence
     * @param quality  quality
     * @throws java.lang.IllegalArgumentException if {@code sequence.size() != quality.size()}
     */
    public NSequenceWithQuality(NucleotideSequence sequence, SequenceQuality quality) {
        super(sequence, quality);
    }

    /**
     * Returns reverse complement sequence with reversed quality.
     *
     * @return reverse complement sequence with reversed quality
     */
    public NSequenceWithQuality getReverseComplement() {
        return new NSequenceWithQuality(sequence.getReverseComplement(), quality.reverse());
    }
}

package com.milaboratory.core.sequence;

import com.milaboratory.core.Range;

/**
 * Container of nucleotide sequence with its quality.
 *
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 * @see com.milaboratory.core.sequence.SequenceWithQuality
 */
public final class NSequenceWithQuality extends SequenceWithQuality<NucleotideSequence>
        implements NSeq<NSequenceWithQuality> {
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
    @Override
    public NSequenceWithQuality getReverseComplement() {
        return new NSequenceWithQuality(sequence.getReverseComplement(), quality.reverse());
    }

    @Override
    public NSequenceWithQuality getSubSequence(int from, int to) {
        return new NSequenceWithQuality(sequence.getSubSequence(from, to), quality.getSubSequence(from, to));
    }

    @Override
    public NSequenceWithQuality getSubSequence(Range range) {
        return new NSequenceWithQuality(sequence.getSubSequence(range), quality.getSubSequence(range));
    }

    @Override
    public NSequenceWithQualityBuilder getSequenceBuilder() {
        return new NSequenceWithQualityBuilder();
    }
}

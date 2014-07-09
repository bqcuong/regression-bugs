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
    public NSequenceWithQuality getRange(int from, int to) {
        return new NSequenceWithQuality(sequence.getRange(from, to), quality.getRange(from, to));
    }

    @Override
    public NSequenceWithQuality getRange(Range range) {
        return new NSequenceWithQuality(sequence.getRange(range), quality.getRange(range));
    }

    @Override
    public NSequenceWithQualityBuilder getBuilder() {
        return new NSequenceWithQualityBuilder();
    }
}

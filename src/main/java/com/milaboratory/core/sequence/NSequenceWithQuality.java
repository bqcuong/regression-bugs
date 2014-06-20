package com.milaboratory.core.sequence;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */

public final class NSequenceWithQuality extends SequenceWithQuality<NucleotideSequence> {
    public NSequenceWithQuality(NucleotideSequence sequence, SequenceQuality quality) {
        super(sequence, quality);
    }

    public NSequenceWithQuality getReverseComplement() {
        return new NSequenceWithQuality(sequence.getReverseComplement(), quality.reverse());
    }
}

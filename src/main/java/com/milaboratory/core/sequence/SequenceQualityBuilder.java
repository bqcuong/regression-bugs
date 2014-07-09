package com.milaboratory.core.sequence;

/**
 * Created by poslavsky on 09/07/14.
 */
public final class SequenceQualityBuilder extends ArraySeqBuilder<SequenceQuality> {
    public SequenceQualityBuilder() {
    }

    public SequenceQualityBuilder(byte[] data, int size) {
        super(data, size);
    }

    @Override
    SequenceQuality createUnsafe(byte[] b) {
        return new SequenceQuality(b, true);
    }

    @Override
    byte[] getUnsafe(SequenceQuality sequenceQuality) {
        return sequenceQuality.data;
    }

    @Override
    public SequenceQualityBuilder clone() {
        return new SequenceQualityBuilder(data.clone(), size);
    }
}

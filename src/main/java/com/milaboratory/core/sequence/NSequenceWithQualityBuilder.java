package com.milaboratory.core.sequence;

/**
 * Created by poslavsky on 10/07/14.
 */
public class NSequenceWithQualityBuilder implements SequenceBuilder<NSequenceWithQuality> {
    final NucleotideSequenceBuilder sBuilder;
    final SequenceQualityBuilder qBuilder;

    public NSequenceWithQualityBuilder() {
        this(new NucleotideSequenceBuilder(), new SequenceQualityBuilder());
    }

    NSequenceWithQualityBuilder(NucleotideSequenceBuilder sBuilder, SequenceQualityBuilder qBuilder) {
        this.sBuilder = sBuilder;
        this.qBuilder = qBuilder;
    }

    @Override
    public int size() {
        return sBuilder.size();
    }

    @Override
    public SequenceBuilder<NSequenceWithQuality> ensureCapacity(int capacity) {
        sBuilder.ensureCapacity(capacity);
        qBuilder.ensureCapacity(capacity);
        return this;
    }

    @Override
    public NSequenceWithQuality createAndDestroy() {
        return new NSequenceWithQuality(sBuilder.createAndDestroy(), qBuilder.createAndDestroy());
    }

    @Override
    public SequenceBuilder<NSequenceWithQuality> set(int position, byte letter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SequenceBuilder<NSequenceWithQuality> append(byte letter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SequenceBuilder<NSequenceWithQuality> append(NSequenceWithQuality sequence) {
        sBuilder.append(sequence.sequence);
        qBuilder.append(sequence.quality);
        return this;
    }

    @Override
    public SequenceBuilder<NSequenceWithQuality> clone() {
        return new NSequenceWithQualityBuilder(sBuilder.clone(), qBuilder.clone());
    }
}

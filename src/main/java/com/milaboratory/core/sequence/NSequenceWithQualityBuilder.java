package com.milaboratory.core.sequence;

/**
 * Created by poslavsky on 10/07/14.
 */
public class NSequenceWithQualityBuilder implements SeqBuilder<NSequenceWithQuality> {
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
    public NSequenceWithQualityBuilder ensureCapacity(int capacity) {
        sBuilder.ensureCapacity(capacity);
        qBuilder.ensureCapacity(capacity);
        return this;
    }

    @Override
    public NSequenceWithQuality createAndDestroy() {
        return new NSequenceWithQuality(sBuilder.createAndDestroy(), qBuilder.createAndDestroy());
    }

    @Override
    public NSequenceWithQualityBuilder append(NSequenceWithQuality seq) {
        sBuilder.append(seq.sequence);
        qBuilder.append(seq.quality);
        return this;
    }

    @Override
    public NSequenceWithQualityBuilder clone() {
        return new NSequenceWithQualityBuilder(sBuilder.clone(), qBuilder.clone());
    }
}

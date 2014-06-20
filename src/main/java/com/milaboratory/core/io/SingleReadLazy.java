package com.milaboratory.core.io;

import com.milaboratory.core.sequence.NSequenceWithQuality;
import com.milaboratory.core.sequence.NucleotideSequence;
import com.milaboratory.core.sequence.SequenceQuality;
import com.milaboratory.util.SingleIterator;

import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */

public abstract class SingleReadLazy implements SingleRead {
    final long id;
    final byte[] buffer;
    final int descriptionFrom;
    final short dataOffset, qualityOffset, dataLength, descriptionLength;
    NSequenceWithQuality sequenceWithQuality;
    String description;


    public SingleReadLazy(long id,
                          byte[] buffer,
                          int descriptionFrom,
                          short dataOffset,
                          short qualityOffset,
                          short dataLength,
                          short descriptionLength) {
        this.id = id;
        this.buffer = buffer;
        this.descriptionFrom = descriptionFrom;
        this.dataOffset = dataOffset;
        this.qualityOffset = qualityOffset;
        this.dataLength = dataLength;
        this.descriptionLength = descriptionLength;
    }

    abstract byte getQualityOffset();

    @Override
    public String getDescription() {
        if (description == null)
            description = createDescription();
        return description;
    }

    @Override
    public NSequenceWithQuality getData() {
        if (sequenceWithQuality == null)
            sequenceWithQuality = createNSequenceWithQuality();
        return sequenceWithQuality;
    }

    private String createDescription() {
        return new String(buffer, descriptionFrom, descriptionLength, Charset.defaultCharset());
    }

    private NSequenceWithQuality createNSequenceWithQuality() {
        SequenceQuality quality = SequenceQuality.createUnchecked(getQualityOffset(),
                buffer, descriptionFrom + qualityOffset, dataLength);
        NucleotideSequence sequence = NucleotideSequence.parse(buffer, descriptionFrom + dataOffset, dataLength);
        return new NSequenceWithQuality(sequence, quality);
    }

    @Override
    public int numberOfReads() {
        return 1;
    }

    @Override
    public SingleRead getRead(int i) {
        if (i != 0)
            throw new IndexOutOfBoundsException();
        return this;
    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public Iterator<SingleRead> iterator() {
        return new SingleIterator<>((SingleRead) this);
    }
}

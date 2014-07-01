package com.milaboratory.core.io.sequence;

import com.milaboratory.core.sequence.NSequenceWithQuality;
import com.milaboratory.util.SingleIterator;

import java.util.Iterator;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public final class SingleReadImpl implements SingleRead {
    final long id;
    final NSequenceWithQuality sequenceWithQuality;
    final String description;

    public SingleReadImpl(long id, NSequenceWithQuality sequenceWithQuality, String description) {
        this.id = id;
        this.sequenceWithQuality = sequenceWithQuality;
        this.description = description;
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
        return id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public NSequenceWithQuality getData() {
        return sequenceWithQuality;
    }

    @Override
    public Iterator<SingleRead> iterator() {
        return new SingleIterator<>((SingleRead) this);
    }
}

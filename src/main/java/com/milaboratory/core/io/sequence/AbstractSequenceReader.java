package com.milaboratory.core.io.sequence;

import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractSequenceReader<R extends SequenceRead> implements SequenceReader<R> {
    protected final AtomicLong readsCounter = new AtomicLong();

    @Override
    public final long getNumberOfReads() {
        return readsCounter.get();
    }

    protected final void addOneRead() {
        readsCounter.incrementAndGet();
    }
}

package com.milaboratory.core.sequence;

import com.milaboratory.core.Range;

abstract class AbstractSeq<S extends AbstractSeq<S>> implements Seq<S> {
    @Override
    public S getRange(Range range) {
        if (range.isReverse())
            throw new IllegalArgumentException("Reverse range not supported.");
        return getRange(range.getFrom(), range.getTo());
    }

    @Override
    public S concatenate(S other) {
        return getBuilder()
                .ensureCapacity(other.size() + size())
                .append((S) this).append(other).createAndDestroy();
    }
}

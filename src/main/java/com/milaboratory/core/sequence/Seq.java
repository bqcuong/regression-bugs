package com.milaboratory.core.sequence;

import com.milaboratory.core.Range;

public interface Seq<T extends Seq<T>> {
    T getSubSequence(Range range);

    T getSubSequence(int from, int to);

    int size();

    SequenceBuilder<T> getSequenceBuilder();
}

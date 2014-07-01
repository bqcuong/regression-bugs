package com.milaboratory.core.clustering;

import com.milaboratory.core.sequence.Sequence;

public interface SequenceExtractor<T, S extends Sequence> {
    S getSequence(T object);
}

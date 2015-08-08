package com.milaboratory.core.alignment.batch;

import com.milaboratory.core.sequence.Sequence;

public interface HasSequence<S extends Sequence<S>> {
    S getSequence();
}

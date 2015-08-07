package com.milaboratory.core.alignment;

import com.milaboratory.core.sequence.Sequence;

public interface HasSequence<S extends Sequence<S>> {
    S getSequence();
}

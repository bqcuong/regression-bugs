package com.milaboratory.core.alignment.batch;

import com.milaboratory.core.sequence.Sequence;

public interface BatchAlignerWithBase<S extends Sequence<S>, P, H extends AlignmentHit<? extends S, ? extends P>>
        extends BatchAligner<S, H>, WithBase<S, P> {
}

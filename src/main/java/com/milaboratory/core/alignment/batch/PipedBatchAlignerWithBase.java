package com.milaboratory.core.alignment.batch;

import com.milaboratory.core.sequence.Sequence;

/**
 * Created by dbolotin on 09/08/15.
 */
public interface PipedBatchAlignerWithBase<S extends Sequence<S>, P, H extends AlignmentHit<? extends S, ? extends P>>
        extends PipedBatchAligner<S, H>, WithBase<S, P> {
}

package com.milaboratory.core.alignment;

import com.milaboratory.core.sequence.Sequence;

/**
 * Created by dbolotin on 07/08/15.
 */
public interface PipedAlignmentResult<S extends Sequence<S>, P, Q> extends AlignmentResult<S, P> {
    Q getQuery();
}

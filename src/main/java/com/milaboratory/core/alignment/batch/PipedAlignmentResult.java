package com.milaboratory.core.alignment.batch;

import com.milaboratory.core.sequence.Sequence;

public interface PipedAlignmentResult<H extends AlignmentHit<?, ?>, Q> extends AlignmentResult<H> {
    Q getQuery();
}

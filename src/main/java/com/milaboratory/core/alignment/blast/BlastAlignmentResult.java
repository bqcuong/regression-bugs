package com.milaboratory.core.alignment.blast;

import com.milaboratory.core.alignment.AlignmentResult;
import com.milaboratory.core.sequence.Sequence;

import java.util.Collections;
import java.util.List;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public final class BlastAlignmentResult<S extends Sequence<S>> implements AlignmentResult<S> {
    private final List<BlastAlignmentHit<S>> hits;

    public BlastAlignmentResult(List<BlastAlignmentHit<S>> hits) {
        this.hits = Collections.unmodifiableList(hits);
    }

    @Override
    public List<BlastAlignmentHit<S>> getHits() {
        return hits;
    }
}

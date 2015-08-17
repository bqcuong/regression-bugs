package com.milaboratory.core.alignment.batch;

import java.util.Collections;
import java.util.List;

/**
 * Defines a set of alignment hits.
 *
 * @param <H> type of hit
 */
public class AlignmentResult<H extends AlignmentHit<?, ?>> {
    final List<? extends H> hits;

    public AlignmentResult() {
        this.hits = Collections.emptyList();
    }

    public AlignmentResult(List<? extends H> hits) {
        this.hits = hits;
    }

    public List<? extends H> getHits() {
        return hits;
    }

    public boolean isEmpty() {
        return hits.isEmpty();
    }

    @Override
    public String toString() {
        return isEmpty() ? "Empty result." : (hits.size() + " hits.");
    }
}

package com.milaboratory.core.alignment.batch;

import java.util.List;

/**
 * {@link AlignmentResult} that also stores original query object. Used as an alignment result object in {@link
 * PipedBatchAligner}.
 *
 * @param <H> hit class
 * @param <Q> query class
 */
public class PipedAlignmentResult<H extends AlignmentHit<?, ?>, Q>
        extends AlignmentResult<H> {
    final Q query;

    public PipedAlignmentResult(List<? extends H> alignmentHits, Q query) {
        super(alignmentHits);
        this.query = query;
    }

    public Q getQuery() {
        return query;
    }

    @Override
    public String toString() {
        return query + " -> " + super.toString();
    }
}

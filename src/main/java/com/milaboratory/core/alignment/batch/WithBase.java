package com.milaboratory.core.alignment.batch;

import com.milaboratory.core.sequence.Sequence;

/**
 * Created by dbolotin on 07/08/15.
 */
public interface WithBase<S extends Sequence<S>, P> {
    /**
     * Adds record to the base of this aligner (a set of sequences that this instance aligns queries
     * with).
     *
     * @param sequence sequence
     * @param payload  payload to store additional information with this record (can be retrieved from resulting {@link
     *                 AlignmentHit})
     */
    void addReference(S sequence, P payload);
}

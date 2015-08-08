package com.milaboratory.core.alignment.blast;

import com.milaboratory.core.alignment.Alignment;
import com.milaboratory.core.alignment.batch.AlignmentHitImpl;
import com.milaboratory.core.sequence.Sequence;

/**
 * Created by dbolotin on 08/08/15.
 */
public class BlastHit<S extends Sequence<S>, P> extends AlignmentHitImpl<S, P> {
    final double score, bitScore, eValue;

    public BlastHit(Alignment<S> alignment, P recordPayload, double score, double bitScore, double eValue) {
        super(alignment, recordPayload);
        this.score = score;
        this.bitScore = bitScore;
        this.eValue = eValue;
    }

    public double getScore() {
        return score;
    }

    public double getBitScore() {
        return bitScore;
    }

    public double geteValue() {
        return eValue;
    }
}

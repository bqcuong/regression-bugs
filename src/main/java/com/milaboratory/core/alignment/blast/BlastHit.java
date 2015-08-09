package com.milaboratory.core.alignment.blast;

import com.milaboratory.core.Range;
import com.milaboratory.core.alignment.Alignment;
import com.milaboratory.core.alignment.batch.AlignmentHitImpl;
import com.milaboratory.core.sequence.Sequence;

/**
 * Created by dbolotin on 08/08/15.
 */
public class BlastHit<S extends Sequence<S>, P> extends AlignmentHitImpl<S, P> {
    private final double score, bitScore, eValue;
    private final Range subjectRange;

    public BlastHit(Alignment<S> alignment, P recordPayload, BlastHit<S, ?> hit) {
        super(alignment, recordPayload);
        this.score = hit.getScore();
        this.bitScore = hit.getBitScore();
        this.eValue = hit.getEValue();
        this.subjectRange = getSubjectRange();
    }

    public BlastHit(Alignment<S> alignment, P recordPayload, double score, double bitScore, double eValue, Range subjectRange) {
        super(alignment, recordPayload);
        this.score = score;
        this.bitScore = bitScore;
        this.eValue = eValue;
        this.subjectRange = subjectRange;
    }

    public Range getSubjectRange() {
        return subjectRange;
    }

    public double getScore() {
        return score;
    }

    public double getBitScore() {
        return bitScore;
    }

    public double getEValue() {
        return eValue;
    }

    @Override
    public String toString() {
        return "Record: " + getRecordPayload() + "\n" +
                "EValue = " + eValue + ";  Score = " + score + ";  BitScore = " + bitScore + " \n" +
                getAlignment().getAlignmentHelper().toString();
    }
}

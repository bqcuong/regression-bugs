package com.milaboratory.core.alignment.blast;

import com.milaboratory.core.Range;
import com.milaboratory.core.alignment.Alignment;
import com.milaboratory.core.sequence.Sequence;

/**
 * Created by dbolotin on 08/08/15.
 */
public class ExternalDBBlastHit<S extends Sequence<S>> extends BlastHit<S, String> {
    public ExternalDBBlastHit(Alignment<S> alignment, String recordPayload, double score, double bitScore,
                              double eValue, Range subjectRange) {
        super(alignment, recordPayload, score, bitScore, eValue, subjectRange);
    }

    public String getTitle() {
        return getRecordPayload();
    }
}

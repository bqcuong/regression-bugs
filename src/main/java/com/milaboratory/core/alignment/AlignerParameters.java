package com.milaboratory.core.alignment;

import com.milaboratory.core.alignment.batch.AlignmentHit;
import com.milaboratory.core.alignment.batch.BatchAlignerWithBase;
import com.milaboratory.core.sequence.NucleotideSequence;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public interface AlignerParameters {
    <P> BatchAlignerWithBase<NucleotideSequence, P, ? extends AlignmentHit> createAligner();
}

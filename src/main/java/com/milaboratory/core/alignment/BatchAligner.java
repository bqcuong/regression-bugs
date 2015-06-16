package com.milaboratory.core.alignment;

import com.milaboratory.core.sequence.Sequence;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public interface BatchAligner<S extends Sequence<S>> {
    AlignmentResult<S> align(S sequence);
}

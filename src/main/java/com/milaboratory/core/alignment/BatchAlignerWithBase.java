package com.milaboratory.core.alignment;

import com.milaboratory.core.sequence.Sequence;

public interface BatchAlignerWithBase<S extends Sequence<S>, P> extends BatchAligner<S, P>, WithBase<S, P> {
}

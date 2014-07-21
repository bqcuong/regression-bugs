package com.milaboratory.core.io.sequence;

import cc.redberry.pipe.OutputPortCloseable;

/**
 * Created by dbolotin on 18/07/14.
 */
public interface SequenceReaderCloseable<S extends SequenceRead> extends SequenceReader<S>, OutputPortCloseable<S> {
}

package com.milaboratory.core.io.sequence;

import cc.redberry.pipe.OutputPort;

/**
 * If this reader has some system resources associated with it (like opened file or network streams) implement
 * {@link cc.redberry.pipe.OutputPortCloseable} interface along with this interface.
 *
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public interface SequenceReader<S extends SequenceRead> extends OutputPort<S> {
}

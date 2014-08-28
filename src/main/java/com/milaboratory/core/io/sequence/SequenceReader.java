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
    /**
     * For sequential readers returns the number of reads read till this moment, after reader is exhausted returns total
     * number of reads. For random access readers returns total number of reads.
     *
     * <p>This method is thread-safe.</p>
     *
     * @return number of reads read till this moment for sequential readers and total number of reads for random access
     * readers
     */
    long getNumberOfReads();
}

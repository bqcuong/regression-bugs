package com.milaboratory.core.io.sequence;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */

public interface SingleWriter {
    /**
     * Writes {@link com.milaboratory.core.io.sequence.SingleRead}
     *
     * @param read read
     */
    void write(SingleRead read);
}

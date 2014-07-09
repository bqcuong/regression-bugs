package com.milaboratory.core.io.sequence;

/**
 * Common class for sequence writers.
 *
 * @param <R> type of reads
 */
public interface SequenceWriter<R extends SequenceRead> extends AutoCloseable {
    /**
     * Writes corresponding subtype of {@link com.milaboratory.core.io.sequence.SequenceRead}
     *
     * @param read read
     */
    void write(R read);

    /**
     * Writes all buffers to the underlying stream
     */
    void flush();

    /**
     * Closes this writer
     */
    @Override
    void close();
}

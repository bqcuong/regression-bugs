package com.milaboratory.core.io.sequence.fastq;

import com.milaboratory.core.io.sequence.SingleRead;
import com.milaboratory.core.io.sequence.SingleReader;
import com.milaboratory.core.io.util.AbstractRandomAccessReader;
import com.milaboratory.core.io.util.FileIndex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * FASTQ file reader with random access.
 */
public final class RandomAccessFastqReader
        extends AbstractRandomAccessReader<SingleRead>
        implements SingleReader, AutoCloseable {
    private final QualityFormat qualityFormat;
    private final FastqRecordsReader recordsReader;

    /**
     * Creates reader of specified FASTQ file with specified index.
     *
     * @param fileIndex file index
     * @param file      FASTQ file
     */
    public RandomAccessFastqReader(String fileIndex, String file)
            throws IOException {
        this(FileIndex.read(fileIndex), new RandomAccessFile(file, "r"), true);
    }

    /**
     * Creates reader of specified FASTQ file with specified index.
     *
     * @param fileIndex file index
     * @param file      FASTQ file
     * @param lazyReads create reads data on demand
     */
    public RandomAccessFastqReader(String fileIndex, String file, boolean lazyReads)
            throws IOException {
        this(FileIndex.read(fileIndex), new RandomAccessFile(file, "r"), lazyReads);
    }

    /**
     * Creates reader of specified FASTQ file with specified index.
     *
     * @param fileIndex file index
     * @param file      FASTQ file
     * @param lazyReads create reads data on demand
     */
    public RandomAccessFastqReader(FileIndex fileIndex, File file, boolean lazyReads)
            throws FileNotFoundException {
        this(fileIndex, new RandomAccessFile(file, "r"), lazyReads);
    }

    /**
     * Creates reader of specified FASTQ file with specified index.
     *
     * @param fileIndex file index
     * @param file      FASTQ file
     * @param lazyReads create reads data on demand
     */
    public RandomAccessFastqReader(FileIndex fileIndex, RandomAccessFile file, boolean lazyReads) {
        super(fileIndex, file);
        this.qualityFormat = QualityFormat.fromName(fileIndex.getMetadata("format"));
        this.recordsReader = new FastqRecordsReader(lazyReads, file, SingleFastqReader.DEFAULT_BUFFER_SIZE, false);
    }

    @Override
    public synchronized SingleRead take() {
        if (recordsReader.closed.get())
            return null;
        return super.take();
    }

    @Override
    public synchronized SingleRead take(long recordNumber) {
        if (recordsReader.closed.get())
            return null;
        try {
            return super.take(recordNumber);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected SingleRead take0() {
        try {
            if (!recordsReader.nextRecord(true))
                return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return recordsReader.createRead(currentRecordNumber, qualityFormat);
    }

    @Override
    protected void skip() {
        try {
            recordsReader.nextRecord(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void resetBufferOnSeek() {
        try {
            recordsReader.resetBuffer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        recordsReader.close();
    }
}

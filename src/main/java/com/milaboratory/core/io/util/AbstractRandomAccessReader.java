package com.milaboratory.core.io.util;

import cc.redberry.pipe.OutputPort;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Abstract class that allows random access to some records written in file.
 *
 * @param <T> type of records
 */
public abstract class AbstractRandomAccessReader<T> implements OutputPort<T> {
    /**
     * Index of file
     */
    protected final FileIndex fileIndex;
    /**
     * Random access file
     */
    protected final RandomAccessFile file;
    /**
     * Current record number, i.e. number of record that will be returned by invocation of take()
     */
    protected long currentRecordNumber;

    protected AbstractRandomAccessReader(FileIndex fileIndex, RandomAccessFile file) {
        this.fileIndex = fileIndex;
        this.file = file;
    }

    /**
     * Sets the file-pointer offset, measured from the beginning of this
     * file, at which the next record occurs.
     *
     * @param recordNumber number of record, at which to set the file
     *                     pointer.
     * @throws IOException if {@code pos} is less than
     *                     {@code 0} or if an I/O error occurs.
     */
    public void seekToRecord(long recordNumber) throws IOException {
        if (currentRecordNumber == recordNumber)
            return;
        long skip;
        if (recordNumber < fileIndex.getStartingRecordNumber()) {
            if (currentRecordNumber < recordNumber)
                skip = recordNumber - currentRecordNumber;
            else {
                skip = recordNumber;
                seek0(0);
            }
        } else if (recordNumber > fileIndex.getLastRecordNumber()) {
            if (currentRecordNumber < recordNumber)
                skip = recordNumber - currentRecordNumber;
            else {
                long record = fileIndex.getLastRecordNumber();
                skip = recordNumber - record;
                seek0(fileIndex.getNearestPosition(record));
            }
        } else if (recordNumber > currentRecordNumber && recordNumber - currentRecordNumber < fileIndex.getStep()) {
            skip = recordNumber - currentRecordNumber;
        } else {
            seek0(fileIndex.getNearestPosition(recordNumber));
            skip = recordNumber - fileIndex.getNearestRecordNumber(recordNumber);
        }

        for (; skip > 0; --skip)
            skip();

        currentRecordNumber = recordNumber;
    }

    /**
     * Returns the specified record or null if {@code recordNumber} is greater than actual
     * number of records in file.
     *
     * @param recordNumber record number
     * @return record with specified number or null if {@code recordNumber} is greater than actual
     * number of records in file
     * @throws IOException if I/O occurs
     */
    public T take(long recordNumber) throws IOException {
        seekToRecord(recordNumber);
        T t = take0();
        if (t != null)
            ++currentRecordNumber;
        return t;
    }

    /**
     * Returns the next record or null if no more records exist.
     *
     * @return next record or null if no more records exist
     */
    @Override
    public T take() {
        T t = take0();
        if (t != null)
            ++currentRecordNumber;
        return t;
    }

    private void seek0(long position) throws IOException {
        file.seek(position);
        resetBufferOnSeek();
    }

    protected void resetBufferOnSeek() {
    }

    protected void skip() {
        take0();
    }

    protected abstract T take0();
}

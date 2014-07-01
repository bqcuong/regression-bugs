package com.milaboratory.core.io.util;

import cc.redberry.pipe.OutputPort;

import java.io.IOException;
import java.io.RandomAccessFile;

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

    public void seekToRecord(long recordNumber) throws IOException {
        long skip;
        if (recordNumber < fileIndex.getStartingRecordNumber()) {
            if (currentRecordNumber < recordNumber)
                skip = recordNumber - currentRecordNumber;
            else {
                skip = recordNumber;
                file.seek(0);
            }
        } else if (recordNumber > fileIndex.getLastRecordNumber()) {
            if (currentRecordNumber < recordNumber)
                skip = recordNumber - currentRecordNumber;
            else {
                long record = fileIndex.getLastRecordNumber();
                skip = recordNumber - record;
                file.seek(fileIndex.getNearestPosition(record));
            }
        } else if (recordNumber > currentRecordNumber && recordNumber - currentRecordNumber < fileIndex.getStep()) {
            skip = recordNumber - currentRecordNumber;
        } else {
            long position = fileIndex.getNearestPosition(recordNumber);
            file.seek(position);
            skip = recordNumber - fileIndex.getNearestRecordNumber(recordNumber);
        }

        for (; skip > 0; --skip)
            take0();

        currentRecordNumber = recordNumber;
    }

    public T take(long recordNumber) throws IOException {
        seekToRecord(recordNumber);
        T t = take0();
        if (t != null)
            ++currentRecordNumber;
        return t;
    }

    @Override
    public T take() {
        T t = take0();
        if (t != null)
            ++currentRecordNumber;
        return t;
    }

    protected abstract T take0();
}

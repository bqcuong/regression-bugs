package com.milaboratory.core.io.util;

import gnu.trove.list.array.TLongArrayList;

import java.util.Map;

public final class FileIndex {
    /**
     * Step between records in file.
     */
    final long step;
    /**
     * Metadata recorded in the index
     */
    final Map<String, String> metadata;
    /**
     * Stored positions in file (measured in bytes)
     */
    final TLongArrayList index;
    /**
     * Starting record number
     */
    final long startingRecordNumber;
    /**
     * Last record number
     */
    final long lastRecordNumber;

    FileIndex(long step, Map<String, String> metadata,
              TLongArrayList index, long startingRecordNumber, long lastRecordNumber) {
        this.step = step;
        this.metadata = metadata;
        this.index = index;
        this.startingRecordNumber = startingRecordNumber;
        this.lastRecordNumber = lastRecordNumber;
    }

    /**
     * Returns step of current index
     *
     * @return step of current index
     */
    public long getStep() {
        return step;
    }

    /**
     * Returns the first record number indexed by this index.
     *
     * @return the first record number indexed by this index
     */
    public long getStartingRecordNumber() {
        return startingRecordNumber;
    }

    /**
     * Returns the last record number indexed by this index.
     *
     * @return the last record number indexed by this index
     */
    public long getLastRecordNumber() {
        return lastRecordNumber;
    }

    /**
     * Returns a metadata record.
     *
     * @param key metadata key
     * @return metadata record
     */
    public String getMetadata(String key) {
        return metadata.get(key);
    }

    /**
     * Returns the nearest (from the left side, i.e. smaller) position (in bytes) to specified record number.
     *
     * @param recordNumber number of record
     * @return nearest (from the left side, i.e. smaller) position (in bytes) to specified record number
     */
    public long getNearestPosition(long recordNumber) {
        if (recordNumber < startingRecordNumber || recordNumber > lastRecordNumber)
            throw new IndexOutOfBoundsException();
        return index.get((int) ((recordNumber - startingRecordNumber) / step));
    }

    /**
     * Returns the record number, which is nearest (from the left side, i.e. smaller) to the specified record number.
     *
     * @param recordNumber number of record
     * @return record number, which is nearest (from the left side, i.e. smaller) to the specified record number
     */
    public long getNearestRecordNumber(long recordNumber) {
        if (recordNumber < startingRecordNumber || recordNumber > lastRecordNumber)
            throw new IndexOutOfBoundsException();
        return (startingRecordNumber + step * ((recordNumber - startingRecordNumber) / step));
    }
}

/*
 * Copyright 2016 MiLaboratory.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.milaboratory.core.io.sequence.fasta;

import com.milaboratory.primitivio.PrimitivI;
import com.milaboratory.primitivio.PrimitivO;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TLongArrayList;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class RandomAccessFastaIndex {
    public static int MAGIC = 0xDEC730C5;
    /**
     * Default index step = the most common HDD cluster size
     */
    public static final int DEFAULT_INDEX_STEP = 4096;
    public static final long SKIP_MASK = 0xFFFFFL;
    public static final int FILE_POSITION_OFFSET = 20;
    private final int indexStep;
    private final long[] indexArray;
    private final IndexRecord[] records;

    RandomAccessFastaIndex(InputStream is) {
        PrimitivI pi = new PrimitivI(is);
        int magic = pi.readInt();
        if (magic != MAGIC)
            throw new IllegalArgumentException("Wrong stream format.");
        this.indexStep = pi.readVarInt();
        this.indexArray = new long[pi.readVarInt()];

        if (indexArray.length == 0) {
            this.records = new IndexRecord[0];
            return;
        }

        this.indexArray[0] = pi.readVarLong();
        for (int i = 1; i < this.indexArray.length; i++)
            this.indexArray[i] = this.indexArray[i - 1] + pi.readVarLong();


        this.records = new IndexRecord[pi.readVarInt()];
        for (int i = 0; i < records.length; i++) {
            int indexStart = pi.readVarInt();
            long length = pi.readVarLong();
            String description = pi.readUTF();
            records[i] = new IndexRecord(description, length, indexStart);
        }
    }

    RandomAccessFastaIndex(IndexBuilder builder) {
        if (builder.lengths.size() != builder.descriptions.size())
            throw new IllegalStateException();

        this.indexStep = builder.indexStep;
        this.indexArray = builder.index.toArray();
        this.records = new IndexRecord[builder.indexIndex.size()];
        for (int i = 0; i < this.records.length; i++)
            records[i] = new IndexRecord(builder.descriptions.get(i),
                    builder.lengths.get(i),
                    builder.indexIndex.get(i));
    }

    public int getIndexStep() {
        return indexStep;
    }

    public int size() {
        return records.length;
    }

    public IndexRecord getRecordByIndex(int i) {
        return records[i];
    }

    public void write(OutputStream stream) {
        PrimitivO po = new PrimitivO(stream);
        po.writeInt(MAGIC);
        po.writeVarInt(indexStep);
        po.writeVarInt(indexArray.length);
        if (indexArray.length == 0)
            return;
        po.writeVarLong(indexArray[0]);
        for (int i = 1; i < indexArray.length; i++)
            po.writeVarLong(indexArray[i] - indexArray[i - 1]);

        po.writeVarInt(records.length);
        for (IndexRecord record : records) {
            po.writeVarInt(record.indexStart);
            po.writeVarLong(record.length);
            po.writeUTF(record.description);
        }
    }

    public static RandomAccessFastaIndex read(InputStream stream) {
        return new RandomAccessFastaIndex(stream);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RandomAccessFastaIndex)) return false;

        RandomAccessFastaIndex that = (RandomAccessFastaIndex) o;

        if (indexStep != that.indexStep) return false;
        if (!Arrays.equals(indexArray, that.indexArray)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(records, that.records);

    }

    @Override
    public int hashCode() {
        int result = indexStep;
        result = 31 * result + Arrays.hashCode(indexArray);
        result = 31 * result + Arrays.hashCode(records);
        return result;
    }

    public final class IndexRecord {
        /**
         * Description line
         */
        private final String description;
        /**
         * Sequence length
         */
        private final long length;
        /**
         * Index of first position in indexArray
         */
        private final int indexStart;

        public IndexRecord(String description, long length, int indexStart) {
            this.description = description;
            this.length = length;
            this.indexStart = indexStart;
        }

        /**
         * Returns fasta record description line. Line that goes after '>' in record header.
         *
         * @return fasta record description line; line that goes after '>' in record header
         */
        public String getDescription() {
            return description;
        }

        /**
         * Returns record length in letters.
         *
         * @return record length in letters
         */
        public long getLength() {
            return length;
        }

        /**
         * Returns encoded position {@code (positionInFile << 20) | (numberOfLettersToSkip)}. Operation takes {@code
         * O(1)}.
         *
         * @param offset sequence offset in letters
         * @return encoded position {@code (positionInFile << 20) | (numberOfLettersToSkip)}
         */
        public long queryPosition(long offset) {
            if (offset < 0 || offset >= length)
                throw new IndexOutOfBoundsException();

            int indexOffset = (int) (offset / indexStep);
            return (indexArray[indexStart + indexOffset] << FILE_POSITION_OFFSET) | (offset - (long) (indexOffset) * indexStep);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof IndexRecord)) return false;

            IndexRecord that = (IndexRecord) o;

            if (length != that.length) return false;
            if (indexStart != that.indexStart) return false;
            return description != null ? description.equals(that.description) : that.description == null;

        }

        @Override
        public int hashCode() {
            int result = description != null ? description.hashCode() : 0;
            result = 31 * result + (int) (length ^ (length >>> 32));
            result = 31 * result + indexStart;
            return result;
        }
    }

    public static final class StreamIndexBuilder {
        /**
         * Internal builder
         */
        final IndexBuilder builder;
        /**
         * -1 = builder finished it's work and invalidated
         */
        long currentStreamPosition = 0;
        long lastNonLineBreakPosition = -1;
        /**
         * Counter of sequence position
         */
        long currentSequencePosition = -1;
        /**
         * true if on first char of the line
         */
        boolean onLineStart = true;
        /**
         * -1 = not on header
         * >0 = on header
         */
        int headerBufferPointer = -1;
        /**
         * Stores header string before record creation
         */
        byte[] headerBuffer = new byte[32768];

        public StreamIndexBuilder() {
            this(DEFAULT_INDEX_STEP);
        }

        public StreamIndexBuilder(int indexStep) {
            this(new IndexBuilder(indexStep), 0L);
        }

        public StreamIndexBuilder(int indexStep, long streamPosition) {
            this(new IndexBuilder(indexStep), streamPosition);
        }

        public StreamIndexBuilder(IndexBuilder builder, long streamPosition) {
            this.currentStreamPosition = streamPosition;
            this.builder = builder;
        }

        public void processBuffer(String str) {
            processBuffer(str.getBytes());
        }

        public void processBuffer(byte[] buffer) {
            processBuffer(buffer, 0, buffer.length);
        }

        public void processBuffer(byte[] buffer, int offset, int length) {
            // Throw exception if builder invalidated
            if (currentStreamPosition == -1)
                throw new IllegalStateException();

            for (int i = 0; i < length; i++) {
                long streamPosition = currentStreamPosition++;
                byte b = buffer[offset + i];

                // Processing line breaks
                if (b == '\n' || b == '\r') {
                    if (!onLineStart)
                        lastNonLineBreakPosition = streamPosition - 1;
                    onLineStart = true;
                    continue;
                }

                // Detecting record header start
                if (onLineStart && b == '>') {
                    // End of record detected
                    endOfRecord();
                    headerBufferPointer = 0;
                    onLineStart = false;
                    continue;
                }

                // End of record header
                if (onLineStart && headerBufferPointer >= 0) {
                    builder.addRecord(new String(headerBuffer, 0, headerBufferPointer), streamPosition);
                    // We left header
                    headerBufferPointer = -1;
                    // We are at the very first letter of sequence
                    currentSequencePosition = 0;
                }

                if (headerBufferPointer == -1) {
                    long sequencePosition = currentSequencePosition++;
                    if (sequencePosition != 0 && sequencePosition % builder.indexStep == 0)
                        builder.addIndexPoint(streamPosition);
                } else
                    headerBuffer[headerBufferPointer++] = b;
            }
        }

        private void endOfRecord() {
            if (builder.isOnRecord())
                builder.setLastRecordLength(currentSequencePosition);
        }

        public RandomAccessFastaIndex build() {
            endOfRecord();
            currentStreamPosition = -1;
            return builder.build();
        }
    }

    public static final class IndexBuilder {
        private final int indexStep;
        private final List<String> descriptions = new ArrayList<>();
        private final TLongArrayList lengths = new TLongArrayList();
        private final TIntArrayList indexIndex = new TIntArrayList();
        private final TLongArrayList index = new TLongArrayList();

        public IndexBuilder() {
            this(DEFAULT_INDEX_STEP);
        }

        public IndexBuilder(int indexStep) {
            if (indexStep <= 0)
                throw new IllegalArgumentException();
            this.indexStep = indexStep;
        }

        public boolean isOnRecord() {
            return lengths.size() + 1 == descriptions.size();
        }

        public void addRecord(String description, long position) {
            if (lengths.size() != descriptions.size())
                throw new IllegalStateException();

            if (!index.isEmpty() && index.get(index.size() - 1) >= position)
                throw new IllegalArgumentException();

            descriptions.add(description);
            indexIndex.add(index.size());
            index.add(position);
        }

        public void addIndexPoint(long position) {
            index.add(position);
        }

        public void setLastRecordLength(long length) {
            if (lengths.size() + 1 != descriptions.size())
                throw new IllegalStateException();
            if ((index.size() - indexIndex.get(indexIndex.size() - 1) - 1) * indexStep > length)
                throw new IllegalArgumentException();

            lengths.add(length);
        }

        public RandomAccessFastaIndex build() {
            return new RandomAccessFastaIndex(this);
        }
    }
}

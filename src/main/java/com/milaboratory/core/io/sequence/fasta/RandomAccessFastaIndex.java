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

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TLongArrayList;

import java.util.ArrayList;
import java.util.List;

public class RandomAccessFastaIndex {
    public static final long SKIP_MASK = 0xFFFFFL;
    public static final int FILE_POSITION_OFFSET = 20;
    private final int indexStep;
    private final long[] indexArray;
    private final IndexRecord[] records;

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

    public IndexRecord getRecordByIndex(int i) {
        return records[i];
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
    }

    public static final class StreamIndexBuilder {
        final IndexBuilder builder;
        /**
         * -1 = builder finish it's work and invalidated.
         */
        long currentPosition = 0;

        public StreamIndexBuilder(int indexStep) {
            this.builder = new IndexBuilder(indexStep);
        }

        public StreamIndexBuilder(IndexBuilder builder) {
            this.builder = builder;
        }

        public void processBuffer(byte[] buffer, int offset, int length) {

        }

        public RandomAccessFastaIndex finish() {
            return null;
        }
    }

    public static final class IndexBuilder {
        private final int indexStep;
        private final List<String> descriptions = new ArrayList<>();
        private final TLongArrayList lengths = new TLongArrayList();
        private final TIntArrayList indexIndex = new TIntArrayList();
        private final TLongArrayList index = new TLongArrayList();

        public IndexBuilder(int indexStep) {
            this.indexStep = indexStep;
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

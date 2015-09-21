/*
 * Copyright 2015 MiLaboratory.com
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
package com.milaboratory.core.alignment.kaligner2;

import com.milaboratory.util.IntArrayList;

import java.util.Arrays;

import static java.lang.Math.*;

public final class OffsetPacksAccumulator {
    public static final int FIRST_INDEX = 0;
    public static final int LAST_INDEX = 1;
    public static final int MIN_VALUE = 2;
    public static final int MAX_VALUE = 3;
    public static final int LAST_VALUE = 4;
    public static final int SCORE = 5;
    public static final int STRETCH_INDEX_MARK = 0xA0000000;
    public static final int STRETCH_INDEX_MASK = 0xE0000000;

    public static final int RECORD_SIZE = 6;

    final int[] slidingArray;
    final int slotCount;
    final int allowedDelta;
    final int matchScore, mismatchScore, shiftScore, islandMinimalScore;
    final IntArrayList results = new IntArrayList(RECORD_SIZE * 2);

    final int bitsForIndex, indexMask;

    public OffsetPacksAccumulator(int slotCount, int allowedDelta, int matchScore,
                                  int mismatchScore, int shiftScore, int islandMinimalScore) {
        this(slotCount, allowedDelta, matchScore, mismatchScore, shiftScore, islandMinimalScore, 12);
    }

    public OffsetPacksAccumulator(int slotCount, int allowedDelta, int matchScore,
                                  int mismatchScore, int shiftScore, int islandMinimalScore,
                                  int bitsForIndex) {
        //Bits
        this.bitsForIndex = bitsForIndex;
        this.indexMask = 0xFFFFFFFF >>> (32 - bitsForIndex);

        this.slotCount = slotCount;
        this.allowedDelta = allowedDelta;
        this.slidingArray = new int[RECORD_SIZE * slotCount];
        this.matchScore = matchScore;
        this.mismatchScore = mismatchScore;
        this.shiftScore = shiftScore;
        this.islandMinimalScore = islandMinimalScore;
    }

    private void reset() {
        results.clear();
        Arrays.fill(slidingArray, Integer.MIN_VALUE);
    }

    /**
     * Accepts array with elements in the following format:
     */
    public void put(int[] data) {
        reset();

        int index, offset;
        OUTER:
        for (int recordId = 0; recordId < data.length; recordId++) {
            int record = data[recordId];
            offset = offset(record);
            index = index(record);

            // Matching existing records
            for (int i = 0; i < slidingArray.length; i += RECORD_SIZE) {
                if ((slidingArray[i + SCORE] & STRETCH_INDEX_MASK) == STRETCH_INDEX_MARK)
                    if (slidingArray[i + MIN_VALUE] <= offset && offset <= slidingArray[i + MAX_VALUE]) {

                        int pRecordId = slidingArray[i + SCORE] ^ STRETCH_INDEX_MARK,
                                pOffset = offset(data[pRecordId]),
                                pIndex = index(data[pRecordId]),
                                minDelta = Integer.MAX_VALUE,
                                minDeltaId = -1, temp;

                        if (minDelta > (temp = abs(offset - offset(data[pRecordId])))) {
                            minDeltaId = pRecordId;
                            minDelta = temp;
                        }

                        while (pRecordId < data.length - 1
                                && pIndex == index(data[pRecordId + 1])
                                && abs(pOffset - offset(data[pRecordId + 1])) <= allowedDelta)
                            if (minDelta > (temp = abs(offset - offset(data[++pRecordId])))) {
                                minDeltaId = pRecordId;
                                minDelta = temp;
                            }

                        pOffset = offset(data[minDeltaId]);
                        slidingArray[i + LAST_VALUE] = pOffset;
                        slidingArray[i + MIN_VALUE] = pOffset;
                        slidingArray[i + MAX_VALUE] = pOffset;
                        slidingArray[i + SCORE] = matchScore;
                    }

                if (inDelta(slidingArray[i + LAST_VALUE], offset)) {
                    // Processing exceptional cases for self-correlated K-Mers
                    // {

                    // If next record has same index and better offset
                    // (closer to current island LAST_VALUE)
                    if (recordId < data.length - 1
                            && index == index(data[recordId + 1])
                            && abs(slidingArray[i + LAST_VALUE] - offset) > abs(slidingArray[i + LAST_VALUE] - offset(data[recordId + 1])))
                        // Skip current record
                        continue OUTER;

                    // If previous record has same index and better offset
                    // (closer to current island LAST_VALUE)
                    if (recordId > 0
                            && index == index(data[recordId - 1])
                            && abs(slidingArray[i + LAST_VALUE] - offset) > abs(slidingArray[i + LAST_VALUE] - offset(data[recordId - 1])))
                        // Skip current record
                        continue OUTER;

                    // }

//                    int j = i - LAST_VALUE;

                    assert index > slidingArray[i + LAST_INDEX];

                    int scoreDelta = matchScore + (index - slidingArray[i + LAST_INDEX] - 1) * mismatchScore +
                            abs(slidingArray[i + LAST_VALUE] - offset) * shiftScore;

                    if (scoreDelta > 0) {
                        slidingArray[i + LAST_INDEX] = index;
                        slidingArray[i + MIN_VALUE] = min(slidingArray[i + MIN_VALUE], offset);
                        slidingArray[i + MAX_VALUE] = max(slidingArray[i + MAX_VALUE], offset);
                        slidingArray[i + LAST_VALUE] = offset;
                        slidingArray[i + SCORE] += scoreDelta;
                        continue OUTER;
                    }
                }
            }

            int minimalIndex = -1;
            int minimalValue = Integer.MAX_VALUE;
            for (int i = LAST_INDEX; i < slidingArray.length; i += RECORD_SIZE) {
                if (slidingArray[i] == Integer.MIN_VALUE) {
                    minimalIndex = i;
                    break;
                } else if (slidingArray[i] < minimalValue) {
                    minimalIndex = i;
                    minimalValue = slidingArray[i];
                }
            }
            minimalIndex -= LAST_INDEX;

            assert minimalIndex >= 0;

            //finishing previous record
            finished(minimalIndex);

            //create new record
            slidingArray[minimalIndex + FIRST_INDEX] = index;
            slidingArray[minimalIndex + LAST_INDEX] = index;
            slidingArray[minimalIndex + MIN_VALUE] = offset;
            slidingArray[minimalIndex + MAX_VALUE] = offset;
            //TODO move lower; don't copy
            slidingArray[minimalIndex + LAST_VALUE] = offset;
            slidingArray[minimalIndex + SCORE] = matchScore;

            // If next record has same index
            while (recordId < data.length - 1
                    && index == index(data[recordId + 1])
                    && abs(offset - offset(data[recordId + 1])) <= allowedDelta) {
                //mark slot
                if (slidingArray[minimalIndex + SCORE] > 0) {
                    slidingArray[minimalIndex + SCORE] = STRETCH_INDEX_MARK | recordId;
                    slidingArray[minimalIndex + LAST_VALUE] = Integer.MIN_VALUE;
                }

                assert slidingArray[minimalIndex + MAX_VALUE] < offset(data[recordId + 1]);

                slidingArray[minimalIndex + MAX_VALUE] = offset = offset(data[++recordId]);
            }
        }

        for (int i = 0; i < slidingArray.length; i += RECORD_SIZE)
            finished(i);
    }

    private void finished(int indexOfFinished) {
        if (slidingArray[indexOfFinished + SCORE] < islandMinimalScore)
            return;//just drop

        results.add(slidingArray, indexOfFinished, RECORD_SIZE);
    }

    private boolean inDelta(int a, int b) {
        int diff = a - b;
        return -allowedDelta <= diff && diff <= allowedDelta;
    }

    public int numberOfIslands() {
        return results.size() / RECORD_SIZE;
    }

    private int index(int record) {
        return record & indexMask;
    }

    private int offset(int record) {
        return record >> bitsForIndex;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Number of clusters: " + numberOfIslands()).append("\n\n");
        int k = 0;
        for (int i = 0; i < results.size(); i += RECORD_SIZE) {
            sb.append(k++ + "th cloud:\n")
                    .append("  first index:" + results.get(i + FIRST_INDEX)).append("\n")
                    .append("  last index:" + results.get(i + LAST_INDEX)).append("\n")
                    .append("  minimal index:" + results.get(i + MIN_VALUE)).append("\n")
                    .append("  maximal index:" + results.get(i + MAX_VALUE)).append("\n")
                    .append("  score:" + results.get(i + SCORE)).append("\n\n");
        }

        return sb.toString();
    }
}

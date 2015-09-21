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

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

public final class OffsetPacksAccumulator {
    /**
     * FirstIndex  0    0
     * LastIndex   0    1
     * MinValue    100  100
     * MaxValue    100  100
     * (key) LastValue   100  100
     * Score       10   20
     */
    public static final int FIRST_INDEX = 0;
    public static final int LAST_INDEX = 1;
    public static final int MIN_VALUE = 2;
    public static final int MAX_VALUE = 3;
    public static final int LAST_VALUE = 4;
    public static final int SCORE = 5;

    public static final int RECORD_SIZE = 6;

    final int[] slidingArray;
    final int slotCount;
    final int allowedDelta;
    final int matchScore, mismatchScore, shiftScore;
    final IntArrayList results = new IntArrayList(RECORD_SIZE * 2);

    public OffsetPacksAccumulator(int slotCount, int allowedDelta, int matchScore,
                                  int mismatchScore, int shiftScore) {
        this.slotCount = slotCount;
        this.allowedDelta = allowedDelta;
        this.slidingArray = new int[RECORD_SIZE * slotCount];
        this.matchScore = matchScore;
        this.mismatchScore = mismatchScore;
        this.shiftScore = shiftScore;
    }

    public void reset() {
        for (int i = 0; i < slidingArray.length; i += RECORD_SIZE) {
            slidingArray[i + FIRST_INDEX] = -1;
            slidingArray[i + LAST_INDEX] = Integer.MIN_VALUE;
            slidingArray[i + LAST_VALUE] = Integer.MIN_VALUE;
        }
    }

    public void put(int offset, int index) {
        // Matching existing records
        for (int i = LAST_VALUE; i < slidingArray.length; i += RECORD_SIZE)
            if (inDelta(slidingArray[i], offset)) {
                i -= LAST_VALUE;

                assert index > slidingArray[i + LAST_INDEX];

                int scoreDelta = matchScore + (index - slidingArray[i + LAST_INDEX] - 1) * mismatchScore +
                        abs(slidingArray[i + LAST_VALUE] - offset) * shiftScore;

                if (scoreDelta > 0) {
                    slidingArray[i + LAST_INDEX] = index;
                    slidingArray[i + MIN_VALUE] = min(slidingArray[i + MIN_VALUE], offset);
                    slidingArray[i + MAX_VALUE] = max(slidingArray[i + MAX_VALUE], offset);
                    slidingArray[i + LAST_VALUE] = offset;
                    slidingArray[i + SCORE] += scoreDelta;
                    return;
                }

                break;
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

        assert minimalIndex != -1;


    }

    private void finished(int recordOffset) {
        
    }

    private boolean inDelta(int a, int b) {
        int diff = a - b;
        return diff <= allowedDelta && diff >= -allowedDelta;
    }
}

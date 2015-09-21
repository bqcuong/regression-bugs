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

/**
 * KMappingHit - class which represents single hit for {@link KMappingResult2}
 */
public final class KMappingHit2 implements java.io.Serializable {
    /**
     * Best offset value (most popular offset)
     */
    final int offset,
    /**
     * Reference id (in storage)
     */
    id;
    /**
     * Intersection range of target and reference sequences, where they are supposed to be aligned From and to are
     * indices of seeds in seeds array of {@link KMappingResult2}
     */
    int from, to;
    /**
     * Alignment score value
     */
    float score;
    /**
     * Offset values for according seeds inside intersection
     */
    final int[] seedOffsets;
    /**
     * Islands boundaries
     */
    final int[] boundaries;

    /**
     * Creates new KMappingHit
     *
     * @param seedOffsets offset values for according seeds inside intersection
     * @param boundaries  boundaries of compact supports
     * @param offset      best offset value (most popular offset)
     * @param id          reference id (in {@link com.milaboratory.core.alignment.KMapper} storage)
     * @param score       absolute alignment score value
     * @param from        index of seed in seeds array of {@link KMappingResult2} from which intersection range of target and reference sequences starts
     * @param to          index of seed in seeds array of {@link KMappingResult2} from which intersection range of target and reference sequences ends
     */
    public KMappingHit2(int[] seedOffsets, int[] boundaries, int offset, int id, float score, int from, int to) {
        this.seedOffsets = seedOffsets;
        this.boundaries = boundaries;
        this.offset = offset;
        this.id = id;
        this.score = score;
        this.from = from;
        this.to = to;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "offset=" + offset +
                ", id=" + id +
                ", score=" + score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KMappingHit2 kMappingHit2 = (KMappingHit2) o;

        if (id != kMappingHit2.id) return false;
        if (offset != kMappingHit2.offset) return false;
        if (Float.compare(kMappingHit2.score, score) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = offset;
        result = 31 * result + id;
        result = 31 * result + (score != +0.0f ? Float.floatToIntBits(score) : 0);
        return result;
    }
}

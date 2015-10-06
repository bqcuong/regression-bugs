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

import com.milaboratory.core.sequence.NucleotideSequence;
import com.milaboratory.util.IntArrayList;
import com.milaboratory.util.RandomUtil;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.milaboratory.core.alignment.kaligner2.OffsetPacksAccumulator.*;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.util.Arrays.copyOf;

/**
 * KMapper - class to perform fast alignment based only on matches between kMers of target and one of reference
 * sequences. Alignment performed using seed-and-vote procedure.
 *
 * <p>{@link #align(NucleotideSequence, int, int)} and {@link
 * #align(NucleotideSequence)} methods of this object are thread-safe and can
 * be concurrently used by several threads if no new sequences added after its first invocation.</p>
 *
 * <p><b>Algorithm inspired by:</b> <i>Liao Y et al.</i> The Subread aligner: fast, accurate and scalable read mapping
 * by seed-and-vote. <i>Nucleic Acids Res. 2013 May 1;41(10):e108. doi: 10.1093/nar/gkt214. Epub 2013 Apr 4.</i></p>
 */
//TODO visuzlization for hits
public final class KMapper2 implements java.io.Serializable {
    public static final int SEED_NOT_FOUND_OFFSET = Integer.MIN_VALUE + 1;

    /*
                                   MSB                         LSB
                                   < --------- 32 bits --------- >
        Base record format:   int  |.... ID ....|.... OFFSET ....|
                                                 < bitsForOffset >
     */

    /**
     * Number of bits in base record for offset value
     */
    private static final int bitsForIndex = 13;
    /**
     * Index mask (= 0xFFFFFFFF << (32 - bitsForIndex))
     */
    private static final int indexMask = 0xFFFFFFFF >>> (32 - bitsForIndex);
    /**
     * Mask to extract offset value (= 0xFFFFFFFF >>> bitsForIndex)
     */
    private static final int offsetMask = 0xFFFFFFFF >>> bitsForIndex;

    /*           Parameters             */

    /**
     * Nucleotides in kMer (value of k)
     */
    private final int kValue;
    /**
     * Base of records for individual kMers
     */
    private final int[][] base;
    /**
     * Number of records for each individual kMer (used only for building of base)
     */
    private final int[] lengths;
    /**
     * Minimal absolute score value
     */
    private final int absoluteMinClusterScore,
    //TODO
    extraClusterScore,
    /**
     * Reward for match (must be > 0)
     */
    matchScore,
    /**
     * Penalty for kMer mismatch (not mapped kMer), must be < 0
     */
    mismatchScore,
    /**
     * Penalty for different offset between adjacent seeds
     */
    offsetShiftScore,

    slotCount,

    maxClusterIndels;

    /**
     * Minimal score in fractions of top score.
     */
    private final float relativeMinScore;


    /**
     * Determines boundaries type: floating(only part of sequence should be aligned) or fixed (whole sequence should be
     * aligned).
     */
    private final boolean floatingLeftBound, floatingRightBound;
    /**
     * Minimal and maximal distance between kMer seed positions in target sequence
     */
    private final int minDistance, maxDistance;

    /*                  Utility fields                   */
    private boolean built = false;
    private int[] refFrom = new int[10], refLength = new int[10];
    private int maxReferenceLength = 0, minReferenceLength = Integer.MAX_VALUE;
    private int sequencesInBase = 0;
    //private final float terminationThreshold = 6.6e6f;

    /**
     * Creates new KMer mapper.
     *
     * @param kValue                  nucleotides in kMer (value of k)
     * @param minDistance             minimal distance between kMer seed positions in target sequence
     * @param maxDistance             maximal distance between kMer seed positions in target sequence
     * @param absoluteMinClusterScore minimal score
     * @param relativeMinScore        maximal ratio between best hit score and other hits scores in returned result
     * @param matchScore              reward for match (must be > 0)
     * @param mismatchScore           penalty for mismatch (must be < 0)
     * @param floatingLeftBound       true if left bound of alignment could be floating
     * @param floatingRightBound      true if right bound of alignment could be floating
     */
    public KMapper2(int kValue,
                    int minDistance, int maxDistance,
                    int absoluteMinClusterScore, int extraClusterScore,
                    float relativeMinScore,
                    int matchScore, int mismatchScore, int offsetShiftScore,
                    int slotCount, int maxClusterIndels,
                    boolean floatingLeftBound, boolean floatingRightBound) {
        this.kValue = kValue;

        //Bits
        //this.bitsForIndex = 14;
        //this.offsetMask = 0xFFFFFFFF >>> bitsForIndex;
        //this.indexMask = 0xFFFFFFFF >>> (32 - bitsForIndex);

        //Initialize base
        int maxNumberOfKmers = 1 << (kValue * 2);

        // TODO lazy
        base = new int[maxNumberOfKmers][10];
        lengths = new int[maxNumberOfKmers];

        //Parameters
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.absoluteMinClusterScore = absoluteMinClusterScore;
        this.extraClusterScore = extraClusterScore;
        this.relativeMinScore = relativeMinScore;
        this.matchScore = matchScore;
        this.mismatchScore = mismatchScore;
        this.offsetShiftScore = offsetShiftScore;
        this.slotCount = slotCount;
        this.maxClusterIndels = maxClusterIndels;
        this.floatingLeftBound = floatingLeftBound;
        this.floatingRightBound = floatingRightBound;
    }

    /**
     * Factory method to create KMapper using parametners specified in the {@link KAlignerParameters2}
     * object.
     *
     * @param parameters parameters instance
     * @return new KMapper
     */
    public static KMapper2 createFromParameters(KAlignerParameters2 parameters) {
        return new KMapper2(parameters.getMapperKValue(), parameters.getMapperMinSeedsDistance(),
                parameters.getMapperMaxSeedsDistance(), parameters.getMapperAbsoluteMinClusterScore(),
                parameters.getMapperExtraClusterScore(),
                parameters.getMapperRelativeMinScore(),
                parameters.getMapperMatchScore(), parameters.getMapperMismatchScore(),
                parameters.getMapperOffsetShiftScore(), parameters.getMapperSlotCount(),
                parameters.getMapperMaxClusterIndels(),
                parameters.isFloatingLeftBound(), parameters.isFloatingRightBound());
    }

    /**
     * Encodes and adds individual kMer to the base.
     */
    private void addKmer(int kmer, int id, int offset) {
        if (base[kmer].length == lengths[kmer])
            base[kmer] = copyOf(base[kmer], base[kmer].length * 3 / 2 + 1);

        if ((offset & offsetMask) != offset)
            throw new IllegalArgumentException("Record is too long.");

        assert lengths[kmer] == 0 || index(base[kmer][lengths[kmer] - 1]) != id
                || offset(base[kmer][lengths[kmer] - 1]) < offset;

        base[kmer][lengths[kmer]++] = record(offset, id);
    }

    /**
     * Adds new reference sequence to the base of this mapper and returns index assigned to it.
     *
     * @param sequence sequence
     * @return index assigned to the sequence
     */
    public int addReference(NucleotideSequence sequence) {
        return addReference(sequence, 0, sequence.size());
    }

    /**
     * Adds new reference sequence to the base of this mapper and returns index assigned to it.
     *
     * <p>User can specify a part of a sequence to be indexed (only this part will be identified during the alignment
     * procedure). The offset returned by alignment procedure will be in global sequence coordinates, relative to the
     * beginning of the sequence (not to the specified offset).</p>
     *
     * @param sequence sequence
     * @param offset   offset of subsequence to be indexed
     * @param length   length of subsequence to be indexed
     * @return index assigned to the sequence
     */
    public int addReference(NucleotideSequence sequence, int offset, int length) {
        // Checking parameters
        if (sequencesInBase >= (1 << bitsForIndex))
            throw new IllegalArgumentException("Maximum number of records reached.");

        if (length + offset >= (1 << (32 - bitsForIndex)))
            throw new IllegalArgumentException("Sequence is too long.");

        //Resetting built flag
        built = false;

        //Next id.
        if (refLength.length == sequencesInBase) {
            refLength = copyOf(refLength, sequencesInBase * 3 / 2 + 1);
            refFrom = copyOf(refFrom, sequencesInBase * 3 / 2 + 1);
        }
        int id = sequencesInBase++;
        refFrom[id] = offset;
        refLength[id] = sequence.size();

        //Calculating min and max reference sequences lengths
        maxReferenceLength = max(maxReferenceLength, sequence.size());
        minReferenceLength = Math.min(minReferenceLength, sequence.size());

        int kmer = 0;
        int kmerMask = 0xFFFFFFFF >>> (32 - kValue * 2);
        int tMask = 0xFFFFFFFF >>> (34 - kValue * 2);

        int to = length - kValue;
        for (int j = 0; j < kValue; ++j)
            kmer = kmer << 2 | sequence.codeAt(j + offset);
        addKmer(kmer, id, offset);

        for (int i = 1; i <= to; ++i) {
            //Next kMer
            kmer = kmerMask & (kmer << 2 | sequence.codeAt(offset + i + kValue - 1));

            //Detecting homopolymeric kMers and dropping them
            if (((kmer ^ (kmer >>> 2)) & tMask) == 0 &&
                    ((kmer ^ (kmer << 2)) & (tMask << 2)) == 0)
                continue;

            addKmer(kmer, id, i + offset);
        }

        return id;
    }

    /**
     * Builds additional data fields used by this mapper. Invoked automatically if this mapper is not yet built by
     * {@link #align(NucleotideSequence, int, int)} method.
     */
    void ensureBuilt() {
        if (!built)
            synchronized (this) {
                if (!built) {
                    for (int i = 0; i < base.length; ++i)
                        base[i] = copyOf(base[i], lengths[i]);
                    refLength = copyOf(refLength, sequencesInBase);
                    refFrom = copyOf(refFrom, sequencesInBase);
                    built = true;
                }
            }
    }

    /**
     * Performs an alignment.
     *
     * <p>This methods is thread-safe and can be concurrently used by several threads if no new sequences added after
     * its first invocation.</p>
     *
     * @param sequence target sequence
     * @return a list of hits found in the target sequence
     */
    public KMappingResult2 align(NucleotideSequence sequence) {
        return align(sequence, 0, sequence.size());
    }

    /**
     * Performs an alignment for a part of the target sequence.
     *
     * <p>This methods is thread-safe and can be concurrently used by several threads if no new sequences added after
     * its first invocation.</p>
     *
     * @param sequence target sequence
     * @param from     first nucleotide to align (inclusive)
     * @param to       last nucleotide to align (exclusive)
     * @return a list of hits found in the target sequence
     */
    public KMappingResult2 align(final NucleotideSequence sequence, final int from, final int to) {
        ensureBuilt();

        final ArrayList<KMappingHit2> result = new ArrayList<>();

        if (to - from <= kValue)
            return new KMappingResult2(null, result);

        final IntArrayList seedPositions = new IntArrayList((to - from) / minDistance + 2);
        int seedPosition = from;
        seedPositions.add(seedPosition);

        RandomGenerator random = RandomUtil.getThreadLocalRandom();

        while ((seedPosition += random.nextInt(maxDistance + 1 - minDistance) + minDistance) < to - kValue)
            seedPositions.add(seedPosition);

        seedPositions.add(to - kValue);

        int kmer;
        final IntArrayList[] candidates = new IntArrayList[sequencesInBase];

        //Building candidates arrays (seed)
        int id, positionInTarget;
        for (int i = 0; i < seedPositions.size(); ++i) {
            kmer = 0;
            for (int j = seedPositions.get(i); j < seedPositions.get(i) + kValue; ++j)
                kmer = kmer << 2 | sequence.codeAt(j);

            if (base[kmer].length == 0)
                continue;

            for (int record : base[kmer]) {
                id = index(record);
                positionInTarget = offset(record);

                if (candidates[id] == null)
                    candidates[id] = new IntArrayList();

                assert candidates[id].isEmpty() || index(candidates[id].last()) != i
                        || offset(candidates[id].last()) < positionInTarget - seedPositions.get(i);

                candidates[id].add(record(positionInTarget - seedPositions.get(i), i));
            }
        }

        final int possibleMinKmers = (int) Math.ceil(absoluteMinClusterScore / matchScore);

        OffsetPacksAccumulator accumulator = new OffsetPacksAccumulator(slotCount, maxClusterIndels, matchScore,
                mismatchScore, offsetShiftScore, absoluteMinClusterScore);

        for (int i = 0; i < candidates.length; i++) {
            if (candidates[i] == null)
                continue;
            if (candidates[i].size() - 1 < possibleMinKmers)
                continue;

            accumulator.calculateInitialPartitioning(candidates[i]);

            result.add(calculateHit(accumulator.results, i, candidates[i], seedPositions));
        }

        Collections.sort(result, new Comparator<KMappingHit2>() {
            @Override
            public int compare(KMappingHit2 o1, KMappingHit2 o2) {
                return Integer.compare(o2.score, o1.score);
            }
        });
        return new KMappingResult2(seedPositions, result);
    }

    public KMappingHit2 calculateHit(IntArrayList results, int id, IntArrayList data, IntArrayList seedPositions) {
        return calculateHit(results, id, IntArrayList.getArrayReference(data), 0, data.size(), seedPositions);
    }

    private void truncateClusterFromRightAndRebuild(
            final IntArrayList results, final int[] data,
            final int dataTo, final int clusterId, final int indexTo) {
        int lastRecordId,
                record = data[lastRecordId = results.get(clusterId + FIRST_RECORD_ID)],
                prevOffset = offset(record),
                prevIndex = index(record),
                score = matchScore;

        //roll right in stretch
        int i = results.get(clusterId + FIRST_RECORD_ID) + 1;
        while (i < dataTo && prevIndex == index(data[i++])) ;

        //calculate score
        int offset, index = prevIndex;
        OUTER:
        for (; i < dataTo && index < indexTo; ++i) {
            offset = offset(data[i]);
            index = index(data[i]);
            if (inDelta(prevOffset, offset, maxClusterIndels)) {
                // Processing exceptional cases for self-correlated K-Mers

                // If next record has same index and better offset
                // (closer to current island LAST_VALUE)
                if (i < dataTo - 1
                        && index == index(data[i + 1])
                        && abs(prevOffset - offset) > abs(prevOffset - offset(data[i + 1])))
                    // Skip current record
                    continue OUTER;

                int scoreDelta = matchScore + (index - prevIndex - 1) * mismatchScore +
                        abs(prevOffset - offset) * offsetShiftScore;

                if (scoreDelta > 0) {
                    score += scoreDelta;
                    prevOffset = offset;
                    prevIndex = index;
                    lastRecordId = i;
                }
            }
        }
        results.set(clusterId + LAST_RECORD_ID, lastRecordId);
        results.set(clusterId + SCORE, score);
    }

    private void truncateClusterFromLeftAndRebuild(
            final IntArrayList results, final int[] data,
            final int dataFrom, final int clusterId, final int indexFrom) {
        int lastRecordId,
                record = data[lastRecordId = results.get(clusterId + FIRST_RECORD_ID)],
                prevOffset = offset(record),
                prevIndex = index(record),
                score = matchScore;

        //roll right in stretch
        int i = results.get(clusterId + LAST_RECORD_ID) - 1;
        while (i >= dataFrom && prevIndex == index(data[i--])) ;

        //calculate score
        int offset, index = prevIndex;
        OUTER:
        for (; i >= dataFrom && index > indexFrom; --i) {
            offset = offset(data[i]);
            index = index(data[i]);
            if (inDelta(prevOffset, offset, maxClusterIndels)) {
                // Processing exceptional cases for self-correlated K-Mers

                // If next record has same index and better offset
                // (closer to current island LAST_VALUE)
                if (i > dataFrom
                        && index == index(data[i - 1])
                        && abs(prevOffset - offset) > abs(prevOffset - offset(data[i - 1])))
                    // Skip current record
                    continue OUTER;

                int scoreDelta = matchScore + (index - prevIndex - 1) * mismatchScore +
                        abs(prevOffset - offset) * offsetShiftScore;

                if (scoreDelta > 0) {
                    score += scoreDelta;
                    prevOffset = offset;
                    prevIndex = index;
                    lastRecordId = i;
                }
            }
        }
        results.set(clusterId + FIRST_RECORD_ID, lastRecordId);
        results.set(clusterId + SCORE, score);
    }

    private KMappingHit2 calculateHit(final IntArrayList results, final int id, final int[] data,
                                      final int dataFrom, final int dataTo,
                                      final IntArrayList seedPositions) {
        IntArrayList seedRecords = new IntArrayList(); // TODO initialize with rational length
        IntArrayList packBoundaries = new IntArrayList();

        for (int i = 0; i < results.size(); i += OUTPUT_RECORD_SIZE)
            if (results.get(i + FIRST_RECORD_ID) != DROPPED_CLUSTER)
                for (int j = i + OUTPUT_RECORD_SIZE; j < results.size(); j += OUTPUT_RECORD_SIZE) {
                    if (results.get(j + FIRST_RECORD_ID) == DROPPED_CLUSTER)
                        continue;
                    //intersecting clusters in query
                    int a = i, b = j;
                    int aStartIndex = index(data[results.get(a + FIRST_RECORD_ID)]);
                    int aEndIndex = index(data[results.get(a + LAST_RECORD_ID)]);
                    int bStartIndex = index(data[results.get(b + FIRST_RECORD_ID)]);
                    int bEndIndex = index(data[results.get(b + LAST_RECORD_ID)]);

                    if (aStartIndex > bStartIndex) {
                        //swap by xor
                        aStartIndex ^= bStartIndex;
                        bStartIndex ^= aStartIndex;
                        aStartIndex ^= bStartIndex;
                        aEndIndex ^= bEndIndex;
                        bEndIndex ^= aEndIndex;
                        aEndIndex ^= bEndIndex;
                        a ^= b;
                        b ^= a;
                        a ^= b;
                    }

                    if (aEndIndex >= bStartIndex) {
                        if (bEndIndex <= aEndIndex) {
                            if (results.get(a + SCORE) < results.get(b + SCORE))
                                results.set(a + FIRST_RECORD_ID, DROPPED_CLUSTER);
                            else
                                results.set(b + FIRST_RECORD_ID, DROPPED_CLUSTER);
                        } else {
                            if (results.get(a + SCORE) < results.get(b + SCORE))
                                truncateClusterFromRightAndRebuild(results, data, dataTo, a, results.get(b + LAST_RECORD_ID));
                            else
                                truncateClusterFromLeftAndRebuild(results, data, dataFrom, b, results.get(a + FIRST_RECORD_ID));
                        }
                    }
                }


        int score = 0;
        for (int i = 0; i < results.size(); i += OUTPUT_RECORD_SIZE) {
            if (i != 0) {
                packBoundaries.add(seedRecords.size());
                score += extraClusterScore;
            }

            int recordId = results.get(i + FIRST_RECORD_ID);

            assert recordId >= dataFrom;

            int lastIndex = results.get(i + LAST_INDEX);

            int record, index, offset, delta;
            score += matchScore;

            int previousIndex = index = index(record = data[recordId]);
            seedRecords.add(record);

            int previousOffset = offset(record);

            while (++recordId < dataTo && index(data[recordId]) == index) ;

            while (recordId < dataTo && (index = index(record = data[recordId++])) <= lastIndex) {
                offset = offset(record);
                int minRecord = record;
                int minDelta = abs(offset - previousOffset);

                if (minDelta > maxClusterIndels)
                    continue;

                boolean $ = false;
                while (recordId < dataTo && index(record = data[recordId++]) == index) {
                    if ((delta = abs(offset(record) - previousOffset)) < minDelta) {
                        minDelta = delta;
                        minRecord = record;
                    }
                    $ = true;
                }
                if ($)
                    --recordId;

                score += matchScore + (index - previousIndex - 1) * mismatchScore +
                        minDelta * offsetShiftScore;

                seedRecords.add(minRecord);
                previousIndex = index;
            }
        }

        return new KMappingHit2(id, seedRecords.toArray(), packBoundaries.toArray(), score);
    }

    /**
     * Returns number of nucleotides in kMer (value of k)
     *
     * @return number of nucleotides in kMer (value of k)
     */

    public int getKValue() {
        return kValue;
    }

    /**
     * Returns minimal score for the cluster
     *
     * @return minimal score for the cluster
     */
    public int getAbsoluteMinClusterScore() {
        return absoluteMinClusterScore;
    }

    public int getExtraClusterScore() {
        return extraClusterScore;
    }

    /**
     * Returns maximal distance between kMer seed positions in target sequence
     *
     * @return maximal distance between kMer seed positions in target sequence
     */
    public int getMaxDistance() {
        return maxDistance;
    }

    /**
     * Returns minimal distance between kMer seed positions in target sequence
     *
     * @return minimal distance between kMer seed positions in target sequence
     */
    public int getMinDistance() {
        return minDistance;
    }

    /**
     * Returns maximal ratio between best hit score and other hits scores in returned result
     *
     * @return maximal ratio between best hit score and other hits scores in returned result
     */
    public float getRelativeMinScore() {
        return relativeMinScore;
    }

    static int index(int record) {
        return record & indexMask;
    }

    static int offset(int record) {
        return record >> bitsForIndex;
    }

    static int record(int offset, int index) {
        return (offset << bitsForIndex) | index;
    }

    static boolean inDelta(int a, int b, int maxAllowedDelta) {
        int diff = a - b;
        return -maxAllowedDelta <= diff && diff <= maxAllowedDelta;
    }

    /**
     * Method used internally.
     */
    public SummaryStatistics getRecordSizeSummaryStatistics() {
        SummaryStatistics ss = new SummaryStatistics();
        for (int len : lengths)
            ss.addValue(len);
        return ss;
    }

    @Override
    public String toString() {
        SummaryStatistics ss = getRecordSizeSummaryStatistics();
        return "K=" + kValue + "; Avr=" + ss.getMean() + "; SD=" + ss.getStandardDeviation();
    }

    /**
     * Used to store preliminary information about hit.
     */
    private static class Info {
        int offset, score;
    }
}

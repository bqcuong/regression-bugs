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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import static com.milaboratory.core.alignment.kaligner2.OffsetPacksAccumulator.*;
import static java.lang.Math.*;
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

    private static final int maxClusters = 10;

    /**
     * Minimal absolute score.
     */
    private final int absoluteMinScore;
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
    private volatile boolean built = false;
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
                    int absoluteMinScore, float relativeMinScore,
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
        this.absoluteMinScore = absoluteMinScore;
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
                parameters.getMapperAbsoluteMinScore(),
                parameters.getMapperRelativeMinScore(),
                parameters.getMapperMatchScore(), parameters.getMapperMismatchScore(),
                parameters.getMapperOffsetShiftScore(), parameters.getMapperSlotCount(),
                parameters.getMapperMaxClusterIndels(),
                parameters.isFloatingLeftBound(), parameters.isFloatingRightBound());
    }

    final ThreadLocal<OffsetPacksAccumulator> threadLocalAccumulator = new ThreadLocal<OffsetPacksAccumulator>() {
        @Override
        protected OffsetPacksAccumulator initialValue() {
            return new OffsetPacksAccumulator(
                    slotCount, maxClusterIndels, matchScore,
                    mismatchScore, offsetShiftScore, absoluteMinClusterScore);
        }
    };

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
        // Checking parameters
        if (sequencesInBase >= (1 << bitsForIndex))
            throw new IllegalArgumentException("Maximum number of records reached.");

        //Resetting built flag
        built = false;

        int id = sequencesInBase++;

        //Calculating min and max reference sequences lengths
        maxReferenceLength = max(maxReferenceLength, sequence.size());
        minReferenceLength = Math.min(minReferenceLength, sequence.size());

        int kmer = 0;
        int kmerMask = 0xFFFFFFFF >>> (32 - kValue * 2);
        int tMask = 0xFFFFFFFF >>> (34 - kValue * 2);

        int to = sequence.size() - kValue;
        for (int j = 0; j < kValue; ++j)
            kmer = kmer << 2 | sequence.codeAt(j);
        addKmer(kmer, id, 0);

        for (int i = 1; i <= to; ++i) {
            //Next kMer
            kmer = kmerMask & (kmer << 2 | sequence.codeAt(i + kValue - 1));

            //Detecting homopolymeric kMers and dropping them
            if (((kmer ^ (kmer >>> 2)) & tMask) == 0 &&
                    ((kmer ^ (kmer << 2)) & (tMask << 2)) == 0)
                continue;

            addKmer(kmer, id, i);
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

        final ArrList<KMappingHit2> result = new ArrList<>();

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

        for (int i = 0; i < candidates.length; i++) {
            if (candidates[i] == null)
                continue;
            if (candidates[i].size() - 1 < possibleMinKmers)
                continue;

            KMappingHit2 e = calculateHit(i, candidates[i], seedPositions);
            if (e != null)
                result.add(e);
        }

        Collections.sort(result, SCORE_COMPARATOR);
        if (!result.isEmpty()) {
            int threshold = max((int) (result.get(0).score * relativeMinScore), absoluteMinScore);
            int i = 0;
            for (; i < result.size(); ++i)
                if (result.get(i).score <= threshold)
                    break;
            result.removeRange(i, result.size());
        }
        return new KMappingResult2(seedPositions, result);
    }

    private static final Comparator<KMappingHit2> SCORE_COMPARATOR = new Comparator<KMappingHit2>() {
        @Override
        public int compare(final KMappingHit2 o1, final KMappingHit2 o2) {
            return Integer.compare(o2.score, o1.score);
        }
    };

    public KMappingHit2 calculateHit(int id, IntArrayList data, IntArrayList seedPositions) {
        return calculateHit(id, IntArrayList.getArrayReference(data), 0, data.size(), seedPositions);
    }

    private boolean truncateClusterFromRight(
            boolean byIndex,
            final IntArrayList seedPositions,
            final IntArrayList results, final int[] data,
            final int dataTo, final int clusterPointer, final int truncationPoint) {
        int lastRecordId = results.get(clusterPointer + FIRST_RECORD_ID),
                record = data[lastRecordId],
                prevOffset = offset(record),
                prevIndex = index(record),
                score = matchScore;

        //roll right in stretch
        int i = lastRecordId;
        while (++i < dataTo && prevIndex == index(data[i])) ;

        //calculate score
        int offset, index;
        for (; i < dataTo &&
                (byIndex ?
                        index(data[i]) < truncationPoint :
                        (positionInTarget(seedPositions, data[i]) < truncationPoint));
             ++i) {
            index = index(data[i]);
            offset = offset(data[i]);
            if (inDelta(prevOffset, offset, maxClusterIndels)) {
                // Processing exceptional cases for self-correlated K-Mers
                // If next record has same index and better offset
                // (closer to current island LAST_VALUE)
                if (i < dataTo - 1
                        && index == index(data[i + 1])
                        && abs(prevOffset - offset) > abs(prevOffset - offset(data[i + 1])))
                    // Skip current record
                    continue;

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
        results.set(clusterPointer + LAST_RECORD_ID, lastRecordId);
        results.set(clusterPointer + SCORE, score);
        if (score < absoluteMinClusterScore) {
            results.set(clusterPointer + FIRST_RECORD_ID, DROPPED_CLUSTER);
            return false;
        }
        return true;
    }

    private boolean truncateClusterFromLeft(
            boolean byIndex,
            final IntArrayList seedPositions,
            final IntArrayList results, final int[] data,
            final int dataFrom, final int clusterPointer, final int truncationPoint) {
        int lastRecordId = results.get(clusterPointer + FIRST_RECORD_ID),
                record = data[lastRecordId],
                prevOffset = offset(record),
                prevIndex = index(record),
                score = matchScore;

        //roll right in stretch
        int i = lastRecordId;
        while (--i >= dataFrom && prevIndex == index(data[i])) ;

        //calculate score
        int offset, index;
        for (; i >= dataFrom &&
                (byIndex ?
                        index(data[i]) > truncationPoint :
                        (positionInTarget(seedPositions, data[i]) > truncationPoint));
             --i) {
            index = index(data[i]);
            offset = offset(data[i]);
            if (inDelta(prevOffset, offset, maxClusterIndels)) {
                // Processing exceptional cases for self-correlated K-Mers

                // If next record has same index and better offset
                // (closer to current island LAST_VALUE)
                if (i > dataFrom
                        && index == index(data[i - 1])
                        && abs(prevOffset - offset) > abs(prevOffset - offset(data[i - 1])))
                    // Skip current record
                    continue;

                assert prevIndex - index - 1 >= 0;
                int scoreDelta = matchScore + (prevIndex - index - 1) * mismatchScore +
                        abs(prevOffset - offset) * offsetShiftScore;

                if (scoreDelta > 0) {
                    score += scoreDelta;
                    prevOffset = offset;
                    prevIndex = index;
                    lastRecordId = i;
                }
            }
        }
        results.set(clusterPointer + FIRST_RECORD_ID, lastRecordId);
        results.set(clusterPointer + SCORE, score);
        if (score < absoluteMinClusterScore) {
            results.set(clusterPointer + FIRST_RECORD_ID, DROPPED_CLUSTER);
            return false;
        }
        return true;
    }

    private KMappingHit2 calculateHit(final int id, final int[] data,
                                      final int dataFrom, final int dataTo,
                                      final IntArrayList seedPositions) {
        OffsetPacksAccumulator accumulator = threadLocalAccumulator.get();
        accumulator.calculateInitialPartitioning(data, dataFrom, dataTo);

        IntArrayList results = accumulator.results;
        if (accumulator.results.size() == 0)
            return null;

        //A1: correcting intersections, step 1
        OUT:
        for (int i = 0; i < results.size(); i += OUTPUT_RECORD_SIZE)
            if (results.get(i + FIRST_RECORD_ID) != DROPPED_CLUSTER)
                for (int j = i + OUTPUT_RECORD_SIZE; j < results.size(); j += OUTPUT_RECORD_SIZE) {
                    if (results.get(i + FIRST_RECORD_ID) == DROPPED_CLUSTER)
                        continue OUT;
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
                            if (results.get(a + SCORE) < results.get(b + SCORE)) {
                                results.set(a + FIRST_RECORD_ID, DROPPED_CLUSTER);
                                continue;
                            } else {
                                results.set(b + FIRST_RECORD_ID, DROPPED_CLUSTER);
                                continue;
                            }
                        } else {
                            if (results.get(a + SCORE) < results.get(b + SCORE)) {
                                if (!truncateClusterFromRight(true, null, results, data, dataTo, a, bStartIndex))
                                    continue;
                            } else if (!truncateClusterFromLeft(true, null, results, data, dataFrom, b, aEndIndex))
                                continue;
                        }
                    }

                    //intersecting clusters in target
                    a = i;
                    b = j;
                    int aStart = positionInTarget(seedPositions, data[results.get(a + FIRST_RECORD_ID)]);
                    int aEnd = positionInTarget(seedPositions, data[results.get(a + LAST_RECORD_ID)]);
                    int bStart = positionInTarget(seedPositions, data[results.get(b + FIRST_RECORD_ID)]);
                    int bEnd = positionInTarget(seedPositions, data[results.get(b + LAST_RECORD_ID)]);

                    if (aStart > bStart) {
                        //swap by xor
                        aStart ^= bStart;
                        bStart ^= aStart;
                        aStart ^= bStart;
                        aEnd ^= bEnd;
                        bEnd ^= aEnd;
                        aEnd ^= bEnd;
                        a ^= b;
                        b ^= a;
                        a ^= b;
                    }


                    if (aEnd >= bStart) {
                        if (bEnd <= aEnd) {
                            if (results.get(a + SCORE) < results.get(b + SCORE))
                                results.set(a + FIRST_RECORD_ID, DROPPED_CLUSTER);
                            else
                                results.set(b + FIRST_RECORD_ID, DROPPED_CLUSTER);
                        } else {
                            if (results.get(a + SCORE) < results.get(b + SCORE))
                                truncateClusterFromRight(false, seedPositions, results, data, dataTo, a, bStart);
                            else
                                truncateClusterFromLeft(false, seedPositions, results, data, dataFrom, b, aEnd);
                        }
                    }
                }

        //A2: correcting intersections, step 2 untangling
        int bestScore = 0, currentScore;
        int numberOfClusters = results.size() / OUTPUT_RECORD_SIZE;

        final long[] forPreFiltering = new long[numberOfClusters];
        for (int i = 0; i < numberOfClusters; i++)
            forPreFiltering[i] = (i * OUTPUT_RECORD_SIZE) | (((long) (-results.get(i * OUTPUT_RECORD_SIZE + SCORE))) << 33);
        Arrays.sort(forPreFiltering);

        numberOfClusters = min(numberOfClusters, maxClusters);

        IntArrayList untangled = new IntArrayList(numberOfClusters),
                current = new IntArrayList(numberOfClusters);
        OUTER:
        for (long it = 0, size = (1L << numberOfClusters); it < size; ++it) {
            current.clear();
            currentScore = 0;
            for (int ai = numberOfClusters - 1; ai >= 0; --ai) {
                //int a = ai * OUTPUT_RECORD_SIZE;
                int a = (int) forPreFiltering[ai];
                if (((it >> ai) & 1) == 1) {
                    if (results.get(a + FIRST_RECORD_ID) == DROPPED_CLUSTER) {
                        it += ((1 << ai) - 1);
                        continue OUTER;
                    }

                    for (int i = 0; i < current.size(); i++) {
                        int b = current.get(i);
                        if (results.get(b + FIRST_RECORD_ID) == DROPPED_CLUSTER)
                            continue;
                        if (crosses(seedPositions, data, results.get(a + FIRST_RECORD_ID), results.get(b + FIRST_RECORD_ID))) {
                            assert crosses(seedPositions, data, results.get(a + LAST_RECORD_ID), results.get(b + LAST_RECORD_ID));
                            //it += ((1 << ai) - 1);
                            continue OUTER;
                        }
                    }
                    current.add(a);
                    currentScore += results.get(a + SCORE);
                }
            }
            if (bestScore < currentScore) {
                untangled.copyFrom(current);
                bestScore = currentScore;
            }
        }

        current.clear();//economy
        IntArrayList seedRecords = current;
        IntArrayList packBoundaries = new IntArrayList();
        int score = 0;

        final long[] untangledForSort = new long[untangled.size()];
        for (int i = 0; i < untangled.size(); ++i) {
            int pointer = untangled.get(i);
            untangledForSort[i] = pointer | (((long) index(data[results.get(pointer + FIRST_RECORD_ID)])) << 32);
        }
        Arrays.sort(untangledForSort);

        for (int i = 0; i < untangledForSort.length; ++i) {
            int pointer = (int) (untangledForSort[i]);
            if (i != 0) {
                packBoundaries.add(seedRecords.size());
                score += extraClusterScore;
            }

            int recordId = results.get(pointer + FIRST_RECORD_ID);
            assert recordId >= dataFrom;

            int lastRecordId = results.get(pointer + LAST_RECORD_ID);
            assert lastRecordId < dataTo;

            int record, index, offset, delta;
            int clusterScore = matchScore;

            int previousIndex = index = index(record = data[recordId]);
            seedRecords.add(record);

            int previousOffset = offset(record);

            while (++recordId < dataTo && index(data[recordId]) == index) ;

            --recordId;
            while (++recordId <= lastRecordId) {
                record = data[recordId];
                index = index(record);
                offset = offset(record);

                int minRecord = record;
                int minDelta = abs(offset - previousOffset);

                if (minDelta > maxClusterIndels)
                    continue;

                boolean $ = false;
                while (recordId < lastRecordId && index(record = data[recordId + 1]) == index) {
                    ++recordId;
                    if ((delta = abs(offset(record) - previousOffset)) < minDelta) {
                        minDelta = delta;
                        minRecord = record;
                        $ = true;
                    }
                }
                if ($) offset = offset(minRecord);

                if (positionInTarget(seedPositions, minRecord) <= positionInTarget(seedPositions, seedRecords.last())) {
                    int minRecordId = recordId + 1;
                    while (data[--minRecordId] != minRecord) ;
                    System.arraycopy(data, minRecordId + 1, data, minRecordId, dataTo - minRecordId - 1);
                    return calculateHit(id, data, dataFrom, dataTo - 1, seedPositions);
                }

                int scoreDelta = matchScore + (index - previousIndex - 1) * mismatchScore +
                        minDelta * offsetShiftScore;
                if (scoreDelta > 0) {
                    clusterScore += scoreDelta;
                    seedRecords.add(minRecord);
                    previousIndex = index;
                    previousOffset = offset;
                }
            }

            // TODO point for counter : clusterScore == results.get(pointer + SCORE)
            clusterScore = max(clusterScore, results.get(pointer + SCORE));
            score += clusterScore;
        }

        return new KMappingHit2(id, seedRecords.toArray(), packBoundaries.toArray(), score);
    }

    private static boolean crosses(final IntArrayList seedPositions, final int[] data, final int a, final int b) {
        return (index(data[a]) < index(data[b])) ^
                (positionInTarget(seedPositions, data[a]) < positionInTarget(seedPositions, data[b]));
    }

    public static int positionInTarget(final IntArrayList seedPositions,
                                       final int record) {
        return seedPositions.get(index(record)) + offset(record);
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

    static int index(final int record) {
        return record & indexMask;
    }

    static int offset(final int record) {
        return record >> bitsForIndex;
    }

    static int record(final int offset, final int index) {
        return (offset << bitsForIndex) | index;
    }

    static boolean inDelta(final int a, final int b, final int maxAllowedDelta) {
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

    static final class ArrList<T> extends ArrayList<T> {
        public ArrList() {
        }

        @Override
        public void removeRange(int fromIndex, int toIndex) {
            super.removeRange(fromIndex, toIndex);
        }
    }
}

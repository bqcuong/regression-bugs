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
import java.util.Comparator;

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
    private final int bitsForOffset;
    /**
     * Mask to extract offset value (= 0xFFFFFFFF >>> (32 - bitsForOffset))
     */
    private final int offsetMask;

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
    mapperExtraClusterScore,
    /**
     * Minimal score in fractions of top score.
     */
    relativeMinScore,
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
    offsetShiftScore;

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
    private final float terminationThreshold = 6.6e6f;

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
                    int absoluteMinClusterScore, int mapperExtraClusterScore,
                    int relativeMinScore,
                    int matchScore, int mismatchScore, int offsetShiftScore,
                    boolean floatingLeftBound, boolean floatingRightBound) {
        this.kValue = kValue;

        //Bits
        this.bitsForOffset = 18;
        this.offsetMask = 0xFFFFFFFF >>> (32 - bitsForOffset);

        //Initialize base
        int maxNumberOfKmers = 1 << (kValue * 2);
        // TODO lazy
        base = new int[maxNumberOfKmers][10];
        lengths = new int[maxNumberOfKmers];

        //Parameters
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.absoluteMinClusterScore = absoluteMinClusterScore;
        this.mapperExtraClusterScore = mapperExtraClusterScore;
        this.relativeMinScore = relativeMinScore;
        this.matchScore = matchScore;
        this.mismatchScore = mismatchScore;
        this.offsetShiftScore = offsetShiftScore;
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
                parameters.getMapperOffsetShiftScore(),
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

        base[kmer][lengths[kmer]++] = (id << bitsForOffset) | (offset);
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
        int id, offset;
        for (int i = 0; i < seedPositions.size(); ++i) {
            kmer = 0;
            for (int j = seedPositions.get(i); j < seedPositions.get(i) + kValue; ++j)
                kmer = kmer << 2 | sequence.codeAt(j);

            if (base[kmer].length == 0)
                continue;

            for (int record : base[kmer]) {
                id = record >>> bitsForOffset;
                offset = record & offsetMask;

                if (candidates[id] == null) {
                    candidates[id] = new IntArrayList();
                    candidates[id].add(id);
                }

                candidates[id].add((seedPositions.get(i) - offset) << (32 - bitsForOffset) | i);
            }
        }

        Arrays.sort(candidates, new Comparator<IntArrayList>() {
            @Override
            public int compare(IntArrayList o1, IntArrayList o2) {
                return Integer.compare(o2 == null ? 0 : o2.size(),
                        o1 == null ? 0 : o1.size());
            }
        });

        final int possibleMinKmers = (int) Math.ceil(absoluteMinScore / matchScore);
        IntArrayList seedOffsets = new IntArrayList(),
                boundaries = new IntArrayList();
        float score;
        int alRegionStart, alRegionEnd;
        for (int i = 0; i < candidates.length; i++) {
            if (candidates[i] == null)
                break;
            if (candidates[i].size() - 1 < possibleMinKmers)
                break;

            score = 0;
            for (int k = 1; k < candidates[i].size(); k++) {
                alRegionStart = k;
                offset = candidates[i].get(k) >> (32 - bitsForOffset);
            }

            result.add(new KMappingHit2(seedOffsets.toArray(), boundaries.toArray(),
                    0, candidates[i].get(0), score, 0, 0));
            seedOffsets.clear();
            boundaries.clear();
        }

/*

   Match     = 10
   Mismatch  = -10
   Shift     = -9

      FirstIndex  0    0
      LastIndex   0    1
      MinValue    100  100
      MaxValue    100  100
(key) LastValue   100  100
      Score       10   20

      FirstIndex  2
      LastIndex   2
      MinValue    50
      MaxValue    50
(key) LastValue   50
      Score       10

      FirstIndex  3
      LastIndex   3
      MinValue    70
      MaxValue    70
(key) LastValue   70
      Score       10

*/
//        //Selecting best candidates (vote)
//        //int resultId = 0;
//        Info info = new Info();
//        int cFrom, cTo, siFrom, siTo;
//        int maxRawScore = 0, j, i;
//        double preScore;
//        double maxScore = Float.MIN_VALUE;
//        for (i = candidates.length - 1; i >= 0; --i) {
//            //TODO reconsider conditions:
//            if (candidates[i] != null &&
//                    candidates[i].size() >= ((minAlignmentLength - kValue + 1) / maxDistance) &&
//                    candidates[i].size() * matchScore >= maxScore * relativeMinScore) {
//
//                //Sorting (important)
//                candidates[i].sort();
//                //Calculating best score and offset values
//                getScoreFromOffsets(candidates[i], info);
//
//                //Theoretical range of target and reference sequence intersection
//                cFrom = max(info.offset, from);
//                cTo = min(info.offset + refLength[i], to) - kValue;
//
//                //Calculating number of seeds in this range
//                siTo = siFrom = -1;
//                for (j = seedPositions.size() - 1; j >= 0; --j)
//                    if ((seedPosition = seedPositions.get(j)) <= cTo) {
//                        if (siTo == -1)
//                            siTo = j + 1;
//                        if (seedPosition < cFrom) {
//                            siFrom = j + 1;
//                            break;
//                        }
//                    }
//
//                //If siFrom not set, first seed is inside the range of target and
//                //reference sequence intersection
//                if (siFrom == -1)
//                    siFrom = 0;
//
//                //Calculating score without penalty
//                preScore = matchScore * info.score; //+ max(siTo - siFrom - info.score, 0) * mismatchScore;
//
//                //Selecting candidates
//                if (preScore >= absoluteMinScore) {
//                    result.add(new KMappingHit2(info.offset, i, (float) preScore, siFrom, siTo));
//
//                    if (maxRawScore < info.score)
//                        maxRawScore = info.score;
//
//                    if (maxScore < preScore)
//                        maxScore = preScore;
//                }
//            }
//        }
//
//        int c, seedIndex, seedIndexMask = (0xFFFFFFFF >>> (bitsForOffset)),
//                offsetDelta, currentSeedPosition, prev;
//        float score;
//
//        KMappingHit2 hit;
//        maxScore = 0.0;
//        for (j = result.size() - 1; j >= 0; --j) {
//            hit = result.get(j);
//
//            //Fulfilling the seed positions array
//            //getting seed positions in intersection of target and reference sequences
//            hit.seedOffsets = new int[hit.to - hit.from];
//            Arrays.fill(hit.seedOffsets, SEED_NOT_FOUND_OFFSET);
//            for (int k = candidates[hit.id].size() - 1; k >= 0; --k) {
//                //  offset value | seed index
//                c = candidates[hit.id].get(k);
//                seedIndex = c & seedIndexMask;
//
//                //filling seed position array with actual positions of seeds inside intersection
//                if (seedIndex >= result.get(j).from && seedIndex < result.get(j).to) {
//                    seedIndex -= hit.from;
//                    offsetDelta = abs((c >> (32 - bitsForOffset)) - hit.offset);
//
//                    //check if offset difference is less than max allowed and better seed position is found
//                    if (offsetDelta <= maxIndels &&
//                            (hit.seedOffsets[seedIndex] == SEED_NOT_FOUND_OFFSET ||
//                                    abs(hit.seedOffsets[seedIndex] - hit.offset) > offsetDelta))
//                        hit.seedOffsets[seedIndex] = (c >> (32 - bitsForOffset));
//                }
//            }
//
//            //Previous seed position
//            prev = -1;
//            c = -1;
//            for (i = hit.seedOffsets.length - 1; i >= 0; --i)
//                if (hit.seedOffsets[i] != SEED_NOT_FOUND_OFFSET) {
//                    if (c != -1)
//                        //check for situation like: seedsOffset = [25,25,25,25,  28  ,25]
//                        //we have to remove such offsets because it's most likely wrong mapping
//                        //c - most left index, prev - middle index and i - right most index
//                        //but we iterate in reverse direction
//                        if (hit.seedOffsets[c] != hit.seedOffsets[prev] && hit.seedOffsets[prev] != hit.seedOffsets[i] &&
//                                ((hit.seedOffsets[c] < hit.seedOffsets[prev])
//                                        != (hit.seedOffsets[prev] < hit.seedOffsets[i]))) {
//                            hit.seedOffsets[prev] = SEED_NOT_FOUND_OFFSET;
//                            prev = -1;
//                        }
//                    c = prev;
//                    prev = i;
//                }
//
//
//            //Calculating score
//            score = 0.0f;
//            for (int off : hit.seedOffsets)
//                if (off != SEED_NOT_FOUND_OFFSET)
//                    score += matchScore;
//                else
//                    score += mismatchPenalty;
//
//            //Floating bounds reward
//            if (floatingLeftBound) {
//                prev = -1;
//                for (i = 0; i < hit.seedOffsets.length; ++i)
//                    if (hit.seedOffsets[i] != SEED_NOT_FOUND_OFFSET) {
//                        if (prev == -1) {
//                            prev = i;
//                            continue;
//                        }
//
//                        //Calculating score gain for deleting first kMer
//                        if (matchScore + abs(hit.seedOffsets[i] - hit.seedOffsets[prev]) * offsetShiftPenalty + (i - prev - 1) * mismatchPenalty <= 0.0f) {
//                            //Bad kMer
//                            hit.seedOffsets[prev] = SEED_NOT_FOUND_OFFSET;
//                            prev = i;
//                            continue;
//                        }
//
//                        score -= prev * mismatchPenalty;
//                        break;
//                    }
//            }
//
//            //Floating bounds reward
//            if (floatingRightBound) {
//                prev = -1;
//                for (i = hit.seedOffsets.length - 1; i >= 0; --i)
//                    if (hit.seedOffsets[i] != SEED_NOT_FOUND_OFFSET) {
//                        if (prev == -1) {
//                            prev = i;
//                            continue;
//                        }
//
//                        //Calculating score gain for deleting first kMer
//                        if (matchScore + abs(hit.seedOffsets[i] - hit.seedOffsets[prev]) * offsetShiftPenalty + (prev - 1 - i) * mismatchPenalty <= 0.0f) {
//                            //Bad kMer
//                            hit.seedOffsets[prev] = SEED_NOT_FOUND_OFFSET;
//                            prev = i;
//                            continue;
//                        }
//
//                        score -= (hit.seedOffsets.length - 1 - prev) * mismatchPenalty;
//                    }
//            }
//
//            c = -1;
//            prev = -1;
//            //totalIndels = 0;
//            for (i = hit.seedOffsets.length - 1; i >= 0; --i) {
//                if (hit.seedOffsets[i] != SEED_NOT_FOUND_OFFSET) {
//                    currentSeedPosition = seedPositions.get(i + hit.from) - hit.seedOffsets[i];
//                    if (c == -1) {
//                        c = currentSeedPosition;
//                        prev = i;
//                    } else if (c <= currentSeedPosition)
//                        //Removing paradoxical kMer offsets
//                        hit.seedOffsets[i] = SEED_NOT_FOUND_OFFSET;
//                    else {
//                        //totalIndels += abs(hit.seedOffsets[i] - hit.seedOffsets[prev]);
//                        score += abs(hit.seedOffsets[i] - hit.seedOffsets[prev]) * offsetShiftPenalty;
//                        c = currentSeedPosition;
//                        prev = i;
//                    }
//                }
//            }
//
//            hit.score = score;
//
//            if (score < absoluteMinScore)
//                result.remove(j);
//
//            if (maxScore < score)
//                maxScore = score;
//
//            //if (totalIndels > maxIndels * 2) {
//            //    result.remove(j);
//            //}
//        }
//
//        //Removing candidates with score < maxScore * hitsRange
//        maxScore *= relativeMinScore;
//        for (j = result.size() - 1; j >= 0; --j) {
//            hit = result.get(j);
//            if (hit.score < maxScore)
//                result.remove(j);
//        }
//
//        //Removing seed conflicts
//
//        return new KMappingResult2(seedPositions, result);
        return null;
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
     * Returns minimal score
     *
     * @return minimal score
     */
    public float getAbsoluteMinScore() {
        return absoluteMinScore;
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

package com.milaboratory.core.alignment;

import com.milaboratory.core.mutations.MutationsBuilder;
import com.milaboratory.core.sequence.NucleotideSequence;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public class BandedAffineAligner {
    private BandedAffineAligner() {
    }

    /**
     * Classical Banded Alignment with affine gap scoring.
     *
     * Both sequences must be highly similar.
     *
     * Align 2 sequence completely (i.e. while first sequence will be aligned against whole second sequence).
     *
     * @param scoring   scoring system
     * @param seq1      first sequence
     * @param seq2      second sequence
     * @param offset1   offset in first sequence
     * @param length1   length of first sequence's part to be aligned
     * @param offset2   offset in second sequence
     * @param length2   length of second sequence's part to be aligned
     * @param width     width of banded alignment matrix. In other terms max allowed number of indels
     * @param mutations mutations array where all mutations will be kept
     * @param cache     matrix cache
     */
    public static int align0(AffineGapAlignmentScoring<NucleotideSequence> scoring,
                             NucleotideSequence seq1, NucleotideSequence seq2,
                             int offset1, int length1, int offset2, int length2,
                             int width, MutationsBuilder<NucleotideSequence> mutations,
                             MatrixCache cache) {
        int size1 = length1 + 1,
                size2 = length2 + 1;

        cache.prepareMatrices(size1, size2, width, scoring);

        BandedMatrix main = cache.main;
        BandedMatrix gapIn1 = cache.gapIn1;
        BandedMatrix gapIn2 = cache.gapIn2;

        int i, j;

        int match, gap1, gap2, to;

        for (i = 0; i < length1; ++i) {
            to = Math.min(i + main.getRowFactor() - main.getColumnDelta() + 1, length2);
            for (j = Math.max(0, i - main.getColumnDelta()); j < to; ++j) {
                match = main.get(i, j) +
                        scoring.getScore(seq1.codeAt(offset1 + i), seq2.codeAt(offset2 + j));

                gap1 = Math.max(main.get(i + 1, j) + scoring.getGapOpenPenalty(), gapIn1.get(i + 1, j) + scoring.getGapExtensionPenalty());
                gap2 = Math.max(main.get(i, j + 1) + scoring.getGapOpenPenalty(), gapIn2.get(i, j + 1) + scoring.getGapExtensionPenalty());

                gapIn1.set(i + 1, j + 1, gap1);
                gapIn2.set(i + 1, j + 1, gap2);
                main.set(i + 1, j + 1, Math.max(match, Math.max(gap1, gap2)));
            }
        }

        to = mutations.size();
        i = length1 - 1;
        j = length2 - 1;
        byte c1, c2;
        while (i >= 0 || j >= 0) {
            if (i >= 0 && j >= 0 &&
                    main.get(i + 1, j + 1) == main.get(i, j) +
                            scoring.getScore(c1 = seq1.codeAt(offset1 + i),
                                    c2 = seq2.codeAt(offset2 + j))) {
                if (c1 != c2)
                    mutations.appendSubstitution(offset1 + i, c1, c2);
                --i;
                --j;
            } else if (i >= 0 &&
                    main.get(i + 1, j + 1) ==
                            gapIn2.get(i + 1, j + 1)) {
                mutations.appendDeletion(offset1 + i, seq1.codeAt(offset1 + i));
                --i;
            } else if (j >= 0 &&
                    main.get(i + 1, j + 1) ==
                            gapIn1.get(i + 1, j + 1)) {
                mutations.appendInsertion(offset1 + i + 1, seq2.codeAt(offset2 + j));
                --j;
            } else
                throw new RuntimeException();
        }

        mutations.reverseRange(to, mutations.size());
        return main.get(length1, length2);
    }

    public static BandedSemiLocalResult semiLocalRight(AffineGapAlignmentScoring<NucleotideSequence> scoring,
                                                       NucleotideSequence seq1, NucleotideSequence seq2,
                                                       int offset1, int length1, int offset2, int length2,
                                                       int width, MutationsBuilder<NucleotideSequence> mutations,
                                                       MatrixCache cache) {
        int minLength = Math.min(length1, length2) + width;
        length1 = Math.min(length1, minLength);
        length2 = Math.min(length2, minLength);

        int size1 = length1 + 1,
                size2 = length2 + 1;

        cache.prepareMatrices(size1, size2, width, scoring);

        BandedMatrix main = cache.main;
        BandedMatrix gapIn1 = cache.gapIn1;
        BandedMatrix gapIn2 = cache.gapIn2;

        int i, j;

        int match, gap1, gap2, to;

        int maxI = 0, maxJ = 0, maxScore = Integer.MIN_VALUE;

        for (i = 0; i < length1; ++i) {
            to = Math.min(i + main.getRowFactor() - main.getColumnDelta() + 1, length2);
            for (j = Math.max(0, i - main.getColumnDelta()); j < to; ++j) {
                match = main.get(i, j) +
                        scoring.getScore(seq1.codeAt(offset1 + i), seq2.codeAt(offset2 + j));

                gap1 = Math.max(main.get(i + 1, j) + scoring.getGapOpenPenalty(), gapIn1.get(i + 1, j) + scoring.getGapExtensionPenalty());
                gap2 = Math.max(main.get(i, j + 1) + scoring.getGapOpenPenalty(), gapIn2.get(i, j + 1) + scoring.getGapExtensionPenalty());

                gapIn1.set(i + 1, j + 1, gap1);
                gapIn2.set(i + 1, j + 1, gap2);
                int score = Math.max(match, Math.max(gap1, gap2));
                main.set(i + 1, j + 1, score);

                if (score > maxScore) {
                    maxScore = score;
                    maxI = i;
                    maxJ = j;
                }
            }
        }

        to = mutations.size();
        i = maxI;
        j = maxJ;
        byte c1, c2;
        while (i >= 0 || j >= 0) {
            if (i >= 0 && j >= 0 &&
                    main.get(i + 1, j + 1) == main.get(i, j) +
                            scoring.getScore(c1 = seq1.codeAt(offset1 + i),
                                    c2 = seq2.codeAt(offset2 + j))) {
                if (c1 != c2)
                    mutations.appendSubstitution(offset1 + i, c1, c2);
                --i;
                --j;
            } else if (i >= 0 &&
                    main.get(i + 1, j + 1) ==
                            gapIn2.get(i + 1, j + 1)) {
                mutations.appendDeletion(offset1 + i, seq1.codeAt(offset1 + i));
                --i;
            } else if (j >= 0 &&
                    main.get(i + 1, j + 1) ==
                            gapIn1.get(i + 1, j + 1)) {
                mutations.appendInsertion(offset1 + i + 1, seq2.codeAt(offset2 + j));
                --j;
            } else
                throw new RuntimeException();
        }

        mutations.reverseRange(to, mutations.size());

        return new BandedSemiLocalResult(offset1 + maxI, offset2 + maxJ, maxScore);
    }

    public static BandedSemiLocalResult semiLocalLeft(AffineGapAlignmentScoring<NucleotideSequence> scoring,
                                                      NucleotideSequence seq1, NucleotideSequence seq2,
                                                      int offset1, int length1, int offset2, int length2,
                                                      int width, MutationsBuilder<NucleotideSequence> mutations,
                                                      MatrixCache cache) {
        offset1 += length1;
        offset2 += length2;

        int minLength = Math.min(length1, length2) + width;
        length1 = Math.min(length1, minLength);
        length2 = Math.min(length2, minLength);

        offset1 -= length1;
        offset2 -= length2;

        int size1 = length1 + 1,
                size2 = length2 + 1;

        cache.prepareMatrices(size1, size2, width, scoring);

        BandedMatrix main = cache.main;
        BandedMatrix gapIn1 = cache.gapIn1;
        BandedMatrix gapIn2 = cache.gapIn2;

        int i, j;

        int match, gap1, gap2, to;

        int maxI = 0, maxJ = 0, maxScore = Integer.MIN_VALUE;

        for (i = 0; i < length1; ++i) {
            to = Math.min(i + main.getRowFactor() - main.getColumnDelta() + 1, length2);
            for (j = Math.max(0, i - main.getColumnDelta()); j < to; ++j) {
                match = main.get(i, j) +
                        scoring.getScore(seq1.codeAt(offset1 + length1 - 1 - i), seq2.codeAt(offset2 + length2 - 1 - j));

                gap1 = Math.max(main.get(i + 1, j) + scoring.getGapOpenPenalty(), gapIn1.get(i + 1, j) + scoring.getGapExtensionPenalty());
                gap2 = Math.max(main.get(i, j + 1) + scoring.getGapOpenPenalty(), gapIn2.get(i, j + 1) + scoring.getGapExtensionPenalty());

                gapIn1.set(i + 1, j + 1, gap1);
                gapIn2.set(i + 1, j + 1, gap2);
                int score = Math.max(match, Math.max(gap1, gap2));
                main.set(i + 1, j + 1, score);

                if (score > maxScore) {
                    maxScore = score;
                    maxI = i;
                    maxJ = j;
                }
            }
        }

        i = maxI;
        j = maxJ;
        byte c1, c2;
        while (i >= 0 || j >= 0) {
            if (i >= 0 && j >= 0 &&
                    main.get(i + 1, j + 1) == main.get(i, j) +
                            scoring.getScore(c1 = seq1.codeAt(offset1 + length1 - 1 - i),
                                    c2 = seq2.codeAt(offset2 + length2 - 1 - j))) {
                if (c1 != c2)
                    mutations.appendSubstitution(offset1 + length1 - 1 - i, c1, c2);
                --i;
                --j;
            } else if (i >= 0 &&
                    main.get(i + 1, j + 1) ==
                            gapIn2.get(i + 1, j + 1)) {
                mutations.appendDeletion(offset1 + length1 - 1 - i, seq1.codeAt(offset1 + length1 - 1 - i));
                --i;
            } else if (j >= 0 &&
                    main.get(i + 1, j + 1) ==
                            gapIn1.get(i + 1, j + 1)) {
                mutations.appendInsertion(offset1 + length1 - i, seq2.codeAt(offset2 + length2 - 1 - j));
                --j;
            } else
                throw new RuntimeException();
        }

        return new BandedSemiLocalResult(offset1 + length1 - 1 - maxI, offset2 + length2 - 1 - maxJ, maxScore);
    }

    public static BandedSemiLocalResult semiGlobalRight(AffineGapAlignmentScoring<NucleotideSequence> scoring,
                                                        NucleotideSequence seq1, NucleotideSequence seq2,
                                                        int offset1, int length1, int addedNucleotides1,
                                                        int offset2, int length2, int addedNucleotides2,
                                                        int width, MutationsBuilder<NucleotideSequence> mutations,
                                                        MatrixCache cache) {
        int minLength = Math.min(length1, length2) + width;
        length1 = Math.min(length1, minLength);
        length2 = Math.min(length2, minLength);

        int size1 = length1 + 1,
                size2 = length2 + 1;

        cache.prepareMatrices(size1, size2, width, scoring);

        BandedMatrix main = cache.main;
        BandedMatrix gapIn1 = cache.gapIn1;
        BandedMatrix gapIn2 = cache.gapIn2;

        int i, j;

        int match, gap1, gap2, to;

        for (i = 0; i < length1; ++i) {
            to = Math.min(i + main.getRowFactor() - main.getColumnDelta() + 1, length2);
            for (j = Math.max(0, i - main.getColumnDelta()); j < to; ++j) {
                match = main.get(i, j) +
                        scoring.getScore(seq1.codeAt(offset1 + i), seq2.codeAt(offset2 + j));

                gap1 = Math.max(main.get(i + 1, j) + scoring.getGapOpenPenalty(), gapIn1.get(i + 1, j) + scoring.getGapExtensionPenalty());
                gap2 = Math.max(main.get(i, j + 1) + scoring.getGapOpenPenalty(), gapIn2.get(i, j + 1) + scoring.getGapExtensionPenalty());

                gapIn1.set(i + 1, j + 1, gap1);
                gapIn2.set(i + 1, j + 1, gap2);
                main.set(i + 1, j + 1, Math.max(match, Math.max(gap1, gap2)));
            }
        }

        int maxI = 0, maxJ = 0, maxScore = Integer.MIN_VALUE;

        j = length2;
        for (i = length1 - addedNucleotides1; i < size1; ++i)
            if (maxScore < main.get(i, j)) {
                maxScore = main.get(i, j);
                maxI = i - 1;
                maxJ = j - 1;
            }

        i = length1;
        for (j = length2 - addedNucleotides2; j < size2; ++j)
            if (maxScore < main.get(i, j)) {
                maxScore = main.get(i, j);
                maxI = i - 1;
                maxJ = j - 1;
            }

        to = mutations.size();
        i = maxI;
        j = maxJ;
        byte c1, c2;
        while (i >= 0 || j >= 0) {
            if (i >= 0 && j >= 0 &&
                    main.get(i + 1, j + 1) == main.get(i, j) +
                            scoring.getScore(c1 = seq1.codeAt(offset1 + i),
                                    c2 = seq2.codeAt(offset2 + j))) {
                if (c1 != c2)
                    mutations.appendSubstitution(offset1 + i, c1, c2);
                --i;
                --j;
            } else if (i >= 0 &&
                    main.get(i + 1, j + 1) ==
                            gapIn2.get(i + 1, j + 1)) {
                mutations.appendDeletion(offset1 + i, seq1.codeAt(offset1 + i));
                --i;
            } else if (j >= 0 &&
                    main.get(i + 1, j + 1) ==
                            gapIn1.get(i + 1, j + 1)) {
                mutations.appendInsertion(offset1 + i + 1, seq2.codeAt(offset2 + j));
                --j;
            } else
                throw new RuntimeException();
        }

        mutations.reverseRange(to, mutations.size());

        return new BandedSemiLocalResult(offset1 + maxI, offset2 + maxJ, maxScore);
    }

    public static final class MatrixCache {
        private final CachedIntArray mainCache, gapIn1Cache, gapIn2Cache;
        private BandedMatrix main, gapIn1, gapIn2;

        public MatrixCache() {
            this.mainCache = new CachedIntArray();
            this.gapIn1Cache = new CachedIntArray();
            this.gapIn2Cache = new CachedIntArray();
        }

        private void prepareMatrices(int size1, int size2, int width,
                                     AffineGapAlignmentScoring<NucleotideSequence> scoring) {
            BandedMatrix main = this.main = new BandedMatrix(mainCache, size1, size2, width);
            BandedMatrix gapIn1 = this.gapIn1 = new BandedMatrix(gapIn1Cache, size1, size2, width);
            BandedMatrix gapIn2 = this.gapIn2 = new BandedMatrix(gapIn2Cache, size1, size2, width);

            for (int i = main.getRowFactor() - main.getColumnDelta(); i > 0; --i) {
                int v = scoring.getGapOpenPenalty() + scoring.getGapExtensionPenalty() * (i - 1);
                main.set(0, i, v);
                gapIn1.set(0, i, v);
                gapIn2.set(0, i, BandedMatrix.DEFAULT_VALUE);
            }

            for (int i = main.getColumnDelta(); i > 0; --i) {
                int v = scoring.getGapOpenPenalty() + scoring.getGapExtensionPenalty() * (i - 1);
                main.set(i, 0, v);
                gapIn1.set(i, 0, BandedMatrix.DEFAULT_VALUE);
                gapIn2.set(i, 0, v);
            }

            main.set(0, 0, 0);
            gapIn1.set(0, 0, BandedMatrix.DEFAULT_VALUE);
            gapIn2.set(0, 0, BandedMatrix.DEFAULT_VALUE);
        }
    }
}

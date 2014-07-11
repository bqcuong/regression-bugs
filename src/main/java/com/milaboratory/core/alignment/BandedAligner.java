package com.milaboratory.core.alignment;

import com.milaboratory.core.sequence.NucleotideSequence;
import com.milaboratory.util.IntArrayList;

import static com.milaboratory.core.mutations.Mutation.*;

/**
 * BandedAligner - class, static methods of which perform banded alignments, i.e. alignments where sequences to be
 * aligned are highly similar and number of mutations is very low.
 *
 * <p>It reduces the time complexity of alignment to roughly linear time by being semi-greedy, as it disallows the
 * possibility of a high scoring alignment that wanders significantly off the main diagonal (which means that it works
 * only for highly similar sequences)</p>
 */
public class BandedAligner {

    /**
     * Classical Banded Alignment
     *
     * <p>Both sequences must be highly similar</p> <p>Align 2 sequence completely (i.e. while first sequence will be
     * aligned against whole second sequence)</p>
     *
     * @param scoring scoring system
     * @param seq1    first sequence
     * @param seq2    second sequence
     * @param width   width of matrix. In other terms max allowed number of indels
     * @return array of mutations
     */
    public static int[] align(LinearGapAlignmentScoring scoring, NucleotideSequence seq1, NucleotideSequence seq2, int width) {
        return align(scoring, seq1, seq2, 0, seq1.size(), 0, seq2.size(), width);
    }

    /**
     * Classical Banded Alignment
     *
     * <p>Both sequences must be highly similar</p> <p>Align 2 sequence completely (i.e. while first sequence will be
     * aligned against whole second sequence)</p>
     *
     * @param scoring scoring system
     * @param seq1    first sequence
     * @param seq2    second sequence
     * @param offset1 offset in first sequence
     * @param length1 length of first sequence's part to be aligned
     * @param offset2 offset in second sequence
     * @param length2 length of second sequence's part to be aligned
     * @param width   width of banded alignment matrix. In other terms max allowed number of indels
     * @return
     */
    public static int[] align(LinearGapAlignmentScoring scoring, NucleotideSequence seq1, NucleotideSequence seq2,
                              int offset1, int length1, int offset2, int length2,
                              int width) {
        IntArrayList mutations = new IntArrayList();
        try {
            align(scoring, seq1, seq2, offset1, length1, offset2, length2, width, mutations, AlignmentCache.get());
        } finally {
            AlignmentCache.release();
        }
        return mutations.toArray();
    }

    /**
     * Classical Banded Alignment
     *
     * <p>Both sequences must be highly similar</p> <p>Align 2 sequence completely (i.e. while first sequence will be
     * aligned against whole second sequence)</p>
     *
     * @param scoring     scoring system
     * @param seq1        first sequence
     * @param seq2        second sequence
     * @param offset1     offset in first sequence
     * @param length1     length of first sequence's part to be aligned
     * @param offset2     offset in second sequence
     * @param length2     length of second sequence's part to be aligned
     * @param width       width of banded alignment matrix. In other terms max allowed number of indels
     * @param mutations   mutations array where all mutations will be kept
     * @param cachedArray cached (created once) array to be used in {@link BandedMatrix}, which is compact alignment
     *                    scoring matrix
     */
    public static void align(LinearGapAlignmentScoring scoring, NucleotideSequence seq1, NucleotideSequence seq2,
                             int offset1, int length1, int offset2, int length2,
                             int width, IntArrayList mutations, CachedIntArray cachedArray) {

        int size1 = length1 + 1,
                size2 = length2 + 1;

        BandedMatrix matrix = new BandedMatrix(cachedArray, size1, size2, width);

        int i, j;

        for (i = matrix.getRowFactor() - matrix.getColumnDelta(); i > 0; --i)
            matrix.set(0, i, scoring.getGapPenalty() * i);

        for (i = matrix.getColumnDelta(); i > 0; --i)
            matrix.set(i, 0, scoring.getGapPenalty() * i);

        matrix.set(0, 0, 0);

        int match, delete, insert, to;

        for (i = 0; i < length1; ++i) {
            to = Math.min(i + matrix.getRowFactor() - matrix.getColumnDelta() + 1, length2);
            for (j = Math.max(0, i - matrix.getColumnDelta()); j < to; ++j) {
                match = matrix.get(i, j) +
                        scoring.getScore(seq1.codeAt(offset1 + i), seq2.codeAt(offset2 + j));
                delete = matrix.get(i, j + 1) + scoring.getGapPenalty();
                insert = matrix.get(i + 1, j) + scoring.getGapPenalty();
                matrix.set(i + 1, j + 1, Math.max(match, Math.max(delete, insert)));
            }
        }

        to = mutations.size();
        i = length1 - 1;
        j = length2 - 1;
        byte c1, c2;
        while (i >= 0 || j >= 0) {
            if (i >= 0 && j >= 0 &&
                    matrix.get(i + 1, j + 1) == matrix.get(i, j) +
                            scoring.getScore(c1 = seq1.codeAt(offset1 + i),
                                    c2 = seq2.codeAt(offset2 + j))) {
                if (c1 != c2)
                    mutations.add(createSubstitution(offset1 + i, c1, c2));
                --i;
                --j;
            } else if (i >= 0 &&
                    matrix.get(i + 1, j + 1) ==
                            matrix.get(i, j + 1) + scoring.getGapPenalty()) {
                mutations.add(createDeletion(offset1 + i, seq1.codeAt(offset1 + i)));
                --i;
            } else if (j >= 0 &&
                    matrix.get(i + 1, j + 1) ==
                            matrix.get(i + 1, j) + scoring.getGapPenalty()) {
                mutations.add(createInsertion(offset1 + i + 1, seq2.codeAt(offset2 + j)));
                --j;
            } else
                throw new RuntimeException();
        }

        mutations.reverse(to, mutations.size());
    }

    /**
     * Semi-semi-global alignment with artificially added letters.
     *
     * <p>Alignment where second sequence is aligned to the right part of first sequence.</p> <p>Whole second sequence
     * must be highly similar to the first sequence</p>
     *
     * @param scoring           scoring system
     * @param seq1              first sequence
     * @param seq2              second sequence
     * @param offset1           offset in first sequence
     * @param length1           length of first sequence's part to be aligned including artificially added letters
     * @param addedNucleotides1 number of artificially added letters to the first sequence
     * @param offset2           offset in second sequence
     * @param length2           length of second sequence's part to be aligned including artificially added letters
     * @param addedNucleotides2 number of artificially added letters to the second sequence
     * @param width             width of banded alignment matrix. In other terms max allowed number of indels
     * @param mutations         mutations array where all mutations will be kept
     * @return result of alignment which consists of mutations array at positions in both sequences where alignment has
     * been terminated
     */
    public static BandedSemiLocalResult alignRightAdded(LinearGapAlignmentScoring scoring, NucleotideSequence seq1, NucleotideSequence seq2,
                                                        int offset1, int length1, int addedNucleotides1, int offset2, int length2, int addedNucleotides2,
                                                        int width, IntArrayList mutations) {
        try {
            return alignRightAdded(scoring, seq1, seq2, offset1, length1, addedNucleotides1, offset2, length2, addedNucleotides2, width, mutations, AlignmentCache.get());
        } finally {
            AlignmentCache.release();
        }
    }

    /**
     * Semi-semi-global alignment with artificially added letters.
     *
     * <p>Alignment where second sequence is aligned to the right part of first sequence.</p> <p>Whole second sequence
     * must be highly similar to the first sequence</p>
     *
     * @param scoring           scoring system
     * @param seq1              first sequence
     * @param seq2              second sequence
     * @param offset1           offset in first sequence
     * @param length1           length of first sequence's part to be aligned including artificially added letters
     * @param addedNucleotides1 number of artificially added letters to the first sequence
     * @param offset2           offset in second sequence
     * @param length2           length of second sequence's part to be aligned including artificially added letters
     * @param addedNucleotides2 number of artificially added letters to the second sequence
     * @param width             width of banded alignment matrix. In other terms max allowed number of indels
     * @param mutations         mutations array where all mutations will be kept
     * @param cachedArray       cached (created once) array to be used in {@link BandedMatrix}, which is compact
     *                          alignment scoring matrix
     */
    public static BandedSemiLocalResult alignRightAdded(LinearGapAlignmentScoring scoring, NucleotideSequence seq1, NucleotideSequence seq2,
                                                        int offset1, int length1, int addedNucleotides1, int offset2, int length2, int addedNucleotides2,
                                                        int width, IntArrayList mutations, CachedIntArray cachedArray) {

        int size1 = length1 + 1,
                size2 = length2 + 1;

        BandedMatrix matrix = new BandedMatrix(cachedArray, size1, size2, width);

        int i, j;

        for (i = matrix.getRowFactor() - matrix.getColumnDelta(); i > 0; --i)
            matrix.set(0, i, scoring.getGapPenalty() * i);

        for (i = matrix.getColumnDelta(); i > 0; --i)
            matrix.set(i, 0, scoring.getGapPenalty() * i);

        matrix.set(0, 0, 0);

        int match, delete, insert, to;

        for (i = 0; i < length1; ++i) {
            to = Math.min(i + matrix.getRowFactor() - matrix.getColumnDelta() + 1, length2);
            for (j = Math.max(0, i - matrix.getColumnDelta()); j < to; ++j) {
                match = matrix.get(i, j) +
                        scoring.getScore(seq1.codeAt(offset1 + i), seq2.codeAt(offset2 + j));
                delete = matrix.get(i, j + 1) + scoring.getGapPenalty();
                insert = matrix.get(i + 1, j) + scoring.getGapPenalty();
                matrix.set(i + 1, j + 1, Math.max(match, Math.max(delete, insert)));
            }
        }

        //Searching for max.
        int maxI = -1, maxJ = -1;
        int maxScore = Integer.MIN_VALUE;

        j = length2;
        for (i = length1 - addedNucleotides1; i < size1; ++i)
            if (maxScore < matrix.get(i, j)) {
                maxScore = matrix.get(i, j);
                maxI = i;
                maxJ = j;
            }

        i = length1;
        for (j = length2 - addedNucleotides2; j < size2; ++j)
            if (maxScore < matrix.get(i, j)) {
                maxScore = matrix.get(i, j);
                maxI = i;
                maxJ = j;
            }

        to = mutations.size();
        i = maxI - 1;
        j = maxJ - 1;
        byte c1, c2;
        while (i >= 0 || j >= 0) {
            if (i >= 0 && j >= 0 &&
                    matrix.get(i + 1, j + 1) == matrix.get(i, j) +
                            scoring.getScore(c1 = seq1.codeAt(offset1 + i),
                                    c2 = seq2.codeAt(offset2 + j))) {
                if (c1 != c2)
                    mutations.add(createSubstitution(offset1 + i, c1, c2));
                --i;
                --j;
            } else if (i >= 0 &&
                    matrix.get(i + 1, j + 1) ==
                            matrix.get(i, j + 1) + scoring.getGapPenalty()) {
                mutations.add(createDeletion(offset1 + i, seq1.codeAt(offset1 + i)));
                --i;
            } else if (j >= 0 &&
                    matrix.get(i + 1, j + 1) ==
                            matrix.get(i + 1, j) + scoring.getGapPenalty()) {
                mutations.add(createInsertion(offset1 + i + 1, seq2.codeAt(offset2 + j)));
                --j;
            } else
                throw new RuntimeException();
        }

        mutations.reverse(to, mutations.size());

        return new BandedSemiLocalResult(offset1 + maxI - 1, offset2 + maxJ - 1, null, maxScore);
        //return new LocalAlignment(new Range(offset1, offset1 + maxI), new Range(offset2, offset2 + maxJ), null);
    }

    /**
     * Semi-semi-global alignment with artificially added letters.
     *
     * <p>Alignment where second sequence is aligned to the left part of first sequence.</p>
     *
     * <p>Second sequence must be highly similar to the first sequence, except last {@code width} letters, which are to
     * be checked whether they can improve alignment or not.</p>
     *
     * @param scoring           scoring system
     * @param seq1              first sequence
     * @param seq2              second sequence
     * @param offset1           offset in first sequence
     * @param length1           length of first sequence's part to be aligned including artificially added letters
     * @param addedNucleotides1 number of artificially added letters to the first sequence
     * @param offset2           offset in second sequence
     * @param length2           length of second sequence's part to be aligned including artificially added letters
     * @param addedNucleotides2 number of artificially added letters to the second sequence
     * @param width             width of banded alignment matrix. In other terms max allowed number of indels
     * @param mutations         mutations array where all mutations will be kept
     * @return result of alignment which consists of mutations array at positions in both sequences where alignment has
     * been terminated
     */
    public static BandedSemiLocalResult alignLeftAdded(LinearGapAlignmentScoring scoring, NucleotideSequence seq1, NucleotideSequence seq2,
                                                       int offset1, int length1, int addedNucleotides1, int offset2, int length2, int addedNucleotides2,
                                                       int width, IntArrayList mutations) {
        try {
            return alignLeftAdded(scoring, seq1, seq2, offset1, length1, addedNucleotides1, offset2, length2, addedNucleotides2, width, mutations, AlignmentCache.get());
        } finally {
            AlignmentCache.release();
        }
    }

    /**
     * Semi-semi-global alignment with artificially added letters.
     *
     * <p>Alignment where second sequence is aligned to the left part of first sequence.</p>
     *
     * <p>Whole second sequence must be highly similar to the first sequence, except last {@code width} letters, which
     * are to be checked whether they can improve alignment or not.</p>
     *
     * @param scoring           scoring system
     * @param seq1              first sequence
     * @param seq2              second sequence
     * @param offset1           offset in first sequence
     * @param length1           length of first sequence's part to be aligned
     * @param addedNucleotides1 number of artificially added letters to the first sequence
     * @param offset2           offset in second sequence
     * @param length2           length of second sequence's part to be aligned
     * @param addedNucleotides2 number of artificially added letters to the second sequence
     * @param width             width of banded alignment matrix. In other terms max allowed number of indels
     * @param mutations         mutations array where all mutations will be kept
     * @param cachedArray       cached (created once) array to be used in {@link BandedMatrix}, which is compact
     *                          alignment scoring matrix
     */
    public static BandedSemiLocalResult alignLeftAdded(LinearGapAlignmentScoring scoring, NucleotideSequence seq1, NucleotideSequence seq2,
                                                       int offset1, int length1, int addedNucleotides1, int offset2, int length2, int addedNucleotides2,
                                                       int width, IntArrayList mutations, CachedIntArray cachedArray) {
        int size1 = length1 + 1,
                size2 = length2 + 1;

        BandedMatrix matrix = new BandedMatrix(cachedArray, size1, size2, width);

        int i, j;

        for (i = matrix.getRowFactor() - matrix.getColumnDelta(); i > 0; --i)
            matrix.set(0, i, scoring.getGapPenalty() * i);

        for (i = matrix.getColumnDelta(); i > 0; --i)
            matrix.set(i, 0, scoring.getGapPenalty() * i);

        matrix.set(0, 0, 0);

        int match, delete, insert, to;

        for (i = 0; i < length1; ++i) {
            to = Math.min(i + matrix.getRowFactor() - matrix.getColumnDelta() + 1, length2);
            for (j = Math.max(0, i - matrix.getColumnDelta()); j < to; ++j) {
                match = matrix.get(i, j) +
                        scoring.getScore(seq1.codeAt(offset1 + length1 - 1 - i),
                                seq2.codeAt(offset2 + length2 - 1 - j));
                delete = matrix.get(i, j + 1) + scoring.getGapPenalty();
                insert = matrix.get(i + 1, j) + scoring.getGapPenalty();
                matrix.set(i + 1, j + 1, Math.max(match, Math.max(delete, insert)));
            }
        }

        //Searching for max.
        int maxI = -1, maxJ = -1;
        int maxScore = Integer.MIN_VALUE;

        j = length2;
        for (i = length1 - addedNucleotides1; i < size1; ++i)
            if (maxScore < matrix.get(i, j)) {
                maxScore = matrix.get(i, j);
                maxI = i;
                maxJ = j;
            }

        i = length1;
        for (j = length2 - addedNucleotides2; j < size2; ++j)
            if (maxScore < matrix.get(i, j)) {
                maxScore = matrix.get(i, j);
                maxI = i;
                maxJ = j;
            }

        to = mutations.size();
        i = maxI - 1;
        j = maxJ - 1;
        byte c1, c2;
        while (i >= 0 || j >= 0) {
            if (i >= 0 && j >= 0 &&
                    matrix.get(i + 1, j + 1) == matrix.get(i, j) +
                            scoring.getScore(c1 = seq1.codeAt(offset1 + length1 - 1 - i),
                                    c2 = seq2.codeAt(offset2 + length2 - 1 - j))) {
                if (c1 != c2)
                    mutations.add(createSubstitution(offset1 + length1 - 1 - i, c1, c2));
                --i;
                --j;
            } else if (i >= 0 &&
                    matrix.get(i + 1, j + 1) ==
                            matrix.get(i, j + 1) + scoring.getGapPenalty()) {
                mutations.add(createDeletion(offset1 + length1 - 1 - i, seq1.codeAt(offset1 + length1 - 1 - i)));
                --i;
            } else if (j >= 0 &&
                    matrix.get(i + 1, j + 1) ==
                            matrix.get(i + 1, j) + scoring.getGapPenalty()) {
                mutations.add(createInsertion(offset1 + length1 - 1 - i, seq2.codeAt(offset2 + length2 - 1 - j)));
                --j;
            } else
                throw new RuntimeException();
        }

        return new BandedSemiLocalResult(offset1 + length1 - maxI, offset2 + length2 - maxJ, null, maxScore);
    }

    //TODO: Meaning of width in right and left (optimization)

    /**
     * Alignment which identifies what is the highly similar part of the both sequences.
     *
     * <p>Alignment is done in the way that beginning of second sequence is aligned to beginning of first
     * sequence.</p>
     *
     * <p>Alignment terminates when score in banded alignment matrix reaches {@code stopPenalty} value.</p>
     *
     * <p>In other words, only left part of second sequence is to be aligned</p>
     *
     * @param scoring     scoring system
     * @param seq1        first sequence
     * @param seq2        second sequence
     * @param width       width of banded alignment matrix. In other terms max allowed number of indels
     * @param stopPenalty alignment score value in banded alignment matrix at which alignment terminates
     * @return object which contains positions at which alignment terminated and array of mutations
     */
    public static BandedSemiLocalResult alignSemiLocalLeft(LinearGapAlignmentScoring scoring, NucleotideSequence seq1, NucleotideSequence seq2,
                                                           int width, int stopPenalty) {
        try {
            return alignSemiLocalLeft(scoring, seq1, seq2, 0, seq1.size(), 0, seq2.size(), width, stopPenalty, null, AlignmentCache.get());
        } finally {
            AlignmentCache.release();
        }
    }

    /**
     * Alignment which identifies what is the highly similar part of the both sequences.
     *
     * <p>Alignment is done in the way that beginning of second sequence is aligned to beginning of first
     * sequence.</p>
     *
     * <p>Alignment terminates when score in banded alignment matrix reaches {@code stopPenalty} value.</p>
     *
     * <p>In other words, only left part of second sequence is to be aligned</p>
     *
     * @param parameters alignment paramenters
     * @param seq1       first sequence
     * @param seq2       second sequence
     * @return object which contains positions at which alignment terminated and array of mutations
     */
    public static BandedSemiLocalResult alignSemiLocalLeft(BandedAlignerParameters parameters, NucleotideSequence seq1, NucleotideSequence seq2) {
        try {
            return alignSemiLocalLeft(parameters.getScoring(), seq1, seq2, 0, seq1.size(), 0, seq2.size(), parameters.getWidth(), parameters.getStopPenalty(),
                    null, AlignmentCache.get());
        } finally {
            AlignmentCache.release();
        }
    }


    /**
     * Alignment which identifies what is the highly similar part of the both sequences.
     *
     * <p>Alignment is done in the way that beginning of second sequences is aligned to beginning of first
     * sequence.</p>
     *
     * <p>Alignment terminates when score in banded alignment matrix reaches {@code stopPenalty} value.</p>
     *
     * <p>In other words, only left part of second sequence is to be aligned</p>
     *
     * @param scoring     scoring system
     * @param seq1        first sequence
     * @param seq2        second sequence
     * @param offset1     offset in first sequence
     * @param length1     length of first sequence's part to be aligned
     * @param offset2     offset in second sequence
     * @param length2     length of second sequence's part to be aligned@param width
     * @param stopPenalty alignment score value in banded alignment matrix at which alignment terminates
     * @param mutations   array where all mutations will be kept
     * @param cachedArray cached (created once) array to be used in {@link BandedMatrix}, which is compact alignment
     *                    scoring matrix
     * @return object which contains positions at which alignment terminated and array of mutations
     */
    public static BandedSemiLocalResult alignSemiLocalLeft(LinearGapAlignmentScoring scoring, NucleotideSequence seq1, NucleotideSequence seq2,
                                                           int offset1, int length1, int offset2, int length2,
                                                           int width, int stopPenalty, IntArrayList mutations, CachedIntArray cachedArray) {

        int size1 = length1 + 1,
                size2 = length2 + 1;

        int matchReward = scoring.getScore((byte) 0, (byte) 0);

        BandedMatrix matrix = new BandedMatrix(cachedArray, size1, size2, width);

        int i, j;

        for (i = matrix.getRowFactor() - matrix.getColumnDelta(); i > 0; --i)
            matrix.set(0, i, scoring.getGapPenalty() * i);

        for (i = matrix.getColumnDelta(); i > 0; --i)
            matrix.set(i, 0, scoring.getGapPenalty() * i);

        matrix.set(0, 0, 0);

        int match, delete, insert, to;
        int max = 0;
        int iStop = 0, jStop = 0;
        int rowMax;

        for (i = 0; i < length1; ++i) {
            to = Math.min(i + matrix.getRowFactor() - matrix.getColumnDelta() + 1, size2 - 1);
            rowMax = Integer.MIN_VALUE;
            for (j = Math.max(0, i - matrix.getColumnDelta()); j < to; ++j) {
                match = matrix.get(i, j) +
                        scoring.getScore(seq1.codeAt(offset1 + i), seq2.codeAt(offset2 + j));
                delete = matrix.get(i, j + 1) + scoring.getGapPenalty();
                insert = matrix.get(i + 1, j) + scoring.getGapPenalty();
                matrix.set(i + 1, j + 1, match = Math.max(match, Math.max(delete, insert)));
                if (max < match) {
                    iStop = i + 1;
                    jStop = j + 1;
                    max = match;
                }
                rowMax = Math.max(rowMax, match);
            }
            if (rowMax - i * matchReward < stopPenalty)
                break;
        }

        IntArrayList list = mutations == null ? new IntArrayList() : mutations;

        int fromL = list.size();

        i = iStop - 1;
        j = jStop - 1;
        byte c1, c2;
        while (i >= 0 || j >= 0) {
            if (i >= 0 && j >= 0 &&
                    matrix.get(i + 1, j + 1) == matrix.get(i, j) +
                            scoring.getScore(c1 = seq1.codeAt(offset1 + i),
                                    c2 = seq2.codeAt(offset2 + j))) {
                if (c1 != c2)
                    list.add(createSubstitution(offset1 + i, c1, c2));
                --i;
                --j;
            } else if (i >= 0 &&
                    matrix.get(i + 1, j + 1) ==
                            matrix.get(i, j + 1) + scoring.getGapPenalty()) {
                list.add(createDeletion(offset1 + i, seq1.codeAt(offset1 + i)));
                --i;
            } else if (j >= 0 &&
                    matrix.get(i + 1, j + 1) ==
                            matrix.get(i + 1, j) + scoring.getGapPenalty()) {
                list.add(createInsertion(offset1 + i + 1, seq2.codeAt(offset2 + j)));
                --j;
            } else
                throw new RuntimeException();
        }

        list.reverse(fromL, list.size());

        return new BandedSemiLocalResult(offset1 + iStop - 1, offset2 + jStop - 1, mutations == null ? list.toArray() : null, max);
    }

    /**
     * Alignment which identifies what is the highly similar part of the both sequences.
     *
     * <p>Alignment is done in the way that end of second sequence is aligned to end of first sequence.</p>
     *
     * <p>Alignment terminates when score in banded alignment matrix reaches {@code stopPenalty} value.</p>
     *
     * <p>In other words, only right part of second sequence is to be aligned.</p>
     *
     * @param scoring     scoring system
     * @param seq1        first sequence
     * @param seq2        second sequence
     * @param width       width of banded alignment matrix. In other terms max allowed number of indels
     * @param stopPenalty alignment score value in banded alignment matrix at which alignment terminates
     * @return object which contains positions at which alignment terminated and array of mutations
     */
    public static BandedSemiLocalResult alignSemiLocalRight(LinearGapAlignmentScoring scoring, NucleotideSequence seq1, NucleotideSequence seq2,
                                                            int width, int stopPenalty) {
        try {
            return alignSemiLocalRight(scoring, seq1, seq2, 0, seq1.size(), 0, seq2.size(), width, stopPenalty, null, AlignmentCache.get());
        } finally {
            AlignmentCache.release();
        }
    }

    /**
     * Alignment which identifies what is the highly similar part of the both sequences.
     *
     * <p>Alignment is done in the way that end of second sequence is aligned to end of first sequence.</p>
     *
     * <p>Alignment terminates when score in banded alignment matrix reaches {@code stopPenalty} value.</p>
     *
     * <p>In other words, only right part of second sequence is to be aligned.</p>
     *
     * @param paramenters alignment paramenters
     * @param seq1        first sequence
     * @param seq2        second sequence
     * @return object which contains positions at which alignment terminated and array of mutations
     */
    public static BandedSemiLocalResult alignSemiLocalRight(BandedAlignerParameters paramenters,
                                                            NucleotideSequence seq1, NucleotideSequence seq2) {
        try {
            return alignSemiLocalRight(paramenters.getScoring(), seq1, seq2, 0, seq1.size(), 0, seq2.size(), paramenters.getWidth(),
                    paramenters.getStopPenalty(), null, AlignmentCache.get());
        } finally {
            AlignmentCache.release();
        }
    }

    /**
     * Alignment which identifies what is the highly similar part of the both sequences.
     *
     * <p>Alignment is done in the way that end of second sequence is aligned to end of first sequence.</p>
     * <p>Alignment terminates when score in banded alignment matrix reaches {@code stopPenalty} value.</p> <p>In other
     * words, only right part of second sequence is to be aligned.</p>
     *
     * @param scoring     scoring system
     * @param seq1        first sequence
     * @param seq2        second sequence
     * @param offset1     offset in first sequence
     * @param length1     length of first sequence's part to be aligned
     * @param offset2     offset in second sequence
     * @param length2     length of second sequence's part to be aligned@param width
     * @param stopPenalty alignment score value in banded alignment matrix at which alignment terminates
     * @param mutations   array where all mutations will be kept
     * @param cachedArray cached (created once) array to be used in {@link BandedMatrix}, which is compact alignment
     *                    scoring matrix
     * @return object which contains positions at which alignment terminated and array of mutations
     */
    public static BandedSemiLocalResult alignSemiLocalRight(LinearGapAlignmentScoring scoring, NucleotideSequence seq1, NucleotideSequence seq2,
                                                            int offset1, int length1, int offset2, int length2,
                                                            int width, int stopPenalty, IntArrayList mutations, CachedIntArray cachedArray) {
        int size1 = length1 + 1,
                size2 = length2 + 1;

        int matchReward = scoring.getScore((byte) 0, (byte) 0);

        BandedMatrix matrix = new BandedMatrix(cachedArray, size1, size2, width);

        int i, j;

        for (i = matrix.getRowFactor() - matrix.getColumnDelta(); i > 0; --i)
            matrix.set(0, i, scoring.getGapPenalty() * i);

        for (i = matrix.getColumnDelta(); i > 0; --i)
            matrix.set(i, 0, scoring.getGapPenalty() * i);

        matrix.set(0, 0, 0);

        int match, delete, insert, to;
        int max = 0;
        int iStop = 0, jStop = 0;
        int rowMax;

        for (i = 0; i < length1; ++i) {
            to = Math.min(i + matrix.getRowFactor() - matrix.getColumnDelta() + 1, length2);
            rowMax = Integer.MIN_VALUE;
            for (j = Math.max(0, i - matrix.getColumnDelta()); j < to; ++j) {
                match = matrix.get(i, j) +
                        scoring.getScore(seq1.codeAt(offset1 + length1 - 1 - i),
                                seq2.codeAt(offset2 + length2 - 1 - j));
                delete = matrix.get(i, j + 1) + scoring.getGapPenalty();
                insert = matrix.get(i + 1, j) + scoring.getGapPenalty();
                matrix.set(i + 1, j + 1, match = Math.max(match, Math.max(delete, insert)));
                if (max < match) {
                    iStop = i + 1;
                    jStop = j + 1;
                    max = match;
                }
                rowMax = Math.max(rowMax, match);
            }
            if (rowMax - i * matchReward < stopPenalty)
                break;
        }

        IntArrayList list = mutations == null ? new IntArrayList() : mutations;

        i = iStop - 1;
        j = jStop - 1;
        byte c1, c2;
        while (i >= 0 || j >= 0) {
            if (i >= 0 && j >= 0 &&
                    matrix.get(i + 1, j + 1) == matrix.get(i, j) +
                            scoring.getScore(c1 = seq1.codeAt(offset1 + length1 - 1 - i),
                                    c2 = seq2.codeAt(offset2 + length2 - 1 - j))) {
                if (c1 != c2)
                    list.add(createSubstitution(offset1 + length1 - 1 - i, c1, c2));
                --i;
                --j;
            } else if (i >= 0 &&
                    matrix.get(i + 1, j + 1) ==
                            matrix.get(i, j + 1) + scoring.getGapPenalty()) {
                list.add(createDeletion(offset1 + length1 - 1 - i, seq1.codeAt(offset1 + length1 - 1 - i)));
                --i;
            } else if (j >= 0 &&
                    matrix.get(i + 1, j + 1) ==
                            matrix.get(i + 1, j) + scoring.getGapPenalty()) {
                list.add(createInsertion(offset1 + length1 - 1 - i, seq2.codeAt(offset2 + length2 - 1 - j)));
                --j;
            } else
                throw new RuntimeException();
        }

        return new BandedSemiLocalResult(offset1 + length1 - iStop, offset2 + length2 - jStop, mutations == null ? list.toArray() : null, max);
    }
}

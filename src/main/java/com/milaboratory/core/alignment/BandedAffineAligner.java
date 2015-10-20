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
    public static float align0(AffineGapAlignmentScoring<NucleotideSequence> scoring,
                               NucleotideSequence seq1, NucleotideSequence seq2,
                               int offset1, int length1, int offset2, int length2,
                               int width, MutationsBuilder<NucleotideSequence> mutations,
                               CachedIntArray cachedArray1,
                               CachedIntArray cachedArray2,
                               CachedIntArray cachedArray3) {

        int size1 = length1 + 1,
                size2 = length2 + 1;

        BandedMatrix matrix = new BandedMatrix(cachedArray1, size1, size2, width);
        BandedMatrix gapIn1 = new BandedMatrix(cachedArray2, size1, size2, width);
        BandedMatrix gapIn2 = new BandedMatrix(cachedArray3, size1, size2, width);

        int i, j;

        for (i = matrix.getRowFactor() - matrix.getColumnDelta(); i > 0; --i) {
            int v = scoring.getGapOpenPenalty() + scoring.getGapExtensionPenalty() * (i - 1);
            matrix.set(0, i, v);
            gapIn1.set(0, i, v);
            gapIn2.set(0, i, BandedMatrix.DEFAULT_VALUE);
        }

        for (i = matrix.getColumnDelta(); i > 0; --i) {
            int v = scoring.getGapOpenPenalty() + scoring.getGapExtensionPenalty() * (i - 1);
            matrix.set(i, 0, v);
            gapIn1.set(i, 0, BandedMatrix.DEFAULT_VALUE);
            gapIn2.set(i, 0, v);
        }

        matrix.set(0, 0, 0);
        gapIn1.set(0, 0, BandedMatrix.DEFAULT_VALUE);
        gapIn2.set(0, 0, BandedMatrix.DEFAULT_VALUE);

        int match, gap1, gap2, to;

        for (i = 0; i < length1; ++i) {
            to = Math.min(i + matrix.getRowFactor() - matrix.getColumnDelta() + 1, length2);
            for (j = Math.max(0, i - matrix.getColumnDelta()); j < to; ++j) {
                match = matrix.get(i, j) +
                        scoring.getScore(seq1.codeAt(offset1 + i), seq2.codeAt(offset2 + j));

                gap1 = Math.max(matrix.get(i + 1, j) + scoring.getGapOpenPenalty(), gapIn1.get(i + 1, j) + scoring.getGapExtensionPenalty());
                gap2 = Math.max(matrix.get(i, j + 1) + scoring.getGapOpenPenalty(), gapIn2.get(i, j + 1) + scoring.getGapExtensionPenalty());

                gapIn1.set(i + 1, j + 1, gap1);
                gapIn2.set(i + 1, j + 1, gap2);
                matrix.set(i + 1, j + 1, Math.max(match, Math.max(gap1, gap2)));
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
                    mutations.appendSubstitution(offset1 + i, c1, c2);
                --i;
                --j;
            } else if (i >= 0 &&
                    matrix.get(i + 1, j + 1) ==
                            gapIn2.get(i + 1, j + 1)) {
                mutations.appendDeletion(offset1 + i, seq1.codeAt(offset1 + i));
                --i;
            } else if (j >= 0 &&
                    matrix.get(i + 1, j + 1) ==
                            gapIn1.get(i + 1, j + 1)) {
                mutations.appendInsertion(offset1 + i + 1, seq2.codeAt(offset2 + j));
                --j;
            } else
                throw new RuntimeException();
        }

        mutations.reverseRange(to, mutations.size());
        return matrix.get(length1, length2);
    }
}

package com.milaboratory.core.alignment;

import com.milaboratory.core.Range;
import com.milaboratory.core.mutations.MutationsBuilder;
import com.milaboratory.core.sequence.Sequence;

public final class Aligner {
    private Aligner() {
    }

    /**
     * Performs global alignment using Linear scoring system (penalty exists only for gap)
     *
     * @param scoring linear scoring system
     * @param seq1    first sequence
     * @param seq2    second sequence
     * @return array of mutations
     */
    public static <S extends Sequence<S>> Alignment<S> align(LinearGapAlignmentScoring scoring,
                                                             S seq1, S seq2) {
        if (seq1.getAlphabet() != seq2.getAlphabet() ||
                seq1.getAlphabet() != scoring.getAlphabet())
            throw new IllegalArgumentException("Different alphabets.");

        int size1 = seq1.size() + 1,
                size2 = seq2.size() + 1;
        int[] matrix = new int[size1 * (seq2.size() + 1)];

        for (int i = 0; i < size2; ++i)
            matrix[i] = scoring.getGapPenalty() * i;

        for (int j = 1; j < size1; ++j)
            matrix[size2 * j] = scoring.getGapPenalty() * j;

        int i1, i2,
                match, delete, insert;

        for (i1 = 0; i1 < seq1.size(); ++i1)
            for (i2 = 0; i2 < seq2.size(); ++i2) {
                match = matrix[i1 * size2 + i2] +
                        scoring.getScore(seq1.codeAt(i1), seq2.codeAt(i2));
                delete = matrix[i1 * size2 + i2 + 1] + scoring.getGapPenalty();
                insert = matrix[(i1 + 1) * size2 + i2] + scoring.getGapPenalty();
                matrix[(i1 + 1) * size2 + i2 + 1] = Math.max(match, Math.max(delete, insert));
            }

        MutationsBuilder<S> builder = new MutationsBuilder<>(seq1.getAlphabet(), true);

        i1 = seq1.size() - 1;
        i2 = seq2.size() - 1;
        int score = matrix[(i1 + 1) * size2 + i2 + 1];

        while (i1 >= 0 || i2 >= 0) {
            if (i1 >= 0 && i2 >= 0 &&
                    matrix[(i1 + 1) * size2 + i2 + 1] == matrix[i1 * size2 + i2] +
                            scoring.getScore(seq1.codeAt(i1), seq2.codeAt(i2))) {
                if (seq1.codeAt(i1) != seq2.codeAt(i2))
                    builder.appendSubstitution(i1, seq1.codeAt(i1), seq2.codeAt(i2));
                --i1;
                --i2;
            } else if (i1 >= 0 &&
                    matrix[(i1 + 1) * size2 + i2 + 1] ==
                            matrix[i1 * size2 + i2 + 1] + scoring.getGapPenalty()) {
                builder.appendDeletion(i1, seq1.codeAt(i1));
                i1--;
            } else if (i2 >= 0 &&
                    matrix[(i1 + 1) * size2 + i2 + 1] ==
                            matrix[(i1 + 1) * size2 + i2] + scoring.getGapPenalty()) {
                builder.appendInsertion(i1 + 1, seq2.codeAt(i2));
                i2--;
            } else
                throw new RuntimeException();
        }

        return new Alignment<>(seq1, builder.createAndDestroy(), new Range(0, seq1.size()), new Range(0, seq2.size()),
                score);
    }
}

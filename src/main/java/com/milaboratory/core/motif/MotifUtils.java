package com.milaboratory.core.motif;

import com.milaboratory.core.sequence.Sequence;

public class MotifUtils {
    public static <S extends Sequence<S>> Motif<S> twoSequenceMotif(S seq1, int offset1,
                                                                    S seq2, int offset2,
                                                                    int length) {
        if (seq1 == null || seq2 == null)
            throw new NullPointerException();
        if (offset1 < 0 || offset2 < 0 ||
                seq1.size() < offset1 + length ||
                seq2.size() < offset2 + length)
            throw new IllegalArgumentException();

        MotifBuilder<S> builder = new MotifBuilder<>(seq1.getAlphabet(), length);

        for (int i = 0; i < length; ++i) {
            builder.setAllowedLetter(i, seq1.codeAt(offset1 + i));
            builder.setAllowedLetter(i, seq2.codeAt(offset2 + i));
        }

        return builder.createAndDestroy();
    }
}

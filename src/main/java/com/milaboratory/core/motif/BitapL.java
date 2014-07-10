package com.milaboratory.core.motif;

import com.milaboratory.core.sequence.Sequence;

import java.util.Arrays;

public class BitapL {
    final long[] patternMask;
    final int patternLength;

    public BitapL(Sequence sequence) {
        if (sequence.size() > 64)
            throw new IllegalArgumentException("Supports only sequences with length < 64.");
        this.patternLength = sequence.size();
        this.patternMask = new long[sequence.getAlphabet().size()];

        Arrays.fill(patternMask, ~0);

        for (int i = 0; i < patternLength; ++i)
            patternMask[sequence.codeAt(i)] &= ~(1L << i);
    }

    public int search(Sequence sequence, int mismatches, int from) {
        long[] R = new long[mismatches + 1];
        Arrays.fill(R, ~1L);

        int d;
        long tmp, oldRd1;

        for (int i = from; i < sequence.size(); ++i) {
            oldRd1 = R[0];

            R[0] |= patternMask[sequence.codeAt(i)];
            R[0] <<= 1;

            for (d = 1; d <= mismatches; ++d) {
                tmp = R[d];
                R[d] = (oldRd1 & (R[d] | patternMask[sequence.codeAt(i)])) << 1;
                oldRd1 = tmp;
            }

            if (0 == (R[mismatches] & 1L << patternLength))
                return i - patternLength + 1;
        }
        return -1;
    }
}

package com.milaboratory.core.motif;

import java.util.Arrays;

public abstract class BitapMatcherImpl implements BitapMatcher {
    int errors;
    final long[] R;
    final int to;
    int current;

    public BitapMatcherImpl(int count, int from, int to) {
        this.R = new long[count];
        Arrays.fill(R, ~1L);
        this.current = from;
        this.to = to;
    }

    public abstract int findNext();

    public int getNumberOfErrors() {
        return errors;
    }
}

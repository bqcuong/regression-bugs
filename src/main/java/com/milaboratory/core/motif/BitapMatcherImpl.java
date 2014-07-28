package com.milaboratory.core.motif;

public abstract class BitapMatcherImpl implements BitapMatcher {
    int errors;
    final long[] R;
    final int to;
    int current;

    public BitapMatcherImpl(int count, int from, int to) {
        this.R = new long[count];
        for (int i = 0; i < count; ++i)
            R[i] = (~0) << i;
        this.current = from;
        this.to = to;
    }

    public abstract int findNext();

    public int getNumberOfErrors() {
        return errors;
    }
}

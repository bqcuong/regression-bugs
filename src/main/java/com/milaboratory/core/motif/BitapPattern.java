package com.milaboratory.core.motif;

import com.milaboratory.core.sequence.Sequence;

public final class BitapPattern {
    final int size;
    final long[] patternMask;

    BitapPattern(int size, long[] patternMask) {
        this.size = size;
        this.patternMask = patternMask;
    }

    public int exactSearch(Sequence sequence) {
        return exactSearch(sequence, 0, sequence.size());
    }

    public int exactSearch(Sequence sequence, int from) {
        return exactSearch(sequence, from, sequence.size());
    }

    public int exactSearch(Sequence sequence, int from, int to) {
        if (sequence.getAlphabet().size() != patternMask.length)
            throw new IllegalArgumentException();

        long R = ~1L;
        long matchingMask = (1L << size);

        for (int i = from; i < to; ++i) {
            R |= patternMask[sequence.codeAt(i)];
            R <<= 1;
            if (0 == (R & matchingMask))
                return i - size + 1;
        }
        return -1;
    }

    public BitapMatcher exactMatcher(final Sequence sequence, final int from, final int to) {
        if (sequence.getAlphabet().size() != patternMask.length)
            throw new IllegalArgumentException();

        return new BitapMatcher() {
            long R = ~1L;
            int current = from;

            @Override
            public int findNext() {
                long matchingMask = (1L << size);
                for (int i = current; i < to; ++i) {
                    R |= patternMask[sequence.codeAt(i)];
                    R <<= 1;
                    if (0 == (R & matchingMask)) {
                        current = i + 1;
                        return i - size + 1;
                    }
                }
                current = to;
                return -1;
            }

            @Override
            public int getNumberOfErrors() {
                return 0;
            }
        };
    }

    public BitapMatcher mismatchOnlyMatcher(int mismatches, final Sequence sequence) {
        return mismatchOnlyMatcher(mismatches, sequence, 0, sequence.size());
    }

    public BitapMatcher mismatchOnlyMatcher(int mismatches, final Sequence sequence, int from) {
        return mismatchOnlyMatcher(mismatches, sequence, from, sequence.size());
    }

    public BitapMatcher mismatchOnlyMatcher(int mismatches, final Sequence sequence, int from, int to) {
        if (sequence.getAlphabet().size() != patternMask.length)
            throw new IllegalArgumentException();

        return new BitapMatcherImpl(mismatches + 1, from, to) {
            @Override
            public int findNext() {
                long matchingMask = (1L << size);

                int d;
                long tmp, oldRd1;

                for (int i = current; i < to; ++i) {
                    oldRd1 = R[0];

                    long currentPatternMask = patternMask[sequence.codeAt(i)];

                    R[0] |= currentPatternMask;
                    R[0] <<= 1;

                    if (0 == (R[0] & matchingMask)) {
                        errors = 0;
                        current = i + 1;
                        return i - size + 1;
                    }

                    for (d = 1; d < R.length; ++d) {
                        tmp = R[d];
                        R[d] = (oldRd1 & (R[d] | currentPatternMask)) << 1;
                        oldRd1 = tmp;
                        if (0 == (R[d] & matchingMask)) {
                            errors = d;
                            current = i + 1;
                            return i - size + 1;
                        }
                    }
                }
                current = to;
                return -1;
            }
        };
    }

    public BitapMatcher mismatchAndIndelMatcher(int maxNumberOfErrors, final Sequence sequence, int from, int to) {
        if (sequence.getAlphabet().size() != patternMask.length)
            throw new IllegalArgumentException();

        return new BitapMatcherImpl(maxNumberOfErrors + 1, from, to) {
            @Override
            public int findNext() {
                long matchingMask = (1L << (size - 1));

                int d;
                long tmp, oldRd1;

                for (int i = current; i < to; ++i) {
                    oldRd1 = R[0];

                    long currentPatternMask = patternMask[sequence.codeAt(i)];

                    R[0] |= currentPatternMask;
                    R[0] <<= 1;

                    if (0 == (R[0] & matchingMask)) {
                        errors = 0;
                        current = i + 1;
                        return i - size + 1;
                    }

                    for (d = 1; d < R.length; ++d) {
                        tmp = R[d];
                        R[d] = (R[d] << 1) | currentPatternMask & oldRd1;
                        oldRd1 = tmp;
                        if (0 == (R[d] & matchingMask)) {
                            errors = d;
                            current = i + 1;
                            return i - size + 1;
                        }
                    }
                }
                current = to;
                return -1;
            }
        };
    }
}

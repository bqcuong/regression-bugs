package com.milaboratory.core.motif;

import com.milaboratory.core.sequence.Sequence;

public final class BitapPattern {
    final int size;
    final long[] patternMask;
    final long[] reversePatternMask;

    public BitapPattern(int size, long[] patternMask, long[] reversePatternMask) {
        this.size = size;
        this.patternMask = patternMask;
        this.reversePatternMask = reversePatternMask;
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

    public BitapMatcher mismatchOnlyMatcherFirst(int mismatches, final Sequence sequence) {
        return mismatchOnlyMatcherFirst(mismatches, sequence, 0, sequence.size());
    }

    public BitapMatcher mismatchOnlyMatcherFirst(int mismatches, final Sequence sequence, int from) {
        return mismatchOnlyMatcherFirst(mismatches, sequence, from, sequence.size());
    }

    public BitapMatcher mismatchOnlyMatcherFirst(int mismatches, final Sequence sequence, int from, int to) {
        if (sequence.getAlphabet().size() != patternMask.length)
            throw new IllegalArgumentException();

        return new BitapMatcherImpl(mismatches + 1, from, to) {
            @Override
            public int findNext() {
                long matchingMask = (1L << (size - 1));

                int d;
                long preMismatchTmp, mismatchTmp;

                for (int i = current; i < to; ++i) {
                    long currentPatternMask = patternMask[sequence.codeAt(i)];

                    // Exact match on the previous step == match with insertion on current step
                    R[0] <<= 1;
                    mismatchTmp = R[0];
                    R[0] |= currentPatternMask;

                    if (0 == (R[0] & matchingMask)) {
                        errors = 0;
                        current = i + 1;
                        return i - size + 1;
                    }

                    for (d = 1; d < R.length; ++d) {
                        R[d] <<= 1;
                        preMismatchTmp = R[d];
                        R[d] |= currentPatternMask;
                        R[d] &= mismatchTmp;
                        if (0 == (R[d] & matchingMask)) {
                            errors = d;
                            current = i + 1;
                            return i - size + 1;
                        }
                        mismatchTmp = preMismatchTmp;
                    }
                }
                current = to;
                return -1;
            }
        };
    }

    public BitapMatcher mismatchAndIndelMatcherLast(int maxNumberOfErrors, final Sequence sequence) {
        return mismatchAndIndelMatcherLast(maxNumberOfErrors, sequence, 0, sequence.size());
    }

    public BitapMatcher mismatchAndIndelMatcherLast(int maxNumberOfErrors, final Sequence sequence, int from) {
        return mismatchAndIndelMatcherLast(maxNumberOfErrors, sequence, from, sequence.size());
    }

    public BitapMatcher mismatchAndIndelMatcherLast(int maxNumberOfErrors, final Sequence sequence, int from, int to) {
        if (sequence.getAlphabet().size() != patternMask.length)
            throw new IllegalArgumentException();

        return new BitapMatcherImpl(maxNumberOfErrors + 1, from, to) {
            @Override
            public int findNext() {
                long matchingMask = (1L << (size - 1));

                int d;
                long preInsertionTmp, preMismatchTmp,
                        insertionTmp, deletionTmp, mismatchTmp;

                for (int i = current; i < to; ++i) {
                    long currentPatternMask = patternMask[sequence.codeAt(i)];

                    // Exact match on the previous step == match with insertion on current step
                    insertionTmp = R[0];
                    R[0] <<= 1;
                    mismatchTmp = R[0];
                    R[0] |= currentPatternMask;
                    deletionTmp = R[0];

                    if (0 == (R[0] & matchingMask)) {
                        errors = 0;
                        current = i + 1;
                        return i;
                    }

                    for (d = 1; d < R.length; ++d) {
                        preInsertionTmp = R[d];
                        R[d] <<= 1;
                        preMismatchTmp = R[d];
                        R[d] |= currentPatternMask;
                        R[d] &= insertionTmp & mismatchTmp & (deletionTmp << 1);
                        if (0 == (R[d] & matchingMask)) {
                            errors = d;
                            current = i + 1;
                            return i;
                        }
                        deletionTmp = R[d];
                        insertionTmp = preInsertionTmp;
                        mismatchTmp = preMismatchTmp;
                    }
                }
                current = to;
                return -1;
            }
        };
    }

    public BitapMatcher mismatchAndIndelMatcherFirst(int maxNumberOfErrors, final Sequence sequence) {
        return mismatchAndIndelMatcherFirst(maxNumberOfErrors, sequence, 0, sequence.size());
    }

    public BitapMatcher mismatchAndIndelMatcherFirst(int maxNumberOfErrors, final Sequence sequence, int from) {
        return mismatchAndIndelMatcherFirst(maxNumberOfErrors, sequence, from, sequence.size());
    }

    public BitapMatcher mismatchAndIndelMatcherFirst(int maxNumberOfErrors, final Sequence sequence, int from, int to) {
        if (sequence.getAlphabet().size() != patternMask.length)
            throw new IllegalArgumentException();

        return new BitapMatcherImpl(maxNumberOfErrors + 1, to - 1, from) {
            @Override
            public int findNext() {
                long matchingMask = (1L << (size - 1));

                int d;
                long preInsertionTmp, preMismatchTmp,
                        insertionTmp, deletionTmp, mismatchTmp;

                for (int i = current; i >= to; --i) {
                    long currentPatternMask = reversePatternMask[sequence.codeAt(i)];

                    // Exact match on the previous step == match with insertion on current step
                    insertionTmp = R[0];
                    R[0] <<= 1;
                    mismatchTmp = R[0];
                    R[0] |= currentPatternMask;
                    deletionTmp = R[0];

                    if (0 == (R[0] & matchingMask)) {
                        errors = 0;
                        current = i - 1;
                        return i;
                    }

                    for (d = 1; d < R.length; ++d) {
                        preInsertionTmp = R[d];
                        R[d] <<= 1;
                        preMismatchTmp = R[d];
                        R[d] |= currentPatternMask;
                        R[d] &= insertionTmp & mismatchTmp & (deletionTmp << 1);
                        if (0 == (R[d] & matchingMask)) {
                            errors = d;
                            current = i - 1;
                            return i;
                        }
                        deletionTmp = R[d];
                        insertionTmp = preInsertionTmp;
                        mismatchTmp = preMismatchTmp;
                    }
                }
                current = to;
                return -1;
            }
        };
    }
}

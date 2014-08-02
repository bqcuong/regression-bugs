package com.milaboratory.core.motif;

import java.util.Arrays;

import static java.lang.System.arraycopy;

public final class BitapMatcherFilter implements BitapMatcher {
    final BitapMatcher nestedMatcher;
    final int[] positionsBuffer;
    final int[] errorsBuffer;
    //int currentIndex = -1;

    public BitapMatcherFilter(BitapMatcher nestedMatcher) {
        this.nestedMatcher = nestedMatcher;
        this.positionsBuffer = new int[3];
        Arrays.fill(this.positionsBuffer, -1);
        this.errorsBuffer = new int[3];
        Arrays.fill(this.errorsBuffer, -1);
        next();
    }

    private void next() {
        arraycopy(positionsBuffer, 1, positionsBuffer, 0, 2);
        arraycopy(errorsBuffer, 1, errorsBuffer, 0, 2);
        int pos = nestedMatcher.findNext();
        if (pos == -1) {
            positionsBuffer[2] = -1;
            errorsBuffer[2] = -1;
        } else {
            positionsBuffer[2] = pos;
            errorsBuffer[2] = nestedMatcher.getNumberOfErrors();
        }
    }

    @Override
    public int findNext() {
        while (true) {
            next();
            if (positionsBuffer[0] != -1 &&
                    Math.abs(positionsBuffer[0] - positionsBuffer[1]) == 1
                    && errorsBuffer[0] + 1 == errorsBuffer[1])
                continue;
            if (positionsBuffer[2] != -1 &&
                    Math.abs(positionsBuffer[1] - positionsBuffer[2]) == 1
                    && errorsBuffer[1] == errorsBuffer[2] + 1)
                continue;
            return positionsBuffer[1];
        }
    }

    @Override
    public int getNumberOfErrors() {
        return errorsBuffer[1];
    }
}

package com.milaboratory.core.motif;

public class BitapMatcherFilter implements BitapMatcher {
    final BitapMatcher nestedMatcher;
    int previousPosition;
    int previousErrors = -1;

    public BitapMatcherFilter(BitapMatcher nestedMatcher) {
        this.nestedMatcher = nestedMatcher;
    }

    @Override
    public int findNext() {
        //if (previousErrors == -1) {
        //    do{
        //    previousPosition = nestedMatcher.findNext();
        //    while (pre`)
        //}
        return 0;
    }

    @Override
    public int getNumberOfErrors() {
        return nestedMatcher.getNumberOfErrors();
    }
}

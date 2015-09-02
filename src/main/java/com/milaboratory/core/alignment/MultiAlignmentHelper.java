/*
 * Copyright 2015 MiLaboratory.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.milaboratory.core.alignment;

import com.milaboratory.core.Range;
import com.milaboratory.core.mutations.MutationType;
import com.milaboratory.core.mutations.Mutations;
import com.milaboratory.core.sequence.Alphabet;
import com.milaboratory.core.sequence.Sequence;
import com.milaboratory.util.BitArray;
import com.milaboratory.util.IntArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.milaboratory.core.mutations.Mutation.RAW_MUTATION_TYPE_DELETION;
import static com.milaboratory.core.mutations.Mutation.RAW_MUTATION_TYPE_SUBSTITUTION;

public class MultiAlignmentHelper {
    final String subject;
    final String[] queries;
    final int[] subjectPositions;
    final int[][] queryPositions;
    final BitArray[] match;

    String subjectTitle;
    final String[] queryTitles;

    private MultiAlignmentHelper(String subject, String[] queries, int[] subjectPositions, int[][] queryPositions,
                                 BitArray[] match) {
        this(subject, queries, subjectPositions, queryPositions, match, "", new String[queries.length]);
    }

    public MultiAlignmentHelper(String subject, String[] queries, int[] subjectPositions,
                                int[][] queryPositions, BitArray[] match, String subjectTitle,
                                String[] queryTitles) {
        this.subject = subject;
        this.queries = queries;
        this.subjectPositions = subjectPositions;
        this.queryPositions = queryPositions;
        this.match = match;
        this.subjectTitle = subjectTitle;
        this.queryTitles = queryTitles;
    }

    public MultiAlignmentHelper setSubjectTitle(String subjectTitle) {
        this.subjectTitle = subjectTitle;
        return this;
    }

    public MultiAlignmentHelper setQueryTitle(int id, String queryTitle) {
        this.queryTitles[id] = queryTitle;
        return this;
    }

    public int getSubjectFrom() {
        return getFirstPosition(subjectPositions);
    }

    public int getSubjectTo() {
        return getLastPosition(subjectPositions);
    }

    public int getQueryFrom(int index) {
        return getFirstPosition(queryPositions[index]);
    }

    public int getQueryTo(int index) {
        return getLastPosition(queryPositions[index]);
    }

    public int size() {
        return subject.length();
    }

    public MultiAlignmentHelper getRange(int from, int to) {
        boolean[] queriesToExclude = new boolean[queries.length];
        int queriesCount = 0;
        for (int i = 0; i < queries.length; i++) {
            boolean exclude = true;
            for (int j = from; j < to; j++)
                if (queryPositions[i][j] != -1) {
                    exclude = false;
                    break;
                }
            queriesToExclude[i] = exclude;
            if (!exclude)
                queriesCount++;
        }

        String[] cQueries = new String[queriesCount];
        int[][] cQueryPositions = new int[queriesCount][];
        BitArray[] cMatch = new BitArray[queriesCount];
        String[] cQueryTitles = new String[queriesCount];

        int j = 0;
        for (int i = 0; i < queries.length; i++) {
            if (queriesToExclude[i])
                continue;
            cQueries[j] = queries[i].substring(from, to);
            cQueryPositions[j] = Arrays.copyOfRange(queryPositions[i], from, to);
            cMatch[j] = match[i].getRange(from, to);
            cQueryTitles[j] = queryTitles[i];
            j++;
        }

        return new MultiAlignmentHelper(subject.substring(from, to), cQueries,
                Arrays.copyOfRange(subjectPositions, from, to), cQueryPositions, cMatch,
                subjectTitle, cQueryTitles);
    }

    public MultiAlignmentHelper[] split(int length) {
        MultiAlignmentHelper[] ret = new MultiAlignmentHelper[(size() + length - 1) / length];
        for (int i = 0; i < ret.length; ++i) {
            int pointer = i * length;
            int l = Math.min(length, size() - pointer);
            ret[i] = getRange(pointer, pointer + l);
        }
        return ret;
    }

    private static int getFirstPosition(int[] array) {
        for (int pos : array)
            if (pos >= 0)
                return pos;
        for (int pos : array)
            if (pos < -1)
                return -2 - pos;
        return -1;
    }

    private static int getLastPosition(int[] array) {
        for (int i = array.length - 1; i >= 0; i--)
            if (array[i] >= 0)
                return array[i];
        for (int i = array.length - 1; i >= 0; i--)
            if (array[i] < -1)
                return -2 - array[i];
        return -1;
    }

    private static void fixedWidth(String[] strings) {
        int length = 0;
        for (String string : strings)
            length = Math.max(length, string.length());
        for (int i = 0; i < strings.length; i++)
            strings[i] = spaces(length - strings[i].length()) + strings[i];
    }

    public static class Settings {
        public final boolean markMatchWithSpecialLetter;
        public final boolean lowerCaseMatch;
        public final boolean lowerCaseMismatch;
        public final char matchChar;
        public final char outOfRangeChar;

        public Settings(boolean markMatchWithSpecialLetter, boolean lowerCaseMatch, boolean lowerCaseMismatch, char matchChar, char outOfRangeChar) {
            this.markMatchWithSpecialLetter = markMatchWithSpecialLetter;
            this.lowerCaseMatch = lowerCaseMatch;
            this.lowerCaseMismatch = lowerCaseMismatch;
            this.matchChar = matchChar;
            this.outOfRangeChar = outOfRangeChar;
        }
    }

    @Override
    public String toString() {
        int aCount = queries.length;
        String[] leftColumn = new String[queries.length + 1];

        leftColumn[0] = "" + getSubjectFrom();
        for (int i = 0; i < aCount; i++)
            leftColumn[i + 1] = "" + getQueryFrom(i);

        fixedWidth(leftColumn);

        leftColumn[0] = (subjectTitle == null ? "" : subjectTitle) +
                " " + leftColumn[0];

        for (int i = 0; i < aCount; i++)
            leftColumn[i + 1] = (queryTitles[i] == null ? "" : queryTitles[i]) +
                    " " + leftColumn[i + 1];

        fixedWidth(leftColumn);

        StringBuilder result = new StringBuilder();
        result.append(leftColumn[0]).append(" ").append(subject).append(" ").append(getSubjectTo());
        for (int i = 0; i < aCount; i++) {
            result.append('\n').append(leftColumn[i + 1]).append(" ").append(queries[i]).append(" ").append(getQueryTo(i));
        }
        return result.toString();
    }

    public static final Settings DEFAULT_SETTINGS = new Settings(false, true, false, '.', ' ');
    public static final Settings DOT_MATCH_SETTINGS = new Settings(true, true, false, '.', ' ');

    public static <S extends Sequence<S>> MultiAlignmentHelper build(Settings settings, Range subjectRange,
                                                                     Alignment<S>... alignments) {
        S subject = alignments[0].getSequence1();

        for (Alignment<S> alignment : alignments)
            if (!alignment.getSequence1().equals(subject))
                throw new IllegalArgumentException();

        int subjectPointer = subjectRange.getFrom();
        int subjectPointerTo = subjectRange.getTo();

        int aCount = alignments.length;
        int[] queryPointers = new int[aCount];
        int[] mutationPointers = new int[aCount];
        Mutations<S>[] mutations = new Mutations[aCount];
        List<Boolean>[] matches = new List[aCount];

        IntArrayList subjectPositions = new IntArrayList();
        IntArrayList[] queryPositions = new IntArrayList[aCount];

        StringBuilder subjectString = new StringBuilder();
        StringBuilder[] queryStrings = new StringBuilder[aCount];

        for (int i = 0; i < aCount; i++) {
            queryPointers[i] = alignments[i].getSequence2Range().getFrom();
            matches[i] = new ArrayList<>();
            mutations[i] = alignments[i].getAbsoluteMutations();
            queryPositions[i] = new IntArrayList();
            queryStrings[i] = new StringBuilder();
        }

        final Alphabet<S> alphabet = subject.getAlphabet();

        BitArray processed = new BitArray(aCount);
        while (true) {
            // Checking continue condition
            boolean doContinue = subjectPointer < subjectPointerTo;
            for (int i = 0; i < aCount; i++)
                doContinue |= mutationPointers[i] < mutations[i].size();
            if (!doContinue)
                break;

            processed.clearAll();

            // Checking for insertions
            boolean insertion = false;
            for (int i = 0; i < aCount; i++)
                if (mutationPointers[i] < mutations[i].size() &&
                        mutations[i].getTypeByIndex(mutationPointers[i]) == MutationType.Insertion &&
                        mutations[i].getPositionByIndex(mutationPointers[i]) == subjectPointer) {
                    insertion = true;
                    queryStrings[i].append(mutations[i].getToAsSymbolByIndex(mutationPointers[i]));
                    queryPositions[i].add(queryPointers[i]++);
                    matches[i].add(false);
                    mutationPointers[i]++;
                    processed.set(i);
                }

            // Processing out of range sequences
            for (int i = 0; i < aCount; i++)
                if (!alignments[i].getSequence1Range().containsBoundary(subjectPointer)) {
                    queryStrings[i].append(settings.outOfRangeChar);
                    queryPositions[i].add(-1);
                    matches[i].add(false);
                    assert !processed.get(i);
                    processed.set(i);
                }

            if (insertion) { // In case on insertion in query sequence
                subjectString.append('-');
                subjectPositions.add(-2 - subjectPointer);

                for (int i = 0; i < aCount; i++) {
                    if (!processed.get(i)) {
                        queryStrings[i].append('-');
                        queryPositions[i].add(-2 - queryPointers[i]);
                        matches[i].add(false);
                    }
                }
            } else { // In other cases
                char subjectSymbol = subject.symbolAt(subjectPointer);
                subjectString.append(subjectSymbol);
                subjectPositions.add(subjectPointer);

                for (int i = 0; i < aCount; i++) {
                    if (processed.get(i))
                        continue;

                    Mutations<S> cMutations = mutations[i];
                    int cMutationPointer = mutationPointers[i];

                    boolean mutated = false;

                    if (cMutationPointer < cMutations.size()) {
                        int mutPosition = cMutations.getPositionByIndex(cMutationPointer);
                        assert mutPosition >= subjectPointer;
                        mutated = mutPosition == subjectPointer;
                    }

                    if (mutated) {
                        switch (cMutations.getRawTypeByIndex(cMutationPointer)) {
                            case RAW_MUTATION_TYPE_SUBSTITUTION:
                                char symbol = cMutations.getToAsSymbolByIndex(cMutationPointer);
                                queryStrings[i].append(settings.lowerCaseMismatch ?
                                        Character.toLowerCase(symbol) :
                                        symbol);
                                queryPositions[i].add(queryPointers[i]++);
                                matches[i].add(false);
                                break;
                            case RAW_MUTATION_TYPE_DELETION:
                                queryStrings[i].append('-');
                                queryPositions[i].add(-2 - queryPointers[i]);
                                matches[i].add(false);
                                break;
                            default:
                                assert false;
                        }
                        mutationPointers[i]++;
                    } else {
                        if (settings.markMatchWithSpecialLetter)
                            queryStrings[i].append(settings.matchChar);
                        else
                            queryStrings[i].append(settings.lowerCaseMatch ? Character.toLowerCase(subjectSymbol) :
                                    subjectSymbol);
                        queryPositions[i].add(queryPointers[i]++);
                        matches[i].add(true);
                    }
                }
                subjectPointer++;
            }
        }

        int[][] queryPositionsArrays = new int[aCount][];
        BitArray[] matchesBAs = new BitArray[aCount];
        String[] queryStringsArray = new String[aCount];
        for (int i = 0; i < aCount; i++) {
            queryPositionsArrays[i] = queryPositions[i].toArray();
            matchesBAs[i] = new BitArray(matches[i]);
            queryStringsArray[i] = queryStrings[i].toString();
        }

        return new MultiAlignmentHelper(subjectString.toString(), queryStringsArray, subjectPositions.toArray(),
                queryPositionsArrays, matchesBAs);
    }

    private static String spaces(int n) {
        char[] c = new char[n];
        Arrays.fill(c, ' ');
        return String.valueOf(c);
    }
}
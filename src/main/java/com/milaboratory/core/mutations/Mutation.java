package com.milaboratory.core.mutations;

import com.milaboratory.core.sequence.Alphabet;
import com.milaboratory.util.IntArrayList;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Mutation {
    public static final int RAW_MUTATION_TYPE_SUBSTITUTION = 0x20,
            RAW_MUTATION_TYPE_DELETION = 0x40,
            RAW_MUTATION_TYPE_INSERTION = 0x60,
            MUTATION_TYPE_MASK = 0x60,
            LETTER_MASK = 0x1F,
            FROM_OFFSET = 7,
            POSITION_OFFSET = 12,
            MAX_POSITION_VALUE = 0xFFFFF,
            NON_MUTATION = 0,
            MUTATION_TYPE_OFFSET = 5;

    private Mutation() {
    }

    public static int createInsertion(int position, int to) {
        return createMutation(RAW_MUTATION_TYPE_INSERTION, position, 0, to);
    }

    public static int createDeletion(int position, int from) {
        return createMutation(RAW_MUTATION_TYPE_DELETION, position, from, 0);
    }

    public static int createSubstitution(int position, int from, int to) {
        return createMutation(RAW_MUTATION_TYPE_SUBSTITUTION, position, from, to);
    }

    public static int createMutation(MutationType type, int from, int to) {
        return createMutation(type, 0, from, to);
    }

    public static int createMutation(int rawType, int from, int to) {
        return createMutation(rawType, 0, from, to);
    }

    public static int createMutation(MutationType type, int position, int from, int to) {
        if (type == null)
            throw new NullPointerException();

        return createMutation(type.rawType, position, from, to);
    }

    public static int createMutation(int rawType, int position, int from, int to) {
        if (position < 0 || position > MAX_POSITION_VALUE)
            throw new IllegalArgumentException();

        return (position << POSITION_OFFSET) | (from << FROM_OFFSET) | rawType | to;
    }

    public static int getPosition(int code) {
        return code >>> POSITION_OFFSET;
    }

    public static int getFrom(int code) {
        return (code >> FROM_OFFSET) & LETTER_MASK;
    }

    public static int getTo(int code) {
        return code & LETTER_MASK;
    }

    /**
     * Returns: 0x20 for substitution, 0x40 for Deletion, 0x60 for insertion.
     *
     * @param code mutation code form mutations array returned by {@link com.milaboratory.core.sequence.alignment.GlobalAligner#align(com.milaboratory.core.sequence.alignment.AlignmentScoring,
     *             com.milaboratory.core.sequence.Sequence, com.milaboratory.core.sequence.Sequence)} method.
     * @return 0x20 for substitution, 0x40 for Deletion, 0x60 for insertion
     */
    public static int getRawTypeCode(int code) {
        return code & MUTATION_TYPE_MASK;
    }

    public static MutationType getType(int code) {
        switch (code & MUTATION_TYPE_MASK) {
            case RAW_MUTATION_TYPE_SUBSTITUTION:
                return MutationType.Substitution;
            case RAW_MUTATION_TYPE_DELETION:
                return MutationType.Deletion;
            case RAW_MUTATION_TYPE_INSERTION:
                return MutationType.Insertion;
            default:
                return null;
        }
    }

    public static boolean isSubstitution(int code) {
        return (code & MUTATION_TYPE_MASK) == RAW_MUTATION_TYPE_SUBSTITUTION;
    }

    public static boolean isInsertion(int code) {
        return (code & MUTATION_TYPE_MASK) == RAW_MUTATION_TYPE_INSERTION;
    }

    public static boolean isDeletion(int code) {
        return (code & MUTATION_TYPE_MASK) == RAW_MUTATION_TYPE_DELETION;
    }

    public static boolean isInDel(int code) {
        final int m = (code & MUTATION_TYPE_MASK);
        return m == RAW_MUTATION_TYPE_DELETION || m == RAW_MUTATION_TYPE_INSERTION;
    }

    public static int move(int mutation, int offset) {
        return mutation + (offset << POSITION_OFFSET);
    }

    public static String toString(Alphabet alphabet, int mutation) {
        switch (mutation & MUTATION_TYPE_MASK) {
            case RAW_MUTATION_TYPE_SUBSTITUTION:
                return "S" + (mutation >>> POSITION_OFFSET) + ":" +
                        alphabet.symbolFromCode((byte) ((mutation >> FROM_OFFSET) & LETTER_MASK)) +
                        "->" + alphabet.symbolFromCode((byte) (mutation & LETTER_MASK));
            case RAW_MUTATION_TYPE_DELETION:
                return "D" + (mutation >>> POSITION_OFFSET) + ":" +
                        alphabet.symbolFromCode((byte) ((mutation >> FROM_OFFSET) & LETTER_MASK));
            case RAW_MUTATION_TYPE_INSERTION:
                return "I" + (mutation >>> POSITION_OFFSET) + ":" + alphabet.symbolFromCode((byte) (mutation & LETTER_MASK));
        }
        return null;
    }

//    public static <S extends Sequence> int[] extractSubstitutions(S from, S to) {
//        if (from.getAlphabet() != to.getAlphabet())
//            throw new IllegalArgumentException();
//
//        if (from.size() != to.size())
//            throw new IllegalArgumentException("Size of 'from' and 'to' sequences must be the same.");
//
//        IntArrayList list = new IntArrayList();
//
//        for (int i = 0; i < from.size(); ++i)
//            if (from.codeAt(i) != to.codeAt(i))
//                list.add(createSubstitution(i, from.codeAt(i), to.codeAt(i)));
//
//        return list.toArray();
//    }
//
//
//

//
//    public static <T extends Sequence> void printAlignment(T initialSequence, int[] mutations) {
//        System.out.print(printAlignmentToString(initialSequence, mutations));
//    }
//
//    /**
//     * Calculates score of alignments.
//     *
//     * @param scoring       scoring
//     * @param initialLength length of initial sequence (before mutations; upper line of alignment)
//     * @param mutations     array of mutations
//     * @return score of alignment
//     */
//    public static float calculateScore(LinearGapAlignmentScoring scoring, int initialLength, int[] mutations) {
//        if (!scoring.hasUniformMatchScore())
//            throw new IllegalArgumentException("Scoring with non-uniform match score is not supported.");
//
//        float matchScore = scoring.getScore((byte) 0, (byte) 0);
//        float score = matchScore * initialLength;
//        for (int mutation : mutations) {
//            if (isDeletion(mutation) || isInsertion(mutation))
//                score += scoring.getGapPenalty();
//            else //Substitution
//                score += scoring.getScore((byte) getFrom(mutation), (byte) getTo(mutation));
//
//            if (isDeletion(mutation) || isSubstitution(mutation))
//                score -= matchScore;
//        }
//
//        return score;
//    }

//
//    public static <T extends Sequence> String printAlignmentToString(T initialSequence, int[] mutations) {
//        int pointer = 0;
//        int mutPointer = 0;
//        int mut;
//        final Alphabet alphabet = initialSequence.getAlphabet();
//        StringBuilder sb1 = new StringBuilder(),
//                sb2 = new StringBuilder();
//        while (pointer < initialSequence.size() || mutPointer < mutations.length) {
//            if (mutPointer < mutations.length && ((mut = mutations[mutPointer]) >>> POSITION_OFFSET) <= pointer)
//                switch (mut & MUTATION_TYPE_MASK) {
//                    case RAW_MUTATION_TYPE_SUBSTITUTION:
//                        if (((mut >> FROM_OFFSET) & LETTER_MASK) != initialSequence.codeAt(pointer))
//                            throw new IllegalArgumentException("Mutation = " + toString(initialSequence.getAlphabet(), mut) +
//                                    " but seq[" + pointer + "]=" + initialSequence.charFromCodeAt(pointer));
//                        sb1.append(Character.toLowerCase(initialSequence.charFromCodeAt(pointer++)));
//                        sb2.append(Character.toLowerCase(alphabet.symbolFromCode((byte) (mut & LETTER_MASK))));
//                        ++mutPointer;
//                        break;
//                    case RAW_MUTATION_TYPE_DELETION:
//                        if (((mut >> FROM_OFFSET) & LETTER_MASK) != initialSequence.codeAt(pointer))
//                            throw new IllegalArgumentException("Mutation = " + toString(initialSequence.getAlphabet(), mut) +
//                                    " but seq[" + pointer + "]=" + initialSequence.charFromCodeAt(pointer));
//                        sb1.append(initialSequence.charFromCodeAt(pointer++));
//                        sb2.append("-");
//                        ++mutPointer;
//                        break;
//                    case RAW_MUTATION_TYPE_INSERTION:
//                        sb1.append("-");
//                        sb2.append(alphabet.symbolFromCode((byte) (mut & LETTER_MASK)));
//                        ++mutPointer;
//                        break;
//                }
//            else {
//                sb1.append(initialSequence.charFromCodeAt(pointer));
//                sb2.append(initialSequence.charFromCodeAt(pointer++));
//            }
//        }
//
//        return sb1.toString() + "\n" + sb2.toString() + '\n';
//    }
//
//    public static int[] generateMutations(NucleotideSequence sequence, NucleotideMutationModel model) {
//        IntArrayList result = new IntArrayList();
//        int mut, previous = NON_MUTATION;
//        for (int i = 0; i < sequence.size(); ++i) {
//            mut = model.mutation(i, sequence.codeAt(i));
//            if (mut != NON_MUTATION) {
//                switch (getRawTypeCode(mut)) {
//                    case RAW_MUTATION_TYPE_SUBSTITUTION:
//                        result.add(mut);
//                        break;
//                    case RAW_MUTATION_TYPE_DELETION:
//                        if (getRawTypeCode(previous) == RAW_MUTATION_TYPE_INSERTION)
//                            mut = NON_MUTATION;
//                        else
//                            result.add(mut);
//                        break;
//                    case RAW_MUTATION_TYPE_INSERTION:
//                        if (getRawTypeCode(previous) == RAW_MUTATION_TYPE_DELETION)
//                            mut = NON_MUTATION;
//                        else {
//                            result.add(mut);
//                            --i;
//                        }
//                        break;
//                }
//            }
//            previous = mut;
//        }
//
//        mut = model.mutation(sequence.size(), -1);
//        if (getRawTypeCode(mut) == RAW_MUTATION_TYPE_INSERTION &&
//                getRawTypeCode(previous) != RAW_MUTATION_TYPE_DELETION)
//            result.add(mut);
//
//        return result.toArray();
//    }
//


    static final Map<Alphabet, Pattern> mutationPatterns = Collections.synchronizedMap(new HashMap<Alphabet, Pattern>());

    static Pattern getMutationPatternForAlphabet(Alphabet alphabet) {
        Pattern pattern = mutationPatterns.get(alphabet);
        if (pattern == null) {
            StringBuilder sb = new StringBuilder();
            StringBuilder t = new StringBuilder("([\\Q");
            for (byte i = 0; i < alphabet.size(); ++i)
                t.append(alphabet.symbolFromCode(i));
            t.append("\\E])");
            sb.append("S").append(t).append("(\\d+)").append(t);
            sb.append("|");
            sb.append("D").append(t).append("(\\d+)");
            sb.append("|");
            sb.append("I").append("(\\d+)").append(t);
            mutationPatterns.put(alphabet, pattern = Pattern.compile(sb.toString()));
        }
        return pattern;
    }

    /**
     * Encodes mutations in compact human-readable string, that can be decoded by method {@link #decode(String,
     * com.milaboratory.core.sequence.Alphabet)}. <p/> <p>For format see {@link #encode(int,
     * com.milaboratory.core.sequence.Alphabet)}.</p> <p/> <p>Mutations are just concatenated. The following RegExp can
     * be used for simple parsing of resulting string for nucleotide sequences: {@code ([SDI])([ATGC]?)(\d+)([ATGC]?)}
     * .</p>
     *
     * @param mutations mutations to encode
     * @return mutations in a human-readable string
     */
    public static String encode(int[] mutations, Alphabet alphabet) {
        StringBuilder builder = new StringBuilder();
        for (int mut : mutations)
            builder.append(encode(mut, alphabet));
        return builder.toString();
    }

    /**
     * Encodes single mutation in compact human-readable string, that can be decoded by method {@link #decode(String,
     * com.milaboratory.core.sequence.Alphabet)}. <p/> <p>The format is following: <ul> <p/> <li><b>Substitution</b>:
     * starts with {@code S} then nucleotide in initial sequence encoded in one letter (<b>from</b>) then
     * <b>position</b> then resulting nucleotide (<b>to</b>) encoded in one letter. (Example: {@code SA12T} =
     * substitution from A to T at position 12).</li> <p/> <li><b>Deletion</b>: starts with {@code D} then nucleotide
     * that was deleted encoded in one letter (<b>from</b>) then <b>position</b>. (Example: {@code DG43} = G deleted at
     * position 43).</li> <p/> <li><b>Insertion</b>: starts with {@code I} then <b>position</b> then inserted letter
     * <b>to</b>. (Example: {@code I54C} = C inserted before letter at position 54).</li> <p/> </ul> </p>
     *
     * @param mutation mutation to encode
     * @return mutation in a human-readable format
     */
    public static String encode(int mutation, Alphabet alphabet) {
        switch (mutation & MUTATION_TYPE_MASK) {
            case RAW_MUTATION_TYPE_SUBSTITUTION:
                return "S" + alphabet.symbolFromCode((byte) getFrom(mutation)) + Integer.toString(getPosition(mutation)) + alphabet.symbolFromCode((byte) getTo(mutation));
            case RAW_MUTATION_TYPE_DELETION:
                return "D" + alphabet.symbolFromCode((byte) getFrom(mutation)) + Integer.toString(getPosition(mutation));
            case RAW_MUTATION_TYPE_INSERTION:
                return "I" + Integer.toString(getPosition(mutation)) + alphabet.symbolFromCode((byte) getTo(mutation));
        }
        throw new IllegalArgumentException("Illegal mutation code.");
    }

    /**
     * Decodes mutations encoded using format described in {@link #encode(int, com.milaboratory.core.sequence.Alphabet)}.
     *
     * @param mutations
     * @return
     */
    public static int[] decode(String mutations, Alphabet alphabet) {
        Matcher matcher = getMutationPatternForAlphabet(alphabet).matcher(mutations);
        IntArrayList list = new IntArrayList();
        while (matcher.find()) {
            switch (matcher.group(0).charAt(0)) {
                case 'S':
                    list.add(createSubstitution(Integer.parseInt(matcher.group(2)),
                            alphabet.codeFromSymbol(matcher.group(1).charAt(0)),
                            alphabet.codeFromSymbol(matcher.group(3).charAt(0))));
                    break;
                case 'D':
                    list.add(createDeletion(Integer.parseInt(matcher.group(5)),
                            alphabet.codeFromSymbol(matcher.group(4).charAt(0))));
                    break;
                case 'I':
                    list.add(createInsertion(Integer.parseInt(matcher.group(6)),
                            alphabet.codeFromSymbol(matcher.group(7).charAt(0))));
                    break;
            }
        }
        return list.toArray();
    }
}

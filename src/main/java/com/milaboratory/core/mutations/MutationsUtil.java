package com.milaboratory.core.mutations;

import com.milaboratory.core.sequence.Sequence;

import static com.milaboratory.core.mutations.Mutation.*;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */

public final class MutationsUtil {
    private MutationsUtil() {
    }

    /**
     * This one shifts indels to the left at homopolymer regions Applicable to KAligner data, which normally put indels
     * randomly along such regions Required for filterMutations algorithm to work correctly Works inplace
     *
     * @param seq       reference sequence for the mutations
     * @param mutations array of mutations
     */
    public static void shiftIndelsAtHomopolymers(Sequence seq, int[] mutations) {
        int prevPos = 0;

        for (int i = 0; i < mutations.length; i++) {
            int code = mutations[i];
            if (!isSubstitution(code)) {
                int pos = getPosition(code), offset = 0;
                int nt = isDeletion(code) ? getFrom(code) : getTo(code);
                while (pos > prevPos && seq.codeAt(pos - 1) == nt) {
                    pos--;
                    offset--;
                }
                mutations[i] = move(code, offset);
                prevPos = getPosition(mutations[i]);
                if (isDeletion(mutations[i]))
                    prevPos++;
            } else {
                prevPos = getPosition(mutations[i]) + 1;
            }
        }
    }

    public static boolean check(Mutations mutations) {
        return check(mutations.mutations);
    }

    //TODO add more checks
    public static boolean check(int[] mutations) {
        for (int i = 0; i < mutations.length; ++i) {
            if (i > 0) {
                if (isDeletion(mutations[i - 1]) && isInsertion(mutations[i]) &&
                        getPosition(mutations[i - 1]) == getPosition(mutations[i]) - 1)
                    return false;
                if (isDeletion(mutations[i]) && isInsertion(mutations[i - 1]) &&
                        getPosition(mutations[i - 1]) == getPosition(mutations[i]))
                    return false;
            }
            if (isSubstitution(mutations[i]) && getFrom(mutations[i]) == getTo(mutations[i]))
                return false;
        }
        return true;
    }

    public static boolean isSorted(int[] mutations) {
        if (mutations.length == 0)
            return true;
        int position = getPosition(mutations[0]);
        int p;
        for (int i = 1; i < mutations.length; ++i) {
            if ((p = getPosition(mutations[i])) < position)
                return false;
            position = p;
        }
        return true;
    }

    public static <S extends Sequence> boolean isCompatibleWithSequence(S sequence, int[] mutations) {
        for (int mutation : mutations) {
            int position = getPosition(mutation);
            if (isInsertion(mutation)) {
                if (position >= sequence.size() + 1)
                    return false;
            } else if (position >= sequence.size() || sequence.codeAt(position) != getFrom(mutation))
                return false;
        }
        return true;
    }
}

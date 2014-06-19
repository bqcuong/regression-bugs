package com.milaboratory.core.alignment;

import com.milaboratory.core.mutations.Mutation;
import com.milaboratory.core.mutations.Mutations;
import com.milaboratory.core.sequence.Alphabet;
import com.milaboratory.core.sequence.Sequence;
import com.milaboratory.util.BitArray;
import com.milaboratory.util.IntArrayList;

import java.util.ArrayList;
import java.util.List;

import static com.milaboratory.core.mutations.Mutation.*;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public final class Alignment<S extends Sequence> {
    final S sequence;
    final Mutations<S> mutations;
    final int from, to;

    public Alignment(S sequence, Mutations<S> mutations, int from, int to) {
        if (!mutations.isCompatibleWith(sequence)
                || from > mutations.minPosition() || to < mutations.maxPosition())
            throw new IllegalArgumentException("Not compatible mutations with sequence.");
        this.sequence = sequence;
        this.mutations = mutations;
        this.from = from;
        this.to = to;
    }

    public Alignment(S sequence, Mutations<S> mutations) {
        this(sequence, mutations, 0, sequence.size());
    }

    public S getSequence() {
        return sequence;
    }

    public Mutations<S> getGlobalMutations() {
        return mutations;
    }

    public Mutations<S> getLocalMutations() {
        return mutations.move(-from);
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public float similarity() {
        int match = 0, mismatch = 0;
        int pointer = from;
        int mutPointer = 0;
        int mut;
        while (pointer < to || mutPointer < mutations.size())
            if (mutPointer < mutations.size() && ((mut = mutations.getMutation(mutPointer)) >>> POSITION_OFFSET) <= pointer)
                switch (mut & MUTATION_TYPE_MASK) {
                    case RAW_MUTATION_TYPE_SUBSTITUTION:
                        pointer++;
                        ++mutPointer;
                        ++mismatch;
                        break;
                    case RAW_MUTATION_TYPE_DELETION:
                        pointer++;
                        ++mutPointer;
                        ++mismatch;
                        break;
                    case RAW_MUTATION_TYPE_INSERTION:
                        ++mutPointer;
                        ++mismatch;
                        break;
                }
            else {
                ++match;
                ++pointer;
            }
        return 1.0f * match / (match + mismatch);
    }

    public AlignmentHelper getAlignmentHelper() {
        int pointer = from;
        int pointer2 = 0;
        int mutPointer = 0;
        int mut;
        final Alphabet<S> alphabet = sequence.getAlphabet();
        List<Boolean> matches = new ArrayList<>();
        IntArrayList pos1 = new IntArrayList(sequence.size() + mutations.size()),
                pos2 = new IntArrayList(sequence.size() + mutations.size());
        StringBuilder sb1 = new StringBuilder(),
                sb2 = new StringBuilder();
        while (pointer < sequence.size() || mutPointer < mutations.size()) {
            if (mutPointer < mutations.size() && ((mut = mutations.getMutation(mutPointer)) >>> POSITION_OFFSET) <= pointer)
                switch (mut & MUTATION_TYPE_MASK) {
                    case RAW_MUTATION_TYPE_SUBSTITUTION:
                        if (((mut >> FROM_OFFSET) & LETTER_MASK) != sequence.codeAt(pointer))
                            throw new IllegalArgumentException("Mutation = " + Mutation.toString(sequence.getAlphabet(), mut) +
                                    " but seq[" + pointer + "]=" + sequence.charFromCodeAt(pointer));
                        pos1.add(pointer);
                        pos2.add(pointer2++);
                        sb1.append(sequence.charFromCodeAt(pointer++));
                        sb2.append(alphabet.symbolFromCode((byte) (mut & LETTER_MASK)));
                        matches.add(false);
                        ++mutPointer;
                        break;
                    case RAW_MUTATION_TYPE_DELETION:
                        if (((mut >> FROM_OFFSET) & LETTER_MASK) != sequence.codeAt(pointer))
                            throw new IllegalArgumentException("Mutation = " + Mutation.toString(alphabet, mut) +
                                    " but seq[" + pointer + "]=" + sequence.charFromCodeAt(pointer));
                        pos1.add(pointer);
                        pos2.add(-1 - pointer2);
                        sb1.append(sequence.charFromCodeAt(pointer++));
                        sb2.append("-");
                        matches.add(false);
                        ++mutPointer;
                        break;
                    case RAW_MUTATION_TYPE_INSERTION:
                        pos1.add(-1 - pointer);
                        pos2.add(pointer2++);
                        sb1.append("-");
                        sb2.append(alphabet.symbolFromCode((byte) (mut & LETTER_MASK)));
                        matches.add(false);
                        ++mutPointer;
                        break;
                }
            else {
                pos1.add(pointer);
                pos2.add(pointer2++);
                sb1.append(sequence.charFromCodeAt(pointer));
                sb2.append(sequence.charFromCodeAt(pointer++));
                matches.add(true);
            }
        }
        return new AlignmentHelper(sb1.toString(), sb2.toString(), pos1.toArray(), pos2.toArray(),
                new BitArray(matches));
    }
}

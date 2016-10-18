/*
 * Copyright 2016 MiLaboratory.com
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
import com.milaboratory.core.mutations.Mutation;
import com.milaboratory.core.mutations.Mutations;
import com.milaboratory.core.sequence.Sequence;

import static com.milaboratory.core.mutations.Mutation.*;

/**
 * Iterates over all positions in alignment
 *
 * @param <S> sequence type
 */
public final class AlignmentIterator<S extends Sequence<S>> {
    final Mutations<S> mutations;
    private final int seq1To;
    public int seq1Position, seq2Position;
    /**
     * Points to current mutation or if currentMutation == {@link Mutation#NON_MUTATION} points to the next mutation.
     */
    public int mutationsPointer = 0;
    /**
     * Current mutation or {@link Mutation#NON_MUTATION} if on match
     */
    public int currentMutation;

    /**
     * Create alignment iterator
     *
     * @param mutations mutations (alignment)
     * @param seq1Range aligned range
     */
    public AlignmentIterator(final Mutations<S> mutations, final Range seq1Range) {
        this(mutations, seq1Range, 0);
    }

    /**
     * Create alignment iterator
     *
     * @param mutations    mutations (alignment)
     * @param seq1Range    aligned range
     * @param seq2Position seq2 start position
     */
    public AlignmentIterator(final Mutations<S> mutations, final Range seq1Range, int seq2Position) {
        this.mutations = mutations;
        this.seq1To = seq1Range.getTo();
        this.seq1Position = seq1Range.getFrom();
        this.currentMutation = NON_MUTATION_1;
        this.seq2Position = seq2Position;
    }

    /**
     * Advance to the next alignment position
     *
     * @return {@literal true} if iteration successful; {@literal false} if iteration ended
     */
    public boolean advance() {
        if (currentMutation != NON_MUTATION_1) {

            // This will be executed starting from the second call to the method
            if (currentMutation != NON_MUTATION) {
                switch (Mutation.getRawTypeCode(currentMutation)) {
                    case RAW_MUTATION_TYPE_SUBSTITUTION:
                        ++seq1Position;
                        ++seq2Position;
                        break;

                    case RAW_MUTATION_TYPE_DELETION:
                        ++seq1Position;
                        break;

                    case RAW_MUTATION_TYPE_INSERTION:
                        ++seq2Position;
                        break;
                }
                ++mutationsPointer;
            } else if (seq1Position < seq1To) {
                ++seq1Position;
                ++seq2Position;
            } else
                ++seq1Position;
        }

        // Setting current state
        currentMutation = mutationsPointer < mutations.size() &&
                mutations.getPositionByIndex(mutationsPointer) == seq1Position ? mutations.getMutation(mutationsPointer) :
                NON_MUTATION;

        return seq1Position < seq1To || currentMutation != NON_MUTATION;
    }
}

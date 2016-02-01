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
package com.milaboratory.core.mutations;

import gnu.trove.impl.Constants;
import gnu.trove.map.custom_hash.TObjectIntCustomHashMap;
import gnu.trove.map.hash.TIntLongHashMap;
import gnu.trove.strategy.HashingStrategy;

import java.util.Arrays;

public final class MutationsCounter {
    /**
     * Main counter
     */
    final TIntLongHashMap counter = new TIntLongHashMap();
    /**
     * Mapping between long inserts and their ids in counter map.
     */
    TObjectIntCustomHashMap<int[]> insertMapping = null;

    public MutationsCounter() {
    }

    void adjust(int[] mutationsArray, int offset, int length, int delta) {
        assert length != 0;
        if (length == 1)
            adjustSingleMutation(mutationsArray[offset], delta);
        else
            adjustLongInsert(Arrays.copyOfRange(mutationsArray, offset, offset + length),
                    delta);
    }

    private void adjustSingleMutation(int mutation, int delta) {
        counter.adjustOrPutValue(mutation, delta, delta);
    }

    private void adjustLongInsert(int[] insert, int delta) {
        if (insertMapping == null)
            insertMapping = new TObjectIntCustomHashMap<>(new IntArrayHashingStrategy(),
                    Constants.DEFAULT_CAPACITY,
                    Constants.DEFAULT_LOAD_FACTOR, Mutation.NON_MUTATION);
        int next = nextId();
        int mutCode = insertMapping.putIfAbsent(insert, next);
        if (mutCode == Mutation.NON_MUTATION)
            mutCode = next;
        counter.adjustOrPutValue(mutCode, delta, delta);
    }

    private int nextId() {
        // MUTATION_TYPE = 0
        // TO_LETTER = 0
        return (insertMapping.size() + 1) << Mutation.FROM_OFFSET;
    }

    private static final class IntArrayHashingStrategy implements HashingStrategy<int[]> {
        @Override
        public int computeHashCode(int[] object) {
            return Arrays.hashCode(object);
        }

        @Override
        public boolean equals(int[] o1, int[] o2) {
            return Arrays.equals(o1, o2);
        }
    }
}

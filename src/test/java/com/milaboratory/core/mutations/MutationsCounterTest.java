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

import org.junit.Test;

/**
 * Created by dbolotin on 01/02/16.
 */
public class MutationsCounterTest {
    @Test
    public void test1() throws Exception {
        MutationsCounter counter = new MutationsCounter();
        int[] mutations = Mutations.decodeNuc("SA1T ST12C DG13 I16T I16G SA20G").mutations;
        counter.adjust(mutations, 0, 1, 1);
        counter.adjust(mutations, 1, 1, 1);
        counter.adjust(mutations, 2, 1, 1);
        counter.adjust(mutations, 3, 2, 1);
        counter.adjust(mutations, 5, 1, 1);
        int i =0;
    }
}
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
package com.milaboratory.core.io.sequence.fasta;

import com.milaboratory.core.sequence.AminoAcidSequence;
import org.junit.Assert;
import org.junit.Test;

public class FastaReaderGeneralTest {
    @Test
    public void test1() throws Exception {
        try (FastaReaderGeneral<AminoAcidSequence> reader =
                     new FastaReaderGeneral<>(
                             FastaReaderGeneralTest.class.getResourceAsStream("/sequences/aafasta.fa"),
                             AminoAcidSequence.ALPHABET)) {
            FastaRecord<AminoAcidSequence> read;
            read = reader.take();
            AminoAcidSequence expected = new AminoAcidSequence(
                    "MKTTQPPSMDCAEGRAANLPCNHSTISGNEYVYWYRQIHSQGPQYIIHGL" +
                            "KNNETNEMASLIITEDRKSSTLILPHATLRDTAVYYCIAFQGAQKLVFG" +
                            "QGTRLTINPNIQNPDPAVYQLRDSKSSDKSVCLFTDFDSQTNVSQSKDSD" +
                            "VYITDKCVLDMRSMDFKSNSAVAWSNKSDFACANAFNNSIIPEDTFFPSPESS");
            Assert.assertEquals(expected, read.getSequence());
            Assert.assertEquals("1", read.getDescription());
            Assert.assertEquals(0, read.getId());
            int i = 0;
            while ((read = reader.take()) != null)
                Assert.assertEquals(++i, read.getId());
            Assert.assertEquals(9, i);
        }
    }
}
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
package com.milaboratory.core.alignment.batchaligner;

import com.milaboratory.core.alignment.AffineGapAlignmentScoring;
import com.milaboratory.core.sequence.NucleotideSequence;
import org.junit.Assert;
import org.junit.Test;

public class BatchAlignerTest {
    @Test
    public void test1() {
        NucleotideSequence ref1 = new NucleotideSequence("ATAAGAGACACATAGGTCTGGC"),
                ref2 = new NucleotideSequence("ATTAGAGACACATAGGTCTAGC"),
                ref3 = new NucleotideSequence("ATGAGAGACACATAGGTCTTGC"),
                ref4 = new NucleotideSequence("ATCAGAGACACTTAGGTCTCGC"),
                ref5 = new NucleotideSequence("ATCAGAAATAAAAATAACTGGC");

        NucleotideSequence query = new NucleotideSequence("ATCAGAGACACATAGGTCTGGC");

        BatchAlignerParameters<NucleotideSequence> batchAlignerParameters = new BatchAlignerParameters<>(5,
                0.5f, 0f, true, AffineGapAlignmentScoring.getNucleotideBLASTScoring());

        BatchAligner<NucleotideSequence> batchAligner = new BatchAligner<>(batchAlignerParameters);

        batchAligner.addReference(ref1);
        batchAligner.addReference(ref2);
        batchAligner.addReference(ref3);
        batchAligner.addReference(ref4);
        batchAligner.addReference(ref5);

        BatchAlignmentResult alignmentResult = batchAligner.align(query);

        System.out.println(alignmentResult);

        Assert.assertEquals(0, ((BatchAlignmentHit) alignmentResult.hits.get(0)).id);
        Assert.assertEquals(4, alignmentResult.hits.size());
    }
}
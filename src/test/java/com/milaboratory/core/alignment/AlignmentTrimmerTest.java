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
import com.milaboratory.core.sequence.NucleotideSequence;
import com.milaboratory.test.TestUtil;
import org.junit.Assert;
import org.junit.Test;

public class AlignmentTrimmerTest {
    @Test
    @SuppressWarnings("unchecked")
    public void test0() throws Exception {
        NucleotideSequence n1 = new NucleotideSequence("TAGACCGATAAGTCGCTGGACTGCAGCGCGGATGTACTCGGGCATGTATCGT");
        TrimmingTarget[] ns = {
                new TrimmingTarget(new NucleotideSequence("TAGTCAAGTCGCTGGACTGCAGCGCGGATGTACTCGGGCATGTATCGT"),
                        new Range(9, 52),
                        new Range(5, 48))
        };
        AlignmentScoring<NucleotideSequence>[] scorings = new AlignmentScoring[]{
                LinearGapAlignmentScoring.getNucleotideBLASTScoring(),
                AffineGapAlignmentScoring.getNucleotideBLASTScoring()
        };
        for (TrimmingTarget n : ns) {
            for (AlignmentScoring<NucleotideSequence> scoring : scorings) {
                Alignment<NucleotideSequence> alignment = Aligner.alignGlobal(scoring, n1, n.seq);

                System.out.println(alignment.getScore());
                System.out.println(alignment);

                Alignment<NucleotideSequence> alignment1 = new Alignment<>(n1,
                        alignment.getAbsoluteMutations().extractMutationsForRange(n.expectedTrimmingRangeSeq1),
                        n.expectedTrimmingRangeSeq1,
                        n.expectedTrimmingRangeSeq2,
                        scoring);

                System.out.println(alignment1.getScore());
                System.out.println(alignment1);

                System.out.println();
                System.out.println("========");
                System.out.println();
            }
        }
    }

    @Test
    public void test1() throws Exception {
        NucleotideSequence ns1, ns2;
        LinearGapAlignmentScoring<NucleotideSequence> scoring = LinearGapAlignmentScoring.getNucleotideBLASTScoring();
        long time, t;
        int iterations = 100000;

        for (int j = 0; j < 4; j++) {
            time = 0;
            for (int i = 0; i < iterations; i++) {
                ns1 = TestUtil.randomSequence(NucleotideSequence.ALPHABET, 50, 100);
                ns2 = TestUtil.randomSequence(NucleotideSequence.ALPHABET, 50, 100);
                Alignment<NucleotideSequence> alignment = Aligner.alignGlobal(scoring, ns1, ns2);
                t = System.nanoTime();
                int calcScore = AlignmentUtils.calculateScore(ns1, new Range(0, ns1.size()), alignment.getAbsoluteMutations(), scoring);
                time += System.nanoTime() - t;
                if (alignment.getScore() != calcScore) {
                    System.out.println(alignment.getScore());
                    System.out.println(calcScore);
                    System.out.println(alignment);
                }
                Assert.assertEquals(alignment.getScore(), calcScore, 0.1);
            }
            System.out.println(TestUtil.time(time / iterations));
        }
    }

    public static final class TrimmingTarget {
        final NucleotideSequence seq;
        final Range expectedTrimmingRangeSeq1;
        final Range expectedTrimmingRangeSeq2;

        public TrimmingTarget(NucleotideSequence seq, Range expectedTrimmingRangeSeq1, Range expectedTrimmingRangeSeq2) {
            this.seq = seq;
            this.expectedTrimmingRangeSeq1 = expectedTrimmingRangeSeq1;
            this.expectedTrimmingRangeSeq2 = expectedTrimmingRangeSeq2;
        }
    }
}
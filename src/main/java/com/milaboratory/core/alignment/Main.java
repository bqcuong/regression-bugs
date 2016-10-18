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
import com.milaboratory.core.sequence.Alphabet;
import com.milaboratory.core.sequence.NucleotideSequence;
import com.milaboratory.core.sequence.Sequence;
import com.milaboratory.core.sequence.SequenceBuilder;
import com.milaboratory.util.RandomUtil;
import com.milaboratory.util.TimeUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.random.RandomGenerator;

public class Main {
    public static void main(String[] args) {
        if (args[0].equals("1"))
            m1();
        else
            m2();
    }

    public static final void m2() {
        NucleotideSequence ns1, ns2;
        LinearGapAlignmentScoring<NucleotideSequence> scoring = LinearGapAlignmentScoring.getNucleotideBLASTScoring();
        long time, t;
        int iterations = 200000;

        for (int j = 0; j < 100; j++) {
            time = 0;
            for (int i = 0; i < iterations; i++) {
                ns1 = randomSequence(NucleotideSequence.ALPHABET, 50, 100);
                ns2 = randomSequence(NucleotideSequence.ALPHABET, 50, 100);
                Alignment<NucleotideSequence> alignment = Aligner.alignGlobal(scoring, ns1, ns2);
                t = System.nanoTime();
                int calcScore = AlignmentUtils.calculateScore(ns1, new Range(0, ns1.size()), alignment.getAbsoluteMutations(), scoring);
                time += System.nanoTime() - t;
                if (alignment.getScore() != calcScore) {
                    System.out.println(alignment.getScore());
                    System.out.println(calcScore);
                    System.out.println(alignment);
                }
            }
            System.out.println(TimeUtils.nanoTimeToString(time / iterations));
        }
    }

    public static final void m1() {
        NucleotideSequence ns1, ns2;
        LinearGapAlignmentScoring<NucleotideSequence> scoring = LinearGapAlignmentScoring.getNucleotideBLASTScoring();
        long time, t;
        int iterations = 200000;

        for (int j = 0; j < 100; j++) {
            time = 0;
            for (int i = 0; i < iterations; i++) {
                ns1 = randomSequence(NucleotideSequence.ALPHABET, 50, 100);
                ns2 = randomSequence(NucleotideSequence.ALPHABET, 50, 100);
                Alignment<NucleotideSequence> alignment = Aligner.alignGlobal(scoring, ns1, ns2);
                t = System.nanoTime();
                int calcScore = AlignmentUtils.calculateScore(ns1, new Range(0, ns1.size()), alignment.getAbsoluteMutations(), scoring);
                time += System.nanoTime() - t;
                if (alignment.getScore() != calcScore) {
                    System.out.println(alignment.getScore());
                    System.out.println(calcScore);
                    System.out.println(alignment);
                }
            }
            System.out.println(TimeUtils.nanoTimeToString(time / iterations));
        }
    }

    public static <S extends Sequence<S>> S randomSequence(Alphabet<S> alphabet,
                                                           int minLength, int maxLength) {
        return randomSequence(alphabet, RandomUtil.getThreadLocalRandom(), minLength, maxLength);
    }

    public static <S extends Sequence<S>> S randomSequence(Alphabet<S> alphabet,
                                                           int minLength, int maxLength, boolean basicLettersOnly) {
        return randomSequence(alphabet, RandomUtil.getThreadLocalRandom(), minLength, maxLength, basicLettersOnly);
    }

    public static <S extends Sequence<S>> S randomSequence(Alphabet<S> alphabet, RandomDataGenerator r,
                                                           int minLength, int maxLength) {
        return randomSequence(alphabet, r.getRandomGenerator(), minLength, maxLength);
    }

    public static <S extends Sequence<S>> S randomSequence(Alphabet<S> alphabet, RandomDataGenerator r,
                                                           int minLength, int maxLength, boolean basicLettersOnly) {
        return randomSequence(alphabet, r.getRandomGenerator(), minLength, maxLength, basicLettersOnly);
    }

    public static <S extends Sequence<S>> S randomSequence(Alphabet<S> alphabet, RandomGenerator r,
                                                           int minLength, int maxLength) {
        return randomSequence(alphabet, r, minLength, maxLength, true);
    }

    public static <S extends Sequence<S>> S randomSequence(Alphabet<S> alphabet, RandomGenerator r,
                                                           int minLength, int maxLength, boolean basicLettersOnly) {
        int length = minLength == maxLength ?
                minLength : minLength + r.nextInt(maxLength - minLength + 1);
        SequenceBuilder<S> builder = alphabet.createBuilder();
        for (int i = 0; i < length; ++i)
            builder.append((byte) r.nextInt(basicLettersOnly ? alphabet.basicSize() : alphabet.size()));
        return builder.createAndDestroy();
    }
}

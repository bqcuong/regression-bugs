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
package com.milaboratory.core.alignment.kaligner2;

import com.milaboratory.test.TestUtil;
import gnu.trove.set.hash.TIntHashSet;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dbolotin on 29/09/15.
 */
public class CrossTest {
    @Test
    public void test1() throws Exception {
        Line l1 = new Line(10, 20, 100), l2 = new Line(20, 15, 200);
        Assert.assertTrue(l1.crosses(l2));
    }

    @Test
    public void testCHeckAlgorithm() throws Exception {
        RandomGenerator rg = new Well19937c();

        final UntanglingAlgorithm algorithm = BRUTE_FORCE;

        int K = 10000;
        System.out.println("Burning JVM...");
        System.out.print("BF: ");
        go(BRUTE_FORCE, 5, K, rg);
        System.out.print("ALG: ");
        go(algorithm, 5, K, rg);
        System.out.println();
        System.out.println("Run.");
        System.out.println();
        for (int N = 2; N < 14; N++) {
            System.out.print("BF: ");
            go(BRUTE_FORCE, N, K, rg);
            System.out.print("ALG: ");
            go(algorithm, N, K, rg);
            System.out.println();
        }
    }

    public static void go(UntanglingAlgorithm algorithm, int N, int K, RandomGenerator rg) {
        long start, time;
        long meanTime = 0, maxTime = 0;
        for (int z = 0; z < K; z++) {
            TIntHashSet generated = new TIntHashSet();
            Line[] lines = new Line[N];

            int[] limits = {300, 300, 700};
            for (int i = 0; i < N; i++) {
                int numbers[] = new int[3];
                for (int j = 0; j < 3; j++)
                    while (!generated.add(numbers[j] = rg.nextInt(limits[j]))) ;
                lines[i] = new Line(numbers[0], numbers[1], numbers[2]);
            }

            start = System.nanoTime();
            Line[] answer = bruteForce(lines);
            time = System.nanoTime() - start;
            maxTime = Math.max(maxTime, time);
            meanTime += time;
        }
        meanTime /= K;
        System.out.println("N=" + N + "  Mean=" + TestUtil.time(meanTime) + "   Max=" + TestUtil.time(maxTime));
    }

    @Test
    public void testRandom() throws Exception {
        RandomGenerator rg = new Well19937c();

        int N = 10;

        for (int z = 0; z < 100000; z++) {

            TIntHashSet generated = new TIntHashSet();
            Line[] lines = new Line[N];

            int[] limits = {300, 300, 700};
            for (int i = 0; i < N; i++) {
                int numbers[] = new int[3];
                for (int j = 0; j < 3; j++)
                    while (!generated.add(numbers[j] = rg.nextInt(limits[j]))) ;
                lines[i] = new Line(numbers[0], numbers[1], numbers[2]);
            }

            Arrays.sort(lines);

            //for (Line l : lines)
            //    System.out.println(l);
            //
            //System.out.println("-----");

            long start = System.nanoTime();
            Line[] answer = bruteForce(lines);
            System.out.println(TestUtil.time(System.nanoTime() - start));
            //for (Line bestLine : answer) {
            //    System.out.println(bestLine);
            //}
        }
    }

    interface UntanglingAlgorithm {
        Line[] calculate(Line[] lines);
    }

    public static final UntanglingAlgorithm BRUTE_FORCE = new UntanglingAlgorithm() {
        @Override
        public Line[] calculate(Line[] lines) {
            return bruteForce(lines);
        }
    };

    public static Line[] bruteForce(Line[] lines) {
        List<Line> best = null;
        int bestScore = 0;
        List<Line> current = new ArrayList<>(lines.length);
        int currentScore;
        for (long it = (1 << lines.length) - 1; it >= 0; --it) {
            current.clear();
            currentScore = 0;
            OUTER:
            for (int i = 0; i < lines.length; i++) {
                if (((it >> i) & 1) == 1) {
                    Line l = lines[i];
                    for (Line cLine : current)
                        if (l.crosses(cLine))
                            continue OUTER;
                    current.add(l);
                    currentScore += l.score;
                }
            }
            if (bestScore < currentScore) {
                best = current;
                current = new ArrayList<>(lines.length);
                bestScore = currentScore;
            }
        }

        return best.toArray(new Line[best.size()]);
    }


    @Test
    public void testName() throws Exception {

        Line[] ls = {new Line(10, 20, 100), new Line(20, 15, 200)};
        System.out.println(Arrays.toString(alg2(ls)));
    }

    public static Line[] alg2(final Line[] set) {
        for (int i = 0; i < set.length; ++i) {
            if (set[i] == null)
                continue;
            int delScore = 0;
            for (int j = 0; j < set.length; ++j) {
                if (i == j) continue;
                if (set[j] != null && set[i].crosses(set[j]))
                    delScore += set[j].score;
            }
            if (delScore > set[i].score)
                set[i] = null;
        }

        ArrayList<Line> res = new ArrayList<>();
        for (Line line : set) {
            if (line != null)
                res.add(line);
        }
        return res.toArray(new Line[res.size()]);
    }

    public static final class Line implements Comparable<Line> {
        final int a, b;
        final int score;

        public Line(int a, int b, int score) {
            this.a = a;
            this.b = b;
            this.score = score;
        }

        @Override
        public int compareTo(Line o) {
            return Integer.compare(a, o.a);
        }

        public boolean crosses(Line l) {
            return Integer.compare(a, l.a) * Integer.compare(b, l.b) < 0;
        }

        @Override
        public String toString() {
            return "{" + a +
                    "->" + b +
                    "}=" + score;
        }
    }
}

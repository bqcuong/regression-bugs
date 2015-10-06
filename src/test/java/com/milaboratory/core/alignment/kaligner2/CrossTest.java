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
import java.util.Collections;
import java.util.List;

/**
 * Created by dbolotin on 29/09/15.
 */
public class CrossTest {
    //@Test
    //public void test1() throws Exception {
    //    Line l1 = new Line(10, 20, 100), l2 = new Line(20, 15, 200);
    //    Assert.assertTrue(l1.crosses(l2));
    //}

    @Test
    public void testCHeckAlgorithm() throws Exception {
        RandomGenerator rg = new Well19937c();

        final UntanglingAlgorithm algorithm = BRUTE_FORCE_2;

        //final UntanglingAlgorithm algorithm = new UntanglingAlgorithm() {
        //    @Override
        //    public Line[] calculate(Line[] lines) {
        //        return alg4(lines.clone());
        //    }
        //};

        int K = 10000;
        System.out.println("Burning JVM...");
        System.out.print("BF: ");
        go(BRUTE_FORCE, 5, K, rg);
        System.out.print("ALG: ");
        go(algorithm, 5, K, rg);
        System.out.println();
        System.out.println("Run.");
        System.out.println();
        for (int N = 2; N < 13; N++) {
            System.out.print("BF: ");
            go(BRUTE_FORCE, N, K, rg);
            System.out.print("ALG: ");
            go(algorithm, N, K, rg);
            System.out.println();
        }
    }

    @Test
    public void test111() throws Exception {
        go(SUBSUM_ADD_ALGORITHM, 15, 10000, new Well19937c());
    }

    public static void go(UntanglingAlgorithm algorithm, int N, int K, RandomGenerator rg) {
        long start, time;
        long meanTime = 0, maxTime = 0, errors = 0, fails = 0, delta = 0, ascore = 0;
        for (int z = 0; z < K; z++) {
            Line[] lines = randomData(N, rg);

            Line[] correctAnswer = BRUTE_FORCE.calculate(lines);
            Assert.assertFalse(hasCrosses(correctAnswer));
            start = System.nanoTime();
            Line[] algorithmResult = algorithm.calculate(lines);
            time = System.nanoTime() - start;
            Assert.assertFalse(hasCrosses(algorithmResult));
            maxTime = Math.max(maxTime, time);
            meanTime += time;
            Arrays.sort(correctAnswer);
            Arrays.sort(algorithmResult);
            ascore += score(correctAnswer);
            if (!Arrays.equals(correctAnswer, algorithmResult)) {
                errors++;
                delta += score(correctAnswer) - score(algorithmResult);
            }
        }
        meanTime /= K;
        if (errors > 0)
            delta /= errors;
        ascore /= K;
        System.out.println("N=" + N + "  Errors=" + TestUtil.DECIMAL_FORMAT.format(100.0 * errors / K) +
                "%  Failed=" + TestUtil.DECIMAL_FORMAT.format(100.0 * fails / K) +
                "   Mean=" + TestUtil.time(meanTime) +
                "   Max=" + TestUtil.time(maxTime) +
                "   Delta=" + TestUtil.DECIMAL_FORMAT.format(100.0 * delta / ascore) + "%");
    }

    public static Line[] randomData(int N, RandomGenerator rg) {
        TIntHashSet generated = new TIntHashSet();
        Line[] lines = new Line[N];

        int[] limits = {300, 300, 700};
        for (int i = 0; i < N; i++) {
            int numbers[] = new int[3];
            for (int j = 0; j < 3; j++)
                while (!generated.add(numbers[j] = rg.nextInt(limits[j]))) ;
            lines[i] = new Line(numbers[0], numbers[1], numbers[2], i);
        }
        return lines;
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
                lines[i] = new Line(numbers[0], numbers[1], numbers[2], i);
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

    public static final UntanglingAlgorithm BRUTE_FORCE_2 = new UntanglingAlgorithm() {
        @Override
        public final Line[] calculate(final Line[] lines) {
            List<Line> best = null;
            int bestScore = 0;
            List<Line> current = new ArrayList<>(lines.length);
            int currentScore;
            long badCombination = 0xFFFFFFFFFFFFFFFFL, add = 0;
            OUTER:
            for (long it = 0, size = (1 << lines.length); it < size; ++it) {
//                if ((it & badCombination) == badCombination) {
//                    it += add;
//                    continue;
//                }
                current.clear();
                currentScore = 0;
                for (int i = lines.length - 1; i >= 0; --i) {
                    if (((it >> i) & 1) == 1) {
                        Line l = lines[i];
                        for (Line cLine : current)
                            if (l.crosses(cLine)) {
                                long a = ((1 << i) - 1);
                                it += a;
//                                long newBadComp = (1 << i) | (1 << cLine.index);
//                                if (badCombination == 0xFFFFFFFFFFFFFFFFL || badCombination < newBadComp) {
//                                    badCombination = newBadComp;
//                                    add = a;
//                                }
                                continue OUTER;
                            }
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
    };

    public static final UntanglingAlgorithm SUBSUM_ADD_ALGORITHM = new UntanglingAlgorithm() {
        @Override
        public Line[] calculate(Line[] lines) {
            Arrays.sort(lines);
            LineWrapper1[] wrappers = new LineWrapper1[lines.length];

            for (int i = 0; i < lines.length; i++) {
                wrappers[i] = new LineWrapper1(lines[i]);
                wrappers[i].score = lines[i].score;
            }

            for (int i = 0; i < lines.length; i++)
                for (int j = i + 1; j < lines.length; j++) {
                    if (lines[i].crosses(lines[j])) {
                        wrappers[i].score -= lines[j].score;
                        wrappers[j].score -= lines[i].score;
                        //for (int k = j + 1; k < lines.length; k++) {
                        //    if (lines[j].crosses(lines[k]) & lines[i].crosses(lines[k])) {
                        //        wrappers[k].score += lines[i].score;
                        //        wrappers[i].score += lines[k].score;
                        //        wrappers[k].score += lines[j].score;
                        //        wrappers[j].score += lines[k].score;
                        //    }
                        //}
                    }
                }

            //for (int i = 0; i < lines.length; i++)
            //    for (int j = i + 1; j < lines.length; j++) {
            //        if (lines[i].crosses(lines[j])) {
            //            //if(wrappers[i].score > wrappers[j].score)
            //                wrappers[i].score *= wrappers[j].score;
            //            //else
            //                wrappers[j].score *= wrappers[i].score;
            //        }
            //    }

            Arrays.sort(wrappers);

            List<Line> best, result = new ArrayList<>(lines.length);

            int previousScore = 0;
            int b1 = -1, b2 = -1;

            OUTER:
            for (int i = 0; i < wrappers.length; i++) {
                for (int j = 0; j < result.size(); j++) {
                    Line fromResult = result.get(j);
                    if (fromResult.crosses(wrappers[i].line)) {
                        if (b1 == -1) {
                            b1 = i;
                            b2 = j;
                        }
                        continue OUTER;
                    }
                }
                result.add(wrappers[i].line);
            }

            if (b1 == -1)
                return result.toArray(new Line[result.size()]);

            int score = score(result);

            best = result;

            result = new ArrayList<>();

            swap(wrappers, b1, b2);
            //swap(wrappers, 0, 1);

            OUTER:
            for (int i = 0; i < wrappers.length; i++) {
                for (int j = 0; j < result.size(); j++) {
                    Line fromResult = result.get(j);
                    if (fromResult.crosses(wrappers[i].line)) {
                        if (b1 == -1) {
                            b1 = i;
                            b2 = j;
                        }
                        continue OUTER;
                    }
                }
                result.add(wrappers[i].line);
            }

            if (score(result) > score)
                best = result;

            return best.toArray(new Line[best.size()]);
        }
    };

    public static void swap(final Object[] arr, final int i, final int j) {
        Object a = arr[i];
        arr[i] = arr[j];
        arr[j] = a;
    }

    public static int score(final List<Line> set) {
        int score = 0;
        for (Line line : set)
            score += line.score;
        return score;
    }

    public static final UntanglingAlgorithm SUBSUM_REMOVE_ALGORITHM = new UntanglingAlgorithm() {
        @Override
        public Line[] calculate(Line[] lines) {
            Arrays.sort(lines);
            List<LineWrapper1> wrappers = new ArrayList<>();

            for (int i = 0; i < lines.length; i++)
                wrappers.add(new LineWrapper1(lines[i]));

            OUTER:
            while (true) {
                for (int i = 0; i < wrappers.size(); i++)
                    wrappers.get(i).score = wrappers.get(i).line.score;

                for (int i = 0; i < wrappers.size(); i++)
                    for (int j = i + 1; j < wrappers.size(); j++) {
                        if (wrappers.get(i).line.crosses(wrappers.get(j).line)) {
                            wrappers.get(i).score -= wrappers.get(j).line.score;
                            wrappers.get(j).score -= wrappers.get(i).line.score;
                        }
                    }

                Collections.sort(wrappers);

                for (int i = wrappers.size() - 1; i >= 0; i--) {
                    for (int j = i - 1; j >= 0; --j)
                        if (wrappers.get(i).line.crosses(wrappers.get(j).line)) {
                            wrappers.remove(i);
                            continue OUTER;
                        }
                }

                Line[] result = new Line[wrappers.size()];
                for (int i = 0; i < wrappers.size(); i++) {
                    result[i] = wrappers.get(i).line;
                }

                return result;
            }
        }
    };

    public static final class LineWrapper1 implements Comparable<LineWrapper1> {
        final Line line;
        int score;

        public LineWrapper1(Line line) {
            this.line = line;
        }

        @Override
        public int compareTo(LineWrapper1 o) {
            return Integer.compare(o.score, score);
        }
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
        OUTER:
        for (long it = (1 << lines.length) - 1; it >= 0; --it) {
            current.clear();
            currentScore = 0;
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

    //@Test
    //public void testName() throws Exception {
    //    System.out.println(Arrays.toString(
    //            alg3(new Line[]{new Line(10, 20, 100), new Line(20, 15, 200)})));
    //}

    @Test
    public void testAlg() throws Exception {
        go(new UntanglingAlgorithm() {
            @Override
            public Line[] calculate(Line[] lines) {
                return alg3(lines.clone());
            }
        }, 12, 10000, new Well19937c());
    }

    public static boolean hasCrosses(final Line[] set) {
        for (int i = 0; i < set.length; ++i)
            for (int j = 0; j < set.length; ++j)
                if (i != j && set[i].crosses(set[j]))
                    return true;
        return false;
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

    public static Line[] alg3(final Line[] set) {
        int pos = -1, delScore = Integer.MIN_VALUE, ds;
        for (int i = 0; i < set.length; ++i) {
            if (set[i] == null)
                continue;
            ds = -set[i].score;
            for (int j = 0; j < set.length; ++j)
                if (i != j && set[j] != null && set[i].crosses(set[j]))
                    ds += set[j].score;

            if (ds != -set[i].score && ds > delScore) {
                pos = i;
                delScore = ds;
            }
        }
        if (pos == -1) {
            ArrayList<Line> res = new ArrayList<>();
            for (Line line : set) {
                if (line != null)
                    res.add(line);
            }
            return res.toArray(new Line[res.size()]);
        }
        set[pos] = null;
        return alg3(set);
    }

    public static Line[] alg4(final Line[] set) {
        alg40(set);
        ArrayList<Line> res = new ArrayList<>();
        for (Line line : set) {
            if (line != null)
                res.add(line);
        }
        return res.toArray(new Line[res.size()]);
    }

    public static void alg40(final Line[] set) {
        int pos = -1, delScore = Integer.MIN_VALUE, prevPos = -1, ds;
        for (int i = 0; i < set.length; ++i) {
            if (set[i] == null)
                continue;
            ds = -set[i].score;
            for (int j = 0; j < set.length; ++j)
                if (i != j && set[j] != null && set[i].crosses(set[j]))
                    ds += set[j].score;

            if (ds != -set[i].score && ds > delScore) {
                prevPos = pos;
                pos = i;
                delScore = ds;
            }
        }
        if (pos == -1)
            return;
        else if (prevPos != -1) {
            Line[] tempA = set.clone();
            tempA[prevPos] = null;
            alg40(tempA);
            int scoreA = score(tempA);

            Line[] tempB = set;
            tempB[pos] = null;
            alg40(tempB);
            int scoreB = score(tempB);

            if (scoreA > scoreB)
                System.arraycopy(tempA, 0, set, 0, set.length);
//            else
//                System.arraycopy(tempB, 0, set, 0, set.length);
        } else {
            set[pos] = null;
            alg40(set);
        }
    }

    public static int score(final Line[] set) {
        int score = 0;
        for (Line line : set)
            if (line != null)
                score += line.score;
        return score;
    }

    public static final class Line implements Comparable<Line> {
        final int a, b;
        final int score;
        final int index;

        public Line(int a, int b, int score, int index) {
            this.a = a;
            this.b = b;
            this.score = score;
            this.index = index;
        }

        @Override
        public int compareTo(Line o) {
            return Integer.compare(a, o.a);
        }

        public boolean crosses(Line l) {
            return Integer.compare(a, l.a) * Integer.compare(b, l.b) < 0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Line line = (Line) o;

            if (a != line.a) return false;
            if (b != line.b) return false;
            return score == line.score;

        }

        @Override
        public int hashCode() {
            int result = a;
            result = 31 * result + b;
            result = 31 * result + score;
            return result;
        }

        @Override
        public String toString() {
            return "{" + a +
                    "->" + b +
                    "}=" + score;
        }
    }
}

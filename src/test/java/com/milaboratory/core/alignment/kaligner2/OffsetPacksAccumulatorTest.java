package com.milaboratory.core.alignment.kaligner2;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static com.milaboratory.core.alignment.kaligner2.OffsetPacksAccumulator.*;

/**
 * Created by poslavsky on 21/09/15.
 */
public class OffsetPacksAccumulatorTest {
    public static final int bitsForOffset = 18;
    public static final int bitsForIndex = 32 - bitsForOffset;

    @Test
    public void test1() throws Exception {
        int[] data = {10, 10, 11, 12, 23, 33, 33, 34, 33, 33, 31, 32, 32, 10, 10, 10, 10};
        assertBunches(process(new OffsetPacksAccumulator(3, 4, 15, -4, -2, 30, bitsForIndex), data),
                new Bunch(0, 3, 10, 12, 56),
                new Bunch(5, 12, 31, 34, 110),
                new Bunch(13, 16, 10, 10, 60));
    }

    @Test
    public void test2() throws Exception {
        int[] data = {10, 10, 11, 12, 23, 33, 33, 34, 33, 33, 31, 32, 32, 10, 10, 10, 10};
        assertBunches(process(new OffsetPacksAccumulator(4, 4, 15, -4, -2, 30, bitsForIndex), data),
                new Bunch(0, 3, 10, 12, 56),
                new Bunch(5, 12, 31, 34, 110),
                new Bunch(13, 16, 10, 10, 60));
    }

    @Test
    public void test3() throws Exception {
        int[] data = {10, 10, 1, 11, 12, 23, 33, 33, 34, 33, 33, 31, 32, 32, 10, 10, 10, 10};
        assertBunches(process(new OffsetPacksAccumulator(4, 4, 15, -4, -2, 30, bitsForIndex), data),
                new Bunch(0, 4, 10, 12, 52),
                new Bunch(6, 13, 31, 34, 110),
                new Bunch(14, 17, 10, 10, 60));
    }


    @Test
    public void test4() throws Exception {
        int[] data = {10, 10, 1, 11, 33, 12, 23, 33, 33, 34, 33, 33, 31, 32, 32, 10, 10, 10, 10};
        assertBunches(process(new OffsetPacksAccumulator(4, 4, 15, -4, -2, 30, bitsForIndex), data),
                new Bunch(0, 5, 10, 12, 48),
                new Bunch(4, 14, 31, 34, 117),
                new Bunch(15, 18, 10, 10, 60));
    }

    @Test
    public void test5()
            throws Exception {
        int[] data = {10, 10, 102, 10, 10};
        int[] indexes = {0, 1, 2, 2, 3};
        assertBunches(process(new OffsetPacksAccumulator(4, 4, 15, -4, -2, 30, bitsForIndex), data, indexes),
                new Bunch(0, 3, 10, 10, 60));
    }

    @Test
    public void test6()
            throws Exception {
        int[] data = {10, 10, 102, 10, 10};
        int[] indexes = {0, 1, 1, 2, 3};
        assertBunches(process(new OffsetPacksAccumulator(4, 4, 15, -4, -2, 30, bitsForIndex), data, indexes),
                new Bunch(0, 3, 10, 10, 60));
    }

    @Test
    public void testSelfCorrelatedKMer1()
            throws Exception {
        int[] data = {10, 10, 12, 10, 10};
        int[] indexes = {0, 1, 2, 2, 3};
        OffsetPacksAccumulator of = process(new OffsetPacksAccumulator(4, 4, 15, -4, -2, 30, bitsForIndex), data, indexes);
        System.out.println(of);
        assertBunches(of, new Bunch(0, 3, 10, 10, 60));
    }

    @Test
    public void testSelfCorrelatedKMer2()
            throws Exception {
        int[] data = {10, 10, 12, 10, 10};
        int[] indexes = {0, 1, 1, 2, 3};
        OffsetPacksAccumulator of = process(new OffsetPacksAccumulator(4, 4, 15, -4, -2, 30, bitsForIndex), data, indexes);
        System.out.println(of);
        assertBunches(of, new Bunch(0, 3, 10, 10, 60));
    }

    @Test
    public void testSelfCorrelatedKMer3()
            throws Exception {
        int[] data = {12, 10, 10, 10, 10};
        int[] indexes = {0, 0, 1, 2, 3};
        OffsetPacksAccumulator of = process(new OffsetPacksAccumulator(4, 4, 15, -4, -2, 30, bitsForIndex), data, indexes);
        System.out.println(of);
        assertBunches(of, new Bunch(0, 3, 10, 10, 60));
    }

    static String verbose(OffsetPacksAccumulator of, int[] clouds) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("Number of clusters: " + of.numberOfIslands()).append("\n\n");
        int k = 0;
        for (int i = 0; i < of.results.size(); i += RECORD_SIZE) {
            sb.append(k++ + "-th cloud:\n")
                    .append("  first index: " + of.results.get(i + FIRST_INDEX)).append("\n")
                    .append("  last index: " + of.results.get(i + LAST_INDEX)).append("\n")
                    .append("  minimal index: " + of.results.get(i + MIN_VALUE)).append("\n")
                    .append("  maximal index: " + of.results.get(i + MAX_VALUE)).append("\n")
                    .append("  score: " + of.results.get(i + SCORE)).append("\n");

            int[] arr = new int[clouds.length];
            for (int j = of.results.get(i + FIRST_INDEX); j <= of.results.get(i + LAST_INDEX); ++j)
                arr[j] = 1;
            sb.append(p2a("  clouds: ", "  result: ", clouds, arr)).append("\n\n");
        }

        return sb.toString();
    }

    private static String p2a(String refPr, String maskPr, int[] ref, int[] mask) {
        StringBuilder refb = new StringBuilder(), maskb = new StringBuilder();
        refb.append(refPr).append("[");
        maskb.append(maskPr).append("[");
        for (int i = 0; ; ++i) {
            String r = Integer.toString(ref[i]), m = Integer.toString(mask[i]);
            for (int k = 0; k < r.length() - m.length(); ++k)
                m = " " + m;

            refb.append(r);
            maskb.append(m);
            if (i == ref.length - 1) {
                refb.append("]");
                maskb.append("]");
                return refb.append("\n").append(maskb).toString();
            }
            refb.append(",");
            maskb.append(",");
        }
    }

    private static OffsetPacksAccumulator process(OffsetPacksAccumulator of, int[] data, int[] indexes) {
        if (data.length != indexes.length)
            throw new IllegalArgumentException();
        int[] packedData = new int[data.length];
        for (int i = 0; i < data.length; i++)
            packedData[i] = (data[i] << bitsForIndex) | indexes[i];
        of.put(packedData);
        return of;
    }

    private static OffsetPacksAccumulator process(OffsetPacksAccumulator of, int... data) {
        int[] packedData = new int[data.length];
        for (int i = 0; i < data.length; i++)
            packedData[i] = (data[i] << bitsForIndex) | i;

        of.put(packedData);
        return of;
    }

    private static OffsetPacksAccumulator createWithDefaultParams() {
        return new OffsetPacksAccumulator(4, 4, 15, -4, -2, 30);
    }

    private static OffsetPacksAccumulator process(int... data) {
        return process(createWithDefaultParams(), data);
    }

    private static void assertBunches(int[] clouds, Bunch... expected) {
        assertBunches(process(clouds), expected);
    }

    private static void assertBunches(OffsetPacksAccumulator of, Bunch... expected) {
        Assert.assertEquals(new HashSet<>(Arrays.asList(expected)),
                new HashSet<>(Arrays.asList(getBunches(of))));
    }

    private static Bunch[] getBunches(OffsetPacksAccumulator of) {
        Bunch[] bunchs = new Bunch[of.numberOfIslands()];
        int k = 0;
        for (int i = 0; i < of.results.size(); i += RECORD_SIZE)
            bunchs[k++] = new Bunch(of.results.get(i + FIRST_INDEX),
                    of.results.get(i + LAST_INDEX),
                    of.results.get(i + MIN_VALUE),
                    of.results.get(i + MAX_VALUE),
                    of.results.get(i + SCORE));
        return bunchs;
    }

    private static final class Bunch {
        final int start, end, minVal, maxVal, score;

        public Bunch(int start, int end) {
            this(start, end, -1, -1);
        }

        public Bunch(int start, int end, int minVal, int maxVal) {
            this(start, end, minVal, maxVal, -1);
        }

        public Bunch(int start, int end, int minVal, int maxVal, int score) {
            this.start = start;
            this.end = end;
            this.minVal = minVal;
            this.maxVal = maxVal;
            this.score = score;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Bunch bunch = (Bunch) o;

            if (start != bunch.start) return false;
            if (end != bunch.end) return false;
            if (minVal != bunch.minVal) return false;
            if (maxVal != bunch.maxVal) return false;
            return score == bunch.score;
//            return true;
        }

        @Override
        public int hashCode() {
            int result = start;
            result = 31 * result + end;
            result = 31 * result + minVal;
            result = 31 * result + maxVal;
            result = 31 * result + score;
            return result;
        }

        @Override
        public String toString() {
            return "[" + start + " : " + end + ", " + minVal + " : " + maxVal + ", " + score + "]";
        }
    }
}
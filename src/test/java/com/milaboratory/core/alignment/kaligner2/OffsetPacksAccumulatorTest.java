package com.milaboratory.core.alignment.kaligner2;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static com.milaboratory.core.alignment.kaligner2.KMapper2.*;
import static com.milaboratory.core.alignment.kaligner2.OffsetPacksAccumulator.*;

/**
 * Created by poslavsky on 21/09/15.
 */
public class OffsetPacksAccumulatorTest {
    @Test
    public void test1() throws Exception {
        int[] data = {10, 10, 11, 12, 23, 33, 33, 34, 33, 33, 31, 32, 32, 10, 10, 10, 10};
        assertBunches(process(new OffsetPacksAccumulator(3, 4, 15, -4, -2, 30), data),
                new Bunch(0, 3, 56),
                new Bunch(5, 12, 110),
                new Bunch(13, 16, 60));
    }

    @Test
    public void test2() throws Exception {
        int[] data = {10, 10, 11, 12, 23, 33, 33, 34, 33, 33, 31, 32, 32, 10, 10, 10, 10};
        assertBunches(process(new OffsetPacksAccumulator(4, 4, 15, -4, -2, 30), data),
                new Bunch(0, 3, 56),
                new Bunch(5, 12, 110),
                new Bunch(13, 16, 60));
    }

    @Test
    public void test3() throws Exception {
        int[] data = {10, 10, 1, 11, 12, 23, 33, 33, 34, 33, 33, 31, 32, 32, 10, 10, 10, 10};
        assertBunches(process(new OffsetPacksAccumulator(4, 4, 15, -4, -2, 30), data),
                new Bunch(0, 4, 52),
                new Bunch(6, 13, 110),
                new Bunch(14, 17, 60));
    }


    @Test
    public void test4() throws Exception {
        int[] data = {10, 10, 1, 11, 33, 12, 23, 33, 33, 34, 33, 33, 31, 32, 32, 10, 10, 10, 10};
        assertBunches(process(new OffsetPacksAccumulator(4, 4, 15, -4, -2, 30), data),
                new Bunch(0, 5, 48),
                new Bunch(4, 14, 117),
                new Bunch(15, 18, 60));
    }

    @Test
    public void test5()
            throws Exception {
        int[] data = {10, 10, 102, 10, 10};
        int[] indexes = {0, 1, 2, 2, 3};
        assertBunches(process(new OffsetPacksAccumulator(4, 4, 15, -4, -2, 30), data, indexes),
                new Bunch(0, 4, 60));
    }

    @Test
    public void test6()
            throws Exception {
        int[] data = {10, 10, 102, 10, 10};
        int[] indexes = {0, 1, 1, 2, 3};
        assertBunches(process(new OffsetPacksAccumulator(4, 4, 15, -4, -2, 30), data, indexes),
                new Bunch(0, 4, 60));
    }

    @Test
    public void testSelfCorrelatedKMer1()
            throws Exception {
        int[] data = {10, 10, 12, 10, 10};
        int[] indexes = {0, 1, 2, 2, 3};
        assertBunches(process(new OffsetPacksAccumulator(4, 4, 15, -4, -2, 30), data, indexes),
                new Bunch(0, 4, 60));
    }

    @Test
    public void testSelfCorrelatedKMer2()
            throws Exception {
//        int[] data =    {10, 10, 12, 10, 10};
//        int[] indexes = { 0,  1,  1,  2,  3};
        int[] data =    {10, 10, 12, 10, 10};
        int[] indexes = { 0,  1,  1,  2,  3};
        assertBunches(process(new OffsetPacksAccumulator(4, 4, 15, -4, -2, 30), data, indexes),
                new Bunch(0, 4, 60));
    }

    @Test
    public void testSelfCorrelatedKMer3()
            throws Exception {
        int[] data = {8, 10, 10, 10, 10};
        int[] indexes = {0, 0, 1, 2, 3};
        assertBunches(process(new OffsetPacksAccumulator(4, 4, 15, -4, -2, 30), data, indexes),
                new Bunch(1, 4, 60));
    }

    @Test
    public void testSelfCorrelatedKMer4()
            throws Exception {
//        int[] data =    {8, 10, 11, 50, 51, 52, 10, 10, 10, 50, 50, 50};
//        int[] indexes = {0,  0,  0,  0,  0,  0,  1,  2,  3,  4,  5,  6};
        int[] data = {8, 10, 11, 50, 51, 52, 10, 10, 10, 50, 50, 50};
        int[] indexes = {0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6};
        assertBunches(process(new OffsetPacksAccumulator(4, 4, 15, -4, -2, 30), data, indexes),
                new Bunch(1, 8, 60),
                new Bunch(3, 11, 48));
    }

    @Test
    public void testSelfCorrelatedKMer5()
            throws Exception {
        int[] data = {8, 10, 11, 50, 51, 52, 10, 10, 10, 50, 50, 50};
        for (int i = 0; i < data.length; i++)
            data[i] -= 1000;
        int[] indexes = {0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6};
        assertBunches(process(new OffsetPacksAccumulator(4, 4, 15, -4, -2, 30), data, indexes),
                new Bunch(1, 8, 60),
                new Bunch(3, 11, 48));
    }

    @Test
    public void testSelfCorrelatedKMer6()
            throws Exception {
//        int[] data =    {-50, -49, -48, -10, -8, -6, -52, -51, -50, -11, -10, -8};
//        int[] indexes = {  0,   0,   0,   0,  0,  0,   1,   2,   3,   4,   5,  6};

        int[] data = {-50, -49, -48, -10, -8, -6, -52, -51, -50, -11, -10, -8};
        int[] indexes = {0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6};
        OffsetPacksAccumulator of = process(new OffsetPacksAccumulator(4, 4, 15, -4, -2, 30), data, indexes);
        assertBunches(of, new Bunch(0, 8, 52), new Bunch(3, 11, 40));
    }

    @Test
    public void test222() throws Exception {
        int data[] = {73729, 73730, 81923, 81924, 180231, 1769479, 180232, 704520, 1933320, 458762, 106507, 122891, 139275, 368651, 73740, 90124, 106508, 335884, 106509, 106510, 106511, -155632, -139248, -122864, 106512};
        for (int i = 0; i < data.length; ++i)
            System.out.println(i + "     " + index(data[i]) + "    " + offset(data[i]));

        OffsetPacksAccumulator of = new OffsetPacksAccumulator(4, 3, 10, -7, -8, 15);
        of.calculateInitialPartitioning(data);
        System.out.println(of);

    }

    private static String verbose(OffsetPacksAccumulator of, int[] clouds) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("Number of clusters: " + of.numberOfIslands()).append("\n\n");
        int k = 0;
        for (int i = 0; i < of.results.size(); i += OUTPUT_RECORD_SIZE) {
            sb.append(k++ + "-th cloud:\n")
                    .append("  first index: " + of.results.get(i + FIRST_RECORD_ID)).append("\n")
                    .append("  last index: " + of.results.get(i + LAST_INDEX)).append("\n")
                    .append("  minimal index: " + of.results.get(i + MIN_VALUE)).append("\n")
                    .append("  maximal index: " + of.results.get(i + MAX_VALUE)).append("\n")
                    .append("  score: " + of.results.get(i + SCORE)).append("\n");

            int[] arr = new int[clouds.length];
            for (int j = of.results.get(i + FIRST_RECORD_ID); j <= of.results.get(i + LAST_INDEX); ++j)
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
            packedData[i] = record(data[i], indexes[i]);
        of.calculateInitialPartitioning(packedData);
        return of;
    }

    private static OffsetPacksAccumulator process(OffsetPacksAccumulator of, int... data) {
        int[] packedData = new int[data.length];
        for (int i = 0; i < data.length; i++)
            packedData[i] = record(data[i], i);

        of.calculateInitialPartitioning(packedData);
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
        for (int i = 0; i < of.results.size(); i += OUTPUT_RECORD_SIZE)
            bunchs[k++] = new Bunch(of.results.get(i + FIRST_RECORD_ID),
                    of.results.get(i + LAST_RECORD_ID),
                    of.results.get(i + SCORE));
        return bunchs;
    }

    private static final class Bunch {
        final int start, end, score;

        public Bunch(int start, int end) {
            this(start, end, -1);
        }

        public Bunch(int start, int end, int score) {
            this.start = start;
            this.end = end;
            this.score = score;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Bunch bunch = (Bunch) o;

            if (start != bunch.start) return false;
            if (end != bunch.end) return false;
            return score == bunch.score;
//            return true;
        }

        @Override
        public int hashCode() {
            int result = start;
            result = 31 * result + end;
            result = 31 * result + score;
            return result;
        }

        @Override
        public String toString() {
            return "[" + start + " : " + end + ", " + score + "]";
        }
    }
}
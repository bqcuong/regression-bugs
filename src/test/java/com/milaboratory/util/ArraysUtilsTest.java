package com.milaboratory.util;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class ArraysUtilsTest {
    @Test
    public void test1() throws Exception {
        int[] a = {3, 6, 2, 1, 6, 7};
        int[] b = a.clone();
        int[] r = {7, 6, 1, 2, 6, 3};
        ArraysUtils.reverse(a);
        assertArrayEquals(r, a);
        ArraysUtils.reverse(a);
        assertArrayEquals(b, a);
    }

    @Test
    public void test2() throws Exception {
        int[] a = {3, 6, 2, 8, 1, 6, 7};
        int[] b = a.clone();
        int[] r = {7, 6, 1, 8, 2, 6, 3};
        ArraysUtils.reverse(a);
        assertArrayEquals(r, a);
        ArraysUtils.reverse(a);
        assertArrayEquals(b, a);
    }
}
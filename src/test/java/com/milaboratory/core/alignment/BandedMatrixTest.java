package com.milaboratory.core.alignment;

import org.junit.Assert;
import org.junit.Test;

public class BandedMatrixTest {

    @Test
    public void test1() throws Exception {
        BandedMatrix matrix = new BandedMatrix(new CachedIntArray(), 10, 10, 0);
        matrix.set(0, 0, 1);
        matrix.set(9, 9, 2);
        Assert.assertEquals(1, matrix.get(0, 0));
        Assert.assertEquals(BandedMatrix.DEFAULT_VALUE, matrix.get(0, 1));
        Assert.assertEquals(2, matrix.get(9, 9));
    }

    @Test(expected = AssertionError.class)
    public void test2() throws Exception {
        BandedMatrix matrix = new BandedMatrix(new CachedIntArray(), 10, 10, 0);
        matrix.set(0, 1, 1);
    }


    @Test(expected = AssertionError.class)
    public void test3() throws Exception {
        BandedMatrix matrix = new BandedMatrix(new CachedIntArray(), 5, 10, 2);
        //matrix.set(0, 0, 1);
        matrix.set(3, 0, 2);
    }

    @Test
    public void test4() throws Exception {
        BandedMatrix matrix = new BandedMatrix(new CachedIntArray(), 5, 10, 2);
        matrix.set(0, 0, 1);
        matrix.set(2, 0, 2);
        matrix.set(4, 9, 3);

        Assert.assertEquals(1, matrix.get(0, 0));
        Assert.assertEquals(2, matrix.get(2, 0));
        Assert.assertEquals(3, matrix.get(4, 9));
    }
}
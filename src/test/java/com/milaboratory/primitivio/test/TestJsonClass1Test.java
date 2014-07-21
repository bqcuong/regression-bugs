package com.milaboratory.primitivio.test;

import com.milaboratory.util.GlobalObjectMappers;
import org.junit.Assert;
import org.junit.Test;

public class TestJsonClass1Test {
    @Test
    public void test1() throws Exception {
        TestJsonClass1 c1 = new TestJsonClass1(1, "FER");
        String str = GlobalObjectMappers.ONE_LINE.writeValueAsString(c1);
        Assert.assertEquals(c1, GlobalObjectMappers.ONE_LINE.readValue(str, TestJsonClass1.class));
    }
}
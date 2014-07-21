package com.milaboratory.primitivio;

import com.milaboratory.primitivio.test.TestClass1;
import com.milaboratory.primitivio.test.TestSubClass2;
import com.milaboratory.primitivio.test.TestSubClass3;
import com.milaboratory.primitivio.test.TestSubSubClass1;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UtilTest {
    @Test
    public void test1() throws Exception {
        assertEquals(TestSubClass2.class, Util.findSerializableParent(TestSubClass2.class, true, false));
        assertEquals(TestSubClass2.class, Util.findSerializableParent(TestSubSubClass1.class, true, false));
    }

    @Test
    public void test2() throws Exception {
        assertEquals(TestClass1.class, Util.findSerializableParent(TestSubClass2.class, true, true));
        assertEquals(TestClass1.class, Util.findSerializableParent(TestSubSubClass1.class, true, true));
    }

    @Test(expected = RuntimeException.class)
    public void test3() throws Exception {
        Util.findSerializableParent(TestSubClass3.class, false, false);
    }

    @Test(expected = RuntimeException.class)
    public void test4() throws Exception {
        Util.findSerializableParent(TestSubClass2.class, false, false);
    }
}
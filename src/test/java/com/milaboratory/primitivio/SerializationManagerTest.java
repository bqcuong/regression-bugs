package com.milaboratory.primitivio;

import com.milaboratory.primitivio.test.TestClass;
import com.milaboratory.primitivio.test.TestSubClass1;
import com.milaboratory.primitivio.test.TestSubClass2;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SerializationManagerTest {
    @Test
    public void testRoot1() throws Exception {
        assertEquals(TestClass.class, SerializationManager.findRoot(TestSubClass1.class, true));
        assertEquals(TestClass.class, SerializationManager.findRoot(TestSubClass2.class, true));
    }

    @Test(expected = RuntimeException.class)
    public void testRoot2() throws Exception {
        SerializationManager.findRoot(TestSubClass2.class, false);
    }
}
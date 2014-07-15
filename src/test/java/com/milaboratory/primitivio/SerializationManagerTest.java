package com.milaboratory.primitivio;

import com.milaboratory.primitivio.test.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SerializationManagerTest {
    @Test
    public void testRoot1() throws Exception {
        assertEquals(TestClass1.class, SerializationManager.findRoot(TestSubClass1.class));
        assertEquals(TestClass1.class, SerializationManager.findRoot(TestSubClass2.class));
    }

    @Test(expected = RuntimeException.class)
    public void testRoot2() throws Exception {
        System.out.println(SerializationManager.findRoot(TestSubClass3.class));
    }

    @Test(expected = RuntimeException.class)
    public void testRoot3() throws Exception {
        System.out.println(SerializationManager.findRoot(TestSubClass4.class));
    }

    @Test
    public void test1() throws Exception {
        SerializationManager manager = new SerializationManager();
        Serializer serializer = manager.getSerializer(TestSubClass2.class);
        assertEquals(CustomSerializerImpl.class, serializer.getClass());
        assertTrue(serializer == manager.getSerializer(TestSubSubClass1.class));
        assertTrue(serializer == manager.getSerializer(TestSubClass1.class));
    }
}
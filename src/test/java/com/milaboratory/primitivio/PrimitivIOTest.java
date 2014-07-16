package com.milaboratory.primitivio;

import com.milaboratory.primitivio.test.TestClass1;
import com.milaboratory.primitivio.test.TestSubClass1;
import com.milaboratory.primitivio.test.TestSubClass2;
import com.milaboratory.test.TestUtil;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class PrimitivIOTest {
    @Test
    public void testVarInt1() throws Exception {
        RandomGenerator rg = new Well19937c();
        final int count = TestUtil.its(100, 1000);
        int[] values = new int[count];
        for (int i = 0; i < count; ++i) {
            int bits = rg.nextInt(31);
            values[i] = rg.nextInt(0x7FFFFFFF >>> (bits));
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrimitivO po = new PrimitivO(bos);
        for (int i = 0; i < count; ++i)
            po.writeVarInt(values[i]);
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        PrimitivI pi = new PrimitivI(bis);
        for (int i = 0; i < count; ++i)
            Assert.assertEquals(values[i], pi.readVarInt());
    }

    @Test
    public void testSimpleSerialization1() throws Exception {
        TestClass1 obj1 = new TestClass1(1, "Surep");
        TestClass1 obj2 = new TestSubClass2(3, "Ref");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrimitivO po = new PrimitivO(bos);
        po.writeObject(obj1);
        po.writeObject(obj2);
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        PrimitivI pi = new PrimitivI(bis);
        TestClass1 dobj1 = pi.readObject(TestClass1.class);
        TestClass1 dobj2 = pi.readObject(TestClass1.class);
        Assert.assertEquals(obj1, dobj1);
        Assert.assertEquals(obj2, dobj2);
    }

    @Test
    public void testSimpleSerialization2() throws Exception {
        TestClass1 obj1 = new TestClass1(1, "Surep");
        TestClass1 obj2 = new TestSubClass2(3, "Ref", obj1, obj1, null, null, new TestSubClass1(2, "DERR"));
        obj2.subObjects[3] = obj2;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrimitivO po = new PrimitivO(bos);
        int cc = 10;
        for (int i = 0; i < cc; ++i)
            po.writeObject(obj2);
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        PrimitivI pi = new PrimitivI(bis);
        TestClass1 dobj2;
        for (int i = 0; i < cc; ++i) {
            Assert.assertTrue(pi.references.isEmpty());
            dobj2 = pi.readObject(TestClass1.class);
            Assert.assertEquals(dobj2.i, obj2.i);
            Assert.assertEquals(dobj2.k, obj2.k);
            Assert.assertTrue(dobj2 == dobj2.subObjects[3]);
            Assert.assertNull(dobj2.subObjects[2]);
            Assert.assertTrue(dobj2.subObjects[0] == dobj2.subObjects[1]);
            Assert.assertEquals(obj2.subObjects[0], dobj2.subObjects[0]);
            Assert.assertEquals(obj2.subObjects[4], dobj2.subObjects[4]);
            Assert.assertTrue(dobj2 == dobj2.subObjects[3]);
        }
    }

    @Test
    public void testSimpleSerialization3() throws Exception {
        TestSubClass1 objr = new TestSubClass1(2, "DERR");
        TestClass1 obj1 = new TestClass1(1, "Surep", objr);
        TestClass1 obj2 = new TestSubClass2(3, "Ref", obj1, obj1, null, null, objr);
        obj2.subObjects[3] = obj2;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrimitivO po = new PrimitivO(bos);
        int cc = 10;
        for (int i = 0; i < cc; ++i)
            po.writeObject(obj2);
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        PrimitivI pi = new PrimitivI(bis);
        TestClass1 dobj2;
        for (int i = 0; i < cc; ++i) {
            Assert.assertTrue(pi.references.isEmpty());
            dobj2 = pi.readObject(TestClass1.class);
            Assert.assertEquals(dobj2.i, obj2.i);
            Assert.assertEquals(dobj2.k, obj2.k);
            Assert.assertTrue(dobj2 == dobj2.subObjects[3]);
            Assert.assertNull(dobj2.subObjects[2]);
            Assert.assertTrue(dobj2.subObjects[0] == dobj2.subObjects[1]);
            Assert.assertEquals(obj2.subObjects[0], dobj2.subObjects[0]);
            Assert.assertEquals(obj2.subObjects[4], dobj2.subObjects[4]);
            Assert.assertTrue(dobj2.subObjects[0].subObjects[0] == dobj2.subObjects[1].subObjects[0]);
            Assert.assertTrue(dobj2.subObjects[4] == dobj2.subObjects[1].subObjects[0]);
            Assert.assertTrue(dobj2 == dobj2.subObjects[3]);
        }
    }
}
package com.milaboratory.primitivio.test;

import com.milaboratory.primitivio.PrimitivI;
import com.milaboratory.primitivio.PrimitivO;
import com.milaboratory.primitivio.Serializer;

public class TestCustomSerializer1 implements Serializer<TestSubClass2> {
    @Override
    public void write(PrimitivO output, TestSubClass2 object) {
        output.writeVarInt(object.i);
        output.writeUTF(object.k);
        output.writeInt(object.subObjects.length);
        output.writeReference(object);
        for (TestClass1 subObject : object.subObjects)
            output.writeObject(subObject);
    }

    @Override
    public TestSubClass2 read(PrimitivI input) {
        int j = input.readVarInt();
        String s = input.readUTF();
        int count = input.readInt();
        TestSubClass2 instance = new TestSubClass2(j, s, new TestClass1[count]);
        input.readReference(instance);
        for (int i = 0; i < count; i++)
            instance.subObjects[i] = input.readObject(TestClass1.class);
        return instance;
    }

    @Override
    public boolean isReference() {
        return true;
    }

    @Override
    public boolean handlesReference() {
        return true;
    }
}

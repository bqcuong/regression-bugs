package com.milaboratory.primitivio.test;

import com.milaboratory.primitivio.PrimitivI;
import com.milaboratory.primitivio.PrimitivO;
import com.milaboratory.primitivio.Serializer;

public class TestSerializer implements Serializer<TestClass> {
    @Override
    public void write(PrimitivO output, TestClass object) {
        if (object.getClass() == TestClass.class)
            output.writeByte((byte) 1);
        else if (object.getClass() == TestSubClass2.class)
            output.writeByte((byte) 2);
        else
            throw new RuntimeException();
        output.writeInt(object.i);
        output.writeUTF(object.k);
    }

    @Override
    public TestClass read(PrimitivI input) {
        byte t = input.readByte();
        int i = input.readInt();
        String s = input.readUTF();
        if (t == 1)
            return new TestClass(i, s);
        else if (t == 2)
            return new TestSubClass2(i, s);
        else
            throw new RuntimeException();
    }
}

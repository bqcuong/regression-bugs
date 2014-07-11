package com.milaboratory.primitivio.test;

import com.milaboratory.primitivio.PrimitivI;
import com.milaboratory.primitivio.PrimitivO;
import com.milaboratory.primitivio.Serializer;

public class TestCustomSerializer1 implements Serializer<TestSubClass2> {
    @Override
    public void write(PrimitivO output, TestSubClass2 object) {

    }

    @Override
    public TestSubClass2 read(PrimitivI input) {
        return null;
    }
}

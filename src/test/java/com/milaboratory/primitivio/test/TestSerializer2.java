package com.milaboratory.primitivio.test;

import com.milaboratory.primitivio.PrimitivI;
import com.milaboratory.primitivio.PrimitivO;
import com.milaboratory.primitivio.Serializer;

public class TestSerializer2 implements Serializer<TestInterface> {
    @Override
    public void write(PrimitivO output, TestInterface object) {
    }

    @Override
    public TestInterface read(PrimitivI input) {
        return null;
    }
}

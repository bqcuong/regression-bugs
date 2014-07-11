package com.milaboratory.primitivio.test;

import com.milaboratory.primitivio.annotations.SerializableBy;

@SerializableBy(TestCustomSerializer1.class)
public class TestSubClass2 extends TestClass {
    public TestSubClass2(int i, String k) {
        super(i, k);
    }
}

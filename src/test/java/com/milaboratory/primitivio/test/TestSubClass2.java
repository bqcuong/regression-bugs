package com.milaboratory.primitivio.test;

import com.milaboratory.primitivio.annotations.Serializable;

@Serializable(by = TestCustomSerializer1.class)
public class TestSubClass2 extends TestClass1 {
    public TestSubClass2(int i, String k, TestClass1... subObjects) {
        super(i, k, subObjects);
    }
}

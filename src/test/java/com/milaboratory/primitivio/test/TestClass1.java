package com.milaboratory.primitivio.test;

import com.milaboratory.primitivio.annotations.CustomSerializer;
import com.milaboratory.primitivio.annotations.Serializable;

@Serializable(by = TestSerializer1.class,
        custom = {
                @CustomSerializer(id = 1, type = TestSubClass2.class)
        })
public class TestClass1 {
    final int i;
    final String k;

    public TestClass1(int i, String k) {
        this.i = i;
        this.k = k;
    }
}

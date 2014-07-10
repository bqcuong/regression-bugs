package com.milaboratory.primitivio.test;

import com.milaboratory.primitivio.annotations.CustomSerializer;
import com.milaboratory.primitivio.annotations.CustomSerializers;
import com.milaboratory.primitivio.annotations.SerializableBy;

@SerializableBy(TestSerializer.class)
@CustomSerializers({
        @CustomSerializer(id = 1, type = TestSubClass2.class)
})
public class TestClass {
    final int i;
    final String k;

    public TestClass(int i, String k) {
        this.i = i;
        this.k = k;
    }
}

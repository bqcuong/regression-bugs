package com.milaboratory.primitivio.test;

import com.milaboratory.primitivio.annotations.CustomSerializer;
import com.milaboratory.primitivio.annotations.Serializable;

import java.util.Arrays;

@Serializable(by = TestSerializer1.class,
        custom = {
                @CustomSerializer(id = 1, type = TestSubClass2.class)
        })
public class TestClass1 {
    public final int i;
    public final String k;
    public final TestClass1[] subObjects;

    public TestClass1(int i, String k, TestClass1... subObjects) {
        this.i = i;
        this.k = k;
        this.subObjects = subObjects.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestClass1 that = (TestClass1) o;

        if (i != that.i) return false;
        if (!k.equals(that.k)) return false;
        if (!Arrays.equals(subObjects, that.subObjects)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = i;
        result = 31 * result + k.hashCode();
        result = 31 * result + Arrays.hashCode(subObjects);
        return result;
    }
}

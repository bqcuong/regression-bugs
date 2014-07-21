package com.milaboratory.primitivio.test;

import com.milaboratory.primitivio.annotations.CustomSerializer;
import com.milaboratory.primitivio.annotations.Serializable;

@Serializable(by = TestCustomSerializer1.class,
        custom = {
                @CustomSerializer(id = 1, type = TestCustomSerializer1.class)
        })
public class TestSubClass4 extends TestClass2 {
}

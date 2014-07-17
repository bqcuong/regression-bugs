package com.milaboratory.primitivio.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.milaboratory.primitivio.annotations.Serializable;

@Serializable(asJson = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE)
public final class TestJsonClass1 {
    public final int guga;
    public final String muga;

    @JsonCreator
    public TestJsonClass1(@JsonProperty("guga") int guga,
                          @JsonProperty("muga") String muga) {
        this.guga = guga;
        this.muga = muga;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestJsonClass1 that = (TestJsonClass1) o;

        if (guga != that.guga) return false;
        if (!muga.equals(that.muga)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = guga;
        result = 31 * result + muga.hashCode();
        return result;
    }
}

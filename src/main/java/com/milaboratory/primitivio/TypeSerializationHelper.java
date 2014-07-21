package com.milaboratory.primitivio;

public class TypeSerializationHelper {
    Serializer serializer;

    public TypeSerializationHelper(Serializer serializer) {
        this.serializer = serializer;
    }

    public Serializer getSerializer() {
        return serializer;
    }
}

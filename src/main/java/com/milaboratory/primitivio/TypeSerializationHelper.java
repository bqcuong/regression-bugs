package com.milaboratory.primitivio;

public class TypeSerializationHelper {
    Serializer serializer;
    boolean acceptSubclasses;

    public TypeSerializationHelper(Serializer serializer, boolean acceptSubclasses) {
        this.serializer = serializer;
        this.acceptSubclasses = acceptSubclasses;
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public boolean isAcceptSubclasses() {
        return acceptSubclasses;
    }
}

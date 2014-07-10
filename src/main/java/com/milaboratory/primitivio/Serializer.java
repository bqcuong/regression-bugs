package com.milaboratory.primitivio;

public interface Serializer<T> {
    void write(PrimitivO output, T object);

    T read(PrimitivI input);
}

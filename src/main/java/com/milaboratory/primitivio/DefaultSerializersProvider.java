package com.milaboratory.primitivio;

public interface DefaultSerializersProvider {
    Serializer createSerializer(Class<?> type, SerializersManager manager);
}

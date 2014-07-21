package com.milaboratory.primitivio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.milaboratory.util.GlobalObjectMappers;

import java.io.IOException;

public final class JSONSerializer implements Serializer {
    final Class<?> type;

    public JSONSerializer(Class<?> type) {
        if (type == null)
            throw new NullPointerException();
        this.type = type;
    }

    @Override
    public void write(PrimitivO output, Object object) {
        try {
            output.writeUTF(GlobalObjectMappers.ONE_LINE.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object read(PrimitivI input) {
        String str = input.readUTF();
        try {
            return GlobalObjectMappers.ONE_LINE.readValue(str, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isReference() {
        return false;
    }

    @Override
    public boolean handlesReference() {
        return false;
    }
}

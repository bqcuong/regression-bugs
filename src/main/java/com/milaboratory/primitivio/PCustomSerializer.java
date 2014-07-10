package com.milaboratory.primitivio;

import gnu.trove.map.custom_hash.TObjectByteCustomHashMap;
import gnu.trove.map.hash.TByteObjectHashMap;

public class PCustomSerializer implements Serializer {
    final TObjectByteCustomHashMap<Class> idByClass;
    final TByteObjectHashMap<Serializer> serializersById;

    public PCustomSerializer(TObjectByteCustomHashMap<Class> idByClass, TByteObjectHashMap<Serializer> serializersById) {
        this.idByClass = idByClass;
        this.serializersById = serializersById;
    }

    @Override
    public void write(PrimitivO output, Object object) {
        if (idByClass.containsKey(object.getClass())) {
            byte id = idByClass.get(object.getClass());
            output.writeByte(id);
            serializersById.get(id).write(output, object);
        } else {
            output.writeByte((byte) 0);
            serializersById.get((byte) 0).write(output, object);
        }
    }

    @Override
    public Object read(PrimitivI input) {
        byte id = input.readByte();
        return serializersById.get(id).read(input);
    }
}

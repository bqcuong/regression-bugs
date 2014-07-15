package com.milaboratory.primitivio;

import gnu.trove.map.hash.TByteObjectHashMap;

import java.util.HashMap;

public final class CustomSerializerImpl implements Serializer {
    final HashMap<Class<?>, TypeInfo> infoByClass;
    final TByteObjectHashMap<TypeInfo> infoById;

    public CustomSerializerImpl(HashMap<Class<?>, TypeInfo> infoByClass) {
        this.infoByClass = infoByClass;
        this.infoById = new TByteObjectHashMap<>();
        for (TypeInfo info : infoByClass.values())
            infoById.put(info.id, info);
    }

    @Override
    public void write(PrimitivO output, Object object) {
        Class<?> type = object.getClass();

        // Trying to find appropriate serializer for
        TypeInfo info = infoByClass.get(type);
        if (info == null) {
            Class<?> parent = Util.findSerializableParent(type, true, false);
            info = infoByClass.get(parent);
            if (info == null)
                throw new RuntimeException("Can't find serializer for " + type);
            else
                //Caching this type->info mapping
                infoByClass.put(type, info);
        }

        // Writing type id
        output.writeByte(info.id);
        // Writing content using corresponding sub-serializer
        info.serializer.write(output, object);
    }

    @Override
    public Object read(PrimitivI input) {
        byte id = input.readByte();
        return infoById.get(id).serializer.read(input);
    }

    public static class TypeInfo {
        final byte id;
        final Serializer serializer;

        public TypeInfo(byte id, Serializer serializer) {
            this.id = id;
            this.serializer = serializer;
        }
    }
}

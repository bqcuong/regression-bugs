package com.milaboratory.primitivio;

import com.milaboratory.primitivio.annotations.CustomSerializer;
import com.milaboratory.primitivio.annotations.CustomSerializers;
import com.milaboratory.primitivio.annotations.SerializableBy;
import gnu.trove.map.custom_hash.TObjectByteCustomHashMap;
import gnu.trove.map.hash.TByteObjectHashMap;

import java.util.HashMap;

public class SerializationManager {
    HashMap<Class<?>, TypeSerializationHelper> registeredHelpers = new HashMap<>();

    TypeSerializationHelper getHelper(Class<?> type) {
        return getHelper(type, false);
    }

    TypeSerializationHelper getHelper(Class<?> type, boolean assertNew) {
        TypeSerializationHelper helper;
        if ((helper = registeredHelpers.get(type)) == null)
            registeredHelpers.put(type, helper = createForType(type));
        else if (assertNew)
            throw new IllegalStateException();
        return helper;
    }

    private TypeSerializationHelper createForType(Class<?> type) {
        SerializableBy serializableByAnnotation = type.getAnnotation(SerializableBy.class);
        if (serializableByAnnotation != null) {
            CustomSerializers cssAnnotation = type.getAnnotation(CustomSerializers.class);
            if (cssAnnotation != null)
                return createForTypeWithCustomSerializers(cssAnnotation, serializableByAnnotation);
        }
        return null;
    }

    private TypeSerializationHelper createForTypeWithCustomSerializers(CustomSerializers cssAnnotation,
                                                                       SerializableBy serializableByAnnotation) {
        CustomSerializer[] css = cssAnnotation.value();
        TByteObjectHashMap<Serializer> serializersById = new TByteObjectHashMap<>();
        TObjectByteCustomHashMap<Class> idByClass = new TObjectByteCustomHashMap<>();

        try {
            serializersById.put((byte) 0, serializableByAnnotation.value().newInstance());

            for (CustomSerializer cs : css) {
                serializersById.put(cs.id(), getHelper(cs.type(), true).getSerializer());
                idByClass.put(cs.type(), cs.id());
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return new TypeSerializationHelper(new PCustomSerializer(idByClass, serializersById), true);
    }
}

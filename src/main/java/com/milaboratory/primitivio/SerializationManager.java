package com.milaboratory.primitivio;

import com.milaboratory.primitivio.annotations.CustomSerializer;
import com.milaboratory.primitivio.annotations.CustomSerializers;
import com.milaboratory.primitivio.annotations.SerializableBy;
import gnu.trove.map.custom_hash.TObjectByteCustomHashMap;
import gnu.trove.map.hash.TByteObjectHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SerializationManager {
    final HashMap<Class<?>, TypeSerializationHelper> registeredHelpers = new HashMap<>();

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


    private static List<Class<?>> getAllSerializableInTree(Class<?> type) {
        List<Class<?>> list = new ArrayList<>();
        addAllSerializableInTree(type, list);
        return list;
    }

    private static void addAllSerializableInTree(Class<?> type, List<Class<?>> list) {
        if (type.getAnnotation(SerializableBy.class) != null)
            list.add(type);

        Class<?> superclass = type.getSuperclass();
        if (superclass != null)
            addAllSerializableInTree(superclass, list);

        for (Class<?> cInterface : type.getInterfaces()) {
            addAllSerializableInTree(cInterface, list);
        }
    }

    static Class<?> findRoot(Class<?> type) {
        List<Class<?>> all = getAllSerializableInTree(type);

        if (all.isEmpty())
            return null;

        Class<?> realRoot = findRoot(all.get(0), true);
        

        Class<?> tmp = findRoot(type, true);
        if (tmp != null)
            return tmp;
        tmp = findRoot(type, true);
    }

    static Class<?> findRealRoot(Class<?> type) {
        Class<?> tmp = findRoot(type, true);
        if (tmp != null)
            return tmp;
        tmp = findRoot(type, false);
        return tmp;
    }

    static Class<?> findRoot(Class<?> type, boolean withCustomSerializers) {
        Class<?> root = null, tmp;

        if (type.getAnnotation(SerializableBy.class) != null &&
                (!withCustomSerializers || type.getAnnotation(CustomSerializers.class) != null))
            root = type;

        Class<?> superclass = type.getSuperclass();

        if (superclass != null) {
            tmp = findRoot(superclass, withCustomSerializers);
            if (tmp != null) {
                if (root == null)
                    root = tmp;
                else
                    throw new RuntimeException("Custom serializers conflict between: " + root + " and " + tmp + ".");
            }
        }

        for (Class<?> cInterface : type.getInterfaces()) {
            tmp = findRoot(cInterface, withCustomSerializers);
            if (tmp != null) {
                if (root == null || root == tmp)
                    root = tmp;
                else
                    throw new RuntimeException("Custom serializers conflict:" + root + " and " + tmp + ".");
            }
        }

        return root;
    }

    private TypeSerializationHelper createForTypeWithCustomSerializers(CustomSerializers cssAnnotation,
                                                                       SerializableBy serializableByAnnotation) {
        CustomSerializer[] css = cssAnnotation.value();
        TByteObjectHashMap<Serializer> serializersById = new TByteObjectHashMap<>();
        TObjectByteCustomHashMap<Class> idByClass = new TObjectByteCustomHashMap<>();

        try {
            serializersById.put((byte) 0, serializableByAnnotation.value().newInstance());

            for (CustomSerializer cs : css) {
                serializersById.put(cs.id(), createForType(cs.type()).getSerializer());
                idByClass.put(cs.type(), cs.id());
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return new TypeSerializationHelper(new PCustomSerializer(idByClass, serializersById), true);
    }

    private static final class SearchResult {
        Class<?> withCustomSerializers;
        Class<?> withSerializer;
    }
}

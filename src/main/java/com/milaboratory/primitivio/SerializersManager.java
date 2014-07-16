package com.milaboratory.primitivio;

import com.milaboratory.primitivio.annotations.CustomSerializer;
import com.milaboratory.primitivio.annotations.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.milaboratory.primitivio.Util.findSerializableParent;

public final class SerializersManager {
    final DefaultSerializersProvider defaultSerializersProvider = new DefaultSerializersProviderImpl();
    final HashMap<Class<?>, Serializer> registeredHelpers = new HashMap<>();

    public <T> Serializer<? super T> getSerializer(Class<T> type) {
        Serializer serializer = registeredHelpers.get(type);

        if (serializer == null) {
            Class<?> parent = findSerializableParent(type, true, false);
            serializer = registeredHelpers.get(parent);
            if (serializer != null)
                registeredHelpers.put(type, serializer);
        }

        if (serializer != null)
            return serializer;


        return createAndRegisterSerializer(type);
    }

    private Serializer createAndRegisterSerializer(Class<?> type) {
        Class<?> root = findRoot(type);

        Serializer serializer;
        if (root == null) {
            serializer = defaultSerializersProvider.createSerializer(type, this);
            if (serializer == null)
                throw new RuntimeException("" + type + " is not serializable.");
            else
                root = type;
        } else
            serializer = createSerializer0(root, false);

        registeredHelpers.put(root, serializer);

        if (type != root)
            registeredHelpers.put(type, serializer);

        if (serializer instanceof CustomSerializerImpl)
            for (Class<?> subType : ((CustomSerializerImpl) serializer).infoByClass.keySet())
                registeredHelpers.put(subType, serializer);

        return serializer;
    }

    private Serializer createSerializer0(Class<?> type, boolean nested) {
        Serializable annotation = type.getAnnotation(Serializable.class);

        if (annotation == null)
            throw new IllegalArgumentException("" + type + " is not serializable.");

        try {
            Serializer defaultSerializer =
                    annotation.by() == Serializer.class ?
                            null :
                            annotation.by().newInstance();

            CustomSerializer[] css = annotation.custom();
            if (css.length > 0) {
                if (nested)
                    throw new RuntimeException("Nested custom serializers in " + type + ".");

                HashMap<Class<?>, CustomSerializerImpl.TypeInfo> infoByClass = new HashMap<>();

                // Adding default serializer
                if (defaultSerializer != null)
                    infoByClass.put(type, new CustomSerializerImpl.TypeInfo((byte) 0, defaultSerializer));

                // Adding custom serializers
                for (CustomSerializer cs : css)
                    infoByClass.put(cs.type(), new CustomSerializerImpl.TypeInfo(cs.id(), createSerializer0(cs.type(), true)));

                return new CustomSerializerImpl(infoByClass);
            } else {
                if (defaultSerializer == null)
                    throw new RuntimeException("No serializer for " + type);
                return defaultSerializer;
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static Class<?> findRoot(Class<?> type) {
        List<Class<?>> serializableClasses = getAllSerializableInTree(type);

        if (serializableClasses.isEmpty())
            return null;

        Class<?> realRoot = findRealRoot(serializableClasses.get(0)), tmp;

        for (int i = 1; i < serializableClasses.size(); i++) {
            tmp = findRealRoot(serializableClasses.get(i));
            if (!Objects.equals(tmp, realRoot))
                throw new IllegalArgumentException("Conflict between " + realRoot + " and " + tmp + " through " + serializableClasses.get(i));
        }

        return realRoot;
    }

    /* Utility methods for root calculation */

    private static List<Class<?>> getAllSerializableInTree(Class<?> type) {
        List<Class<?>> list = new ArrayList<>();
        addAllSerializableInTree(type, list);
        return list;
    }

    private static void addAllSerializableInTree(Class<?> type, List<Class<?>> list) {
        if (type.getAnnotation(Serializable.class) != null)
            list.add(type);

        Class<?> superclass = type.getSuperclass();
        if (superclass != null)
            addAllSerializableInTree(superclass, list);

        for (Class<?> cInterface : type.getInterfaces())
            addAllSerializableInTree(cInterface, list);
    }

    private static Class<?> findRealRoot(Class<?> type) {
        Class<?> tmp = findSerializableParent(type, false, true);
        if (tmp != null)
            return tmp;
        tmp = findSerializableParent(type, false, false);
        return tmp;
    }
}

package com.milaboratory.primitivio;

import com.milaboratory.primitivio.annotations.Serializable;

public final class Util {
    private Util() {
    }

    public static Class<?> findSerializableParent(Class<?> type, boolean direct, boolean withCustomSerializersOnly) {
        Class<?> root = null, tmp;

        Serializable a = type.getAnnotation(Serializable.class);
        if (a != null && (!withCustomSerializersOnly || a.custom().length > 0))
            if (direct)
                return type;
            else
                root = type;

        Class<?> superclass = type.getSuperclass();

        if (superclass != null) {
            tmp = findSerializableParent(superclass, direct, withCustomSerializersOnly);
            if (tmp != null) {
                if (root == null)
                    root = tmp;
                else
                    throw new RuntimeException("Custom serializers conflict: " + root + " and " + tmp + " through " + type);
            }
        }

        for (Class<?> cInterface : type.getInterfaces()) {
            tmp = findSerializableParent(cInterface, direct, withCustomSerializersOnly);
            if (tmp != null) {
                if (root == null || root == tmp)
                    root = tmp;
                else
                    throw new RuntimeException("Custom serializers conflict: " + root + " and " + tmp + " through " + type);
            }
        }

        return root;
    }
}

package com.milaboratory.primitivio;

import com.milaboratory.primitivio.annotations.Serializable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class Util {
    private Util() {
    }

    static Class<?> findSerializableParent(Class<?> type, boolean direct, boolean withCustomSerializersOnly) {
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

    public static void writeList(List<?> list, String fileName) throws IOException {
        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(fileName))) {
            writeList(list, stream);
        }
    }

    public static void writeList(List<?> list, File file) throws IOException {
        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file))) {
            writeList(list, stream);
        }
    }

    public static void writeList(List<?> list, OutputStream output) {
        writeList(list, new PrimitivO(output));
    }

    public static void writeList(List<?> list, PrimitivO output) {
        output.writeInt(list.size());
        for (Object o : list)
            output.writeObject(o);
    }

    public static <O> List<O> readList(Class<O> type, String fileName) throws IOException {
        try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(fileName))) {
            return readList(type, stream);
        }
    }

    public static <O> List<O> readList(Class<O> type, File file) throws IOException {
        try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file))) {
            return readList(type, stream);
        }
    }

    public static <O> List<O> readList(Class<O> type, InputStream is) {
        return readList(type, new PrimitivI(is));
    }

    public static <O> List<O> readList(Class<O> type, PrimitivI input) {
        int size = input.readInt();
        List<O> list = new ArrayList<>(size);
        while ((--size) >= 0)
            list.add(input.readObject(type));
        return list;
    }
}

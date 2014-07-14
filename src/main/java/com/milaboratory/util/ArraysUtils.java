package com.milaboratory.util;

public final class ArraysUtils {
    private ArraysUtils() {
    }

    public static void reverse(int[] array, int from, int to) {
        int i, v;
        int length = to - from;
        for (i = 0; i < (length + 1) / 2; ++i) {
            v = array[from + i];
            array[from + i] = array[from + length - i - 1];
            array[from + length - i - 1] = v;
        }
    }

    public static void reverse(int[] array) {
        int i, v;
        for (i = 0; i < (array.length + 1) / 2; ++i) {
            v = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = v;
        }
    }

    public static void reverse(byte[] array) {
        int i;
        byte v;
        for (i = 0; i < (array.length + 1) / 2; ++i) {
            v = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = v;
        }
    }
}

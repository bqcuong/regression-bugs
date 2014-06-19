package com.milaboratory.util;

public final class ArraysUtils {
    private ArraysUtils() {
    }

    public static void reverse(int[] array) {
        int i, v;
        for (i = 0; i < (array.length + 1) / 2; ++i) {
            v = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = v;
        }
    }
}

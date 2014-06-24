package com.milaboratory.core.alignment;

/**
 * CachedIntArray - class which is used for storing alignment matrix.
 */
public final class CachedIntArray {
    private int[] array = null;

    /**
     * Returns {@code int[]} array. If passed {@code #size} argument is more than actual size of CachedIntArray, then
     * CachedIntArray will increase its size to {@code size}.
     *
     * @param size needed sie
     * @return array
     */
    public int[] get(int size) {
        if (array == null || size > array.length)
            return array = new int[size];

        return array;
    }
}

package com.milaboratory.core.io.sequence;

import com.milaboratory.util.ArrayIterator;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public class MultipleSequenceRead implements SequenceRead {
    final SingleRead[] data;

    public MultipleSequenceRead(SingleRead[] data) {
        if (data.length == 0)
            throw new IllegalArgumentException("Empty data.");
        long id = data[0].getId();
        for (int i = 1; i < data.length; ++i)
            if (data[i].getId() != id)
                throw new IllegalArgumentException("Incompatible read ids.");
        this.data = data;
    }

    @Override
    public int numberOfReads() {
        return data.length;
    }

    @Override
    public SingleRead getRead(int i) {
        return data[i];
    }

    @Override
    public long getId() {
        return data[0].getId();
    }

    @Override
    public Iterator<SingleRead> iterator() {
        return new ArrayIterator<>(data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MultipleSequenceRead that = (MultipleSequenceRead) o;

        return Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }
}

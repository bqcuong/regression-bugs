package com.milaboratory.core.sequence;

import java.util.Arrays;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
abstract class AbstractArraySequence<S extends AbstractArraySequence<S>> extends Sequence<S> {
    protected final byte[] data;

    protected AbstractArraySequence(String sequence) {
        this.data = dataFromChars(getAlphabet(), sequence.toCharArray());
    }

    protected AbstractArraySequence(byte[] data) {this.data = data;}

    @Override
    public abstract AbstractArrayAlphabet<S> getAlphabet();

    @Override
    public byte codeAt(int position) {
        return data[position];
    }

    @Override
    public int size() {
        return data.length;
    }

    @Override
    public byte[] asArray() {
        return data.clone();
    }

    @Override
    public S getRange(int from, int to) {
        return getAlphabet().createUnsafe(Arrays.copyOfRange(data, from, to));
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Arrays.equals(data, ((AbstractArraySequence) o).data);
    }

    @Override
    public final int hashCode() {
        int result = getAlphabet().hashCode();
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    protected static byte[] dataFromChars(Alphabet alphabet, char[] chars) {
        byte[] data = new byte[chars.length];
        for (int i = 0; i < chars.length; ++i)
            if ((data[i] = alphabet.codeFromSymbol(chars[i])) == -1)
                throw new IllegalArgumentException("Unknown symbol \"" + chars[i] + "\"");
        return data;
    }
}

package com.milaboratory.core.sequence;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
final class ArraySequenceBuilder<S extends AbstractArraySequence<S>> extends ArraySeqBuilder<S> implements SequenceBuilder<S> {
    private final AbstractArrayAlphabet<S> alphabet;

    ArraySequenceBuilder(AbstractArrayAlphabet<S> alphabet) {
        this.alphabet = alphabet;
    }

    ArraySequenceBuilder(byte[] data, int size, AbstractArrayAlphabet<S> alphabet) {
        super(data, size);
        this.alphabet = alphabet;
    }

    @Override
    S createUnsafe(byte[] b) {
        return alphabet.createUnsafe(b);
    }

    @Override
    byte[] getUnsafe(S s) {
        return s.data;
    }

    @Override
    public ArraySequenceBuilder<S> set(int position, byte letter) {
        if (position < 0 || position >= size)
            throw new IndexOutOfBoundsException();
        data[position] = letter;
        return this;
    }

    @Override
    public ArraySequenceBuilder<S> append(byte letter) {
        ensureInternalCapacity(size + 1);
        data[size++] = letter;
        return this;
    }

    @Override
    public ArraySequenceBuilder<S> append(S seq) {
        super.append(seq);
        return this;
    }

    @Override
    public ArraySequenceBuilder<S> clone() {
        return new ArraySequenceBuilder<>(data == null ? null : data.clone(), size, alphabet);
    }

    @Override
    public ArraySequenceBuilder<S> ensureCapacity(int capacity) {
        super.ensureCapacity(capacity);
        return this;
    }
}

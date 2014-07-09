package com.milaboratory.core.sequence;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
final class ArraySequenceBuilder<S extends AbstractArraySequence<S>> extends ArraySeqBuilder<S> {
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
    public SequenceBuilder<S> clone() {
        return new ArraySequenceBuilder<>(data == null ? null : data.clone(), size, alphabet);
    }
}

package com.milaboratory.core.sequence;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
abstract class AbstractArrayAlphabet<S extends AbstractArraySequence> extends Alphabet<S>  {
    protected AbstractArrayAlphabet(String alphabetName) {
        super(alphabetName);
    }

    @Override
    public SequenceBuilder<S> getBuilder() {
        return new ArraySequenceBuilder<S>(this);
    }

    abstract S createUnsafe(byte[] array);
}

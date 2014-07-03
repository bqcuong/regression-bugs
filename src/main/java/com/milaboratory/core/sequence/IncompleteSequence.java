package com.milaboratory.core.sequence;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
abstract class IncompleteSequence<IS extends IncompleteSequence<IS, S>, S extends Sequence<S>>
        extends AbstractArraySequence<IS> {
    IncompleteSequence(byte[] data) {
        super(data);
    }

    protected IncompleteSequence(String sequence) {
        super(sequence);
    }

    @Override
    public abstract IncompleteAlphabet<IS, S> getAlphabet();

    public final boolean isComplete() {
        for (byte c : data)
            if (c == getAlphabet().getUnknownLetterCode())
                return false;
        return true;
    }

    public final S convertToComplete() {
        if (!isComplete())
            throw new RuntimeException("This sequence contains incomplete parts.");
        SequenceBuilder<S> builder = getAlphabet().getOrigin()
                .getBuilder().ensureCapacity(data.length);
        for (byte b : data)
            builder.append(b);
        return builder.createAndDestroy();
    }
}

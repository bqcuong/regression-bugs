package com.milaboratory.core.io.sequence;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */

public final class PairedRead extends MultipleSequenceRead {
    public PairedRead(SingleRead[] data) {
        super(data);
        if (data.length != 2)
            throw new IllegalArgumentException();
    }

    public SingleRead getLeftRead() {
        return data[0];
    }

    public SingleRead getRightRead() {
        return data[1];
    }
}

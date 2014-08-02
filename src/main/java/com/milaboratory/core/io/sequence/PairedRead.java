package com.milaboratory.core.io.sequence;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public final class PairedRead extends MultiRead {
    public PairedRead(SingleRead... data) {
        super(data);
        if (data.length != 2)
            throw new IllegalArgumentException();
    }

    public SingleRead getR1() {
        return data[0];
    }

    public SingleRead getR2() {
        return data[1];
    }
}

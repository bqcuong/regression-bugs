package com.milaboratory.core.io.sequence;

/**
 * Created by dbolotin on 23/06/14.
 */
public class PairedReaderImpl extends AbstractMultiReader<PairedRead> implements PairedReader {
    public PairedReaderImpl(SingleReader... readers) {
        super(readers);
    }

    @Override
    public PairedRead take() {
        SingleRead[] singleReads = takeReads();

        if (singleReads == null)
            return null;

        return new PairedRead(singleReads);
    }
}

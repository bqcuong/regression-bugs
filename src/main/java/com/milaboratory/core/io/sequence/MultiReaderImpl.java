package com.milaboratory.core.io.sequence;

public class MultiReaderImpl extends AbstractMultiReader<MultiRead> implements MultiReader {
    public MultiReaderImpl(SingleReader... readers) {
        super(readers);
    }

    @Override
    public MultiRead take() {
        SingleRead[] singleReads = takeReads();

        if (singleReads == null)
            return null;

        return new MultiRead(singleReads);
    }
}

package com.milaboratory.core.io.sequence;

public final class MultiReader extends AbstractMultiReader<MultiRead> {
    public MultiReader(SingleReader... readers) {
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

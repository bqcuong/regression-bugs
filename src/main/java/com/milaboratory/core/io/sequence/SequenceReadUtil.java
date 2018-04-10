package com.milaboratory.core.io.sequence;

public final class SequenceReadUtil {
    private SequenceReadUtil() {}

    public static SequenceRead setReadId(long readId, SequenceRead read) {
        if (readId == read.getId())
            return read;

        if (read instanceof SingleReadLazy)
            return ((SingleReadLazy) read).setReadId(readId);

        if (read.numberOfReads() == 1) {
            SingleRead sRead = (SingleRead) read;
            return new SingleReadImpl(readId, sRead.getData(), sRead.getDescription());
        }

        int nReads = read.numberOfReads();
        SingleRead[] reads = new SingleRead[nReads];
        for (int i = 0; i < reads.length; i++)
            reads[i] = (SingleRead) setReadId(readId, read.getRead(i));
        if (nReads == 1)
            return reads[0];
        else if (nReads == 2)
            return new PairedRead(reads);
        else
            return new MultiRead(reads);
    }
}

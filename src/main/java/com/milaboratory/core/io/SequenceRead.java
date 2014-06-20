package com.milaboratory.core.io;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public interface SequenceRead extends Iterable<SingleRead> {
    int numberOfReads();

    SingleRead getRead(int i);

    long getId();
}

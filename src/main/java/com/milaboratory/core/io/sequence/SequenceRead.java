package com.milaboratory.core.io.sequence;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public interface SequenceRead extends Iterable<SingleRead> {
    int numberOfReads();

    SingleRead getRead(int i);

    long getId();
}

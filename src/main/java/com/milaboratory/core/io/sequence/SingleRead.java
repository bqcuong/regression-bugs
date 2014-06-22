package com.milaboratory.core.io.sequence;

import com.milaboratory.core.sequence.NSequenceWithQuality;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public interface SingleRead extends SequenceRead {
    String getDescription();

    NSequenceWithQuality getData();
}

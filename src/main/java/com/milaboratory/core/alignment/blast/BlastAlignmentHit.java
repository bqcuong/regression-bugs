package com.milaboratory.core.alignment.blast;

import com.milaboratory.core.alignment.Alignment;
import com.milaboratory.core.alignment.AlignmentHit;
import com.milaboratory.core.sequence.Sequence;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public final class BlastAlignmentHit<S extends Sequence<S>> implements AlignmentHit<S> {
    private final String stringId;
    private final int id;
    private final Alignment<S> alignment;

    public BlastAlignmentHit(String stringId, int id, Alignment<S> alignment) {
        this.stringId = stringId;
        this.id = id;
        this.alignment = alignment;
    }

    public String getStringId() {
        return stringId;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Alignment<S> getAlignment() {
        return alignment;
    }
}

package com.milaboratory.core.io.binary;

import com.milaboratory.core.Range;
import com.milaboratory.core.alignment.Alignment;
import com.milaboratory.core.mutations.Mutations;
import com.milaboratory.core.sequence.Sequence;
import com.milaboratory.primitivio.PrimitivI;
import com.milaboratory.primitivio.PrimitivO;
import com.milaboratory.primitivio.Serializer;

public class AlignmentSerializer implements Serializer<Alignment> {
    @Override
    public void write(PrimitivO output, Alignment object) {
        output.writeObject(object.getSequence1());
        output.writeObject(object.getAbsoluteMutations());
        output.writeObject(object.getSequence1Range());
        output.writeObject(object.getSequence2Range());
        output.writeFloat(object.getScore());
    }

    @Override
    public Alignment read(PrimitivI input) {
        Sequence sequence = input.readObject(Sequence.class);
        Mutations mutations = input.readObject(Mutations.class);
        Range range1 = input.readObject(Range.class);
        Range range2 = input.readObject(Range.class);
        return new Alignment(sequence, mutations, range1, range2, input.readFloat());
    }

    @Override
    public boolean isReference() {
        return false;
    }

    @Override
    public boolean handlesReference() {
        return false;
    }
}

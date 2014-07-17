package com.milaboratory.core.io.binary;

import com.milaboratory.core.Range;
import com.milaboratory.primitivio.PrimitivI;
import com.milaboratory.primitivio.PrimitivO;
import com.milaboratory.primitivio.Serializer;

public class RangeSerializer implements Serializer<Range> {
    @Override
    public void write(PrimitivO output, Range object) {
        output.writeInt(object.getFrom());
        output.writeInt(object.getTo());
    }

    @Override
    public Range read(PrimitivI input) {
        int from = input.readInt();
        return new Range(from, input.readInt());
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

package com.milaboratory.core.motif;

import com.milaboratory.core.sequence.Alphabet;
import com.milaboratory.core.sequence.Sequence;
import com.milaboratory.util.BitArray;

public final class MotifBuilder<S extends Sequence<S>> {
    private final Alphabet<S> alphabet;
    private final int size;
    BitArray data;

    public MotifBuilder(Alphabet<S> alphabet, int size) {
        this.alphabet = alphabet;
        this.size = size;
        this.data = new BitArray(alphabet.size() * size);
    }

    public void setAllowedLetter(int position, byte letter) {
        data.set(letter * size + position);
    }

    public Motif<S> createAndDestroy() {
        BitArray d = data;
        data = null;
        return new Motif<>(alphabet, size, d);
    }
}

package com.milaboratory.core.mutations;

import com.milaboratory.core.sequence.Alphabet;
import com.milaboratory.primitivio.PrimitivI;
import com.milaboratory.primitivio.PrimitivO;
import com.milaboratory.primitivio.Serializer;

class IO {
    private IO() {
    }

    public static class MutationsSerializer implements Serializer<Mutations> {
        @Override
        public void write(PrimitivO output, Mutations object) {
            output.writeObject(object.alphabet);
            output.writeObject(object.mutations);
        }

        @Override
        public Mutations read(PrimitivI input) {
            Alphabet alphabet = input.readObject(Alphabet.class);
            return new Mutations(alphabet, input.readObject(int[].class), true);
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
}

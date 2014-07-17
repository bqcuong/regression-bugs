package com.milaboratory.core.sequence;

import com.milaboratory.primitivio.PrimitivI;
import com.milaboratory.primitivio.PrimitivO;
import com.milaboratory.primitivio.Serializer;
import com.milaboratory.util.Bit2Array;

import java.io.IOException;

final class IO {
    private IO() {
    }

    public static class AlphabetSerializer implements Serializer<Alphabet> {
        @Override
        public void write(PrimitivO output, Alphabet object) {
            output.writeByte(object.getId());
        }

        @Override
        public Alphabet read(PrimitivI input) {
            return Alphabets.getById(input.readByte());
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

    public static class SequenceSerializer implements Serializer<Sequence> {
        @Override
        public void write(PrimitivO output, Sequence object) {
            output.writeObject(object.getAlphabet());
            output.writeObject(object.asArray());
        }

        @Override
        public Sequence read(PrimitivI input) {
            Alphabet alphabet = input.readObject(Alphabet.class);
            return alphabet.getBuilder().append(input.readObject(byte[].class)).createAndDestroy();
        }

        @Override
        public boolean isReference() {
            return true;
        }

        @Override
        public boolean handlesReference() {
            return false;
        }
    }

    public static class NucleotideSequenceSerializer implements Serializer<NucleotideSequence> {
        @Override
        public void write(PrimitivO output, NucleotideSequence object) {
            try {
                object.data.writeTo(output);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public NucleotideSequence read(PrimitivI input) {
            try {
                return new NucleotideSequence(Bit2Array.readFrom(input), true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean isReference() {
            return true;
        }

        @Override
        public boolean handlesReference() {
            return false;
        }
    }

    public static class SequenceQualitySerializer implements Serializer<SequenceQuality> {
        @Override
        public void write(PrimitivO output, SequenceQuality object) {
            output.writeObject(object.data);
        }

        @Override
        public SequenceQuality read(PrimitivI input) {
            return new SequenceQuality(input.readObject(byte[].class));
        }

        @Override
        public boolean isReference() {
            return true;
        }

        @Override
        public boolean handlesReference() {
            return false;
        }
    }

    public static class NSequenceWithQualitySerializer implements Serializer<NSequenceWithQuality> {
        @Override
        public void write(PrimitivO output, NSequenceWithQuality object) {
            output.writeObject(object.sequence);
            output.writeObject(object.quality);
        }

        @Override
        public NSequenceWithQuality read(PrimitivI input) {
            NucleotideSequence seq = input.readObject(NucleotideSequence.class);
            SequenceQuality qual = input.readObject(SequenceQuality.class);
            return new NSequenceWithQuality(seq, qual);
        }

        @Override
        public boolean isReference() {
            return true;
        }

        @Override
        public boolean handlesReference() {
            return false;
        }
    }
}

package com.milaboratory.core.sequence;

import com.milaboratory.util.Bit2Array;
import com.milaboratory.util.HashFunctions;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */

public final class UnsafeFactory {
    private UnsafeFactory() {
    }

    public static NSequenceWithQuality fastqParse(final String sequenceString,
                                                  final String qualityString,
                                                  final byte qualityValueOffset,
                                                  final long id) {
        assert sequenceString.length() == qualityString.length();
        Bit2Array data = new Bit2Array(sequenceString.length());
        byte[] quality = new byte[sequenceString.length()];
        byte code;
        for (int i = 0; i < sequenceString.length(); ++i) {
            quality[i] = (byte) (qualityString.charAt(i) - qualityValueOffset);
            code = NucleotideSequence.ALPHABET.codeFromSymbol(sequenceString.charAt(i));
            if (code == -1) {
                code = rndByte(i, id);
                quality[i] = 0;
            }
            data.set(i, code);
        }
        return new NSequenceWithQuality(new NucleotideSequence(data, true),
                new SequenceQuality(quality, true));
    }

    public static NSequenceWithQuality fastqParse(
            byte[] buffer,
            int fromSequence,
            int fromQuality,
            int length,
            byte qualityValueOffset,
            long id) {
        Bit2Array data = new Bit2Array(length);
        byte[] quality = new byte[length];
        int pointerSeq = fromSequence, pointerQua = fromQuality;
        byte code;
        for (int i = 0; i < length; ++i) {
            quality[i] = (byte) (buffer[pointerQua++] - qualityValueOffset);
            code = NucleotideAlphabet.codeFromSymbolByte(buffer[pointerSeq++]);
            if (code == -1) {
                code = rndByte(i, id);
                quality[i] = 0;
            }
            data.set(i, code);
        }
        return new NSequenceWithQuality(new NucleotideSequence(data, true),
                new SequenceQuality(quality, true));
    }

    private static byte rndByte(int i, long id) {
        return (byte) (HashFunctions.JenkinWang64shift(i + id) & 3); // :)
    }
}

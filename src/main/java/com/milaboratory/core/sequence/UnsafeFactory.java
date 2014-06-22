package com.milaboratory.core.sequence;

import com.milaboratory.core.io.sequence.fastq.QualityFormat;
import com.milaboratory.util.Bit2Array;
import com.milaboratory.util.HashFunctions;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */

public final class UnsafeFactory {
    private UnsafeFactory() {
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
                code = (byte) (HashFunctions.JenkinWang64shift(i + id) & 3); // :)
                quality[i] = 0;
            }
            data.set(i, code);
        }
        return new NSequenceWithQuality(new NucleotideSequence(data, true),
                new SequenceQuality(quality, true));
    }
}

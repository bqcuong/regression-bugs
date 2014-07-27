package com.milaboratory.core.motif;

import com.milaboratory.core.sequence.Alphabet;
import com.milaboratory.core.sequence.Sequence;
import com.milaboratory.core.sequence.WildcardSymbol;
import com.milaboratory.core.sequence.WithWildcards;
import com.milaboratory.util.BitArray;

import java.util.Arrays;

public final class Motif<S extends Sequence<S>> {
    private final Alphabet<S> alphabet;
    private final int size;
    final BitArray data;

    public Motif(Alphabet<S> alphabet, String motif) {
        this.alphabet = alphabet;
        this.size = motif.length();
        int alphabetSize = alphabet.size();
        this.data = new BitArray(alphabetSize * size);
        if (alphabet instanceof WithWildcards) {
            WithWildcards wildcardAlphabet = (WithWildcards) alphabet;
            for (int i = 0; i < size; ++i) {
                final WildcardSymbol wildcard = wildcardAlphabet.getWildcardFor(motif.charAt(i));
                if (wildcard == null)
                    throw new IllegalArgumentException("Unknown wildcard " + motif.charAt(i));
                for (int j = 0; j < wildcard.size(); ++j)
                    data.set(wildcard.getCode(j) * size + i);
            }
        } else {
            for (int i = 0; i < size; ++i) {
                byte code = alphabet.codeFromSymbol(motif.charAt(i));
                if (code == -1)
                    throw new IllegalArgumentException("Unknown symbol " + motif.charAt(i));
                data.set(code * size + i);
            }
        }
    }

    public BitapPattern toBitapPattern() {
        int aSize = alphabet.size();
        long[] patternMask = new long[aSize];
        Arrays.fill(patternMask, ~0);
        int p = 0;
        for (int i = 0; i < aSize; ++i)
            for (int j = 0; j < size; ++j)
                if (data.get(p++))
                    patternMask[i] &= ~(1L << j);
        return new BitapPattern(size, patternMask);
    }

    public int size() {
        return size;
    }

    public boolean allows(byte code, int position) {
        return data.get(code * size + position);
    }

    public boolean matches(S sequence, int from) {
        if (from < 0 || from + size > sequence.size())
            throw new IndexOutOfBoundsException();
        for (int i = 0; i < size; ++i)
            if (!allows(sequence.codeAt(from++), i))
                return false;
        return true;
    }
}

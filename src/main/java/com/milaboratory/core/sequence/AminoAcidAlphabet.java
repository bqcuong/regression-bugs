/*
 * Copyright 2015 MiLaboratory.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.milaboratory.core.sequence;

import gnu.trove.map.hash.TCharObjectHashMap;

import java.util.Collection;
import java.util.Collections;

import static java.lang.Character.toUpperCase;

/**
 * Amino acid alphabet with additional symbols.<br/> "~" - non full codon. 2 or 1 nucleotides.<br/> "-" - no nucleotides
 * <p>
 * This class is a singleton, and the access provided by
 * {@link com.milaboratory.core.sequence.AminoAcidSequence#ALPHABET}
 * or {@link #INSTANCE}.
 * </p>
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 * @see com.milaboratory.core.sequence.Alphabet
 * @see com.milaboratory.core.sequence.AminoAcidSequence
 * @see com.milaboratory.core.sequence.Sequence
 */
public final class AminoAcidAlphabet extends AbstractArrayAlphabet<AminoAcidSequence> implements WithWildcards {
    static final AminoAcidAlphabet INSTANCE = new AminoAcidAlphabet();
    public static final byte Stop = 0;
    public static final byte A = 1;
    public static final byte C = 2;
    public static final byte D = 3;
    public static final byte E = 4;
    public static final byte F = 5;
    public static final byte G = 6;
    public static final byte H = 7;
    public static final byte I = 8;
    public static final byte K = 9;
    public static final byte L = 10;
    public static final byte M = 11;
    public static final byte N = 12;
    public static final byte P = 13;
    public static final byte Q = 14;
    public static final byte R = 15;
    public static final byte S = 16;
    public static final byte T = 17;
    public static final byte V = 18;
    public static final byte W = 19;
    public static final byte Y = 20;
    public static final byte IncompleteCodon = 21;

    public static final WildcardSymbol X = new WildcardSymbol('X', (byte) 22, new byte[]{A, C, D, E, F, G, H, I, K, L, M, N, P, Q, R, S, T, V, W, Y});
    public static final WildcardSymbol B = new WildcardSymbol('B', (byte) 23, new byte[]{N, D});
    public static final WildcardSymbol J = new WildcardSymbol('J', (byte) 24, new byte[]{I, L});
    public static final WildcardSymbol Z = new WildcardSymbol('Z', (byte) 25, new byte[]{E, Q});

    private static final char[] aa = {
            '*',
            'A', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'K', 'L',
            'M', 'N', 'P', 'Q', 'R',
            'S', 'T', 'V', 'W', 'Y',
            '_'};

    private AminoAcidAlphabet() {
        super("aminoacid", (byte) 2);
    }

    @Override
    public char symbolFromCode(byte code) {
        return aa[code];
    }

    @Override
    public byte codeFromSymbol(char symbol) {
        // Special case for backward compatibility
        if (symbol == '~')
            return IncompleteCodon;

        // For case insensitive conversion
        symbol = toUpperCase(symbol);

        // Normal conversion (can be optimized :) )
        for (int i = 0; i < aa.length; ++i)
            if (aa[i] == symbol)
                return (byte) i;

        // Unknown symbol
        return -1;
    }

    @Override
    public int size() {
        return aa.length;
    }

    @Override
    AminoAcidSequence createUnsafe(byte[] array) {
        return new AminoAcidSequence(array, true);
    }

    private static TCharObjectHashMap<WildcardSymbol> wildcardsMap =
            new WildcardMapBuilder()
                    /* Exact match letters */
                    .addAlphabet(INSTANCE)
                    .addWildcards(X, B, J, Z)
                    .get();

    @Override
    public Collection<WildcardSymbol> getAllWildcards() {
        return Collections.unmodifiableCollection(wildcardsMap.valueCollection());
    }

    @Override
    public WildcardSymbol getWildcardFor(char symbol) {
        return wildcardsMap.get(toUpperCase(symbol));
    }
}

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

import gnu.trove.impl.Constants;
import gnu.trove.map.hash.TCharByteHashMap;
import gnu.trove.map.hash.TCharObjectHashMap;

import java.util.Collection;
import java.util.Collections;

import static java.lang.Character.toUpperCase;

/**
 * Amino acid alphabet with additional symbols.
 *
 * <p>"_" represents incomplete codon (residual 1 or 2 nucleotides remaining after translation of all full codons)</p>
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 *
 * @see com.milaboratory.core.sequence.Alphabet
 * @see com.milaboratory.core.sequence.AminoAcidSequence
 * @see com.milaboratory.core.sequence.Sequence
 */
public final class AminoAcidAlphabet extends AbstractArrayAlphabet<AminoAcidSequence> implements WithWildcards {
    static final AminoAcidAlphabet INSTANCE = new AminoAcidAlphabet();
    /**
     * Stop codon byte representation
     */
    public static final byte Stop = 0;
    /**
     * Alanine byte representation
     */
    public static final byte A = 1;
    /**
     * Cysteine byte representation
     */
    public static final byte C = 2;
    /**
     * Aspartic Acid byte representation
     */
    public static final byte D = 3;
    /**
     * Glutamic Acid representation
     */
    public static final byte E = 4;
    /**
     * Phenylalanine byte representation
     */
    public static final byte F = 5;
    /**
     * Glycine byte representation
     */
    public static final byte G = 6;
    /**
     * Histidine byte representation
     */
    public static final byte H = 7;
    /**
     * Isoleucine byte representation
     */
    public static final byte I = 8;
    /**
     * Lysine byte representation
     */
    public static final byte K = 9;
    /**
     * Leucine byte representation
     */
    public static final byte L = 10;
    /**
     * Methionine byte representation
     */
    public static final byte M = 11;
    /**
     * Asparagine byte representation
     */
    public static final byte N = 12;
    /**
     * Proline byte representation
     */
    public static final byte P = 13;
    /**
     * Glutamine byte representation
     */
    public static final byte Q = 14;
    /**
     * Arginine byte representation
     */
    public static final byte R = 15;
    /**
     * Serine byte representation
     */
    public static final byte S = 16;
    /**
     * Threonine byte representation
     */
    public static final byte T = 17;
    /**
     * Valine byte representation
     */
    public static final byte V = 18;
    /**
     * Tryptophan byte representation
     */
    public static final byte W = 19;
    /**
     * Tyrosine byte representation
     */
    public static final byte Y = 20;
    /**
     * Stop codon byte representation
     */
    public static final byte IncompleteCodon = 21;

    /**
     * Any amino acid
     */
    public static final WildcardSymbol X = new WildcardSymbol('X', (byte) 22, new byte[]{A, C, D, E, F, G, H, I, K, L, M, N, P, Q, R, S, T, V, W, Y});
    /**
     * Aspartic acid or Asparagine (N or D)
     */
    public static final WildcardSymbol B = new WildcardSymbol('B', (byte) 23, new byte[]{N, D});
    /**
     * Leucine or Isoleucine (I or L)
     */
    public static final WildcardSymbol J = new WildcardSymbol('J', (byte) 24, new byte[]{I, L});
    /**
     * Glutamine or Glutamic acid (E or Q)
     */
    public static final WildcardSymbol Z = new WildcardSymbol('Z', (byte) 25, new byte[]{E, Q});

    private static final char[] aa = {
            '*',
            'A', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'K', 'L',
            'M', 'N', 'P', 'Q', 'R',
            'S', 'T', 'V', 'W', 'Y',
            '_'};
    private static final TCharByteHashMap symbolToCode = new TCharByteHashMap(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, (char) -1, (byte) -1);

    static {
        for (byte i = 0; i < aa.length; i++)
            symbolToCode.put(aa[i], i);
    }

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

        // Normal conversion (-1 will be returned for unknown symbols, see map constructor parameters)
        return symbolToCode.get(symbol);
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

    private final Collection<WildcardSymbol> allWildcardSymbols = Collections.unmodifiableCollection(wildcardsMap.valueCollection());

    @Override
    public Collection<WildcardSymbol> getAllWildcards() {
        return allWildcardSymbols;
    }

    @Override
    public WildcardSymbol getWildcardFor(char symbol) {
        return wildcardsMap.get(toUpperCase(symbol));
    }
}

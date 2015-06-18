package com.milaboratory.core.sequence;

import gnu.trove.impl.Constants;
import gnu.trove.map.hash.TCharByteHashMap;

import java.util.Collection;

/**
 * Alphabet for sequences with wildcards.
 *
 * @param <IS> type of incomplete sequence
 * @param <S>  type of sequence for incomplete sequence is defined: for example, if IS is
 *             {@code IncompleteNucleotideSequence}, then S is {@code NucleotideSequence}
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 * @see com.milaboratory.core.sequence.Alphabet
 * @see AbstractSequenceWithWildcards
 */
public abstract class AlphabetWithWildcards<IS extends AbstractSequenceWithWildcards<IS, S>, S extends Sequence<S>>
        extends AbstractArrayAlphabet<IS> {
    static final int WILDCARDS_ALPHABET_ID_OFFSET = 64;
    private final char[] symbols;
    private final WildcardSymbol[] wildcards;
    private final Alphabet<S> alphabet;
    private final TCharByteHashMap symbolToCode;

    public AlphabetWithWildcards(Alphabet<S> alphabet) {
        super(alphabet.getAlphabetName() + "_with_wildcards", (byte) (alphabet.getId() + WILDCARDS_ALPHABET_ID_OFFSET));

        if (!(alphabet instanceof DefinesWildcards))
            throw new IllegalArgumentException();

        Collection<WildcardSymbol> wildcardsCollection = ((DefinesWildcards) alphabet).getAllWildcards();
        this.symbols = new char[wildcardsCollection.size()];
        this.wildcards = new WildcardSymbol[wildcardsCollection.size()];
        this.alphabet = alphabet;
        this.symbolToCode = new TCharByteHashMap(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, (char) -1, (byte) -1);

        for (WildcardSymbol w : wildcardsCollection) {
            if (symbols[w.wildcardCode] != 0)
                throw new IllegalArgumentException();
            this.symbols[w.wildcardCode] = w.cSymbol;
            this.symbolToCode.put(w.cSymbol, w.wildcardCode);
            this.wildcards[w.wildcardCode] = w;
        }

        for (char symbol : symbols) if (symbol == 0) throw new IllegalArgumentException();
    }

    @Override
    public char symbolFromCode(byte code) {
        return symbols[code];
    }

    @Override
    public int size() {
        return symbols.length;
    }

    @Override
    public byte codeFromSymbol(char symbol) {
        return symbolToCode.get(Character.toUpperCase(symbol));
    }

    /**
     * Returns an alphabet for parent type of sequence.
     *
     * @return alphabet for parent type of sequence
     */
    public Alphabet<S> getOrigin() {
        return alphabet;
    }

    /**
     * Returns wildcard defined by specified code (letter).
     *
     * @param code code
     * @return wildcard defined by specified code (letter)
     */
    public WildcardSymbol getWildcard(byte code) {
        return wildcards[code];
    }
}

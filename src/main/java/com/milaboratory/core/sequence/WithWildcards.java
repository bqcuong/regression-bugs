package com.milaboratory.core.sequence;

import java.util.Collection;

/**
 * Interface specifies that alphabet contains wild cards.
 *
 * @see com.milaboratory.core.sequence.NucleotideAlphabet
 * @see com.milaboratory.core.sequence.WildcardSymbol
 */
public interface WithWildcards {
    /**
     * Returns a collection of all wildcards defined for this.
     *
     * @return a collection of all wildcards defined for this.
     */
    Collection<WildcardSymbol> getAllWildcards();

    /**
     * Returns a wildcard object for specified letter.
     *
     * @param symbol symbol
     * @return wildcard object for specified letter
     */
    WildcardSymbol getWildcardFor(char symbol);
}

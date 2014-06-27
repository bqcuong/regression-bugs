package com.milaboratory.core.sequence;

import java.util.Collection;

public interface WithWildcards {
    Collection<WildcardSymbol> getAllWildcards();

    WildcardSymbol getWildcardFor(char symbol);
}

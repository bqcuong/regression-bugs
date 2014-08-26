package com.milaboratory.core.sequence;

import gnu.trove.map.hash.TCharObjectHashMap;

import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;

final class WildcardMapBuilder {
    final TCharObjectHashMap<WildcardSymbol> map = new TCharObjectHashMap<>();

    WildcardMapBuilder addWildcard(char c, byte... codes) {
        char uC = toUpperCase(c);
        WildcardSymbol wcSymbol = new WildcardSymbol(uC, codes);
        map.put(toLowerCase(c), wcSymbol);
        map.put(uC, wcSymbol);
        return this;
    }

    WildcardMapBuilder addAlphabet(Alphabet alphabet) {
        for (byte i = 0; i < alphabet.size(); ++i) {
            char c = alphabet.symbolFromCode(i);
            map.put(c, new WildcardSymbol(c, i));
        }
        return this;
    }

    TCharObjectHashMap<WildcardSymbol> get() {
        return map;
    }
}

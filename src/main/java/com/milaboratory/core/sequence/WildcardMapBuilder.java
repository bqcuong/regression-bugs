package com.milaboratory.core.sequence;

import gnu.trove.map.hash.TCharObjectHashMap;

final class WildcardMapBuilder {
    final TCharObjectHashMap<WildcardSymbol> map = new TCharObjectHashMap<>();

    WildcardMapBuilder addWildcard(char c, byte... codes) {
        map.put(c, new WildcardSymbol(c, codes));
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

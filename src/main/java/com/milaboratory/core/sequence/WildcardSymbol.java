package com.milaboratory.core.sequence;

import java.util.Arrays;

import static java.util.Arrays.binarySearch;

public final class WildcardSymbol {
    final char cSymbol;
    final byte bSymbol;
    final byte[] codes;

    public WildcardSymbol(char cSymbol, byte... codes) {
        this.cSymbol = cSymbol;
        this.bSymbol = (byte) cSymbol;
        this.codes = codes.clone();
        Arrays.sort(this.codes);
    }

    public char getSymbol() {
        return cSymbol;
    }

    public byte getByteSymbol() {
        return bSymbol;
    }

    public int size() {
        return codes.length;
    }

    public byte getCode(int index) {
        return codes[index];
    }

    public boolean contains(byte code) {
        return binarySearch(codes, code) >= 0;
    }
}

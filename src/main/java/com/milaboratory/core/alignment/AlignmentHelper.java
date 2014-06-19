package com.milaboratory.core.alignment;

import com.milaboratory.util.BitArray;

import java.util.Arrays;

public class AlignmentHelper {
    final String seq1String, seq2String;
    final int[] seq1Position, seq2Position;
    final BitArray match;
    final int offset;

    public AlignmentHelper(String seq1String, String seq2String, int[] seq1Position, int[] seq2Position, BitArray match) {
        this.seq1String = seq1String;
        this.seq2String = seq2String;
        this.seq1Position = seq1Position;
        this.seq2Position = seq2Position;
        this.match = match;
        this.offset = Math.max(("" + seq2Position[0]).length(), ("" + seq1Position[0]).length());
    }

    public double identity() {
        return match.bitCount() * 1.0 / match.size();
    }

    public int size() {
        return match.size();
    }

    public int convertPositionToSeq1(int i) {
        return seq1Position[i];
    }

    public int convertPositionToSeq2(int i) {
        return seq2Position[i];
    }

    public String getLine1() {
        String startPosition = String.valueOf(seq1Position[0]);
        int spaces = offset - startPosition.length();
        return spaces(spaces) + startPosition + " " + seq1String;
    }

    public String getLine2() {
        char[] chars = new char[match.size()];
        Arrays.fill(chars, ' ');
        for (int n : match.getBits())
            chars[n] = '|';
        return (spaces(offset + 1) + new String(chars));
    }

    public String getLine3() {
        String startPosition = String.valueOf(seq2Position[0]);
        int spaces = offset - startPosition.length();
        return spaces(spaces) + startPosition + " " + seq2String;
    }

    @Override
    public String toString() {
        return getLine1() + "\n" + getLine2() + "\n" + getLine3();
    }


    private static String spaces(int n) {
        char[] c = new char[n];
        Arrays.fill(c, ' ');
        return String.valueOf(c);
    }
}

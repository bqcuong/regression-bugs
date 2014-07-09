package com.milaboratory.core.alignment;

import com.milaboratory.util.BitArray;

import java.util.Arrays;

public class AlignmentHelper {
    protected final String seq1String, seq2String;
    protected final int[] seq1Position, seq2Position;
    protected final BitArray match;
    protected final int offset;

    public AlignmentHelper(String seq1String, String seq2String, int[] seq1Position, int[] seq2Position, BitArray match) {
        this.seq1String = seq1String;
        this.seq2String = seq2String;
        this.seq1Position = seq1Position;
        this.seq2Position = seq2Position;
        this.match = match;
        this.offset = Math.max(("" + a(seq2Position[0])).length(), ("" + a(seq1Position[0])).length());
    }

    public double identity() {
        return match.bitCount() * 1.0 / match.size();
    }

    public int size() {
        return match.size();
    }

    public int getSequence1PositionAt(int i) {
        return seq1Position[i];
    }

    public int getSequence2PositionAt(int i) {
        return seq2Position[i];
    }

    public String getLine1() {
        String startPosition = String.valueOf(a(seq1Position[0]));
        int spaces = offset - startPosition.length();
        return spaces(spaces) + startPosition + " " + seq1String +
                " " + a(seq1Position[seq1Position.length - 1]);
    }

    public String getLine1Compact() {
        String startPosition = String.valueOf(a(seq1Position[0]));
        int spaces = offset - startPosition.length();
        return spaces(spaces) + startPosition + " " + toCompact(seq1String) +
                " " + a(seq1Position[seq1Position.length - 1]);
    }

    public String getLine2() {
        char[] chars = new char[match.size()];
        Arrays.fill(chars, ' ');
        for (int n : match.getBits())
            chars[n] = '|';
        return (spaces(offset + 1) + new String(chars));
    }

    public String getLine3() {
        String startPosition = String.valueOf(a(seq2Position[0]));
        int spaces = offset - startPosition.length();
        return spaces(spaces) + startPosition + " " + seq2String +
                " " + a(seq2Position[seq2Position.length - 1]);
    }

    public String getLine3Compact() {
        String startPosition = String.valueOf(a(seq2Position[0]));
        int spaces = offset - startPosition.length();
        return spaces(spaces) + startPosition + " " + toCompact(seq2String) +
                " " + a(seq2Position[seq2Position.length - 1]);
    }

    private String toCompact(String seqString) {
        char[] chars = seqString.toCharArray();
        for (int i = 0; i < match.size(); ++i)
            if (!match.get(i))
                chars[i] = Character.toLowerCase(chars[i]);
        return new String(chars);
    }

    @Override
    public String toString() {
        return getLine1() + "\n" + getLine2() + "\n" + getLine3();
    }

    public String toCompactString() {
        return getLine1Compact() + "\n" + getLine3Compact();
    }

    private static String spaces(int n) {
        char[] c = new char[n];
        Arrays.fill(c, ' ');
        return String.valueOf(c);
    }

    private static int a(int f) {
        if (f < 0)
            return ~f;
        return f;
    }
}

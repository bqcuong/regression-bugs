package com.milaboratory.primitivio;

import java.io.DataInput;

/**
 * Created by dbolotin on 11/07/14.
 */
public class PrimitivI implements DataInput {
    @Override
    public void readFully(byte[] b) {

    }

    @Override
    public void readFully(byte[] b, int off, int len) {

    }

    @Override
    public int skipBytes(int n) {
        return 0;
    }

    @Override
    public boolean readBoolean() {
        return false;
    }

    @Override
    public byte readByte() {
        return 0;
    }

    @Override
    public int readUnsignedByte() {
        return 0;
    }

    @Override
    public short readShort() {
        return 0;
    }

    @Override
    public int readUnsignedShort() {
        return 0;
    }

    @Override
    public char readChar() {
        return 0;
    }

    @Override
    public int readInt() {
        return 0;
    }

    @Override
    public long readLong() {
        return 0;
    }

    @Override
    public float readFloat() {
        return 0;
    }

    @Override
    public double readDouble() {
        return 0;
    }

    @Override
    public String readLine() {
        return null;
    }

    @Override
    public String readUTF() {
        return null;
    }
}

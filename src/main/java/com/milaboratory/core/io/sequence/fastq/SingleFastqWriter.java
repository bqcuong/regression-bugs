package com.milaboratory.core.io.sequence.fastq;

import com.milaboratory.core.io.CompressionType;
import com.milaboratory.core.io.sequence.SingleRead;
import com.milaboratory.core.io.sequence.SingleSequenceWriter;
import com.milaboratory.core.sequence.NucleotideAlphabet;
import com.milaboratory.core.sequence.NucleotideSequence;
import com.milaboratory.core.sequence.SequenceQuality;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class SingleFastqWriter implements SingleSequenceWriter {
    public static final int DEFAULT_BUFFER_SIZE = 131072;
    final OutputStream outputStream;
    final QualityFormat qualityFormat;
    final byte[] buffer;
    int pointer;

    public SingleFastqWriter(String fileName) throws IOException {
        this(new FileOutputStream(fileName), QualityFormat.Phred33, CompressionType.detectCompressionType(fileName), DEFAULT_BUFFER_SIZE);
    }

    public SingleFastqWriter(File file) throws IOException {
        this(new FileOutputStream(file), QualityFormat.Phred33, CompressionType.detectCompressionType(file), DEFAULT_BUFFER_SIZE);
    }

    public SingleFastqWriter(OutputStream outputStream) throws IOException {
        this(outputStream, QualityFormat.Phred33, CompressionType.None, DEFAULT_BUFFER_SIZE);
    }

    public SingleFastqWriter(File file, QualityFormat qualityFormat, CompressionType ct) throws IOException {
        this(new FileOutputStream(file), qualityFormat, ct, DEFAULT_BUFFER_SIZE);
    }

    public SingleFastqWriter(String fileName, QualityFormat qualityFormat, CompressionType ct) throws IOException {
        this(new FileOutputStream(fileName), qualityFormat, ct, DEFAULT_BUFFER_SIZE);
    }

    public SingleFastqWriter(OutputStream outputStream, QualityFormat qualityFormat, CompressionType ct) throws IOException {
        this(outputStream, qualityFormat, ct, DEFAULT_BUFFER_SIZE);
    }

    public SingleFastqWriter(OutputStream outputStream, QualityFormat qualityFormat, CompressionType ct, int bufferSize) throws IOException {
        this.outputStream = ct.createOutputStream(outputStream, bufferSize / 2);
        this.qualityFormat = qualityFormat;
        this.buffer = new byte[bufferSize];
    }

    @Override
    public synchronized void write(SingleRead read) {
        int pointer = this.pointer;

        String description = read.getDescription();
        int len = description.length();
        flushIfNeededToWrite(len + 1);
        buffer[pointer++] = '@';
        for (int i = 0; i < len; ++i)
            buffer[pointer++] = (byte) description.charAt(i);

        NucleotideSequence sequence = read.getData().getSequence();
        SequenceQuality quality = read.getData().getQuality();

        len = sequence.size();

        flushIfNeededToWrite(len + 2);

        buffer[pointer++] = '\n';

        for (int i = 0; i < len; ++i)
            buffer[pointer++] = NucleotideAlphabet.symbolByteFromCode(sequence.codeAt(i));

        buffer[pointer++] = '\n';

        flushIfNeededToWrite(len + 3);

        buffer[pointer++] = '+';
        buffer[pointer++] = '\n';

        quality.encodeTo(qualityFormat, buffer, pointer);
        pointer += len;

        buffer[pointer++] = '\n';

        this.pointer = pointer;
    }

    public void flushIfNeededToWrite(int sizeToWrite) {
        if (buffer.length - pointer < sizeToWrite)
            flush();
    }

    @Override
    public void flush() {
        try {
            outputStream.write(buffer, 0, pointer);
            pointer = 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            flush();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

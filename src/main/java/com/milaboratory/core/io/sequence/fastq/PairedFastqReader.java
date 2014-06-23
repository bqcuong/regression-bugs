package com.milaboratory.core.io.sequence.fastq;

import com.milaboratory.core.io.CompressionType;
import com.milaboratory.core.io.sequence.PairedReaderImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dbolotin on 23/06/14.
 */
public class PairedFastqReader extends PairedReaderImpl {
    public PairedFastqReader(File file1, File file2) throws IOException {
        this(new SingleFastqReader(file1),
                new SingleFastqReader(file2));
    }

    public PairedFastqReader(String fileName1, String fileName2) throws IOException {
        this(new SingleFastqReader(fileName1),
                new SingleFastqReader(fileName2));
    }

    public PairedFastqReader(File file1, File file2,
                             boolean lazyReads) throws IOException {
        this(new SingleFastqReader(file1, lazyReads),
                new SingleFastqReader(file2, lazyReads));
    }

    public PairedFastqReader(String fileName1, String fileName2,
                             boolean lazyReads) throws IOException {
        this(new SingleFastqReader(fileName1, lazyReads),
                new SingleFastqReader(fileName2, lazyReads));
    }


    public PairedFastqReader(File file1, File file2,
                             QualityFormat format, CompressionType ct) throws IOException {
        this(new SingleFastqReader(new FileInputStream(file1), format, ct, false, SingleFastqReader.DEFAULT_BUFFER_SIZE, true),
                new SingleFastqReader(new FileInputStream(file1), format, ct, false, SingleFastqReader.DEFAULT_BUFFER_SIZE, true));
    }

    public PairedFastqReader(String fileName1, String fileName2,
                             QualityFormat format, CompressionType ct) throws IOException {
        this(new SingleFastqReader(new FileInputStream(fileName1), format, ct, false, SingleFastqReader.DEFAULT_BUFFER_SIZE, true),
                new SingleFastqReader(new FileInputStream(fileName1), format, ct, false, SingleFastqReader.DEFAULT_BUFFER_SIZE, true));
    }

    public PairedFastqReader(File file1, File file2,
                             QualityFormat format, CompressionType ct,
                             boolean lazyReads) throws IOException {
        this(new SingleFastqReader(new FileInputStream(file1), format, ct, false, SingleFastqReader.DEFAULT_BUFFER_SIZE, lazyReads),
                new SingleFastqReader(new FileInputStream(file1), format, ct, false, SingleFastqReader.DEFAULT_BUFFER_SIZE, lazyReads));
    }

    public PairedFastqReader(String fileName1, String fileName2,
                             QualityFormat format, CompressionType ct,
                             boolean lazyReads) throws IOException {
        this(new SingleFastqReader(new FileInputStream(fileName1), format, ct, false, SingleFastqReader.DEFAULT_BUFFER_SIZE, lazyReads),
                new SingleFastqReader(new FileInputStream(fileName1), format, ct, false, SingleFastqReader.DEFAULT_BUFFER_SIZE, lazyReads));
    }

    public PairedFastqReader(InputStream stream1, InputStream stream2,
                             QualityFormat format, CompressionType ct,
                             boolean guessQualityFormat, int bufferSize, boolean lazyReads) throws IOException {
        this(new SingleFastqReader(stream1, format, ct, guessQualityFormat, bufferSize, lazyReads),
                new SingleFastqReader(stream2, format, ct, guessQualityFormat, bufferSize, lazyReads));
    }

    public PairedFastqReader(SingleFastqReader reader1, SingleFastqReader reader2) {
        super(reader1, reader2);
    }
}

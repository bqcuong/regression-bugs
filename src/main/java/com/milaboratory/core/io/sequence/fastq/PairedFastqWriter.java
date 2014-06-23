package com.milaboratory.core.io.sequence.fastq;

import com.milaboratory.core.io.CompressionType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by dbolotin on 23/06/14.
 */
public final class PairedFastqWriter implements AutoCloseable {
    SingleFastqWriter[] writers;

    public PairedFastqWriter(File file1, File file2) throws IOException {
        this(new SingleFastqWriter(file1), new SingleFastqWriter(file2));
    }

    public PairedFastqWriter(String file1, String file2) throws IOException {
        this(new SingleFastqWriter(file1), new SingleFastqWriter(file2));
    }

    public PairedFastqWriter(String file1, String file2, QualityFormat qualityFormat, CompressionType ct) throws IOException {
        this(new SingleFastqWriter(new FileOutputStream(file1), qualityFormat, ct, SingleFastqWriter.DEFAULT_BUFFER_SIZE),
                new SingleFastqWriter(new FileOutputStream(file2), qualityFormat, ct, SingleFastqWriter.DEFAULT_BUFFER_SIZE));
    }

    public PairedFastqWriter(SingleFastqWriter writer1, SingleFastqWriter writer2) {
        this.writers = new SingleFastqWriter[]{writer1, writer2};
    }

    @Override
    public void close() throws Exception {

    }
}

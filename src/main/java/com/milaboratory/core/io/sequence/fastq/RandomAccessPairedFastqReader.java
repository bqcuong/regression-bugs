package com.milaboratory.core.io.sequence.fastq;

import com.milaboratory.core.io.sequence.PairedRead;
import com.milaboratory.core.io.sequence.SequenceReader;
import com.milaboratory.core.io.sequence.SingleRead;
import com.milaboratory.core.io.util.FileIndex;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * FASTQ file reader for paired reads with random access.
 */
public final class RandomAccessPairedFastqReader
        implements SequenceReader<PairedRead>, AutoCloseable {
    private final RandomAccessFastqReader reader1, reader2;

    public RandomAccessPairedFastqReader(String file1, String index1,
                                         String file2, String index2) throws IOException {
        this(file1, index1, file2, index2, true);
    }

    public RandomAccessPairedFastqReader(RandomAccessFile file1, FileIndex index1,
                                         RandomAccessFile file2, FileIndex index2) throws IOException {
        this(file1, index1, file2, index2, true);
    }

    public RandomAccessPairedFastqReader(String file1, String index1,
                                         String file2, String index2,
                                         boolean lazyReads) throws IOException {
        this(new RandomAccessFastqReader(file1, index1, lazyReads),
                new RandomAccessFastqReader(file2, index2, lazyReads));
    }

    public RandomAccessPairedFastqReader(RandomAccessFile file1, FileIndex index1,
                                         RandomAccessFile file2, FileIndex index2,
                                         boolean lazyReads) throws IOException {
        this(new RandomAccessFastqReader(file1, index1, lazyReads),
                new RandomAccessFastqReader(file2, index2, lazyReads));
    }

    public RandomAccessPairedFastqReader(RandomAccessFastqReader reader1, RandomAccessFastqReader reader2) {
        if (reader1.getCurrentRecordNumber() != reader2.getCurrentRecordNumber())
            throw new IllegalArgumentException("Random access readers must have same pointers.");
        this.reader1 = reader1;
        this.reader2 = reader2;
    }

    public void seekToRecord(long recordNumber) throws IOException {
        reader1.seekToRecord(recordNumber);
        reader2.seekToRecord(recordNumber);
    }

    public PairedRead take(long recordNumber) {
        SingleRead read1 = reader1.take(recordNumber);
        if (read1 == null)
            return null;
        SingleRead read2 = reader2.take(recordNumber);
        assert read2 != null;
        return new PairedRead(read1, read2);
    }

    @Override
    public PairedRead take() {
        SingleRead read1 = reader1.take();
        if (read1 == null)
            return null;
        SingleRead read2 = reader2.take();
        assert read2 != null;
        return new PairedRead(read1, read2);
    }

    @Override
    public void close() {
        reader1.close();
        reader2.close();
    }
}

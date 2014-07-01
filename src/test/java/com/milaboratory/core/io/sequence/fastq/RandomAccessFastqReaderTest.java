package com.milaboratory.core.io.sequence.fastq;

import com.milaboratory.core.io.CompressionType;
import com.milaboratory.core.io.sequence.SingleRead;
import com.milaboratory.core.io.util.FileIndex;
import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

public class RandomAccessFastqReaderTest {

    @Test
    public void test1() throws Exception {
        File sample = new File(SingleFastqReaderTest.class.getClassLoader().getResource("sequences/sample_r1.fastq").toURI());
        SingleFastqReader reader = new SingleFastqReader(
                new FileInputStream(sample),
                QualityFormat.Phred33,
                CompressionType.None,
                false,
                253,
                false);

        SingleFastqIndexer indexer = new SingleFastqIndexer(reader, 3);
        while (indexer.take() != null)
            indexer.take();
        FileIndex index = indexer.createIndex();
        reader = new SingleFastqReader(
                new FileInputStream(sample),
                QualityFormat.Phred33,
                CompressionType.None,
                false,
                253,
                false);
        RandomAccessFastqReader rreader = new RandomAccessFastqReader(index, sample);
        SingleRead read;
        while ((read = reader.take()) != null) {
            SingleRead actual = rreader.take(read.getId());
            Assert.assertEquals(read.getData(), actual.getData());
            Assert.assertEquals(read.getDescription(), actual.getDescription());
        }
    }
}
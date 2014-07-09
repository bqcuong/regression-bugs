package com.milaboratory.core.io.sequence.fastq;

import cc.redberry.pipe.CUtils;
import com.milaboratory.core.io.CompressionType;
import com.milaboratory.core.io.sequence.SingleRead;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SingleFastqWriterTest {
    @Test
    public void test1() throws Exception {
        File sample = new File(SingleFastqReaderTest.class.getClassLoader().getResource("sequences/sample_r1.fastq").toURI());

        List<SingleRead> reads = new ArrayList<>();
        try (SingleFastqReader reader = new SingleFastqReader(sample, false)) {
            for (SingleRead read : CUtils.it(reader))
                reads.add(read);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (SingleFastqWriter writer = new SingleFastqWriter(bos)) {
            for (SingleRead read : reads)
                writer.write(read);
        }

        assertTrue(bos.size() > 800);

        try (SingleFastqReader reader = new SingleFastqReader(new ByteArrayInputStream(bos.toByteArray()))) {
            for (SingleRead read : reads)
                assertReadsEquals(read, reader.take());
            assertNull(reader.take());
        }
    }

    @Test
    public void test2() throws Exception {
        File sample = new File(SingleFastqReaderTest.class.getClassLoader().getResource("sequences/sample_r1.fastq").toURI());

        List<SingleRead> reads = new ArrayList<>();
        try (SingleFastqReader reader = new SingleFastqReader(sample, false)) {
            for (SingleRead read : CUtils.it(reader))
                reads.add(read);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (SingleFastqWriter writer = new SingleFastqWriter(bos,
                QualityFormat.Phred33, CompressionType.GZIP)) {
            for (SingleRead read : reads)
                writer.write(read);
        }

        assertTrue(bos.size() > 800);

        try (SingleFastqReader reader = new SingleFastqReader(
                new ByteArrayInputStream(bos.toByteArray()),
                QualityFormat.Phred33, CompressionType.GZIP)) {
            for (SingleRead read : reads)
                assertReadsEquals(read, reader.take());
            assertNull(reader.take());
        }
    }

    public static void assertReadsEquals(SingleRead r1, SingleRead r2) {
        assertEquals(r1.getId(), r2.getId());
        assertEquals(r1.getData(), r2.getData());
        assertEquals(r1.getDescription(), r2.getDescription());
    }
}
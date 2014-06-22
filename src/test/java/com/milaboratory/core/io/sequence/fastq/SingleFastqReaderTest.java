package com.milaboratory.core.io.sequence.fastq;

import cc.redberry.pipe.CUtils;
import com.milaboratory.core.io.CompressionType;
import com.milaboratory.core.io.sequence.SingleRead;
import com.milaboratory.util.HashFunctions;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public class SingleFastqReaderTest {

    @Test
    public void test1() throws IOException, URISyntaxException {
        assertReaderOnSampleWithGZ("sample_r1.fastq");
    }

    @Test
    public void test2() throws IOException, URISyntaxException {
        assertReaderOnSampleWithGZ("sample_r2.fastq");
    }

    @Test
    public void test3() throws IOException, URISyntaxException {
        assertReaderOnSample("solexa1.fastq.gz");
    }

    @Test
    public void test4() throws IOException, URISyntaxException {
        assertReaderOnSample("solexa2.fastq.gz");
    }

    @Test
    public void test5() throws IOException, URISyntaxException {
        assertReaderOnSample("solexa3.fastq.gz");
    }

    private static void assertReaderOnSampleWithGZ(String file) throws IOException, URISyntaxException {
        File sample = new File(SingleFastqReaderTest.class.getClassLoader().getResource("sequences/" + file).toURI());
        File sampleGZIP = new File(SingleFastqReaderTest.class.getClassLoader().getResource("sequences/" + file + ".gz").toURI());
        SingleFastqReader reader = new SingleFastqReader(
                new FileInputStream(sample),
                QualityFormat.Phred33,
                CompressionType.None,
                false,
                253,
                false);

        TreeSet<SingleRead> set = new TreeSet<>(SINGLE_READ_COMPARATOR);
        for (SingleRead read : CUtils.it(reader))
            set.add(read);

        for (int bufferSize = 253; bufferSize < 5000; ) {
            assertSameReadContent(set, new SingleFastqReader(
                    new FileInputStream(sample),
                    QualityFormat.Phred33,
                    CompressionType.None,
                    false,
                    bufferSize,
                    false));
            assertSameReadContent(set, new SingleFastqReader(
                    new FileInputStream(sample),
                    QualityFormat.Phred33,
                    CompressionType.None,
                    false,
                    bufferSize,
                    true));
            assertSameReadContent(set, new SingleFastqReader(
                    new FileInputStream(sampleGZIP),
                    QualityFormat.Phred33,
                    CompressionType.detectCompressionType(sampleGZIP),
                    false,
                    bufferSize,
                    true));
            assertSameReadContent(set, new SingleFastqReader(
                    new FileInputStream(sampleGZIP),
                    QualityFormat.Phred33,
                    CompressionType.detectCompressionType(sampleGZIP),
                    false,
                    bufferSize,
                    false));


            bufferSize += (HashFunctions.JenkinWang32shift(bufferSize) & 15) + 1;
        }
        assertSameReadContent(set, new SingleFastqReader(sample, true));
        assertSameReadContent(set, new SingleFastqReader(sampleGZIP, true));
        assertSameReadContent(set, new SingleFastqReader(sample, false));
        assertSameReadContent(set, new SingleFastqReader(sampleGZIP, false));
    }

    private static void assertReaderOnSample(String file) throws IOException, URISyntaxException {
        File sample = new File(SingleFastqReaderTest.class.getClassLoader().getResource("sequences/" + file).toURI());
        SingleFastqReader reader = new SingleFastqReader(
                new FileInputStream(sample),
                QualityFormat.Phred64,
                CompressionType.GZIP,
                false,
                253,
                false);

        TreeSet<SingleRead> set = new TreeSet<>(SINGLE_READ_COMPARATOR);
        for (SingleRead read : CUtils.it(reader))
            set.add(read);

        for (int bufferSize = 253; bufferSize < 5000; ) {
            assertSameReadContent(set, new SingleFastqReader(
                    new FileInputStream(sample),
                    QualityFormat.Phred64,
                    CompressionType.GZIP,
                    false,
                    bufferSize,
                    false));
            assertSameReadContent(set, new SingleFastqReader(
                    new FileInputStream(sample),
                    QualityFormat.Phred64,
                    CompressionType.GZIP,
                    false,
                    bufferSize,
                    true));

            bufferSize += (HashFunctions.JenkinWang32shift(bufferSize) & 15) + 1;
        }
        assertSameReadContent(set, new SingleFastqReader(sample, true));
        assertSameReadContent(set, new SingleFastqReader(sample, false));
    }

    private static void assertSameReadContent(TreeSet<SingleRead> expected, SingleFastqReader reader) {
        TreeSet<SingleRead> set = new TreeSet<>(SINGLE_READ_COMPARATOR);
        for (SingleRead read : CUtils.it(reader))
            set.add(read);
        Assert.assertEquals(expected, set);
    }

    public static final Comparator<SingleRead> SINGLE_READ_COMPARATOR = new Comparator<SingleRead>() {
        @Override
        public int compare(SingleRead o1, SingleRead o2) {
            int c;
            if ((c = Long.compare(o1.getId(), o2.getId())) != 0)
                return c;
            if ((c = o1.getData().getSequence().compareTo(o2.getData().getSequence())) != 0)
                return c;
            if ((c = Integer.compare(o1.getData().getQuality().hashCode(), o2.getData().getQuality().hashCode())) != 0)
                return c;
            if ((c = o1.getDescription().compareTo(o2.getDescription())) != 0)
                return c;
            return 0;
        }
    };
}
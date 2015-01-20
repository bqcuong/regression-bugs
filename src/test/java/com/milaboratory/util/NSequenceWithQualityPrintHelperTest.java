package com.milaboratory.util;

import cc.redberry.pipe.CUtils;
import com.milaboratory.core.io.sequence.SingleRead;
import com.milaboratory.core.io.sequence.fastq.SingleFastqReader;
import org.junit.Ignore;
import org.junit.Test;

public class NSequenceWithQualityPrintHelperTest {
    @Ignore
    @Test
    public void test1() throws Exception {
        try (SingleFastqReader reader = new SingleFastqReader(
                NSequenceWithQualityPrintHelperTest.class
                        .getResourceAsStream("/sequences/sample_r2.fastq")
        )) {
            for (SingleRead singleRead : CUtils.it(reader)) {
                NSequenceWithQualityPrintHelper helper = new NSequenceWithQualityPrintHelper(singleRead.getData(), 7, 20);
                System.out.println(helper);
            }
        }
    }
}
package com.milaboratory.core.alignment.blast;

import org.junit.Test;

public class BlastDBTest {
    @Test
    public void testName() throws Exception {
        String path = "/Volumes/Data/tools/ncbi-blast-2.2.31+/db/yeast";
        BlastDB blastDB = BlastDB.get(path);
    }
}
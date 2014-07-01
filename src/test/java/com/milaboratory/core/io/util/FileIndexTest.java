package com.milaboratory.core.io.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

import static com.milaboratory.core.io.util.TestUtil.*;

public class FileIndexTest {

    @Test
    public void test1() throws Exception {
        long seed = System.currentTimeMillis();
        File tempFile = createRandomFile(seed);
        File tempIndexFile = File.createTempFile("index" + seed, ".mix");
        String[] getAllLines = getAllLines(tempFile);
        for (int step = 5; step < 10; step += 2) {
            for (int start = 1; start <= step; ++start) {
                FileIndex index = buildIndex(tempFile, step, start);
                index.write(tempIndexFile);
                index = FileIndex.read(tempIndexFile);
                StringReader reader = new StringReader(index, tempFile);
                for (int i = 0; i < getAllLines.length; ++i)
                    Assert.assertEquals(getAllLines[i], reader.take(i));
            }
        }
        tempFile.delete();
        tempIndexFile.delete();
    }

}
package com.milaboratory.core.io.util;

import org.apache.commons.math3.random.Well1024a;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;

public class AbstractRandomAccessReaderTest {
    @Test
    public void test1() throws Exception {
        File tempFile = createTempFile(System.currentTimeMillis());
        String[] allLines = allLines(tempFile);
        FileIndex index = buildIndex(tempFile, 5, 0);
        StringReader reader = new StringReader(index, tempFile);
        for (int i = 0; i < allLines.length; ++i)
            Assert.assertEquals(allLines[i], reader.take(i));
        tempFile.delete();
    }

    @Test
    public void test2() throws Exception {
        File tempFile = createTempFile(System.currentTimeMillis());
        String[] allLines = allLines(tempFile);
        for (int step = 5; step < 10; step += 2) {
            for (int start = 1; start <= step; ++start) {
                FileIndex index = buildIndex(tempFile, step, start);
                StringReader reader = new StringReader(index, tempFile);
                for (int i = 0; i < allLines.length; ++i)
                    Assert.assertEquals(allLines[i], reader.take(i));
            }
        }
        tempFile.delete();
    }

    public static String[] allLines(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        ArrayList<String> list = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            list.add(line);
        }
        return list.toArray(new String[list.size()]);
    }

    public static FileIndex buildIndex(File file, long step, long startingRecord)
            throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        FileIndexBuilder indexBuilder = new FileIndexBuilder(step).setStartingRecordNumber(startingRecord);
        long startingByte = 0;
        String line;
        while ((line = reader.readLine()) != null) {
            byte[] bytes = line.getBytes();
            if (startingRecord == 0) {
                if (startingByte != -1)
                    indexBuilder.setStartingRecordPosition(startingByte);
                indexBuilder.appendNextRecord(bytes.length + 1);
                startingByte = -1;
            } else {
                --startingRecord;
                startingByte += bytes.length + 1;
            }
        }
        return indexBuilder.createAndDestroy();
    }

    public static File createTempFile(long seed)
            throws IOException {
        File temp = File.createTempFile("temp" + seed, "tmp");
        temp.deleteOnExit();
        FileOutputStream output = new FileOutputStream(temp);
        Well1024a random = new Well1024a(seed);

        int numberOfLines = 100 + random.nextInt(10);
        for (int i = 0; i < numberOfLines; ++i) {
            StringBuilder sb = new StringBuilder();
            for (int j = (1 + random.nextInt(100)); j >= 0; --j)
                sb.append(random.nextInt(100));
            String string = sb.toString();
            byte[] bytes = string.getBytes();
            output.write(bytes);
            output.write('\n');
        }
        output.close();
        return temp;
    }

    public static final class StringReader extends AbstractRandomAccessReader<String> {
        StringReader(FileIndex fileIndex, File file) throws FileNotFoundException {
            super(fileIndex, new RandomAccessFile(file, "r"));
        }

        StringReader(FileIndex fileIndex, RandomAccessFile file) {
            super(fileIndex, file);
        }

        @Override
        protected String take0() {
            try {
                return file.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
package com.milaboratory.core.io.util;

import org.apache.commons.math3.random.Well1024a;

import java.io.*;
import java.util.ArrayList;

public class TestUtil {


    public static String[] getAllLines(File file) throws IOException {
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

    public static File createRandomFile(long seed) throws IOException {
        return createRandomFile(seed, 100);
    }

    public static File createRandomFile(long seed, int avLinesCount) throws IOException {
        File temp = File.createTempFile("temp" + seed, "tmp");
        temp.deleteOnExit();
        FileOutputStream output = new FileOutputStream(temp);
        Well1024a random = new Well1024a(seed);

        int numberOfLines = avLinesCount + random.nextInt(10);
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

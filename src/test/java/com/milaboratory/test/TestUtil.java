package com.milaboratory.test;

//import com.fasterxml.jackson.core.type.TypeReference;
//import com.milaboratory.core.sequence.Alphabet;
//import com.milaboratory.core.sequence.Sequence;
//import com.milaboratory.core.sequence.SequenceBuilder;
//import com.milaboratory.core.serialization.GlobalObjectMappers;
//import org.apache.commons.math.random.RandomDataImpl;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.DecimalFormat;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;

public class TestUtil {
//    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
//
//    public static boolean lt() {
//        return System.getProperty("longTests") != null;
//    }
//
//    public static int its(int shortTest, int longTest) {
//        return lt() ? longTest : shortTest;
//    }
//
//    public static String env() {
//        String serverEnv = System.getProperty("serverEnv");
//
//        if (serverEnv == null)
//            return null;
//
//        File serverEnvPath = new File(serverEnv);
//        if (serverEnvPath.exists() && serverEnvPath.isDirectory()) {
//            String ret = serverEnvPath.getAbsolutePath();
//            if (!ret.endsWith(File.separator))
//                ret = ret + File.separator;
//            return ret;
//        } else
//            throw new IllegalArgumentException(serverEnv + " not exists.");
//    }
//
//    private static volatile Map<String, String> envProperties;
//
//    public static synchronized Map<String, String> getProperties() {
//        if (envProperties == null) {
//            String e = env();
//
//            if (e == null)
//                envProperties = Collections.EMPTY_MAP;
//            else {
//                File propsFile = new File(e + "properties.json");
//
//                if (!propsFile.exists())
//                    envProperties = Collections.EMPTY_MAP;
//                else {
//                    try {
//                        envProperties = GlobalObjectMappers.ONE_LINE.readValue(propsFile, new TypeReference<HashMap<String, String>>() {
//                        });
//                    } catch (IOException ex) {
//                        envProperties = Collections.EMPTY_MAP;
//                    }
//                }
//            }
//        }
//
//        return envProperties;
//    }
//
//    public static String time(long t) {
//        double v = t;
//        if ((t /= 1000) == 0)
//            return "" + DECIMAL_FORMAT.format(v) + "ns";
//
//        v /= 1000;
//        if ((t /= 1000) == 0)
//            return "" + DECIMAL_FORMAT.format(v) + "us";
//
//        v /= 1000;
//        if ((t /= 1000) == 0)
//            return "" + DECIMAL_FORMAT.format(v) + "ms";
//
//        v /= 1000;
//        if ((t /= 60) == 0)
//            return "" + DECIMAL_FORMAT.format(v) + "s";
//
//        v /= 60;
//        return "" + DECIMAL_FORMAT.format(v) + "m";
//    }
//
//    public static <T extends Sequence> T randomSequence(Alphabet<T> alphabet, RandomDataImpl rdi, int minLength, int maxLength) {
//        int length = minLength == maxLength ? minLength : rdi.nextInt(minLength, maxLength);
//        SequenceBuilder<T> builder = alphabet.getBuilderFactory().create(length);
//        for (int i = 0; i < length; ++i)
//            builder.setCode(i, (byte) rdi.nextInt(0, alphabet.size() - 1));
//        return builder.create();
//    }

}

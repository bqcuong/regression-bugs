package com.milaboratory.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.milaboratory.core.sequence.Alphabet;
import com.milaboratory.core.sequence.Sequence;
import com.milaboratory.core.sequence.SequenceBuilder;
import com.milaboratory.util.GlobalObjectMappers;
import com.milaboratory.util.RandomUtil;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TestUtil {
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    public static boolean lt() {
        return Objects.equals(System.getProperty("longTests"), "") ||
                Objects.equals(System.getProperty("longTests"), "true");
    }

    @Test
    public void testLT() throws Exception {
        if (lt())
            System.out.println("Long tests.");
        else
            System.out.println("Short tests.");

        if (getProperties().isEmpty()) {
            System.out.println("No system env properties.");
        } else {
            System.out.println("There are some system env properties.");
        }
    }

    public static int its(int shortTest, int longTest) {
        return lt() ? longTest : shortTest;
    }

    public static String env() {
        String serverEnv = System.getProperty("serverEnv");

        if (serverEnv == null)
            return null;

        File serverEnvPath = new File(serverEnv);
        if (serverEnvPath.exists() && serverEnvPath.isDirectory()) {
            String ret = serverEnvPath.getAbsolutePath();
            if (!ret.endsWith(File.separator))
                ret = ret + File.separator;
            return ret;
        } else
            throw new IllegalArgumentException(serverEnv + " not exists.");
    }

    private static volatile Map<String, String> envProperties;

    public static synchronized Map<String, String> getProperties() {
        if (envProperties == null) {
            String e = env();

            if (e == null)
                envProperties = Collections.EMPTY_MAP;
            else {
                File propsFile = new File(e + "properties.json");

                if (!propsFile.exists())
                    envProperties = Collections.EMPTY_MAP;
                else {
                    try {
                        envProperties = GlobalObjectMappers.ONE_LINE
                                .readValue(propsFile, new TypeReference<HashMap<String, String>>() {
                                });
                    } catch (IOException ex) {
                        envProperties = Collections.EMPTY_MAP;
                    }
                }
            }
        }

        return envProperties;
    }

    public static String time(long t) {
        double v = t;
        if ((t /= 1000) == 0)
            return "" + DECIMAL_FORMAT.format(v) + "ns";

        v /= 1000;
        if ((t /= 1000) == 0)
            return "" + DECIMAL_FORMAT.format(v) + "us";

        v /= 1000;
        if ((t /= 1000) == 0)
            return "" + DECIMAL_FORMAT.format(v) + "ms";

        v /= 1000;
        if ((t /= 60) == 0)
            return "" + DECIMAL_FORMAT.format(v) + "s";

        v /= 60;
        return "" + DECIMAL_FORMAT.format(v) + "m";
    }

    public static <S extends Sequence<S>> S randomSequence(Alphabet<S> alphabet,
                                                           int minLength, int maxLength) {
        return randomSequence(alphabet, RandomUtil.getThreadLocalRandom(), minLength, maxLength);
    }

    public static <S extends Sequence<S>> S randomSequence(Alphabet<S> alphabet, RandomDataGenerator r,
                                                           int minLength, int maxLength) {
        return randomSequence(alphabet, r.getRandomGenerator(), minLength, maxLength);
    }

    public static <S extends Sequence<S>> S randomSequence(Alphabet<S> alphabet, RandomGenerator r,
                                                           int minLength, int maxLength) {
        int length = minLength == maxLength ?
                minLength : minLength + r.nextInt(maxLength - minLength + 1);
        SequenceBuilder<S> builder = alphabet.getBuilder();
        for (int i = 0; i < length; ++i)
            builder.append((byte) r.nextInt(alphabet.size()));
        return builder.createAndDestroy();
    }
}

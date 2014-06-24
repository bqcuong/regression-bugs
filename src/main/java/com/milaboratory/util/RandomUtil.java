package com.milaboratory.util;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.random.Well19937c;

import java.util.concurrent.atomic.AtomicLong;

public final class RandomUtil {
    private RandomUtil() {
    }

    static final ThreadLocal<Randomm> threadLocalRandom = new ThreadLocal<Randomm>() {
        @Override
        protected Randomm initialValue() {
            //Generating thread-specific seed
            long seed = seedCounter.addAndGet(353L);
            seed = HashFunctions.JenkinWang64shift(seed);

            //Creating random generator
            return new Randomm(new Well19937c(seed));
        }
    };
    //Used to generate individual seeds for each thread-local random generator
    private static final AtomicLong seedCounter = new AtomicLong(641L);

    public static Well19937c getThreadLocalRandom() {
        return threadLocalRandom.get().generator;
    }

    public static RandomDataGenerator getThreadLocalRandomData() {
        return threadLocalRandom.get().rdi;
    }

    private static final class Randomm {
        final Well19937c generator;
        final RandomDataGenerator rdi;

        private Randomm(Well19937c generator) {
            this.generator = generator;
            this.rdi = new RandomDataGenerator(generator);
        }
    }
}

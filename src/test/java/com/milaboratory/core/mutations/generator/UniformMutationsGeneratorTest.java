package com.milaboratory.core.mutations.generator;

import com.milaboratory.core.mutations.Mutations;
import com.milaboratory.core.sequence.NucleotideSequence;
import com.milaboratory.test.TestUtil;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.junit.Assert;
import org.junit.Test;

public class UniformMutationsGeneratorTest {
    @Test
    public void testRandom1() throws Exception {
        RandomGenerator generator = new Well19937c();
        for (int i = 0; i < 10000; ++i) {
            NucleotideSequence seq = TestUtil.randomSequence(NucleotideSequence.ALPHABET, generator,
                    30, 100);
            Mutations<NucleotideSequence> muts = UniformMutationsGenerator.createUniformMutationAsObject(seq, generator);
            NucleotideSequence seqM = muts.mutate(seq);
            Assert.assertFalse(seq.equals(seqM));
        }
    }
}
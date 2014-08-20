package com.milaboratory.core.motif;

import com.milaboratory.core.sequence.NucleotideSequence;
import junit.framework.Assert;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.junit.Test;

import static com.milaboratory.test.TestUtil.its;
import static com.milaboratory.test.TestUtil.randomSequence;

public class MotifUtilsTest {
    @Test
    public void test1() throws Exception {
        RandomGenerator rg = new Well19937c();
        for (int i = 0; i < its(1000, 10000); ++i) {
            int length = 10 + rg.nextInt(30);
            NucleotideSequence seq1 = randomSequence(NucleotideSequence.ALPHABET, length, length);
            NucleotideSequence seq2 = randomSequence(NucleotideSequence.ALPHABET, length, length);
            Motif<NucleotideSequence> motif = MotifUtils.twoSequenceMotif(seq1, 0, seq2, 0, length);
            Assert.assertTrue(motif.matches(seq1, 0));
            Assert.assertTrue(motif.matches(seq2, 0));
        }
    }
}
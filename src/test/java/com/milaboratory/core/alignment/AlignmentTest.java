package com.milaboratory.core.alignment;


import com.milaboratory.core.mutations.Mutations;
import com.milaboratory.core.sequence.NucleotideSequence;
import com.milaboratory.util.RandomUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AlignmentTest {

    static NucleotideSequence mutate(NucleotideSequence seq, int[] mut) {
        return new Mutations<NucleotideSequence>(NucleotideSequence.ALPHABET, mut).mutate(seq);
    }

    static int[] move(int[] mutations, int offset) {
        return new Mutations<NucleotideSequence>(NucleotideSequence.ALPHABET, mutations).move(offset).getAllMutations();
    }

    @Before
    public void setUp() throws Exception {
        //0123456L bad
        RandomUtil.getThreadLocalRandom().setSeed(014343433L);
    }

    @Test
    public void test1() throws Exception {
        NucleotideSequence sequence1 = new NucleotideSequence("TACCGCCAT");
        NucleotideSequence sequence2 = new NucleotideSequence("CCTCAT");
        Alignment<NucleotideSequence> alignment = Aligner.alignLocal(LinearGapAlignmentScoring.getNucleotideBLASTScoring(),
                sequence1, sequence2);
        Assert.assertEquals(3, alignment.convertPosition(6));
        Assert.assertEquals(0, alignment.convertPosition(2));
    }

    @Test
    public void test2() throws Exception {
        NucleotideSequence sequence1 = new NucleotideSequence("TACCGCCATGACCA");
        NucleotideSequence sequence2 = new NucleotideSequence("CCTCATCTCTT");
        Alignment<NucleotideSequence> alignment = Aligner.alignLocal(LinearGapAlignmentScoring.getNucleotideBLASTScoring(),
                sequence1, sequence2);

        AlignmentHelper helper = alignment.getAlignmentHelper();

        Assert.assertEquals(alignment.getSequence1Range().getFrom(), helper.getSequence1PositionAt(0));
        Assert.assertEquals(alignment.getSequence1Range().getTo() - 1,
                helper.getSequence1PositionAt(helper.size() - 1));

        Assert.assertEquals(alignment.getSequence2Range().getFrom(), helper.getSequence2PositionAt(0));
        Assert.assertEquals(alignment.getSequence2Range().getTo() - 1,
                helper.getSequence2PositionAt(helper.size() - 1));
    }
}

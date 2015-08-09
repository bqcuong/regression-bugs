package com.milaboratory.core.alignment.blast;

import com.milaboratory.core.alignment.AlignmentUtils;
import com.milaboratory.core.alignment.batch.PipedAlignmentResult;
import com.milaboratory.core.sequence.NucleotideSequence;
import org.junit.Assert;
import org.junit.Test;

import static cc.redberry.pipe.CUtils.asOP;

public class BlastAlignerTest {
    @Test
    public void test1() throws Exception {
        BlastAligner<NucleotideSequence, Integer> ba = new BlastAligner<>();
        NucleotideSequence ns1 = new NucleotideSequence("ATTAGACGAATCCGATGCTGACTGCGCGATGATGCTAGTCGTGCTAGTACTAGCTGGCGCGGATTC");
        NucleotideSequence ns2 = new NucleotideSequence("TATTACCTGCTGCGCGCGCTAGATCGGTACTACGTTGCTAGCTAGCTTCGTATACGTCGTGCTAGTATCGATCGCTAG");

        ba.addReference(ns1, 1);
        ba.addReference(ns2, 2);

        NucleotideSequence nsq = new NucleotideSequence("TAGACGAATCCGATGCTGACTGCGCGATGAACCTAGTCGTGCTAGTACTA");

        PipedAlignmentResult<BlastHit<NucleotideSequence, Integer>, NucleotideSequence> result = ba.align(asOP(nsq)).take();
        Assert.assertEquals((Integer) 1, result.getHits().get(0).getRecordPayload());
        Assert.assertEquals(nsq, AlignmentUtils.getAlignedSequence2Part(result.getHits().get(0).getAlignment()));
    }
}
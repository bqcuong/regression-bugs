package com.milaboratory.core.alignment.blast;

import com.milaboratory.core.alignment.Alignment;
import com.milaboratory.core.sequence.NucleotideSequenceWithWildcards;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by poslavsky on 15/06/15.
 */
@Ignore
public class BlastAlignerTest {
    @Test
    public void test1() throws Exception {
        BlastAligner<NucleotideSequenceWithWildcards> aligner = new BlastAligner<>(1, "blastn", "-db", "/Users/poslavsky/Projects/milab/blast/16SMicrobial");
        String query = "CSTCAGAACGAACGCTGGCGGCATGCCTAACACATGCAAGSCGAACGAGAAACCAGAGCTTGCTCTGGCGGACAGTGGCGGACGGGTGAGTAACGC";
        NucleotideSequenceWithWildcards s = new NucleotideSequenceWithWildcards(query);
        for (int i = 0; i < 10; ++i) {
            Alignment<NucleotideSequenceWithWildcards> la = aligner.align(new NucleotideSequenceWithWildcards[]{s})[0].getHits().get(0).getAlignment();
            Assert.assertEquals(s.getRange(la.getSequence2Range()),
                    la.getRelativeMutations().mutate(la.getSequence1()));
        }
    }

    @Test
    public void test2() throws Exception {
        BlastAligner<NucleotideSequenceWithWildcards> aligner = new BlastAligner<>(1, "blastn", "-db", "/Users/poslavsky/Projects/milab/blast/16SMicrobial");
        String query = "ATGC";
        NucleotideSequenceWithWildcards s = new NucleotideSequenceWithWildcards(query);
        Assert.assertEquals(0, aligner.align(new NucleotideSequenceWithWildcards[]{s})[0].getHits().size());
    }

    @Test
    public void test3() throws Exception {
        BlastAligner<NucleotideSequenceWithWildcards> aligner = new BlastAligner<>(2, "blastn", "-db", "/Users/poslavsky/Projects/milab/blast/16SMicrobial");
        String query1 = "CSTCAGAACGAACGCTGGCGGCATGCCTAACACATGCAAGSCGAACGAGAAACCAGAGCTTGCTCTGGCGGACAGTGGCGGACGGGTGAGTAACGC";
        String query2 = "CSTCAGAACGAACGCTGAAGSCGAACGAGAAACCAGAGGCGGCATGCCTAACACATGCCTTGCTCTGGCGGACAGTGGCGGACGGGTGAGTAACGC";
        NucleotideSequenceWithWildcards[] s = {new NucleotideSequenceWithWildcards(query1),
                new NucleotideSequenceWithWildcards(query2)};
        for (int i = 0; i < 12; ++i) {
            BlastAlignmentResult<NucleotideSequenceWithWildcards>[] results = aligner.align(s);
            for (int j = 0; j < s.length; ++j) {
                Alignment<NucleotideSequenceWithWildcards> la = results[j].getHits().get(0).getAlignment();
                Assert.assertEquals(s[j].getRange(la.getSequence2Range()),
                        la.getRelativeMutations().mutate(la.getSequence1()));
            }
        }

    }
}
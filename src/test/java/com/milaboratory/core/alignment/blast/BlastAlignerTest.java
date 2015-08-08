//package com.milaboratory.core.alignment.blast;
//
//import com.milaboratory.core.alignment.Alignment;
//import com.milaboratory.core.sequence.NucleotideSequence;
//import org.junit.Assert;
//import org.junit.Assume;
//import org.junit.Ignore;
//import org.junit.Test;
//
///**
// * Created by poslavsky on 15/06/15.
// */
//@Ignore
//public class BlastAlignerTest {
//    //@Test
//    //public void test2() throws Exception {
//    //    BlastAligner<NucleotideSequence> aligner = new BlastAligner<>(NucleotideSequence.ALPHABET,null);
//    //    String query = "CTCAGAACGAACGCTGGCGGCATGCCTAACACATGCAAGTCGAACGAGAAACCAGAGCTTGCTCTGGCGGACAGTGGCGGACGGGTGAGTAACGC";
//    //    NucleotideSequence s = new NucleotideSequence(query);
//    //    for(int i = 0;i< 100;++i){
//    //        System.out.println(aligner.align(s).getHits().get(0).getAlignment());
//    //    }
//    //}
//
//    @Test
//    public void test11() throws Exception {
//        Assume.assumeTrue(Blast.isBlastAvailable());
//    }
//
//    @Test
//    public void test2() throws Exception {
//        BlastAligner<NucleotideSequence> aligner = new BlastAligner<>(1, "blastn", "-db", "/Users/poslavsky/Projects/milab/blast/16SMicrobial");
//        String query = "ATGC";
//        NucleotideSequence s = new NucleotideSequence(query);
//        Assert.assertEquals(0, aligner.align(new NucleotideSequence[]{s})[0].getHits().size());
//    }
//
//    @Test
//    public void test3() throws Exception {
//        BlastAligner<NucleotideSequence> aligner = new BlastAligner<>(2, "blastn", "-db", "/Users/poslavsky/Projects/milab/blast/16SMicrobial");
//        String query1 = "CSTCAGAACGAACGCTGGCGGCATGCCTAACACATGCAAGSCGAACGAGAAACCAGAGCTTGCTCTGGCGGACAGTGGCGGACGGGTGAGTAACGC";
//        String query2 = "CSTCAGAACGAACGCTGAAGSCGAACGAGAAACCAGAGGCGGCATGCCTAACACATGCCTTGCTCTGGCGGACAGTGGCGGACGGGTGAGTAACGC";
//        NucleotideSequence[] s = {new NucleotideSequence(query1),
//                new NucleotideSequence(query2)};
//        for (int i = 0; i < 12; ++i) {
//            BlastAlignmentResult<NucleotideSequence>[] results = aligner.align(s);
//            for (int j = 0; j < s.length; ++j) {
//                Alignment<NucleotideSequence> la = results[j].getHits().get(0).getAlignment();
//                Assert.assertEquals(s[j].getRange(la.getSequence2Range()),
//                        la.getRelativeMutations().mutate(la.getSequence1()));
//            }
//        }
//
//    }
//}
package com.milaboratory.core.alignment.kaligner2;

import com.milaboratory.core.alignment.AffineGapAlignmentScoring;
import com.milaboratory.core.alignment.Alignment;
import com.milaboratory.core.sequence.NucleotideSequence;
import com.milaboratory.util.RandomUtil;
import org.junit.Test;

/**
 * Created by poslavsky on 26/10/15.
 */
public class KAligner2Test {
    public static final AffineGapAlignmentScoring<NucleotideSequence> scoring = new AffineGapAlignmentScoring<>(
            NucleotideSequence.ALPHABET, 10, -7, -11, -2);
    public static final KAlignerParameters2 gParams = new KAlignerParameters2(
            5, true, true,
            15, -10, 15, 0f, 13, -7, -3,
            3, 6, 4, 3,
            0, 70, 0.8f, 5, scoring);

    @Test
    public void test1() throws Exception {
        KAligner2<Object> aligner = new KAligner2<>(gParams);
        aligner.addReference(new NucleotideSequence("atgcgtcgatcgtagctagctgatcgatcgactgactagcataggatgtagagctagctagctac"));
        aligner.addReference(new NucleotideSequence("atgcgtcgatcgtagctagctgatcgatcgactgactagcatcagcatcaggatgtagagctagctagctac"));
        aligner.addReference(new NucleotideSequence("atgcgtcgatcgtagctagctgtagtagatgatgatagtagatagtagtagtgatgacgatcgactgaatgtagagctagctagctac"));

        KAlignmentResult2<Object> al = aligner.align(new NucleotideSequence("atgcgtcgatcgtagctagctgtcgatcgactgaatgtagagctagctagctac"));
        System.out.println(al.hasHits());
        Alignment<NucleotideSequence> val = al.getHits().get(0).getAlignment();
        System.out.println(val.getScore());
        System.out.println(val);

        val = al.getHits().get(1).getAlignment();
        System.out.println(val.getScore());
        System.out.println(val);


        val = al.getHits().get(2).getAlignment();
        System.out.println(val.getScore());
        System.out.println(val);
    }


    @Test
    public void test2() throws Exception {
        RandomUtil.reseedThreadLocal(System.currentTimeMillis());
        KAlignerParameters2 gParams = KAligner2Test.gParams.clone();
        gParams.setAbsoluteMinScore(0);
        gParams.setMapperAbsoluteMinClusterScore(-10000);
        gParams.setMapperMaxClusterIndels(10);

        KAligner2<Object> aligner = new KAligner2<>(gParams);
        aligner.addReference(nt("ttttttt   attggcatgcccgatcgac   atatatatatgatgatgat   atttgtagaagtggatgagcgcg  aaaaaaaaa"));

        KAlignmentResult2<Object> al = aligner.align(nt("gcat  attggcatgtgcgatcgac atttcaagaagtatgagcgcg  tgc"));
        System.out.println(al.hasHits());
        Alignment<NucleotideSequence> val = al.getHits().get(0).getAlignment();
        System.out.println(val.getScore());
        System.out.println(val);

    }

    static NucleotideSequence nt(String str) {
        return new NucleotideSequence(str.replace(" ", ""));
    }
}
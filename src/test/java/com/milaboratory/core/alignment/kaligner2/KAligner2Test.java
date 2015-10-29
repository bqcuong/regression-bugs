package com.milaboratory.core.alignment.kaligner2;

import com.milaboratory.core.alignment.AffineGapAlignmentScoring;
import com.milaboratory.core.alignment.Alignment;
import com.milaboratory.core.sequence.NucleotideSequence;
import com.milaboratory.util.GlobalObjectMappers;
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

    @Test
    public void testCase1() throws Exception {
        RandomUtil.reseedThreadLocal(-8563992448850301133L);
        NucleotideSequence query = new NucleotideSequence("GATACTAGTCTCAACGGGGTACTACTAACGTCGAGGTGAATCCATAGGAAC");
        NucleotideSequence reference = new NucleotideSequence("GATACTAACATTGGGTATCAAACAGCGTAGTCACTTAAAATGGCGCCTCCTCAGATCAATGAGCAATAGTACGGTCGTTCACTACCTGCCTCAAAGGCAATGTTT");

        KAlignerParameters2 params = GlobalObjectMappers.ONE_LINE.readValue("{\"mapperKValue\":4,\"floatingLeftBound\":true,\"floatingRightBound\":true,\"mapperAbsoluteMinClusterScore\":80,\"mapperExtraClusterScore\":-30,\"mapperMatchScore\":40,\"mapperMismatchScore\":-50,\"mapperOffsetShiftScore\":-10,\"mapperSlotCount\":3,\"mapperMaxClusterIndels\":4,\"mapperAbsoluteMinScore\":220,\"mapperRelativeMinScore\":0.0,\"mapperMinSeedsDistance\":2,\"mapperMaxSeedsDistance\":2,\"alignmentStopPenalty\":0,\"absoluteMinScore\":80,\"relativeMinScore\":0.8,\"maxHits\":5,\"scoring\":{\"type\":\"affine\",\"subsMatrix\":\"raw(4, -3, -3, -3, -1, 0, -3, -3, 0, -3, 0, -3, 0, 0, 0, -3, 4, -3, -3, -1, 0, -3, 0, -3, 0, -3, 0, 0, -3, 0, -3, -3, 4, -3, -1, -3, 0, 0, -3, -3, 0, 0, -3, 0, 0, -3, -3, -3, 4, -1, -3, 0, -3, 0, 0, -3, 0, 0, 0, -3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, -3, -3, -1, 0, -3, -1, -1, -1, -1, -1, 0, -1, 0, -3, -3, 0, 0, -1, -3, 0, -1, -1, -1, -1, 0, -1, 0, -1, -3, 0, 0, -3, -1, -1, -1, 0, -3, -1, -1, 0, -1, -1, 0, 0, -3, -3, 0, -1, -1, -1, -3, 0, -1, -1, -1, 0, 0, -1, -3, 0, -3, 0, -1, -1, -1, -1, -1, 0, -3, 0, 0, -1, -1, 0, -3, 0, -3, -1, -1, -1, -1, -1, -3, 0, -1, -1, 0, 0, -3, 0, 0, 0, -1, -1, 0, 0, -1, 0, -1, 0, -1, -1, -1, 0, 0, -3, 0, -1, 0, -1, -1, 0, 0, -1, -1, 0, -1, -1, 0, -3, 0, 0, -1, -1, 0, -1, 0, -1, 0, -1, -1, 0, -1, 0, 0, 0, -3, -1, 0, -1, 0, -1, -1, 0, -1, -1, -1, 0)\",\"gapOpenPenalty\":-17,\"gapExtensionPenalty\":-1,\"uniformBasicMatch\":true}}", KAlignerParameters2.class);
        params.setMapperAbsoluteMinScore(-10000);
        params.setAbsoluteMinScore(-10000);
        params.setMapperAbsoluteMinClusterScore(-10000);

//        ID: 0
//        Score: 140
//        Cluster 0:
//        Q 0 -> T 0 - 0
//        Q 2 -> T 2 - 0
//        Cluster 1:
//        Q 20 -> T 3 - -17
//        Q 22 -> T 2 - -20
//        Q 24 -> T 4 - -20


        KAligner2 aligner = new KAligner2(params);
        aligner.addReference(reference);
        KAlignmentResult2<NucleotideSequence> align = aligner.align(query);
        System.out.println(align.getHits().size());
    }
}
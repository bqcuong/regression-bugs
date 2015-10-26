package com.milaboratory.core.alignment.kaligner2;

import com.milaboratory.core.alignment.AffineGapAlignmentScoring;
import com.milaboratory.core.sequence.NucleotideSequence;
import org.junit.Test;

/**
 * Created by poslavsky on 26/10/15.
 */
public class KAligner2Test {
    public static final AffineGapAlignmentScoring<NucleotideSequence> scoring = new AffineGapAlignmentScoring<>(
            NucleotideSequence.ALPHABET, 3, -1, -3, -1);
    public static final KAlignerParameters2 gParams = new KAlignerParameters2(5, false, false,
            15, -20, 15, 0.87f, 13, -7,
            -3, 3, 6, 4, 3, 0, 0, 0, 0, scoring);

    @Test
    public void test1() throws Exception {
        KAligner2<Object> aligner = new KAligner2<>(gParams);
        aligner.addReference(new NucleotideSequence("atgcgtcgatcgtagctagctgatcgatcgactgactagcatcagcatcaggatgtagagctagctagctac"));
        KAlignmentResult2<Object> al = aligner.align(new NucleotideSequence("atgcgtcgatcgtagctagctgtcgatcgactgaatgtagagctagctagctac"));
        System.out.println(al.hasHits());
        System.out.println(al.getHits().get(0).getAlignment());

    }
}
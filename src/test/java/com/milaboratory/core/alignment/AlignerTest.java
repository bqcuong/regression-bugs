package com.milaboratory.core.alignment;

import com.milaboratory.core.sequence.NucleotideSequence;
import org.junit.Test;

public class AlignerTest {
    @Test
    public void test1() throws Exception {
        Alignment<NucleotideSequence> alignment = Aligner.align(
                LinearGapAlignmentScoring.getNucleotideBLASTScoring(),
                new NucleotideSequence("ATTAGACA"),
                new NucleotideSequence("ATTAAGA"));

        System.out.println(alignment.getAlignmentHelper());
    }
}
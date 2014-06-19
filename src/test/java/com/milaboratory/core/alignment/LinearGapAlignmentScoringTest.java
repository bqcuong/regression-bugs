package com.milaboratory.core.alignment;

import com.milaboratory.core.sequence.IncompleteNucleotideSequence;
import com.milaboratory.util.GlobalObjectMappers;
import org.junit.Assert;
import org.junit.Test;

public class LinearGapAlignmentScoringTest {
    @Test
    public void test1() throws Exception {
        AlignmentScoring expected = AffineGapAlignmentScoring.getNucleotideBLASTScoring();
        String s = GlobalObjectMappers.PRETTY.writeValueAsString(expected);
        AlignmentScoring scoring = GlobalObjectMappers.ONE_LINE.readValue(s, AlignmentScoring.class);
        Assert.assertEquals(expected, scoring);
    }

    @Test
    public void test2() throws Exception {
        AlignmentScoring expected = new LinearGapAlignmentScoring<IncompleteNucleotideSequence>(IncompleteNucleotideSequence.ALPHABET, 15, -24, -4);
        String s = GlobalObjectMappers.PRETTY.writeValueAsString(expected);
        AlignmentScoring scoring = GlobalObjectMappers.ONE_LINE.readValue(s, AlignmentScoring.class);
        Assert.assertEquals(expected, scoring);
    }
}

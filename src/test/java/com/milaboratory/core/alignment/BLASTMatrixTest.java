package com.milaboratory.core.alignment;

import com.milaboratory.core.sequence.AminoAcidSequence;
import org.junit.Assert;
import org.junit.Test;

import static com.milaboratory.core.sequence.AminoAcidAlphabet.*;

public class BLASTMatrixTest {
    @Test
    public void testSum1() throws Exception {
        int[] matrix = BLASTMatrix.PAM70.getMatrix(AminoAcidSequence.ALPHABET);
        LinearGapAlignmentScoring scoring = new LinearGapAlignmentScoring(AminoAcidSequence.ALPHABET, matrix, -2);
        Assert.assertEquals(-5, scoring.getScore(I, Q));
        Assert.assertEquals(1, scoring.getScore(Stop, Stop));
        Assert.assertEquals(-11, scoring.getScore(IncompleteCodon, Stop));
        Assert.assertEquals(-11, scoring.getScore(A, Stop));
        Assert.assertEquals(-11, scoring.getScore(Stop, A));
        Assert.assertEquals(0, scoring.getScore(E, N));
        Assert.assertEquals(3, scoring.getScore(V, I));
        Assert.assertEquals(-3, scoring.getScore(IncompleteCodon, IncompleteCodon));
    }
}

package com.milaboratory.core.alignment;

import com.milaboratory.core.sequence.AminoAcidSequence;
import com.milaboratory.core.sequence.IncompleteAminoAcidSequence;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

import static com.milaboratory.core.alignment.ScoringMatrixIO.readAABlastMatrix;

public class ScoringMatrixIOTest {
    @Test(expected = IllegalArgumentException.class)
    public void testReadMatrix2() throws Exception {
        try (InputStream stream = ScoringMatrixIO.class.getClassLoader().getResourceAsStream("matrices/PAM70")) {
            int[] matrix = readAABlastMatrix(stream, AminoAcidSequence.ALPHABET);
        }
    }

    @Test
    public void testReadMatrix3() throws Exception {
        try (InputStream stream = ScoringMatrixIO.class.getClassLoader().getResourceAsStream("matrices/PAM70")) {
            int[] matrix = readAABlastMatrix(stream, IncompleteAminoAcidSequence.ALPHABET, '_', '.');
            int sum = 0;
            for (int i : matrix)
                sum += i;
            Assert.assertEquals(-2057, sum);
        }
    }
}
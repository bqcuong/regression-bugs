package com.milaboratory.core.alignment;

import com.milaboratory.core.mutations.Mutation;
import com.milaboratory.core.mutations.Mutations;
import com.milaboratory.core.sequence.NucleotideSequence;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public class AlignmentUtilsTest {
    @Test
    public void testScore() {
        NucleotideSequence s1 = new NucleotideSequence("CGTA");

        int[] mut = new int[2];
        mut[0] = Mutation.createInsertion(1, 1);
        mut[1] = Mutation.createSubstitution(3, 0, 1);
        float score = AlignmentUtils.calculateScore(LinearGapAlignmentScoring.getNucleotideBLASTScoring(),
                s1.size(), new Mutations(NucleotideSequence.ALPHABET, mut));

        //3 matches(15) - 1 mismatch(-4) - 1 insertion(-4);
        Assert.assertEquals(6, (int) score);
    }
}
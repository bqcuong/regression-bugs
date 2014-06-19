package com.milaboratory.core.mutations.generator;

import com.milaboratory.core.mutations.Mutation;
import org.junit.Assert;
import org.junit.Test;

public class GenericNucleotideMutationModelTest {
    @Test
    public void test1() throws Exception {
        GenericNucleotideMutationModel model =
                new GenericNucleotideMutationModel(
                        SubstitutionModels.getUniformNucleotideSubstitutionModel(.1), .1, .1)
                        .multiplyProbabilities(2.0);
        model.reseed(12335872L);
        for (int k = 0; k < 100; ++k) {
            int ins = 0, del = 0, sub = 0;
            for (int i = 0; i < 100000; ++i) {
                int m = model.generateMutation(0, 1);

                switch (Mutation.getRawTypeCode(m)) {
                    case Mutation.RAW_MUTATION_TYPE_SUBSTITUTION:
                        ++sub;
                        break;
                    case Mutation.RAW_MUTATION_TYPE_DELETION:
                        ++del;
                        break;
                    case Mutation.RAW_MUTATION_TYPE_INSERTION:
                        ++ins;
                        break;
                }
            }

            Assert.assertEquals(20000, sub, 1000);
            Assert.assertEquals(20000, del, 1000);
            Assert.assertEquals(20000, ins, 1000);
        }
    }
}

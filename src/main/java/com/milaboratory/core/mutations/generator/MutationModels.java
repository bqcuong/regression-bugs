package com.milaboratory.core.mutations.generator;

import org.apache.commons.math3.random.RandomGenerator;

import static com.milaboratory.core.mutations.generator.SubstitutionModels.getEmpiricalNucleotideSubstitutionModelWithNoise;

public class MutationModels {
    public static NucleotideMutationModel getEmpiricalNucleotideMutationModel() {
        return new GenericNucleotideMutationModel(
                SubstitutionModels.getEmpiricalNucleotideSubstitutionModel(), 0.00522, 0.00198);
    }

    public static NucleotideMutationModel getEmpiricalNucleotideMutationModelWithNoise(
            RandomGenerator rd, double minFactor, double maxFactor) {
        double l = maxFactor - minFactor;
        return new GenericNucleotideMutationModel(
                getEmpiricalNucleotideSubstitutionModelWithNoise(rd, minFactor, maxFactor),
                0.00522 * (minFactor + l * rd.nextDouble()),
                0.00198 * (minFactor + l * rd.nextDouble()),
                rd.nextLong());
    }
}

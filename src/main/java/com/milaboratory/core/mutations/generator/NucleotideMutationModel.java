package com.milaboratory.core.mutations.generator;

public interface NucleotideMutationModel {
    void reseed(long seed);

    int generateMutation(int position, int inputLetter);

    NucleotideMutationModel multiplyProbabilities(double factor);

    /**
     * Clones only parameters, the state is generated randomly. Use {@link #reseed(long)} to obtain the same sequence of
     * mutations.
     *
     * @return copy of this mutation model
     */
    NucleotideMutationModel clone();
}

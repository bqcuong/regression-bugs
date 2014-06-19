package com.milaboratory.core.mutations.generator;

import com.milaboratory.core.sequence.Alphabet;

public final class SubstitutionModelBuilder {
    final int size;
    final double[] probabilities;

    public SubstitutionModelBuilder(Alphabet alphabet) {
        this(alphabet.size());
    }

    public SubstitutionModelBuilder(int letters) {
        this.size = letters;
        this.probabilities = new double[letters * letters];
    }

    public void setProbability(int from, int to, double value) {
        this.probabilities[from * size + to] = value;
    }

    public SubstitutionModel build() {
        return new SubstitutionModel(probabilities);
    }

    public double getProbability(int fromLetter, int toLetter) {
        return this.probabilities[fromLetter * size + toLetter];
    }
}

package com.milaboratory.core.mutations.generator;

import org.apache.commons.math3.random.RandomGenerator;

import java.util.Arrays;

import static java.lang.Math.round;
import static java.lang.Math.sqrt;

public final class SubstitutionModel {
    final int size;
    final double[] probabilities;
    final double[] cdf;

    public SubstitutionModel(double[] probabilities) {
        this.size = (int) round(sqrt(probabilities.length));

        if (probabilities.length != size * size)
            throw new IllegalArgumentException();

        this.probabilities = probabilities.clone();

        int i, j;
        double sum;
        //Normalize rows
        for (i = 0; i < size; ++i) {
            sum = 0;
            for (j = 0; j < size; ++j)
                sum += this.probabilities[i * size + j];
            if ((this.probabilities[i * size + i] -= sum - 1.0) < 0.0)
                throw new IllegalArgumentException();
        }

        this.cdf = new double[size * size];
        for (i = 0; i < size; ++i) {
            sum = 0;
            for (j = 0; j < size; ++j)
                this.cdf[i * size + j] = (sum += this.probabilities[i * size + j]);
            assert sum <= 1.001 & sum >= 0.9999;
        }
    }

    public SubstitutionModel multiplyProbabilities(double factor) {
        double[] newProbabilities = new double[size * size];
        for (int i = size * size - 1; i >= 0; --i)
            newProbabilities[i] = probabilities[i] * factor;
        return new SubstitutionModel(newProbabilities);
    }

    public int randomLetter(RandomGenerator generator, int letter) {
        double value = generator.nextDouble();
        int index = Arrays.binarySearch(cdf, letter * size, (letter + 1) * size, value);
        if (index < 0)
            index = -index - 1;
        index -= letter * size;
        assert index < size;
        return index;
    }

    public double getTotalSubstitutionProbability(int letter) {
        return 1.0 - probabilities[letter * size + letter];
    }

    public double getProbability(int fromLetter, int toLetter) {
        return this.probabilities[fromLetter * size + toLetter];
    }
}

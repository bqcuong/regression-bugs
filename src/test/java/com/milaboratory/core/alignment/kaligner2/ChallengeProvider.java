package com.milaboratory.core.alignment.kaligner2;

import cc.redberry.pipe.OutputPort;
import com.milaboratory.core.mutations.generator.GenericNucleotideMutationModel;
import com.milaboratory.core.mutations.generator.SubstitutionModels;
import com.milaboratory.core.sequence.NucleotideSequence;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;

import static com.milaboratory.test.TestUtil.randomSequence;

/**
 * Created by dbolotin on 27/10/15.
 */
public class ChallengeProvider implements OutputPort<Challenge> {
    public static final ChallengeParameters DEFAULT_PARAMETERS = new ChallengeParameters(100, 100, 500,
            100000,
            1, 4, 15, 50, 3, 30,
            0.45, 0.45, 0.5,
            new GenericNucleotideMutationModel(
                    SubstitutionModels.getEmpiricalNucleotideSubstitutionModel(),
                    0.000522, 0.000198).multiplyProbabilities(50)
    );

    final ChallengeParameters parameters;
    final RandomGenerator gen;
    final RandomDataGenerator rdg;

    public ChallengeProvider(ChallengeParameters parameters, long seed) {
        this.parameters = parameters;
        this.gen = new Well19937c(seed);
        this.rdg = new RandomDataGenerator(gen);
    }

    @Override
    public Challenge take() {
        return new Challenge(generateDB(rdg, parameters), parameters, gen.nextLong());
    }

    public static NucleotideSequence[] generateDB(RandomDataGenerator generator, ChallengeParameters params) {
        NucleotideSequence[] db = new NucleotideSequence[params.dbSize];
        for (int i = 0; i < params.dbSize; i++)
            db[i] = randomSequence(NucleotideSequence.ALPHABET, generator, params.dbMinSeqLength, params.dbMaxSeqLength);
        return db;
    }

    public static ChallengeParameters getParams1(double multiplier) {
        return new ChallengeParameters(100, 100, 500,
                100000,
                1, 4, 15, 50, 3, 30,
                0.45, 0.45, 0.5,
                new GenericNucleotideMutationModel(
                        SubstitutionModels.getEmpiricalNucleotideSubstitutionModel(),
                        0.000522, 0.000198).multiplyProbabilities(multiplier)
        );
    }

    public static ChallengeParameters getParams1NoGap(double multiplier) {
        return new ChallengeParameters(100, 100, 500,
                100000,
                1, 4, 15, 50, 3, 30,
                0.45, 0.45, 0.5,
                new GenericNucleotideMutationModel(
                        SubstitutionModels.getEmpiricalNucleotideSubstitutionModel(),
                        0, 0).multiplyProbabilities(multiplier)
        );
    }

    public static ChallengeParameters getParams2OneCluster(double multiplier) {
        return new ChallengeParameters(100, 100, 500,
                100000,
                1, 1, 30, 80, 3, 30,
                0.45, 0.45, 0.5,
                new GenericNucleotideMutationModel(
                        SubstitutionModels.getEmpiricalNucleotideSubstitutionModel(),
                        0.00522, 0.00198).multiplyProbabilities(multiplier)
        );
    }
}

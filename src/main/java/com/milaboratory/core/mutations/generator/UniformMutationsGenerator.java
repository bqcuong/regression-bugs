package com.milaboratory.core.mutations.generator;

import com.milaboratory.core.mutations.Mutation;
import com.milaboratory.core.mutations.MutationType;
import com.milaboratory.core.mutations.Mutations;
import com.milaboratory.core.sequence.Sequence;
import org.apache.commons.math3.random.RandomGenerator;

public class UniformMutationsGenerator {
    private static final MutationType[] types = MutationType.values();

    public static final <S extends Sequence<S>> Mutations<S> createUniformMutationAsObject(S sequence,
                                                                                           RandomGenerator generator) {
        return new Mutations<>(sequence.getAlphabet(), createUniformMutation(sequence, generator));
    }

    public static final <S extends Sequence<S>> int createUniformMutation(S sequence, RandomGenerator generator) {
        return createUniformMutation(sequence, generator, types[generator.nextInt(3)]);
    }

    public static final <S extends Sequence<S>> Mutations<S> createUniformMutationAsObject(S sequence,
                                                                                           RandomGenerator generator,
                                                                                           MutationType type) {
        return new Mutations<>(sequence.getAlphabet(), createUniformMutation(sequence, generator, type));
    }

    public static final <S extends Sequence<S>> int createUniformMutation(S sequence, RandomGenerator generator,
                                                                          MutationType type) {
        int position;
        byte from, to;
        int alphabetSize = sequence.getAlphabet().size();
        switch (type) {
            case Substitution:
                position = generator.nextInt(sequence.size());
                from = sequence.codeAt(position);
                to = (byte) ((from + 1 + generator.nextInt(alphabetSize - 1)) % alphabetSize);
                assert from != to;
                return Mutation.createSubstitution(position, from, to);
            case Deletion:
                position = generator.nextInt(sequence.size());
                from = sequence.codeAt(position);
                return Mutation.createDeletion(position, from);
            case Insertion:
                position = generator.nextInt(sequence.size() + 1);
                to = (byte) generator.nextInt(alphabetSize);
                return Mutation.createInsertion(position, to);
        }
        throw new NullPointerException();
    }
}

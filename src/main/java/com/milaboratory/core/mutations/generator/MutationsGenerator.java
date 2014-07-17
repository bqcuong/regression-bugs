package com.milaboratory.core.mutations.generator;

import com.milaboratory.core.mutations.Mutations;
import com.milaboratory.core.mutations.MutationsBuilder;
import com.milaboratory.core.sequence.NucleotideSequence;

import static com.milaboratory.core.mutations.Mutation.*;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public final class MutationsGenerator {
    private MutationsGenerator() {
    }

    public static Mutations<NucleotideSequence> generateMutations(NucleotideSequence sequence,
                                                                  NucleotideMutationModel model,
                                                                  int from, int to) {
        MutationsBuilder<NucleotideSequence> builder = new MutationsBuilder<>(NucleotideSequence.ALPHABET);
        int mut, previous = NON_MUTATION;
        for (int i = from; i < to; ++i) {
            mut = model.generateMutation(i, sequence.codeAt(i));
            if (mut != NON_MUTATION) {
                switch (getRawTypeCode(mut)) {
                    case RAW_MUTATION_TYPE_SUBSTITUTION:
                        builder.append(mut);
                        break;
                    case RAW_MUTATION_TYPE_DELETION:
                        if (getRawTypeCode(previous) == RAW_MUTATION_TYPE_INSERTION)
                            mut = NON_MUTATION;
                        else
                            builder.append(mut);
                        break;
                    case RAW_MUTATION_TYPE_INSERTION:
                        if (getRawTypeCode(previous) == RAW_MUTATION_TYPE_DELETION)
                            mut = NON_MUTATION;
                        else {
                            builder.append(mut);
                            --i;
                        }
                        break;
                }
            }
            previous = mut;
        }

        mut = model.generateMutation(to, -1);
        if (getRawTypeCode(mut) == RAW_MUTATION_TYPE_INSERTION &&
                getRawTypeCode(previous) != RAW_MUTATION_TYPE_DELETION)
            builder.append(mut);

        return builder.createAndDestroy();
    }

    public static Mutations<NucleotideSequence> generateMutations(NucleotideSequence sequence,
                                                                  NucleotideMutationModel model) {
        return generateMutations(sequence, model, 0, sequence.size());
    }

}

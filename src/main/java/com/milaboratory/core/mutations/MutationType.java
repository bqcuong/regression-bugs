package com.milaboratory.core.mutations;

import static com.milaboratory.core.mutations.Mutation.*;

public enum MutationType {
    Substitution(RAW_MUTATION_TYPE_SUBSTITUTION),
    Deletion(RAW_MUTATION_TYPE_DELETION),
    Insertion(RAW_MUTATION_TYPE_INSERTION);
    public final int rawType;

    private MutationType(int rawType) {
        this.rawType = rawType;
    }

    private static MutationType[] types = new MutationType[4];

    static {
        for (MutationType mutationType : values())
            types[mutationType.rawType >> MUTATION_TYPE_OFFSET] = mutationType;
    }

    public static MutationType getType(int rawType) {
        return types[rawType >> MUTATION_TYPE_OFFSET];
    }
}

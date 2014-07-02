package com.milaboratory.core.tree;

import com.milaboratory.core.sequence.Sequence;

public interface MutationGuide<S extends Sequence<S>> {
    boolean allowMutation(S reference, int position, byte type, byte to);
}

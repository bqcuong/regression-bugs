package com.milaboratory.core.clustering;

import com.milaboratory.core.sequence.Sequence;
import com.milaboratory.core.tree.NeighborhoodIterator;
import com.milaboratory.core.tree.TreeSearchParameters;

import java.util.Comparator;

public interface ClusteringStrategy<T, S extends Sequence<S>> extends Comparator<T> {
    boolean canAddToCluster(Cluster<T> cluster, T minorObject,
                            NeighborhoodIterator<T[], S> iterator);

    TreeSearchParameters getSearchParameters();

    int getMaxClusterDepth();
}

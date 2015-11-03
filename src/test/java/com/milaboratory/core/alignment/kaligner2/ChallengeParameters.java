package com.milaboratory.core.alignment.kaligner2;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.milaboratory.core.mutations.generator.NucleotideMutationModel;

/**
 * Created by dbolotin on 27/10/15.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE)
public final class ChallengeParameters {
    final int dbSize, dbMinSeqLength, dbMaxSeqLength;
    final int queryCount;
    final int minClusters, maxClusters,
            minClusterLength, maxClusterLength,
            minIndelLength, maxIndelLength;
    final double insertionProbability, deletionProbability, boundaryInsertProbability;
    final NucleotideMutationModel mutationModel;

    public ChallengeParameters(int dbSize, int dbMinSeqLength, int dbMaxSeqLength, int queryCount, int minClusters, int maxClusters, int minClusterLength, int maxClusterLength, int minIndelLength, int maxIndelLength, double insertionProbability, double deletionProbability, double boundaryInsertProbability, NucleotideMutationModel mutationModel) {
        this.dbSize = dbSize;
        this.dbMinSeqLength = dbMinSeqLength;
        this.dbMaxSeqLength = dbMaxSeqLength;
        this.queryCount = queryCount;
        this.minClusters = minClusters;
        this.maxClusters = maxClusters;
        this.minClusterLength = minClusterLength;
        this.maxClusterLength = maxClusterLength;
        this.minIndelLength = minIndelLength;
        this.maxIndelLength = maxIndelLength;
        this.insertionProbability = insertionProbability;
        this.deletionProbability = deletionProbability;
        this.boundaryInsertProbability = boundaryInsertProbability;
        this.mutationModel = mutationModel;
    }
}

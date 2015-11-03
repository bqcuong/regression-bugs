package com.milaboratory.core.alignment.kaligner2;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.milaboratory.core.Range;
import com.milaboratory.core.mutations.Mutations;
import com.milaboratory.core.sequence.NucleotideSequence;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE)
public class KAligner2Query {
    public final int targetId;
    public final List<Range> queryClusters, targetClusters;
    public final List<Mutations<NucleotideSequence>> mutationsInTarget;
    public final NucleotideSequence query;

    public KAligner2Query(int targetId, List<Range> queryClusters, List<Range> targetClusters,
                          List<Mutations<NucleotideSequence>> mutationsInTarget, NucleotideSequence query) {
        this.targetId = targetId;
        this.queryClusters = queryClusters;
        this.targetClusters = targetClusters;
        this.mutationsInTarget = mutationsInTarget;
        this.query = query;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "targetId=" + targetId +
                ", queryClusters=" + queryClusters +
                ", targetClusters=" + targetClusters +
                ", mutationsInTarget=" + mutationsInTarget +
                ", query=" + query +
                '}';
    }
}

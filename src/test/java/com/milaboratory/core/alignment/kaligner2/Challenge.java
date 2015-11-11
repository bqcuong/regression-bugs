package com.milaboratory.core.alignment.kaligner2;

import cc.redberry.pipe.CUtils;
import cc.redberry.pipe.OutputPort;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.milaboratory.core.sequence.NucleotideSequence;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE)
public final class Challenge {
    final NucleotideSequence[] db;
    @JsonIgnore
    public final ChallengeParameters parameters;
    public final List<KAligner2Query> queries;
    public final long seed;

    public Challenge(NucleotideSequence[] db, List<KAligner2Query> queries,
                     ChallengeParameters parameters, long seed) {
        this.db = db;
        this.parameters = parameters;
        this.queries = queries;
        this.seed = seed;
    }

    public NucleotideSequence[] getDB() {
        return db;
    }

    public OutputPort<KAligner2Query> queries() {
        return CUtils.asOutputPort(queries);
    }
}

package com.milaboratory.core.alignment.kaligner2;

import cc.redberry.pipe.OutputPort;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.milaboratory.core.Range;
import com.milaboratory.core.mutations.Mutations;
import com.milaboratory.core.mutations.generator.MutationsGenerator;
import com.milaboratory.core.mutations.generator.NucleotideMutationModel;
import com.milaboratory.core.sequence.NucleotideSequence;
import com.milaboratory.core.sequence.SequenceBuilder;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.milaboratory.test.TestUtil.randomSequence;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE)
public final class Challenge {
    final NucleotideSequence[] db;
    @JsonIgnore
    public final ChallengeParameters parameters;
    public final long seed;

    public Challenge(NucleotideSequence[] db, ChallengeParameters parameters, long seed) {
        this.db = db;
        this.parameters = parameters;
        this.seed = seed;
    }

    public NucleotideSequence[] getDB() {
        return db;
    }

    public OutputPort<KAligner2Query> queries() {
        final RandomGenerator rg = new Well19937c(seed);
        final RandomDataGenerator generator = new RandomDataGenerator(rg);
        final AtomicInteger counter = new AtomicInteger();

        return new OutputPort<KAligner2Query>() {
            @Override
            public KAligner2Query take() {

                if (counter.incrementAndGet() > parameters.queryCount)
                    return null;
                NucleotideMutationModel model = parameters.mutationModel.clone();
                model.reseed(generator.getRandomGenerator().nextLong());
                int targetId = generator.nextInt(0, db.length - 1);
                NucleotideSequence target = db[targetId];
                SequenceBuilder<NucleotideSequence> queryBuilder = NucleotideSequence.ALPHABET.getBuilder();
                if (generator.nextUniform(0, 1) < parameters.boundaryInsertProbability)
                    queryBuilder.append(randomSequence(NucleotideSequence.ALPHABET, generator, parameters.minIndelLength, parameters.maxIndelLength, true));

                List<Range> tRanges = new ArrayList<>(), qRanges = new ArrayList<>();
                List<Mutations<NucleotideSequence>> muts = new ArrayList<>();

                int tOffset = generator.nextInt(0, parameters.maxIndelLength), qOffset = queryBuilder.size();
                Range r;
                Mutations<NucleotideSequence> m;
                NucleotideSequence ins;
                double v;
                for (int i = generator.nextInt(parameters.minClusters, parameters.maxClusters); i >= 0; --i)
                    if (tRanges.isEmpty()) {
                        r = new Range(tOffset, tOffset += generator.nextInt(parameters.minClusterLength, parameters.maxClusterLength));
                        if (r.getTo() > target.size())
                            break;
                        tRanges.add(r);
                        muts.add(m = MutationsGenerator.generateMutations(target, model, r));
                        qRanges.add(new Range(qOffset, qOffset += r.length() + m.getLengthDelta()));
                        queryBuilder.append(m.move(-r.getFrom()).mutate(target.getRange(r)));
                    } else {
                        if ((v = generator.nextUniform(0, 1.0)) < parameters.insertionProbability)
                            ins = randomSequence(NucleotideSequence.ALPHABET, generator, parameters.minIndelLength, parameters.maxIndelLength, true);
                        else if (v < parameters.insertionProbability + parameters.deletionProbability) {
                            tOffset += generator.nextInt(parameters.minIndelLength, parameters.maxIndelLength);
                            ins = NucleotideSequence.EMPTY;
                        } else {
                            ins = randomSequence(NucleotideSequence.ALPHABET, generator, parameters.minIndelLength, parameters.maxIndelLength, true);
                            tOffset += generator.nextInt(parameters.minIndelLength, parameters.maxIndelLength);
                        }
                        r = new Range(tOffset, tOffset += generator.nextInt(parameters.minClusterLength, parameters.maxClusterLength));
                        if (r.getTo() > target.size())
                            break;
                        tRanges.add(r);
                        muts.add(m = MutationsGenerator.generateMutations(target, model, r));
                        qRanges.add(new Range(qOffset += ins.size(), qOffset += r.length() + m.getLengthDelta()));
                        queryBuilder.append(ins).append(m.move(-r.getFrom()).mutate(target.getRange(r)));
                    }

                if (generator.nextUniform(0, 1) < parameters.boundaryInsertProbability)
                    queryBuilder.append(randomSequence(NucleotideSequence.ALPHABET, generator, parameters.minIndelLength, parameters.maxIndelLength, true));

                return new KAligner2Query(targetId, qRanges, tRanges, muts, queryBuilder.createAndDestroy());
            }
        };
    }
}

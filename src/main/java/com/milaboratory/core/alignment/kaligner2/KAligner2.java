package com.milaboratory.core.alignment.kaligner2;

import com.milaboratory.core.Range;
import com.milaboratory.core.alignment.Alignment;
import com.milaboratory.core.alignment.BandedAffineAligner;
import com.milaboratory.core.alignment.BandedSemiLocalResult;
import com.milaboratory.core.alignment.batch.BatchAlignerWithBase;
import com.milaboratory.core.mutations.Mutations;
import com.milaboratory.core.mutations.MutationsBuilder;
import com.milaboratory.core.sequence.NucleotideSequence;
import com.milaboratory.util.IntArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public class KAligner2<P> implements BatchAlignerWithBase<NucleotideSequence, P, KAlignmentHit2<P>> {
    /**
     * Link to KMapper
     */
    final KMapper2 mapper;
    /**
     * Parameters of alignment
     */
    final KAlignerParameters2 parameters;
    /**
     * Base records for reference sequences
     */
    final List<NucleotideSequence> sequences = new ArrayList<>();
    /**
     * Record payloads.
     */
    final TIntObjectHashMap<P> payloads = new TIntObjectHashMap<>();

    public KAligner2(KAlignerParameters2 parameters) {
        this.parameters = parameters;
        this.mapper = KMapper2.createFromParameters(parameters);
    }

    /**
     * Adds new reference sequence to the base of this aligner and returns index assigned to it.
     *
     * @param sequence sequence
     * @return index assigned to the sequence
     */
    public int addReference(NucleotideSequence sequence) {
        return addReference(sequence, 0, sequence.size());
    }

    /**
     * Adds new reference sequence to the base of this mapper and returns index assigned to it.
     * <p/>
     * <p>User can specify a part of a sequence to be indexed by {@link com.milaboratory.core.alignment.KMapper},
     * but {@link com.milaboratory.core.alignment.KAligner} stores whole adding sequences.</p>
     *
     * @param sequence sequence
     * @param offset   offset of subsequence to be indexed by {@link com.milaboratory.core.alignment.KMapper}
     * @param length   length of subsequence to be indexed by {@link com.milaboratory.core.alignment.KMapper}
     * @return index assigned to the sequence
     */
    public int addReference(NucleotideSequence sequence, int offset, int length) {
        if (sequence.containWildcards())
            throw new IllegalArgumentException("Reference sequences with wildcards not supported.");
        int id = mapper.addReference(sequence, offset, length);
        assert sequences.size() == id;
        sequences.add(sequence);
        return id;
    }

    /**
     * Returns sequence by its id (order number) in a base.
     *
     * @param id id of sequence to be returned
     * @return sequence
     */
    public NucleotideSequence getReference(int id) {
        return sequences.get(id);
    }

    @Override
    public void addReference(NucleotideSequence sequence, P payload) {
        payloads.put(addReference(sequence), payload);
    }

    @Override
    public KAlignmentResult2<P> align(NucleotideSequence sequence) {
        return align(sequence, 0, sequence.size());
    }

    public KAlignmentResult2<P> align(final NucleotideSequence query, final int from, final int to) {
        final BandedAffineAligner.MatrixCache cache = new BandedAffineAligner.MatrixCache();

        final KMappingResult2 mapping = mapper.align(query, from, to);
        final IntArrayList seeds = mapping.seeds;

        List<KAlignmentHit2<P>> hits = new ArrayList<>();

        final int maxIndels = parameters.getMapperMaxClusterIndels();
        final int kValue = mapper.getKValue();

        // 1 - target
        // 2 - query

        // maxIndels = 5
        // length1 = 100
        // length2 = 100

        // added1 = maxIndels
        // added2 = maxIndels


        // maxIndels = 5
        // length1 = 100
        // length2 = 130

        // added1 = 0
        // length2 = 100 + maxIndels
        // added2 = maxIndels * 2


        // maxIndels = 5
        // length1 = 100
        // length2 = 103

        // delta = sig(length2 - length1) * min(length2 - length1, maxIndels)
        // added1 = maxIndels - delta
        // added2 = maxIndels + delta

        int length1, length2, added1, added2, offset1, offset2, delta;
        int targetFrom, targetTo, queryFrom, queryTo;

        for (int hitIndex = 0; hitIndex < mapping.getHits().size(); hitIndex++) {
            final KMappingHit2 mappingHit = mapping.getHits().get(hitIndex);
            final NucleotideSequence target = sequences.get(mappingHit.id);
            final MutationsBuilder<NucleotideSequence> mutations =
                    new MutationsBuilder<NucleotideSequence>(NucleotideSequence.ALPHABET);
            //Left edge alignment

            int seedPosition2 = seeds.get(mappingHit.indexById(0));
            int seedPosition1 = seedPosition2 + mappingHit.offsetById(0);

            length1 = seedPosition1;
            length2 = seedPosition2;

            if (length1 >= length2) {
                delta = Math.min(length1 - length2, maxIndels);
                added1 = maxIndels + delta;
                added2 = maxIndels - delta;
                if (length1 > length2 + maxIndels)
                    length1 = length2 + maxIndels;
            } else {
                delta = Math.min(length2 - length1, maxIndels);
                added1 = maxIndels - delta;
                added2 = maxIndels + delta;
                if (length2 > length1 + maxIndels)
                    length2 = length1 + maxIndels;
            }

            offset1 = seedPosition1 - length1;
            offset2 = seedPosition2 - length2;

            BandedSemiLocalResult br;
            if (parameters.isFloatingLeftBound()) {
                br = BandedAffineAligner.semiLocalLeft0(parameters.getScoring(), target, query,
                        offset1, length1,
                        offset2, length2,
                        maxIndels, mutations, cache);

            } else {
                br = BandedAffineAligner.semiGlobalLeft0(parameters.getScoring(), target, query,
                        offset1, length1, added1,
                        offset2, length2, added2,
                        maxIndels, mutations, cache);
            }
            targetFrom = br.sequence1Stop;
            queryFrom = br.sequence2Stop;


            int previousSeedPosition1 = -1,
                    previousSeedPosition2 = -1;

            int boundaryPointer = 0;
            // for each cluster
            for (int seedId = 0; seedId < mappingHit.seedRecords.length; seedId++) {
                if (seedId == mappingHit.boundaries[boundaryPointer]) {
                    //todo

//                    previousSeedPosition1 = -1;
//                    previousSeedPosition2 = -1;
//                    ++boundaryPointer;
//                    continue;
                }
                if (previousSeedPosition1 != -1) {
                    seedPosition2 = seeds.get(mappingHit.indexById(seedId));
                    seedPosition1 = seedPosition2 + mappingHit.offsetById(seedId);

                    offset1 = previousSeedPosition1 + kValue;
                    length1 = seedPosition1 - previousSeedPosition1;

                    offset2 = previousSeedPosition2 + kValue;
                    length2 = seedPosition2 - previousSeedPosition2;

//                if (refLength < 0 || seqLength < 0) {
//                    seqFrom -= kValue;
//                    refFrom -= kValue;
//                    seqLength += kValue;
//                    refLength += kValue;
//                }

                    assert target.getRange(offset1 - kValue, offset1).equals(query.getRange(offset2 - kValue, offset2));

                    if (length1 > 0 || length2 > 0)
                        BandedAffineAligner.align0(parameters.getScoring(), target, query,
                                offset1, length1,
                                offset2, length2,
                                maxIndels, mutations, cache);
                }
                previousSeedPosition1 = seedPosition1;
                previousSeedPosition2 = seedPosition2;
            }


            //Right edge
            offset2 = seeds.get(mappingHit.indexById(mappingHit.seedRecords.length - 1));
            offset1 = offset2 + mappingHit.offsetById(mappingHit.seedRecords.length - 1);

            length1 = mapper.refLength[mappingHit.id] - offset1;
            length2 = query.size() - offset2;

            if (length1 >= length2) {
                delta = Math.min(length1 - length2, maxIndels);
                added1 = maxIndels + delta;
                added2 = maxIndels - delta;
                if (length1 > length2 + maxIndels)
                    length1 = length2 + maxIndels;
            } else {
                delta = Math.min(length2 - length1, maxIndels);
                added1 = maxIndels - delta;
                added2 = maxIndels + delta;
                if (length2 > length1 + maxIndels)
                    length2 = length1 + maxIndels;
            }

            if (parameters.isFloatingRightBound()) {
                br = BandedAffineAligner.semiLocalRight0(parameters.getScoring(), target, query,
                        offset1, length1,
                        offset2, length2,
                        maxIndels, mutations, cache);
            } else {
                br = BandedAffineAligner.semiGlobalRight0(parameters.getScoring(), target, query,
                        offset1, length1, added1,
                        offset2, length2, added2,
                        maxIndels, mutations, cache);
            }

            targetTo = br.sequence1Stop + 1;
            queryTo = br.sequence2Stop + 1;

            Mutations<NucleotideSequence> muts = mutations.createAndDestroy();
            hits.add(new KAlignmentHit2<P>(null, mappingHit.id,
                    new Alignment<>(target,
                            muts,
                            new Range(targetFrom, targetTo),
                            new Range(queryFrom, queryTo),
                            0//todo!!!, nu
                    ), null));
        }

        return new KAlignmentResult2<>(mapping, hits, query, from, to);
    }

    //@Override
    //public <Q> OutputPort<? extends PipedAlignmentResult<KAlignmentHit2<P>, Q>> align(OutputPort<Q> input, SequenceExtractor<Q, NucleotideSequence> extractor) {
    //    return null;
    //}
    //
    //@Override
    //public <Q extends HasSequence<NucleotideSequence>> OutputPort<? extends PipedAlignmentResult<KAlignmentHit2<P>, Q>> align(OutputPort<Q> input) {
    //    return null;
    //}
}

package com.milaboratory.core.alignment.kaligner2;

import com.milaboratory.core.alignment.BandedAffineAligner;
import com.milaboratory.core.alignment.batch.BatchAlignerWithBase;
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

            if (parameters.isFloatingLeftBound()) {
                BandedAffineAligner.semiLocalLeft0(parameters.getScoring(), target, query,
                        offset1, length1,
                        offset2, length2,
                        maxIndels, mutations, cache);
            } else {
                BandedAffineAligner.semiGlobalLeft0(parameters.getScoring(), target, query,
                        offset1, length1, added1,
                        offset2, length2, added2,
                        maxIndels, mutations, cache);
            }

            

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

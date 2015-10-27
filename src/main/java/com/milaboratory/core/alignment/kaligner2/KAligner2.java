package com.milaboratory.core.alignment.kaligner2;

import com.milaboratory.core.Range;
import com.milaboratory.core.alignment.Alignment;
import com.milaboratory.core.alignment.AlignmentUtils;
import com.milaboratory.core.alignment.BandedAffineAligner;
import com.milaboratory.core.alignment.BandedSemiLocalResult;
import com.milaboratory.core.alignment.batch.BatchAlignerWithBase;
import com.milaboratory.core.alignment.kaligner2.KMapper2.ArrList;
import com.milaboratory.core.mutations.Mutations;
import com.milaboratory.core.mutations.MutationsBuilder;
import com.milaboratory.core.sequence.NucleotideSequence;
import com.milaboratory.util.IntArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
     * Adds new reference sequence to the base of this mapper and returns index assigned to it.
     *
     * @param sequence sequence
     * @return index assigned to the sequence
     */
    public int addReference(NucleotideSequence sequence) {
        if (sequence.containWildcards())
            throw new IllegalArgumentException("Reference sequences with wildcards not supported.");
        int id = mapper.addReference(sequence);
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

        ArrList<KAlignmentHit2<P>> hits = new ArrList<>();

        final int maxIndels = parameters.getMapperMaxClusterIndels();
        final int kValue = mapper.getKValue();

        KAlignmentResult2<P> kAlignmentResult = new KAlignmentResult2<>(mapping, hits, query, from, to);
        if (mapping.getHits().isEmpty())
            return kAlignmentResult;

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
        int seq1From, seq1To, seq2From, seq2To;

        for (int hitIndex = 0; hitIndex < mapping.getHits().size(); hitIndex++) {
            final KMappingHit2 mappingHit = mapping.getHits().get(hitIndex);
            final NucleotideSequence target = sequences.get(mappingHit.id);
            final MutationsBuilder<NucleotideSequence> mutations =
                    new MutationsBuilder<>(NucleotideSequence.ALPHABET);
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
            seq1From = br.sequence1Stop;
            seq2From = br.sequence2Stop;


            int previousSeedPosition2 = seeds.get(mappingHit.indexById(0)),
                    previousSeedPosition1 = previousSeedPosition2 + mappingHit.offsetById(0);

            for (int seedId = 1; seedId < mappingHit.seedRecords.length; seedId++) {
                seedPosition2 = seeds.get(mappingHit.indexById(seedId));
                seedPosition1 = seedPosition2 + mappingHit.offsetById(seedId);

                offset1 = previousSeedPosition1 + kValue;
                length1 = seedPosition1 - offset1;

                offset2 = previousSeedPosition2 + kValue;
                length2 = seedPosition2 - offset2;

                assert target.getRange(offset1 - kValue, offset1).equals(query.getRange(offset2 - kValue, offset2));

                if (length2 < 0 || length1 < 0) {
                    offset1 -= kValue;
                    offset2 -= kValue;
                    length1 += 2 * kValue;
                    length2 += 2 * kValue;
                }

                assert length1 >= 0 && length2 >= 0;

                BandedAffineAligner.align0(parameters.getScoring(), target, query,
                        offset1, length1,
                        offset2, length2,
                        maxIndels, mutations, cache);

                previousSeedPosition1 = seedPosition1;
                previousSeedPosition2 = seedPosition2;
            }

            //Right edge
            offset2 = seeds.get(mappingHit.indexById(mappingHit.seedRecords.length - 1)) + kValue;
            offset1 = offset2 + mappingHit.offsetById(mappingHit.seedRecords.length - 1);

            length1 = target.size() - offset1;
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

            seq1To = br.sequence1Stop + 1;
            seq2To = br.sequence2Stop + 1;

            Mutations<NucleotideSequence> muts = mutations.createAndDestroy();
            hits.add(new KAlignmentHit2<>(kAlignmentResult, mappingHit.id,
                    new Alignment<>(target, muts,
                            new Range(seq1From, seq1To),
                            new Range(seq2From, seq2To),
                            AlignmentUtils.calculateScore(parameters.getScoring(), seq1To - seq1From, muts)),
                    payloads.get(mappingHit.id)));
        }

        Collections.sort(hits, SCORE_COMPARATOR);
        int threshold = (int) Math.max(parameters.getAbsoluteMinScore(),
                parameters.getRelativeMinScore() * hits.get(0).getAlignment().getScore());
        int i = 0;
        for (; i < parameters.getMaxHits() && i < hits.size(); ++i)
            if (hits.get(i).getAlignment().getScore() < threshold)
                break;
        if (i < hits.size())
            hits.removeRange(i, hits.size());
        return kAlignmentResult;
    }

    private static final Comparator<KAlignmentHit2> SCORE_COMPARATOR = new Comparator<KAlignmentHit2>() {
        @Override
        public int compare(KAlignmentHit2 o1, KAlignmentHit2 o2) {
            return Double.compare(o2.alignment.getScore(), o1.alignment.getScore());
        }
    };
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

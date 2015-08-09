package com.milaboratory.core.alignment.blast;

import cc.redberry.pipe.CUtils;
import cc.redberry.pipe.OutputPort;
import cc.redberry.pipe.Processor;
import com.milaboratory.core.Range;
import com.milaboratory.core.alignment.Alignment;
import com.milaboratory.core.alignment.batch.*;
import com.milaboratory.core.sequence.Sequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlastAligner<S extends Sequence<S>, P> implements PipedBatchAlignerWithBase<S, P, BlastHit<S, P>> {
    private final List<S> sequenceList = new ArrayList<>();
    private final Map<String, S> sequences = new HashMap<>();
    private final Map<String, P> payloads = new HashMap<>();

    // Parameters
    private final BlastAlignerParameters parameters;

    // Not initialized -> null
    private volatile BlastDB db = null;
    private volatile ExternalDBBlastAligner<S> aligner = null;

    public BlastAligner() {
        this(null);
    }

    public BlastAligner(BlastAlignerParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public <Q> OutputPort<? extends PipedAlignmentResult<BlastHit<S, P>, Q>> align(OutputPort<Q> input, SequenceExtractor<Q, S> extractor) {
        ensureInit();
        return null;
    }

    @Override
    public <Q extends HasSequence<S>> OutputPort<PipedAlignmentResult<BlastHit<S, P>, Q>> align(OutputPort<Q> input) {
        ensureInit();
        OutputPort<PipedAlignmentResult<ExternalDBBlastHit<S>, Q>> iResults = aligner.align(input);
        return CUtils.wrap(iResults, new ResultsConverter<Q>());
    }

    private synchronized void ensureInit() {
        if (db != null)
            return;

        db = BlastDBBuilder.build(new ArrayList<>(sequenceList));
        aligner = new ExternalDBBlastAligner<>(db, parameters);
    }

    @Override
    public synchronized void addReference(S sequence, P payload) {
        if (db != null)
            throw new IllegalStateException("Aligner is already in use, can't add sequence to database.");

        // See BlastDBBuilder sequence naming convention (see code)
        String key = BlastDBBuilder.getId(sequenceList.size());
        // Adding to list for blastDB
        sequenceList.add(sequence);
        // Saving payload mapping
        payloads.put(key, payload);
        // Saving sequence mapping
        sequences.put(key, sequence);
    }

    private class ResultsConverter<Q> implements Processor<PipedAlignmentResult<ExternalDBBlastHit<S>, Q>, PipedAlignmentResult<BlastHit<S, P>, Q>> {
        @Override
        public PipedAlignmentResult<BlastHit<S, P>, Q> process(PipedAlignmentResult<ExternalDBBlastHit<S>, Q> input) {
            List<BlastHit<S, P>> hits = new ArrayList<>(input.getHits().size());
            for (ExternalDBBlastHit<S> iHit : input.getHits()) {
                String id = iHit.getTitle();
                S sequence = sequences.get(id);
                P payload = payloads.get(id);
                Alignment<S> alignment = iHit.getAlignment();
                Range subjectRange = iHit.getSubjectRange();
                alignment = new Alignment<>(sequence,
                        alignment.getAbsoluteMutations().move(subjectRange.getLower()), subjectRange, alignment.getSequence2Range(),
                        alignment.getScore());
                hits.add(new BlastHit<>(alignment, payload, iHit));
            }
            return new PipedAlignmentResultImpl<>(hits, input.getQuery());
        }
    }
}

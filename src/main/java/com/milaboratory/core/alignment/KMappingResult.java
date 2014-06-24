package com.milaboratory.core.alignment;

import com.milaboratory.util.IntArrayList;

import java.util.Collections;
import java.util.List;

/**
 * KMappingResult - class which is result of {@link com.milaboratory.core.alignment.KMapper#align(com.milaboratory.core.sequence.nucleotide.NucleotideSequence,
 * int, int)}, {@link com.milaboratory.core.alignment.KMapper#align(com.milaboratory.core.sequence.NucleotideSequence)}
 * methods. <p>It contains seeds used for aligning by {@link com.milaboratory.core.alignment.KMapper} and list
 * of hits found in target sequence.</p>
 */
public class KMappingResult {
    /**
     * Seeds used to align target sequence
     */
    IntArrayList seeds;
    /**
     * List of hits (potential candidates) for target sequence
     */
    List<KMappingHit> hits;

    /**
     * Creates new KMappingResult
     *
     * @param seeds seeds used for alignment
     * @param hits  hits obtained by {@link com.milaboratory.core.alignment.KMapper}
     */
    public KMappingResult(IntArrayList seeds, List<KMappingHit> hits) {
        this.seeds = seeds;
        this.hits = hits;
    }

    public int getSeedsCount() {
        return seeds.size();
    }

    public int getSeedPosition(int i) {
        return seeds.get(i);
    }

    public List<KMappingHit> getHits() {
        return Collections.unmodifiableList(hits);
    }
}

package com.milaboratory.core.alignment;

import com.milaboratory.core.Range;
import com.milaboratory.core.mutations.MutationsBuilder;
import com.milaboratory.core.sequence.NucleotideSequence;
import org.junit.Test;

/**
 * Created by poslavsky on 20/10/15.
 */
public class BandedAffineAlignerTest {
    @Test
    public void test1() throws Exception {
        AffineGapAlignmentScoring<NucleotideSequence> scoring = new AffineGapAlignmentScoring<>(
                NucleotideSequence.ALPHABET, 1, -1, -3, -1);

        NucleotideSequence a = new NucleotideSequence("ataaaaaaatgatcgacaaaaaaaatttttttt");
        NucleotideSequence b = new NucleotideSequence("agtcgttagcgacaaaaaaa");

        a = new NucleotideSequence("atcgagctagttttttttttt");
        b = new NucleotideSequence("atcgcagctag");

        MutationsBuilder<NucleotideSequence> mutations = new MutationsBuilder<>(NucleotideSequence.ALPHABET);
        BandedAffineAligner.align0(scoring, a, b, 0, a.size(), 0, b.size(), 151, mutations, new CachedIntArray(), new CachedIntArray(), new CachedIntArray());


        System.out.println(new Alignment<>(a, mutations.createAndDestroy(), new Range(0, a.size()), new Range(0, b.size()), 100));
    }
}
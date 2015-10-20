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
                NucleotideSequence.ALPHABET, 1, -10, -3, -1);

        NucleotideSequence a = new NucleotideSequence("ataaaaaaatgatcgacaaaaaaaatttttttt");
        NucleotideSequence b = new NucleotideSequence("agtcgttagcgacaaaaaaa");

        a = new NucleotideSequence("atcgagctagttttttttttt");
        b = new NucleotideSequence("ataaaaaaaaaaacgagctag");

        MutationsBuilder<NucleotideSequence> mutations = new MutationsBuilder<>(NucleotideSequence.ALPHABET);
        BandedAffineAligner.align0(scoring, a, b, 0, a.size(), 0, b.size(), 151, mutations, new BandedAffineAligner.MatrixCache());

        System.out.println(new Alignment<>(a, mutations.createAndDestroy(), new Range(0, a.size()), new Range(0, b.size()), 100));
        System.out.println(Aligner.alignGlobalAffine(scoring, a, b));
        //System.out.println(new Alignment<>(a, mutations.createAndDestroy(), new Range(0, a.size()), new Range(0, b.size()), 100));
    }

    @Test
    public void test2() throws Exception {
        AffineGapAlignmentScoring<NucleotideSequence> scoring = new AffineGapAlignmentScoring<>(
                NucleotideSequence.ALPHABET, 3, -1, -3, -1);

        NucleotideSequence a = new NucleotideSequence("ataaaaaaatgatcgacaaaaaaaatttttttt");
        NucleotideSequence b = new NucleotideSequence("agtcgttagcgacaaaaaaa");

        a = new NucleotideSequence("atgcggggatgc");
        b = new NucleotideSequence("atgctaatgcttttttttttt");

        MutationsBuilder<NucleotideSequence> mutations = new MutationsBuilder<>(NucleotideSequence.ALPHABET);
        BandedSemiLocalResult res = BandedAffineAligner.semiLocalRight(scoring, a, b, 0, a.size(), 0, b.size(), 2, mutations, new BandedAffineAligner.MatrixCache());

        //BandedAffineAligner.align0(scoring, a, b, 0, a.size(), 0, b.size(), 151, mutations, new BandedAffineAligner.MatrixCache());

        System.out.println(new Alignment<>(a, mutations.createAndDestroy(), new Range(0, res.sequence1Stop + 1), new Range(0, res.sequence2Stop + 1), 100));
        System.out.println(Aligner.alignGlobalAffine(scoring, a, b));
        //System.out.println(new Alignment<>(a, mutations.createAndDestroy(), new Range(0, a.size()), new Range(0, b.size()), 100));
    }

    @Test
    public void test23() throws Exception {
        AffineGapAlignmentScoring<NucleotideSequence> scoring = new AffineGapAlignmentScoring<>(
                NucleotideSequence.ALPHABET, 3, -1, -3, -1);

        NucleotideSequence a = new NucleotideSequence("ataaaaaaatgatcgacaaaaaaaatttttttt");
        NucleotideSequence b = new NucleotideSequence("agtcgttagcgacaaaaaaa");

        a = new NucleotideSequence("atgcggggatgc");
        b = new NucleotideSequence("atgcggggatgc");

        MutationsBuilder<NucleotideSequence> mutations = new MutationsBuilder<>(NucleotideSequence.ALPHABET);
        BandedSemiLocalResult res = BandedAffineAligner.semiLocalRight(scoring, a, b, 0, a.size(), 0, b.size(), 2, mutations, new BandedAffineAligner.MatrixCache());
        //BandedSemiLocalResult res = BandedLinearAligner.alignSemiLocalLeft0(
        //        new LinearGapAlignmentScoring<>(NucleotideSequence.ALPHABET, 5, -2, -4), a, b, 0, a.size(), 0,
        //        b.size(), 2, -1000, mutations, new CachedIntArray());

        //BandedAffineAligner.align0(scoring, a, b, 0, a.size(), 0, b.size(), 151, mutations, new BandedAffineAligner.MatrixCache());

        System.out.println(new Alignment<>(a, mutations.createAndDestroy(), new Range(0, res.sequence1Stop + 1), new Range(0, res.sequence2Stop + 1), 100));

        //System.out.println(Aligner.alignGlobalAffine(scoring, a, b));
        //System.out.println(new Alignment<>(a, mutations.createAndDestroy(), new Range(0, a.size()), new Range(0, b.size()), 100));
    }

    @Test
    public void test3() throws Exception {
        AffineGapAlignmentScoring<NucleotideSequence> scoring = new AffineGapAlignmentScoring<>(
                NucleotideSequence.ALPHABET, 3, -1, -3, -1);

        NucleotideSequence a = new NucleotideSequence("ataaaaaaatgatcgacaaaaaaaatttttttt");
        NucleotideSequence b = new NucleotideSequence("agtcgttagcgacaaaaaaa");

        a = new NucleotideSequence("cgtaggggcgta");
        b = new NucleotideSequence("tttttttttttcgtaatcgta");

        MutationsBuilder<NucleotideSequence> mutations = new MutationsBuilder<>(NucleotideSequence.ALPHABET);
        BandedSemiLocalResult res = BandedAffineAligner.semiLocalLeft(scoring, a, b, 0, a.size(), 0, b.size(), 2, mutations, new BandedAffineAligner.MatrixCache());

        //BandedAffineAligner.align0(scoring, a, b, 0, a.size(), 0, b.size(), 151, mutations, new BandedAffineAligner.MatrixCache());

        System.out.println(new Alignment<>(a, mutations.createAndDestroy(), new Range(res.sequence1Stop, a.size()), new Range(res.sequence2Stop, b.size()), 100));
        System.out.println(Aligner.alignGlobalAffine(scoring, a, b));
        //System.out.println(new Alignment<>(a, mutations.createAndDestroy(), new Range(0, a.size()), new Range(0, b.size()), 100));
    }

    @Test
    public void test4() throws Exception {
        AffineGapAlignmentScoring<NucleotideSequence> scoring = new AffineGapAlignmentScoring<>(
                NucleotideSequence.ALPHABET, 3, -1, -3, -1);

        NucleotideSequence a = new NucleotideSequence("ataaaaaaatgatcgacaaaaaaaatttttttt");
        NucleotideSequence b = new NucleotideSequence("agtcgttagcgacaaaaaaa");

        //a = new NucleotideSequence("atgcggggatgc");
        //b = new NucleotideSequence("atgctaatgcttttttttttt");
        a = new NucleotideSequence("atgcggggatgttttttt");
        b = new NucleotideSequence("atgcggggatag");


        MutationsBuilder<NucleotideSequence> mutations = new MutationsBuilder<>(NucleotideSequence.ALPHABET);
        BandedSemiLocalResult res = BandedAffineAligner.semiGlobalRight(scoring, a, b,
                0, a.size(), 2,
                0, b.size(), 2,
                2, mutations, new BandedAffineAligner.MatrixCache());

        //BandedAffineAligner.align0(scoring, a, b, 0, a.size(), 0, b.size(), 151, mutations, new BandedAffineAligner.MatrixCache());

        System.out.println(new Alignment<>(a, mutations.createAndDestroy(), new Range(0, res.sequence1Stop + 1), new Range(0, res.sequence2Stop + 1), 100));
        System.out.println(Aligner.alignGlobalAffine(scoring, a, b));
        //System.out.println(new Alignment<>(a, mutations.createAndDestroy(), new Range(0, a.size()), new Range(0, b.size()), 100));
    }
}
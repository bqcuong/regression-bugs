package com.milaboratory.core.alignment;

import com.milaboratory.core.sequence.NucleotideSequence;
import com.milaboratory.util.IntArrayList;
import junit.framework.Assert;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.junit.Test;

import static com.milaboratory.test.TestUtil.its;
import static com.milaboratory.test.TestUtil.randomSequence;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BandedAlignerTest extends AlignmentTest {

    @Test
    public void testGlobal() throws Exception {
        NucleotideSequence sequence1 = new NucleotideSequence("ATTAGACA");
        NucleotideSequence sequence2 = new NucleotideSequence("ATTGACA");
        int[] mut = BandedAligner.align(LinearGapAlignmentScoring.getNucleotideBLASTScoring(), sequence1, sequence2, 0);
        assertEquals(sequence2, mutate(sequence1, mut));
        //Mutations.printAlignment(sequence1, mut);

        sequence1 = new NucleotideSequence("ATTGACA");
        sequence2 = new NucleotideSequence("ATTAGACA");
        mut = BandedAligner.align(LinearGapAlignmentScoring.getNucleotideBLASTScoring(), sequence1, sequence2, 0);
        assertEquals(sequence2, mutate(sequence1, mut));
        //Mutations.printAlignment(sequence1, mut);

        sequence1 = new NucleotideSequence("ATTGACA");
        sequence2 = new NucleotideSequence("AGTAGCCA");
        mut = BandedAligner.align(LinearGapAlignmentScoring.getNucleotideBLASTScoring(), sequence1, sequence2, 0);
        assertEquals(sequence2, mutate(sequence1, mut));
        //Mutations.printAlignment(sequence1, mut);

        sequence1 = new NucleotideSequence("ATTGACAATTGACA");
        sequence2 = new NucleotideSequence("ATTGACATTGAA");
        mut = BandedAligner.align(LinearGapAlignmentScoring.getNucleotideBLASTScoring(), sequence1, sequence2, 0);
        assertEquals(sequence2, mutate(sequence1, mut));
        //Mutations.printAlignment(sequence1, mut);

        sequence1 = new NucleotideSequence("ATTGACAATTGACA");
        sequence2 = new NucleotideSequence("ATGAAATTGCACA");
        mut = BandedAligner.align(LinearGapAlignmentScoring.getNucleotideBLASTScoring(), sequence1, sequence2, 1);
        assertEquals(sequence2, mutate(sequence1, mut));
        //Mutations.printAlignment(sequence1, mut);
    }

    @Test
    public void testRandomGlobal() throws Exception {
        int its = its(100, 10000);
        RandomDataGenerator random = new RandomDataGenerator(new Well19937c());
        for (int i = 0; i < its; ++i) {
            NucleotideSequence seq1, seq2;
            seq1 = randomSequence(NucleotideSequence.ALPHABET, random, 10, 100);
            seq2 = randomSequence(NucleotideSequence.ALPHABET, random, 10, 100);
            int[] mut = BandedAligner.align(LinearGapAlignmentScoring.getNucleotideBLASTScoring(),
                    seq1, seq2,
                    random.nextInt(0, Math.min(seq1.size(), seq2.size()) - 1));
            assertEquals(seq2, mutate(seq1, mut));
        }
    }

    @Test
    public void test5() {
        NucleotideSequence seq1 = new NucleotideSequence("A"), seq2 = new NucleotideSequence("ATTA");
        int[] mutations = BandedAligner.align(LinearGapAlignmentScoring.getNucleotideBLASTScoring(), seq1, seq2, 1);
        Assert.assertEquals(seq2, mutate(seq1, mutations));
    }

    @Test
    public void testRandomLeft() throws Exception {
        int its = its(100, 10000);
        RandomDataGenerator random = new RandomDataGenerator(new Well19937c());
        for (int i = 0; i < its; ++i) {
            NucleotideSequence seq1, seq2;
            seq1 = randomSequence(NucleotideSequence.ALPHABET, random, 80, 84);
            seq2 = randomSequence(NucleotideSequence.ALPHABET, random, 80, 84);
            BandedSemiLocalResult r = BandedAligner.alignSemiLocalLeft(LinearGapAlignmentScoring.getNucleotideBLASTScoring(),
                    seq1, seq2,
                    random.nextInt(0, Math.min(seq1.size(), seq2.size()) - 1),
                    -10);
            int[] mut = r.mutations;
            assertEquals(seq2.getRange(0, r.sequence2Stop + 1), mutate(seq1.getRange(0, r.sequence1Stop + 1), mut));
        }
    }

    @Test
    public void testRandomRight() throws Exception {
        int its = its(100, 10000);
        RandomDataGenerator random = new RandomDataGenerator(new Well19937c());
        for (int i = 0; i < its; ++i) {
            NucleotideSequence seq1, seq2;
            seq1 = randomSequence(NucleotideSequence.ALPHABET, random, 80, 84);
            seq2 = randomSequence(NucleotideSequence.ALPHABET, random, 80, 84);
            BandedSemiLocalResult r = BandedAligner.alignSemiLocalRight(LinearGapAlignmentScoring.getNucleotideBLASTScoring(),
                    seq1, seq2,
                    random.nextInt(0, Math.min(seq1.size(), seq2.size()) - 1),
                    -10);
            int[] mut = move(r.mutations, -r.sequence1Stop);
            assertEquals(seq2.getRange(r.sequence2Stop, seq2.size()),
                    mutate(seq1.getRange(r.sequence1Stop, seq1.size()), mut));
        }
    }

    @Test
    public void testLocalLeft() throws Exception {
        NucleotideSequence seq1 = new NucleotideSequence("ATTAGACA");
        NucleotideSequence seq2 = new NucleotideSequence("ATTACGC");
        BandedSemiLocalResult r = BandedAligner.alignSemiLocalLeft(LinearGapAlignmentScoring.getNucleotideBLASTScoring(), seq1, seq2, 0, -10);
        assertEquals(seq2.getRange(0, r.sequence2Stop + 1), mutate(seq1.getRange(0, r.sequence1Stop + 1), r.mutations));
        assertEquals(3, r.sequence1Stop);
        assertEquals(3, r.sequence2Stop);

        seq1 = new NucleotideSequence("ATTAGACA");
        seq2 = new NucleotideSequence("ATTGACGC");
        r = BandedAligner.alignSemiLocalLeft(LinearGapAlignmentScoring.getNucleotideBLASTScoring(), seq1, seq2, 1, -10);
        //Mutations.printAlignment(seq1.getSubSequence(0, r.sequence1Stop + 1), r.mutations);
        assertEquals(seq2.getRange(0, r.sequence2Stop + 1), mutate(seq1.getRange(0, r.sequence1Stop + 1), r.mutations));
        assertEquals(6, r.sequence1Stop);
        assertEquals(5, r.sequence2Stop);

        seq1 = new NucleotideSequence("ATAGACAGGGAGACA");
        seq2 = new NucleotideSequence("ATTAGACATTAGACA");
        r = BandedAligner.alignSemiLocalLeft(LinearGapAlignmentScoring.getNucleotideBLASTScoring(), seq1, seq2, 1, -10);
        //Mutations.printAlignment(seq1.getSubSequence(0, r.sequence1Stop + 1), r.mutations);
        assertEquals(seq2.getRange(0, r.sequence2Stop + 1), mutate(seq1.getRange(0, r.sequence1Stop + 1), r.mutations));
        assertEquals(6, r.sequence1Stop);
        assertEquals(7, r.sequence2Stop);
    }

    @Test
    public void testLocalRight() throws Exception {
        NucleotideSequence seq1 = new NucleotideSequence("ATTAGACA");
        NucleotideSequence seq2 = new NucleotideSequence("GACA");
        BandedSemiLocalResult r = BandedAligner.alignSemiLocalRight(LinearGapAlignmentScoring.getNucleotideBLASTScoring(), seq1, seq2, 0, -10);
        //Mutations.printAlignment(seq1.getSubSequence(r.sequence1Stop, seq1.size()), r.mutations);
        assertEquals(seq2.getRange(r.sequence2Stop, seq2.size()),
                mutate(seq1.getRange(r.sequence1Stop, seq1.size()), move(r.mutations, -r.sequence1Stop)));
        assertEquals(4, r.sequence1Stop);
        assertEquals(0, r.sequence2Stop);

        seq1 = new NucleotideSequence("ATTAGACAATTAGACA");
        seq2 = new NucleotideSequence("GCGAATAGACA");
        r = BandedAligner.alignSemiLocalRight(LinearGapAlignmentScoring.getNucleotideBLASTScoring(), seq1, seq2, 0, -10);
        //Mutations.printAlignment(seq1.getSubSequence(r.sequence1Stop, seq1.size()), Mutations.move(r.mutations, -r.sequence1Stop));
        assertEquals(seq2.getRange(r.sequence2Stop, seq2.size()),
                mutate(seq1.getRange(r.sequence1Stop, seq1.size()), move(r.mutations, -r.sequence1Stop)));
        assertEquals(7, r.sequence1Stop);
        assertEquals(3, r.sequence2Stop);
    }

    @Test
    public void testAddedRight1() throws Exception {
        NucleotideSequence seq1 = new NucleotideSequence("ATTAGACA"),
                seq2 = new NucleotideSequence("ATTTAGACA");
        IntArrayList mutations = new IntArrayList();
        BandedSemiLocalResult la = BandedAligner.alignRightAdded(LinearGapAlignmentScoring.getNucleotideBLASTScoring(),
                seq1, seq2, 0, seq1.size(), 0, 0, seq2.size(), 0, 1, mutations);
        assertEquals(seq1.size() - 1, la.sequence1Stop);
        assertEquals(seq2.size() - 1, la.sequence2Stop);
        assertEquals(seq2.getRange(0, la.sequence2Stop + 1), mutate(seq1.getRange(0, la.sequence1Stop + 1), mutations.toArray()));
    }

    @Test
    public void testAddedRight2() throws Exception {
        NucleotideSequence seq1 = new NucleotideSequence("ATTAGACA"),
                seq2 = new NucleotideSequence("ATTTAGACAC");
        IntArrayList mutations = new IntArrayList();
        BandedSemiLocalResult la = BandedAligner.alignRightAdded(LinearGapAlignmentScoring.getNucleotideBLASTScoring(),
                seq1, seq2, 0, seq1.size(), 1, 0, seq2.size(), 1, 1, mutations);
        assertEquals(seq1.size() - 1, la.sequence1Stop);
        assertEquals(seq2.size() - 2, la.sequence2Stop);
        //printAlignment(seq1.getSubSequence(0, la.sequence1Stop + 1), mutations.toArray());
        assertEquals(seq2.getRange(0, la.sequence2Stop + 1), mutate(seq1.getRange(0, la.sequence1Stop + 1), mutations.toArray()));
    }

    @Test
    public void testAddedRight3() throws Exception {
        NucleotideSequence seq1 = new NucleotideSequence("ATTAGACA"),
                seq2 = new NucleotideSequence("ATTTAGACAC");
        IntArrayList mutations = new IntArrayList();
        BandedSemiLocalResult la = BandedAligner.alignRightAdded(LinearGapAlignmentScoring.getNucleotideBLASTScoring(),
                seq1, seq2, 0, seq1.size(), 1, 0, seq2.size(), 0, 1, mutations);
        assertEquals(seq1.size() - 1, la.sequence1Stop);
        assertEquals(seq2.size() - 1, la.sequence2Stop);
        //printAlignment(seq1.getSubSequence(0, la.sequence1Stop + 1), mutations.toArray());
        assertEquals(seq2.getRange(0, la.sequence2Stop + 1), mutate(seq1.getRange(0, la.sequence1Stop + 1), mutations.toArray()));
    }

    @Test
    public void testAddedRightRandom1() throws Exception {
        int its = its(1000, 100000);
        NucleotideSequence seq1, seq2;
        int offset1, offset2, length1, length2, added1, added2;
        BandedSemiLocalResult la;
        RandomDataGenerator random = new RandomDataGenerator(new Well19937c());
        for (int i = 0; i < its; ++i) {
            seq1 = randomSequence(NucleotideSequence.ALPHABET, random, 80, 84);
            seq2 = randomSequence(NucleotideSequence.ALPHABET, random, 80, 84);
            offset1 = random.nextInt(0, seq1.size() - 10);
            offset2 = random.nextInt(0, seq2.size() - 10);
            length1 = random.nextInt(1, seq1.size() - offset1);
            length2 = random.nextInt(1, seq2.size() - offset2);
            added1 = random.nextInt(0, length1);
            added2 = random.nextInt(0, length2);
            IntArrayList mutations = new IntArrayList();
            la = BandedAligner.alignRightAdded(LinearGapAlignmentScoring.getNucleotideBLASTScoring(),
                    seq1, seq2, offset1, length1, added1, offset2, length2, added2, 1, mutations);

            assertTrue(la.sequence1Stop == offset1 + length1 - 1 ||
                    la.sequence2Stop == offset2 + length2 - 1);

            assertTrue(la.sequence1Stop >= offset1 + length1 - 1 - added1);
            assertTrue(la.sequence2Stop >= offset2 + length2 - 1 - added2);

            int[] mut = mutations.toArray();
            mut = move(mut, -offset1);
            assertEquals(seq2.getRange(offset2, la.sequence2Stop + 1), mutate(seq1.getRange(offset1, la.sequence1Stop + 1), mut));
        }
    }

    @Test
    public void testAddedLeft1() throws Exception {
        NucleotideSequence seq1 = new NucleotideSequence("ATTAGACA"),
                seq2 = new NucleotideSequence("ATTTAGACA");
        IntArrayList mutations = new IntArrayList();
        BandedSemiLocalResult la = BandedAligner.alignLeftAdded(LinearGapAlignmentScoring.getNucleotideBLASTScoring(),
                seq1, seq2, 0, seq1.size(), 0, 0, seq2.size(), 0, 1, mutations);
        assertEquals(0, la.sequence1Stop);
        assertEquals(0, la.sequence2Stop);
        int[] mut = mutations.toArray();
        mut = move(mut, -la.sequence1Stop);
        assertEquals(seq2.getRange(la.sequence2Stop, seq2.size()), mutate(seq1.getRange(la.sequence1Stop, seq1.size()), mut));
    }

    @Test
    public void testAddedLeft2() throws Exception {
        NucleotideSequence seq1 = new NucleotideSequence("CTTAGACA"),
                seq2 = new NucleotideSequence("CATTTAGACA");
        IntArrayList mutations = new IntArrayList();
        BandedSemiLocalResult la = BandedAligner.alignLeftAdded(LinearGapAlignmentScoring.getNucleotideBLASTScoring(),
                seq1, seq2, 0, seq1.size(), 0, 0, seq2.size(), 1, 1, mutations);
        assertEquals(0, la.sequence1Stop);
        assertEquals(0, la.sequence2Stop);
        int[] mut = mutations.toArray();
        mut = move(mut, -la.sequence1Stop);
        //printAlignment(seq1.getSubSequence(la.sequence1Stop, seq1.size()), mut);
        assertEquals(seq2.getRange(la.sequence2Stop, seq2.size()), mutate(seq1.getRange(la.sequence1Stop, seq1.size()), mut));
    }

    @Test
    public void testAddedLeft3() throws Exception {
        NucleotideSequence seq1 = new NucleotideSequence("CTTAGACA"),
                seq2 = new NucleotideSequence("CATTTAGACA");
        IntArrayList mutations = new IntArrayList();
        BandedSemiLocalResult la = BandedAligner.alignLeftAdded(LinearGapAlignmentScoring.getNucleotideBLASTScoring(),
                seq1, seq2, 0, seq1.size(), 0, 0, seq2.size(), 3, 1, mutations);
        assertEquals(0, la.sequence1Stop);
        assertEquals(2, la.sequence2Stop);
        int[] mut = mutations.toArray();
        mut = move(mut, -la.sequence1Stop);
        //printAlignment(seq1.getSubSequence(la.sequence1Stop, seq1.size()), mut);
        assertEquals(seq2.getRange(la.sequence2Stop, seq2.size()), mutate(seq1.getRange(la.sequence1Stop, seq1.size()), mut));
    }

    @Test
    public void testAddedLeftRandom1() throws Exception {
        int its = its(1000, 100000);
        NucleotideSequence seq1, seq2;
        int offset1, offset2, length1, length2, added1, added2;
        BandedSemiLocalResult la;
        RandomDataGenerator random = new RandomDataGenerator(new Well19937c());
        for (int i = 0; i < its; ++i) {
            seq1 = randomSequence(NucleotideSequence.ALPHABET, random, 80, 84);
            seq2 = randomSequence(NucleotideSequence.ALPHABET, random, 80, 84);
            offset1 = random.nextInt(0, seq1.size() - 10);
            offset2 = random.nextInt(0, seq2.size() - 10);
            length1 = random.nextInt(1, seq1.size() - offset1);
            length2 = random.nextInt(1, seq2.size() - offset2);
            added1 = random.nextInt(0, length1);
            added2 = random.nextInt(0, length2);
            IntArrayList mutations = new IntArrayList();
            la = BandedAligner.alignLeftAdded(LinearGapAlignmentScoring.getNucleotideBLASTScoring(),
                    seq1, seq2, offset1, length1, added1, offset2, length2, added2, 1, mutations);

            assertTrue(la.sequence1Stop == offset1 ||
                    la.sequence2Stop == offset2);
            assertTrue(la.sequence1Stop <= offset1 + added1);
            assertTrue(la.sequence2Stop <= offset2 + added2);

            int[] mut = mutations.toArray();
            mut = move(mut, -la.sequence1Stop);
            assertEquals(seq2.getRange(la.sequence2Stop, offset2 + length2), mutate(seq1.getRange(la.sequence1Stop, offset1 + length1), mut));
        }
    }
}
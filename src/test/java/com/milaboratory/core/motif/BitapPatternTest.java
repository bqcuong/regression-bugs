package com.milaboratory.core.motif;

import com.milaboratory.core.sequence.NucleotideSequence;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BitapPatternTest {
    @Test
    public void testExact1() throws Exception {
        Motif<NucleotideSequence> motif = new Motif<>(NucleotideSequence.ALPHABET, "ATTAGACA");
        NucleotideSequence seq = new NucleotideSequence("ACTGCGATAAATTAGACAGTACGTA");
        assertEquals(10, motif.toBitapPattern().exactSearch(seq));
    }

    @Test
    public void testExact2() throws Exception {
        Motif<NucleotideSequence> motif = new Motif<>(NucleotideSequence.ALPHABET, "ATTRGACA");
        NucleotideSequence seq = new NucleotideSequence("ACTGCGATAAATTAGACAGTACGTA");
        assertEquals(10, motif.toBitapPattern().exactSearch(seq));
        seq = new NucleotideSequence("ACTGCGATAAATTGGACAGTACGTA");
        assertEquals(10, motif.toBitapPattern().exactSearch(seq));
    }

    @Test
    public void testMismatchIndel1() throws Exception {
        Motif<NucleotideSequence> motif = new Motif<>(NucleotideSequence.ALPHABET, "ATTAGACA");
        NucleotideSequence seq;
        BitapMatcher bitapMatcher;

        // Exact
        seq = new NucleotideSequence("ACTGCGATAAATTAGACAGTACGTA");
        bitapMatcher = motif.toBitapPattern().mismatchAndIndelMatcherLast(1, seq);
        boolean t = false;
        int pos;
        while ((pos = bitapMatcher.findNext()) > 0)
            if (bitapMatcher.getNumberOfErrors() == 0) {
                t = true;
                break;
            }
        assertTrue(t);
        assertEquals(17, pos);

        // Deletion
        seq = new NucleotideSequence("ACTGCGATAAATAGACAGTACGTA");
        bitapMatcher = motif.toBitapPattern().mismatchAndIndelMatcherLast(1, seq);
        assertEquals(16, bitapMatcher.findNext());
        assertEquals(1, bitapMatcher.getNumberOfErrors());
        assertEquals(-1, bitapMatcher.findNext());

        // Insertion
        seq = new NucleotideSequence("ACTGCGATAAATTATGACAGTACGTA");
        bitapMatcher = motif.toBitapPattern().mismatchAndIndelMatcherLast(1, seq);
        assertEquals(18, bitapMatcher.findNext());
        assertEquals(1, bitapMatcher.getNumberOfErrors());
        assertEquals(-1, bitapMatcher.findNext());

        // Mismatch
        seq = new NucleotideSequence("ACTGCGATAAATTACACAGTACGTA");
        bitapMatcher = motif.toBitapPattern().mismatchAndIndelMatcherLast(1, seq);
        assertEquals(17, bitapMatcher.findNext());
        assertEquals(1, bitapMatcher.getNumberOfErrors());
        assertEquals(-1, bitapMatcher.findNext());
    }

    @Test
    public void testMismatchIndel2() throws Exception {
        Motif<NucleotideSequence> motif = new Motif<>(NucleotideSequence.ALPHABET, "ATTAGACA");
        NucleotideSequence seq;
        BitapMatcher bitapMatcher;

        // Exact
        seq = new NucleotideSequence("ACTGCGATAAATTAGACAGTACGTA");
        bitapMatcher = motif.toBitapPattern().mismatchAndIndelMatcherFirst(1, seq);
        boolean t = false;
        int pos;
        while ((pos = bitapMatcher.findNext()) > 0)
            if (bitapMatcher.getNumberOfErrors() == 0) {
                t = true;
                break;
            }
        assertTrue(t);
        assertEquals(10, pos);

        // Deletion
        seq = new NucleotideSequence("ACTGCGATAAATAGACAGTACGTA");
        bitapMatcher = motif.toBitapPattern().mismatchAndIndelMatcherFirst(1, seq);
        assertEquals(10, bitapMatcher.findNext());
        assertEquals(1, bitapMatcher.getNumberOfErrors());
        assertEquals(9, bitapMatcher.findNext());
        assertEquals(1, bitapMatcher.getNumberOfErrors());
        assertEquals(-1, bitapMatcher.findNext());

        // Insertion
        seq = new NucleotideSequence("ACTGCGATAAATTATGACAGTACGTA");
        bitapMatcher = motif.toBitapPattern().mismatchAndIndelMatcherFirst(1, seq);
        assertEquals(10, bitapMatcher.findNext());
        assertEquals(1, bitapMatcher.getNumberOfErrors());
        assertEquals(-1, bitapMatcher.findNext());

        // Mismatch
        seq = new NucleotideSequence("ACTGCGATAAATTACACAGTACGTA");
        bitapMatcher = motif.toBitapPattern().mismatchAndIndelMatcherFirst(1, seq);
        assertEquals(10, bitapMatcher.findNext());
        assertEquals(1, bitapMatcher.getNumberOfErrors());
        assertEquals(-1, bitapMatcher.findNext());
    }

    @Test
    public void testMismatch1() throws Exception {
        Motif<NucleotideSequence> motif = new Motif<>(NucleotideSequence.ALPHABET,
                "ATTRGACA");
        NucleotideSequence seq = new NucleotideSequence("ACTGCGATAAATTAGACAGTACGTA");
        BitapMatcher matcher = motif.toBitapPattern().mismatchOnlyMatcherFirst(1, seq);
        Assert.assertEquals(10, matcher.findNext());
        Assert.assertEquals(0, matcher.getNumberOfErrors());
    }

    @Test
    public void testMismatch2() throws Exception {
        Motif<NucleotideSequence> motif = new Motif<>(NucleotideSequence.ALPHABET,
                "ATTRGACA");
        NucleotideSequence seq = new NucleotideSequence("ACTGCGATAAATCAGACAGTACGTA");
        BitapMatcher matcher = motif.toBitapPattern().mismatchOnlyMatcherFirst(1, seq);
        Assert.assertEquals(10, matcher.findNext());
        Assert.assertEquals(1, matcher.getNumberOfErrors());
    }
}
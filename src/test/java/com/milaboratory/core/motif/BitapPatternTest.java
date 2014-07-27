package com.milaboratory.core.motif;

import com.milaboratory.core.sequence.NucleotideSequence;
import org.junit.Assert;
import org.junit.Test;

public class BitapPatternTest {
    @Test
    public void testExact1() throws Exception {
        Motif<NucleotideSequence> motif = new Motif<>(NucleotideSequence.ALPHABET, "ATTAGACA");
        NucleotideSequence seq = new NucleotideSequence("ACTGCGATAAATTAGACAGTACGTA");
        Assert.assertEquals(10, motif.toBitapPattern().exactSearch(seq));
    }

    @Test
    public void testExact2() throws Exception {
        Motif<NucleotideSequence> motif = new Motif<>(NucleotideSequence.ALPHABET, "ATTRGACA");
        NucleotideSequence seq = new NucleotideSequence("ACTGCGATAAATTAGACAGTACGTA");
        Assert.assertEquals(10, motif.toBitapPattern().exactSearch(seq));
        seq = new NucleotideSequence("ACTGCGATAAATTGGACAGTACGTA");
        Assert.assertEquals(10, motif.toBitapPattern().exactSearch(seq));
    }

    @Test
    public void testMismatch1() throws Exception {
        Motif<NucleotideSequence> motif = new Motif<>(NucleotideSequence.ALPHABET,
                "ATTRGACA");
        NucleotideSequence seq = new NucleotideSequence("ACTGCGATAAATTAGACAGTACGTA");
        BitapMatcherImpl matcher = motif.toBitapPattern().mismatchOnlyMatcher(1, seq, 0, seq.size());
        Assert.assertEquals(10, matcher.findNext());
        Assert.assertEquals(0, matcher.getNumberOfErrors());
    }

    @Test
    public void testMismatch2() throws Exception {
        Motif<NucleotideSequence> motif = new Motif<>(NucleotideSequence.ALPHABET,
                "ATTRGACA");
        NucleotideSequence seq = new NucleotideSequence("ACTGCGATAAATCAGACAGTACGTA");
        BitapMatcherImpl matcher = motif.toBitapPattern().mismatchOnlyMatcher(1, seq, 0, seq.size());
        Assert.assertEquals(10, matcher.findNext());
        Assert.assertEquals(1, matcher.getNumberOfErrors());
    }
}
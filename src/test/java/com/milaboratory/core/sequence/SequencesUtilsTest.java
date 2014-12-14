package com.milaboratory.core.sequence;

import org.junit.Test;

import java.util.Set;

import static com.milaboratory.core.sequence.SequencesUtils.*;
import static org.junit.Assert.*;

public class SequencesUtilsTest {
    @Test
    public void testCat() throws Exception {
        String seq1s = "gttacagc",
                seq2s = "gctatacgatgc";
        NucleotideSequence seq1 = new NucleotideSequence(seq1s),
                seq2 = new NucleotideSequence(seq2s),
                catAssert = new NucleotideSequence(seq1s + seq2s);

        assertEquals(catAssert, concatenate(seq1, seq2));
        assertEquals(catAssert.hashCode(), concatenate(seq1, seq2).hashCode()); //Just in case
    }

    @Test
    public void testBelongs1() throws Exception {
        assertTrue(belongsToAlphabet(NucleotideSequence.ALPHABET, "ATTAGagacca"));
        assertFalse(belongsToAlphabet(NucleotideSequence.ALPHABET, "ATTAGagaccaw"));
    }

    @Test
    public void testBelongs2() throws Exception {
        assertTrue(belongsToAlphabet(AminoAcidSequence.ALPHABET, "CAsSL*~GAT"));
        assertTrue(belongsToAlphabet(AminoAcidSequence.ALPHABET, "CAsSL*_GAT"));
        assertFalse(belongsToAlphabet(AminoAcidSequence.ALPHABET, "CAsSL*_XAT"));
    }

    @Test
    public void testSet1() throws Exception {
        Set<Alphabet<?>> set = possibleAlphabets("ATTAGagacca");
        assertTrue(set.contains(NucleotideSequence.ALPHABET));
    }

    @Test
    public void testSet2() throws Exception {
        Set<Alphabet<?>> set = possibleAlphabets("CAsSL*_GAT");
        assertTrue(set.contains(AminoAcidSequence.ALPHABET));
        assertFalse(set.contains(NucleotideSequence.ALPHABET));
    }
}

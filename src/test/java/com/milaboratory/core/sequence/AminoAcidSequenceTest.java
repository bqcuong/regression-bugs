package com.milaboratory.core.sequence;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public class AminoAcidSequenceTest {
    @Test
    public void test1() throws Exception {
        Assert.assertEquals(new AminoAcidSequence("IR_"),
                AminoAcidSequence.translate(true, true, new NucleotideSequence("ATTAGACA")));
        Assert.assertEquals(new AminoAcidSequence("_*T"),
                AminoAcidSequence.translate(false, true, new NucleotideSequence("ATTAGACA")));

        Assert.assertEquals(new AminoAcidSequence("IR"),
                AminoAcidSequence.translate(true, true, new NucleotideSequence("ATTAGA")));
        Assert.assertEquals(new AminoAcidSequence("IR"),
                AminoAcidSequence.translate(false, true, new NucleotideSequence("ATTAGA")));
    }

    @Test
    public void test2() throws Exception {
        Assert.assertEquals(new AminoAcidSequence("IR"),
                AminoAcidSequence.translate(true, false, new NucleotideSequence("ATTAGACA")));
        Assert.assertEquals(new AminoAcidSequence("*T"),
                AminoAcidSequence.translate(false, false, new NucleotideSequence("ATTAGACA")));

        Assert.assertEquals(new AminoAcidSequence("IR"),
                AminoAcidSequence.translate(true, false, new NucleotideSequence("ATTAGA")));
        Assert.assertEquals(new AminoAcidSequence("IR"),
                AminoAcidSequence.translate(false, false, new NucleotideSequence("ATTAGA")));
    }
}
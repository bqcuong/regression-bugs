package com.milaboratory.core.sequence;

import com.milaboratory.primitivio.PrimitivI;
import com.milaboratory.primitivio.PrimitivO;
import com.milaboratory.test.TestUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class NucleotideSequenceSerializerTest {
    @Test
    public void test1() throws Exception {
        NucleotideSequence[] seqs = new NucleotideSequence[100];
        for (int i = 0; i < seqs.length; i++)
            seqs[i] = TestUtil.randomSequence(NucleotideSequence.ALPHABET, 100, 200);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrimitivO po = new PrimitivO(bos);
        for (int i = 0; i < seqs.length; i++)
            po.writeObject(seqs[i]);

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        PrimitivI pi = new PrimitivI(bis);

        for (int i = 0; i < seqs.length; i++)
            Assert.assertEquals(seqs[i], pi.readObject(NucleotideSequence.class));
    }
}
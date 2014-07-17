package com.milaboratory.core.sequence;

import com.milaboratory.primitivio.PrimitivI;
import com.milaboratory.primitivio.PrimitivO;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class AlphabetSerializerTest {
    @Test
    public void test1() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrimitivO po = new PrimitivO(bos);
        int cc = 10;
        for (int i = 0; i < cc; i++)
            po.writeObject(NucleotideSequence.ALPHABET);

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        PrimitivI pi = new PrimitivI(bis);

        for (int i = 0; i < cc; i++) {
            Alphabet actual = pi.readObject(Alphabet.class);
            Assert.assertEquals(NucleotideSequence.ALPHABET, actual);
        }
    }
}
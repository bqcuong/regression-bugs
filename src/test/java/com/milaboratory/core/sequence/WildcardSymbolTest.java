package com.milaboratory.core.sequence;

import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public class WildcardSymbolTest {
    @Test
    public void testRandomSymbol() throws Exception {
        byte[] codes;
        WildcardSymbol symbol;
        int[] unif;
        int tries = 2000;
        double precision = 0.15, expected;
        for (int count = 2; count < 10; ++count) {
            codes = new byte[count];
            for (int i = 1; i < count; ++i)
                codes[i] = (byte) i;
            symbol = new WildcardSymbol('a', codes);
            unif = new int[count];
            for (int i = 0; i < tries; ++i)
                unif[symbol.getUniformlyDistributedSymbol(i)]++;
            expected = (1. * tries) / count;
            for (int actual : unif)
                Assert.assertTrue(actual < (1. + precision) * expected && actual > (1. - precision) * expected);
        }
    }
}
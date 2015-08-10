package com.milaboratory.core.alignment.blast;

import org.junit.Assume;
import org.junit.Before;

public class BlastTest {
    @Before
    public void setUp() throws Exception {
        Assume.assumeTrue(Blast.isBlastAvailable());
    }
}

package com.milaboratory.core.alignment;

import com.milaboratory.util.GlobalObjectMappers;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class KAlignerParametersTest extends AlignmentTest {
    private static final KAlignerParameters gParams = new KAlignerParameters(5, false, false,
            1.5f, 0.75f, 1.0f, -0.1f, -0.3f, 4, 10, 15, 2, -10,
            40.0f, 0.87f, 7,
            LinearGapAlignmentScoring.getNucleotideBLASTScoring());

    @Test
    public void test1() throws Exception {
        Assert.assertTrue(gParams.equals(gParams.clone()));
        check(gParams);
        for (String key : KAlignerParameters.getAvailableNames())
            check(KAlignerParameters.getByName(key));
    }

    private void check(KAlignerParameters params) throws IOException {
        String seialized = GlobalObjectMappers.PRETTY.writeValueAsString(params);
        KAlignerParameters deserialized = GlobalObjectMappers.PRETTY.readValue(seialized, KAlignerParameters.class);
        Assert.assertTrue(deserialized.equals(params));
    }
}

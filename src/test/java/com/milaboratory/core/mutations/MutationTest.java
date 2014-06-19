package com.milaboratory.core.mutations;

import com.milaboratory.core.sequence.*;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well44497a;
import org.junit.Assert;
import org.junit.Test;

import static com.milaboratory.core.mutations.Mutation.*;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public class MutationTest {

    @Test
    public void test1() throws Exception {
        int code = Mutation.createSubstitution(49, 3, 1);
        Assert.assertEquals(3, getFrom(code));
        Assert.assertEquals(1, getTo(code));
        Assert.assertEquals(RAW_MUTATION_TYPE_SUBSTITUTION,
                getRawTypeCode(code));
    }

}
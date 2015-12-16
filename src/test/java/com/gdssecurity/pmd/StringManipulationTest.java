package com.gdssecurity.pmd;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class StringManipulationTest {
	@Test
	@Ignore
	public void testStringManipulation () throws Exception {
		Assert.assertEquals(2, PMDRunner.run("src/test/java/resources/others/StringManipulationExample.java", PMDRunner.RULESET_SQL_INJECTION));
	}
}

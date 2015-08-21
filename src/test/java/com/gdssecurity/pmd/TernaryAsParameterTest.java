package com.gdssecurity.pmd;

import org.junit.Assert;
import org.junit.Test;

public class TernaryAsParameterTest {

	
	@Test
	public void testTernary () throws Exception {
		Assert.assertEquals(0, PMDRunner.run("src/test/java/resources/others/TernaryAsParameter.java", PMDRunner.RULESET_XSS));
	}
}

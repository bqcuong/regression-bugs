package com.gdssecurity.pmd;

import org.junit.Assert;
import org.junit.Test;

public class UsingThisTest {

	@Test
	public void testTernary () throws Exception {
		Assert.assertEquals(1, PMDRunner.run("src/test/java/resources/others/UsingThis.java", PMDRunner.RULESET_SQL_INJECTION));
	}
}

package com.gdssecurity.pmd;

import org.junit.Assert;
import org.junit.Test;

public class CWE113HttpResponseSplittingTest {
	@Test
	public void test1() throws Exception {
		Assert.assertEquals(1, PMDRunner.run("src/test/java/resources/cwe113responsesplitting/redirect", PMDRunner.RULESET_UNVALIDATED_REDIRECTS));
	}
}

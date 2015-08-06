package com.gdssecurity.pmd;

import org.junit.Assert;
import org.junit.Test;

public class CWE328ReversibleHashTest {
	@Test
	public void test1() throws Exception {
		Assert.assertEquals(1, PMDRunner.run("src/test/java/resources/cwe328reversiblehash", PMDRunner.RULESET_VULNERABLE));
	}
}

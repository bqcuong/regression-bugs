package com.gdssecurity.pmd;

import org.junit.Assert;
import org.junit.Test;

// cwe-116
// cwe-931
public class CWE931XssTest {

	public CWE931XssTest() {
		super();
	}

	@Test
	public void test1() throws Exception {
		Assert.assertEquals(1, PMDRunner.run("src/test/java/resources/cwe931xss/xss01", PMDRunner.RULESET_XSS));
	}

	@Test
	public void test2() throws Exception {
		Assert.assertEquals(2, PMDRunner.run("src/test/java/resources/cwe931xss/xss02jsp", PMDRunner.RULESET_XSS));
	}

}

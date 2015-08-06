package com.gdssecurity.pmd;

import org.junit.Assert;
import org.junit.Test;

public class CWE89SqlInjectionTest {

	public CWE89SqlInjectionTest() {
		super();
	}

	@Test
	public void test1() throws Exception {
		Assert.assertEquals(1, PMDRunner.run("src/test/java/resources/cwe89sqlinjection/TestSqliServlet.java",
				PMDRunner.RULESET_SQL_INJECTION));
	}

}

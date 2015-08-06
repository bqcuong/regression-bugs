package com.gdssecurity.pmd;

import org.junit.Assert;
import org.junit.Test;

public class CWE328ReversibleHashTest {

	public CWE328ReversibleHashTest() {
		super();
	}

	@Test
	public void test1() throws Exception {
		Assert.assertEquals(1,
				PMDRunner.run("src/test/java/resources/cwe328reversiblehash/CWE328_Reversible_One_Way_Hash__basic_01.java", PMDRunner.RULESET_VULNERABLE));
	}
}

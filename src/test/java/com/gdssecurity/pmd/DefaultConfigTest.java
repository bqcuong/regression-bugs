package com.gdssecurity.pmd;

import org.junit.Assert;
import org.junit.Test;

public class DefaultConfigTest {
	
	public DefaultConfigTest () {
		super();
	}
	
	// Execute default rules over all tests files to ensure no misconfiguration occurs
	@Test
	public void defaultConfig() throws Exception {
		Assert.assertTrue(0 < PMDRunner.run("src/test/java/resources", PMDRunner.RULESET_DEFAULT) );
	}

}

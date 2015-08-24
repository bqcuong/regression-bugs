package com.gdssecurity.pmd;

import org.junit.Assert;
import org.junit.Test;

public class AnnotationTest {

	private static final String RULESET_ANNOTATIONS = "rulesets/xss-annotations.xml";
	
	@Test
	public void testSinkIsOk () throws Exception {
		Assert.assertEquals(0, PMDRunner.run("src/test/java/resources/others/AnnotationExample.java", RULESET_ANNOTATIONS));	
	}
	@Test
	public void testCallingSinkIsNotOk () throws Exception {
		Assert.assertEquals(1, PMDRunner.run("src/test/java/resources/others/AnnotationExample2.java", RULESET_ANNOTATIONS));	
	}
}

package com.gdssecurity.pmd;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.cli.PMDCommandLineInterface;
import net.sourceforge.pmd.cli.PMDParameters;

public final class PMDRunner {

	public static final String RULESET_DEFAULT = "rulesets/GDS/SecureCoding.xml";
	public static final String RULESET_SQL_INJECTION = "rulesets/GDS/OWASP/2013-A1-Injection.xml";
	public static final String RULESET_XSS = "rulesets/GDS/OWASP/2013-A3-Cross-Site-Scripting.xml";
	public static final String RULESET_ACCESS="rulesets/GDS/OWASP/2013-A7-Missing-Function-Level-Access-Control.xml";
	public static final String RULESET_VULNERABLE = "rulesets/GDS/OWASP/2013-A9-Using-Known-Vulnerable-Component.xml";
	public static final String RULESET_UNVALIDATED_REDIRECTS = "rulesets/GDS/OWASP/2013-A10-Unvalidated-Redirects-and-Forwards.xml";

	private PMDRunner() {
		throw new AssertionError("No instances allowed");
	}

	public static int run(String directory, String ruleset) throws Exception {
		int violations = run(new String[] { "-d", directory, "-R", ruleset, "-f", "text", "-language", "java" });
		return violations;

	}

	public static int run(String directory) throws Exception {
		return run(directory, RULESET_DEFAULT);
	}

	public static int run(String[] args) throws Exception {
		final PMDParameters params = PMDCommandLineInterface.extractParameters(new PMDParameters(), args, "pmd");
		final PMDConfiguration configuration = PMDParameters.transformParametersIntoConfiguration(params);

		try {
			int violations = PMD.doPMD(configuration);
			return violations;
		} catch (Exception e) {
			throw e;
		}
	}
}

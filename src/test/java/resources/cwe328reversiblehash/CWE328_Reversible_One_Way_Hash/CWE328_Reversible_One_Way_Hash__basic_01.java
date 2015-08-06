package resources.cwe328reversiblehash.CWE328_Reversible_One_Way_Hash;

import java.security.MessageDigest;

public class CWE328_Reversible_One_Way_Hash__basic_01 {

	public CWE328_Reversible_One_Way_Hash__basic_01() {
		super();
	}

	public void bad() throws Throwable {

		String input = "Test Input";

		/* FLAW: Insecure cryptographic hashing algorithm (MD5) */
		MessageDigest hash = MessageDigest.getInstance("MD5");
		byte[] hashv = hash.digest(input.getBytes()); /*
													 * INCIDENTAL FLAW:
													 * Hard-coded input to hash
													 * algorithm
													 */

		System.out.println(new String(hashv));

	}

	public void good() throws Throwable {
		good1();
	}

	private void good1() throws Throwable {

		String input = "Test Input";

		/* FIX: Secure cryptographic hashing algorithm (SHA-512) */
		MessageDigest hash = MessageDigest.getInstance("SHA-512");
		byte[] hashv = hash.digest(input.getBytes()); /*
													 * INCIDENTAL FLAW:
													 * Hard-coded input to hash
													 * algorithm
													 */

		System.out.println(new String(hashv));

	}
}

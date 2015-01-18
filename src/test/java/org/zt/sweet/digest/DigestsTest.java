/**
 * 
 */
package org.zt.sweet.digest;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ternence
 * @create 2015年1月18日
 */
public class DigestsTest {

	/**
	 * Test method for {@link org.zt.sweet.digest.Digests#md5Digest(byte[])}.
	 */
	@Test
	public void testMd5Digest() {
		Assert.assertTrue(Digests.md5("abc".getBytes()) != null);
		Assert.assertTrue(Digests.md5("abc") != null);
	}

	/**
	 * Test method for
	 * {@link org.zt.sweet.digest.Digests#md5DigestAsHex(byte[])}.
	 */
	@Test
	public void testMd5DigestAsHex() {
		String hexString = Digests.fixedHexString(Digests.md5("abc"));
		Assert.assertEquals("900150983cd24fb0d6963f7d28e17f72", hexString);
		hexString = Digests.md5AsHex("abc");
		Assert.assertEquals("900150983cd24fb0d6963f7d28e17f72", hexString);
	}

	/**
	 * Test method for
	 * {@link org.zt.sweet.digest.Digests#appendMd5DigestAsHex(byte[], java.lang.StringBuilder)}
	 * .
	 */
	@Test
	public void testAppendMd5DigestAsHex() {
		StringBuilder hexString = Digests.appendMd5DigestAsHex(
				"abc".getBytes(), new StringBuilder("0123-"));
		Assert.assertEquals("0123-900150983cd24fb0d6963f7d28e17f72",
				hexString.toString());
	}

}

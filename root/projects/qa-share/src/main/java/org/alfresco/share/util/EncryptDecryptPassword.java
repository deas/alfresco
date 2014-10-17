package org.alfresco.share.util;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;

/**
 * Methods that encrypt and decrypt password for security reasons
 * 
 * @author Oana.Caciuc
 */

public class EncryptDecryptPassword {

	private static Random rand = new Random((new Date()).getTime());

	/**
	 * 
	 * @param password
	 * @return encrypted password
	 */

	public static String encrypt(String str) {

		BASE64Encoder encoder = new BASE64Encoder();

		byte[] salt = new byte[8];

		rand.nextBytes(salt);

		return encoder.encode(salt) + encoder.encode(str.getBytes());
	}

	/**
	 * 
	 * @param encrypted
	 *            password
	 * @return decrypted password
	 */

	public static String decrypt(String encstr) {

		if (encstr.length() > 12) {

			String cipher = encstr.substring(12);

			BASE64Decoder decoder = new BASE64Decoder();

			try {

				return new String(decoder.decodeBuffer(cipher));

			} catch (IOException e) {

				// throw new InvalidImplementationException(

				// Fail

			}

		}

		return null;
	}
}

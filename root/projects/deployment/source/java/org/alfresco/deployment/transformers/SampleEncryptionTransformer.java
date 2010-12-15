/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.deployment.transformers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.alfresco.deployment.DeploymentTransportInputFilter;
import org.alfresco.deployment.DeploymentTransportOutputFilter;
import org.alfresco.deployment.impl.DeploymentException;


/**
 * Sample payload transformer for the File System Receiver (FSR).  Encrypts the 
 * contents of the file being transmitted over the wire to a File System Receiver.
 * 
 * The intention of this class is to be simple sample code rather than being highly secure.
 * In particular this class uses password based encryption via a symetric key rather than 
 * a more secure (and complex) key strategy.
 * 
 * The password will be held in plain text in the spring configuration files.   Nevertheless this 
 * class will give some protection against network snooping.
 * 
 * Enabling TripleDES requires "unlimited strength policy files" for that algorithm to be available 
 * in a Sun JVM.
 * 
 * @see javax.crypto
 * 
 * @author mrogers
 *
 */
public class SampleEncryptionTransformer implements DeploymentTransportInputFilter, DeploymentTransportOutputFilter {
	
	/**
	 * The name of the cipher to use for encrypting the data.
	 * 
	 * PBEWithMD5AndDES is the default since it is guaranteed to be available.
	 * 
	 * PBEWithMD5AndTripleDES is a more secure choice but requires changes to the 
	 * java security policy files to make that algorithm available. 
	 */
	private String cipherName = "PBEWithMD5AndDES";
	
	// 8-byte Salt
	private byte[] salt = 
	{
		(byte)0xA2, (byte)0x6B, (byte)0x12, (byte)0x66,
		(byte)0x74, (byte)0x53, (byte)0x31, (byte)0x99
	};
	
	// Iteration count
	private int iterationCount = 19;
	
	/**
	 * The password used to generate the secret key
	 * change value via spring configuration
	 */
	private String password = "Alfresco";
	
	
	/**
	 * Gets a secret key from the password.
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 */
	private SecretKey getSecretKey() throws InvalidKeySpecException, NoSuchAlgorithmException
	{
		KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, getIterationCount());
		SecretKey key = SecretKeyFactory.getInstance(cipherName).generateSecret(keySpec);
		return key;
	}
	
	/**
	 *  Encrypted outgoing stream
	 *  @param out the stream to encrypt
	 *  @param path the path of the file to encrypt 
	 */
	public OutputStream addFilter(OutputStream out, String path, String mimeType, String encoding) 
	{	
		Cipher ecipher;
		
		try 
		{
			SecretKey key = getSecretKey();
			ecipher = Cipher.getInstance(key.getAlgorithm());
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, getIterationCount());
			ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			CipherOutputStream cos = new CipherOutputStream(out, ecipher);
			return new EncryptedOutputStream(cos, password);
		} 
		catch (Exception e) {
			throw new DeploymentException("Unable to initialise encryption cipherName:" + cipherName, e);
		}
	}
	
	/**
	 * Decrypts inbound stream,
	 *  @param out the stream to decrypt
	 *  @param path the path of the file to decrypt 
	 */
	public InputStream addFilter(InputStream in, String path, String mimeType, String encoding) 
	{
		Cipher dcipher;
		
		try 
		{
			SecretKey key = getSecretKey();
			dcipher = Cipher.getInstance(key.getAlgorithm());
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, getIterationCount());
			dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
			CipherInputStream cis = new CipherInputStream(in, dcipher);
			return new EncryptedInputStream(cis, password);
		} 
		catch (Exception e) {
			throw new DeploymentException("Unable to initialise decryption cipherName:" + cipherName, e);
		}
	}	
	
	/**
	 * The name of the cipher to use such as "PBEWithMD5AndDES".
	 * 
	 * @param cipherName the name of the cipher to use
	 */
	public void setCipherName(String cipherName) {
		this.cipherName = cipherName;
	}
	
	public String getCipherName() {
		return cipherName;
	}

	/**
	 * Sets the password
	 * @param password the password used to generate a key
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setIterationCount(int iterationCount) {
		this.iterationCount = iterationCount;
	}

	public int getIterationCount() {
		return iterationCount;
	}
	/**
	 * Private class to inject information into the outgoing stream so the receiver
	 * can compare what is uncompressed and work out if decryption has failed.
	 */
	private class EncryptedOutputStream extends FilterOutputStream 
	{
		private boolean firstSend = true;
		private String password; 
		
		public EncryptedOutputStream(OutputStream out, String password) {
			super(out);
			this.password = password;
		}
		
		@Override public void write(byte[] b) throws IOException
		{
		   injectPassword();
		   out.write(b);	
		}
		
		@Override public void write(byte[] b, int off, int len) throws IOException
		{
			injectPassword();
			out.write(b, off, len);
		}
		
		@Override public void write(int b) throws java.io.IOException
		{
			injectPassword();
			out.write(b);
		}
		
		private void injectPassword() throws java.io.IOException
		{
			if(firstSend) 
			{
				firstSend=false;
				out.write(password.getBytes("UTF-8"));
			}
		}
	}
	
	private class EncryptedInputStream extends FilterInputStream 
	{
		private String password;
		private boolean firstRx = true;
		
		protected EncryptedInputStream(InputStream in, String password) {
			super(in);
			this.password = password;
		}
		
		 @Override public int read(byte[] b, int off, int len) throws IOException
		 {
			 extractPassword(); 
			 return in.read(b, off, len);
		 }
		 
		 @Override public int read(byte[] b) throws java.io.IOException
		 {
			 extractPassword(); 
			 return in.read(b); 
		 }
		 
		 @Override public int read() throws IOException
		 {
			 extractPassword(); 
			 return in.read();
		 }

		 /**
		  * Utility to extract the decrypted password from the beginning of the stream 
		  * @throws IOException
		  */
		 private void extractPassword() throws IOException
		 {
			 if(firstRx)
			 {
				 firstRx = false;
				 byte expectedPassword[] = this.password.getBytes("UTF-8");
				 byte gotPassword[] = new byte[expectedPassword.length];
				 in.read(gotPassword);
				 
				 if(!Arrays.equals(expectedPassword, gotPassword))
				 {
					throw new IOException("Password error"); 
				 }
			 }
		 }
	}	
 }

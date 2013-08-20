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

import junit.framework.TestCase;
import java.io.*;
import java.util.Random;

//import org.alfresco.deployment.DeploymentTransportTransformer.Direction;

/**
 * Tests of the CompressionTransformer
 *  
 * @author mrogers
 * @see org.alfresco.deployment.transformers.CompressionTransformer
 */
public class CompressionTransformerTest extends TestCase {
	
    private Random randomNumber = new Random( System.currentTimeMillis() );

	/**
	 * @param name
	 */
	public CompressionTransformerTest(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.alfresco.deployment.transformers.CompressionTransformer#addFilter(java.io.OutputStream, org.alfresco.deployment.DeploymentTransportTransformer.Direction, java.lang.String)}.
	 * This test compresses a message with one transformation.   Then sends the results through another instance to give us plain text again. 
	 */
	public void testAddFilter() {
		CompressionTransformer transformer = new CompressionTransformer();
		String path = "wibble";
		
		ByteArrayOutputStream compressed = new ByteArrayOutputStream();
		
		// A sender should compress the stream
		OutputStream out = null;
		//out = (OutputStream)transformer.addFilter(compressed, Direction.SENDER, path);
		out = (OutputStream)transformer.addFilter(compressed, path, null, null);
		
		assertNotNull("null output stream returned", compressed);
		
		String clearText="hello world, <head>sample</head>";
		try {
			out.write(clearText.getBytes());
		} catch (IOException ie){
			fail("unexpected exception thrown" + ie.toString());
			
		}
		
		try{
		    out.flush();
		    out.close();
		} catch (IOException ie){
			fail("unexpected exception thrown, " + ie.toString());	
		}
		
		assert(compressed.size() > 0);
		
		// Now set up another instance to decompress the message
		InputStream decompress = null;
		
		ByteArrayInputStream compressedStream = new ByteArrayInputStream(compressed.toByteArray());
		
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		
		decompress = (InputStream)transformer.addFilter(compressedStream, "wibble", null, null);
		
		try {
			byte[] readBuffer = new byte[1002];
			while(true)
			{
				int readLen = decompress.read(readBuffer);
				if(readLen > 0) 
				{
					result.write(readBuffer,0, readLen);
				}
				else
				{
					break;
				}
			}

		} catch (IOException ie){
			fail("unexpected exception thrown, " + ie.toString());	
		}
			
		// now uncompress should equal clearText
		assertTrue(result.toString().equalsIgnoreCase(clearText));
	}
	

	
	/**
	 * End to end test.   This test passes a big message through compression and decompression to check buffering.
	 * compression is rather good at reducing the size of the message so this test needs to generate some random clear text.
  	 */
	public void testCompressDecompressBigMessage() {
		String sampleData = "Ring-a-ring a roses, a pocket full of posies, atishoo, atishoo, we all fall down. ";
		CompressionTransformer transformer = new CompressionTransformer();
		String path = "wibble";

	   	
		ByteArrayOutputStream compressed = new ByteArrayOutputStream();
		
		// A sender should compress the stream
		OutputStream out = (OutputStream)transformer.addFilter(compressed, path, null, null);	
		assertNotNull("null output stream returned", out);
		
		StringBuffer clearText= new StringBuffer();
		for(int i = 0; i < 1000; i++) {
			String random = randomString();
			clearText.append(sampleData);
			clearText.append(random);
		}
		
		try {
			out.write(clearText.toString().getBytes());
		} catch (IOException ie){
			fail("unexpected exception thrown, " + ie.toString());		
		}
		
		try{
			out.flush();
			out.close();
		} catch (IOException ie){
			fail("unexpected exception thrown, " + ie.toString());	
		}
	
		// Now set up another instance to decompress the message
		InputStream decompress = null;
		
		ByteArrayInputStream compressedStream = new ByteArrayInputStream(compressed.toByteArray());
		
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		
		decompress = (InputStream)transformer.addFilter(compressedStream, "wibble", null, null);
		
		try {
			byte[] readBuffer = new byte[1002];
			while(true)
			{
				int readLen = decompress.read(readBuffer);
				if(readLen > 0) 
				{
					result.write(readBuffer,0, readLen);
				}
				else
				{
					break;
				}
			}

		} catch (IOException ie){
			fail("unexpected exception thrown, " + ie.toString());	
		}		
		
		// now resultStr should equal clearText
		String resultStr = result.toString();
		
		assertTrue(resultStr.length() == clearText.length());
		
		assertTrue(resultStr.toString().equalsIgnoreCase(clearText.toString()));
	}
	
	
	/**
	 * End to end test.   
	 * This test tests buffering by ensuring that many sends are processed correctly.
	 */
	public void testCompressDecompressManySends() {
		String sampleData = "Ring-a-ring a roses, a pocket full of posies, atishoo, atishoo, we all fall down.";
		CompressionTransformer transformer = new CompressionTransformer();
		String path = "wibble";
		
		int numberOfSends = 137;
		
		// A sender should compress the stream
		ByteArrayOutputStream compressed = new ByteArrayOutputStream();		
		OutputStream out = (OutputStream)transformer.addFilter(compressed, path, null, null);
		
		assertNotNull("null output stream returned", out);
		
		StringBuffer clearText = new StringBuffer();
		for(int i = 0; i < numberOfSends; i++) 
		{
			String randomText = randomString() + sampleData;
			clearText.append(randomText);
			try 
			{
				out.write(randomText.toString().getBytes());
			} 
			catch (IOException ie)
			{
				fail("unexpected exception thrown, " + ie.toString());	
			}
		}
		
		try 
		{
			out.flush();
			out.close();
		} 
		catch (IOException ie)
		{
			fail("unexpected exception thrown, " + ie.toString());	
		}
		
		// Now set up another instance to decompress the message

		ByteArrayInputStream compressedStream = new ByteArrayInputStream(compressed.toByteArray());
		
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		
		InputStream decompress = (InputStream)transformer.addFilter(compressedStream, "wibble", null, null);
		
		try {
			byte[] readBuffer = new byte[509];
			while(true)
			{
				int readLen = decompress.read(readBuffer);
				if(readLen > 0) 
				{
					result.write(readBuffer,0, readLen);
				}
				else
				{
					break;
				}
			}

		} catch (IOException ie){
			fail("unexpected exception thrown, " + ie.toString());	
		}		
		
		// now resultStr should equal clearText
		String resultStr = result.toString();
		
		assertTrue(clearText.length() > 0);
		
		assertTrue(resultStr.length() == clearText.length());
		
		assertTrue(resultStr.toString().equalsIgnoreCase(clearText.toString()));
	}
	
	private String randomString() 
	{	
	    StringBuffer clearText = new StringBuffer();

		// need to add some random data - otherwise compression simply compresses the big buffer into a small one.
		for (int i = 0; i < 100; i++) 
		{
			char c = (char)randomNumber.nextInt(100);
			if(Character.isLetterOrDigit(c))
			{
			     clearText.append(c);
			}
		}		
		return clearText.toString();
	}
}

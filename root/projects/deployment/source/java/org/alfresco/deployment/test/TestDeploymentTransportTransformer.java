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
package org.alfresco.deployment.test;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FilterOutputStream;

import org.alfresco.deployment.DeploymentTransportInputFilter;
import org.alfresco.deployment.DeploymentTransportOutputFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This test deployment transformer simply returns the data it was given
 * 
 * @author mrogers
 *
 */
public class TestDeploymentTransportTransformer implements DeploymentTransportInputFilter, DeploymentTransportOutputFilter  {
	
    private static Log fgLogger = LogFactory.getLog(TestDeploymentTransportTransformer.class);

    /**
     * Outgoing filter
     */
	public OutputStream addFilter(OutputStream out, String path, String encoding, String mimeType) {
		return new TestOutFilter(out);
	}

	/**
	 * In-bound filter
	 */
	public InputStream addFilter(InputStream in, String path, String encoding, String mimeType) {

		return new TestInFilter(in);
	}
	
	/**
	 * Test filter class to intercept data 
	 */
	private class TestOutFilter extends FilterOutputStream 
	{
		public TestOutFilter(OutputStream out) {
			super(out);		
		}
		
	    /* (non-Javadoc)
	     * @see java.io.OutputStream#write(byte[], int, int)
	     */
	    @Override
	    public void write(byte[] b, int off, int len) throws IOException
	    {
	    	fgLogger.debug("wrote bytes len:" + len); 	
	        out.write(b, off, len);
	    }
	}

	private class TestInFilter extends FilterInputStream 
	{
		public TestInFilter(InputStream in) {
			super(in);		
		}
	    
		/* (non-Javadoc)
	     * @see java.io.OutputStream#read(byte[], int, int)
	     */
	    @Override
		public int read(byte[] b, int off, int len) throws java.io.IOException
		{
			 return in.read(b, off, len);
		}
	}
}


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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.alfresco.deployment.DeploymentTransportInputFilter;
import org.alfresco.deployment.DeploymentTransportOutputFilter;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Sample payload transformer for remote communication with the File System Receiver (FSR) 
 * 
 * Compresses the outgoing stream using ZLIB
 * 
 * Uncompresses the incoming stream using ZLIB
 * 
 * @author mrogers
 */
public class CompressionTransformer implements DeploymentTransportInputFilter, DeploymentTransportOutputFilter {
	
    @SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(CompressionTransformer.class);
    
    // This is the outgoing - compression transformation
	public OutputStream addFilter(OutputStream out, String path, String mimeType, String encoding) {
		return new DeflaterOutputStream(out);
	}

    // This is the incomming  - de-compression transformation
	public InputStream addFilter(InputStream in, String path, String mimeType, String encoding) {
		return new InflaterInputStream(in);
	}
}

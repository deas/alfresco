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


package org.alfresco.deployment;

import java.io.InputStream;

/**
 * This interface is used for payload transformation of messages received by a file 
 * system receiver.
 * 
 * The transformers are called just before or just after content is sent over the network 
 * to an FSR, but in all cases before the deployment is committed.
 * 
 * Implementors will typically create a java.io.FilterOutputStream to wrap the given stream.
 * 
 * @see java.io.FilterInputStream
 * @see org.alfresco.deployment.transformers.ZipCompressionTransformer
 * @see org.alfresco.deployment.DeploymentTransportOutputFilter
 * 
 * @author mrogers
 *
 */
public interface DeploymentTransportInputFilter 
{
	/**
	 * Add a filter to transform the payload of a deployment.
	 * 
	 * The inputStream is received on the File System Receiver
	 * 
	 * If this transformation is not required then simply return <i>in</i>. Do not return null.
	 * 
	 * @param in the input stream being filtered.
	 * @param path the path of the file
	 * @param encoding the encoding of the file
	 * @param mimeType the mimeType of the file
	 * @return the filtered input stream
	 */
	public InputStream addFilter(InputStream in, String path, String encoding, String mimeType ); 
}

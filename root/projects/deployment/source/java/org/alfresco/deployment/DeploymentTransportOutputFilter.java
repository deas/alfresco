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

import java.io.OutputStream;

/**
 * This interface is used for payload transformation of messages to a file 
 * system receiver.
 * 
 * The transformers are called just before or just after content is sent over the network 
 * to an FSR, but in all cases before the deployment is committed.
 * 
 * Implementors will typically create a java.io.FilterOutputStream to wrap the given stream.
 * 
 * @see java.io.FilterInputStream
 * @see org.alfresco.deployment.transformers.ZipCompressionTransformer
 * @see org.alfresco.deployment.DeploymentTransportInputFilter
 * 
 * @author mrogers
 *
 */
public interface DeploymentTransportOutputFilter 
{
	
	/**
	 * Add a filter to transform the payload of a deployment.
	 * 
	 * The outputStream is the outgoing payload from WCM to the FSR.
	 * 
	 * If this transformation is not required then simply return <i>out</i>. Do not return null.
	 * 
	 * @param out the output stream being filtered.
	 * @param encoding the encoding of the file
	 * @param mimeType the mimeType of the file
	 * @param path the path of the file
	 * 
	 * @return the filtered output stream
	 */
	public OutputStream addFilter(OutputStream out, String path, String encoding, String mimeType); 
	
}

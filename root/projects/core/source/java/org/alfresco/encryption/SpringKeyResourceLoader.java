/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
package org.alfresco.encryption;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.util.ResourceUtils;

/**
 * Loads key resources (key store and key store passwords) from the Spring classpath.
 * 
 * @since 4.0
 *
 */
public class SpringKeyResourceLoader implements KeyResourceLoader
{
    /**
     * {@inheritDoc}
     */
    @Override
	public InputStream getKeyStore(String keyStoreLocation)
	{
    	try
    	{
    		File f = ResourceUtils.getFile(keyStoreLocation);
    		return new BufferedInputStream(new FileInputStream(f));
    	}
    	catch(FileNotFoundException e)
    	{
    		return null;
    	}
	}

    /**
     * {@inheritDoc}
     */
    @Override
	public Properties loadKeyMetaData(String keyMetaDataFileLocation) throws IOException
	{
    	try
    	{
	    	Properties p = new Properties();
	    	p.load(new BufferedInputStream(new FileInputStream(ResourceUtils.getFile(keyMetaDataFileLocation))));
	    	return p;
    	}
    	catch(FileNotFoundException e)
    	{
    		return null;
    	}
	}

}

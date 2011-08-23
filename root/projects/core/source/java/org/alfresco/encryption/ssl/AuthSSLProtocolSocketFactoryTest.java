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
package org.alfresco.encryption.ssl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.alfresco.encryption.KeyResourceLoader;
import org.alfresco.encryption.KeyStoreParameters;

public class AuthSSLProtocolSocketFactoryTest
{
	public void execute()
	{
		try
		{
			ClasspathKeyResourceLoader keyResourceLoader = new ClasspathKeyResourceLoader();
			KeyStoreParameters keyStoreParameters = new KeyStoreParameters("JCEKS", null,
					"config/alfresco/ssl-keystore-passwords.properties", "config/alfresco/ssl.keystore");
			KeyStoreParameters trustStoreParameters = new KeyStoreParameters("JCEKS", null,
					"config/alfresco/ssl-truststore-passwords.properties", "config/alfresco/ssl.truststore");
			SSLEncryptionParameters parameters = new SSLEncryptionParameters(keyStoreParameters, trustStoreParameters);
			AuthSSLProtocolSocketFactory socketFactory = new AuthSSLProtocolSocketFactory(parameters, keyResourceLoader);
			socketFactory.createSocket("localhost", 8443);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		new AuthSSLProtocolSocketFactoryTest().execute();
	}
	
	private class ClasspathKeyResourceLoader implements KeyResourceLoader
    {
		@Override
    	public InputStream getKeyStore(String location)
    	throws FileNotFoundException
    	{
    		return getClass().getClassLoader().getResourceAsStream(location);
    	}

		@Override
		public Properties loadKeyMetaData(String location) throws IOException
		{
			Properties p = new Properties();
			p.load(getClass().getClassLoader().getResourceAsStream(location));
			return p;
		}
    }
}

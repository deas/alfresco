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

import java.security.Key;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * Provides the system-wide secret key for symmetric database encryption from a key store
 * in the filesystem.
 * 
 * @author Derek Hulley
 * @since 4.0
 */
public class KeystoreKeyProvider extends AbstractKeyProvider
{
    private static final Log logger = LogFactory.getLog(KeystoreKeyProvider.class);

    private KeyStoreParameters keyStoreParameters;
    //private AlfrescoKeyStoreFactory keyStoreFactory;
    private KeyResourceLoader keyResourceLoader;
    private AlfrescoKeyStore keyStore;

    /**
     * Constructs the provider with required defaults
     */
    public KeystoreKeyProvider()
    {
    }

	/**
     * For testing.
     * 
     * @param encryptionParameters
     * @param keyResourceLoader
     */
    public KeystoreKeyProvider(KeyStoreParameters keyStoreParameters, KeyResourceLoader keyResourceLoader)
    {
    	this();
    	this.setKeyStoreParameters(keyStoreParameters);
    	this.keyResourceLoader = keyResourceLoader;
        init();
    }

	public void setKeyStoreParameters(KeyStoreParameters setKeyStoreParameters)
	{
		this.keyStoreParameters = setKeyStoreParameters;
	}

    public void setKeyResourceLoader(KeyResourceLoader keyResourceLoader)
	{
		this.keyResourceLoader = keyResourceLoader;
	}

    // TODO move locking from CachingKeyStore to here
    public void init()
    {
        this.keyStore = new CachingKeyStore(keyStoreParameters, keyResourceLoader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public Key getKey(String keyAlias)
    {
    	return keyStore.getKey(keyAlias);
    }
}

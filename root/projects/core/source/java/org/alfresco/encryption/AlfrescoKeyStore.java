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

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Set;

import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;

/**
 * Manages a Java Keystore for Alfresco, including caching keys where appropriate.
 * 
 * @since 4.0
 *
 */
public interface AlfrescoKeyStore
{
    public static final String KEY_KEYSTORE_PASSWORD = "keystore.password";

    public String getName();
    
    /**
     * The underlying keystore location
     * 
     * @return
     */
	public String getLocation();
	
	/**
	 * The key store parameters.
	 * 
	 * @return
	 */
	public KeyStoreParameters getkeyStoreParameters();
	
	/**
	 * Does the underlying key store exist?
	 * 
	 * @return true if it exists, false otherwise
	 */
    public boolean exists();
    
    /**
     * Return the key with the given key alias.
     * 
     * @param keyAlias
     * @return
     */
    public Key getKey(String keyAlias);
    
    /**
     * Return all key aliases in the key store.
     * 
     * @return
     */
    public Set<String> getKeyAliases();
    
    /**
     * Create an array of key managers from keys in the key store.
     * 
     * @return
     */
	public KeyManager[] createKeyManagers();
	
	/**
	 * Create an array of trust managers from certificates in the key store.
	 * 
	 * @return
	 */
	public TrustManager[] createTrustManagers();
	
	/**
	 * Create the key store if it doesn't exist.
	 */
	public void create();
	
	/**
	 * Reload the keys from the key store.
	 */
    public int reload();
    
	public void importPrivateKey(String keyAlias, String keyPassword, InputStream keyFile, InputStream certFile)
	throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, CertificateException, KeyStoreException;
}

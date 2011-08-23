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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

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

	public String getLocation();
	public KeyResourceLoader getKeyResourceLoader();
	public KeyStoreParameters getkeyStoreParameters();
    public boolean exists();
    public Key getKey(String keyAlias);
	public KeyManager[] createKeyManagers() throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, FileNotFoundException, IOException;
	public TrustManager[] createTrustManagers() throws KeyStoreException, NoSuchAlgorithmException;
}

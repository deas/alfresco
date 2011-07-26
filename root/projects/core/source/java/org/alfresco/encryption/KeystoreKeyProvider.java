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
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.util.PropertyCheck;
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
    public static final String KEY_KEYSTORE_PASSWORD = "keystore";
    
    private static final Log logger = LogFactory.getLog(KeyProvider.class);

    private String passwordsFileLocation;

    // Will be cleared after initialization
    private Map<String, String> passwords;
    private String location;
    private String provider;
    private String type;
    private Map<String, Key> keys;
    
    private KeyResourceLoader keyResourceLoader;

    private final ReadLock readLock;
    private final WriteLock writeLock;
    
    /**
     * Constructs the provider with required defaults
     */
    public KeystoreKeyProvider()
    {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
        keys = new HashMap<String, Key>(7);
    }

    /**
     * Convenience constructor for tests.  Note that {@link #init()} is also called.
     */
    /* package */ KeystoreKeyProvider(String location, KeyResourceLoader keyResourceLoader, String provider, String type, Map<String, String> passwords)
    {
        this();
        setLocation(location);
        setProvider(provider);
        setType(type);
        setPasswords(passwords);
        setKeyResourceLoader(keyResourceLoader);
        init();
    }

    public void setPasswordsFileLocation(String passwordsFileLocation)
    {
    	this.passwordsFileLocation = passwordsFileLocation;
    }

    public void setKeyResourceLoader(KeyResourceLoader keyResourceLoader)
	{
		this.keyResourceLoader = keyResourceLoader;
	}

	public void setLocation(String location)
    {
        this.location = location;
    }

    public void setProvider(String provider)
    {
        this.provider = provider;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * Set the map of passwords to access the keystore.
     * <p/>
     * Where required, <tt>null</tt> values must be inserted into the map to indicate the presence
     * of a key that is not protected by a password.  They entry for {@link #KEY_KEYSTORE_PASSWORD}
     * is required if the keystore is password protected.
     * 
     * @param passwords             a map of passwords including <tt>null</tt> values
     */
    private void setPasswords(Map<String, String> passwords)
    {
        this.passwords = new HashMap<String, String>(passwords);
    }

    public void init()
    {
        writeLock.lock();
        try
        {
            safeInit();
        }
        finally
        {
            writeLock.unlock();
        }
    }
    
    private void loadPasswords() throws IOException
    {
    	Properties passwords = keyResourceLoader.getPasswords(passwordsFileLocation);
        this.passwords = new HashMap<String, String>(passwords.size());
    	for(String key : passwords.stringPropertyNames())
    	{
        	this.passwords.put(key, passwords.getProperty(key));
    	}
    }

    /**
     * Initializes class; must be done in a write lock.
     */
    private void safeInit()
    {
        KeyStore ks = null;
        InputStream is = null;
        String pwdKeyStore = null;

        if (!PropertyCheck.isValidPropertyString(location))
        {
            location = null;
        }
        if (!PropertyCheck.isValidPropertyString(provider))
        {
            provider = null;
        }
        if (!PropertyCheck.isValidPropertyString(type))
        {
            type = null;
        }

        try
        {
	        if(passwordsFileLocation != null)
	        {
	        	loadPasswords();
	        }

	        PropertyCheck.mandatory(this, "location", location);
	        // Extract the keystore password
	        pwdKeyStore = passwords.get(KEY_KEYSTORE_PASSWORD);
	
	        // Make sure we choose the default type, if required
	        if (type == null)
	        {
	            type = KeyStore.getDefaultType();
	        }

            if (provider == null)
            {
                ks = KeyStore.getInstance(type);
            }
            else
            {
                ks = KeyStore.getInstance(type, provider);
            }
            // Load it up
            is = keyResourceLoader.getKeyStore(location);
            if(is == null)
            {
                throw new IOException("Unable to find keystore file: " + location);
            }
            ks.load(is, pwdKeyStore == null ? null : pwdKeyStore.toCharArray());
            // Loaded
            if (logger.isDebugEnabled())
            {
                logger.debug(
                        "Initialize keystore provider: \n" +
                        "   Location: " + location + "\n" +
                        "   Provider: " + provider + "\n" +
                        "   Type:     " + type);
            }
        }
        catch (Throwable e)
        {
            throw new AlfrescoRuntimeException(
                    "Failed to initialize keystore provider: \n" +
                    "   Location: " + location + "\n" +
                    "   Provider: " + provider + "\n" +
                    "   Type:     " + type,
                    e);
        }
        finally
        {
            pwdKeyStore = null;
            if(passwords != null)
            {
            	passwords.remove(KEY_KEYSTORE_PASSWORD);
            }
            if (is != null)
            {
                try { is.close(); } catch (Throwable e) {}
            }
        }
        
        // Now get the other keys
        for (Map.Entry<String, String> element : passwords.entrySet())
        {
            String keyAlias = element.getKey();
            String passwordStr = element.getValue();
            if (!PropertyCheck.isValidPropertyString(passwordStr))
            {
                // Force a failure because the property was not properly initialized
                PropertyCheck.mandatory(this, "passwords." + keyAlias, null);
            }
            // Null is an acceptable value (means no key)
            Key key = null;
            // Attempt to key the key
            try
            {
                key = ks.getKey(keyAlias, passwordStr == null ? null : passwordStr.toCharArray());
                keys.put(keyAlias, key);
                // Key loaded
                if (logger.isDebugEnabled())
                {
                    logger.debug(
                            "Retrieved key from keystore: \n" +
                            "   Location: " + location + "\n" +
                            "   Provider: " + provider + "\n" +
                            "   Type:     " + type + "\n" +
                            "   Alias:    " + keyAlias + "\n" +
                            "   Password?: " + (passwordStr != null));
                }
            }
            catch (Throwable e)
            {
                throw new AlfrescoRuntimeException(
                        "Failed to retrieve key from keystore: \n" +
                        "   Location: " + location + "\n" +
                        "   Provider: " + provider + "\n" +
                        "   Type:     " + type + "\n" +
                        "   Alias:    " + keyAlias + "\n" +
                        "   Password?: " + (passwordStr != null),
                        e);
            }
        }
        // Clear passwords
        passwords.clear();
    }

    @Override
    public Key getKey(String keyAlias)
    {
        readLock.lock();
        try
        {
            return keys.get(keyAlias);
        }
        finally
        {
            readLock.unlock();
        }
    }
}

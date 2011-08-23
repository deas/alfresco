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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This wraps a Java Keystore and caches the encryption keys. It manages the loading and caching of the encryption keys.
 * 
 * @since 4.0
 *
 */
public class CachingKeyStore implements AlfrescoKeyStore
{
    private static final Log logger = LogFactory.getLog(CachingKeyStore.class);
    
    protected KeyStoreParameters keyStoreParameters;    
    protected KeyResourceLoader keyResourceLoader;

    protected KeyStore ks = null;
    protected Map<String, Key> keys;

    private final ReadLock readLock;
    private final WriteLock writeLock;

    public CachingKeyStore(KeyStoreParameters keyStoreParameters, KeyResourceLoader keyResourceLoader)
    {
	    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	    readLock = lock.readLock();
	    writeLock = lock.writeLock();

        keys = new HashMap<String, Key>(7);
        this.keyResourceLoader = keyResourceLoader;
        this.keyStoreParameters = keyStoreParameters;

        init();
    }

	public KeyStoreParameters getkeyStoreParameters()
	{
		return keyStoreParameters;
	}

	public KeyResourceLoader getKeyResourceLoader()
	{
		return keyResourceLoader;
	}

	public String getLocation()
	{
		return keyStoreParameters.getLocation();
	}
	
	public String getProvider()
	{
		return keyStoreParameters.getProvider();
	}
	
	public String getType()
	{
		return keyStoreParameters.getType();
	}
	
	public String getKeyMetaDataFileLocation()
	{
		return keyStoreParameters.getKeyMetaDataFileLocation();
	}
	
//	public void setProvider(String provider)
//	{
//		this.provider = provider;
//	}
	
//	public void setType(String type)
//	{
//		this.type = type;
//	}
//
//	public void setKeyMetaDataFileLocation(String keyMetaDataFileLocation)
//	{
//		this.keyMetaDataFileLocation = keyMetaDataFileLocation;
//	}

	/**
     * Set the map of passwords to access the keystore.
     * <p/>
     * Where required, <tt>null</tt> values must be inserted into the map to indicate the presence
     * of a key that is not protected by a password.  They entry for {@link #KEY_KEYSTORE_PASSWORD}
     * is required if the keystore is password protected.
     * 
     * @param passwords             a map of passwords including <tt>null</tt> values
     */
//    private void setKeyInformation(Map<String, String> passwords)
//    {
//        this.keyInfos = new HashMap<String, String>(passwords);
//    }

	protected InputStream getKeyStoreStream() throws FileNotFoundException
	{
		return keyResourceLoader.getKeyStore(getLocation());
//		File f = ResourceUtils.getFile(location);
//		return new BufferedInputStream(new FileInputStream(f));
	}

//	protected Properties loadKeyInfo() throws IOException, FileNotFoundException
//	{
//    	Properties p = new Properties();
//    	p.load(new BufferedInputStream(new FileInputStream(ResourceUtils.getFile(passwordsFileLocation))));
//    	return p;
//	}
    
//    public Map<String, KeyInformation> getKeyInformation() throws IOException
//    {
//    	Properties keyInfoProps = loadKeyInfo();
//        Map<String, KeyInformation> keyInfo = new HashMap<String, KeyInformation>(keyAliases.size());
//
//        String keyStorePassword = keyInfoProps.getProperty(KEY_KEYSTORE_PASSWORD + ".password");
//        keyInfo.put(KEY_KEYSTORE_PASSWORD, new KeyInformation(KEY_KEYSTORE_PASSWORD, keyAlgorithm, null, keyStorePassword));
//
//        for(String keyAlias : keyAliases)
//        {
//    		String keyPassword = keyInfoProps.getProperty(keyAlias + ".password");
//    		String keySeed = keyInfoProps.getProperty(keyAlias + ".seed");
//	        keyInfo.put(keyAlias, new SecretKeyInformation(keyAlias, keyAlgorithm, keySeed.getBytes("UTF-8"), keyPassword));
//    	}
//
//    	return keyInfo;
//    }
    
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

    protected KeyInfoManager getKeyInfoManager() throws FileNotFoundException, IOException
    {
    	return new KeyInfoManager();
    }

    /**
     * Initializes class
     */
    private void safeInit()
    {
        InputStream is = null;
        String pwdKeyStore = null;
        KeyInfoManager keyInfoManager = null;

        PropertyCheck.mandatory(this, "location", getLocation());

        if (!PropertyCheck.isValidPropertyString(getLocation()))
        {
            keyStoreParameters.setLocation(null);
        }
        if (!PropertyCheck.isValidPropertyString(getProvider()))
        {
        	keyStoreParameters.setProvider(null);
        }
        if (!PropertyCheck.isValidPropertyString(getType()))
        {
        	keyStoreParameters.setType(null);
        }
        if (!PropertyCheck.isValidPropertyString(getKeyMetaDataFileLocation()))
        {
        	keyStoreParameters.setKeyMetaDataFileLocation(null);
        }

        try
        {
            keyInfoManager = getKeyInfoManager();

	        //PropertyCheck.mandatory(this, "location", location);
	        // Extract the keystore password
	        pwdKeyStore = keyInfoManager.getKeyStorePassword();
	        if(pwdKeyStore == null)
	        {
	        	throw new AlfrescoRuntimeException("Keystore password must not be null");
	        }

	        // Make sure we choose the default type, if required
	        if(getType() == null)
	        {
	            keyStoreParameters.setType(KeyStore.getDefaultType());
	        }

            if(getProvider() == null)
            {
                ks = KeyStore.getInstance(getType());
            }
            else
            {
                ks = KeyStore.getInstance(getType(), getProvider());
            }
            // Load it up
            is = getKeyStoreStream();
            if(is == null)
            {
            	if(logger.isDebugEnabled())
            	{
            		logger.debug("Unable to find keystore file: " + getLocation());
            	}
            	// this is ok, the keystore will contain no keys.
            	return;
            }

            ks.load(is, pwdKeyStore == null ? null : pwdKeyStore.toCharArray());

    		if(logger.isDebugEnabled())
    		{
    			Enumeration<String> aliases = ks.aliases();
    			while(aliases.hasMoreElements())
    			{
    				String alias = (String)aliases.nextElement();                        
    				Certificate[] certs = ks.getCertificateChain(alias);
    				if(certs != null)
    				{
    					logger.debug("Certificate chain '" + alias + "':");
    					for(int c = 0; c < certs.length; c++)
    					{
    						if(certs[c] instanceof X509Certificate)
    						{
    							X509Certificate cert = (X509Certificate)certs[c];
    							logger.debug(" Certificate " + (c + 1) + ":");
    							logger.debug("  Subject DN: " + cert.getSubjectDN());
    							logger.debug("  Signature Algorithm: " + cert.getSigAlgName());
    							logger.debug("  Valid from: " + cert.getNotBefore() );
    							logger.debug("  Valid until: " + cert.getNotAfter());
    							logger.debug("  Issuer: " + cert.getIssuerDN());
    						}
    					}
    				}
    			}
    			
                logger.debug(
                        "Initialized keystore: \n" +
                        "   Location: " + getLocation() + "\n" +
                        "   Provider: " + getProvider() + "\n" +
                        "   Type:     " + getType() + "\n" +
                        keys.size() + " keys found");
    		}

            // Loaded
        }
        catch (Throwable e)
        {
            throw new AlfrescoRuntimeException(
                    "Failed to initialize keystore: \n" +
                    "   Location: " + getLocation() + "\n" +
                    "   Provider: " + getProvider() + "\n" +
                    "   Type:     " + getType(),
                    e);
        }
        finally
        {
            pwdKeyStore = null;
            if(keyInfoManager != null)
            {
            	keyInfoManager.clearKeyStorePassword();
            }

            if (is != null)
            {
                try { is.close(); } catch (Throwable e) {}
            }
        }
		
        try
        {
        	// load and cache the keys
        	Enumeration<String> keyAliases = ks.aliases();
	        while(keyAliases.hasMoreElements())
	        {
	        	String keyAlias = keyAliases.nextElement();
	        	KeyInformation keyInfo = keyInfoManager.getKeyInformation(keyAlias);
	            String passwordStr = keyInfo.getPassword();
	
	            if (!PropertyCheck.isValidPropertyString(passwordStr))
	            {
	                // Force a failure because the property was not properly initialized
	                PropertyCheck.mandatory(this, "passwords." + keyAlias, null);
	            }
	            // Null is an acceptable value (means no key)
	            Key key = null;

	            // Attempt to get the key
	            try
	            {
	                key = ks.getKey(keyAlias, passwordStr == null ? null : passwordStr.toCharArray());
	                keys.put(keyAlias, key);
	                // Key loaded
	                if (logger.isDebugEnabled())
	                {
	                    logger.debug(
	                            "Retrieved key from keystore: \n" +
	                            "   Location: " + getLocation() + "\n" +
	                            "   Provider: " + getProvider() + "\n" +
	                            "   Type:     " + getType() + "\n" +
	                            "   Alias:    " + keyAlias + "\n" +
	                            "   Password?: " + (passwordStr != null));
	                }
	            }
	            catch (Throwable e)
	            {
	                throw new AlfrescoRuntimeException(
	                        "Failed to retrieve key from keystore: \n" +
	                        "   Location: " + getLocation() + "\n" +
	                        "   Provider: " + getProvider() + "\n" +
	                        "   Type:     " + getType() + "\n" +
	                        "   Alias:    " + keyAlias + "\n" +
	                        "   Password?: " + (passwordStr != null),
	                        e);
	            }
	        }
        }
        catch(Throwable e)
        {
            throw new AlfrescoRuntimeException(
                    "Failed to retrieve keys from keystore: \n" +
                    "   Location: " + getLocation() + "\n" +
                    "   Provider: " + getProvider() + "\n" +
                    "   Type:     " + getType() + "\n",
                    e);
        }
        finally
        {
	        // Clear key information
	        keyInfoManager.clear();
        }

        // Now get the other keys
//        for (Map.Entry<String, KeyInformation> element : keyInfo.entrySet())
//        {
//            String keyAlias = element.getKey();
//            String passwordStr = element.getValue().getPassword();
//
//            if (!PropertyCheck.isValidPropertyString(passwordStr))
//            {
//                // Force a failure because the property was not properly initialized
//                PropertyCheck.mandatory(this, "passwords." + keyAlias, null);
//            }
//            // Null is an acceptable value (means no key)
//            Key key = null;
//            // Attempt to key the key
//            try
//            {
//                key = ks.getKey(keyAlias, passwordStr == null ? null : passwordStr.toCharArray());
//                keys.put(keyAlias, key);
//                // Key loaded
//                if (logger.isDebugEnabled())
//                {
//                    logger.debug(
//                            "Retrieved key from keystore: \n" +
//                            "   Location: " + location + "\n" +
//                            "   Provider: " + provider + "\n" +
//                            "   Type:     " + type + "\n" +
//                            "   Alias:    " + keyAlias + "\n" +
//                            "   Password?: " + (passwordStr != null));
//                }
//            }
//            catch (Throwable e)
//            {
//                throw new AlfrescoRuntimeException(
//                        "Failed to retrieve key from keystore: \n" +
//                        "   Location: " + location + "\n" +
//                        "   Provider: " + provider + "\n" +
//                        "   Type:     " + type + "\n" +
//                        "   Alias:    " + keyAlias + "\n" +
//                        "   Password?: " + (passwordStr != null),
//                        e);
//            }
//        }
        // Clear key information
//        keyInfo.clear();
    }
    
    public boolean exists()
    {
    	try
    	{
    		return (getKeyStoreStream() != null);
    	}
    	catch(FileNotFoundException e)
    	{
    		return false;
    	}
    }
	
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
    
//    public KeyStore getKeyStore()
//    {
//    	
//    }

	public KeyManager[] createKeyManagers()
	throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, FileNotFoundException, IOException
	{
		if(ks == null)
		{
			throw new IllegalArgumentException("Keystore may not be null");
		}
		logger.debug("Initializing key manager");
		KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		
		KeyInfoManager keyInfoManager = getKeyInfoManager();
		try
		{
			String keyStorePassword = keyInfoManager.getKeyStorePassword();
			kmfactory.init(ks, keyStorePassword != null ? keyStorePassword.toCharArray(): null);
			return kmfactory.getKeyManagers(); 
		}
		finally
		{
			keyInfoManager.clear();
		}
	}
	
	public TrustManager[] createTrustManagers()
	throws KeyStoreException, NoSuchAlgorithmException
	{ 
		if(ks == null)
		{
			throw new IllegalArgumentException("Keystore may not be null");
		}
		logger.debug("Initializing trust manager");
		TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(
				TrustManagerFactory.getDefaultAlgorithm());
		tmfactory.init(ks);
		//        TrustManager[] trustmanagers = tmfactory.getTrustManagers();
		//        for (int i = 0; i < trustmanagers.length; i++) {
		//            if (trustmanagers[i] instanceof X509TrustManager) {
		//                trustmanagers[i] = new AuthSSLX509TrustManager(
		//                    (X509TrustManager)trustmanagers[i]); 
		//            }
		//        }
		//        return trustmanagers;
		return tmfactory.getTrustManagers();
	}

	protected Key generateSecretKey(KeyInformation keyInformation) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException
    {
		DESedeKeySpec keySpec = new DESedeKeySpec(keyInformation.getKeyData());	

//    	SecretKeySpec keySpec = new SecretKeySpec(keyInformation.getSeed(), keyInformation.getKeyAlgorithm());
    	SecretKeyFactory kf = SecretKeyFactory.getInstance(keyInformation.getKeyAlgorithm());
    	SecretKey secretKey = kf.generateSecret(keySpec);
    	return secretKey;
    }

//	public void create()
//	{
//		KeyInfoManager keyInfoManager = null;
//
//		try
//		{
//	        keyInfoManager = getKeyInfoManager();
//	
//	        ks.load(null, null);
//	        String keyStorePassword = keyInfoManager.getKeyStorePassword();
//	        if(keyStorePassword == null)
//	        {
//	        	// TODO
//	        }
//	
//			// Add keys from the passwords file to the keystore
//			for(Map.Entry<String, CachingKeyStore.KeyInformation> keyEntry : keyInfoManager.getKeyInfo().entrySet())
//			{
//				KeyInformation keyInfo = keyInfoManager.getKeyInformation(keyEntry.getKey());
//		        String keyPassword = keyInfo.getPassword();
//		        if(keyPassword == null)
//		        {
//		        	// TODO
//		        }
//	        	Key key = generateSecretKey(keyEntry.getValue());
//	        	ks.setKeyEntry(keyInfo.getAlias(), key, keyInfo.getPassword().toCharArray(), null);
//			}
//	
//	        ks.store(new FileOutputStream(getLocation()), keyStorePassword.toCharArray());
//		}
//		catch(Throwable e)
//		{
//            throw new AlfrescoRuntimeException(
//                    "Failed to create keystore: \n" +
//                    "   Location: " + getLocation() + "\n" +
//                    "   Provider: " + getProvider() + "\n" +
//                    "   Type:     " + getType(),
//                    e);
//		}
//		finally
//		{
//			if(keyInfoManager != null)
//			{
//				keyInfoManager.clear();
//			}
//		}
//	}

    public static class KeyInformation
    {
    	protected String alias;
    	protected byte[] keyData;
    	protected String password;
    	protected String keyAlgorithm;

		public KeyInformation(String alias, byte[] keyData, String password, String keyAlgorithm)
		{
			super();
			this.alias = alias;
			this.keyData = keyData;
			this.password = password;
			this.keyAlgorithm = keyAlgorithm;
		}

		public String getAlias()
		{
			return alias;
		}
		
		public byte[] getKeyData()
		{
			return keyData;
		}

		public String getPassword()
		{
			return password;
		}

		public String getKeyAlgorithm()
		{
			return keyAlgorithm;
		}
    }

    /*
     * Caches key meta data information such as password, seed.
     *
     */
    protected class KeyInfoManager
    {
    	private Properties keyProps;
    	private String keyStorePassword = null;
    	private Map<String, KeyInformation> keyInfo;

    	/**
    	 * For testing.
    	 * 
    	 * @param passwords
    	 * @throws IOException
    	 * @throws FileNotFoundException
    	 */
    	KeyInfoManager(Map<String, String> passwords) throws IOException, FileNotFoundException
    	{
    		keyInfo = new HashMap<String, KeyInformation>(2);
    		for(Map.Entry<String, String> password : passwords.entrySet())
    		{
    			keyInfo.put(password.getKey(), new KeyInformation(password.getKey(), null, password.getValue(), null));
    		}
    	}

    	KeyInfoManager() throws IOException, FileNotFoundException
    	{
    		keyInfo = new HashMap<String, KeyInformation>(2);
    		loadKeyMetaData();
    	}

    	public Map<String, KeyInformation> getKeyInfo()
    	{
    		// TODO defensively copy
    		return keyInfo;
    	}

    	protected void loadKeyMetaData() throws IOException, FileNotFoundException
    	{
    		keyProps = keyResourceLoader.loadKeyMetaData(getKeyMetaDataFileLocation());
    		if(keyProps != null)
    		{
	    		String aliases = keyProps.getProperty("aliases");
	    		if(aliases == null)
	    		{
	    			throw new AlfrescoRuntimeException("Passwords file must contain an aliases key");
	    		}
	
	    		this.keyStorePassword = keyProps.getProperty(KEY_KEYSTORE_PASSWORD);
	    		
	    		StringTokenizer st = new StringTokenizer(aliases, ",");
	    		while(st.hasMoreTokens())
	    		{
	    			String keyAlias = st.nextToken();
	    			keyInfo.put(keyAlias, loadKeyInformation(keyAlias));
	    		}
    		}
    		else
    		{
    			throw new FileNotFoundException("Cannot find key metadata file " + getKeyMetaDataFileLocation());
    		}

//        	keyProps = new Properties();
//        	if(keyInfoFileLocation != null)
//        	{
//        		keyProps.load(new BufferedInputStream(new FileInputStream(ResourceUtils.getFile(keyInfoFileLocation))));
//        	}
    	}
    	
//    	public void loadKeyInformation() throws IOException
//    	{
//    		Properties keyInfoProps = loadKeyInfo();
//    		Map<String, KeyInformation> keyInfo = new HashMap<String, KeyInformation>(keyAliases.size());
//
//    		String keyStorePassword = keyInfoProps.getProperty(KEY_KEYSTORE_PASSWORD + ".password");
//    		keyInfo.put(KEY_KEYSTORE_PASSWORD, new KeyInformation(KEY_KEYSTORE_PASSWORD, keyAlgorithm, null, keyStorePassword));
//
//    		for(String keyAlias : keyAliases)
//    		{
//    			String keyPassword = keyInfoProps.getProperty(keyAlias + ".password");
//    			String keySeed = keyInfoProps.getProperty(keyAlias + ".seed");
//    			keyInfo.put(keyAlias, new SecretKeyInformation(keyAlias, keyAlgorithm, keySeed.getBytes("UTF-8"), keyPassword));
//    		}
//    	}
    	
    	public void clear()
    	{
    		this.keyStorePassword = null;
    		if(this.keyProps != null)
    		{
    			this.keyProps.clear();
    		}
    	}

    	public void removeKeyInformation(String keyAlias)
    	{
    		this.keyProps.remove(keyAlias);
    	}

    	protected KeyInformation loadKeyInformation(String keyAlias)
    	{
            String keyPassword = keyProps.getProperty(keyAlias + ".password");
            String keySeed = keyProps.getProperty(keyAlias + ".seed");
            String keyAlgorithm = keyProps.getProperty(keyAlias + ".algorithm");
            
            byte[] seedBytes = null;
            if(keySeed != null)
            {
            	seedBytes = Base64.decodeBase64(keySeed);
            }
            KeyInformation keyInfo = new KeyInformation(keyAlias, seedBytes, keyPassword, keyAlgorithm);
            return keyInfo;
    	}

    	public String getKeyStorePassword()
    	{
    		return keyStorePassword;
    	}
    	
    	public void clearKeyStorePassword()
    	{
    		this.keyStorePassword = null;
    	}

    	public KeyInformation getKeyInformation(String keyAlias)
    	{
    		return keyInfo.get(keyAlias);
    	}
    }
}

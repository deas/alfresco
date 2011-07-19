package org.alfresco.solr.client;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.encryption.DefaultEncryptionUtils;
import org.alfresco.encryption.DefaultEncryptor;
import org.alfresco.encryption.EncryptionUtils;
import org.alfresco.encryption.Encryptor;
import org.alfresco.encryption.KeyProvider;
import org.alfresco.encryption.KeyStoreLoader;
import org.alfresco.encryption.KeystoreKeyProvider;
import org.alfresco.encryption.MACUtils;

/**
 * Provides encryption services for SOLR communications with the Alfresco repository
 * 
 * @since 4.0
 *
 */
public class EncryptionService
{
	protected String alfrescoHost;
	protected String cipherAlgorithm;
	protected String keyStoreType;
	protected String keyStoreProvider;
	protected String keyStorePassword;
	protected String solrKeyPassword;
	protected String keyStoreLocation;
	protected String macAlgorithm;

	protected long messageTimeout; // ms

	protected KeyStoreLoader keyStoreLoader;
	
	protected KeystoreKeyProvider keyProvider;
	protected DefaultEncryptor encryptor;
	protected MACUtils macUtils;
	protected DefaultEncryptionUtils encryptionUtils;
	
	public EncryptionService(KeyStoreLoader keyStoreLoader, String keyStoreLocation, String alfrescoHost,
			String cipherAlgorithm, String keyStoreType, String keyStoreProvider, String keyStorePassword, String solrKeyPassword,
			long messageTimeout, String macAlgorithm)
	{
		this.alfrescoHost = alfrescoHost;
		this.cipherAlgorithm = cipherAlgorithm;
		this.keyStoreType = keyStoreType;
		this.keyStoreProvider = keyStoreProvider;
		this.keyStorePassword = keyStorePassword;
		this.solrKeyPassword = solrKeyPassword;
		this.keyStoreLoader = keyStoreLoader;
		this.keyStoreLocation = keyStoreLocation;
		this.messageTimeout = messageTimeout;
		this.macAlgorithm = macAlgorithm;
		setup();
	}

	public KeyProvider getKeyProvider()
	{
		return keyProvider;
	}

	public Encryptor getEncryptor()
	{
		return encryptor;
	}
	
	public MACUtils getMacUtils()
	{
		return macUtils;
	}
	
	public EncryptionUtils getEncryptionUtils()
	{
		return encryptionUtils;
	}
	
	protected void setup()
	{
		setupKeyProvider();
		setupEncryptor();
		setupMacUtils();
		setupEncryptionUtils();
	}

	protected void setupEncryptionUtils()
	{
		encryptionUtils = new DefaultEncryptionUtils();
		encryptionUtils.setEncryptor(getEncryptor());
		encryptionUtils.setMacUtils(getMacUtils());
		encryptionUtils.setMessageTimeout(messageTimeout);
		encryptionUtils.setRemoteIP(alfrescoHost);
	}
	
    protected void setupKeyProvider()
    {
        Map<String, String> passwords = new HashMap<String, String>(5);
        passwords.put(KeyProvider.ALIAS_SOLR, solrKeyPassword);
        passwords.put(KeystoreKeyProvider.KEY_KEYSTORE_PASSWORD, keyStorePassword);
        keyProvider = new KeystoreKeyProvider();
    	keyProvider.setLocation(keyStoreLocation);
    	keyProvider.setKeyStoreLoader(keyStoreLoader);
    	keyProvider.setPasswords(passwords);
    	keyProvider.setProvider(keyStoreProvider);
    	keyProvider.setType(keyStoreType);
    	keyProvider.init();
    }
    
    protected void setupMacUtils()
    {
    	macUtils = new MACUtils();
    	macUtils.setKeyProvider(getKeyProvider());
    	macUtils.setMacAlgorithm(macAlgorithm);
    }
    
    protected void setupEncryptor()
    {
    	encryptor = new DefaultEncryptor();
    	encryptor.setKeyProvider(getKeyProvider());
    	encryptor.setCipherAlgorithm(cipherAlgorithm);
    	encryptor.init();
    }

}

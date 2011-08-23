package org.alfresco.httpclient;

import org.alfresco.encryption.DefaultEncryptionUtils;
import org.alfresco.encryption.DefaultEncryptor;
import org.alfresco.encryption.EncryptionUtils;
import org.alfresco.encryption.Encryptor;
import org.alfresco.encryption.KeyProvider;
import org.alfresco.encryption.KeyResourceLoader;
import org.alfresco.encryption.KeyStoreParameters;
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
	protected KeyStoreParameters keyStoreParameters;
	protected MD5EncryptionParameters encryptionParameters;

	protected KeyResourceLoader keyResourceLoader;
	
	protected String alfrescoHost;
	protected int alfrescoPort;

	protected KeystoreKeyProvider keyProvider;
	protected DefaultEncryptor encryptor;
	protected MACUtils macUtils;
	protected DefaultEncryptionUtils encryptionUtils;
	
	public EncryptionService(String alfrescoHost, int alfrescoPort, KeyResourceLoader keyResourceLoader,
			KeyStoreParameters keyStoreParameters, MD5EncryptionParameters encryptionParameters)
	{
		this.keyStoreParameters = keyStoreParameters;
		this.encryptionParameters = encryptionParameters;
		this.alfrescoHost = alfrescoHost;
		this.alfrescoHost = alfrescoHost;
		this.keyResourceLoader = keyResourceLoader;
		setup();
	}
	
	public MD5EncryptionParameters getEncryptionParameters()
	{
		return encryptionParameters;
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
		encryptionUtils.setMessageTimeout(encryptionParameters.getMessageTimeout());
		encryptionUtils.setRemoteIP(alfrescoHost);
	}
	
    protected void setupKeyProvider()
    {
        keyProvider = new KeystoreKeyProvider(keyStoreParameters, keyResourceLoader);
        //KeyStoreManager keyStoreManager = new KeyStoreManager(encryptionParameters, keyResourceLoader);
//    	keyProvider.setLocation(encryptionParameters.getKeyStoreLocation());
//    	keyProvider.setKeyResourceLoader(keyResourceLoader);
//    	keyProvider.setPasswordsFileLocation(encryptionParameters.getPasswordFileLocation());
//    	keyProvider.setProvider(encryptionParameters.getKeyStoreProvider());
//    	keyProvider.setType(encryptionParameters.getKeyStoreType());
//    	keyProvider.init();
    }
    
    protected void setupMacUtils()
    {
    	macUtils = new MACUtils();
    	macUtils.setKeyProvider(getKeyProvider());
    	macUtils.setMacAlgorithm(encryptionParameters.getMacAlgorithm());
    }
    
    protected void setupEncryptor()
    {
    	encryptor = new DefaultEncryptor();
    	encryptor.setKeyProvider(getKeyProvider());
    	encryptor.setCipherAlgorithm(encryptionParameters.getCipherAlgorithm());
    	encryptor.init();
    }

}

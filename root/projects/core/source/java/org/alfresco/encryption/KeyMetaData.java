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
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Stores encryption key metadata such as password, key algorithm, seed information (if available)
 * @author steveglover
 *
 */
public class KeyMetaData
{
	private String keyMetaDataFileLocation;
	private KeyResourceLoader keyResourceLoader;
	private Properties keyProps;
	private Map<String, KeyInformation> keyInfo;
	
	/**
	 * For testing.
	 * 
	 * @param passwords
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	KeyMetaData(Map<String, String> passwords) throws IOException, FileNotFoundException
	{
		for(Map.Entry<String, String> password : passwords.entrySet())
		{
			keyInfo.put(password.getKey(), new KeyInformation(password.getKey(), null, password.getValue(), null));
		}
	}

	KeyMetaData(String keyMetaDataFileLocation, KeyResourceLoader keyResourceLoader) throws IOException, FileNotFoundException
	{
		this.keyMetaDataFileLocation = keyMetaDataFileLocation;
		this.keyResourceLoader = keyResourceLoader;
		loadKeyInformation();
	}
	
	protected void loadKeyInformation() throws IOException, FileNotFoundException
	{
		keyProps = keyResourceLoader.loadKeyMetaData(keyMetaDataFileLocation);
		StringTokenizer st = new StringTokenizer(keyProps.getProperty("aliases"), ",");
		while(st.hasMoreTokens())
		{
			String keyAlias = st.nextToken();
			keyInfo.put(keyAlias, loadKeyInformation(keyAlias));
		}

//    	keyProps = new Properties();
//    	if(keyInfoFileLocation != null)
//    	{
//    		keyProps.load(new BufferedInputStream(new FileInputStream(ResourceUtils.getFile(keyInfoFileLocation))));
//    	}
	}
	
//	public void loadKeyInformation() throws IOException
//	{
//		Properties keyInfoProps = loadKeyInfo();
//		Map<String, KeyInformation> keyInfo = new HashMap<String, KeyInformation>(keyAliases.size());
//
//		String keyStorePassword = keyInfoProps.getProperty(KEY_KEYSTORE_PASSWORD + ".password");
//		keyInfo.put(KEY_KEYSTORE_PASSWORD, new KeyInformation(KEY_KEYSTORE_PASSWORD, keyAlgorithm, null, keyStorePassword));
//
//		for(String keyAlias : keyAliases)
//		{
//			String keyPassword = keyInfoProps.getProperty(keyAlias + ".password");
//			String keySeed = keyInfoProps.getProperty(keyAlias + ".seed");
//			keyInfo.put(keyAlias, new SecretKeyInformation(keyAlias, keyAlgorithm, keySeed.getBytes("UTF-8"), keyPassword));
//		}
//	}
	
	public void removeKeyInformation()
	{
		keyProps.clear();
	}

	public void removeKeyInformation(String keyAlias)
	{
		keyProps.remove(keyAlias);
	}

	protected KeyInformation loadKeyInformation(String keyAlias)
	{
        String keyPassword = keyProps.getProperty(keyAlias + ".password");
        String keySeed = keyProps.getProperty(keyAlias + ".seed");
        String keyAlgorithm = keyProps.getProperty(keyAlias + ".algorithm");
        
        byte[] seedBytes = null;
        try
        {
        	seedBytes = keySeed.getBytes("UTF-8");
        }
        catch(UnsupportedEncodingException e)
        {
        	seedBytes = keySeed.getBytes();
        }
        KeyInformation keyInfo = new KeyInformation(keyAlias, seedBytes, keyPassword, keyAlgorithm);
        return keyInfo;
	}

	public KeyInformation getKeyInformation(String keyAlias)
	{
		return keyInfo.get(keyAlias);
	}
	
    public static class KeyInformation
    {
    	protected String alias;
    	protected byte[] seed;
    	protected String password;
    	protected String keyAlgorithm;

		public KeyInformation(String alias, byte[] seed, String password, String keyAlgorithm)
		{
			super();
			this.alias = alias;
			this.seed = seed;
			this.password = password;
			this.keyAlgorithm = keyAlgorithm;
		}

		public String getAlias()
		{
			return alias;
		}
		
		public byte[] getSeed()
		{
			return seed;
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
}

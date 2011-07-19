package org.alfresco.encryption;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.springframework.util.ResourceUtils;

/**
 * Loads a key store from the Spring classpath.
 * 
 * @since 4.0
 *
 */
public class SpringKeyStoreLoader implements KeyStoreLoader
{
	
	protected File getKeyStoreFile(String location) throws FileNotFoundException
	{
		return ResourceUtils.getFile(location);
	}
	
    /**
     * {@inheritDoc}
     */
    @Override
	public InputStream getKeyStore(String location) throws FileNotFoundException
	{
		return new BufferedInputStream(new FileInputStream(getKeyStoreFile(location)));
	}

}

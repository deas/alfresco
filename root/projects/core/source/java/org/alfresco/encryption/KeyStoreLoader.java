package org.alfresco.encryption;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Provides a mechanism for loading a key store from an arbitrary source e.g. a classpath.
 * 
 * @since 4.0
 * 
 */
public interface KeyStoreLoader
{
	public InputStream getKeyStore(String location) throws FileNotFoundException;
}

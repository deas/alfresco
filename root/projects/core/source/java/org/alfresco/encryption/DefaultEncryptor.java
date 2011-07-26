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

import java.security.AlgorithmParameters;
import java.security.Key;

import javax.crypto.Cipher;

import org.alfresco.util.PropertyCheck;

/**
 * @author Derek Hulley
 * @since 4.0
 */
public class DefaultEncryptor extends AbstractEncryptor
{
    private final ThreadLocal<Cipher> threadCipher;

    /**
     * Default constructor for IOC
     */
    public DefaultEncryptor()
    {
        threadCipher = new ThreadLocal<Cipher>();
    }
    
    /**
     * Convenience constructor for tests
     */
    /* package */ DefaultEncryptor(KeyProvider keyProvider, String cipherAlgorithm, String cipherProvider)
    {
        this();
        setKeyProvider(keyProvider);
        setCipherAlgorithm(cipherAlgorithm);
        setCipherProvider(cipherProvider);
    }
    
    public void init()
    {
        super.init();
        PropertyCheck.mandatory(this, "cipherAlgorithm", cipherAlgorithm);
    }

    @Override
    protected Cipher getCipher(Key key, AlgorithmParameters params, int mode) throws Exception
    {
        Cipher cipher = threadCipher.get();
        if (cipher == null)
        {
            if (cipherProvider == null)
            {
                cipher = Cipher.getInstance(cipherAlgorithm);
            }
            else
            {
                cipher = Cipher.getInstance(cipherAlgorithm, cipherProvider);
            }
            threadCipher.set(cipher);
        }
        cipher.init(mode, key, params);
        return cipher;
    }
}

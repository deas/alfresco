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

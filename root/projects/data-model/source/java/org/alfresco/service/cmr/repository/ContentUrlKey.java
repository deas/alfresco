package org.alfresco.service.cmr.repository;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * 
 * @author sglover
 *
 */
public class ContentUrlKey implements Serializable
{
	private static final long serialVersionUID = -2943112451758281764L;

	private ByteBuffer encryptedKeyBytes;
    private Integer keySize;
    private String algorithm;
    private String masterKeystoreId;
    private String masterKeyAlias;
    private Long unencryptedFileSize;

    public ContentUrlKey()
    {
    }

	public ByteBuffer getEncryptedKeyBytes() 
	{
		return encryptedKeyBytes;
	}

	public void setEncryptedKeyBytes(ByteBuffer encryptedKeyBytes)
	{
		this.encryptedKeyBytes = encryptedKeyBytes;
	}

	public Long getUnencryptedFileSize()
	{
		return unencryptedFileSize;
	}

	public void setUnencryptedFileSize(Long unencryptedFileSize)
	{
		this.unencryptedFileSize = unencryptedFileSize;
	}

	public void setKeySize(Integer keySize)
	{
		this.keySize = keySize;
	}

	public Integer getKeySize()
	{
		return keySize;
	}

	public String getAlgorithm()
	{
		return algorithm;
	}

	public void setAlgorithm(String algorithm)
	{
		this.algorithm = algorithm;
	}

	public String getMasterKeystoreId()
	{
		return masterKeystoreId;
	}

	public void setMasterKeystoreId(String masterKeystoreId)
	{
		this.masterKeystoreId = masterKeystoreId;
	}

	public String getMasterKeyAlias()
	{
		return masterKeyAlias;
	}

	public void setMasterKeyAlias(String masterKeyAlias) 
	{
		this.masterKeyAlias = masterKeyAlias;
	}
}

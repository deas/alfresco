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
package org.alfresco.httpclient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AlgorithmParameters;

import org.alfresco.encryption.EncryptionUtils;
import org.alfresco.encryption.Encryptor;
import org.alfresco.encryption.KeyProvider;
import org.alfresco.encryption.KeyResourceLoader;
import org.alfresco.encryption.KeyStoreParameters;
import org.alfresco.encryption.ssl.AuthSSLProtocolSocketFactory;
import org.alfresco.encryption.ssl.SSLEncryptionParameters;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.util.Pair;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A factory to create HttpClients and AlfrescoHttpClients based on the setting of the 'secureCommsType' property.
 * 
 * @since 4.0
 *
 */
public class HttpClientFactory
{
	public static enum SecureCommsType
	{
		HTTPS, NONE;
		
		public static SecureCommsType getType(String type)
		{
			if(type.equalsIgnoreCase("https"))
			{
				return HTTPS;
			}
			else if(type.equalsIgnoreCase("none"))
			{
				return NONE;
			}
			else
			{
				throw new IllegalArgumentException("Invalid communications type");
			}
		}
	};

    private static final Log logger = LogFactory.getLog(HttpClientFactory.class);

    private SSLEncryptionParameters sslEncryptionParameters;
    private KeyResourceLoader keyResourceLoader;
	private SecureCommsType secureCommsType;

	// for md5 http client (no longer used but kept for now)
    private KeyStoreParameters keyStoreParameters;
    private MD5EncryptionParameters encryptionParameters;

    public HttpClientFactory()
    {
    }

    public HttpClientFactory(SecureCommsType secureCommsType, SSLEncryptionParameters sslEncryptionParameters, KeyResourceLoader keyResourceLoader,
    		KeyStoreParameters keyStoreParameters, MD5EncryptionParameters encryptionParameters)
    {
    	this.secureCommsType = secureCommsType;
    	this.sslEncryptionParameters = sslEncryptionParameters;
    	this.keyResourceLoader = keyResourceLoader;
    	this.keyStoreParameters = keyStoreParameters;
    	this.encryptionParameters = encryptionParameters;
    }

	public boolean isSSL()
	{
		return secureCommsType == SecureCommsType.HTTPS;
	}

	public void setSecureCommsType(String type)
	{
		try
		{
			this.secureCommsType = SecureCommsType.getType(type);
		}
		catch(IllegalArgumentException e)
		{
			throw new AlfrescoRuntimeException("", e);
		}
	}
	
    public void setSSLEncryptionParameters(SSLEncryptionParameters sslEncryptionParameters)
	{
		this.sslEncryptionParameters = sslEncryptionParameters;
	}

	public void setKeyStoreParameters(KeyStoreParameters keyStoreParameters)
	{
		this.keyStoreParameters = keyStoreParameters;
	}

	public void setEncryptionParameters(MD5EncryptionParameters encryptionParameters)
	{
		this.encryptionParameters = encryptionParameters;
	}

	public void setKeyResourceLoader(KeyResourceLoader keyResourceLoader)
	{
		this.keyResourceLoader = keyResourceLoader;
	}
	
	protected HttpClient constructHttpClient(String host, int port)
	{
        MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		HttpClient httpClient = new HttpClient(connectionManager);
        HttpClientParams params = httpClient.getParams();
        params.setBooleanParameter("http.tcp.nodelay", true);
        params.setBooleanParameter("http.connection.stalecheck", false);

        return httpClient;
	}
	
	protected HttpClient getHttpsClient(String host, int port)
	{
		HttpClient httpClient = constructHttpClient(host, port);
    	ProtocolSocketFactory socketFactory = new AuthSSLProtocolSocketFactory(sslEncryptionParameters, keyResourceLoader);
        Protocol myhttps = new Protocol("https", socketFactory, port);
		Protocol.registerProtocol("https", myhttps);
        httpClient.getHostConfiguration().setHost(host, port, myhttps);
        return httpClient;
	}
	
	protected HttpClient getDefaultHttpClient(String host, int port)
	{
		HttpClient httpClient = constructHttpClient(host, port);
        return httpClient;
	}
	
	protected AlfrescoHttpClient getAlfrescoHttpsClient(String host, int port)
	{
        AlfrescoHttpClient repoClient = new HttpsClient(getHttpClient(host, port));
        return repoClient;
	}

    protected AlfrescoHttpClient getAlfrescoHttpClient(String host, int port)
	{
        AlfrescoHttpClient repoClient = new DefaultHttpClient(getHttpClient(host, port));
        return repoClient;
	}
    
	protected HttpClient getMD5HttpClient(String host, int port)
	{
		HttpClient httpClient = constructHttpClient(host, port);
        httpClient.getHostConfiguration().setHost(host, port);
        return httpClient;
	}
	
    protected AlfrescoHttpClient getAlfrescoMD5HttpClient(String host, int port)
	{
        AlfrescoHttpClient repoClient = new SecureHttpClient(getHttpClient(host, port), keyResourceLoader, host, port,
        		keyStoreParameters, encryptionParameters);
        return repoClient;
	}
    
    /**
     * For testing.
     * 
     * @param host
     * @param port
     * @param encryptionService
     * @return
     */
    protected AlfrescoHttpClient getAlfrescoMD5HttpClient(String host, int port, EncryptionService encryptionService)
	{
        AlfrescoHttpClient repoClient = new SecureHttpClient(getHttpClient(host, port), encryptionService);
        return repoClient;
	}
	
	public AlfrescoHttpClient getRepoClient(String host, int port)
    {
        AlfrescoHttpClient repoClient = null;

        if(secureCommsType == SecureCommsType.HTTPS)
        {
        	repoClient = getAlfrescoHttpsClient(host, port);
        }
        else if(secureCommsType == SecureCommsType.NONE)
        {
        	repoClient = getAlfrescoHttpClient(host, port);
        }
        else
        {
        	throw new AlfrescoRuntimeException("Invalid Solr secure communications type configured in alfresco.secureComms, should be 'ssl'or 'none'");
        }

        return repoClient;
    }
	
	public HttpClient getHttpClient(String host, int port)
    {
        HttpClient httpClient = null;

        if(secureCommsType == SecureCommsType.HTTPS)
        {
        	httpClient = getHttpsClient(host, port);
        }
        else if(secureCommsType == SecureCommsType.NONE)
        {
        	httpClient = getDefaultHttpClient(host, port);
        }
        else
        {
        	throw new AlfrescoRuntimeException("Invalid Solr secure communications type configured in alfresco.secureComms, should be 'ssl'or 'none'");
        }

        return httpClient;
    }
	

	
	/**
	 * A secure client connection to the repository.
	 * 
	 * @since 4.0
	 *
	 */
	// TODO put ssl keystore inside jar file instead of on the filesystem?
	class HttpsClient extends AbstractHttpClient
	{
	    public HttpsClient(HttpClient httpClient/*, String alfrescoHost, int alfrescoPort*/)
	    {
	    	super(httpClient);
	    }

	    protected HttpMethod createMethod(Request req) throws IOException
	    {
	    	HttpMethod method = super.createMethod(req);
	    	return method;
		}

	    /**
	     * Send Request to the repository
	     */
	    public Response sendRequest(Request req) throws AuthenticationException, IOException
	    {
	    	HttpMethod method = super.sendRemoteRequest(req);
	    	return new HttpMethodResponse(method);
	    }
	    
	    protected HttpMethod sendRemoteRequest(Request req) throws AuthenticationException, IOException
	    {
	    	return super.sendRemoteRequest(req);

//	        Header locationHeader = method.getResponseHeader("location");
//	        if (locationHeader != null)
//	        {
//	            String redirectLocation = locationHeader.getValue();
//	            method.setURI(new URI(redirectLocation, true));
//	            executeMethod(method);
//	        }
//	        else
//	        {
//	            // The response is invalid and did not provide the new location for
//	            // the resource.  Report an error or possibly handle the response
//	            // like a 404 Not Found error.
////	        	throw new AlfrescoRuntimeException("");
//	        }
	    }
	}
	
    /**
     * Simple HTTP client to connect to the Alfresco server. Simply wraps a HttpClient.
     * 
     * @since 4.0
     */
    class DefaultHttpClient extends AbstractHttpClient
    {        
        public DefaultHttpClient(HttpClient httpClient)
        {
        	super(httpClient);
        }

        /**
         * Send Request to the repository
         */
	    public Response sendRequest(Request req) throws AuthenticationException, IOException
	    {
	    	HttpMethod method = super.sendRemoteRequest(req);
	    	return new HttpMethodResponse(method);
	    }
    }
    
    /**
     * Simple HTTP client to connect to the Alfresco server.
     * 
     * @since 4.0
     */
    class SecureHttpClient extends AbstractHttpClient
    {
        private Encryptor encryptor;
        private EncryptionUtils encryptionUtils;
        private EncryptionService encryptionService;
        private KeyStoreParameters keyStoreParameters;
        private MD5EncryptionParameters encryptionParameters;
        
        /**
         * For testing purposes.
         * 
         * @param solrResourceLoader
         * @param alfrescoHost
         * @param alfrescoPort
         * @param encryptionParameters
         */
        public SecureHttpClient(HttpClient httpClient, EncryptionService encryptionService)
        {
        	super(httpClient);
            this.encryptionUtils = encryptionService.getEncryptionUtils();
            this.encryptor = encryptionService.getEncryptor();
            this.encryptionService = encryptionService;
            this.encryptionParameters = encryptionService.getEncryptionParameters();
        }
        
        public SecureHttpClient(HttpClient httpClient, KeyResourceLoader keyResourceLoader, String host, int port,
        		KeyStoreParameters keyStoreParameters, MD5EncryptionParameters encryptionParameters)
        {
        	super(httpClient);
        	this.encryptionParameters = encryptionParameters;
            this.encryptionService = new EncryptionService(host, port, keyResourceLoader, keyStoreParameters, encryptionParameters);
            this.encryptionUtils = encryptionService.getEncryptionUtils();
            this.encryptor = encryptionService.getEncryptor();
        }
        
        protected HttpMethod createMethod(Request req) throws IOException
        {
        	byte[] message = null;
        	HttpMethod method = super.createMethod(req);

        	if(req.getMethod().equalsIgnoreCase("POST"))
        	{
    	        message = req.getBody();
    	        // encrypt body
    	        Pair<byte[], AlgorithmParameters> encrypted = encryptor.encrypt(KeyProvider.ALIAS_SOLR, null, message);
    	        encryptionUtils.setRequestAlgorithmParameters(method, encrypted.getSecond());

    	        ByteArrayRequestEntity requestEntity = new ByteArrayRequestEntity(encrypted.getFirst(), "application/octet-stream");
    	        ((PostMethod)method).setRequestEntity(requestEntity);
        	}

        	encryptionUtils.setRequestAuthentication(method, message);

        	return method;
    	}
        
        protected HttpMethod sendRemoteRequest(Request req) throws AuthenticationException, IOException
        {
        	HttpMethod method = super.sendRemoteRequest(req);

        	// check that the request returned with an ok status
        	if(method.getStatusCode() == HttpStatus.SC_UNAUTHORIZED)
        	{
        		throw new AuthenticationException(method);
        	}
        	
        	return method;
        }

        /**
         * Send Request to the repository
         */
        public Response sendRequest(Request req) throws AuthenticationException, IOException
        {
        	HttpMethod method = super.sendRemoteRequest(req);
        	return new SecureHttpMethodResponse(method, httpClient.getHostConfiguration(), encryptionUtils);
        }
    }
    
    static class SecureHttpMethodResponse extends HttpMethodResponse
    {
    	protected HostConfiguration hostConfig;
        protected EncryptionUtils encryptionUtils;
		// Need to get as a byte array because we need to read the request twice, once for authentication
		// and again by the web service.
        protected byte[] decryptedBody;

        public SecureHttpMethodResponse(HttpMethod method, HostConfiguration hostConfig, 
        		EncryptionUtils encryptionUtils) throws AuthenticationException, IOException
        {
        	super(method);
        	this.hostConfig = hostConfig;
            this.encryptionUtils = encryptionUtils;

			if(method.getStatusCode() == HttpStatus.SC_OK)
			{
    			this.decryptedBody = encryptionUtils.decryptResponseBody(method);
				// authenticate the response
    			if(!authenticate())
    			{
    				throw new AuthenticationException(method);
    			}
			}
        }
        
        protected boolean authenticate() throws IOException
        {
        	return encryptionUtils.authenticateResponse(method, hostConfig.getHost(), decryptedBody);
        }
        
        public InputStream getContentAsStream() throws IOException
        {
        	if(decryptedBody != null)
        	{
        		return new ByteArrayInputStream(decryptedBody);
        	}
        	else
        	{
        		return null;
        	}
        }
    }
}

package org.alfresco.solr.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.util.Map;

import org.alfresco.encryption.EncryptionUtils;
import org.alfresco.encryption.Encryptor;
import org.alfresco.encryption.KeyProvider;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.util.Pair;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Simple HTTP client to connect to the Alfresco server.
 * 
 * @since 4.0
 */
public class AlfrescoHttpClient
{
    private static final Log logger = LogFactory.getLog(AlfrescoHttpClient.class);

    protected String url;
    protected String username;
    protected String password;

    protected boolean secureComms;

    // Remote Server access
    private HttpClient httpClient = null;

    private Encryptor encryptor;
    private EncryptionUtils encryptionUtils;
    
    public AlfrescoHttpClient(EncryptionService encryptionService, boolean secureComms, String alfrescoURL, String username, String password)
    {
    	this.secureComms = secureComms;
        this.url = alfrescoURL;
        this.username = username;
        this.password = password;
        this.encryptionUtils = encryptionService.getEncryptionUtils();
        this.encryptor = encryptionService.getEncryptor();
    }
    
    protected void setupHttpClient()
    {
        httpClient = new HttpClient();
        httpClient.getParams().setBooleanParameter(HttpClientParams.PREEMPTIVE_AUTHENTICATION, true);
        httpClient.getState().setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials(username, password));
    }
    
    /**
     * Send Request to Test Web Script Server (as admin)
     */
    protected Response sendRequest(Request req) throws AuthenticationException, IOException
    {
        return sendRequest(req, null);
    }

    /**
     * Send Request
     */
    protected Response sendRequest(Request req, String asUser) throws AuthenticationException, IOException
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("");
            logger.debug("* Request: " + req.getMethod() + " " + req.getFullUri() + (req.getBody() == null ? "" : "\n" + new String(req.getBody(), "UTF-8")));
        }

        Response res = sendRemoteRequest(req);

//        if (logger.isDebugEnabled())
//        {
//            logger.debug("");
//            logger.debug("* Response: " + res.getStatus() + " " + req.getMethod() + " " + req.getFullUri() + "\n" + res.getContentAsString());
//        }

        return res;
    }
    
    /**
     * Send Remote Request to stand-alone Web Script Server
     */
    protected Response sendRemoteRequest(Request req) throws AuthenticationException, IOException
    {
    	byte[] message = null;
    	RequestEntity requestEntity = null;
        String uri = req.getFullUri();
        if (!uri.startsWith("http"))
        {
            uri = this.url + uri;
        }
        
        // construct method
        HttpMethod httpMethod = null;
        String method = req.getMethod();
        if(method.equalsIgnoreCase("GET"))
        {
            GetMethod get = new GetMethod(req.getFullUri());
            httpMethod = get;
        }
        else if(method.equalsIgnoreCase("POST"))
        {
            PostMethod post = new PostMethod(req.getFullUri());

            message = req.getBody();
            if(secureComms)
            {
    	        // encrypt body
    	        Pair<byte[], AlgorithmParameters> encrypted = encryptor.encrypt(KeyProvider.ALIAS_SOLR, null, message);
    	        encryptionUtils.setRequestAlgorithmParameters(post, encrypted.getSecond());

    	        requestEntity = new ByteArrayRequestEntity(encrypted.getFirst(), "application/octet-stream");
            }
            else
            {
            	requestEntity = new ByteArrayRequestEntity(req.getBody(), req.getType());
            }

	        post.setRequestEntity(requestEntity);

            httpMethod = post;
        }
        else if(method.equalsIgnoreCase("HEAD"))
        {
            HeadMethod head = new HeadMethod(req.getFullUri());
            httpMethod = head;
        }
        else
        {
            throw new AlfrescoRuntimeException("Http Method " + method + " not supported");
        }

        if(secureComms)
        {
        	encryptionUtils.setRequestAuthentication(httpMethod, message);
        }

        if (req.getHeaders() != null)
        {
            for (Map.Entry<String, String> header : req.getHeaders().entrySet())
            {
                httpMethod.setRequestHeader(header.getKey(), header.getValue());
            }
        }

        // execute method
        long startTime = System.currentTimeMillis();
        httpClient.executeMethod(httpMethod);
        long endTime = System.currentTimeMillis();

        if(secureComms)
        {
			// check that the request returned with an ok status
			if(httpMethod.getStatusCode() == HttpStatus.SC_UNAUTHORIZED)
			{
				throw new AuthenticationException(httpMethod);
			}

			//"Request " + httpMethod.getPath() + " responded with an error code " + httpMethod.getStatusCode()
	
	        return new SecureHttpMethodResponse(httpMethod, Long.valueOf(endTime - startTime), httpMethod.getHostConfiguration(), encryptionUtils);
        }
        else
        {
        	return new HttpMethodResponse(httpMethod, Long.valueOf(endTime - startTime));
        }
    }
    
    /**
     * A Remote API Response
     */
    public interface Response
    {
        public InputStream getContentAsStream() throws IOException;
        
        public String getHeader(String name);
        
        public String getContentType();
        
        public int getStatus();
        
        public Long getRequestDuration();
        
        public void release();
    }

    public static class HttpMethodResponse implements Response
    {
    	protected HttpMethod method;
    	protected Long duration;

        public HttpMethodResponse(HttpMethod method, Long duration) throws IOException
        {
            this.method = method;
            this.duration = duration;
        }
        
        public void release()
        {
        	method.releaseConnection();
        }

        public InputStream getContentAsStream() throws IOException
        {
        	return method.getResponseBodyAsStream();
        }

        public String getContentType()
        {
            return getHeader("Content-Type");
        }

        public String getHeader(String name)
        {
            Header header = method.getResponseHeader(name);
            return (header != null) ? header.getValue() : null;
        }

        public int getStatus()
        {
            return method.getStatusCode();
        }

        public Long getRequestDuration()
        {
            return duration;
        }
    }
    
    public static class SecureHttpMethodResponse extends HttpMethodResponse
    {
    	protected HostConfiguration hostConfig;
        protected EncryptionUtils encryptionUtils;
		// Need to get as a byte array because we need to read the request twice, once for authentication
		// and again by the web service.
        protected byte[] decryptedBody;

        public SecureHttpMethodResponse(HttpMethod method, Long duration, HostConfiguration hostConfig, 
        		EncryptionUtils encryptionUtils) throws AuthenticationException, IOException
        {
        	super(method, duration);
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
    
    public static class Request
    {
        private String method;
        private String uri;
        private Map<String, String> args;
        private Map<String, String> headers;
        private byte[] body;
        private String encoding = "UTF-8";
        private String contentType;
        
        public Request(Request req)
        {
            this.method = req.method;
            this.uri= req.uri;
            this.args = req.args;
            this.headers = req.headers;
            this.body = req.body;
            this.encoding = req.encoding;
            this.contentType = req.contentType;
        }
        
        public Request(String method, String uri)
        {
            this.method = method;
            this.uri = uri;
        }
        
        public String getMethod()
        {
            return method;
        }
        
        public String getUri()
        {
            return uri;
        }
        
        public String getFullUri()
        {
            // calculate full uri
            String fullUri = uri == null ? "" : uri;
            if (args != null && args.size() > 0)
            {
                char prefix = (uri.indexOf('?') == -1) ? '?' : '&';
                for (Map.Entry<String, String> arg : args.entrySet())
                {
                    fullUri += prefix + arg.getKey() + "=" + (arg.getValue() == null ? "" : arg.getValue());
                    prefix = '&';
                }
            }
            
            return fullUri;
        }
        
        public Request setArgs(Map<String, String> args)
        {
            this.args = args;
            return this;
        }
        
        public Map<String, String> getArgs()
        {
            return args;
        }

        public Request setHeaders(Map<String, String> headers)
        {
            this.headers = headers;
            return this;
        }
        
        public Map<String, String> getHeaders()
        {
            return headers;
        }
        
        public Request setBody(byte[] body)
        {
            this.body = body;
            return this;
        }
        
        public byte[] getBody()
        {
            return body;
        }
        
        public Request setEncoding(String encoding)
        {
            this.encoding = encoding;
            return this;
        }
        
        public String getEncoding()
        {
            return encoding;
        }

        public Request setType(String contentType)
        {
            this.contentType = contentType;
            return this;
        }
        
        public String getType()
        {
            return contentType;
        }
    }
    
    /**
     * HTTP GET Request
     */
    public static class GetRequest extends Request
    {
        public GetRequest(String uri)
        {
            super("get", uri);
        }
    }

    /**
     * HTTP HEAD request
     *
     */
    public static class HeadRequest extends Request
    {
        public HeadRequest(String uri)
        {
            super("get", uri);
        }
    }
    
    /**
     * HTTP POST Request
     */
    public static class PostRequest extends Request
    {
        public PostRequest(String uri, String post, String contentType)
            throws UnsupportedEncodingException 
        {
            super("post", uri);
            setBody(getEncoding() == null ? post.getBytes() : post.getBytes(getEncoding()));
            setType(contentType);
        }

        public PostRequest(String uri, byte[] post, String contentType)
        {
            super("post", uri);
            setBody(post);
            setType(contentType);
        }
    }
}

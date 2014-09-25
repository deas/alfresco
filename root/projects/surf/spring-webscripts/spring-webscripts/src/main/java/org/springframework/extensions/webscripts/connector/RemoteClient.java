/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.webscripts.connector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.springframework.extensions.surf.exception.WebScriptsPlatformException;
import org.springframework.extensions.surf.util.Base64;
import org.springframework.extensions.webscripts.ScriptRemote;

/**
 * Remote client bean for retrieving data from URL resources.
 * <p>
 * Can be used as a Script root object for HTTP methods via {@link ScriptRemote}
 * <p>
 * Generally remote URLs will be "data" webscripts (i.e. returning XML/JSON) called from
 * web-tier script objects or directly from Java backed webscript methods.
 * <p>
 * Support for HTTP methods of GET, DELETE, PUT and POST of body content data. The Apache
 * commons HttpClient library is used to provide superior handling of large POST body
 * rather than the default JDK implementation.
 * <p>
 * A 'Response' is returned containing the response data stream as a String and the Status
 * object representing the status code and error information if any. Methods supplying an
 * InputStream will force a POST and methods supplying an OutputStream will stream the result
 * directly to it (i.e. for a proxy) and will not generate a String response in the 'Response'
 * object.
 * <p>
 * By default this bean has the id of 'connector.remoteclient' and is configured in
 * spring-webscripts-application-context.xml found in the spring-webscripts project.
 * <p>
 * @version 5.0
 * Note since Alfresco 5.0 this was rewritten against Apache HttpClient 4.3
 * 
 * @author Kevin Roast
 */
public class RemoteClient extends AbstractClient implements Cloneable
{
    private static Log logger = LogFactory.getLog(RemoteClient.class);
    
    // HTTP headers
    protected static final String HEADER_TRANSFER_ENCODING  = "Transfer-Encoding";
    protected static final String HEADER_CONTENT_LENGTH     = "Content-Length";
    protected static final String HEADER_CONTENT_TYPE       = "Content-Type";
    protected static final String HEADER_SET_COOKIE         = "Set-Cookie";
    protected static final String HEADER_COOKIE             = "Cookie";
    protected static final String HEADER_SERVER             = "Server";
    
    // timeout values etc. can be modified in the spring config for this bean
    protected static final int DEFAULT_CONNECT_TIMEOUT  = 10000;    // 10 seconds
    protected static final int DEFAULT_READ_TIMEOUT     = 120000;   // 120 seconds
    protected static final int DEFAULT_BUFFERSIZE       = 4096;
    protected static final int DEFAULT_MAX_REDIRECTS    = 10;
    protected static final int DEFAULT_POOLSIZE         = 200;
    protected static final String DEFAULT_TICKET_NAME   = "alf_ticket";
    protected static final String DEFAULT_REQUEST_CONTENT_TYPE = "application/octet-stream";
    
    protected static final String X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    protected static final String CHARSETEQUALS = "charset=";
    
    private static final String XML_START = "<?xml";
    private static final Pattern XML_ENCODING = Pattern.compile("<\\?xml.*.encoding=\"([^\"]*)\"");
    private static final int XML_ENC_READ_LIMIT = 100;
    
    // HTTP client connection manager and proxy hosts
    private static PoolingHttpClientConnectionManager s_connectionManager;
    private static HttpHost s_httpProxyHost;
    private static HttpHost s_httpsProxyHost;
    
    // Stateful values (set programatically for each remote client instance)
    private Map<String, String> cookies;
    private String ticket;
    
    // Stateful values (set programatically for each connection request by the Connector framework)
    private String requestContentType = null;
    private HttpMethod requestMethod = HttpMethod.GET;
    
    // Authentication state - applied to each request if set
    private String username;
    private String password;
    private boolean commitResponseOnAuthenticationError = true;
    private boolean exceptionOnError = false;
    
    // Programmable request properties - overriding default proxied headers
    private Map<String, String> requestProperties;
    
    // Spring bean properties (set via config for each instance)
    // NOTE: must update clone() method below when new config properties are added
    private String ticketName = DEFAULT_TICKET_NAME;
    private String defaultEncoding = null;
    private String defaultContentType = DEFAULT_REQUEST_CONTENT_TYPE;
    private int bufferSize = DEFAULT_BUFFERSIZE;
    private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
    private int readTimeout = DEFAULT_READ_TIMEOUT;
    private int maxRedirects = DEFAULT_MAX_REDIRECTS;
    private int poolSize = DEFAULT_POOLSIZE;
    private boolean allowHttpProxy = true;
    private boolean allowHttpsProxy = true;
    private Set<String> removeRequestHeaders = Collections.<String>emptySet();
    private Set<String> removeResponseHeaders = Collections.<String>emptySet();
    private boolean httpTcpNodelay = true;
    private boolean httpConnectionStalecheck = true;
    
    // Redirect status codes
    public static final int SC_MOVED_TEMPORARILY    = 302;
    public static final int SC_MOVED_PERMANENTLY    = 301;
    public static final int SC_SEE_OTHER            = 303;
    public static final int SC_TEMPORARY_REDIRECT   = 307;
    
    
    /**
     * Initialise the static HTTP objects - Connection Manager and Proxy Hosts
     */
    static
    {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(DEFAULT_POOLSIZE);
        cm.setDefaultMaxPerRoute(DEFAULT_POOLSIZE);
        s_connectionManager = cm;
        
        // Create an HTTP Proxy Host if appropriate system property set
        s_httpProxyHost = createProxyHost("http.proxyHost", "http.proxyPort", 80);
        
        // Create an HTTPS Proxy Host if appropriate system property set
        s_httpsProxyHost = createProxyHost("https.proxyHost", "https.proxyPort", 443);
    }
    
    /**
     * Clone a RemoteClient and all the properties.
     * <p>
     * This method is preferable in hot code to requesting a new copy of the "connector.remoteclient"
     * bean from Spring - as the bean makes use of the prototype pattern and is quite expensive to
     * create each time - also the Spring code has synchronization during the bean creation pattern
     * which is additionally expensive during heavily threaded applications.
     * <p>
     * This clone method will only duplicate the non-stateful members of RemoteClient i.e. the
     * same properties that would have been set by Spring during bean initialisation.
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        RemoteClient clone = (RemoteClient)super.clone();
        clone.allowHttpProxy = this.allowHttpProxy;
        clone.allowHttpsProxy = this.allowHttpsProxy;
        clone.bufferSize = this.bufferSize;
        clone.connectTimeout = this.connectTimeout;
        clone.defaultContentType = this.defaultContentType;
        clone.defaultEncoding = this.defaultEncoding;
        clone.httpConnectionStalecheck = this.httpConnectionStalecheck;
        clone.httpTcpNodelay = this.httpTcpNodelay;
        clone.maxRedirects = this.maxRedirects;
        clone.readTimeout = this.readTimeout;
        clone.readTimeout = this.readTimeout;
        clone.removeRequestHeaders = (Set<String>)((HashSet<String>)this.removeRequestHeaders).clone();
        clone.removeResponseHeaders = (Set<String>)((HashSet<String>)this.removeResponseHeaders).clone();
        clone.ticketName = this.ticketName;
        clone.poolSize = this.poolSize;
        return clone;
    }
    
    
    /////////////////////////////////////////////////////////////////
    // Setters and Spring properties
    
    /**
     * Sets the authentication ticket name to use.  Will be used for all future call() requests.
     * 
     * This allows the ticket mechanism to be repurposed for non-Alfresco
     * implementations that may require similar argument passing
     * 
     * @param ticket
     */
    public void setTicketName(String ticketName)
    {
        this.ticketName = ticketName;
    }
    
    /**
     * @return the authentication ticket name to use
     */
    public String getTicketName()
    {
        return this.ticketName;
    }
    
    /**
     * @param defaultEncoding   the defaultEncoding to set
     */
    public void setDefaultEncoding(String defaultEncoding)
    {
        this.defaultEncoding = defaultEncoding;
    }

    /**
     * @param defaultContentType the defaultContentType to set
     */
    public void setDefaultContentType(String defaultContentType)
    {
        this.defaultContentType = defaultContentType;
    }

    /**
     * @param bufferSize        the bufferSize to set
     */
    public void setBufferSize(int bufferSize)
    {
        this.bufferSize = bufferSize;
    }

    /**
     * @param connectTimeout    the connectTimeout to set
     */
    public void setConnectTimeout(int connectTimeout)
    {
        this.connectTimeout = connectTimeout;
    }

    /**
     * @param readTimeout       the readTimeout to set
     */
    public void setReadTimeout(int readTimeout)
    {
        this.readTimeout = readTimeout;
    }

    /**
     * @param maxRedirects      the maxRedirects to set
     */
    public void setMaxRedirects(int maxRedirects)
    {
        this.maxRedirects = maxRedirects;
    }
    
    /**
     * @return the connection thread pool size
     */
    public int getPoolSize()
    {
        return this.poolSize;
    }

    /**
     * @param poolSize          the connection thread pool size to set
     */
    public void setPoolSize(int poolSize)
    {
        this.poolSize = poolSize;
        s_connectionManager.setMaxTotal(poolSize);
        s_connectionManager.setDefaultMaxPerRoute(poolSize);
    }

    /**
     * @param allowHttpProxy    allowHttpProxy to set
     */
    public void setAllowHttpProxy(boolean allowHttpProxy)
    {
        this.allowHttpProxy = allowHttpProxy;
    }

    /**
     * @param allowHttpsProxy   allowHttpsProxy to set
     */
    public void setAllowHttpsProxy(boolean allowHttpsProxy)
    {
        this.allowHttpsProxy = allowHttpsProxy;
    }

    /**
     * @param removeRequestHeaders the removeRequestHeaders to set
     */
    public void setRemoveRequestHeaders(Set<String> removeRequestHeaders)
    {
        if (removeRequestHeaders != null)
        {
            this.removeRequestHeaders = new HashSet<String>(removeRequestHeaders.size());
            for (String key : removeRequestHeaders)
            {
                this.removeRequestHeaders.add(key.toLowerCase());
            }
        }
    }

    /**
     * @param removeResponseHeaders the removeResponseHeaders to set
     */
    public void setRemoveResponseHeaders(Set<String> removeResponseHeaders)
    {
        if (removeResponseHeaders != null)
        {
            this.removeResponseHeaders = new HashSet<String>(removeResponseHeaders.size());
            for (String key : removeResponseHeaders)
            {
                this.removeResponseHeaders.add(key.toLowerCase());
            }
        }
    }

    /**
     * Sets the authentication ticket to use. Will be used for all future call() requests.
     * 
     * @param ticket
     */
    public void setTicket(String ticket)
    {
        this.ticket = ticket;
    }
    
    /**
     * Returns the authentication ticket
     * 
     * @return
     */
    public String getTicket()
    {
        return this.ticket;
    }

    /**
     * Basic HTTP auth. Will be used for all future call() requests.
     * 
     * @param user
     * @param pass
     */
    public void setUsernamePassword(String user, String pass)
    {
        this.username = user;
        this.password = pass;
    }

    /**
     * @param requestContentType     the POST request "Content-Type" header value to set
     *        NOTE: this value is reset to the defaultContentType value after a call() is made. 
     */
    public void setRequestContentType(String contentType)
    {
        this.requestContentType = contentType;
    }
    
    public String getRequestContentType()
    {
        if (this.requestContentType == null)
        {
            this.requestContentType = this.defaultContentType;
        }
        return this.requestContentType;
    }

    /**
     * @param requestMethod  the request Method to set i.e. one of GET/POST/PUT/DELETE etc.
     *        if not set, GET will be assumed unless an InputStream is supplied during call()
     *        in which case POST will be used unless the request method overrides it with PUT.
     *        NOTE: this value is reset to the default of GET after a call() is made. 
     */
    public void setRequestMethod(HttpMethod method)
    {
        if (method != null)
        {
            this.requestMethod = method;
        }
    }
    
    /**
     * @return the current Request Method
     */
    public HttpMethod getRequestMethod()
    {
        return this.requestMethod;
    }
    
    /**
     * Allows for additional request properties to be set onto this object
     * These request properties are applied to the connection when
     * the connection is called. Will be used for all future call() requests.
     * 
     * @param requestProperties
     */
    public void setRequestProperties(Map<String, String> requestProperties)
    {
        if (requestProperties != null)
        {
            this.requestProperties = new HashMap<String, String>(requestProperties.size());
            for (String key : requestProperties.keySet())
            {
                this.requestProperties.put(key.toLowerCase(), requestProperties.get(key));
            }
        }
    }
    
    /**
     * Provides a set of cookies for state transfer. This set of cookies is maintained through any redirects followed by
     * the client (e.g. redirect through SSO host).
     * 
     * @param cookies the cookies
     */
    public void setCookies(Map<String, String> cookies)
    {
        this.cookies = cookies;
    }
    
    /**
     * Gets the current set of cookies for state transfer. This set of cookies is maintained through any redirects
     * followed by the client (e.g. redirect through SSO host).
     * 
     * @return the cookies
     */
    public Map<String, String> getCookies()
    {
        return this.cookies;
    }
    
    /**
     * @param httpTcpNodelay  Value for the http.tcp.nodelay setting - default is true
     */
    public void setHttpTcpNodelay(boolean httpTcpNodelay)
    {
       this.httpTcpNodelay = httpTcpNodelay;
    }
    
    /**
     * @param httpConnectionStalecheck    Value for the http.connection.stalecheck setting - default is true
     */
    public void setHttpConnectionStalecheck(boolean httpConnectionStalecheck)
    {
       this.httpConnectionStalecheck = httpConnectionStalecheck;
    }
    
    /**
     * @param commitResponseOnAuthenticationError true to commit the response if a 401 error is returned, false otherwise.
     */
    public void setCommitResponseOnAuthenticationError(boolean commitResponseOnAuthenticationError)
    {
        this.commitResponseOnAuthenticationError = commitResponseOnAuthenticationError;
    }
    
    /**
     * @param exceptionOnError true to throw an exception on a server 500 response - else return 500 code
     * in the usual Response object.
     */
    public void setExceptionOnError(boolean exceptionOnError)
    {
        this.exceptionOnError = exceptionOnError;
    }
    
    
    /////////////////////////////////////////////////////////////////
    // Client execute methods

    /**
     * Call a remote WebScript uri. The endpoint as supplied in the constructor will be used
     * as the prefix for the full WebScript url.
     * 
     * This API is generally called from a script host.
     * 
     * @param uri     WebScript URI - for example /test/myscript?arg=value
     * 
     * @return Response object from the call {@link Response}
     */
    public Response call(String uri)
    {
        return call(uri, true, null);
    }
    
    /**
     * Call a remote WebScript uri, passing the supplied body as a POST request (unless the
     * request method is set to override as say PUT).
     * 
     * @param uri    Uri to call on the endpoint
     * @param body   Body of the POST request.
     * 
     * @return Response object from the call {@link Response}
     */
    public Response call(String uri, String body)
    {
        try
        {
            byte[] bytes = body.getBytes("UTF-8");
            return call(uri, true, new ByteArrayInputStream(bytes));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new WebScriptsPlatformException("Encoding not supported.", e);
        }
    }

    /**
     * Call a remote WebScript uri. The endpoint as supplied in the constructor will be used
     * as the prefix for the full WebScript url.
     * 
     * @param uri    WebScript URI - for example /test/myscript?arg=value
     * @param in     The optional InputStream to the call - if supplied a POST will be performed
     * 
     * @return Response object from the call {@link Response}
     */
    public Response call(String uri, InputStream in)
    {
        return call(uri, true, in);
    }

    /**
     * Call a remote WebScript uri. The endpoint as supplied in the constructor will be used
     * as the prefix for the full WebScript url.
     * 
     * @param uri    WebScript URI - for example /test/myscript?arg=value
     * @param buildResponseString   True to build a String result automatically based on the response
     *                              encoding, false to instead return the InputStream in the Response.
     * @param in     The optional InputStream to the call - if supplied a POST will be performed
     * 
     * @return Response object from the call {@link Response}
     */
    public Response call(String uri, boolean buildResponseString, InputStream in)
    {
        if (in != null)
        {
            // we have been supplied an input for the request - either POST or PUT
            if (this.requestMethod != HttpMethod.POST && this.requestMethod != HttpMethod.PUT)
            {
                this.requestMethod = HttpMethod.POST;
            }
        }
        
        Response result;
        ResponseStatus status = new ResponseStatus();
        try
        {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream(this.bufferSize);
            String encoding = service(buildURL(uri), in, bOut, status);
            if (buildResponseString)
            {
                String data;
                if (encoding != null)
                {
                    data = bOut.toString(encoding);
                }
                else
                {
                    data = (defaultEncoding != null ? bOut.toString(defaultEncoding) : bOut.toString());
                    // special case for XML files which contain the encoding with the XML descriptor line
                    if (data.startsWith(XML_START))
                    {
                        String searchXML = data;
                        if (data.length() > XML_ENC_READ_LIMIT)
                        {
                            searchXML = data.substring(0, XML_ENC_READ_LIMIT);
                        }
                        Matcher xmlMatcher = XML_ENCODING.matcher(searchXML);
                        if (xmlMatcher.find())
                        {
                            // found an encoding charset - process data again based on this encoding
                            data = bOut.toString(xmlMatcher.group(1));
                        }
                    }
                }
                result = new Response(data, status);
            }
            else
            {
                result = new Response(new ByteArrayInputStream(bOut.toByteArray()), status);
            }
            result.setEncoding(encoding);
        }
        catch (IOException ioErr)
        {
            if (logger.isInfoEnabled())
                logger.info("Error status " + status.getCode() + " " + status.getMessage(), ioErr);
            
            // error information already applied to Status object during service() call
            result = new Response(status);
        }
        catch (Throwable e)
        {
            if (logger.isDebugEnabled())
                logger.debug("Error status " + status.getCode() + " " + status.getMessage(), e);
            
            // error information already applied to Status object during service() call
            result = new Response(status);
        }

        return result;
    }

    /**
     * Call a remote WebScript uri. The endpoint as supplied in the constructor will be used
     * as the prefix for the full WebScript url.
     * 
     * @param uri    WebScript URI - for example /test/myscript?arg=value
     * @param out    OutputStream to stream successful response to - will be closed automatically.
     *               A response data string will not therefore be available in the Response object.
     *               If remote call fails the OutputStream will not be modified or closed.
     * 
     * @return Response object from the call {@link Response}
     */
    public Response call(String uri, OutputStream out)
    {
        return call(uri, null, out);
    }

    /**
     * Call a remote WebScript uri. The endpoint as supplied in the constructor will be used
     * as the prefix for the full WebScript url.
     * 
     * @param uri    WebScript URI - for example /test/myscript?arg=value
     * @param in     The optional InputStream to the call - if supplied a POST will be performed
     * @param out    OutputStream to stream response to - will be closed automatically.
     *               A response data string will not therefore be available in the Response object.
     *               If remote call returns a status code then any available error response will be
     *               streamed into the output.
     *               If remote call fails completely the OutputStream will not be modified or closed.
     * 
     * @return Response object from the call {@link Response}
     */
    public Response call(String uri, InputStream in, OutputStream out)
    {
        if (in != null)
        {
            // we have been supplied an input for the request - either POST or PUT
            if (this.requestMethod != HttpMethod.POST && this.requestMethod != HttpMethod.PUT)
            {
                this.requestMethod = HttpMethod.POST;
            }
        }
        
        Response result;
        ResponseStatus status = new ResponseStatus();
        try
        {
            String encoding = service(buildURL(uri), in, out, status);
            result = new Response(status);
            result.setEncoding(encoding);
        }
        catch (IOException ioErr)
        {
            if (logger.isInfoEnabled())
                logger.info("Error status " + status.getCode() + " " + status.getMessage(), ioErr);
            
            // error information already applied to Status object during service() call
            result = new Response(status);
        }
        catch (Throwable e)
        {
            if (logger.isDebugEnabled())
                logger.debug("Error status " + status.getCode() + " " + status.getMessage(), e);
            
            // error information already applied to Status object during service() call
            result = new Response(status);
        }

        return result;
    }

    /**
     * Call a remote WebScript uri. The endpoint as supplied in the constructor will be used
     * as the prefix for the full WebScript url.
     * 
     * @param uri    WebScript URI - for example /test/myscript?arg=value
     * @param req    HttpServletRequest the request to retrieve input and headers etc. from
     * @param res    HttpServletResponse the response to stream response to - will be closed automatically.
     *               A response data string will not therefore be available in the Response object.
     *               The HTTP method to be used should be set via the setter otherwise GET will be assumed
     *               and the InputStream will not be retrieve from the request.
     *               If remote call returns a status code then any available error response will be
     *               streamed into the response object. 
     *               If remote call fails completely the OutputStream will not be modified or closed.
     * 
     * @return Response object from the call {@link Response}
     */
    public Response call(String uri, HttpServletRequest req, HttpServletResponse res)
    {
        Response result;
        ResponseStatus status = new ResponseStatus();
        try
        {
            boolean isPush = (requestMethod == HttpMethod.POST || requestMethod == HttpMethod.PUT);
            String encoding = service(
                    buildURL(uri),
                    isPush ? req.getInputStream() : null,
                    res != null ? res.getOutputStream() : null,
                    req, res, status);
            result = new Response(status);
            result.setEncoding(encoding);
        }
        catch (IOException ioErr)
        {
            if (logger.isInfoEnabled())
                logger.info("Error status " + status.getCode() + " " + status.getMessage(), ioErr);
            
            // error information already applied to Status object during service() call
            result = new Response(status);
        }
        catch (Throwable e)
        {
            if (logger.isDebugEnabled())
                logger.debug("Error status " + status.getCode() + " " + status.getMessage(), e);
            
            // error information already applied to Status object during service() call
            result = new Response(status);
        }
        
        return result;
    }
    
    
    /////////////////////////////////////////////////////////////////
    // Response processing and helpers
    
    /**
     * Pre-processes the response, propagating cookies and deciding whether a redirect is required
     * 
     * @param url       URL that was executed
     * @param response  the executed HttpResponse from the method
     * @throws MalformedURLException
     */
    protected URL processResponse(URL url, HttpResponse response)
            throws MalformedURLException
    {
        String redirectLocation = null;
        for (Header header : response.getAllHeaders())
        {
            String headerName = header.getName();            
            if (this.cookies != null && headerName.equalsIgnoreCase(HEADER_SET_COOKIE))
            {
                String headerValue = header.getValue();
                
                int z = headerValue.indexOf('=');
                if (z != -1)
                {
                    String cookieName = headerValue.substring(0, z);
                    String cookieValue = headerValue.substring(z + 1, headerValue.length());
                    int y = cookieValue.indexOf(';');
                    if (y != -1)
                    {
                        cookieValue = cookieValue.substring(0, y);
                    }
                    
                    // store cookie back
                    if (logger.isDebugEnabled())
                        logger.debug("RemoteClient found Set-Cookie: " + cookieName + " = " + cookieValue);
                    
                    this.cookies.put(cookieName, cookieValue);
                }
            }
            if (headerName.equalsIgnoreCase("Location"))
            {
                switch (response.getStatusLine().getStatusCode())
                {
                    case RemoteClient.SC_MOVED_TEMPORARILY:
                    case RemoteClient.SC_MOVED_PERMANENTLY:
                    case RemoteClient.SC_SEE_OTHER:
                    case RemoteClient.SC_TEMPORARY_REDIRECT:
                        redirectLocation = header.getValue();
                }
            }
        }
        return redirectLocation == null ? null : new URL(url, redirectLocation);
    }
    
    /**
     * Build the URL object based on the supplied uri and configured endpoint. Ticket
     * will be appiled as an argument if available.
     * 
     * @param uri     URI to build URL against
     * 
     * @return the URL object representing the call.
     * 
     * @throws MalformedURLException
     */
    protected URL buildURL(final String uri) throws MalformedURLException
    {
        URL url;
        final String resolvedUri = uri.startsWith(endpoint) ? uri : endpoint + uri;
        if (getTicket() == null)
        {
            url = new URL(resolvedUri);
        }
        else
        {
            url = new URL(resolvedUri +
                    (uri.lastIndexOf('?') == -1 ? ("?"+getTicketName()+"="+getTicket()) : ("&"+getTicketName()+"="+getTicket())));
        }
        return url;
    }
    
    
    /////////////////////////////////////////////////////////////////
    // Underlying Client service methods

    /**
     * Service a remote URL and write the the result into an output stream.
     * If an InputStream is provided then a POST will be performed with the content
     * pushed to the url. Otherwise a standard GET will be performed.
     * 
     * @param url    The URL to open and retrieve data from
     * @param in     The optional InputStream - if set a POST will be performed
     * @param out    The OutputStream to write result to
     * @param status The status object to apply the response code too
     * 
     * @return encoding specified by the source URL - may be null
     * 
     * @throws IOException
     */
    private String service(URL url, InputStream in, OutputStream out, ResponseStatus status)
        throws IOException
    {
        return service(url, in, out, null, null, status);
    }

    /**
     * Service a remote URL and write the the result into an output stream.
     * If an InputStream is provided then a POST will be performed with the content
     * pushed to the url. Otherwise a standard GET will be performed.
     * 
     * @param url    The URL to open and retrieve data from
     * @param in     The optional InputStream - if set a POST or similar will be performed
     * @param out    The OutputStream to write result to
     * @param res    Optional HttpServletResponse - to which response headers will be copied - i.e. proxied
     * @param status The status object to apply the response code too
     * 
     * @return encoding specified by the source URL - may be null
     * 
     * @throws IOException
     */
    private String service(URL url, InputStream in, OutputStream out,
            HttpServletRequest req, HttpServletResponse res, ResponseStatus status)
        throws IOException
    {
        final boolean trace = logger.isTraceEnabled();
        final boolean debug = logger.isDebugEnabled();
        if (debug)
        {
            logger.debug("Executing " + "(" + requestMethod + ") " + url.toString());
            if (in != null)  logger.debug(" - InputStream supplied - will push...");
            if (out != null) logger.debug(" - OutputStream supplied - will stream response...");
            if (req != null && res != null) logger.debug(" - Full Proxy mode between servlet request and response...");
        }
        
        // aquire and configure the HttpClient
        HttpClient httpClient = createHttpClient(url);
        
        URL redirectURL = url;
        HttpResponse response;
        HttpRequestBase method = null;
        int retries = 0;
        // Only process redirects if we are not processing a 'push'
        int maxRetries = in == null ? this.maxRedirects : 1;
        try
        {
            do
            {
                // Release a previous method that we processed due to a redirect
                if (method != null)
                {
                    method.reset();
                    method = null;
                }
                
                switch (this.requestMethod)
                {
                    default:
                    case GET:
                        method = new HttpGet(redirectURL.toString());
                        break;
                    case PUT:
                        method = new HttpPut(redirectURL.toString());
                        break;
                    case POST:
                        method = new HttpPost(redirectURL.toString());
                        break;
                    case DELETE:
                        method = new HttpDelete(redirectURL.toString());
                        break;
                    case HEAD:
                        method = new HttpHead(redirectURL.toString());
                        break;
                    case OPTIONS:
                        method = new HttpOptions(redirectURL.toString());
                        break;
                }
                
                // proxy over any headers from the request stream to proxied request
                if (req != null)
                {
                    Enumeration<String> headers = req.getHeaderNames();
                    while (headers.hasMoreElements())
                    {
                        String key = headers.nextElement();
                        if (key != null)
                        {
                            key = key.toLowerCase();
                            if (!this.removeRequestHeaders.contains(key) &&
                                (this.requestProperties == null || !this.requestProperties.containsKey(key)))
                            {
                                method.setHeader(key, req.getHeader(key));
                                if (trace) logger.trace("Proxy request header: " + key + "=" + req.getHeader(key));
                            }
                        }
                    }
                }
                
                // apply request properties, allows for the assignment and override of specific header properties
                if (this.requestProperties != null && this.requestProperties.size() != 0)
                {
                    for (Map.Entry<String, String> entry : requestProperties.entrySet())
                    {
                        String headerName = entry.getKey();
                        String headerValue = this.requestProperties.get(headerName);
                        if (headerValue != null)
                        {
                           method.setHeader(headerName, headerValue);
                        }
                        if (trace) logger.trace("Set request header: " + headerName + "=" + headerValue);
                    }
                }
                
                // Apply cookies
                if (this.cookies != null && !this.cookies.isEmpty())
                {
                    StringBuilder builder = new StringBuilder(128);
                    for (Map.Entry<String, String> entry : this.cookies.entrySet())
                    {
                        if (builder.length() != 0)
                        {
                            builder.append(';');
                        }
                        builder.append(entry.getKey());
                        builder.append('=');
                        builder.append(entry.getValue());
                    }
                    
                    String cookieString = builder.toString();
                    
                    if (debug) logger.debug("Setting Cookie header: " + cookieString);
                    method.setHeader(HEADER_COOKIE, cookieString);
                }
                
                // HTTP basic auth support
                if (this.username != null && this.password != null)
                {
                    String auth = this.username + ':' + this.password;
                    method.addHeader("Authorization", "Basic " + Base64.encodeBytes(auth.getBytes(), Base64.DONT_BREAK_LINES));
                    if (debug) logger.debug("Applied HTTP Basic Authorization for user: " + this.username);
                }
                
                // prepare the POST/PUT entity data if input supplied
                if (in != null)
                {
                    method.setHeader(HEADER_CONTENT_TYPE, getRequestContentType());
                    if (debug) logger.debug("Set Content-Type=" + getRequestContentType());
                    
                    boolean urlencoded = getRequestContentType().startsWith(X_WWW_FORM_URLENCODED);
                    if (!urlencoded)
                    {
                        // apply content-length here if known (i.e. from proxied req)
                        // if this is not set, then the content will be buffered in memory
                        long contentLength = -1L;
                        if (req != null)
                        {
                            String contentLengthStr = req.getHeader(HEADER_CONTENT_LENGTH);
                            if (contentLengthStr != null)
                            {
                                try
                                {
                                    long actualContentLength = Long.parseLong(contentLengthStr); 
                                    if (actualContentLength > 0)
                                    { 
                                       contentLength = actualContentLength; 
                                    } 
                                }
                                catch (NumberFormatException e)
                                {
                                    logger.warn("Can't parse 'Content-Length' header from '" + contentLengthStr +
                                                "'. The contentLength is set to -1");
                                }
                            }
                        }
                        
                        if (debug) logger.debug(requestMethod + " entity Content-Length=" + contentLength);
                        
                        // remove the Content-Length header as the setEntity() method will perform this explicitly
                        method.removeHeaders(HEADER_CONTENT_LENGTH);
                        
                        try
                        {
                            // Apache doc for AbstractHttpEntity states:
                            // HttpClient must use chunk coding if the entity content length is unknown (== -1).
                            HttpEntity entity = new InputStreamEntity(in, contentLength);
                            ((HttpEntityEnclosingRequest)method).setEntity(
                                    contentLength == -1L || contentLength > 16384L ? entity : new BufferedHttpEntity(entity));
                            ((HttpEntityEnclosingRequest)method).setHeader(HTTP.EXPECT_DIRECTIVE, HTTP.EXPECT_CONTINUE);
                        }
                        catch (IOException e)
                        {
                            // During the creation of the BufferedHttpEntity the underlying stream can be closed by the client,
                            // this happens if the request is discarded by the browser - we don't log this IOException as INFO
                            // as that would fill the logs with unhelpful noise - enable DEBUG logging to see these messages.
                            throw new RuntimeException(e.getMessage(), e);
                        }
                    }
                    else
                    {
                        if (req != null)
                        {
                            // apply any supplied request parameters
                            Map<String, String[]> postParams = req.getParameterMap();
                            if (postParams != null)
                            {
                                List<NameValuePair> params = new ArrayList<NameValuePair>(postParams.size());
                                for (String key : postParams.keySet())
                                {
                                    String[] values = postParams.get(key);
                                    for (int i = 0; i < values.length; i++)
                                    {
                                        params.add(new BasicNameValuePair(key, values[i]));
                                    }
                                }
                            }
                            // ensure that the Content-Length header is not directly proxied - as the underlying
                            // HttpClient will encode the body as appropriate - cannot assume same as the original client sent
                            method.removeHeaders(HEADER_CONTENT_LENGTH);
                        }
                    }
                }
                
                // execute the method to get the response
                response = httpClient.execute(method);
                redirectURL = processResponse(redirectURL, response);
            }
            while (redirectURL != null && ++retries < maxRetries);
            
            // record the status code for the internal response object
            int responseCode = response.getStatusLine().getStatusCode(); 
            if (responseCode >= HttpServletResponse.SC_INTERNAL_SERVER_ERROR && this.exceptionOnError)
            {
                buildProxiedServerError(response);
            }
            boolean allowResponseCommit = (responseCode != HttpServletResponse.SC_UNAUTHORIZED || commitResponseOnAuthenticationError);
            status.setCode(responseCode);
            if (debug) logger.debug("Response status code: " + responseCode);
            
            // walk over headers that are returned from the connection
            // if we have a servlet response, push the headers back to the existing response object
            // otherwise, store headers on status
            Header contentType = null;
            Header contentLength = null;
            for (Header header : response.getAllHeaders())
            {
                // NOTE: Tomcat does not appear to be obeying the servlet spec here.
                //       If you call setHeader() the spec says it will "clear existing values" - i.e. not
                //       add additional values to existing headers - but for Server and Transfer-Encoding
                //       if we set them, then two values are received in the response...
                // In addition handle the fact that the key can be null.
                final String key = header.getName();
                if (key != null)
                {
                    if (!key.equalsIgnoreCase(HEADER_SERVER) && !key.equalsIgnoreCase(HEADER_TRANSFER_ENCODING))
                    {
                        if (res != null && allowResponseCommit && !this.removeResponseHeaders.contains(key.toLowerCase()))
                        {
                            res.setHeader(key, header.getValue());
                        }
                        
                        // store headers back onto status
                        status.setHeader(key, header.getValue());
                        
                        if (trace) logger.trace("Response header: " + key + "=" + header.getValue()); 
                    }
                    
                    // grab a reference to the the content-type header here if we find it
                    if (contentType == null && key.equalsIgnoreCase(HEADER_CONTENT_TYPE))
                    {
                        contentType = header;
                        // additional optional processing based on the Content-Type header
                        processContentType(url, res, contentType);
                    }
                    // grab a reference to the Content-Length header here if we find it
                    else if (contentLength == null && key.equalsIgnoreCase(HEADER_CONTENT_LENGTH))
                    {
                        contentLength = header;
                    }
                }
            }
            
            // locate response encoding from the headers
            String encoding = null;
            String ct = null;
            if (contentType != null)
            {
                ct = contentType.getValue();
                int csi = ct.indexOf(CHARSETEQUALS);
                if (csi != -1)
                {
                    encoding = ct.substring(csi + CHARSETEQUALS.length());
                    if ((csi = encoding.lastIndexOf(';')) != -1)
                    {
                        encoding = encoding.substring(0, csi);
                    }
                    if (debug) logger.debug("Response charset: " + encoding);
                }
            }
            if (debug) logger.debug("Response encoding: " + contentType);
            
            // generate container driven error message response for specific response codes
            if (res != null && responseCode == HttpServletResponse.SC_UNAUTHORIZED && allowResponseCommit)
            {
                res.sendError(responseCode, response.getStatusLine().getReasonPhrase());
            }
            else
            {
                // push status to existing response object if required
                if (res != null && allowResponseCommit)
                {
                    res.setStatus(responseCode);
                }
                // perform the stream write from the response to the output
                int bufferSize = this.bufferSize;
                if (contentLength != null)
                {
                    long length = Long.parseLong(contentLength.getValue());
                    if (length < bufferSize)
                    {
                        bufferSize = (int)length;
                    }
                }
                copyResponseStreamOutput(url, res, out, response, ct, bufferSize);
            }
            
            // if we get here call was successful
            return encoding;
        }
        catch (ConnectTimeoutException|SocketTimeoutException timeErr)
        {
            // caught a socket timeout IO exception - apply internal error code
            logger.info("Exception calling (" + requestMethod + ") " + url.toString());
            status.setCode(HttpServletResponse.SC_REQUEST_TIMEOUT);
            status.setException(timeErr);
            status.setMessage(timeErr.getMessage());
            if (res != null)
            {
                //return a Request Timeout error
                res.setStatus(HttpServletResponse.SC_REQUEST_TIMEOUT, timeErr.getMessage());
            }
            
            throw timeErr;
        }
        catch (UnknownHostException|ConnectException hostErr)
        {
            // caught an unknown host IO exception 
            logger.info("Exception calling (" + requestMethod + ") " + url.toString());
            status.setCode(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            status.setException(hostErr);
            status.setMessage(hostErr.getMessage());
            if (res != null)
            {
                // return server error code
                res.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE, hostErr.getMessage());
            }
            
            throw hostErr;
        }
        catch (IOException ioErr)
        {
            // caught a general IO exception - apply generic error code so one gets returned
            logger.info("Exception calling (" + requestMethod + ") " + url.toString());
            status.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            status.setException(ioErr);
            status.setMessage(ioErr.getMessage());
            if (res != null)
            {
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ioErr.getMessage());
            }
            
            throw ioErr;
        }
        catch (RuntimeException e)
        {
            // caught an exception - apply generic error code so one gets returned
            logger.debug("Exception calling (" + requestMethod + ") " + url.toString());
            status.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            status.setException(e);
            status.setMessage(e.getMessage());
            if (res != null)
            {
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
            
            throw e;
        }
        finally
        {
            // reset state values
            if (method != null)
            {
                method.releaseConnection();
            }
            setRequestContentType(null);
            this.requestMethod = HttpMethod.GET;
        }
    }

    /**
     * Copy response stream to the output
     * 
     * @param url           The URL object that the response was retrieved from
     * @param res           The HttpServletResponse (can be null for in-memory response processing)
     * @param out           The OutputStream to use
     * @param response      The HttpResponse from the Method that was executed - will retrieve entity as stream
     * @param contentType   The ContentType value of the response 
     * @param bufferSize    The buffer size to use
     * @throws IOException
     */
    protected void copyResponseStreamOutput(URL url, HttpServletResponse res, OutputStream out,
            HttpResponse response, String contentType, int bufferSize)
        throws IOException
    {
        boolean responseCommit = false;
        final boolean trace = logger.isTraceEnabled();
        StringBuilder traceBuf = null;
        if (trace)
        {
            traceBuf = new StringBuilder(bufferSize);
        }
        // output response body for 200 range response code
        if (response.getEntity() != null)
        {
            final InputStream input = response.getEntity().getContent();
            try
            {
                final byte[] buffer = new byte[bufferSize];
                int read = input.read(buffer);
                if (read != -1) responseCommit = true;
                while (read != -1)
                {
                    if (out != null)
                    {
                        out.write(buffer, 0, read);
                    }
                    
                    if (trace)
                    {
                        if (contentType != null && (contentType.startsWith("text/") || contentType.startsWith("application/json")))
                        {
                            traceBuf.append(new String(buffer, 0, read));
                        }
                    }
                    
                    read = input.read(buffer);
                }
            }
            finally
            {
                if (trace && traceBuf.length() != 0)
                {
                    logger.trace("Output (" + (traceBuf.length()) + " bytes) from: " + url.toString());
                    logger.trace(traceBuf.toString());
                }
                try
                {
                    try
                    {
                        input.close();
                    }
                    finally
                    {
                        if (responseCommit)
                        {
                            if (out != null)
                            {
                                out.close();
                            }
                        }
                    }
                }
                catch (IOException e)
                {
                    if (logger.isWarnEnabled())
                        logger.warn("Exception during close() of HTTP API connection", e);
                }
            }
        }
    }
    
    /**
     * Construct and throw an exception to represent a 500 server error from the endpoint.
     * Ensures the container will use the appropriate error page handler for the result.
     */
    private void buildProxiedServerError(HttpResponse response)
    {
        boolean gzip = false;
        for (Header header : response.getAllHeaders())
        {
            if (header.getName().equalsIgnoreCase("Content-Encoding"))
            {
                gzip = (header.getValue().contains("gzip"));
            }
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream(512);
        try
        {
            if (response.getEntity() != null)
            {
                InputStream input = response.getEntity().getContent();
                if (gzip)
                {
                    input = new GZIPInputStream(input);
                }
                try
                {
                    byte[] buffer = new byte[bufferSize];
                    int read = input.read(buffer);
                    while (read != -1)
                    {
                        if (out != null)
                        {
                            out.write(buffer, 0, read);
                        }
                        
                        read = input.read(buffer);
                    }
                }
                finally
                {
                    try
                    {
                        input.close();
                    }
                    catch (IOException e)
                    {
                        // ignore result
                    }
                }
            }
        }
        catch (IOException ioErr)
        {
            // result result is OK if this happens
        }
        
        // build the exception which the container will then deal with
        try
        {
            throw new WebScriptsPlatformException(out.toString("UTF-8"));
        }
        catch (UnsupportedEncodingException e)
        {
        }
    }
    
    /**
     * Optional additional processing based on the contentType header
     * 
     * @param url           Source URL that was requested
     * @param res           The response (unprocessed as yet)
     * @param contentType   Content-Type header from the response
     */
    protected void processContentType(URL url, HttpServletResponse res, Header contentType)
    {
    }
    
    
    /////////////////////////////////////////////////////////////////
    // HTTPClient and Proxy creation methods
    
    /**
     * Create and configure an HttpClient per thread based on Pooled connection manager.
     * Proxy route will be applied the client based on current settings.
     * 
     * @param url
     * @return HttpClient
     */
    protected HttpClient createHttpClient(URL url)
    {
        // use the appropriate HTTP proxy host if required
        HttpRoutePlanner routePlanner = null;
        if (s_httpProxyHost != null && this.allowHttpProxy &&
            url.getProtocol().equals("http") && requiresProxy(url.getHost()))
        {
            routePlanner = new DefaultProxyRoutePlanner(s_httpProxyHost);
            if (logger.isDebugEnabled()) logger.debug(" - using HTTP proxy host for: " + url);
        }
        else if (s_httpsProxyHost != null && this.allowHttpsProxy &&
                 url.getProtocol().equals("https") && requiresProxy(url.getHost()))
        {
            routePlanner = new DefaultProxyRoutePlanner(s_httpsProxyHost);
            if (logger.isDebugEnabled()) logger.debug(" - using HTTPS proxy host for: " + url);
        }
        
        return HttpClientBuilder.create()
            .setConnectionManager(s_connectionManager)
            .setRoutePlanner(routePlanner)
            .setRedirectStrategy(new RedirectStrategy() {
                // Switch off automatic redirect handling as we want to process them ourselves and maintain cookies
                public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context)
                        throws ProtocolException
                {
                    return false;
                }
                public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context)
                        throws ProtocolException
                {
                    return null;
                }
            }).setDefaultRequestConfig(RequestConfig.custom()
                .setStaleConnectionCheckEnabled(httpConnectionStalecheck)
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(readTimeout)
                .build())
            .build();
        
        // TODO: this appears to have vanished from the config that can be set since httpclient 3.1->4.3
        //params.setBooleanParameter("http.tcp.nodelay", httpTcpNodelay);
    }
    
    /**
     * Create HTTP proxy host for the given system host and port properties.
     * If the properties are not set, no proxy will be created.
     * 
     * @param hostProperty
     * @param portProperty
     * @param defaultPort
     * 
     * @return HttpHost if appropriate properties have been set, null otherwise
     */
    protected static HttpHost createProxyHost(final String hostProperty, final String portProperty, final int defaultPort)
    {
        final String proxyHost = System.getProperty(hostProperty);
        HttpHost proxy = null;
        if (proxyHost != null && proxyHost.length() != 0)
        {
            final String strProxyPort = System.getProperty(portProperty);
            if (strProxyPort == null || strProxyPort.length() == 0)
            {
                proxy = new HttpHost(proxyHost, defaultPort);
            }
            else
            {
                proxy = new HttpHost(proxyHost, Integer.parseInt(strProxyPort));
            }
            if (logger.isDebugEnabled())
                logger.debug("ProxyHost: " + proxy.toString());
        }
        return proxy;
    }
    
    /**
     * Return true unless the given target host is specified in the <code>http.nonProxyHosts</code> system property.
     * See http://download.oracle.com/javase/1.4.2/docs/guide/net/properties.html
     * @param targetHost    Non-null host name to test
     * @return true if not specified in list, false if it is specifed and therefore should be excluded from proxy
     */
    private boolean requiresProxy(final String targetHost)
    {
        boolean requiresProxy = true;
        final String nonProxyHosts = System.getProperty("http.nonProxyHosts");
        if (nonProxyHosts != null)
        {
            StringTokenizer tokenizer = new StringTokenizer(nonProxyHosts, "|");
            while (tokenizer.hasMoreTokens())
            {
                String pattern = tokenizer.nextToken();
                pattern = pattern.replaceAll("\\.", "\\\\.").replaceAll("\\*", ".*");
                if (targetHost.matches(pattern))
                {
                    requiresProxy = false;
                    break;
                }
            }
        }
        return requiresProxy;
    }
}

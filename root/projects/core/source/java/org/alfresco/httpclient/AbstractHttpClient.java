/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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

import java.io.IOException;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractHttpClient implements AlfrescoHttpClient
{
    private static final Log logger = LogFactory.getLog(HttpsClient.class);
    
    // Remote Server access
    protected HttpClient httpClient = null;
//    protected String alfrescoHost;
//    protected int alfrescoPort;

    public AbstractHttpClient(HttpClient httpClient/*, String alfrescoHost, int alfrescoPort*/)
    {
//    	this.alfrescoHost = alfrescoHost;
//    	this.alfrescoPort = alfrescoPort;
    	this.httpClient = httpClient;
    }

//    public AbstractHttpClient(HttpClientFactory httpClientFactory, String alfrescoHost, int alfrescoPort)
//    {
//    	this.httpClientFactory = httpClientFactory;
//    	this.alfrescoHost = alfrescoHost;
//    	this.alfrescoPort = alfrescoPort;
//    	this.httpClient = httpClientFactory.getHttpClient(alfrescoHost, alfrescoPort);
//
////        HttpClientParams params = httpClient.getParams();
////        params.setBooleanParameter("http.tcp.nodelay", true);
////        params.setBooleanParameter("http.connection.stalecheck", false);
//    }
    
    protected HttpClient getHttpClient()
    {
        return httpClient;
    }
    
    /**
     * Send Request to the repository
     */
    protected HttpMethod sendRemoteRequest(Request req) throws AuthenticationException, IOException
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("");
            logger.debug("* Request: " + req.getMethod() + " " + req.getFullUri() + (req.getBody() == null ? "" : "\n" + new String(req.getBody(), "UTF-8")));
        }

    	HttpMethod method = createMethod(req);

        // execute method
        executeMethod(method);

        if(method.getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY || method.getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY)
        {
	        Header locationHeader = method.getResponseHeader("location");
	        if (locationHeader != null)
	        {
	            String redirectLocation = locationHeader.getValue();
	            method.setURI(new URI(redirectLocation, true));
	            httpClient.executeMethod(method);
	        }
        }
        
        // Deal with redirect e.g. for SSL
/*        if(method.getStatusCode() == 301 || method.getStatusCode() == 302 || method.getStatusCode() == 303 || method.getStatusCode() == 307)
        {
	        Header locationHeader = method.getResponseHeader("location");
	        if (locationHeader != null)
	        {
	            String redirectLocation = locationHeader.getValue();
	            method.setURI(new URI(redirectLocation, true));
	            executeMethod(method);
	        }
	        else
	        {
	            // The response is invalid and did not provide the new location for
	            // the resource.  Report an error or possibly handle the response
	            // like a 404 Not Found error.
	        	throw new AlfrescoRuntimeException("Invalid location in HTTP redirect");
	        }
        }*/

    	// check that the request returned with an ok status
//    	if(method.getStatusCode() == HttpStatus.SC_UNAUTHORIZED)
//    	{
//    		throw new AuthenticationException(method);
//    	}

        return method;
    }
    
    protected long executeMethod(HttpMethod method) throws HttpException, IOException
    {
        // execute method

        long startTime = System.currentTimeMillis();

        // TODO: Pool, and sent host configuration and state on execution
        getHttpClient().executeMethod(method);

        return System.currentTimeMillis() - startTime;
    }

    protected HttpMethod createMethod(Request req) throws IOException
    {
    	StringBuilder url = new StringBuilder();
//    	url.append("http://");
//    	url.append(alfrescoHost);
//    	url.append(":");
//    	url.append(alfrescoPort);
    	url.append("/alfresco/service/");
    	url.append(req.getFullUri());

        // construct method
        HttpMethod httpMethod = null;
        String method = req.getMethod();
        if(method.equalsIgnoreCase("GET"))
        {
            GetMethod get = new GetMethod(url.toString());
            httpMethod = get;
            httpMethod.setFollowRedirects(true);
        }
        else if(method.equalsIgnoreCase("POST"))
        {
            PostMethod post = new PostMethod(url.toString());
            httpMethod = post;
    		ByteArrayRequestEntity requestEntity = new ByteArrayRequestEntity(req.getBody(), req.getType());
	        post.setRequestEntity(requestEntity);
            // Note: not able to automatically follow redirects for POST, this is handled by sendRemoteRequest
        }
        else if(method.equalsIgnoreCase("HEAD"))
        {
            HeadMethod head = new HeadMethod(url.toString());
            httpMethod = head;
            httpMethod.setFollowRedirects(true);
        }
        else
        {
            throw new AlfrescoRuntimeException("Http Method " + method + " not supported");
        }

        if (req.getHeaders() != null)
        {
            for (Map.Entry<String, String> header : req.getHeaders().entrySet())
            {
                httpMethod.setRequestHeader(header.getKey(), header.getValue());
            }
        }
        
        return httpMethod;
    }

}

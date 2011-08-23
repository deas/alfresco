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

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A secure client connection to the repository.
 * 
 * @since 4.0
 *
 */
// TODO put ssl keystore inside jar file instead of on the filesystem?
public class HttpsClient //extends AbstractHttpClient
{
/*    private static final Log logger = LogFactory.getLog(HttpsClient.class);

    public HttpsClient(HttpClientFactory httpClientFactory, String alfrescoHost, int alfrescoPort)
    {
    	super(httpClientFactory, alfrescoHost, alfrescoPort);

//    	ProtocolSocketFactory socketFactory = getSSLProtocolSocketFactory();
//        Protocol myhttps = new Protocol("https", socketFactory, alfrescoPort);
//        httpClient.getHostConfiguration().setHost(alfrescoHost, alfrescoPort, myhttps);
        
//    	httpClient.getHostConfiguration().setHost(alfrescoHost, alfrescoPort);
        //httpClient.getParams().setParameter("http.socket.timeout", new Integer(0));


//        HttpClientParams params = httpClient.getParams();
//        params.setBooleanParameter("http.tcp.nodelay", true);
//        params.setBooleanParameter("http.connection.stalecheck", false);
    }

   protected HttpMethod createMethod(Request req) throws IOException
    {
    	HttpMethod method = super.createMethod(req);

//    	if(req.getMethod().equalsIgnoreCase("POST"))
//    	{
//    		ByteArrayRequestEntity requestEntity = new ByteArrayRequestEntity(req.getBody(), req.getType());
//	        ((PostMethod)method).setRequestEntity(requestEntity);
//    	}
//    	else
//    	{
//    		method.setFollowRedirects(true);
//    	}

    	return method;
	}*/

    /**
     * Send Request to the repository
     */
/*    public Response sendRequest(Request req) throws AuthenticationException, IOException
    {
    	HttpMethod method = super.sendRemoteRequest(req);
    	return new HttpMethodResponse(method);
    }
    
    protected HttpMethod sendRemoteRequest(Request req) throws AuthenticationException, IOException
    {
    	return super.sendRemoteRequest(req);
    }*/
}

/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
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
package org.alfresco.wcm.client.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class WebScriptCallerImpl implements WebScriptCaller
{
    static Log log = LogFactory.getLog(WebScriptCallerImpl.class);

    private static ThreadLocal<byte[]> localBuffer = new ThreadLocal<byte[]>()
    {
        @Override
        protected byte[] initialValue()
        {
            return new byte[1024];
        }
    };

    private String baseUrl;
    HttpClient httpClient;
    private AuthScope authScope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM);
    private Credentials credentials;

    public WebScriptCallerImpl()
    {
        httpClient = new HttpClient();
        httpClient.setHttpConnectionManager(new MultiThreadedHttpConnectionManager());
    }

    /**
     * Method that allows the default HttpClient instance to be replaced with one that is configured differently
     * @param httpClient
     */
    public void setHttpClient(HttpClient httpClient)
    {
        this.httpClient = httpClient;
    }

    public void setBaseUrl(URI serviceLocation)
    {
        this.baseUrl = serviceLocation.toString();
        if (!baseUrl.endsWith("/"))
        {
            baseUrl += "/";
        }
    }

    public void setBaseUrl(String serviceLocation) throws URISyntaxException
    {
        setBaseUrl(new URI(serviceLocation));
    }

    public void setCredentials(Credentials credentials)
    {
        this.credentials = credentials;
    }

    public void setAuthScope(AuthScope authScope)
    {
        this.authScope = authScope;
    }

    public void init()
    {
        // Set the credentials and authentication scope on the HTTP client
        // instance
        this.httpClient.getState().setCredentials(authScope, credentials);
    }

    /* (non-Javadoc)
     * @see org.alfresco.wcm.client.impl.WebScriptCaller#getJsonObject(java.lang.String, java.util.List)
     */
    public JSONObject getJsonObject(String servicePath, List<WebscriptParam> params)
    {
        JSONObject jsonObject = null;

        GetMethod getMethod = getGETMethod(servicePath, params);

        String responseText = null;
        try
        {
            httpClient.executeMethod(getMethod);
            if (getMethod.getStatusCode() == 200)
            {
                jsonObject = new JSONObject(new JSONTokener(
                        new InputStreamReader(getMethod.getResponseBodyAsStream(), "UTF-8")));
            }
            else
            {
                discardResponse(getMethod);
            }
        }
        catch (RuntimeException ex)
        {
            log.error("Rethrowing runtime exception.", ex);
            throw ex;
        }
        catch (JSONException ex)
        {
            log.error("Failed to parse response from Alfresco:\n " + responseText);
        }
        catch (Exception ex)
        {
            log.error("Failed to make request to Alfresco web script", ex);
        }
        finally
        {
            getMethod.releaseConnection();
        }
        return jsonObject;
    }
    
    public void get(String servicePath, WebscriptResponseHandler handler, List<WebscriptParam> params)
    {
        GetMethod getMethod = getGETMethod(servicePath, params);
        try
        {
            httpClient.executeMethod(getMethod);
            if (getMethod.getStatusCode() == 200)
            {
                handler.handleResponse(getMethod.getResponseBodyAsStream());
            }
            else
            {
                // Must read the response, even though we don't use it
                discardResponse(getMethod);
            }
        }
        catch (RuntimeException ex)
        {
            log.error("Rethrowing runtime exception.", ex);
            throw ex;
        }
        catch (Exception ex)
        {
            log.error("Failed to make request to Alfresco web script", ex);
        }
        finally
        {
            getMethod.releaseConnection();
        }
    }
    
    public void post(String servicePath, WebscriptResponseHandler handler, List<WebscriptParam> params)
    {
        PostMethod postMethod = getPOSTMethod(servicePath, params);
        try
        {
            httpClient.executeMethod(postMethod);
            if (postMethod.getStatusCode() == 200)
            {
                handler.handleResponse(postMethod.getResponseBodyAsStream());
            }
            else
            {
                // Must read the response, even though we don't use it
                discardResponse(postMethod);
            }
        }
        catch (RuntimeException ex)
        {
            log.error("Rethrowing runtime exception.", ex);
            throw ex;
        }
        catch (Exception ex)
        {
            log.error("Failed to make request to Alfresco web script", ex);
        }
        finally
        {
            postMethod.releaseConnection();
        }
    }

    void discardResponse(HttpMethod getMethod) throws IOException
    {
        if (log.isDebugEnabled())
        {
            log.debug("Received non-OK response when invoking GET method on path " + getMethod.getPath() + 
                    ". Response was:\n" + getMethod.getResponseBodyAsString());
        }
        else
        {
            byte[] buf = localBuffer.get();
            InputStream responseStream = getMethod.getResponseBodyAsStream();
            while (responseStream.read(buf) != -1);
        }
    }

    GetMethod getGETMethod(String servicePath, List<WebscriptParam> params)
    {
        GetMethod getMethod = new GetMethod(this.baseUrl + servicePath);

        if (params != null)
        {
            List<NameValuePair> args = new ArrayList<NameValuePair>();
            for (WebscriptParam param : params)
            {
                args.add(new NameValuePair(param.getName(), param.getValue()));
            }
            getMethod.setQueryString(args.toArray(new NameValuePair[args.size()]));
        }
        return getMethod;
    }

    PostMethod getPOSTMethod(String servicePath, List<WebscriptParam> params)
    {
        PostMethod postMethod = new PostMethod(this.baseUrl + servicePath);

        if (params != null)
        {
            List<NameValuePair> args = new ArrayList<NameValuePair>();
            for (WebscriptParam param : params)
            {
                args.add(new NameValuePair(param.getName(), param.getValue()));
            }
            postMethod.addParameters(args.toArray(new NameValuePair[args.size()]));
        }
        return postMethod;
    }

    @Override
    public JSONObject getJsonObject(String servicePath, WebscriptParam... params)
    {
        return getJsonObject(servicePath, Arrays.asList(params));
    }

    @Override
    public void get(String servicePath, WebscriptResponseHandler handler, WebscriptParam... params)
    {
        get(servicePath, handler, Arrays.asList(params));
    }

}

package org.alfresco.share.util.api;

import java.io.IOException;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.HttpUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class AlfrescoHttpClient extends AbstractUtils
{
    private static final int TIMEOUT_MILLISEC = (int) refreshDuration; // = 10 seconds
    private static Log logger = LogFactory.getLog(AlfrescoHttpClient.class);
    // private AlfrescoHttpClient alfrescoHttpClient = new AlfrescoHttpClient();

    public AlfrescoHttpClient() throws Exception
    {
        //alfrescoHttpClient = this.alfrescoHttpClient;
    }
    
    public static String getHeaderKey()
    {
        if (HEADER_KEY.isEmpty())
        {
            return null;
        }
        return HEADER_KEY;
    }

    @SuppressWarnings("finally")
    public static HttpClient getHttpClientWithBasicAuth(String apiUrl, String username, String password)
    {
        if (null == apiUrl || apiUrl.isEmpty())
        {
            throw new UnsupportedOperationException("URL should not be null.");
        }

        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
        HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);

        DefaultHttpClient httpclient = new DefaultHttpClient(httpParams);

        HttpResponse response = null;

        try
        {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(new AuthScope(AuthScope.ANY), new UsernamePasswordCredentials(username, password));
            httpclient.setCredentialsProvider(credentialsProvider);

            HttpGet httpget = new HttpGet(apiUrl);
            HttpContext httpContext = new BasicHttpContext();

            response = httpclient.execute(httpget, httpContext);
            if (response.getStatusLine().getStatusCode() == org.apache.http.HttpStatus.SC_OK)
            {
                if (logger.isTraceEnabled())
                {
                    logger.info("Http Client with Basic Auth: Success");
                }
            }
        }
        catch (IOException ioe)
        {
            String msg = String.format("Unable to obtain http client with given credentials,%n "
                    + "    Response failed when posting to %s Username: %s Password: %s", apiUrl, username, password);
            throw new RuntimeException(msg, ioe);
        }
        finally
        {

            if (response != null)
            {
                try
                {
                    EntityUtils.consume(response.getEntity());
                }
                catch (IOException ioe)
                {

                }
            }
            return httpclient;
        }
    }

    protected static void releaseResources(HttpRequestBase request, HttpResponse response)
    {
        if (request != null)
        {
            if (request instanceof HttpEntityEnclosingRequest)
            {
                HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) request;
                // Consume entity to completion
                if (entityRequest.getEntity() != null)
                {
                    HttpEntity entity = entityRequest.getEntity();
                    try
                    {
                        EntityUtils.consume(entity);
                    }
                    catch (IOException e)
                    {
                    }
                }
            }
            // Release connection
            try
            {
                request.reset();
            }
            catch (Exception e)
            {
            }
        }
        if (response != null)
        {
            HttpEntity entity = response.getEntity();
            try
            {
                EntityUtils.consume(entity);
            }
            catch (IOException e)
            {
            }
        }
    }

    public static HttpPost generatePostRequest(String requestURL, String[] headers, String[] reqBody) throws Exception
    {
        boolean setRequestHeaders = false;
        boolean setRequestBody = false;

        // Parameters check
        if (requestURL.isEmpty())
        {
            throw new IllegalArgumentException("Empty Request URL: Please correct");
        }

        HttpPost request = new HttpPost(requestURL);

        setRequestHeaders = canSetHeaderOrBody(headers);

        setRequestBody = canSetHeaderOrBody(reqBody);

        // Headers
        if (setRequestHeaders)
        {
            for (int i = 0; i < headers.length; i = i + 2)
            {
                request.addHeader(headers[i], headers[i + 1]);
            }
        }

        // Body
        if (setRequestBody)
        {
            JSONObject json = new JSONObject();

            for (int i = 0; i < reqBody.length; i = i + 2)
            {
                json.put(reqBody[i], reqBody[i + 1]);
            }
            logger.info("Message Body: " + json);
            request.setEntity(HttpUtil.setMessageBody(json));
        }

        return request;
    }

    public static HttpPost generatePostRequest(String requestURL, String[] headers, JSONObject reqBody) throws Exception
    {
        boolean setRequestHeaders = false;

        // Parameters check
        if (requestURL.isEmpty())
        {
            throw new IllegalArgumentException("Empty Request URL: Please correct");
        }

        HttpPost request = new HttpPost(requestURL);

        // Headers
        setRequestHeaders = canSetHeaderOrBody(headers);
        if (setRequestHeaders)
        {
            for (int i = 0; i < headers.length; i = i + 2)
            {
                request.addHeader(headers[i], headers[i + 1]);
            }
        }

        // Body
        request.setEntity(HttpUtil.setMessageBody(reqBody));

        return request;
    }

    public static HttpPut generatePutRequest(String requestURL, String[] headers, String[] reqBody) throws Exception
    {
        boolean setRequestHeaders = false;
        boolean setRequestBody = false;

        // Parameters check
        if (requestURL == null || requestURL.isEmpty())
        {
            throw new IllegalArgumentException("Null Request URL: Please correct");
        }

        HttpPut request = new HttpPut(requestURL);

        try
        {
            // Key-Value pair for headers
            setRequestHeaders = canSetHeaderOrBody(headers);

            // Key-Value pair for body
            setRequestBody = canSetHeaderOrBody(reqBody);

            // Headers
            if (setRequestHeaders)
            {
                for (int i = 0; i < headers.length - 1; i = i + 2)
                {
                    request.addHeader(headers[i], headers[i + 1]);
                }
            }

            // Body
            if (setRequestBody)
            {
                JSONObject json = new JSONObject();

                for (int i = 0; i < reqBody.length - 1; i = i + 2)
                {
                    json.put(reqBody[i], reqBody[i + 1]);
                }
                request.setEntity(HttpUtil.setMessageBody(json));
            }
        }
        catch (Exception e)
        {
            //String msg = String.format("Unable to generate request to URL: %s ", requestURL);
            throw new RuntimeException("Unable to generate request to URL: " + requestURL, e);
        }

        return request;
    }

    public static HttpGet generateGetRequest(String requestURL, String[] headers) throws Exception
    {
        boolean setRequestHeaders = false;

        // Parameters check
        if (requestURL.isEmpty())
        {
            throw new IllegalArgumentException("Empty Request URL: Please correct");
        }
        
        setRequestHeaders = canSetHeaderOrBody(headers);
        
        HttpGet request = new HttpGet(requestURL);
        logger.info(requestURL);
        // Headers
        if (setRequestHeaders)
        {
            for (int i = 0; i < headers.length; i = i + 2)
            {
                request.addHeader(headers[i], headers[i + 1]);
            }
        }

        return request;
    }

    public static String getParameter(String entity, String paramName, String response) throws Exception
    {
        String key = "";
        String result = null;

        try
        {
            JSONObject json = new JSONObject(response);
            JSONArray jArray = json.optJSONArray(entity);
            if (null == jArray)
            {
                JSONObject object = json.optJSONObject(entity);
                key = object.getString(paramName);
            }
            else
            {
                JSONObject object = jArray.getJSONObject(0);
                key = object.getString(paramName);
            }
            result = key;

        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to get Parameter:" + paramName, e);
        }

        return result;
    }

    public static HttpEntity executeRequest(HttpClient client, HttpPost request) throws Exception
    {
        HttpEntity entityResponse = null;
        HttpResponse response = null;
        
        try
        {
            response = client.execute(request);
            logger.info("Status Received:" + response.getStatusLine());
            entityResponse = response.getEntity();
        }
        catch (Exception e)
        {
            logger.error(response);
            throw new RuntimeException("Error during execute request", e);
        }

        return entityResponse;
    }

    public static HttpEntity executeRequest(HttpClient client, HttpGet request) throws Exception
    {
        HttpEntity entityResponse = null;
        HttpResponse response = null;

        try
        {
            response = client.execute(request);
            logger.info("Status Received:" + response.getStatusLine());
            entityResponse = response.getEntity();
        }
        catch (Exception e)
        {
            logger.error(response);
            throw new RuntimeException("Error during execute request", e);
        }

        return entityResponse;
    }

    public static HttpResponse executeRequestHttpResp(HttpClient client, HttpRequestBase request) throws Exception
    {
        HttpResponse response = null;

        try
        {
            response = client.execute(request);
            return response;
        }
        catch (Exception e)
        {
            logger.error(response);
            throw new RuntimeException("Error during execute request: ", e);
        }
    }

    protected static void releaseConnection(HttpClient client, HttpEntity entity) throws Exception
    {
        try
        {
            if (entity != null)
            {
                EntityUtils.consume(entity);
            }
            if (client != null)
            {
                client.getConnectionManager().shutdown();
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error during Release Connection", e);
        }
    }

    private static Boolean canSetHeaderOrBody(String[] params) throws Exception
    {

        // Parameters check
        if (params == null)
        {
            throw new IllegalArgumentException("Null Parameters: Please correct");
        }

        if (params.length < 2)
        {
            return false;
        }
        else if (params[0] == "")
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * Helper to check the actual Result Vs expected
     * 
     * @param HttpResponse actualResponse
     * @param int expectedResponseCode
     * @return Boolean <true> when actual matches expected response
     */
    protected static Boolean checkHttpResponse(HttpResponse actualResponse, int expectedResponseCode)
    {
        if ((null == actualResponse))
        {
            logger.info("Null response received");
        }
        else

        {
            logger.info(actualResponse.getStatusLine());
            return (actualResponse.getStatusLine().getStatusCode() == expectedResponseCode);
        }
        return false;
    }
    
    
    public static HttpDelete generateDeleteRequest(String requestURL, String[] headers, String[] reqBody) throws Exception
    {
        boolean setRequestHeaders = false;
        boolean setRequestBody = false;

        // Parameters check
        if (requestURL.isEmpty())
        {
            throw new IllegalArgumentException("Empty Request URL: Please correct");
        }

        HttpDelete request = new HttpDelete(requestURL);

        setRequestHeaders = canSetHeaderOrBody(headers);

        setRequestBody = canSetHeaderOrBody(reqBody);

        // Headers
        if (setRequestHeaders)
        {
            for (int i = 0; i < headers.length; i = i + 2)
            {
                request.addHeader(headers[i], headers[i + 1]);
            }
        }

        // Body
        if (setRequestBody)
        {
            JSONObject json = new JSONObject();

            for (int i = 0; i < reqBody.length; i = i + 2)
            {
                json.put(reqBody[i], reqBody[i + 1]);
            }
            logger.info("Message Body: " + json);

            //Ignore: Body for Delete Request
        }

        return request;
    }       
}
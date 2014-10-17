package org.alfresco.share.util.api;

import org.alfresco.rest.api.tests.client.*;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.httpclient.HttpMethod;

import java.io.IOException;

/**
 * Created by dmitry.yukhnovets on 13.10.2014.
 */
public class Layer7PublicApiClient extends PublicApiHttpClient {
    private WebDrone drone;
    private HttpClientProvider httpProvider;

    public Layer7PublicApiClient(WebDrone drone, String scheme, String host, int port, String contextPath, String servletName, AuthenticatedHttp authenticatedHttp) {
        super(scheme, host, port, contextPath, servletName, authenticatedHttp);
        this.drone = drone;
        httpProvider = authenticatedHttp.getHttpProvider();
    }

    @Override
    public HttpResponse submitRequest(HttpMethod req, final RequestContext rq) throws IOException
    {
        try
        {
            final long start = System.currentTimeMillis();
            String tokenKey = AbstractUtils.getTokenKey(drone, rq.getRunAsUser(), rq.getPassword());
            req.addRequestHeader("Authorization","Bearer " + tokenKey);

            final AuthenticatedHttp.HttpRequestCallback<HttpResponse> callback = new AuthenticatedHttp.HttpRequestCallback<HttpResponse>()
            {
                @Override
                public HttpResponse onCallSuccess(HttpMethod method) throws Exception
                {
                    long end = System.currentTimeMillis();
                    return new HttpResponse(method, rq.getRunAsUser(), method.getResponseBodyAsString(), (end - start));
                }

                @Override
                public boolean onError(HttpMethod method, Throwable t)
                {
                    return false;
                }
            };
            HttpResponse response = null;
            response = executeWithOauthAuthentication(req, callback);

            return response;
        }
        finally
        {
            if(req != null)
            {
                req.releaseConnection();
            }
        }
    }

    private <T extends Object> T executeWithOauthAuthentication(HttpMethod method, AuthenticatedHttp.HttpRequestCallback<T> callback)
    {
        try
        {
            httpProvider.getHttpClient().executeMethod(null, method);

            if(callback != null)
            {
                return callback.onCallSuccess(method);
            }

            // No callback used, return null
            return null;
        }
        catch(Throwable t)
        {
            boolean handled = false;

            // Delegate to callback to handle error. If not available, throw exception
            if(callback != null)
            {
                handled = callback.onError(method, t);
            }

            if(!handled)
            {
                throw new RuntimeException("Error while executing HTTP-call (" + method.getPath() +")", t);
            }

            return null;
        }
        finally
        {
            method.releaseConnection();
        }
    }


}


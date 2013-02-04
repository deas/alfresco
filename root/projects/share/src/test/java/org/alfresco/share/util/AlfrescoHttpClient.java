/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.share.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class AlfrescoHttpClient
{
    private static Log logger = LogFactory.getLog(AlfrescoHttpClient.class);

    private static final int TIMEOUT_MILLISEC = 10000; // 30 seconds
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    /**
     * Creates the HTTP client with Administrator rights.
     * 
     * @return {@link HttpClient} instance.
     * @throws IOException
     * @throws ClientProtocolException
     */
    public HttpClient getClientAsAdmin(boolean isCloud, final String shareUrl)
    {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
        HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);

        DefaultHttpClient client = new DefaultHttpClient(httpParams);
        client.getCredentialsProvider().setCredentials(new AuthScope(shareUrl, -1),
                new UsernamePasswordCredentials(ADMIN_USERNAME, ADMIN_PASSWORD));

        if (shareUrl.startsWith("https"))
        {
            Scheme http = new Scheme("http", 80, PlainSocketFactory.getSocketFactory());
            SSLSocketFactory sf = buildSSLSocketFactory();
            Scheme https = new Scheme("https", 443, sf);
            SchemeRegistry sr = client.getConnectionManager().getSchemeRegistry();
            sr.register(http);
            sr.register(https);
        }
        //
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("username", "admin"));
        nvps.add(new BasicNameValuePair("password", "admin"));

        String format = isCloud ? "%s/-default-/page/dologin" : "%s/page/dologin";
        String url = String.format(format, shareUrl);

        logger.debug("Logging in using URL: " + url);
        HttpPost httpost = new HttpPost(url);
        
        HttpResponse httpResponse = null;
        try
        {
            httpost.setEntity(new UrlEncodedFormEntity(nvps, Charset.forName("UTF-8").toString()));
            HttpContext httpContext = new BasicHttpContext();
            httpResponse = client.execute(httpost, httpContext);
            // Session handling
            CookieStore cookieStore = client.getCookieStore();
            httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        }
        catch (IOException ioe)
        {
            String msg = String.format("Unable to obtain http client with admin credentials,%n " +
                                       "    Response failed when posting to %s", url);
            throw new RuntimeException(msg, ioe);
        }
        finally 
        {
            if(null != httpResponse)
            {
                try{EntityUtils.consume(httpResponse.getEntity());}catch (IOException ioe){}
            }
        }

        return client;
    }

    /**
     * Factory builder that creates a non strict SSL certificate
     * compliance rule base.
     * @return {@link SSLSocketFactory}
     */
    private SSLSocketFactory buildSSLSocketFactory()
    {
        TrustStrategy ts = new TrustStrategy()
        {
            @Override
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException
            {
                return true; 
            }
        };

        SSLSocketFactory sf = null;

        try
        {
            /* build socket factory with host name verification turned off. */
            sf = new SSLSocketFactory(ts, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        }
        catch (NoSuchAlgorithmException nsae)
        {
            logger.error("Failed to initialize SSL handling.", nsae);
        }
        catch (KeyManagementException kme)
        {
            logger.error("Failed to initialize SSL handling.", kme);
        }
        catch (KeyStoreException kse)
        {
            logger.error("Failed to initialize SSL handling.", kse);
        }
        catch (UnrecoverableKeyException uke)
        {
            logger.error("Failed to initialize SSL handling.", uke);
        }

        return sf;
    }

}

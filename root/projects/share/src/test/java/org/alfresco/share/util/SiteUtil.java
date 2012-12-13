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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility class to manage site related operations
 * <ul>
 * <li>Creates site by calling REST API.</li>
 * <li>Deletes site by calling REST API.</li>
 * <li>Gets NodeRef value by site name.</li>
 * </ul>
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class SiteUtil 
{
    private final static Log logger = LogFactory.getLog(SiteUtil.class);
    private final static String DESCRIPTION = "description";
    private final static String SHORT_NAME = "shortName";
    private final static String SITE_PRESET = "sitePreset";
    private final static String SITE_DASHBOARD = "site-dashboard";
    private final static String TITLE = "title";
    private final static String VISIBILITY = "visibility";
    private final static String PUBLIC = "PUBLIC";
    private final static String ERROR_MESSAGE_PATTERN = 
            "Failed to create a new site %n Site Name: %s%n Create Site API URL: %s%n";

    /**
     * Constructor.
     */
    private SiteUtil(){}
    
    /**
     * Prepare a file in system temp directory to be used
     * in test for uploads.
     * @return {@link File} simple text file.
     */
    public static File prepareFile(){

        File file = null;
        OutputStreamWriter writer = null;
        try
        {
            
            file = File.createTempFile("myfile", ".txt");
            
            writer = new OutputStreamWriter(new FileOutputStream(file),
                                                            Charset.forName("UTF-8").newEncoder());
            writer.write("this is a sample test upload file");
            writer.close();
        }
        catch (IOException ioe)
        {
            logger.error("Unable to create sample file", ioe);
        }
        catch (Exception e)
        {
            logger.error("Unable to create site", e);
        }
        finally
        {
            if (writer != null)
            {
                try
                {
                    writer.close();
                }
                catch (IOException ioe)
                {
                    logger.error("Unable to close properly", ioe);
                }
            }
        }
        return file;
    }
    
    /**
     * Creates a new site using the admin authentication credential's. This will
     * only create a site in repository and not in share.
     * 
     * @param siteName String site name
     * @param desc Site description
     * @param isCloud boolean cloud version
     * @return true if site created
     * @throws IOException 
     * @throws Exception if error
     */
    public static boolean createSite(final String siteName, final String desc, final boolean isCloud, final String shareUrl) throws IOException  
    {
        if (siteName == null || siteName.isEmpty()) throw new UnsupportedOperationException("site name is required");
        boolean siteCreated = false;
        HttpResponse httpResponse = null;
        HttpClient client = null;
        String url = "";
        try
        {
            client = new AlfrescoHttpClient().getClientAsAdmin(isCloud, shareUrl);
            
            JSONObject json = new JSONObject();
            json.put(DESCRIPTION, desc);
            json.put(SITE_PRESET, SITE_DASHBOARD);
            json.put(TITLE, siteName);
            json.put(VISIBILITY, PUBLIC);
            json.put(SHORT_NAME, siteName);

            if (isCloud)
            {
                url = String.format("%s/-system-/service/modules/create-site", shareUrl);
            }
            else
            {
                url = String.format("%s/service/modules/create-site", shareUrl);
            }
            
            HttpPost post = new HttpPost(url);
            post.setEntity(HttpUtil.setMessageBody(json));
            httpResponse = client.execute(post);
            String response = HttpUtil.readStream(httpResponse.getEntity().getContent());
            if (logger.isTraceEnabled())
            {
                logger.trace("response for creating site: " + response);
            }
            if (httpResponse.getStatusLine().getStatusCode() > 200)
            {
                String msg = String.format("Create site: %s response from server was unsuccessful",siteName);
                throw new RuntimeException(msg);
            }
            siteCreated = true;
        }
        catch (JSONException jse)
        {
          String msg = String.format(ERROR_MESSAGE_PATTERN, siteName, url);
          throw new RuntimeException(msg, jse);
        }
        catch (UnsupportedEncodingException une)
        {
            String msg = String.format(ERROR_MESSAGE_PATTERN,siteName , url);
                    throw new RuntimeException(msg, une);
        }
        catch (ClientProtocolException ce)
        {
            String msg = String.format(ERROR_MESSAGE_PATTERN,siteName , url);
            throw new RuntimeException(msg, ce);
        }
        finally
        {
            if(null != httpResponse)
            {
                try{ EntityUtils.consume(httpResponse.getEntity());} catch (IOException e){}
            }
            if (client != null) client.getConnectionManager().shutdown();
            client = null;
        }

        return siteCreated;
    }

    /***
     * Deletes site using rest api.
     * 
     * @param siteName String site name
     * @param isCloud a cloud version
     * @return true if site deleted
     * @throws Exception if error
     */
    public static boolean deleteSite(final String siteName, final boolean isCloud, final String shareUrl) 
    {
        if (siteName == null || siteName.isEmpty()) throw new UnsupportedOperationException("site name is required");

        if(logger.isTraceEnabled())
        {
            logger.trace(String.format("about to remove site {%s} is cloud %s",siteName,isCloud));
        }
        boolean siteDeleted = false;
        HttpResponse httpResponse;
        String url = null;
        HttpClient client = null;
        try
        {
            JSONObject json = new JSONObject();
            json.put("shortName", siteName);
            if (isCloud)
            {
                url = String.format("%s/-system-/service/modules/delete-site", shareUrl);
            }
            else
            {
                url = String.format("%s/service/modules/delete-site", shareUrl);
            }
            
            HttpPost delete = new HttpPost(url);
            delete.setEntity(HttpUtil.setMessageBody(json));
            client = new AlfrescoHttpClient().getClientAsAdmin(isCloud,shareUrl);
            httpResponse = client.execute(delete);

            String response = HttpUtil.readStream(httpResponse.getEntity().getContent());
            if (logger.isDebugEnabled())
            {
                logger.debug("response for deleting site: " + response);
            }

            if (httpResponse.getStatusLine().getStatusCode() > 200)
                throw new RuntimeException("Delete site response was unsuccessful: " + httpResponse.getStatusLine());

            if (response.contains("success")) siteDeleted = true;
        }
        catch (IOException ioe)
        {
            String msg = String.format("Deleting site %s failed, using delete api url: %s",siteName, url);
            throw new RuntimeException(msg,ioe);
        }
        catch (JSONException jse)
        {
            String msg = String.format("Site delete failed, unable to write json value for shortName: %s",siteName);
            throw new RuntimeException(msg,jse);
        }
        finally
        {
            if (client != null) client.getConnectionManager().shutdown();
            client = null;
        }
        return siteDeleted;
    }

}

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User util class, manages all operations relating to user.
 * <ul>
 * <li> Create user by calling a REST api using HttpClient.</li>
 * <li> Find user by calling REST api </li>
 * </ul>
 * @author Michael Suzuki
 * @since 1.0
 */
public class UserUtil extends AlfrescoHttpClient
{
    private static Log logger = LogFactory.getLog(UserUtil.class);
    private static final String JSON_USERNAME = "userName";
    private static final String JSON_FIRSTNAME = "firstName";
    private static final String JSON_LASTNAME = "lastName";
    private static final String JSON_EMAIL = "email";
    private static final String JSON_PASSWORD = "password";
    private static final String PEOPLE_REST_API_FORMAT = "%s/service/api/people";
    
    /**
     * Creates user on Alfresco
     * @param username String username
     * @param lastName String last name
     * @param firstName String first name
     * @param email String email
     * @param password String password
     * @param isCloud if Alfresco cloud
     * @return boolean true when user created successfully
     * @throws Exception if error
     */
    public static boolean createUser(final String username, final String lastName, final String firstName,
            final String email, final String password, boolean isCloud, final String shareUrl) throws Exception
    {
        boolean userCreated = false;
        HttpClient client = new AlfrescoHttpClient().getClientAsAdmin(isCloud, shareUrl);
        // Create request body containing user details
        HttpResponse httpResponse;
        try
        {
            JSONObject json = new JSONObject();
            json.put(JSON_USERNAME, username);
            json.put(JSON_LASTNAME, lastName);
            json.put(JSON_FIRSTNAME, firstName);
            json.put(JSON_EMAIL, email);
            json.put(JSON_PASSWORD, password);

            String url = shareUrl.replaceFirst("share", "alfresco");
            HttpPost post = new HttpPost(String.format(PEOPLE_REST_API_FORMAT, url));
            post.setEntity(HttpUtil.setMessageBody(json));
            httpResponse = client.execute(post);
            /* Checking response */
            if (httpResponse != null)
            {
                String response = HttpUtil.readStream(httpResponse.getEntity().getContent());
                if(logger.isDebugEnabled()) logger.debug("response for creating user: " + response);
                userCreated = response.contains("enabled\": true,");
            }
        }
        catch (JSONException jse)
        {
            logger.error("Create user JSON error", jse);
            throw new Exception("Json obect create error", jse);
        } finally{
            if(client != null) client.getConnectionManager().shutdown();
        }
        return userCreated;
    }

    /**
     * Helper method to determine if user is already registered.
     * 
     * @param username String user name
     * @param isCloud if Alfresco cloud.
     * @return true if user exists
     * @throws Exception if error
     */
    public static boolean findUser(String username, boolean isCloud, final String shareUrl) throws Exception
    {
        boolean exists = false;
        HttpClient client = new AlfrescoHttpClient().getClientAsAdmin(isCloud, shareUrl);
        String url = shareUrl.replaceFirst("share", "alfresco");
        HttpGet httpget = new HttpGet(String.format(PEOPLE_REST_API_FORMAT, url));
        HttpResponse httpResponse;
        HttpEntity entity = null;
        try
        {
            httpResponse = client.execute(httpget);
            entity = httpResponse.getEntity();
            String response = HttpUtil.readStream(entity.getContent());

            if (logger.isDebugEnabled())
            {
                logger.debug(httpResponse.getStatusLine());
                logger.debug(response);
            }

            if (response != null && !response.trim().isEmpty())
            {
                exists = response.matches(username);
            }
        }
        catch (Exception e)
        {
            logger.error("find user error",e);
        }
        finally
        {
            if (entity != null && entity.isStreaming())
            {
                try
                {
                    EntityUtils.consume(entity);
                }
                catch (IOException ioe)
                {
                    logger.error("Error clearing entity", ioe);
                }
            }
            client.getConnectionManager().shutdown();
        }

        return exists;
    }
}

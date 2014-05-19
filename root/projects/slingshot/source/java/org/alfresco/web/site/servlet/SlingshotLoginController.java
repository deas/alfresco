/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.web.site.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.alfresco.web.site.SlingshotUser;
import org.alfresco.web.site.SlingshotUserFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.extensions.surf.FrameworkUtil;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.mvc.LoginController;
import org.springframework.extensions.surf.support.AlfrescoUserFactory;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorContext;
import org.springframework.extensions.webscripts.connector.HttpMethod;
import org.springframework.extensions.webscripts.connector.Response;

/**
 * This extends the standard {@link LoginController} to store the authenticated user's group membership information
 * as an {@link HttpSession} attribute so that it can be retrieved by the {@link SlingshotUserFactory} when creating
 * {@link SlingshotUser} instances.
 *  
 * @author dave
 */
public class SlingshotLoginController extends LoginController
{
    public static String SESSION_ATTRIBUTE_KEY_USER_GROUPS = "_alf_USER_GROUPS";
    
    /**
     * Overrides the inherited method to retrieve the groups that the authenticated user is a member
     * of and stores them as a comma delimited {@link String} in the {@link HttpSession}. This {@link String}
     * is then retrieved when loading a user in the {@link SlingshotUserFactory} and stored as a user
     * property. 
     * 
     * @param request The {@link HttpServletRequest}
     * @param response The {@link HttpServletResponse}
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void onSuccess(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        
        try
        {
            // Get the authenticated user name and use it to retrieve all of the groups that the user is a 
            // member of...
            String username = (String) request.getParameter("username");
            Connector conn = FrameworkUtil.getConnector(request.getSession(), username, AlfrescoUserFactory.ALFRESCO_ENDPOINT_ID);
            ConnectorContext c = new ConnectorContext(HttpMethod.GET);
            c.setContentType("application/json");
            Response res = conn.call("/api/people/" + username + "?groups=true", c);
            if (Status.STATUS_OK == res.getStatus().getCode())
            {
                // Assuming we get a successful response then we need to parse the response as JSON and then
                // retrieve the group data from it...
                // 
                // Step 1: Get a String of the response...
                InputStream in = res.getResponseStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in)); 
                StringBuilder resStr = new StringBuilder();
                try
                {
                    String tmp;
                    while ((tmp = br.readLine()) != null)
                    {
                        resStr.append(tmp);
                    }
                }
                catch (IOException e)
                {
                    throw new Exception("Error reading user group data response");
                }
                
                // Step 2: Parse the JSON...
                JSONParser jp = new JSONParser();
                Object userData = jp.parse(resStr.toString());

                // Step 3: Iterate through the JSON object getting all the groups that the user is a member of...
                StringBuilder groups = new StringBuilder();
                if (userData instanceof JSONObject)
                {
                    Object groupsArray = ((JSONObject) userData).get("groups");
                    if (groupsArray instanceof org.json.simple.JSONArray)
                    {
                        for (Object groupData: (org.json.simple.JSONArray)groupsArray)
                        {
                            if (groupData instanceof JSONObject)
                            {
                                Object groupName = ((JSONObject) groupData).get("itemName");
                                if (groupName != null)
                                {
                                    groups.append(groupName.toString() + ",");
                                }
                            }
                        }
                    }
                }
                
                // Step 4: Trim off any trailing commas...
                if (groups.length()>0)
                {
                    groups.delete(groups.length()-1, groups.length());
                }
                
                // Step 5: Store the groups on the session...
                HttpSession session = request.getSession(false);
                session.setAttribute(SESSION_ATTRIBUTE_KEY_USER_GROUPS, groups.toString());
            }
            else
            {
                throw new Exception("Remote error requesting user group data: " + res.getStatus().getMessage());
            }
        }
        catch (ConnectorServiceException e1)
        {
            throw new Exception("Error creating remote connector to request user group data");
        }

        super.onSuccess(request, response);
    }
}

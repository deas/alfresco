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

package org.springframework.extensions.webscripts.portlet;

import java.util.Map;

import javax.portlet.PortletRequest;

import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.Match;
import org.springframework.extensions.webscripts.Runtime;
import org.springframework.extensions.webscripts.WebScriptRequestURLImpl;


/**
 * JSR-168 Web Script Request
 * 
 * @author davidc
 */
public class WebScriptPortletRequest extends WebScriptRequestURLImpl
{
    public static final String ALFPORTLETUSERNAME = "alfportletusername";
    
    /** Portlet Request */
    private PortletRequest req;
    
    
    /**
     * Construct
     * 
     * @param req
     * @param scriptUrl
     * @param serviceMatch
     */
    public WebScriptPortletRequest(Runtime container, PortletRequest req, String scriptUrl, Match serviceMatch)
    {
        this(container, req, splitURL(scriptUrl), serviceMatch);
    }
    
    /**
     * Construct
     * 
     * @param req
     * @param scriptUrlParts
     * @param serviceMatch
     */
    public WebScriptPortletRequest(Runtime container, PortletRequest req, String[] scriptUrlParts, Match serviceMatch)
    {
        super(container, scriptUrlParts, serviceMatch);
        this.req = req;
        if (req != null)
        {
            // look for the user info map in the portlet request - populated by the portlet container
            Map userInfo = (Map)req.getAttribute(PortletRequest.USER_INFO);
            if (userInfo != null)
            {
                // look for the special Liferay email (username) key
                String liferayUsername = (String)userInfo.get("user.home-info.online.email");
                if (liferayUsername != null)
                {
                    // strip suffix from email address - we only need username part
                    if (liferayUsername.indexOf('@') != -1)
                    {
                        liferayUsername = liferayUsername.substring(0, liferayUsername.indexOf('@'));
                    }
                    // save in session for use by alfresco portlet authenticator
                    this.req.getPortletSession().setAttribute(ALFPORTLETUSERNAME, liferayUsername);
                }
            }
        }
    }

    /**
     * Gets the Portlet Request
     * 
     * @return  Portlet Request
     */
    public PortletRequest getPortletRequest()
    {
        return req;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getServerPath()
     */
    public String getServerPath()
    {
        return req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getAgent()
     */
    public String getAgent()
    {
        // NOTE: rely on default agent mappings
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getHeaderNames()
     */
    public String[] getHeaderNames()
    {
        return new String[] {};
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getHeader(java.lang.String)
     */
    public String getHeader(String name)
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getHeaderValues(java.lang.String)
     */
    public String[] getHeaderValues(String name)
    {
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getContent()
     */
    public Content getContent()
    {
        return null;
    }

}

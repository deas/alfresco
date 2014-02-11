/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.web.scripts.bean;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;

/**
 * Test if the current user Session contains an authenticated userid.
 */
public class Authenticated extends DeclarativeWebScript
{
	/* (non-Javadoc)
     * @see org.alfresco.web.scripts.DeclarativeWebScript#executeImpl(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.WebScriptResponse)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status)
    {
        if (req instanceof WebScriptServletRequest)
        {
            WebScriptServletRequest webScriptServletRequest = (WebScriptServletRequest)req;
            HttpSession session = webScriptServletRequest.getHttpServletRequest().getSession(false);
            
            if (session == null ||
                session.getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID) == null ||
                UserFactory.USER_GUEST.equals(session.getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID)))
            {
               status.setCode(401);
               status.setMessage("There is no User ID in session.");
               status.setRedirect(true);
            }
        }
        return null;
    }
}

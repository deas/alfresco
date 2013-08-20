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
package org.alfresco.webservice.util;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.transport.http.HTTPConstants;

/**
 * @author Roy Wetherall
 */
public class CookieHandler extends BasicHandler 
{
    private static final long serialVersionUID = 5355053439499560511L;

    public void invoke(MessageContext context) 
        throws AxisFault 
    {
        AuthenticationDetails authenticationDetails = AuthenticationUtils.getAuthenticationDetails();
        String sessionId = (authenticationDetails != null) ? (authenticationDetails.getSessionId()) : (null);

        if (sessionId != null)
        {
            context.setProperty(HTTPConstants.HEADER_COOKIE, "JSESSIONID=" + sessionId);
        }
    }
 }

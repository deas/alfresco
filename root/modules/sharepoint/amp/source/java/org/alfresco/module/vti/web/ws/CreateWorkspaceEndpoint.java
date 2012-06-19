/*
* Copyright (C) 2005-2010 Alfresco Software Limited.
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

package org.alfresco.module.vti.web.ws;

import org.alfresco.module.vti.handler.MeetingServiceHandler;
import org.alfresco.repo.SessionUser;
import org.alfresco.repo.webdav.auth.SharepointConstants;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;

/**
 * Class for handling CreateWorkspace soap method
 * 
 * @author PavelYur
 */
public class CreateWorkspaceEndpoint extends AbstractWorkspaceEndpoint
{
    public CreateWorkspaceEndpoint(MeetingServiceHandler handler)
    {
        super(handler);
    }

    /**
     * A site should not be given
     */
    @Override
    protected long getSiteRequired()
    {
        return -1;
    }

    /**
     * Create new Meeting Workspace on Alfresco server
     */
    @Override
    protected void executeWorkspaceAction(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse,
            Element requestElement, SimpleNamespaceContext nc, String siteName, String title, String templateName,
            int lcid) throws Exception
    {
        // A title is required
        if (title == null || title.length() == 0)
        {
            throw new RuntimeException("Site name is not specified. Please fill up subject field.");
        }

        // Have the site created
        siteName = handler.createWorkspace(title, templateName, lcid, getTimeZoneInformation(requestElement),
                (SessionUser) soapRequest.getSession().getAttribute(SharepointConstants.USER_SESSION_ATTRIBUTE));

        // Create the soap response
        Element workspace = buildWorkspaceResponse(soapResponse);
        workspace.addAttribute("Url", getHost(soapRequest) + soapRequest.getAlfrescoContextName() + "/" + siteName);
    }
}
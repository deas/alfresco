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
import org.alfresco.module.vti.handler.SiteTypeException;
import org.alfresco.repo.SessionUser;
import org.alfresco.repo.site.SiteDoesNotExistException;
import org.alfresco.repo.webdav.auth.SharepointConstants;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;

/**
 * Class for handling DeleteWorkspace soap method
 * 
 * @author PavelYur
 */
public class DeleteWorkspaceEndpoint extends AbstractWorkspaceEndpoint
{
    public DeleteWorkspaceEndpoint(MeetingServiceHandler handler)
    {
        super(handler);
    }

    /**
     * A site is always required
     */
    @Override
    protected long getSiteRequired()
    {
        return 4l;
    }

    @Override
    protected void executeWorkspaceAction(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse,
            Element requestElement, SimpleNamespaceContext nc, String siteName, String title, String templateName,
            int lcid) throws Exception
    {
        // Perform the deletion
        try
        {
            handler.deleteWorkspace(siteName, (SessionUser) soapRequest.getSession().getAttribute(SharepointConstants.USER_SESSION_ATTRIBUTE));
        }
        catch (SiteDoesNotExistException se)
        {
            throw new VtiSoapException("vti.meeting.error.no_site", 0x4l); // TODO Is this the right code?
        }
        catch (SiteTypeException ste)
        {
            throw new VtiSoapException(ste.getMsgId(), 0x4l);
        }

        // Create the soap response
        soapResponse.setContentType("text/xml");
        soapResponse.getDocument().addElement("DeleteWorkspaceResponse", namespace);
    }
}
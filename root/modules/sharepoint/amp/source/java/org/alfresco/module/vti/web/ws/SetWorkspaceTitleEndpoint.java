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
import org.alfresco.repo.site.SiteDoesNotExistException;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;

/**
 * Class for handling SetWorkspaceTitle soap method
 * 
 * @author PavelYur
 */
public class SetWorkspaceTitleEndpoint extends AbstractWorkspaceEndpoint
{
    public SetWorkspaceTitleEndpoint(MeetingServiceHandler handler)
    {
        super(handler);
    }

    /**
     * A site name is always required
     */
    @Override
    protected long getSiteRequired()
    {
        return 6;
    }

    @Override
    protected void executeWorkspaceAction(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse,
            Element requestElement, SimpleNamespaceContext nc, String siteName, String title, String templateName,
            int lcid) throws Exception
    {
        // If no new title is given, then an empty string is used
        if (title == null)
        {
            title = "";
        }

        // Update the title
        try
        {
            handler.updateWorkspaceTitle(siteName, title);
        }
        catch (SiteDoesNotExistException e)
        {
            throw new VtiSoapException("Site '" + siteName + "' not found", 6l);
        }

        // Create the soap response
        soapResponse.setContentType("text/xml");
        soapResponse.getDocument().addElement("SetWorkspaceTitleResponse", namespace);
    }
}
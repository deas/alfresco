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

import java.util.List;

import org.alfresco.module.vti.handler.MeetingServiceHandler;
import org.alfresco.service.cmr.site.SiteInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;
import org.springframework.extensions.surf.util.I18NUtil;
/**
 * Class for handling GetMeetingWorkspaces soap method
 * 
 * @author PavelYur
 */
public class GetMeetingWorkspacesEndpoint extends AbstractEndpoint
{
    // handler that provides methods for operating with meetings
    private MeetingServiceHandler handler;

    // xml namespace prefix
    private static String prefix = "mt";

    private static Log logger = LogFactory.getLog(GetMeetingWorkspacesEndpoint.class);

    public GetMeetingWorkspacesEndpoint(MeetingServiceHandler handler)
    {
        this.handler = handler;
    }

    /**
     * Retrieve the list of Meeting Workspaces on specified Alfresco Server
     * 
     * @param soapRequest Vti soap request ({@link VtiSoapRequest})
     * @param soapResponse Vti soap response ({@link VtiSoapResponse})
     */
    public void execute(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug("Soap Method with name " + getName() + " is started.");

        String site = getDwsFromUri(soapRequest);

        if (site.length() > 0)
        {
            throw new RuntimeException(new String(I18NUtil.getMessage("vti.meeting.error.subsites").getBytes("ISO-8859-1"), "UTF-8"));
        }

        // mapping xml namespace to prefix
        SimpleNamespaceContext nc = new SimpleNamespaceContext();
        nc.addNamespace(prefix, namespace);
        nc.addNamespace(soapUriPrefix, soapUri);

        Element requestElement = soapRequest.getDocument().getRootElement();

        // Did they request recurring information?
        // NOTE - The meaning of this flag is currently unclear, and a TDI is open for it
        // Based on the eventual response, the current boolean may need to become a 
        //  Boolean, or may need to become two booleans.
        if (logger.isDebugEnabled())
            logger.debug("Getting recurring from request.");
        XPath recurringPath = new Dom4jXPath(buildXPath(prefix, "/GetMeetingWorkspaces/recurring"));
        recurringPath.setNamespaceContext(nc);
        Element recurringE = (Element) recurringPath.selectSingleNode(requestElement);

        boolean recurring = false;
        if (recurringE != null)
        {
            recurring = Boolean.parseBoolean(recurringE.getText());
        }
        
        // Fetch the sites
        List<SiteInfo> sites = handler.getMeetingWorkspaces(recurring);

        // creating soap response
        Element root = soapResponse.getDocument().addElement("GetMeetingWorkspacesResponse", namespace);
        Element meetingWorkspaces = root.addElement("GetMeetingWorkspacesResult").addElement("MeetingWorkspaces");

        String baseUrl = getHost(soapRequest) + soapRequest.getAlfrescoContextName() + "/";

        for (SiteInfo siteInfo : sites)
        {
            Element workspace = meetingWorkspaces.addElement("Workspace");
            workspace.addAttribute("Url", baseUrl + siteInfo.getShortName());
            workspace.addAttribute("Title", siteInfo.getTitle());
        }

        soapResponse.setContentType("text/xml");
        if (logger.isDebugEnabled())
        {
            logger.debug("SOAP method with name " + getName() + " is finished.");
        }
    }
}

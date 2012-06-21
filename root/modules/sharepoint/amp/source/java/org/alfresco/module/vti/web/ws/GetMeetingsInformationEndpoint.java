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
import org.alfresco.module.vti.metadata.model.MeetingsInformation;
import org.alfresco.module.vti.metadata.model.MwsStatus;
import org.alfresco.module.vti.metadata.model.MwsTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;
/**
 * Class for handling GetMeetingsInformation soap method
 * 
 * @author PavelYur
 */
public class GetMeetingsInformationEndpoint extends AbstractEndpoint
{
    // handler that provides methods for operating with meetings
    private MeetingServiceHandler handler;

    // xml namespace prefix
    private static String prefix = "mt";

    private static Log logger = LogFactory.getLog(GetMeetingsInformationEndpoint.class);

    public GetMeetingsInformationEndpoint(MeetingServiceHandler handler)
    {
        this.handler = handler;
    }

    /**
     * Retrieve information related to meetings
     * 
     * @param soapRequest Vti soap request ({@link VtiSoapRequest})
     * @param soapResponse Vti soap response ({@link VtiSoapResponse})
     */
    public void execute(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug("Soap Method with name " + getName() + " is started.");

        // mapping xml namespace to prefix
        SimpleNamespaceContext nc = new SimpleNamespaceContext();
        nc.addNamespace(prefix, namespace);
        nc.addNamespace(soapUriPrefix, soapUri);

        Element requestElement = soapRequest.getDocument().getRootElement();

        // getting requestFlags parameter from request
        if (logger.isDebugEnabled())
            logger.debug("Getting requestFlags from request.");
        XPath requestFlagsPath = new Dom4jXPath(buildXPath(prefix, "/GetMeetingsInformation/requestFlags"));
        requestFlagsPath.setNamespaceContext(nc);
        Element requestFlagsE = (Element) requestFlagsPath.selectSingleNode(requestElement);

        int requestFlags = 0;
        if (requestFlagsE != null)
        {
            requestFlags = Integer.parseInt(requestFlagsE.getText());
        }
        
        // Some kinds of calls need a site, some don't
        String siteName = getDwsFromUri(soapRequest);
        if (logger.isDebugEnabled())
            logger.debug("Site Name is '"+siteName+"', request flags are " + requestFlags);
        if (siteName.length() > 0)
        {
            // Is Flag 0x8 set? 0x8 = Query the status values of one site
            if ((requestFlags&8) == 8)
            {
                siteName = siteName.substring(1);
            }
            else
            {
                // There's a specific error code for this case
                throw new VtiSoapException("vti.meeting.error.subsites", 0x1);
            }
        }

        // getting lcid parameter from request
        if (logger.isDebugEnabled())
            logger.debug("Getting lcid from request.");
        XPath lcidPath = new Dom4jXPath(buildXPath(prefix, "/GetMeetingsInformation/lcid"));
        lcidPath.setNamespaceContext(nc);
        Element lcidE = (Element) lcidPath.selectSingleNode(requestElement);
        
        int lcid = 0;
        if (lcidE != null)
        {
            lcid = Integer.parseInt(lcidE.getText());
        }
        

        // Fetch the meeting details
        MeetingsInformation info = handler.getMeetingsInformation(siteName, requestFlags, lcid);

        // writing soap response
        Element root = soapResponse.getDocument().addElement("GetMeetingsInformationResponse", namespace);
        Element getMeetingsInformationResult = root.addElement("GetMeetingsInformationResult");

        Element meetingsInformation = getMeetingsInformationResult.addElement("MeetingsInformation");

        // allow create?
        Element allowCreate = meetingsInformation.addElement("AllowCreate");
        allowCreate.addText(info.isAllowCreate() ? "True" : "False");

        // supported languages
        List<Integer> templateLanguages = info.getTemplateLanguages();

        if (!templateLanguages.isEmpty())
        {
            Element listTemplateLanguages = meetingsInformation.addElement("ListTemplateLanguages");

            for (Integer language : templateLanguages)
            {
                Element LCID = listTemplateLanguages.addElement("LCID");
                LCID.addText(String.valueOf(language.intValue()));
            }
        }

        // supported templates
        List<MwsTemplate> templates = info.getTemplates();

        if (!templates.isEmpty())
        {
            Element listTemplates = meetingsInformation.addElement("ListTemplates");

            for (MwsTemplate mwsTemplate : templates)
            {
                Element template = listTemplates.addElement("Template");
                template.addAttribute("Name", mwsTemplate.getName());
                template.addAttribute("Title", mwsTemplate.getTitle());
                template.addAttribute("Id", String.valueOf(mwsTemplate.getId()));
                template.addAttribute("Description", mwsTemplate.getDescription());
                template.addAttribute("ImageUrl", getHost(soapRequest) + soapRequest.getAlfrescoContextName() + "/resources/images/logo/alfresco3d.jpg");
            }
        }

        MwsStatus status = info.getStatus();

        if (status != null)
        {
            Element workspaceStatus = meetingsInformation.addElement("WorkspaceStatus");
            workspaceStatus.addAttribute("UniquePermissions", String.valueOf(status.isUniquePermissions()));
            workspaceStatus.addAttribute("MeetingCount", String.valueOf(status.getMeetingCount()));
            workspaceStatus.addAttribute("AnonymousAccess", String.valueOf(status.isAnonymousAccess()));
            workspaceStatus.addAttribute("AllowAuthenticatedUsers", String.valueOf(status.isAllowAuthenticatedUsers()));
        }
        
        soapResponse.setContentType("text/xml");
        if (logger.isDebugEnabled())
        {
            logger.debug("SOAP method with name " + getName() + " is finished.");
        }
    }
}

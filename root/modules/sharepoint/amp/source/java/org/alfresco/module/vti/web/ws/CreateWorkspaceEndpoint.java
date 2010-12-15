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
import org.alfresco.module.vti.metadata.model.TimeZoneInformation;
import org.alfresco.module.vti.metadata.model.TimeZoneInformationDate;
import org.alfresco.repo.SessionUser;
import org.alfresco.repo.webdav.auth.SharepointConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Class for handling CreateWorkspace soap method
 * 
 * @author PavelYur
 */
public class CreateWorkspaceEndpoint extends AbstractEndpoint
{
    // handler that provides methods for operating with meetings
    private MeetingServiceHandler handler;

    // xml namespace prefix
    private static String prefix = "mt";

    private static Log logger = LogFactory.getLog(CreateWorkspaceEndpoint.class);

    public CreateWorkspaceEndpoint(MeetingServiceHandler handler)
    {
        this.handler = handler;
    }

    /**
     * Create new Meeting Workspace on Alfresco server
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

        // getting title parameter from request
        if (logger.isDebugEnabled())
            logger.debug("Getting title from request.");
        XPath titlePath = new Dom4jXPath(buildXPath(prefix, "/CreateWorkspace/title"));
        titlePath.setNamespaceContext(nc);
        Element title = (Element) titlePath.selectSingleNode(requestElement);

        if (title.getText() == null || title.getText().length() < 1)
        {
            throw new RuntimeException("Site name is not specified. Please fill up subject field.");
        }

        // getting templateName parameter from request
        if (logger.isDebugEnabled())
            logger.debug("Getting templateName from request.");
        XPath templateNamePath = new Dom4jXPath(buildXPath(prefix, "/CreateWorkspace/templateName"));
        templateNamePath.setNamespaceContext(nc);
        Element templateName = (Element) templateNamePath.selectSingleNode(requestElement);

        // getting lcid parameter from request
        if (logger.isDebugEnabled())
            logger.debug("Getting lcid from request.");
        XPath lcidPath = new Dom4jXPath(buildXPath(prefix, "/CreateWorkspace/lcid"));
        lcidPath.setNamespaceContext(nc);
        Element lcid = (Element) lcidPath.selectSingleNode(requestElement);

        String siteName = handler.createWorkspace(title.getText(), templateName.getText(), Integer.parseInt(lcid.getText()), getTimeZoneInformation(requestElement),
                (SessionUser) soapRequest.getSession().getAttribute(SharepointConstants.USER_SESSION_ATTRIBUTE));

        // creating soap response
        Element root = soapResponse.getDocument().addElement("CreateWorkspaceResponse", namespace);
        root.addElement("CreateWorkspaceResult").addElement("CreateWorkspace").addAttribute("Url", getHost(soapRequest) + soapRequest.getAlfrescoContextName() + "/" + siteName);

        if (logger.isDebugEnabled())
        {
            logger.debug("SOAP method with name " + getName() + " is finished.");
        }
    }

    private TimeZoneInformation getTimeZoneInformation(Element element) throws Exception
    {
        TimeZoneInformation timeZoneInforation = new TimeZoneInformation();

        SimpleNamespaceContext nc = new SimpleNamespaceContext();
        nc.addNamespace(prefix, namespace);
        nc.addNamespace(soapUriPrefix, soapUri);

        XPath biasPath = new Dom4jXPath(buildXPath(prefix, "/CreateWorkspace/timeZoneInformation/bias"));
        biasPath.setNamespaceContext(nc);
        timeZoneInforation.setBias(Integer.valueOf(((Element) biasPath.selectSingleNode(element)).getTextTrim()));

        timeZoneInforation.setStandardDate(createTimeZoneInformationDate(element, "standardDate"));

        XPath standardBiasPath = new Dom4jXPath(buildXPath(prefix, "/CreateWorkspace/timeZoneInformation/standardBias"));
        standardBiasPath.setNamespaceContext(nc);
        timeZoneInforation.setStandardBias(Integer.valueOf(((Element) standardBiasPath.selectSingleNode(element)).getTextTrim()));

        timeZoneInforation.setDaylightDate(createTimeZoneInformationDate(element, "daylightDate"));

        XPath daylightBiasPath = new Dom4jXPath(buildXPath(prefix, "/CreateWorkspace/timeZoneInformation/daylightBias"));
        daylightBiasPath.setNamespaceContext(nc);
        timeZoneInforation.setDaylightBias(Integer.valueOf(((Element) daylightBiasPath.selectSingleNode(element)).getTextTrim()));

        return timeZoneInforation;
    }

    private TimeZoneInformationDate createTimeZoneInformationDate(Element element, String typeOfDate) throws Exception
    {
        TimeZoneInformationDate date = new TimeZoneInformationDate();

        SimpleNamespaceContext nc = new SimpleNamespaceContext();
        nc.addNamespace(prefix, namespace);
        nc.addNamespace(soapUriPrefix, soapUri);

        XPath dateYearPath = new Dom4jXPath(buildXPath(prefix, "/CreateWorkspace/timeZoneInformation/" + typeOfDate + "/year"));
        dateYearPath.setNamespaceContext(nc);
        date.setYear(Integer.valueOf(((Element) dateYearPath.selectSingleNode(element)).getTextTrim()));

        XPath dateMonthPath = new Dom4jXPath(buildXPath(prefix, "/CreateWorkspace/timeZoneInformation/" + typeOfDate + "/month"));
        dateMonthPath.setNamespaceContext(nc);
        date.setMonth(Integer.valueOf(((Element) dateMonthPath.selectSingleNode(element)).getTextTrim()));

        XPath dateDayOfWeekPath = new Dom4jXPath(buildXPath(prefix, "/CreateWorkspace/timeZoneInformation/" + typeOfDate + "/dayOfWeek"));
        dateDayOfWeekPath.setNamespaceContext(nc);
        date.setDayOfWeek(Integer.valueOf(((Element) dateDayOfWeekPath.selectSingleNode(element)).getTextTrim()));

        XPath dateDayPath = new Dom4jXPath(buildXPath(prefix, "/CreateWorkspace/timeZoneInformation/" + typeOfDate + "/day"));
        dateDayPath.setNamespaceContext(nc);
        date.setDay(Integer.valueOf(((Element) dateDayPath.selectSingleNode(element)).getTextTrim()));

        XPath dateHourPath = new Dom4jXPath(buildXPath(prefix, "/CreateWorkspace/timeZoneInformation/" + typeOfDate + "/hour"));
        dateHourPath.setNamespaceContext(nc);
        date.setHour(Integer.valueOf(((Element) dateHourPath.selectSingleNode(element)).getTextTrim()));

        XPath dateMinutePath = new Dom4jXPath(buildXPath(prefix, "/CreateWorkspace/timeZoneInformation/" + typeOfDate + "/minute"));
        dateMinutePath.setNamespaceContext(nc);
        date.setMinute(Integer.valueOf(((Element) dateMinutePath.selectSingleNode(element)).getTextTrim()));

        XPath dateSecondPath = new Dom4jXPath(buildXPath(prefix, "/CreateWorkspace/timeZoneInformation/" + typeOfDate + "/second"));
        dateSecondPath.setNamespaceContext(nc);
        date.setSecond(Integer.valueOf(((Element) dateSecondPath.selectSingleNode(element)).getTextTrim()));

        XPath dateMillisecondsPath = new Dom4jXPath(buildXPath(prefix, "/CreateWorkspace/timeZoneInformation/" + typeOfDate + "/milliseconds"));
        dateMillisecondsPath.setNamespaceContext(nc);
        date.setMilliseconds(Integer.valueOf(((Element) dateMillisecondsPath.selectSingleNode(element)).getTextTrim()));

        return date;
    }
}
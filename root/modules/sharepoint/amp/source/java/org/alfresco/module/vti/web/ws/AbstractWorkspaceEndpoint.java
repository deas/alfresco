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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Parent class of Meeting Workspace Endpoints.
 *
 * @author Nick Burch
 */
public abstract class AbstractWorkspaceEndpoint extends AbstractEndpoint
{
    // xml namespace prefix
    private static String prefix = "mt";

    private static Log logger = LogFactory.getLog(AbstractWorkspaceEndpoint.class);

    // handler that provides methods for operating with meetings
    protected MeetingServiceHandler handler;
    public AbstractWorkspaceEndpoint(MeetingServiceHandler handler)
    {
        this.handler = handler;
    }
    
    /**
     * Is a site required? -1 means no site is required, otherwise the
     * result is the {@link VtiSoapException} error code if no site is found
     */
    protected abstract long getSiteRequired();
    
    /**
     * Performs the appropriate action on the workspace
     */
    protected abstract void executeWorkspaceAction(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse,
            Element requestElement, SimpleNamespaceContext nc,
            String siteName, String title, String templateName, int lcid) throws Exception;

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
        
        // Check to see if a site was given
        // Some Workspace Endpoints need a site, for others if a site
        //  is given then that indicates an attempt to work on a subsite
        String siteName = getDwsFromUri(soapRequest);
        if ("".equals(siteName) || "/".equals(siteName))
        {
            // No site was given. Is that a problem?
            if (getSiteRequired() == -1)
            {
                // Request is on the root and that's allowed
            }
            else
            {
                // No site given, but one is required
                throw new VtiSoapException("A Site Name must be supplied", getSiteRequired());
            }
        }
        else
        {
            // A site was given, was one expected?
            if (getSiteRequired() == -1)
            {
                // Report that we don't support a subsite
                throw new VtiSoapException("vti.meeting.error.subsites", 1l);
            }
            else
            {
                // A site was given and was expected, all is good
                if (siteName.startsWith("/"))
                {
                    // Strip the leading slash off
                    siteName = siteName.substring(1);
                }
            }
        }
        if (logger.isDebugEnabled())
        {
           logger.debug("Site Name is '" + siteName + "'"); 
        }
        

        // Process the details of the request
        Element requestElement = soapRequest.getDocument().getRootElement();

        
        // The Title is usually required required
        if (logger.isDebugEnabled())
            logger.debug("Getting title from request.");
        XPath titlePath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/title"));
        titlePath.setNamespaceContext(nc);
        Element titleE = (Element) titlePath.selectSingleNode(requestElement);

        String title = null;
        if (titleE != null && titleE.getText() != null && titleE.getText().length() > 0)
        {
            title = titleE.getText();

            if (logger.isDebugEnabled())
            {
                logger.debug("Workspace title is " + title);
            }
        }
        else
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("No title found in request to " + getName());
            }
        }
        

        // Template Name may be optional
        if (logger.isDebugEnabled())
            logger.debug("Getting templateName from request.");
        XPath templateNamePath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/templateName"));
        templateNamePath.setNamespaceContext(nc);
        Element templateNameE = (Element) templateNamePath.selectSingleNode(requestElement);
        
        String templateName = null;
        if (templateNameE != null)
        {
            templateName = templateNameE.getText();
        }

        
        // LCID (Language Code Identifier) may be optional
        if (logger.isDebugEnabled())
            logger.debug("Getting lcid from request.");
        XPath lcidPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/lcid"));
        lcidPath.setNamespaceContext(nc);
        Element lcidE = (Element) lcidPath.selectSingleNode(requestElement);
        
        int lcid = 0;
        if (lcidE != null)
        {
            lcid = Integer.parseInt(lcidE.getText());
        }
        

        // Have the workspace actioned
        executeWorkspaceAction(soapRequest, soapResponse, requestElement, nc,
                               siteName, title, templateName, lcid);

        // All done
        if (logger.isDebugEnabled())
        {
            logger.debug("SOAP method with name " + getName() + " is finished.");
        }
    }
    
    /**
     * Builds most of the standard response
     */
    protected Element buildWorkspaceResponse(VtiSoapResponse soapResponse) throws Exception
    {
        // Creating soap response
        soapResponse.setContentType("text/xml");
        return buildResultTag(soapResponse).addElement(getName());
    }

    protected TimeZoneInformation getTimeZoneInformation(Element requestElement) throws Exception
    {
        TimeZoneInformation timeZoneInforation = new TimeZoneInformation();

        SimpleNamespaceContext nc = new SimpleNamespaceContext();
        nc.addNamespace(prefix, namespace);
        nc.addNamespace(soapUriPrefix, soapUri);
        
        // Is the timezone present?
        XPath timezonePath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/timeZoneInformation"));
        timezonePath.setNamespaceContext(nc);
        Element tzElement = (Element)timezonePath.selectSingleNode(requestElement);
        if (tzElement == null)
        {
            if (logger.isDebugEnabled())
                logger.debug("No TimeZone information supplied, using the default");
            return null;
        }

        // Fetch the details of the timezone
        // TODO Fetch from the tzElement object
        XPath biasPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/timeZoneInformation/bias"));
        biasPath.setNamespaceContext(nc);
        timeZoneInforation.setBias(Integer.valueOf(((Element) biasPath.selectSingleNode(requestElement)).getTextTrim()));

        timeZoneInforation.setStandardDate(createTimeZoneInformationDate(requestElement, "standardDate"));

        XPath standardBiasPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/timeZoneInformation/standardBias"));
        standardBiasPath.setNamespaceContext(nc);
        timeZoneInforation.setStandardBias(Integer.valueOf(((Element) standardBiasPath.selectSingleNode(requestElement)).getTextTrim()));

        timeZoneInforation.setDaylightDate(createTimeZoneInformationDate(requestElement, "daylightDate"));

        XPath daylightBiasPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/timeZoneInformation/daylightBias"));
        daylightBiasPath.setNamespaceContext(nc);
        timeZoneInforation.setDaylightBias(Integer.valueOf(((Element) daylightBiasPath.selectSingleNode(requestElement)).getTextTrim()));

        return timeZoneInforation;
    }

    private TimeZoneInformationDate createTimeZoneInformationDate(Element element, String typeOfDate) throws Exception
    {
        TimeZoneInformationDate date = new TimeZoneInformationDate();

        SimpleNamespaceContext nc = new SimpleNamespaceContext();
        nc.addNamespace(prefix, namespace);
        nc.addNamespace(soapUriPrefix, soapUri);

        XPath dateYearPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/timeZoneInformation/" + typeOfDate + "/year"));
        dateYearPath.setNamespaceContext(nc);
        date.setYear(Integer.valueOf(((Element) dateYearPath.selectSingleNode(element)).getTextTrim()));

        XPath dateMonthPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/timeZoneInformation/" + typeOfDate + "/month"));
        dateMonthPath.setNamespaceContext(nc);
        date.setMonth(Integer.valueOf(((Element) dateMonthPath.selectSingleNode(element)).getTextTrim()));

        XPath dateDayOfWeekPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/timeZoneInformation/" + typeOfDate + "/dayOfWeek"));
        dateDayOfWeekPath.setNamespaceContext(nc);
        date.setDayOfWeek(Integer.valueOf(((Element) dateDayOfWeekPath.selectSingleNode(element)).getTextTrim()));

        XPath dateDayPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/timeZoneInformation/" + typeOfDate + "/day"));
        dateDayPath.setNamespaceContext(nc);
        date.setDay(Integer.valueOf(((Element) dateDayPath.selectSingleNode(element)).getTextTrim()));

        XPath dateHourPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/timeZoneInformation/" + typeOfDate + "/hour"));
        dateHourPath.setNamespaceContext(nc);
        date.setHour(Integer.valueOf(((Element) dateHourPath.selectSingleNode(element)).getTextTrim()));

        XPath dateMinutePath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/timeZoneInformation/" + typeOfDate + "/minute"));
        dateMinutePath.setNamespaceContext(nc);
        date.setMinute(Integer.valueOf(((Element) dateMinutePath.selectSingleNode(element)).getTextTrim()));

        XPath dateSecondPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/timeZoneInformation/" + typeOfDate + "/second"));
        dateSecondPath.setNamespaceContext(nc);
        date.setSecond(Integer.valueOf(((Element) dateSecondPath.selectSingleNode(element)).getTextTrim()));

        XPath dateMillisecondsPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/timeZoneInformation/" + typeOfDate + "/milliseconds"));
        dateMillisecondsPath.setNamespaceContext(nc);
        date.setMilliseconds(Integer.valueOf(((Element) dateMillisecondsPath.selectSingleNode(element)).getTextTrim()));

        return date;
    }
}
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

import java.util.Date;
import java.util.Locale;

import org.alfresco.module.vti.handler.ListServiceHandler;
import org.alfresco.module.vti.metadata.model.ListInfoBean;
import org.alfresco.module.vti.web.VtiFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

import com.ibm.icu.text.SimpleDateFormat;

/**
 * Parent class of List Endpoints, which need to return
 *  details of a List.
 * 
 * @author Nick Burch
 */
public abstract class AbstractListEndpoint extends AbstractEndpoint
{
	 private final static Log logger = LogFactory.getLog(AbstractListEndpoint.class);
	 private final static String DEFAULT_LOCALE = "1033";

    // handler that provides methods for operating with lists
    protected ListServiceHandler handler;

    // xml namespace prefix
    protected static final String prefix = "listsws";
    
    private static final SimpleDateFormat xmlDateFormat = 
       new SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.ENGLISH);


    /**
     * constructor
     *
     * @param handler
     */
    public AbstractListEndpoint(ListServiceHandler handler)
    {
        this.handler = handler;
    }
    
    /**
     * Does the List work, and returns the detail of the List
     */
    protected abstract ListInfoBean executeListAction(VtiSoapRequest soapRequest, 
          String dws, String listName, String description, int templateID) throws Exception;

    /**
     * Deletes document workspace
     * 
     * @param soapRequest Vti soap request ({@link VtiSoapRequest})
     * @param soapResponse Vti soap response ({@link VtiSoapResponse}) 
     */
    public void execute(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse) throws Exception   {
       if (logger.isDebugEnabled()) {
          logger.debug("SOAP method with name " + getName() + " is started.");
       }

       // mapping xml namespace to prefix
       SimpleNamespaceContext nc = new SimpleNamespaceContext();
       nc.addNamespace(soapUriPrefix, soapUri);
       nc.addNamespace(prefix, namespace);

       String dws = getDwsFromUri(soapRequest);        

       // Get the listName parameter from the request
       XPath listNameXPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/listName"));
       listNameXPath.setNamespaceContext(nc);
       Element listNameE = (Element) listNameXPath.selectSingleNode(soapRequest.getDocument().getRootElement());
       String listName = null;
       if (listNameE != null)
       {
          listName = listNameE.getTextTrim();
       }
       
       // Get the description parameter from the request
       XPath descriptionXPath = new Dom4jXPath(buildXPath(prefix, "/AddList/description"));
       descriptionXPath.setNamespaceContext(nc);
       Element descriptionE = (Element) descriptionXPath.selectSingleNode(soapRequest.getDocument().getRootElement());
       String description = null;
       if (descriptionE != null)
       {
          description = descriptionE.getTextTrim();
       }
       
       // Get the template ID parameter from the request
       XPath templateXPath = new Dom4jXPath(buildXPath(prefix, "/AddList/templateID"));
       templateXPath.setNamespaceContext(nc);
       Element templateE = (Element) templateXPath.selectSingleNode(soapRequest.getDocument().getRootElement());
       int templateID = -1;
       if (templateE != null)
       {
          templateID = Integer.parseInt( templateE.getTextTrim() );
       }


       // Have the List Added / Fetched
       ListInfoBean list = executeListAction(soapRequest, dws, listName, description, templateID);
       
       
       // Return the valid response contents
       Element root = soapResponse.getDocument().addElement(getName()+"Response", namespace);
       Element listResult = root.addElement(getName()+"Result");
       Element listE = listResult.addElement("List");

       listE.addAttribute("ID", list.getNodeRef().getId());
       listE.addAttribute("Name",  list.getName());
       listE.addAttribute("Title", list.getTitle());
       listE.addAttribute("Description", list.getDescription());
       listE.addAttribute("Author", list.getAuthor());
       listE.addAttribute("DefaultViewUrl", null); // TODO
       listE.addAttribute("ImageUrl", null); // TODO
       listE.addAttribute("FeatureId", ""); // Not feature based
       listE.addAttribute("BaseType", Integer.toString( list.getType().getBaseType() ));
       listE.addAttribute("ServerTemplate", Integer.toString( list.getType().getId() ));
       listE.addAttribute("Created", formatDate(list.getCreated()));
       listE.addAttribute("Modified", formatDate(list.getModified()));
       listE.addAttribute("Direction", "none");
       listE.addAttribute("Version", "1"); // Whole lists aren't versioned
       listE.addAttribute("ItemCount", Integer.toString( list.getNumItems() ));
       listE.addAttribute("EnableAttachments", "True");
       listE.addAttribute("EnableVersioning", null); // TODO

       // General Info
       Element regional = listE.addElement("RegionalSettings");
       regional.addElement("Language").addText(DEFAULT_LOCALE);
       regional.addElement("Locale").addText(DEFAULT_LOCALE);
       regional.addElement("AdvanceHijri").addText("0");
       regional.addElement("CalendarType").addText("1");
       regional.addElement("Time24").addText("True");
       regional.addElement("TimeZone").addText("0");
       regional.addElement("SortOrder").addText(DEFAULT_LOCALE);
       regional.addElement("Presence").addText("False");
       
       Element server = listE.addElement("ServerSettings");
       server.addElement("ServerVersion").addText(VtiFilter.EMULATED_SHAREPOINT_VERSION);
       server.addElement("RecycleBinEnabled").addText("False");
       server.addElement("ServerRelativeUrl").addText("/");
       
       // Field Details
       listE.addElement("Fields");
       
       // All done
       if (logger.isDebugEnabled()) {
          logger.debug("SOAP method with name " + getName() + " is finished.");
       }        
    }

    private String formatDate(Date date)
    {
       if(date == null)
       {
          return null;
       }
       return xmlDateFormat.format(date);
    }
}

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
	protected final static String DEFAULT_LOCALE = "1033";

    // handler that provides methods for operating with lists
    protected ListServiceHandler handler;

    // xml namespace prefix
    protected String prefix = "listsws";
    
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

       // Grab the site name
       String dws = getDwsFromUri(soapRequest);        

       // Fetch the root of the document
       Element requestElement = soapRequest.getDocument().getRootElement();

       // Get the listName parameter from the request
       XPath listNameXPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/listName"));
       listNameXPath.setNamespaceContext(nc);
       Element listNameE = (Element) listNameXPath.selectSingleNode(requestElement);
       String listName = null;
       if (listNameE != null)
       {
          listName = listNameE.getTextTrim();
       }
       
       // Have any further details fetched as needed
       executeListActionDetails(soapRequest, soapResponse, dws, listName, requestElement, nc);
       
       // All done
       if (logger.isDebugEnabled()) {
          logger.debug("SOAP method with name " + getName() + " is finished.");
       }        
    }
     
    protected void executeListActionDetails(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse,
         String siteName, String listName, Element requestElement, SimpleNamespaceContext nc) throws Exception
    {
       // Get the description parameter from the request
       XPath descriptionXPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/description"));
       descriptionXPath.setNamespaceContext(nc);
       Element descriptionE = (Element) descriptionXPath.selectSingleNode(soapRequest.getDocument().getRootElement());
       String description = null;
       if (descriptionE != null)
       {
          description = descriptionE.getTextTrim();
       }
       
       // Get the template ID parameter from the request
       XPath templateXPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/templateID"));
       templateXPath.setNamespaceContext(nc);
       Element templateE = (Element) templateXPath.selectSingleNode(soapRequest.getDocument().getRootElement());
       int templateID = -1;
       if (templateE != null)
       {
          templateID = Integer.parseInt( templateE.getTextTrim() );
       }


       // Have the List Added / Fetched
       ListInfoBean list = executeListAction(soapRequest, siteName, listName, description, templateID);
       
       // Have it rendered
       renderList(soapRequest, soapResponse, siteName, list);
    }
    
    protected void renderList(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse,
                              String siteName, ListInfoBean list) throws Exception
    {
       // Return the valid response contents
       Element root = soapResponse.getDocument().addElement(getName()+"Response", namespace);
       Element listResult = root.addElement(getName()+"Result");
       Element listE = listResult.addElement("List");
       
       // Render the list into this element
       String siteUrl = getContext(soapRequest) + siteName;
       renderListDefinition(list, siteName, siteUrl, listE);
       
       // Field Details
       Element fields = listE.addElement("Fields");
       renderFields(siteName, list, fields);
    }
     
    protected void renderListDefinition(ListInfoBean list, String siteName, String siteUrl, Element listE)
    {
       listE.addAttribute("ID", list.getId());
       listE.addAttribute("Name",  list.getName());
       listE.addAttribute("Title", list.getTitle());
       listE.addAttribute("Description", list.getDescription());
       listE.addAttribute("Author", list.getAuthor());
       listE.addAttribute("DefaultViewUrl", null); // TODO
       listE.addAttribute("ImageUrl", null); // TODO
       listE.addAttribute("WebFullUrl", siteUrl);
       listE.addAttribute("RootFolder", siteUrl + "/" + list.getName()); // TODO
       listE.addAttribute("FeatureId", ""); // Not feature based
       listE.addAttribute("BaseType", Integer.toString( list.getType().getBaseType() ));
       listE.addAttribute("ServerTemplate", Integer.toString( list.getType().getId() ));
       listE.addAttribute("Created", formatListDate(list.getCreated()));
       listE.addAttribute("Modified", formatListDate(list.getModified()));
       listE.addAttribute("Direction", "none");
       
       listE.addAttribute("EnableVersioning", "False"); // TODO Is this right?
       listE.addAttribute("EnableMinorVersion", "False");
       listE.addAttribute("Version", "1"); // Whole lists aren't versioned
       listE.addAttribute("MajorVersionLimit", "0");
       listE.addAttribute("MajorWithMinorVersionsLimit", "0");
       listE.addAttribute("AllowDeletion", "True"); // TODO Permissions
       
       listE.addAttribute("ThumbnailSize", "0");
       listE.addAttribute("WebImageWidth", "0");
       listE.addAttribute("WebImageHeight", "0");
       listE.addAttribute("AnonymousPermMask", "0");
       listE.addAttribute("ReadSecurity", "1");
       listE.addAttribute("WriteSecurity", "1");
       listE.addAttribute("EventSinkAssembly", "");
       listE.addAttribute("EventSinkClass", "");
       listE.addAttribute("EventSinkData", "");
       listE.addAttribute("EmailInsertsFolder", "");
       listE.addAttribute("EmailAlias", "");
       listE.addAttribute("SendToLocation", "");
       listE.addAttribute("WorkFlowId", "00000000-0000-0000-0000-000000000000");
       listE.addAttribute("HasUniqueScopes", "False");
       listE.addAttribute("AllowMultiResponses", "False");
       listE.addAttribute("Hidden", "False");
       listE.addAttribute("MultipleDataList", "False");
       listE.addAttribute("Ordered", "False");
       listE.addAttribute("ShowUser", "True");
       listE.addAttribute("RequireCheckout", "False");
       
       listE.addAttribute("ItemCount", Integer.toString( list.getNumItems() ));
       listE.addAttribute("EnableAttachments", "True");
       listE.addAttribute("EnableModeration", "False");

       // General Info
       /*Element regional = listE.addElement("RegionalSettings");
       regional.addElement("Language").addText(DEFAULT_LOCALE);
       regional.addElement("Locale").addText(DEFAULT_LOCALE);
       regional.addElement("AdvanceHijri").addText("0");
       regional.addElement("CalendarType").addText("1");
       regional.addElement("Time24").addText("True");
       regional.addElement("TimeZone").addText(String.valueOf(VtiUtils.getServerOffset()));
       regional.addElement("SortOrder").addText(DEFAULT_LOCALE); // 2070 ?
       regional.addElement("Presence").addText("False");
       
       Element server = listE.addElement("ServerSettings");
       server.addElement("ServerVersion").addText(VtiFilter.EMULATED_SHAREPOINT_VERSION);
       server.addElement("RecycleBinEnabled").addText("False");
       server.addElement("ServerRelativeUrl").addText("/" + siteName);*/
    }
    
    protected void renderFields(String siteName, ListInfoBean list, Element fieldsElement) throws Exception
    {
        // TODO Details on all the fields
    }

    protected String formatListDate(Date date)
    {
       if(date == null)
       {
          return null;
       }
       return xmlDateFormat.format(date);
    }
}

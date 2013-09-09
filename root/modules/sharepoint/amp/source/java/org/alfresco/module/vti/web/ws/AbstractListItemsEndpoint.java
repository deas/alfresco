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

import java.net.URLDecoder;
import java.util.Date;

import org.alfresco.module.vti.handler.ListServiceHandler;
import org.alfresco.module.vti.handler.MethodHandler;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.handler.alfresco.VtiUtils;
import org.alfresco.module.vti.metadata.dic.VtiError;
import org.alfresco.module.vti.metadata.model.DocMetaInfo;
import org.alfresco.module.vti.metadata.model.DocsMetaInfo;
import org.alfresco.module.vti.metadata.model.ListInfoBean;
import org.alfresco.repo.site.SiteDoesNotExistException;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.util.ISO8601DateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Parent class of the GetListItems* methods from lists web service
 * (Get all Items, Get Items Since ... etc)
 *
 * @author PavelYur
 */
public abstract class AbstractListItemsEndpoint extends AbstractListEndpoint
{
    private static Log logger = LogFactory.getLog(GetListItemsEndpoint.class);

    // handler that provides methods for operating with documents and folders
    protected MethodHandler methodHandler;
    // helper class to manipulate with paths
    private VtiPathHelper pathHelper;
    
    /**
     * constructor
     *
     * @param handler that provides methods for operating with documents and folders
     */
    public AbstractListItemsEndpoint(ListServiceHandler listHander, MethodHandler handler)
    {
        super(listHander);
        this.methodHandler = handler;
        this.prefix = "lists";
    }
    
    /**
     * @param pathHelper the pathHelper to set
     */
    public void setPathHelper(VtiPathHelper pathHelper)
    {
        this.pathHelper = pathHelper;
    }
    
    /**
     * Returns information about items in the list based on the specified query
     */
    @Override
    protected void executeListActionDetails(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse, String siteName,
            String listName, Element requestElement, SimpleNamespaceContext nc) throws Exception
    {
        // Have the List Fetched
        ListInfoBean list = null;
        try
        {
           list = handler.getList(listName, siteName);
        }
        catch(SiteDoesNotExistException se)
        {
           // The specification defines the exact code that must be
           //  returned in case of a file not being found
           long code = VtiError.V_LIST_NOT_FOUND.getErrorCode();
           String message = "Site not found: " + se.getMessage();
           throw new VtiSoapException(message, code, se);
        }
        catch(FileNotFoundException fnfe)
        {
           // The specification defines the exact code that must be
           //  returned in case of a file not being found
           long code = VtiError.V_LIST_NOT_FOUND.getErrorCode();
           String message = "List not found: " + fnfe.getMessage();
           throw new VtiSoapException(message, code, fnfe);
        }

        // Get the folder parameter for listing
        XPath listFolderPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/queryOptions/QueryOptions/Folder"));
        listFolderPath.setNamespaceContext(nc);
        Element listFolder = (Element) listFolderPath.selectSingleNode(soapRequest.getDocument().getRootElement());
        
        // There are two ways to get changes since a date, explicit date or token (also date)
        Date changesSince = null;

        // Get the "Changes Since" date for the listing
        XPath changesSinceXPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/since"));
        changesSinceXPath.setNamespaceContext(nc);
        Element changesSinceE = (Element) changesSinceXPath.selectSingleNode(requestElement);
        if (changesSinceE != null)
        {
            changesSince = ISO8601DateFormat.parse(changesSinceE.getTextTrim());
        }
        
        // Alternative is "Changes Since Token", which is also a date
        XPath changesSinceTokenXPath = new Dom4jXPath(buildXPath(prefix, "/"+getName()+"/changeToken"));
        changesSinceTokenXPath.setNamespaceContext(nc);
        Element changesSinceTokenE = (Element) changesSinceTokenXPath.selectSingleNode(requestElement);
        if (changesSinceTokenE != null)
        {
            changesSince = ISO8601DateFormat.parse(changesSinceTokenE.getTextTrim());
        }
        

        // TODO Implement the correct error handling and error codes
        String timeNow = ISO8601DateFormat.format(new Date());
        DocsMetaInfo result = null;

        if (!siteName.equals(""))
        {
            String initialUrl = pathHelper.resolveListName(listName);
            if (listFolder != null)
            {
                initialUrl = URLDecoder.decode(listFolder.getTextTrim(), "UTF-8");
            }
            
            result = getListInfo(siteName, list, initialUrl, changesSince);
        }

        // creating soap response
        Element responseElement = soapResponse.getDocument().addElement("GetListItemsResponse", namespace);
        Element resultElement = responseElement.addElement("GetListItemsResult");       
        Element itemsElement = resultElement.addElement("listitems");
        itemsElement.addNamespace("rs", "urn:schemas-microsoft-com:rowset");
        itemsElement.addNamespace("z", "#RowsetSchema");
        Element dataElement = itemsElement.addElement("rs:data");

        // Change tracking information
        itemsElement.addAttribute("TimeStamp", timeNow);
        if ("GetListItemChangesSinceToken".equals(getName()))
        {
            Element changes = itemsElement.addElement("Changes");
            changes.addAttribute("LastChangeToken", timeNow);
        }
        
        int itemCount = 0;
        
        if (result != null)
        {
            int id = 1;

            for (DocMetaInfo file : result.getFileMetaInfoList())
            {
                buildRow(dataElement, file, id++, soapRequest);
            }

            for (DocMetaInfo folder : result.getFolderMetaInfoList())
            {
                buildRow(dataElement, folder, id++, soapRequest);
            }

            itemCount = result.getFileMetaInfoList().size() + result.getFolderMetaInfoList().size();
        }

        dataElement.addAttribute("ItemCount", Integer.toString(itemCount));

        if (logger.isDebugEnabled())
        {
            logger.debug("Soap Method with name " + getName() + " is finished.");
        }
    }
    
    protected abstract DocsMetaInfo getListInfo(String siteName, ListInfoBean list, String initialUrl, Date since);
    
    /**
     * Not used, we are too specific
     */
    @Override
    protected ListInfoBean executeListAction(VtiSoapRequest soapRequest,
         String dws, String listName, String description, int templateID) throws Exception 
    {
        throw new IllegalStateException("Should not be called, GetListItems* have special handling");
    }

    private void buildRow(Element dataElement, DocMetaInfo docMetaInfo, int id, VtiSoapRequest request)
    {
        String strId = Integer.toString(id);
        String prefix = strId + ";#";

        Element fileElement = dataElement.addElement("z:row");

        fileElement.addAttribute("ows_ID", strId);
        fileElement.addAttribute("ows_IsCheckedoutToLocal", prefix + (docMetaInfo.getSourcecontrolcheckedoutby() == null ? "0" : "1"));
        fileElement.addAttribute("ows_ContentType", docMetaInfo.isFolder() ? "Folder" : "Document");
        fileElement.addAttribute("ows_LinkFilename", docMetaInfo.getPath().substring(docMetaInfo.getPath().lastIndexOf('/') + 1));
        fileElement.addAttribute("ows_File_x0020_Size", docMetaInfo.getFilesize() == null ? prefix : (prefix + docMetaInfo.getFilesize()));
        fileElement.addAttribute("ows_Created", VtiUtils.convertToPropfindFormat(docMetaInfo.getTimecreated()));
        fileElement.addAttribute("ows_Modified", VtiUtils.convertToPropfindFormat(docMetaInfo.getTimelastmodified()));
        fileElement.addAttribute("ows_PermMask", "0x7fffffffffffffff");
        fileElement.addAttribute("ows_Editor", docMetaInfo.getModifiedBy());
        fileElement.addAttribute("ows_Last_x0020_Modified", prefix + VtiUtils.convertToPropfindFormat(docMetaInfo.getTimelastmodified()));
        fileElement.addAttribute("ows_FileLeafRef", prefix + docMetaInfo.getPath().substring(docMetaInfo.getPath().lastIndexOf('/') + 1));
        fileElement.addAttribute("ows_FileRef", prefix + docMetaInfo.getPath());
        fileElement.addAttribute("ows_FSObjType", prefix + (docMetaInfo.isFolder() ? "1" : "0"));
        fileElement.addAttribute("ows_EncodedAbsUrl", getHost(request) + getContext(request) + "/" + docMetaInfo.getPath());
        fileElement.addAttribute("ows_Created_x0020_Date", prefix + VtiUtils.convertToPropfindFormat(docMetaInfo.getTimecreated()));
        fileElement.addAttribute("ows_UniqueId", prefix + "{" + docMetaInfo.getId() + "}");

        if (!docMetaInfo.isFolder())
        {
            fileElement.addAttribute("ows_Modified_x0020_By", docMetaInfo.getModifiedBy());
            fileElement.addAttribute("ows_Created_x0020_By", docMetaInfo.getAuthor());
            fileElement.addAttribute("ows_FileSizeDisplay", docMetaInfo.getFilesize());
            
            String extension = "txt";
            int pos = docMetaInfo.getPath().lastIndexOf('.');
            
            if (pos != -1)
            {
                extension = docMetaInfo.getPath().substring(docMetaInfo.getPath().lastIndexOf('.') + 1);
            }

            // following attributes are used by client to determine application for editing
            fileElement.addAttribute("ows_DocIcon", extension);
            fileElement.addAttribute("ows_File_x0020_Type", extension);

            if (docMetaInfo.getSourcecontrolcheckedoutby() != null)
            {
                fileElement.addAttribute("ows_CheckoutUser", prefix + docMetaInfo.getSourcecontrolcheckedoutby());
            }
        }
    }
}


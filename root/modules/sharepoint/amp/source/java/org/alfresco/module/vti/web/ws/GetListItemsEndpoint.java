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
import java.util.HashMap;

import org.alfresco.module.vti.handler.MethodHandler;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.handler.alfresco.VtiUtils;
import org.alfresco.module.vti.metadata.model.DocMetaInfo;
import org.alfresco.module.vti.metadata.model.DocsMetaInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Class for handling GetListItems method from lists web service
 *
 * @author PavelYur
 */
public class GetListItemsEndpoint extends AbstractEndpoint
{
    private static Log logger = LogFactory.getLog(GetListItemsEndpoint.class);

    // handler that provides methods for operating with documents and folders
    private MethodHandler handler;
    // helper class to manipulate with paths
    private VtiPathHelper pathHelper;

    // xml namespace prefix
    private static String prefix = "lists";
    
    /**
     * constructor
     *
     * @param handler that provides methods for operating with documents and folders
     */
    public GetListItemsEndpoint(MethodHandler handler)
    {
        this.handler = handler;
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

        // getting listName parameter from request
        XPath listIdPath = new Dom4jXPath(buildXPath(prefix, "/GetListItems/listName"));
        listIdPath.setNamespaceContext(nc);
        
        // getting folder parameter for listing
        XPath listFolderPath = new Dom4jXPath(buildXPath(prefix, "/GetListItems/queryOptions/QueryOptions/Folder"));
        listFolderPath.setNamespaceContext(nc);
        
        Element listId = (Element) listIdPath.selectSingleNode(soapRequest.getDocument().getRootElement());
        Element listFolder = (Element) listFolderPath.selectSingleNode(soapRequest.getDocument().getRootElement());

        // TODO Implement the correct error handling and error codes
        
        String siteName = getDwsFromUri(soapRequest);
        
        if (siteName.startsWith("/"))
        {
            siteName = siteName.substring(1);
        }

        DocsMetaInfo result = null;

        if (!siteName.equals(""))
        {
            String initialUrl = pathHelper.resolveListName(listId.getTextTrim());
            if (listFolder != null)
            {
                initialUrl = URLDecoder.decode(listFolder.getTextTrim(), "UTF-8");
            }
            result = handler.getListDocuments(siteName, false, false, "", initialUrl, false, false, true, true, false, false, false, false, new HashMap<String, Object>(0), false);
        }

        // creating soap response
        Element responseElement = soapResponse.getDocument().addElement("GetListItemsResponse", namespace);
        Element resultElement = responseElement.addElement("GetListItemsResult");       
        Element itemsElement = resultElement.addElement("listitems");
        itemsElement.addNamespace("rs", "urn:schemas-microsoft-com:rowset");
        itemsElement.addNamespace("z", "#RowsetSchema");
        Element dataElement = itemsElement.addElement("rs:data");
        
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


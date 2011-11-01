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

import java.util.ArrayList;
import java.util.List;

import org.alfresco.module.vti.handler.VersionsServiceHandler;
import org.alfresco.module.vti.metadata.model.DocumentVersionBean;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Class for handling GetVersions method from versions web service
 *
 * @author PavelYur
 */
public class GetVersionsEndpoint extends AbstractEndpoint
{

    // handler that provides methods for operating with documents and folders
    private VersionsServiceHandler handler;

    // xml namespace prefix
    private static String prefix = "versions";

    private static Log logger = LogFactory.getLog(GetVersionsEndpoint.class);
    
    public GetVersionsEndpoint(VersionsServiceHandler handler)
    {
        this.handler = handler;
    }

    /**
     * Retrieves all versions of the specified document
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

        String host = getHost(soapRequest);
        String context = soapRequest.getAlfrescoContextName();
        String dws = getDwsFromUri(soapRequest);        
        
        if (logger.isDebugEnabled())
            logger.debug("Getting fileName parameter from request.");
        
        // Getting fileName parameter from request
        XPath fileNamePath = new Dom4jXPath(buildXPath(prefix, "/GetVersions/fileName"));
        fileNamePath.setNamespaceContext(nc);
        Element fileNameE = (Element) fileNamePath.selectSingleNode(soapRequest.getDocument().getRootElement());
        String fileName = fileNameE.getText();
        
        // Is it relative or absolute?
        if(fileName.startsWith(host))
        {
           String splitWith = context + dws;
           int splitAt = fileName.indexOf(splitWith);
           
           if(splitAt == -1)
           {
              logger.warn("Unable to find " + splitWith + " in absolute path " + fileName);
           }
           else
           {
              fileName = fileName.substring(splitAt + splitWith.length() + 1);
           }
        }
        
        if (logger.isDebugEnabled())
            logger.debug("Getting versions for file '" + dws + "/" + fileName + "'.");
        
        // Get all versions for the given file
        List<DocumentVersionBean> notSortedVersions;
        try
        {
           notSortedVersions = handler.getVersions(dws + "/" + fileName);
        }
        catch(FileNotFoundException e)
        {
           // The specification defines the exact message that must be
           //  returned in case of a file not being found
           long code = 0x80070002l;
           String msg = "The system cannot find the file specified. (Exception from HRESULT: 0x80070002)";
           throw new VtiSoapException(msg, code, e);
        }
        
        // creating soap response
        Element root = soapResponse.getDocument().addElement("GetVersionsResponse", namespace);
        Element getDwsMetaDataResult = root.addElement("GetVersionsResult");

        Element results = getDwsMetaDataResult.addElement("results", namespace);

        results.addElement("list").addAttribute("id", "");
        results.addElement("versioning").addAttribute("enabled", "1");
        results.addElement("settings").addAttribute("url", host + context + dws + "/documentDetails.vti?doc=" + notSortedVersions.get(0).getId());

        List<DocumentVersionBean> versions = new ArrayList<DocumentVersionBean>();
        
        versions.add(notSortedVersions.get(0));
        for (int i = notSortedVersions.size() - 1; i > 0; --i) {
            versions.add(notSortedVersions.get(i));
        }

        boolean isCurrent = true;
        for (DocumentVersionBean version : versions)
        {
            Element result = results.addElement("result");
            if (isCurrent)
            {
                // prefix @ means that it is current working version, it couldn't be restored or deleted
                result.addAttribute("version", "@" + version.getVersion());
                String url = host + context + dws + "/" + fileName.trim();
                result.addAttribute("url", url);
                isCurrent = false;
            }
            else
            {
                result.addAttribute("version", version.getVersion());
                String url = host + context + dws + version.getUrl();
                result.addAttribute("url", url);
            }            
            
            result.addAttribute("created", version.getCreatedTime());
            result.addAttribute("createdBy", version.getCreatedBy());
            result.addAttribute("size", String.valueOf(version.getSize()));
            result.addAttribute("comments", version.getComments());
        }
        
        if (logger.isDebugEnabled()) {
            String versionsStr = "";
            for (DocumentVersionBean version : versions)
            {
                versionsStr += version.getVersion() + " ";
            }
            logger.debug("The folloving versions [ "+ versionsStr + "] were retrieved");
        }            
        
        if (logger.isDebugEnabled())
        {
            logger.debug("Soap Method with name " + getName() + " is finished.");
        }
        
    }

}

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

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.vti.handler.ListsServiceHandler;
import org.alfresco.module.vti.handler.alfresco.VtiUtils;
import org.alfresco.module.vti.metadata.model.ListBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

import freemarker.core.Environment;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Class for handling GetList method from lists web service.
 * 
 * TODO Convert to be an {@link AbstractListEndpoint} and change
 *  this to handle data lists as well as component lists
 *
 * @author PavelYur
 */
public class GetListEndpoint extends AbstractEndpoint
{
    private static Log logger = LogFactory.getLog(GetWebCollectionEndpoint.class);

    // handler that provides methods for operating with documents and folders
    private ListsServiceHandler handler;    

    // xml namespace prefix
    private static String prefix = "lists";
    
    private Template template = null;
    
    /**
     * constructor
     *
     * @param handler that provides methods for operating with documents and folders
     */
    public GetListEndpoint(ListsServiceHandler handler)
    {
        this.handler = handler;
    }
    
    /**
     * Returns a schema for the specified list.
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
        
        // TODO Convert to using the correct list handlers
        // TODO Correctly handle errors as per the protocol documentation
        // Have the List Fetched
//        ListInfoBean list = null;
//        try
//        {
//           list = handler.getList(listName, dws);
//        }
//        catch(SiteDoesNotExistException se)
//        {
//           // The specification defines the exact code that must be
//           //  returned in case of a file not being found
//           long code = VtiError.V_LIST_NOT_FOUND.getErrorCode();
//           String message = "Site not found: " + se.getMessage();
//           throw new VtiSoapException(message, code, se);
//        }
//        catch(FileNotFoundException fnfe)
//        {
//           // The specification defines the exact code that must be
//           //  returned in case of a file not being found
//           long code = VtiError.V_LIST_NOT_FOUND.getErrorCode();
//           String message = "List not found: " + fnfe.getMessage();
//           throw new VtiSoapException(message, code, fnfe);
//        }

        

        // get site name that is used to list subsites
        String siteName = getDwsFromUri(soapRequest);
        
        // getting listName parameter from request
        XPath listIdPath = new Dom4jXPath(buildXPath(prefix, "/GetList/listName"));
        listIdPath.setNamespaceContext(nc);

        Element listId = (Element) listIdPath.selectSingleNode(soapRequest.getDocument().getRootElement());

        ListBean list = handler.getList(listId.getTextTrim());
        
        // TODO Push much of this logic down to AbstractListEndpoint
        
        Map<String, Object> freeMarkerMap = new HashMap<String, Object>();
        freeMarkerMap.put("list", list);
        freeMarkerMap.put("siteName", getContext(soapRequest) + siteName);
        freeMarkerMap.put("serverOffset", String.valueOf(VtiUtils.getServerOffset()));
        
        try
        {
            if (template == null)
            {
                template = new Template("GetListEndpoint", new InputStreamReader(getClass().getResourceAsStream("GetListEndpoint.ftl")), null, "utf-8");
            }
            Environment env = template.createProcessingEnvironment(freeMarkerMap, soapResponse.getWriter());
            env.setOutputEncoding("utf-8");
            env.process();
            soapResponse.getWriter().flush();
        }
        catch (TemplateException e)
        {
            throw new RuntimeException(e);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Soap Method with name " + getName() + " is finished.");
        }
    }
}

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
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.module.vti.handler.ListServiceHandler;
import org.alfresco.module.vti.handler.alfresco.VtiUtils;
import org.alfresco.module.vti.metadata.dic.VtiError;
import org.alfresco.module.vti.metadata.model.ListInfoBean;
import org.alfresco.repo.site.SiteDoesNotExistException;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import freemarker.core.Environment;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Class for handling GetList method from lists web service.
 * 
 * @author Nick Burch
 */
public class GetListEndpoint extends AbstractListEndpoint
{
    @SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(GetWebCollectionEndpoint.class);

    private Template template = null;
    
    /**
     * constructor
     *
     * @param handler that provides methods for operating with lists
     */
    public GetListEndpoint(ListServiceHandler handler)
    {
        super(handler);
    }
    
    /**
     * Fetches the details of the list
     */
    @Override
    protected ListInfoBean executeListAction(VtiSoapRequest soapRequest, String dws, String listName,
            String description, int templateID) throws Exception
    {
        // Have the List Fetched
        ListInfoBean list = null;
        try
        {
           list = handler.getList(listName, dws);
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

        // Return it for rendering
        return list;
    }
    
    /**
     * Currently based on a FTL Template, which has fake field details in it
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void renderFields(String siteName, ListInfoBean list, Element fieldsElement)
    throws Exception
    {
        Map<String, Object> freeMarkerMap = new HashMap<String, Object>();
        freeMarkerMap.put("list", list);
        freeMarkerMap.put("siteName", siteName);
        freeMarkerMap.put("serverOffset", String.valueOf(VtiUtils.getServerOffset()));
        
        try
        {
            if (template == null)
            {
                template = new Template("GetListEndpoint", new InputStreamReader(getClass().getResourceAsStream("GetListEndpoint.ftl")), null, "utf-8");
            }

            // Put it into a string
            StringWriter sw = new StringWriter();
            Environment env = template.createProcessingEnvironment(freeMarkerMap, sw);
            env.setOutputEncoding("utf-8");
            env.process();
            
            // Have it parsed into the DOM
            SAXReader reader = new SAXReader();
            Element ftlFields = reader.read(new StringReader(sw.toString())).getRootElement();
            
            // Copy it over
            for (Element field : (List<Element>)ftlFields.elements())
            {
                copyElement(field, fieldsElement);
            }
        }
        catch (TemplateException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void copyElement(Element source, Element target)
    {
        Element copy = target.addElement(source.getQName());
        for (Attribute attr : (List<Attribute>)source.attributes())
        {
            copy.addAttribute(attr.getQName(), attr.getValue());
        }
        for (Element child : (List<Element>)source.elements())
        {
            copyElement(child, copy);
        }
        if (source.getText() != null)
        {
            copy.setText(source.getText());
        }
    }
}

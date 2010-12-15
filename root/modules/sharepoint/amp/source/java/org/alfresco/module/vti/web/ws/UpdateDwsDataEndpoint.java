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

import org.alfresco.module.vti.handler.DwsServiceHandler;
import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.metadata.dic.CAMLMethod;
import org.alfresco.module.vti.metadata.model.LinkBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Class for handling UpdateDwsData soap method
 * 
 * @author EugeneZh
 */
public class UpdateDwsDataEndpoint extends AbstractEndpoint
{
    private final static Log logger = LogFactory.getLog(RemoveDwsUserEndpoint.class);

    // handler that provides methods for operating with documents and folders
    private DwsServiceHandler handler;

    // xml namespace prefix
    private static String prefix = "dws";

    /**
     * Constructor
     * 
     * @param handler ({@link DwsServiceHandler})
     */
    public UpdateDwsDataEndpoint(DwsServiceHandler handler)
    {
        this.handler = handler;
    }

    /**
     * Update dws data for site
     * 
     * @param soapRequest Vti soap request ({@link VtiSoapRequest})
     * @param soapResponse Vti soap response ({@link VtiSoapResponse})
     */
    @SuppressWarnings("unchecked")
    public void execute(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("SOAP method with name " + getName() + " is started.");
        }
        // mapping xml namespace to prefix
        SimpleNamespaceContext nc = new SimpleNamespaceContext();
        nc.addNamespace(prefix, namespace);
        nc.addNamespace(soapUriPrefix, soapUri);

        XPath updatesPath = new Dom4jXPath(buildXPath(prefix, "/UpdateDwsData/updates"));
        updatesPath.setNamespaceContext(nc);
        Element updates = (Element) updatesPath.selectSingleNode(soapRequest.getDocument().getRootElement());

        Document updatesDocument = DocumentHelper.parseText(updates.getText());
        XPath setVarPath = new Dom4jXPath("/Batch/Method/SetVar");
        List<Element> list = setVarPath.selectNodes(updatesDocument);

        LinkBean link = createLink(list);
        XPath methodPath = new Dom4jXPath("/Batch/Method");
        CAMLMethod method = CAMLMethod.value(((Element) methodPath.selectSingleNode(updatesDocument)).attributeValue("ID"));

        Element root = soapResponse.getDocument().addElement("UpdateDwsDataResponse", namespace);
        Element updateDwsDataResult = root.addElement("UpdateDwsDataResult");
        Element results = updateDwsDataResult.addElement("Results");
        Element result = results.addElement("Result");
        result.addAttribute("ID", method.toString());
        result.addAttribute("Code", "0");

        try
        {
            link = handler.updateDwsData(link, method, VtiPathHelper.removeSlashes(getDwsFromUri(soapRequest)));
        }
        catch (VtiHandlerException e)
        {
            if (e.getMessage().equals(VtiHandlerException.ITEM_NOT_FOUND))
            {
                result.addElement("Error").addAttribute("ID", "5");
                return;
            }
            else if (e.getMessage().equals(VtiHandlerException.LIST_NOT_FOUND))
            {
                result.addElement("Error").addAttribute("ID", "7");
                return;
            }
            else
            {
                throw e;
            }
        }

        if (method.toString().equals("New"))
        {
            result.addText(generateXml(link));
        }
        else
        {
            result.addAttribute("List", "");
            result.addAttribute("Version", "0");
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("SOAP method with name " + getName() + " is finished.");
        }
    }

    /**
     * Create link
     * 
     * @param list
     * @return link bean ({@link LinkBean})
     */
    private LinkBean createLink(List<Element> list)
    {
        LinkBean linkBean = new LinkBean();

        for (Element element : list)
        {
            String value = element.attribute("Name").getValue();
            String text = element.getText().replaceAll("\"", "");

            if (value.equalsIgnoreCase("urn:schemas-microsoft-com:office:office#URL"))
            {
                int startIndex = text.indexOf(", ");
                linkBean.setUrl(text.substring(0, startIndex));
                linkBean.setDescription(text.substring(startIndex + 2));
            }

            if (value.equalsIgnoreCase("urn:schemas-microsoft-com:office:office#Comments"))
            {
                linkBean.setComments(text);
            }

            if (value.equalsIgnoreCase("ID"))
            {
                linkBean.setId(text);
            }
        }

        return linkBean;
    }
}
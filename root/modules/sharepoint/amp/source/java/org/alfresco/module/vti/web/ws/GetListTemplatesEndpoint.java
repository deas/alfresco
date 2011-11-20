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

import org.alfresco.module.vti.handler.ListServiceHandler;
import org.alfresco.module.vti.metadata.model.ListTypeBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

/**
 * Class for handling the GetListTemplates soap method
 * 
 * @author Nick Burch
 */
public class GetListTemplatesEndpoint extends AbstractEndpoint
{
	private final static Log logger = LogFactory.getLog(GetListTemplatesEndpoint.class);

    // handler that provides methods for operating with lists
    private ListServiceHandler handler;

    // xml namespace prefix
    @SuppressWarnings("unused") // Unused until fully implemented
    private static String prefix = "webss"; 

    /**
     * constructor
     *
     * @param handler
     */
    public GetListTemplatesEndpoint(ListServiceHandler handler)
    {
        this.handler = handler;
    }

    /**
     * Returns the list templates (kinds of document library
     *  components) that are available
     * 
     * @param soapRequest Vti soap request ({@link VtiSoapRequest})
     * @param soapResponse Vti soap response ({@link VtiSoapResponse}) 
     */
    public void execute(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse) throws Exception   
    {
       if (logger.isDebugEnabled()) {
          logger.debug("SOAP method with name " + getName() + " is started.");
       }

       // Fetch the list details
       List<ListTypeBean> lists = handler.getAvailableListTypes();
       
       // Return the details of the list templates
       Element root = soapResponse.getDocument().addElement("GetListTemplatesResponse", namespace);
       Element getListTemplatesRes = root.addElement("GetListTemplatesResult");
       Element listTemplates = getListTemplatesRes.addElement("ListTemplates");
       
       for(ListTypeBean list : lists)
       {
          Element template = listTemplates.addElement("ListTemplate");
          template.addAttribute("Type", Integer.toString(list.getId()));
          template.addAttribute("BaseType", Integer.toString(list.getBaseType()));
          template.addAttribute("Name", list.getName());
          if(list.getTitle() != null)
          {
             template.addAttribute("DisplayName", list.getTitle());
          }
          if(list.getDescription() != null)
          {
             template.addAttribute("Description", list.getDescription());
          }
          
          // DataLists aren't unique, others are
          template.addAttribute("Unique", Boolean.toString( !list.isDataList() ));
       }

       if (logger.isDebugEnabled()) {
          logger.debug("SOAP method with name " + getName() + " is finished.");
       }        
    }
}

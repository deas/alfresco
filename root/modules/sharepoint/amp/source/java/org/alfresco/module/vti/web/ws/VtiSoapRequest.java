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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.alfresco.module.vti.web.VtiRequestDispatcher;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

/**
 * VtiSoapRequest is wrapper for HttpServletRequest. It provides specific methods 
 * which allow to retrieve appropriate xml document from request data. 
 * 
 * @author Stas Sokolovsky
 *
 */
public class VtiSoapRequest extends HttpServletRequestWrapper
{
   public static String SOAP_11_ENVELOPE_NS = "http://schemas.xmlsoap.org/soap/envelope/";
   public static String SOAP_12_ENVELOPE_NS = "http://www.w3.org/2003/05/soap-envelope"; 
   
	private Document document;
	private String version;
    
	 /**
     * Constructor
     * 
     * @param request HttpServletRequest 
     */
    public VtiSoapRequest(HttpServletRequest request)
    {
        super(request);
        try
        {
            SAXReader reader = new SAXReader();
            reader.setValidation(false);
            document = reader.read(request.getInputStream());
            
            String ns = document.getRootElement().getNamespaceURI(); 
            if(SOAP_11_ENVELOPE_NS.equals(ns))
            {
               version = "1.1";
            }
            else if(SOAP_12_ENVELOPE_NS.equals(ns))
            {
               version = "1.2";
            }
        }
        catch (Exception e)
        {
            document = null;
        }
    }

    /**
     * Get xml document
     *  
     * @return Document request xml document
     */
    public Document getDocument()
    {
        return document;
    }
    
    /**
     * Get the SOAP version, either 1.1 or 1.2
     */
    public String getVersion()
    {
        return version;
    }
    
    /**
     * Get alfresco context name
     *  
     * @return String alfresco context name
     */
    public String getAlfrescoContextName()
    {
        return (String) this.getAttribute(VtiRequestDispatcher.VTI_ALFRESCO_CONTEXT);
    }
}

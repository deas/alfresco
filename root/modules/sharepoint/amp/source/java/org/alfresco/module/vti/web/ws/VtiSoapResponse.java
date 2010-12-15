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

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.XMLWriter;


/**
 * VtiSoapResponse is wrapper for HttpServletResponse. It provides specific methods 
 * which allow to generate response for soap requests. 
 *  
 * @author Stas Sokolovsky
 *
 */
public class VtiSoapResponse extends HttpServletResponseWrapper
{
	
	public static final String NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/";
    
	Document document;
    
	/**
     * Constructor
     * 
     * @param response HttpServletResponse 
     */
    public VtiSoapResponse(HttpServletResponse response)
    {
        super(response);
        document = DocumentHelper.createDocument();
        Element envelope = document.addElement(QName.get("Envelope", "s", VtiSoapResponse.NAMESPACE));
        envelope.addElement(QName.get("Body", "s", VtiSoapResponse.NAMESPACE));
    }
    
    /**
     * Get xml document that will be written to response
     *  
     * @return Element response xml document
     */
    public Element getDocument()
    {
        return (Element)document.getRootElement().elements().get(0);
    }
    
    /**
     * Write document to response
     *  
     * @return Document request xml document
     */
    @Override
    public void flushBuffer() throws IOException 
    {
        try
        {
            XMLWriter output = new XMLWriter(getOutputStream());
            output.write(document);
        }
        catch (Exception e)
        {
            // ignore
        }  
        
    }
    
}

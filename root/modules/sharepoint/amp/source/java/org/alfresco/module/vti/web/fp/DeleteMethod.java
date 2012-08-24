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

package org.alfresco.module.vti.web.fp;

import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.repo.webdav.WebDAV;
import org.alfresco.repo.webdav.WebDAVServerException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * Implements the WebDAV DELETE method with VTI specific
 * 
 * @author Pavel Yurkevich
 */
public class DeleteMethod extends org.alfresco.repo.webdav.DeleteMethod
{
    private static final String HEADER_X_MSDAVEXT_ERROR = "X-MSDAVEXT_Error";
    private static final String SC_LOCKED_DESC = "Locked";
    
    private HashMap<String, String> namespaceMap = new HashMap<String, String>();
    
    private String alfrescoContext;

    public DeleteMethod(String alfrescoContext)
    {
        this.alfrescoContext = alfrescoContext;
        namespaceMap.put("urn:schemas-microsoft-com:office:office", "Office");
        namespaceMap.put("http://schemas.microsoft.com/repl/", "Repl");
        namespaceMap.put("urn:schemas-microsoft-com:", "Z");
    }
    
    /**
     * Returns the path, excluding the Servlet Context (if present)
     * @see org.alfresco.repo.webdav.WebDAVMethod#getPath()
     */
    @Override
    public String getPath()
    {
        return AbstractMethod.getPathWithoutContext(alfrescoContext, m_request);
    }
    
    @Override
    protected void executeImpl() throws WebDAVServerException, Exception
    {
        try
        {
            super.executeImpl();
            m_response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
        catch (WebDAVServerException e) 
        {
            if (e.getHttpStatusCode() == WebDAV.WEBDAV_SC_LOCKED)
            {
                // SharePoint requires a special response for the case of
                //  trying to delete a locked document
                m_response.setStatus(WebDAV.WEBDAV_SC_MULTI_STATUS);
                m_response.setContentType(WebDAV.XML_CONTENT_TYPE);
                m_response.addHeader(HEADER_X_MSDAVEXT_ERROR, "589838"); // TODO Don't hard code this constant

                XMLWriter xml = createXMLWriter();

                xml.startDocument();

                String nsdec = generateNamespaceDeclarations(namespaceMap);
                xml.startElement(WebDAV.DAV_NS, WebDAV.XML_MULTI_STATUS + nsdec, WebDAV.XML_NS_MULTI_STATUS + nsdec, getDAVHelper().getNullAttributes());

                xml.startElement(WebDAV.DAV_NS, WebDAV.XML_RESPONSE, WebDAV.XML_NS_RESPONSE, getDAVHelper().getNullAttributes());
                
                xml.startElement(WebDAV.DAV_NS, WebDAV.XML_HREF, WebDAV.XML_NS_HREF, getDAVHelper().getNullAttributes());
                xml.write(m_request.getRequestURL().toString());
                xml.endElement(WebDAV.DAV_NS, WebDAV.XML_HREF, WebDAV.XML_NS_HREF);
                
                xml.startElement(WebDAV.DAV_NS, WebDAV.XML_STATUS, WebDAV.XML_NS_STATUS, getDAVHelper().getNullAttributes());
                xml.write(WebDAV.HTTP1_1 + " " + WebDAV.WEBDAV_SC_LOCKED + " " + SC_LOCKED_DESC);
                xml.endElement(WebDAV.DAV_NS, WebDAV.XML_STATUS, WebDAV.XML_NS_STATUS);
                
                xml.endElement(WebDAV.DAV_NS, WebDAV.XML_RESPONSE, WebDAV.XML_NS_RESPONSE);

                // Close the outer XML element
                xml.endElement(WebDAV.DAV_NS, WebDAV.XML_MULTI_STATUS, WebDAV.XML_NS_MULTI_STATUS);

                // Send remaining data
                flushXML(xml);
            }
            else
            {
                throw e;
            }
        }
    }
    
    @Override
    protected OutputFormat getXMLOutputFormat()
    {
        OutputFormat outputFormat = new OutputFormat();
        outputFormat.setNewLineAfterDeclaration(false);
        outputFormat.setNewlines(false);
        outputFormat.setIndent(false);
        return outputFormat;
    }
}

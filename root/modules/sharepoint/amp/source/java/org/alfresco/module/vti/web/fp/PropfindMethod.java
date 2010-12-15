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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.handler.alfresco.VtiUtils;
import org.alfresco.repo.webdav.LockInfo;
import org.alfresco.repo.webdav.WebDAV;
import org.alfresco.repo.webdav.WebDAVHelper;
import org.alfresco.repo.webdav.WebDAVMethod;
import org.alfresco.repo.webdav.WebDAVServerException;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.repository.datatype.TypeConverter;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.surf.util.URLDecoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Class for handling PROPFIND method of the MS-WDVME protocol.
 * 
 * <p>MS Office client may send 3 variant of the PROPFIND method.
 *    <ul>
 *      <li>1. 'MS-Doclib' header with value '1' presents in request.</li>
 *      <li>2. 'Depth' header with value '0' presents in request.</li>
 *      <li>3. 'Depth' header with value 'infinity' presents in request.</li>
 *    </ul>
 *    All this possible situations are handled by this class. 
 * </p>
 * 
 * @author PavelYur
 *
 */
public class PropfindMethod extends WebDAVMethod
{

    /** Logger */
    private static Log logger = LogFactory.getLog(PropfindMethod.class);

    private static final String HEADER_MS_DOCLIB = "MS-Doclib";
    private static final String XML_REPL = "repl";
    private static final int DEPTH_ZERO = 0;
    private static final int DEPTH_ONE = 1;
    private static final int DEPTH_INFINITY = 99;

    private HashMap<String, String> namespaceMap = new HashMap<String, String>();

    private int depth = DEPTH_INFINITY;
    private boolean containsCollblob = false;

    private String alfrescoContext;

    public PropfindMethod(String alfrescoContex)
    {
        this.alfrescoContext = alfrescoContex;
        namespaceMap.put("urn:schemas-microsoft-com:office:office", "Office");
        namespaceMap.put("http://schemas.microsoft.com/repl/", "Repl");
        namespaceMap.put("urn:schemas-microsoft-com:", "Z");
    }

    
    /**
     * <p>
     *  Handle PROPFIND method of the MS-WDVME protocol.
     *  <ul>
     *      <li>1. If MS-Doclib header with value '1' presents in request it check for library existing and return
     *             the MS-Doclib header in response with value of the valid URL that is point to the library that 
     *             resource belongs to.
     *      </li>
     *      <li>
     *          2. If Depth header in request has a value '0' returns properties for the requested resource.
     *      </li>
     *      <li>
     *          3. If Depth header in request has a value 'infinity' (requested resources should be collection) 
     *             returns properties for the requested resource and all resources that are stored in requested 
     *             collection. 
     *      </li>
     *  </ul>
     *  In case if requested resource was not found then HTTP 404 status is sent to the client.
     * </p>
     */
    @Override
    protected void executeImpl() throws WebDAVServerException, Exception
    {
        String msDoclib = m_request.getHeader(HEADER_MS_DOCLIB);

        if (msDoclib != null && msDoclib.equals("1"))
        {
            try
            {
                getDAVHelper().getParentNodeForPath(getRootNodeRef(), URLDecoder.decode(m_request.getRequestURI()), alfrescoContext);
            }
            catch (FileNotFoundException e)
            {
                m_response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            String docLibHref = URLDecoder.decode(m_request.getRequestURL().substring(0, m_request.getRequestURL().lastIndexOf("/")));
            m_response.setHeader(HEADER_MS_DOCLIB, docLibHref);
            return;
        }

        m_response.setStatus(WebDAV.WEBDAV_SC_MULTI_STATUS);

        if (logger.isDebugEnabled())
        {
            logger.debug("processing PROPFIND request with uri: " + m_request.getRequestURI());
        }

        FileInfo pathNodeInfo = null;
        try
        {
            // Check that the path exists
            pathNodeInfo = getDAVHelper().getNodeForPath(getRootNodeRef(), URLDecoder.decode(m_request.getRequestURI()), alfrescoContext);
        }
        catch (FileNotFoundException e)
        {
            // The path is not valid - send a 404 error back to the client
            m_response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Set the response content type
        m_response.setContentType(WebDAV.XML_CONTENT_TYPE);

        // Create multistatus response
        XMLWriter xml = createMSWebDavXmlWriter();

        xml.startDocument();

        String nsdec = generateNamespaceDeclarations(namespaceMap);
        xml.startElement(WebDAV.DAV_NS, WebDAV.XML_MULTI_STATUS + nsdec, WebDAV.XML_NS_MULTI_STATUS + nsdec, getDAVHelper().getNullAttributes());

        if (containsCollblob)
        {
            xml.startElement("http://schemas.microsoft.com/repl/", "repl", "Repl:repl", getDAVHelper().getNullAttributes());
            xml.startElement("http://schemas.microsoft.com/repl/", "collblob", "Repl:collblob", getDAVHelper().getNullAttributes());
            xml.write(VtiUtils.formatPropfindDate(new Date()));
            xml.endElement("http://schemas.microsoft.com/repl/", "collblob", "Repl:collblob");
            xml.endElement("http://schemas.microsoft.com/repl/", "repl", "Repl:repl");
        }

        xml.write("\n");

        // Create the path for the current location in the tree
        StringBuilder baseBuild = new StringBuilder(256);
        baseBuild.append("");
        if (baseBuild.length() == 0 || baseBuild.charAt(baseBuild.length() - 1) != WebDAVHelper.PathSeperatorChar)
        {
            baseBuild.append(WebDAVHelper.PathSeperatorChar);
        }
        String basePath = baseBuild.toString();

        // Output the response for the root node, depth zero
        try
        {
            generateResponseForNode(xml, pathNodeInfo, basePath);
        }
        catch (Exception e)
        {
            m_response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // If additional levels are required and the root node is a folder then recurse to the required
        // level and output node details a level at a time
        if (depth != DEPTH_ZERO && pathNodeInfo.isFolder())
        {
            // Create the initial list of nodes to report
            List<FileInfo> nodeInfos = new ArrayList<FileInfo>(10);
            nodeInfos.add(pathNodeInfo);

            int curDepth = WebDAV.DEPTH_1;

            // List of next level of nodes to report
            List<FileInfo> nextNodeInfos = null;
            if (depth > WebDAV.DEPTH_1)
            {
                nextNodeInfos = new ArrayList<FileInfo>(10);
            }

            // Loop reporting each level of nodes to the requested depth
            while (curDepth <= depth && nodeInfos != null)
            {
                // Clear out the next level of nodes, if required
                if (nextNodeInfos != null)
                {
                    nextNodeInfos.clear();
                }

                // Output the current level of node(s), the node list should
                // only contain folder nodes

                for (FileInfo curNodeInfo : nodeInfos)
                {
                    // Get the list of child nodes for the current node
                    List<FileInfo> childNodeInfos = getFileFolderService().list(curNodeInfo.getNodeRef());

                    // can skip the current node if it doesn't have children
                    if (childNodeInfos.size() == 0)
                    {
                        continue;
                    }

                    // Output the child node details
                    // Generate the base path for the current parent node

                    baseBuild.setLength(0);
                    try
                    {
                        String pathSnippet = getDAVHelper().getPathFromNode(pathNodeInfo.getNodeRef(), curNodeInfo.getNodeRef());
                        baseBuild.append(pathSnippet);
                    }
                    catch (FileNotFoundException e)
                    {
                        // move to the next node
                        continue;
                    }

                    int curBaseLen = baseBuild.length();

                    // Output the child node details
                    for (FileInfo curChildInfo : childNodeInfos)
                    {
                        // Do not output link nodes

                        if (curChildInfo.isLink() == false)
                        {
                            // Build the path for the current child node
                            baseBuild.setLength(curBaseLen);

                            baseBuild.append(WebDAVHelper.PathSeperatorChar + curChildInfo.getName());

                            // Output the current child node details
                            try
                            {
                                generateResponseForNode(xml, curChildInfo, baseBuild.toString());
                            }
                            catch (Exception e)
                            {
                                m_response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                return;
                            }

                            // If the child is a folder add it to the list of next level nodes
                            if (nextNodeInfos != null && curChildInfo.isFolder())
                            {
                                nextNodeInfos.add(curChildInfo);
                            }
                        }
                    }
                }

                // Update the current tree depth
                curDepth++;

                // Move the next level of nodes to the current node list
                nodeInfos.clear();
                if (nextNodeInfos != null)
                {
                    nodeInfos.addAll(nextNodeInfos);
                }
            }
        }

        // Close the outer XML element
        xml.endElement(WebDAV.DAV_NS, WebDAV.XML_MULTI_STATUS, WebDAV.XML_NS_MULTI_STATUS);

        // Send remaining data
        xml.flush();
    }

    /**
     * Generates the required response XML for the current node
     * 
     * @param xml XMLWriter
     * @param node NodeRef
     * @param path String
     */
    protected void generateResponseForNode(XMLWriter xml, FileInfo nodeInfo, String path) throws Exception
    {
        NodeRef nodeRef = nodeInfo.getNodeRef();
        boolean isFolder = nodeInfo.isFolder();

        // Output the response block for the current node
        xml.startElement(WebDAV.DAV_NS, WebDAV.XML_RESPONSE, WebDAV.XML_NS_RESPONSE, getDAVHelper().getNullAttributes());

        path = URLDecoder.decode(m_request.getRequestURI()).replaceFirst(alfrescoContext, "") + path;
        path = path.replaceAll("//", "/");

        // Build the href string for the current node
        String strHRef = m_request.getScheme() + "://" + m_request.getServerName() + ":" + m_request.getServerPort()
                + WebDAV.getURLForPath(new HttpServletRequestWrapper(m_request)
                {
                    public String getServletPath()
                    {
                        return alfrescoContext.equals("") ? "/" : alfrescoContext;
                    }

                }, path, isFolder);

        if (nodeInfo.isFolder())
        {
            strHRef = strHRef.substring(0, strHRef.length() - 1);
        }

        strHRef = strHRef.endsWith("/") ? strHRef.substring(0, strHRef.length() - 1) : strHRef;

        xml.startElement(WebDAV.DAV_NS, WebDAV.XML_HREF, WebDAV.XML_NS_HREF, getDAVHelper().getNullAttributes());
        xml.write(URLDecoder.decode(strHRef));
        xml.endElement(WebDAV.DAV_NS, WebDAV.XML_HREF, WebDAV.XML_NS_HREF);

        generateAllPropertiesResponse(xml, nodeRef, isFolder);

        // Close off the response element
        xml.endElement(WebDAV.DAV_NS, WebDAV.XML_RESPONSE, WebDAV.XML_NS_RESPONSE);
        xml.write("\n");
    }

    /**
     * Generates the XML response for a PROPFIND request that asks for all known
     * properties
     * 
     * @param xml XMLWriter
     * @param node NodeRef
     * @param isDir boolean
     */
    protected void generateAllPropertiesResponse(XMLWriter xml, NodeRef node, boolean isDir) throws Exception
    {
        // Get the properties for the node

        Map<QName, Serializable> props = getNodeService().getProperties(node);
        TypeConverter typeConv = DefaultTypeConverter.INSTANCE;

        String etag = node.getId();

        NodeRef workingCopy = getDAVHelper().getServiceRegistry().getCheckOutCheckInService().getWorkingCopy(node);
        Map<QName, Serializable> workingCopyProps = null;

        if (workingCopy != null)
        {
            String workingCopyOwner = getDAVHelper().getNodeService().getProperty(workingCopy, ContentModel.PROP_WORKING_COPY_OWNER).toString();
            if (workingCopyOwner.equals(getDAVHelper().getServiceRegistry().getAuthenticationService().getCurrentUserName()))
            {
                workingCopyProps = getDAVHelper().getNodeService().getProperties(workingCopy);
            }
        }

        // Output the start of the properties element

        Attributes nullAttr = getDAVHelper().getNullAttributes();

        xml.startElement(WebDAV.DAV_NS, WebDAV.XML_PROPSTAT, WebDAV.XML_NS_PROPSTAT, nullAttr);
        xml.startElement(WebDAV.DAV_NS, WebDAV.XML_PROP, WebDAV.XML_NS_PROP, nullAttr);

        // Get the node name

        Object davValue = WebDAV.getDAVPropertyValue(props, WebDAV.XML_DISPLAYNAME);

        // Output the node name

        xml.startElement(WebDAV.DAV_NS, WebDAV.XML_DISPLAYNAME, WebDAV.XML_NS_DISPLAYNAME, nullAttr);
        if (davValue != null)
        {
            String name = typeConv.convert(String.class, davValue);
            if (name == null || name.length() == 0)
            {
                logger.error("WebDAV name is null, value=" + davValue.getClass().getName() + ", node=" + node);
            }
            xml.write(name);
        }
        xml.endElement(WebDAV.DAV_NS, WebDAV.XML_DISPLAYNAME, WebDAV.XML_NS_DISPLAYNAME);

        // Generate a lock status report, if locked

        generateLockDiscoveryResponse(xml, node, isDir);

        // Output the supported lock types

        if (!isDir)
        {
            writeLockTypes(xml);
        }

        if (isDir)
        {
            xml.startElement(WebDAV.DAV_NS, "isFolder", WebDAV.DAV_NS + ":isFolder", nullAttr);
            xml.write("t");
            xml.endElement(WebDAV.DAV_NS, "isFolder", WebDAV.DAV_NS + ":isFolder");
            xml.startElement(WebDAV.DAV_NS, "iscollection", WebDAV.DAV_NS + ":iscollection", nullAttr);
            xml.write("1");
            xml.endElement(WebDAV.DAV_NS, "iscollection", WebDAV.DAV_NS + ":iscollection");
            xml.startElement(WebDAV.DAV_NS, "ishidden", WebDAV.DAV_NS + ":ishidden", nullAttr);
            xml.write("0");
            xml.endElement(WebDAV.DAV_NS, "ishidden", WebDAV.DAV_NS + ":ishidden");
            xml.startElement(WebDAV.DAV_NS, "getcontenttype", WebDAV.DAV_NS + ":getcontenttype", nullAttr);
            xml.write("application/octet-stream");
            xml.endElement(WebDAV.DAV_NS, "getcontenttype", WebDAV.DAV_NS + ":getcontenttype");
            xml.startElement(WebDAV.DAV_NS, WebDAV.XML_GET_CONTENT_LENGTH, WebDAV.XML_NS_GET_CONTENT_LENGTH, nullAttr);
            xml.write("0");
            xml.endElement(WebDAV.DAV_NS, WebDAV.XML_GET_CONTENT_LENGTH, WebDAV.XML_NS_GET_CONTENT_LENGTH);

            // If the node is a folder then return as a collection type

            xml.startElement(WebDAV.DAV_NS, WebDAV.XML_RESOURCE_TYPE, WebDAV.XML_NS_RESOURCE_TYPE, nullAttr);
            if (isDir)
                xml.write(DocumentHelper.createElement(WebDAV.XML_NS_COLLECTION));
            xml.endElement(WebDAV.DAV_NS, WebDAV.XML_RESOURCE_TYPE, WebDAV.XML_NS_RESOURCE_TYPE);

            xml.startElement("Repl", "authoritative-directory", "Repl:authoritative-directory", nullAttr);
            xml.write("t");
            xml.endElement("Repl", "authoritative-directory", "Repl:authoritative-directory");
        }

        // Output the source
        //
        // NOTE: source is always a no content element in our implementation

        //xml.write(DocumentHelper.createElement(WebDAV.XML_NS_SOURCE));        

        // Get the modifed date/time

        if (workingCopyProps != null)
        {
            davValue = WebDAV.getDAVPropertyValue(workingCopyProps, WebDAV.XML_GET_LAST_MODIFIED);
        }
        else
        {
            davValue = WebDAV.getDAVPropertyValue(props, WebDAV.XML_GET_LAST_MODIFIED);
        }

        Date lastModified = (Date) davValue;

        // Output the last modified date of the node

        xml.startElement(WebDAV.DAV_NS, WebDAV.XML_GET_LAST_MODIFIED, WebDAV.XML_NS_GET_LAST_MODIFIED, nullAttr);
        if (davValue != null)
            xml.write(VtiUtils.formatPropfindDate(typeConv.convert(Date.class, davValue)));
        xml.endElement(WebDAV.DAV_NS, WebDAV.XML_GET_LAST_MODIFIED, WebDAV.XML_NS_GET_LAST_MODIFIED);

        // Get the creation date
        davValue = WebDAV.getDAVPropertyValue(props, WebDAV.XML_CREATION_DATE);

        // Output the creation date

        xml.startElement(WebDAV.DAV_NS, WebDAV.XML_CREATION_DATE, WebDAV.XML_NS_CREATION_DATE, nullAttr);
        if (davValue != null)
            xml.write(VtiUtils.formatPropfindDate(typeConv.convert(Date.class, davValue)));
        xml.endElement(WebDAV.DAV_NS, WebDAV.XML_CREATION_DATE, WebDAV.XML_NS_CREATION_DATE);

        // For a file node output the content language and content type

        if (isDir == false)
        {
            long len = 0;

            ContentData contentData = (ContentData) props.get(ContentModel.PROP_CONTENT);
            if (contentData != null)
                len = contentData.getSize();

            // Output the content length

            xml.startElement(WebDAV.DAV_NS, WebDAV.XML_GET_CONTENT_LENGTH, WebDAV.XML_NS_GET_CONTENT_LENGTH, nullAttr);
            xml.write("" + len);
            xml.endElement(WebDAV.DAV_NS, WebDAV.XML_GET_CONTENT_LENGTH, WebDAV.XML_NS_GET_CONTENT_LENGTH);

        }

        // Print out all the custom properties

        xml.startElement("Repl", "repl-uid", "Repl:repl-uid", nullAttr);
        xml.write("rid:{" + etag + "}");
        xml.endElement("Repl", "repl-uid", "Repl:repl-uid");

        xml.startElement("Repl", "resourcetag", "Repl:resourcetag", nullAttr);
        xml.write("rt:" + etag + "@" + convertDateToVersion(lastModified));
        xml.endElement("Repl", "resourcetag", "Repl:resourcetag");

        // Output the etag        
        xml.startElement(WebDAV.DAV_NS, WebDAV.XML_GET_ETAG, WebDAV.XML_NS_GET_ETAG, nullAttr);
        xml.write("\"{" + etag + "}," + convertDateToVersion(lastModified) + "\"");
        xml.endElement(WebDAV.DAV_NS, WebDAV.XML_GET_ETAG, WebDAV.XML_NS_GET_ETAG);

        if (!isDir)
        {
            String modifiedBy = props.get(ContentModel.PROP_MODIFIER).toString();
            xml.startElement("Office", "modifiedby", "Office:modifiedby", nullAttr);
            xml.write(modifiedBy);
            xml.endElement("Office", "modifiedby", "Office:modifiedby");

        }
        // Close off the response

        xml.endElement(WebDAV.DAV_NS, WebDAV.XML_PROP, WebDAV.XML_NS_PROP);
        xml.write("\n");

        xml.startElement(WebDAV.DAV_NS, WebDAV.XML_STATUS, WebDAV.XML_NS_STATUS, nullAttr);
        xml.write(WebDAV.HTTP1_1 + " " + HttpServletResponse.SC_OK + " " + WebDAV.SC_OK_DESC);
        xml.endElement(WebDAV.DAV_NS, WebDAV.XML_STATUS, WebDAV.XML_NS_STATUS);
        xml.write("\n");

        xml.endElement(WebDAV.DAV_NS, WebDAV.XML_PROPSTAT, WebDAV.XML_NS_PROPSTAT);
        xml.write("\n");
    }

    /**
     * Generates the XML response snippet showing the lock information for the
     * given path
     * 
     * @param xml XMLWriter
     * @param node NodeRef
     * @param isDir boolean
     */
    protected void generateLockDiscoveryResponse(XMLWriter xml, NodeRef node, boolean isDir) throws Exception
    {
        // Get the lock status for the node

        LockInfo lockInfo = getNodeLockInfo(node);

        // Output the lock status response


        if (lockInfo.isLocked())
        {
            generateLockDiscoveryXML(xml, node, lockInfo);
        }
        else
        {
            xml.startElement(WebDAV.DAV_NS, WebDAV.XML_LOCK_DISCOVERY, WebDAV.XML_NS_LOCK_DISCOVERY, getDAVHelper().getNullAttributes());
            xml.endElement(WebDAV.DAV_NS, WebDAV.XML_LOCK_DISCOVERY, WebDAV.XML_NS_LOCK_DISCOVERY);
            xml.startElement(WebDAV.DAV_NS, WebDAV.XML_SUPPORTED_LOCK, WebDAV.XML_NS_SUPPORTED_LOCK, getDAVHelper().getNullAttributes());
            xml.endElement(WebDAV.DAV_NS, WebDAV.XML_SUPPORTED_LOCK, WebDAV.XML_NS_SUPPORTED_LOCK);
        }
    }

    /**
     * Output the supported lock types XML element
     * 
     * @param xml XMLWriter
     */
    protected void writeLockTypes(XMLWriter xml)
    {
        try
        {
            AttributesImpl nullAttr = getDAVHelper().getNullAttributes();

            xml.startElement(WebDAV.DAV_NS, WebDAV.XML_SUPPORTED_LOCK, WebDAV.XML_NS_SUPPORTED_LOCK, nullAttr);

            xml.startElement(WebDAV.DAV_NS, "lockentry", "D:lockentry", nullAttr);

            xml.startElement(WebDAV.DAV_NS, WebDAV.XML_LOCK_SCOPE, WebDAV.XML_NS_LOCK_SCOPE, nullAttr);
            xml.write(DocumentHelper.createElement(WebDAV.XML_NS_EXCLUSIVE));
            xml.endElement(WebDAV.DAV_NS, WebDAV.XML_LOCK_SCOPE, WebDAV.XML_NS_LOCK_SCOPE);

            xml.startElement(WebDAV.DAV_NS, WebDAV.XML_LOCK_TYPE, WebDAV.XML_NS_LOCK_TYPE, nullAttr);
            xml.write(DocumentHelper.createElement(WebDAV.XML_NS_WRITE));
            xml.endElement(WebDAV.DAV_NS, WebDAV.XML_LOCK_TYPE, WebDAV.XML_NS_LOCK_TYPE);

            xml.endElement(WebDAV.DAV_NS, "lockentry", "D:lockentry");

            xml.endElement(WebDAV.DAV_NS, WebDAV.XML_SUPPORTED_LOCK, WebDAV.XML_NS_SUPPORTED_LOCK);
        }
        catch (Exception ex)
        {
            throw new AlfrescoRuntimeException("XML write error", ex);
        }
    }

    /**
     * Parse the request headers
     * 
     * @exception WebDAVServerException
     */
    protected void parseRequestHeaders() throws WebDAVServerException
    {
        // Store the Depth header as this is used by several WebDAV methods

        String strDepth = m_request.getHeader(WebDAV.HEADER_DEPTH);
        if (strDepth != null && strDepth.length() > 0)
        {
            if (strDepth.equals(WebDAV.ZERO))
            {
                depth = DEPTH_ZERO;
            }
            else if (strDepth.equals(WebDAV.ONE))
            {
                depth = DEPTH_ONE;
            }
            else
            {
                depth = DEPTH_INFINITY;
            }
        }
    }

    /**
     * Parse the request body
     * 
     * @exception WebDAVServerException
     */
    protected void parseRequestBody() throws WebDAVServerException
    {
        Document body = getRequestBodyAsDocument();
        if (body != null)
        {
            Element rootElement = body.getDocumentElement();
            NodeList childList = rootElement.getChildNodes();

            for (int i = 0; i < childList.getLength(); i++)
            {
                Node currentNode = childList.item(i);
                switch (currentNode.getNodeType())
                {
                case Node.TEXT_NODE:
                    break;
                case Node.ELEMENT_NODE:
                    if (currentNode.getNodeName().endsWith(XML_REPL))
                    {
                        containsCollblob = true;
                    }

                    break;
                }
            }
        }
    }

    private XMLWriter createMSWebDavXmlWriter() throws IOException
    {
        OutputFormat outputFormat = new OutputFormat();
        outputFormat.setNewLineAfterDeclaration(false);
        outputFormat.setNewlines(false);
        outputFormat.setIndent(false);
        return new XMLWriter(m_response.getWriter(), outputFormat);
    }

    public static String convertDateToVersion(Date date)
    {
        return (Long.toString(date.getTime())).substring(0, 11);
    }
}

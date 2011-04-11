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
package org.alfresco.module.vti.web.actions;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.web.VtiAction;
import org.alfresco.module.vti.web.fp.PropfindMethod;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;

/**
* <p>VtiIfHeaderAction is used for merging between client document version
* and server document version.</p>
* 
* @author PavelYur
*/
public class VtiIfHeaderAction extends HttpServlet implements VtiAction
{

    private static final long serialVersionUID = 3119971805600532320L;

    private static SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);

    private final static Log logger = LogFactory.getLog(VtiIfHeaderAction.class);

    private NodeService nodeService;

    private FileFolderService fileFolderService;

    private CheckOutCheckInService checkOutCheckInService;

    private AuthenticationService authenticationService;

    private MimetypeService mimetypeService;

    private VtiPathHelper pathHelper;

    static
    {
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * <p>NodeService setter.</p>
     *
     * @param nodeService {@link NodeService}.    
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * <p>FileFolderService setter.</p>
     *
     * @param fileFolderService {@link FileFolderService}.    
     */
    public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }

    /**
     * <p>CheckOutCheckInService setter.</p>
     *
     * @param checkOutCheckInService {@link CheckOutCheckInService}.    
     */
    public void setCheckOutCheckInService(CheckOutCheckInService checkOutCheckInService)
    {
        this.checkOutCheckInService = checkOutCheckInService;
    }

    /**
     * <p>AuthenticationService setter.</p>
     *
     * @param authenticationService {@link AuthenticationService}.    
     */
    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }

    /**
     * <p>MimetypeService setter.</p>
     * 
     * @param mimetypeService {@link MimetypeService}.
     */
    public void setMimetypeService(MimetypeService mimetypeService)
    {
        this.mimetypeService = mimetypeService;
    }

    /**
     * <p>
     * VtiPathHelper setter.
     * </p>
     * 
     * @param pathHelper {@link VtiPathHelper}.
     */
    public void setPathHelper(VtiPathHelper pathHelper)
    {
        this.pathHelper = pathHelper;
    }

    /**
     * <p>Getting server version of document for merging.</p> 
     *
     * @param request HTTP request
     * @param response HTTP response
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String if_header_value = req.getHeader("If");
        String guid = null;

        if (if_header_value != null && if_header_value.length() > 0)
        {
            int begin = if_header_value.indexOf(":");
            int end = if_header_value.indexOf("@");
            if (begin != -1 && end != -1)
            {
                guid = if_header_value.substring(begin + 1, end);
            }
        }

        if (guid == null)
        {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        NodeRef nodeRef = new NodeRef("workspace", "SpacesStore", guid.toLowerCase());

        NodeRef workingCopyNodeRef = checkOutCheckInService.getWorkingCopy(nodeRef);

        // original node props
        Map<QName, Serializable> props = nodeService.getProperties(nodeRef);
        // original node reader
        ContentReader contentReader = fileFolderService.getReader(nodeRef);

        if (workingCopyNodeRef != null)
        {
            String workingCopyOwner = nodeService.getProperty(workingCopyNodeRef, ContentModel.PROP_WORKING_COPY_OWNER).toString();
            if (workingCopyOwner.equals(authenticationService.getCurrentUserName()))
            {
                // allow to see changes in document after it was checked out (only for checked out owner)
                contentReader = fileFolderService.getReader(workingCopyNodeRef);

                // working copy props
                props = nodeService.getProperties(workingCopyNodeRef);
            }
        }

        Date lastModified = (Date) props.get(ContentModel.PROP_MODIFIED);

        resp.setHeader("Last-Modified", format.format(lastModified));
        resp.setHeader("ETag", "\"{" + guid.toUpperCase() + "}," + PropfindMethod.convertDateToVersion(lastModified) + "\"");
        resp.setHeader("ResourceTag", "rt:" + guid.toUpperCase() + "@" + PropfindMethod.convertDateToVersion(lastModified));

        ContentData content = (ContentData) props.get(ContentModel.PROP_CONTENT);

        resp.setContentType(content.getMimetype());

        FileCopyUtils.copy(contentReader.getContentInputStream(), resp.getOutputStream());        
    }

    /**
     * <p>Saving of client version of document while merging.</p> 
     *
     * @param request HTTP request
     * @param response HTTP response
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        if (req.getContentLength() == 0)
        {
            return;
        }
        String path  = req.getPathInfo();
        String alfrescoContext = pathHelper.getAlfrescoContext();
        // remove the servlet path from the path
        if (alfrescoContext != null && alfrescoContext.length() > 0 && path.startsWith(alfrescoContext))
        {
            // Strip the servlet path from the relative path
            path = path.substring(alfrescoContext.length());
        }
        
        
        NodeRef nodeRef = pathHelper.resolvePathFileInfo(path).getNodeRef();
        NodeRef workingCopyNodeRef = checkOutCheckInService.getWorkingCopy(nodeRef);
        
        ContentWriter writer = null;
        
        if (workingCopyNodeRef != null)
        {
            String workingCopyOwner = nodeService.getProperty(workingCopyNodeRef, ContentModel.PROP_WORKING_COPY_OWNER).toString();
            if (workingCopyOwner.equals(authenticationService.getCurrentUserName()))
            {
                // working copy writer
                writer = fileFolderService.getWriter(workingCopyNodeRef);
            }
        }
        else
        {
            // original document writer
            writer = fileFolderService.getWriter(nodeRef);  

        }

        // updates changes on the server
        try
        {
            String documentName = nodeService.getProperty(nodeRef, ContentModel.PROP_NAME).toString();
            String mimetype = mimetypeService.guessMimetype(documentName);
            writer.setMimetype(mimetype);
            writer.putContent(req.getInputStream());
        }
        catch (Exception e)
        {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getOutputStream().close();
            return;
        }

        // original document properties
        Map<QName, Serializable> props = nodeService.getProperties(nodeRef);

        if (workingCopyNodeRef != null)
        {
            String workingCopyOwner = nodeService.getProperty(workingCopyNodeRef, ContentModel.PROP_WORKING_COPY_OWNER).toString();
            if (workingCopyOwner.equals(authenticationService.getCurrentUserName()))
            {
                // working copy properties
                props = nodeService.getProperties(workingCopyNodeRef);
            }
        }
        Date lastModified = (Date) props.get(ContentModel.PROP_MODIFIED);
        resp.setHeader("Repl-uid", "rid{" + nodeRef.getId() + "}");
        resp.setHeader("ResourceTag", "rt:" + nodeRef.getId() + "@" + PropfindMethod.convertDateToVersion(lastModified));
        resp.setContentType(writer.getMimetype());

        resp.getOutputStream().close();
    }

    /**
     * <p>Merge between client document version and server document version.</p> 
     *
     * @param request HTTP request
     * @param response HTTP response
     */
    public void execute(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            service(request, response);
        }
        catch (IOException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Action IO exception", e);
            }
        }
        catch (ServletException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Action execution exception", e);
            }
        }
    }
}

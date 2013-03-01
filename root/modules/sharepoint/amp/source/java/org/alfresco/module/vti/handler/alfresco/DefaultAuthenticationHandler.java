/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.module.vti.handler.alfresco;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.alfresco.module.vti.handler.AuthenticationHandler;
import org.alfresco.module.vti.handler.MethodHandler;
import org.alfresco.module.vti.handler.SiteMemberMappingException;
import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.repo.SessionUser;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
import org.springframework.extensions.surf.util.URLDecoder;

/**
 * Default implementation of web authentication. Delegates to a authentication handler in the core alfresco
 * server authentication subsystem.
 * 
 * @author PavelYur
 */
public class DefaultAuthenticationHandler implements AuthenticationHandler
{
    private final static String USER_SESSION_ATTRIBUTE = "_vtiAuthTicket";

    private MethodHandler vtiHandler;
    private org.alfresco.repo.webdav.auth.AuthenticationDriver delegate;
    private PermissionService permissionService;
    private VtiPathHelper pathHelper;

    public boolean isRequestValidForCurrentUser(HttpServletRequest request, String alfrescoContext)
    {
        String uri = request.getRequestURI();

        if (request.getMethod().equalsIgnoreCase("OPTIONS"))
            return true;

        String targetUri = URLDecoder.decode(uri.startsWith(alfrescoContext) ? uri.substring(alfrescoContext.length()) : uri);

        if (targetUri.equals("/") || targetUri.equals("") || targetUri.startsWith("/_vti_inf.html") || targetUri.startsWith("/_vti_bin/") || targetUri.startsWith("/resources/"))
            return true;

        // Validate this uri is within an existent site
        String[] decompsedUrls;
        try
        {
            decompsedUrls = vtiHandler.decomposeURL(uri, alfrescoContext);
        }
        catch (Exception e)
        {
            throw new SiteMemberMappingException(VtiHandlerException.DOES_NOT_EXIST);
        }
        
        try
        {            
            // Anything that resides in a site folder called "_vti_bin" (e.g. "_vti_bin/_vti_aut/author.dll") is a part
            // of the Frontpage extensions and needs to be ignored
            if (decompsedUrls[1].startsWith("_vti_bin"))
            {
                return true;
            }            
            
            // Anything else should be resolvable as a readable document
            FileInfo documentFileInfo = pathHelper.resolvePathFileInfo(targetUri);
            if (documentFileInfo == null)
            {
                // ALF-16757: If we got nothing back without a security exception then the path doesn't actually exist
                // as a document and should be accessible
                return true;
            }
            NodeRef nodeRef = documentFileInfo.getNodeRef();
            AccessStatus canRead = permissionService.hasPermission(nodeRef, PermissionService.READ_CONTENT);
            return AccessStatus.ALLOWED == canRead;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    
    public SessionUser authenticateRequest(ServletContext context, HttpServletRequest request, HttpServletResponse response,
            String alfrescoContext) throws IOException, ServletException
    {
        if (delegate.authenticateRequest(context, request, response))
        {
            HttpSession session = request.getSession(false);
            if (session == null)
            {
                return null;
            }
            SessionUser user = (SessionUser) session.getAttribute(USER_SESSION_ATTRIBUTE);
            if (user == null)
            {
                return null;
            }
            if(isRequestValidForCurrentUser(request, alfrescoContext))
            {
                return user;
            }
            delegate.restartLoginChallenge(context, request, response);            
        }
        return null;
    }
    
    public void setDelegate(org.alfresco.repo.webdav.auth.AuthenticationDriver delegate)
    {
        this.delegate = delegate;
    }

    public void setVtiHandler(MethodHandler vtiHandler)
    {
        this.vtiHandler = vtiHandler;
    }
    
    public void setPermissionService(PermissionService permissionService)
    {
        this.permissionService = permissionService;
    }

    public void setPathHelper(VtiPathHelper pathHelper)
    {
        this.pathHelper = pathHelper;
    }
    
}

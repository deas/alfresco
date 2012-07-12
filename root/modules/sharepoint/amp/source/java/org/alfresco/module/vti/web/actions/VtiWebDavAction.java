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
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.web.VtiAction;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.webdav.WebDAV;
import org.alfresco.repo.webdav.WebDAVHelper;
import org.alfresco.repo.webdav.WebDAVMethod;
import org.alfresco.repo.webdav.WebDAVServerException;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
* <p>VtiWebDavAction is processor of WebDAV protocol. It provides 
* the back-end controller for dispatching among set of WebDAVMethods. 
* It selects and invokes a realization of {@link WebDAVMethod}
* to perform the requested method of WebDAV protocol.</p>
*
* @author Stas Sokolovsky
*
*/
public abstract class VtiWebDavAction implements VtiAction, VtiWebDavActionExecutor
{
    private static final long serialVersionUID = 8916126506309290108L;

    protected VtiPathHelper pathHelper;

    protected WebDAVHelper webDavHelper;

    protected ServiceRegistry serviceRegistry;

    protected AuthenticationService authenticationService;

    protected TenantService tenantService;
    
    private static Log logger = LogFactory.getLog(VtiWebDavAction.class);

    private VtiWebDavActionExecutor davActionExecutor = this;
    
    /**
     * <p>Process WebDAV protocol request, dispatch among set of 
     * WebDAVMethods, selects and invokes a realization of {@link WebDAVMethod}
     * to perform the requested method of WebDAV protocol.</p> 
     *
     * @param request HTTP request
     * @param response HTTP response
     */
    public void execute(HttpServletRequest request, HttpServletResponse response)
    {
        WebDAVMethod method = getWebDAVMethod();
        method.setDetails(request, response, webDavHelper, pathHelper.getRootNodeRef());
        try
        {
            davActionExecutor.execute(method, request, response);
        }
        catch (WebDAVServerException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Exception while executing WebDAV method", e);
            }
        }
    }

    /**
     * <p>Return executing WebDAV method.</p>
     * 
     * @param pathHelper {@link VtiPathHelper}.
     */
    public abstract WebDAVMethod getWebDAVMethod();

    
    /**
     * @see #execute(WebDAVMethod, HttpServletRequest, HttpServletResponse)
     * @param davActionExecutor the WebDAV method executor.
     */
    public void setDavActionExecutor(VtiWebDavActionExecutor davActionExecutor)
    {
        this.davActionExecutor = davActionExecutor;
    }

    /**
     * Plugable executor implementation allows overriding of this behaviour without disturbing
     * the class hierarchy.
     * 
     * @param method
     * @param request
     * @param response
     * @throws WebDAVServerException
     */
    @Override
    public void execute(WebDAVMethod method, HttpServletRequest request,
                HttpServletResponse response) throws WebDAVServerException
    {
        method.execute();
    }

    /**
     * <p>VtiPathHelper setter.</p>
     *
     * @param pathHelper {@link VtiPathHelper}.    
     */
    public void setPathHelper(VtiPathHelper pathHelper)
    {
        this.pathHelper = pathHelper;
    }

    /**
     * Provide a WebDAVHelper collaborator.
     * 
     * @param webDavHelper the webDavHelper to set
     */
    public void setWebDavHelper(WebDAVHelper webDavHelper)
    {
        this.webDavHelper = webDavHelper;
    }

    /**
     * <p>ServiceRegistry setter.</p>
     *
     * @param serviceRegistry {@link ServiceRegistry}.    
     */
    public void setServiceRegistry(ServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
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
     * Sets the TenantService used by this class.
     * 
     * @param tenantService {@link TenantService}
     */
    public void setTenantService(TenantService tenantService)
    {
        this.tenantService = tenantService;
    }
}

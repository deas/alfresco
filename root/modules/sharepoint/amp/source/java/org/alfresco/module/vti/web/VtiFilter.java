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

package org.alfresco.module.vti.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.alfresco.module.vti.handler.AuthenticationHandler;
import org.alfresco.module.vti.handler.MethodHandler;
import org.alfresco.module.vti.handler.SiteMemberMappingException;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.handler.alfresco.VtiUtils;
import org.alfresco.repo.SessionUser;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.model.FileInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.URLDecoder;

/**
* <p>VtiFilter filter is used as security filter for checking authentication, 
* resource existence, access to specific document workspace and writing
* specific protocol headers to response</p>
*
* @author Michael Shavnev
*   
*/
public class VtiFilter implements Filter
{
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_HEAD = "HEAD";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_OPTIONS = "OPTIONS";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_TRACE = "TRACE";
    public static final String METHOD_PROPFIND = "PROPFIND";
    public static final String METHOD_LOCK = "LOCK";
    public static final String METHOD_UNLOCK = "UNLOCK";
    
    public static final String AUTHENTICATE_HEADER = "WWW-Authenticate";

    public static final String CONTENT_TYPE_XML = "text/xml; charset=utf-8";
    public static final String CONTENT_TYPE_HTML = "text/html; charset=utf-8";
    public static final String CONTENT_TYPE_X_VERMEER_RPC = "application/x-vermeer-rpc";
    public static final String EMULATED_SHAREPOINT_VERSION = "14.00.0.000";

    private AuthenticationHandler authenticationHandler;
    private MethodHandler vtiHandler;
    private VtiPathHelper vtiPathHelper;

    private SysAdminParams sysAdminParams;
    private ServletContext context;
    
    private static Log logger = LogFactory.getLog(VtiFilter.class);

    /**
     * <p>
     * Process the specified HTTP request, check authentication, resource existence, access to document workspace and write specific protocol headers to response.
     * </p>
     * 
     * @param request HTTP request
     * @param response HTTP response
     * @param chain filter chain
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        validSiteUri(httpRequest);
        Object validSiteUrl = httpRequest.getAttribute("VALID_SITE_URL");
        if (logger.isDebugEnabled())
        {
            logger.debug("Checking request for VTI");
        }
        String uri = httpRequest.getRequestURI();
        HttpSession session = httpRequest.getSession(false);
        if (session == null)
        {
            if (validSiteUrl == null && !uri.endsWith(".vti"))
            {
                session = httpRequest.getSession();
            }
            else
            {
                chain.doFilter(request, response);
                return;
            }
        }

        String httpMethod = httpRequest.getMethod();

        String ifHeader = httpRequest.getHeader("If");
        String ifNonMatchHeader = httpRequest.getHeader("If-None-Match");
        boolean checkResourceExistence = false;
        if ((METHOD_GET.equals(httpMethod) || METHOD_HEAD.equals(httpMethod)) && !uri.equals("/_vti_inf.html") && !uri.contains("_vti_bin") && !uri.contains("/_vti_history")
                && !uri.startsWith(getAlfrescoContext() + "/resources") && ifHeader == null && ifNonMatchHeader == null)
        {
            if (validSiteUrl != null || uri.endsWith(".vti"))
            {
                writeHeaders(httpRequest, httpResponse);                
                chain.doFilter(httpRequest, httpResponse);
                return;
            }
            else
            {
                checkResourceExistence = true;
            }
        }
        
        if (logger.isDebugEnabled())
        {
            logger.debug("Check authentication");
        }

        SessionUser user = null;

        try
        {
            user = authenticationHandler.authenticateRequest(this.context, httpRequest, httpResponse, getAlfrescoContext());
        }
        catch (SiteMemberMappingException e)
        {
            httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            httpResponse.getOutputStream().close();
            return;
        }

        if (user == null)
        {
            if (!httpResponse.containsHeader(AUTHENTICATE_HEADER))
            {
                httpResponse.setHeader(AUTHENTICATE_HEADER, "Basic realm=\"Alfresco Server\"");
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getOutputStream().close();
            }
            if (logger.isDebugEnabled())
               logger.debug("No authentication details found, requesting they authenticate");
            return;
        }
        else
        {
            if (logger.isDebugEnabled())
                logger.debug("User was authenticated successfully");
        }

        if (checkResourceExistence)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Checking if resource exists");
            }

            if (!vtiHandler.existResource(httpRequest, httpResponse))
            {
                return;
            }
        }
        
        writeHeaders(httpRequest, httpResponse);                
        chain.doFilter(request, response);
    }

    /**
     * <p>
     * Filter initialization.
     * </p>
     * 
     * @param filterConfig filter configuration
     */
    public void init(FilterConfig filterConfig) throws ServletException
    {
        this.context = filterConfig.getServletContext();
    }

    /**
     * <p>Filter destroy method.</p> 
     *
     */
    public void destroy()
    {
        authenticationHandler = null;
        vtiHandler = null;
    }     


    private void writeHeaders(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    {
        String httpMethod = httpRequest.getMethod();

        // this header is returned always
        httpResponse.setHeader("MicrosoftSharePointTeamServices", "14.00.0.000");

        if (METHOD_OPTIONS.equals(httpMethod))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Return VTI answer for OPTIONS request");
            }
            httpResponse.setHeader("MS-Author-Via", "MS-FP/4.0,DAV");
            httpResponse.setHeader("MicrosoftOfficeWebServer", "5.0_Collab");
            httpResponse.setHeader("DAV", "1,2");
            httpResponse.setHeader("Accept-Ranges", "none");
            httpResponse.setHeader("Cache-Control", "no-cache");
            httpResponse.setHeader("Allow", "GET, POST, OPTIONS, HEAD, MKCOL, PUT, PROPFIND, PROPPATCH, DELETE, MOVE, COPY, GETLIB, LOCK, UNLOCK");
            
            httpResponse.setHeader("DocumentManagementServer", "Properties Schema;Source Control;Version History;");

            if (VtiUtils.isMacClientRequest(httpRequest))
            {
                // Office 2008/2011 for Mac
                httpResponse.setHeader("X-MSDAVEXT", "1");
                httpResponse.setHeader("Public-Extension", "http://schemas.microsoft.com/repl-2");
            }

        }
        else if (METHOD_HEAD.equals(httpMethod) || METHOD_GET.equals(httpMethod) || METHOD_PUT.equals(httpMethod))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Return VTI answer for HEAD request");
            }
            httpResponse.setHeader("Public-Extension", "http://schemas.microsoft.com/repl-2");
            if (METHOD_GET.equals(httpMethod))
            {
                if (httpRequest.getRequestURI().startsWith(getAlfrescoContext() + "/resources"))
                {
                    httpResponse.setHeader("Cache-Control", "public");
                }
                else
                {
                    httpResponse.setHeader("Cache-Control", "private");
                }
            }
            else
            {
                httpResponse.setHeader("Cache-Control", "no-cache");
            }
            httpResponse.setContentType("text/html");
        }
        else if (METHOD_PROPFIND.equals(httpMethod) || METHOD_LOCK.equals(httpMethod) || METHOD_UNLOCK.equals(httpMethod))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Return VTI answer for " + httpMethod + " request");
            }
            httpResponse.setHeader("Public-Extension", "http://schemas.microsoft.com/repl-2");
            httpResponse.setHeader("Cache-Control", "no-cache");
        }
        else if (METHOD_POST.equals(httpMethod))
        {
            httpResponse.setHeader("Cache-Control", "no-cache");
            httpResponse.setHeader("Connection", "close");
            httpResponse.setContentType(getContentType(httpRequest));
        }
    }    
    
    private String getContentType(HttpServletRequest httpRequest)
    {
        if (VtiUtils.isMacClientRequest(httpRequest))
        {
            if (httpRequest.getRequestURI().endsWith(".dll"))
            {
                return CONTENT_TYPE_HTML;
            }
            else
            {
                return CONTENT_TYPE_XML;
            }
        }
        return CONTENT_TYPE_X_VERMEER_RPC;
    }

    private boolean validSiteUri(HttpServletRequest request)
    {
        if (!request.getMethod().equals("GET"))
            return false;

        String[] result;
        String uri = request.getRequestURI();
        String context = getAlfrescoContext();

        String[] parts = VtiPathHelper.removeSlashes(uri).split("/");

        if (parts[parts.length - 1].indexOf('.') != -1)
        {
            return false;
        }

        String decodedUri = URLDecoder.decode(uri);
        if (decodedUri.length() > context.length())
        {
            decodedUri = decodedUri.substring(context.length() + 1);
        }

        final String path = decodedUri;
        FileInfo resourceFileInfo = AuthenticationUtil.runAs(new RunAsWork<FileInfo>()
        {
            @Override
            public FileInfo doWork() throws Exception
            {
                return vtiPathHelper.resolvePathFileInfo(path);
            }
        }, AuthenticationUtil.getSystemUserName());
        
        if (resourceFileInfo != null && !resourceFileInfo.isFolder())
        {
            return false;
        }

        try
        {
            result = vtiHandler.decomposeURL(uri, context);
            if (result[0].length() > context.length())
            {
                request.setAttribute("VALID_SITE_URL", "true");
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (Throwable e)
        {
            return false;
        }
    }    

    public MethodHandler getVtiHandler()
    {
        return vtiHandler;
    }

    public void setVtiHandler(MethodHandler vtiHandler)
    {
        this.vtiHandler = vtiHandler;
    }

    public VtiPathHelper getVtiPathHelper()
    {
        return vtiPathHelper;
    }
    
    public void setVtiPathHelper(VtiPathHelper vtiPathHelper)
    {
        this.vtiPathHelper = vtiPathHelper;
    }

    public String getAlfrescoContext()
    {
        return "/" + sysAdminParams.getAlfrescoContext();
    }

    public void setSysAdminParams(SysAdminParams sysAdminParams)
    {
        this.sysAdminParams = sysAdminParams;
    }
    
    public void setAuthenticationHandler(AuthenticationHandler authenticationHandler)
    {
        this.authenticationHandler = authenticationHandler;
    }
    
    public AuthenticationHandler getAuthenticationHandler()
    {
        return authenticationHandler;
    }

}

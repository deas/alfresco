/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.webscripts.servlet;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.config.ServerProperties;
import org.springframework.extensions.surf.util.URLDecoder;
import org.springframework.extensions.webscripts.AbstractRuntime;
import org.springframework.extensions.webscripts.Authenticator;
import org.springframework.extensions.webscripts.Match;
import org.springframework.extensions.webscripts.RuntimeContainer;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.WebScriptSession;
import org.springframework.extensions.webscripts.WebScriptSessionFactory;
import org.springframework.extensions.webscripts.connector.HttpMethod;

/**
 * HTTP Servlet Web Script Runtime
 * 
 * @author davidc
 */
public class WebScriptServletRuntime extends AbstractRuntime
{
    protected ServletAuthenticatorFactory authFactory;
    protected HttpServletRequest req;
    protected HttpServletResponse res;
    protected ServerProperties serverProperties;
    protected WebScriptServletRequest servletReq;
    protected WebScriptServletResponse servletRes;
    protected WebScriptServletSession servletSession;
    

    /**
     * Construct
     * 
     * @param registry
     * @param serviceRegistry
     * @param authenticator
     * @param req
     * @param res
     */
    public WebScriptServletRuntime(RuntimeContainer container, ServletAuthenticatorFactory authFactory, HttpServletRequest req, HttpServletResponse res, ServerProperties serverProperties)
    {
        super(container);
        this.authFactory = authFactory;
        this.req = req;
        this.res = res;
        this.serverProperties = serverProperties;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRuntime#getScriptMethod()
     */
    @Override
    protected String getScriptMethod()
    {
        // Is this an overloaded POST request?
        String method = req.getMethod();
        if (method.equalsIgnoreCase("post"))
        {
            boolean overloadParam = false;
            String overload = req.getHeader("X-HTTP-Method-Override");
            if (overload == null || overload.length() == 0)
            {
                overload = req.getParameter("alf_method");
                overloadParam = true;
            }
            if (overload != null && overload.length() > 0)
            {
                if (logger.isDebugEnabled())
                    logger.debug("POST is tunnelling method '" + overload + "' as specified by " + (overloadParam ? "alf_method parameter" : "X-HTTP-Method-Override header"));
                
                method = overload;
            }
        }
        
        return method;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRuntime#getScriptUrl()
     */
    @Override
    protected String getScriptUrl()
    {
        // NOTE: Don't use req.getPathInfo() - it truncates the path at first semi-colon in Tomcat
        final String requestURI = req.getRequestURI();
        final String serviceContextPath = req.getContextPath() + req.getServletPath();
        String pathInfo;
        
        if (serviceContextPath.length() > requestURI.length())
        {
            // NOTE: assume a redirect has taken place e.g. tomcat welcome-page
            // NOTE: this is unlikely, and we'll take the hit if the path contains a semi-colon
            pathInfo = req.getPathInfo();
        }
        else
        {
            pathInfo = URLDecoder.decode(requestURI.substring(serviceContextPath.length()));
        }
        
        return pathInfo;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRuntime#createRequest(org.alfresco.web.scripts.WebScriptMatch)
     */
    @SuppressWarnings("deprecation")
    @Override
    protected WebScriptRequest createRequest(Match match)
    {
        // TODO: construct org.springframework.extensions.webscripts.servlet.WebScriptServletResponse when
        //       org.alfresco.web.scripts.WebScriptServletResponse (deprecated) is removed
        servletReq = new WebScriptServletRequest(this, req, match, serverProperties);
        return servletReq;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRuntime#createResponse()
     */
    @SuppressWarnings("deprecation")
    @Override
    protected WebScriptResponse createResponse()
    {
        // TODO: construct org.springframework.extensions.webscripts.servlet.WebScriptServletResponse when
        //       org.alfresco.web.scripts.WebScriptServletResponse (deprecated) is removed
        servletRes = new WebScriptServletResponse(this, res);
        return servletRes;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.AbstractRuntime#createAuthenticator()
     */
    @Override
    protected Authenticator createAuthenticator()
    {
        if (authFactory == null)
        {
            return null;
        }
        return authFactory.create(servletReq, servletRes);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.extensions.webscripts.AbstractRuntime#createSessionFactory()
     */
    @Override
    protected WebScriptSessionFactory createSessionFactory()
    {
        return new WebScriptSessionFactory()
        {
            public WebScriptSession createSession()
            {
                return new WebScriptServletSession(req.getSession());
            }
        };
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptContainer#getName()
     */
    public String getName()
    {
        return "ServletRuntime";
    }

    /**
     * Helper to get HttpServletRequest from Web Script Request
     * 
     * @param request
     * @return
     */
    public static HttpServletRequest getHttpServletRequest(WebScriptRequest request)
    {
        WebScriptRequest realRequest = getRealWebScriptRequest(request);
        if (realRequest instanceof WebScriptServletRequest)
        {
            return ((WebScriptServletRequest)realRequest).getHttpServletRequest();
        }
        return null;
    }

    /**
     * Helper to get HttpServletResponse from Web Script Response
     * 
     * @param response
     * @return
     */
    public static HttpServletResponse getHttpServletResponse(WebScriptResponse response)
    {
        WebScriptResponse realResponse = getRealWebScriptResponse(response);
        if (realResponse instanceof WebScriptServletResponse)
        {
            return ((WebScriptServletResponse)realResponse).getHttpServletResponse();
        }
        return null;
    }

    /**
     * Helper to get the List of supported methods for web script.
     * For responding to OPTIONS requests.
     * 
     * @return List of supported methods for web script
     */
    public List<HttpMethod> getSupportedMethods()
    {
        final HttpMethod[] methods = HttpMethod.values();
        List<HttpMethod> supportedMethods = new ArrayList<HttpMethod>(methods.length);
        Match match = null;
        for (int i = 0; i < methods.length; i++)
        {
            match = container.getRegistry().findWebScript(methods[i].name(), getScriptUrl());
            if (match.getKind().equals(Match.Kind.FULL))
            {
                supportedMethods.add(methods[i]);
            }
        }
        return supportedMethods;
    }
}

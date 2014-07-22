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

package org.springframework.extensions.webscripts.portlet;

import java.io.IOException;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletSecurityException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.URLDecoder;
import org.springframework.extensions.webscripts.AbstractRuntime;
import org.springframework.extensions.webscripts.Authenticator;
import org.springframework.extensions.webscripts.Description;
import org.springframework.extensions.webscripts.Match;
import org.springframework.extensions.webscripts.RuntimeContainer;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptRequestURLImpl;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.WebScriptSession;
import org.springframework.extensions.webscripts.WebScriptSessionFactory;
import org.springframework.web.context.WebApplicationContext;


/**
 * Generic JSR-168 Portlet for hosting an Alfresco Web Script as a Portlet.
 *
 * Accepts the following init-config:
 * 
 * scriptUrl => the url of the web script to host e.g. /alfresco/service/mytasks
 *  
 * @author davidc
 */
public class WebScriptPortlet implements Portlet
{
    private static Log logger = LogFactory.getLog(WebScriptPortlet.class);

    // Portlet initialisation
    protected String initScriptUrl = null;
    
    // Component Dependencies
    protected RuntimeContainer container;
    protected PortletAuthenticatorFactory authenticatorFactory;


    /* (non-Javadoc)
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException
    {
        initScriptUrl = config.getInitParameter("scriptUrl");
        PortletContext portletCtx = config.getPortletContext();
        WebApplicationContext context = (WebApplicationContext)portletCtx.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        container = (RuntimeContainer)context.getBean("webscripts.container");
        
        // retrieve authenticator factory via servlet initialisation parameter
        String authenticatorId = config.getInitParameter("authenticator");
        if (authenticatorId != null && authenticatorId.length() > 0)
        {
            Object bean = context.getBean(authenticatorId);
            if (bean == null || !(bean instanceof PortletAuthenticatorFactory))
            {
                throw new PortletException("Initialisation parameter 'authenticator' does not refer to a portlet authenticator factory (" + authenticatorId + ")");
            }
            authenticatorFactory = (PortletAuthenticatorFactory)bean;
        }
    }

    /* (non-Javadoc)
     * @see javax.portlet.Portlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest req, ActionResponse res) throws PortletException, PortletSecurityException, IOException
    {
        Map<String, String[]> params = req.getParameterMap();
        for (Map.Entry<String, String[]> param : params.entrySet())
        {
            String name = param.getKey();
            if (name.equals("scriptUrl") || name.startsWith("arg."))
            {
                res.setRenderParameter(name, param.getValue());
            }
        }
    }

    /* (non-Javadoc)
     * @see javax.portlet.Portlet#render(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public void render(RenderRequest req, RenderResponse res) throws PortletException, PortletSecurityException, IOException
    {
        PortletMode portletMode = req.getPortletMode();
        if (PortletMode.VIEW.equals(portletMode))
        {
           doView(req, res);
        }
//        else if (PortletMode.HELP.equals(portletMode))
//        {
//           doHelp(request, response);
//        }
//        else if (PortletMode.EDIT.equals(portletMode))
//        {
//           doEdit(request, response);
//        }
    }

    /* (non-Javadoc)
     * @see javax.portlet.Portlet#destroy()
     */
    public void destroy()
    {
    }

    /**
     * Render Web Script view
     * 
     * @param req
     * @param res
     * @throws PortletException
     * @throws PortletSecurityException
     * @throws IOException
     */
    protected void doView(RenderRequest req, RenderResponse res) throws PortletException, PortletSecurityException, IOException
    {
        //
        // Establish Web Script URL
        //
        
        String scriptUrl = req.getParameter("scriptUrl");
        if (scriptUrl != null)
        {
            // build web script url from render request
            StringBuilder scriptUrlArgs = new StringBuilder(128);
            Map<String, String[]> params = req.getParameterMap();
            for (Map.Entry<String, String[]> param : params.entrySet())
            {
                String name = param.getKey();
                if (name.startsWith("arg."))
                {
                    String argName = name.substring("arg.".length());
                    for (String argValue : param.getValue())
                    {
                        scriptUrlArgs.append((scriptUrlArgs.length() == 0) ? "" : "&");
                        
                        // decode url arg (as it would be if this was a servlet)
                        scriptUrlArgs.append(argName).append("=")
                                     .append(URLDecoder.decode(argValue));
                    }
                }
            }
            scriptUrl += (scriptUrlArgs.length() != 0 ? ("?" + scriptUrlArgs.toString()) : "");
        }
        else
        {
            // retrieve initial scriptUrl as configured by Portlet
            scriptUrl = initScriptUrl;
            if (scriptUrl == null)
            {
                throw new PortletException("Initial Web script URL has not been specified.");
            }
        }
    
        //
        // Execute Web Script
        //
        
        if (logger.isDebugEnabled())
            logger.debug("Processing portal render request " + req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + "/" + req.getContextPath() + " (scriptUrl=" + scriptUrl + ")");

        PortletRuntime runtime = new PortletRuntime(container, req, res, scriptUrl);
        runtime.executeScript();
    }
    
    /**
     * JSR-168 Web Script Runtime
     * 
     * @author davidc
     */
    private class PortletRuntime extends AbstractRuntime
    {
        private RenderRequest req;
        private RenderResponse res;
        private String[] requestUrlParts;
        

        /**
         * Construct
         * @param req
         * @param res
         * @param requestUrl
         */
        public PortletRuntime(RuntimeContainer container, RenderRequest req, RenderResponse res, String requestUrl)
        {
            super(container);
            this.req = req;
            this.res = res;
            this.requestUrlParts = WebScriptRequestURLImpl.splitURL(requestUrl);
        }
        
        /* (non-Javadoc)
         * @see org.alfresco.web.scripts.WebScriptContainer#getName()
         */
        public String getName()
        {
            return "JSR-168 Portal";
        }
        
        /* (non-Javadoc)
         * @see org.alfresco.web.scripts.WebScriptRuntime#getScriptMethod()
         */
        @Override
        protected String getScriptMethod()
        {
            return "get";
        }

        /* (non-Javadoc)
         * @see org.alfresco.web.scripts.WebScriptRuntime#getScriptUrl()
         */
        @Override
        protected String getScriptUrl()
        {
            return requestUrlParts[2];
        }

        /* (non-Javadoc)
         * @see org.alfresco.web.scripts.WebScriptRuntime#createAuthenticator()
         */
        @Override
        protected Authenticator createAuthenticator()
        {
            if (authenticatorFactory == null)
            {
                return null;
            }
            return authenticatorFactory.create(req, res);
        }
        
        /* (non-Javadoc)
         * @see org.alfresco.web.scripts.WebScriptRuntime#createRequest(org.alfresco.web.scripts.WebScriptMatch)
         */
        @Override
        protected WebScriptRequest createRequest(Match match)
        {
            return new WebScriptPortletRequest(this, req, requestUrlParts, match);
        }

        /* (non-Javadoc)
         * @see org.alfresco.web.scripts.WebScriptRuntime#createResponse()
         */
        @Override
        protected WebScriptResponse createResponse()
        {
            return new WebScriptPortletResponse(this, res);
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
                    return new WebScriptPortletSession(req.getPortletSession());
                }
            };
        }

        /* (non-Javadoc)
         * @see org.alfresco.web.scripts.WebScriptRuntime#preExecute(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.WebScriptResponse)
         */
        @Override
        protected void executeScript(WebScriptRequest scriptReq, WebScriptResponse scriptRes, Authenticator auth)
            throws IOException
        {
            // Set Portlet title based on Web Script
            WebScript script = scriptReq.getServiceMatch().getWebScript();
            Description desc = script.getDescription();
            res.setTitle(desc.getShortName());

            // Note: Do not render script if portlet window is minimized
            if (!WindowState.MINIMIZED.equals(req.getWindowState()))
            {
                super.executeScript(scriptReq, scriptRes, auth);
            }
        }

    }
}

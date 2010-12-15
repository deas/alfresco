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
package org.alfresco.web.portlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSecurityException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.URLEncoder;

/**
 * Generic JSR-168 Portlet for exposing an Alfresco Web Script as a Portlet.
 *
 * Accepts the following init-config:
 * 
 * scriptUrl => the initial URL to expose e.g. /share/service/sample/cmis/repo
 *  
 * @author davidc
 * @author dward
 * @author kevinr
 * @author mikeh
 */
public class ProxyPortlet implements Portlet
{
    private static Log logger = LogFactory.getLog(ProxyPortlet.class);
    
    private static final String EDIT_URL     = "editScriptUrl";
    private static final String VIEW_URL     = "viewScriptUrl";
    private static final String SCRIPT_URL   = "scriptUrl";
    private static final String PORTLET_URL  = "portletUrl";
    private static final String PORTLET_HOST = "portletHost";
    private static final String MODE_PARAM_NAME = "mode";
    private static final String MODE_PARAM_VALUE_EDIT = "edit";
    private static final String MODE_PARAM_VALUE_VIEW = "view";
    private static final String UPDATED_PARAM_NAME = "updated";
    private static final String PREF_PARAM_NAME_PREFIX = "pref_";
    private static final String DEFAULT_VALUE = "[DEFAULT]";
    
    // Portlet initialisation
    protected PortletConfig config;
    protected String editScriptUrl;
    protected String initScriptUrl;


    /*
     * (non-Javadoc)
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException
    {
        this.config = config;
        this.editScriptUrl = config.getInitParameter(EDIT_URL);
        this.initScriptUrl = config.getInitParameter(VIEW_URL);
    }

    /*
     * (non-Javadoc)
     * @see javax.portlet.Portlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest req, ActionResponse res)
        throws PortletException, PortletSecurityException, IOException
    {
        if (req.getPortletMode() == PortletMode.EDIT)
        {
            //
            // Store updated preferences if any found
            //
            boolean foundPref = false;
            PortletPreferences prefs = req.getPreferences();
            Map<String, String[]> prefsMap = prefs.getMap();
            Enumeration<String> names = req.getParameterNames();
            while (names.hasMoreElements())
            {
               String name = (String)names.nextElement();
               String value = req.getParameter(name);
               if (prefsMap.containsKey(name) && value != null && prefsMap.get(name)[0] != value)
               {
                   prefs.setValue(name, value);
                   foundPref = true;
               }
            }
            if (foundPref)
            {
                prefs.store();
                req.setAttribute(UPDATED_PARAM_NAME, true);
            }
        }
        
        res.setRenderParameter(req.getWindowID() + SCRIPT_URL, req.getParameter(SCRIPT_URL));
    }

    /*
     * (non-Javadoc)
     * @see javax.portlet.Portlet#render(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public void render(RenderRequest req, RenderResponse res)
        throws PortletException, PortletSecurityException, IOException
    {
        PortletMode portletMode = req.getPortletMode();
        if (PortletMode.VIEW.equals(portletMode))
        {
            doView(req, res);
        }
        else if (PortletMode.EDIT.equals(portletMode))
        {
            doEdit(req, res);
        }
    }

    /*
     * (non-Javadoc)
     * @see javax.portlet.Portlet#destroy()
     */
    public void destroy()
    {
    }

    /**
     * Render Surf view (portlet view mode)
     * 
     * @param req
     * @param res
     * @throws PortletException
     * @throws PortletSecurityException
     * @throws IOException
     */
    protected void doView(RenderRequest req, RenderResponse res)
        throws PortletException, PortletSecurityException, IOException
    {
        //
        // Establish View URL
        //
        String scriptUrl = req.getParameter(req.getWindowID() + SCRIPT_URL);
        if (scriptUrl == null || scriptUrl.equals(this.editScriptUrl))
        {
            // retrieve initial scriptUrl as configured by Portlet
            scriptUrl = this.initScriptUrl;
            if (scriptUrl != null)
            {
                // contains replaceable tokens?
                if (scriptUrl.indexOf("{") > -1)
                {
                    PortletPreferences prefs = req.getPreferences();
                    Map<String, String[]> prefsMap = prefs.getMap();
                    // search / replace each available preference occurrence in the string
                    Pattern p = Pattern.compile("\\{(\\w+)\\}");
                    Matcher m = p.matcher(scriptUrl);
                    boolean result = m.find();
                    if (result)
                    {
                        StringBuffer sb = new StringBuffer();
                        do
                        {
                            m.appendReplacement(sb, prefsMap.containsKey(m.group(1)) ? prefsMap.get(m.group(1))[0] : DEFAULT_VALUE);
                            result = m.find();
                        } while (result);
                        m.appendTail(sb);
                        scriptUrl = sb.toString();
                    }

                    // still have non-replaced tokens?
                    if (scriptUrl.indexOf(DEFAULT_VALUE) > -1)
                    {
                        // redirect to the edit page in "view" mode
                        if (this.editScriptUrl != null)
                        {
                            scriptUrl = this.editScriptUrl;
                            req.setAttribute(MODE_PARAM_NAME, MODE_PARAM_VALUE_VIEW);
                        }
                        else
                        {
                            throw new PortletException("Required preferences missing and 'editScriptUrl' parameter has not been specified.");
                        }
                    }
                }
            }
            else
            {
                // If the path parameter has not been provided, forward to the user specific dashboard page
                String userId = req.getRemoteUser();
                if (userId != null)
                {
                    scriptUrl = "/page/user/" + URLEncoder.encode(userId) + "/dashboard";
                }
                else
                {
                    throw new PortletException("Initial 'scriptUrl' parameter has not been specified.");
                }
            }
        }
        
        renderRequest(req, res, scriptUrl);
    }

    /**
     * Render Surf view (portlet edit mode)
     * 
     * @param req
     * @param res
     * @throws PortletException
     * @throws PortletSecurityException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    protected void doEdit(RenderRequest req, RenderResponse res)
        throws PortletException, PortletSecurityException, IOException
    {
        //
        // Establish Edit URL
        //
        String scriptUrl = this.editScriptUrl;
        if (scriptUrl == null)
        {
            throw new PortletException("Initial 'editScriptUrl' parameter has not been specified.");
        }
        
        //
        // Add prefs to request attributes.
        //
        PortletPreferences prefs = req.getPreferences();
        Map<String, String[]> prefsMap = prefs.getMap();
        Iterator it = prefsMap.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry pairs = (Map.Entry)it.next();
            req.setAttribute(PREF_PARAM_NAME_PREFIX + pairs.getKey(), pairs.getValue());
        }
        
        req.setAttribute(MODE_PARAM_NAME, MODE_PARAM_VALUE_EDIT);
        renderRequest(req, res, scriptUrl);
    }
        
    /**
     * Render Surf request
     * 
     * @param req
     * @param res
     * @param scriptUrl
     * @throws PortletException
     * @throws PortletSecurityException
     * @throws IOException
     */
    protected void renderRequest(RenderRequest req, RenderResponse res, String scriptUrl)
        throws PortletException, PortletSecurityException, IOException
    {
        if (logger.isDebugEnabled())
            logger.debug("Processing portal render request " + req.getScheme() + "://" + req.getServerName() + ":"
                    + req.getServerPort() + "/" + req.getContextPath() + " (scriptUrl=" + scriptUrl + ")");
        
        // apply request attribute to indicate portal mode to Share application
        req.setAttribute(PORTLET_HOST, Boolean.TRUE);
        
        // apply request attribute to enable client-side construction of portlet action URLs
        PortletURL actionUrl = res.createActionURL();
        actionUrl.setParameter(SCRIPT_URL, "$$" + SCRIPT_URL + "$$");
        req.setAttribute(PORTLET_URL, actionUrl.toString());
        
        // render view url
        this.config.getPortletContext().getRequestDispatcher(scriptUrl).include(req, res);
    }
}

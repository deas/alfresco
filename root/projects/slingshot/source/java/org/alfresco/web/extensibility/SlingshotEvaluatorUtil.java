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
package org.alfresco.web.extensibility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.extensions.webscripts.ScriptRemote;
import org.springframework.extensions.webscripts.connector.Response;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for evaluators to pick values from the request and get site information etc.
 *
 * @author ewinlof
 */
public class SlingshotEvaluatorUtil {

    private static Log logger = LogFactory.getLog(SlingshotEvaluatorUtil.class);

    public static final String SITE_PRESET_CACHE = SlingshotEvaluatorUtil.class.getName() + ".sitePresets";

    /* Context attributes and url parameters/path tokens */
    protected static final String PORTLET_HOST = "portletHost"; // Set by the ProxyPortlet
    protected static final String PORTLET_URL = "portletUrl"; // Set by the ProxyPortlet
    protected static final String SITE_PRESET = "sitePreset";
    protected static final String SITE = "site";

    protected WebFrameworkServiceRegistry serviceRegistry = null;

    public void setServiceRegistry(WebFrameworkServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
    }

    /**
     * Helper for getting an evaluator parameter trimmed OR defaultValue if no value has been provided.
     *
     * @param params
     * @param name
     * @param defaultValue
     * @return A trimmed evaluator parameter OR defaultValue if no value has been provided.
     */
    protected String getEvaluatorParam(Map<String, String> params, String name, String defaultValue)
    {
        String value = params.get(name);
        if (value != null && !value.trim().isEmpty())
        {
            return value.trim();
        }
        return defaultValue;
    }

    /**
     * Returns true if we are viewed from inside a portal.
     *
     * @param context
     * @return true if we are viewed from inside a portal
     */
    protected Boolean getPortletHost(RequestContext context)
    {
        Boolean portletHost = (Boolean) context.getAttribute(PORTLET_HOST);
        if (portletHost == null)
        {
            String portletHostParam = context.getParameter(PORTLET_HOST);
            portletHost = portletHostParam != null && portletHostParam.equalsIgnoreCase("true");
        }
        return portletHost;
    }

    /**
     * Returns the portal url if we are inside a portal, otherwise null.
     * @param context
     * @return the portal url if we are inside a portal, otherwise null.
     */
    protected String getPortletUrl(RequestContext context)
    {
        String portletUrl = (String) context.getAttribute(PORTLET_URL);
        if (portletUrl == null)
        {
            portletUrl = context.getParameter(PORTLET_URL);
        }
        return portletUrl;
    }

    /**
     * Returns the current site id OR null if we aren't in a site
     *
     * @param context
     * @return The current site id OR null if we aren't in a site
     */
    protected String getSite(RequestContext context)
    {
        // Look for siteId in url path & parameters
        String site = context.getUriTokens().get(SITE);
        if (site == null)
        {
            site = context.getParameter(SITE);
        }
        return site;
    }

    /**
     * The site's sitePreset OR null if something goes wrong.
     *
     * @param context
     * @param siteId The id of the site to retrieve the sitePreset for.
     * @return The site's sitePreset OR null if something goes wrong.
     */
    protected String getSitePreset(RequestContext context, String siteId)
    {
        // Get the preset request cache
        HashMap sitePresetCache = (HashMap) context.getAttributes().get(SITE_PRESET_CACHE);
        if (sitePresetCache == null)
        {
            sitePresetCache = new HashMap();
            context.getAttributes().put(SITE_PRESET_CACHE, sitePresetCache);
        }

        // Check if site's preset already has been asked for during this request
        String sitePresetId = (String) sitePresetCache.get(siteId);
        if (sitePresetId == null)
        {
            try
            {
                JSONObject site = jsonGet("/api/sites/" + URLEncoder.encode(siteId));
                if (site != null)
                {
                    sitePresetId = site.getString(SITE_PRESET);
                    sitePresetCache.put(siteId, sitePresetId);
                }
            }
            catch (JSONException e)
            {
                if (logger.isErrorEnabled())
                {
                    logger.error("Could not get a sitePreset from site json.");
                }
            }
        }

        // Return sites preset
        return sitePresetId;
    }

    /**
     * Helper method for making a json get remote call to the default repository.
     *
     * @param uri The uri to get the content for (MUST contain a json response)
     * @return The content of the uri resource parsed into a json object.
     */
    private JSONObject jsonGet(String uri)
    {
        ScriptRemote scriptRemote = serviceRegistry.getScriptRemote();
        Response response = scriptRemote.connect().get(uri);
        if (response.getStatus().getCode() == 200)
        {
            try
            {
                return new JSONObject(response.getResponse());
            }
            catch (JSONException e)
            {
                if (logger.isErrorEnabled())
                {
                    logger.error("An error occurred when parsing response to json from the uri '" + uri + "': " + e.getMessage());
                }
            }
        }
        return null;
    }
}

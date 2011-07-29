/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
package org.alfresco.web.evaluator;

import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;

import java.util.HashMap;

/**
 * Base class for all UI evaluators.
 *
 * @author: mikeh
 */
public abstract class BaseEvaluator implements Evaluator
{
    private static Log logger = LogFactory.getLog(BaseEvaluator.class);

    // optional args from the calling webscript
    private HashMap<String, String> args = null;

    /**
     * Simple getter for optional webscript args
     *
     * @return HashMap args map (may be null)
     */
    public final HashMap<String, String> getArgs()
    {
        return args;
    }

    /**
     * Optional entry point from Rhino script. Converts JSON String to a JSONObject
     * and calls the overridable evaluate() method.
     *
     * @param obj JSON String as received from a Rhino script
     * @return boolean indicating evaluator result
     */
    public final boolean evaluate(Object obj)
    {
        return evaluate(obj, null);
    }

    /**
     * Main entry point from Rhino script. Converts JSON String to a JSONObject
     * and calls the overridable evaluate() method.
     *
     * @param obj JSON String as received from a Rhino script
     * @param args URL arguments passed to calling webscript
     * @return boolean indicating evaluator result
     */
    public final boolean evaluate(Object obj, HashMap<String, String> args)
    {
        JSONObject jsonObject = null;
        this.args = args;

        try
        {
            jsonObject = (JSONObject)JSONValue.parseWithException((String)obj);
        }
        catch (ParseException perr)
        {
            throw new AlfrescoRuntimeException("Failed to parse JSON string: " + perr.getMessage());
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Failed to run UI evaluator: " + err.getMessage());
        }
        return evaluate(jsonObject);
    }

    /**
     * Evaluator implementations override this method.
     *
     * @param jsonObject The object the evaluation is for
     * @return boolean indicating evaluator result
     */
    public abstract boolean evaluate(JSONObject jsonObject);

    /**
     * Retrieve a JSONArray of aspects for a node
     *
     * @param jsonObject JSONObject containing a "node" object as returned from the ApplicationScriptUtils class.
     * @return JSONArray containing aspects on the node
     */
    public final JSONArray getNodeAspects(JSONObject jsonObject)
    {
        JSONArray aspects = null;

        try
        {
            JSONObject node = (JSONObject) jsonObject.get("node");

            if (node != null)
            {
                aspects = (JSONArray) node.get("aspects");
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Exception whilst running UI evaluator: " + err.getMessage());
        }

        return aspects;
    }

    /**
     * Get webscript argument by name
     *
     * @param name Argument name
     * @return string argument value or null
     */
    public final String getArg(String name)
    {
        if (this.args != null && this.args.containsKey(name))
        {
            return this.args.get(name);
        }
        return null;
    }

    /**
     * Retrieve a JSONArray of aspects for a node
     *
     * @param jsonObject JSONObject containing a "node" object as returned from the ApplicationScriptUtils class.
     * @return JSONArray containing aspects on the node
     */
    public final JSONObject getProperty(JSONObject jsonObject, String propertyName)
    {
        JSONObject property = null;

        try
        {
            JSONObject node = (JSONObject) jsonObject.get("node");

            if (node != null)
            {
                JSONObject properties = (JSONObject) node.get("properties");
                if (properties != null)
                {
                    property = (JSONObject) properties.get(propertyName);
                }
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Exception whilst running UI evaluator: " + err.getMessage());
        }

        return property;
    }

    /**
     * Get the current user associated with this request
     *
     * @return String userId
     */
    public final String getUserId()
    {
        final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
        final String userId = rc.getUserId();
        if (userId == null || AuthenticationUtil.isGuest(userId))
        {
            throw new AlfrescoRuntimeException("User ID must exist and cannot be guest.");
        }

        return userId;
    }

    /**
     * Get the site shortName applicable to this node (if requested via a site-based page context)
     *
     * @param jsonObject JSONObject containing a "node" object as returned from the ApplicationScriptUtils class.
     * @return String siteId or null
     */
    public final String getSiteId(JSONObject jsonObject)
    {
        String siteId = null;

        try
        {
            JSONObject location = (JSONObject) jsonObject.get("location");

            if (location != null)
            {
                JSONObject site = (JSONObject) location.get("site");
                if (site != null)
                {
                    siteId = (String) site.get("name");
                }
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Exception whilst querying siteId from location: " + err.getMessage());
        }

        return siteId;
    }

    /**
     * Get the site preset (e.g. "site-dashboard") applicable to this node (if requested via a site-based page context)
     *
     * @param jsonObject JSONObject containing a "node" object as returned from the ApplicationScriptUtils class.
     * @return String site preset or null
     */
    public final String getSitePreset(JSONObject jsonObject)
    {
        String sitePreset = null;

        try
        {
            JSONObject location = (JSONObject) jsonObject.get("location");

            if (location != null)
            {
                JSONObject site = (JSONObject) location.get("site");
                if (site != null)
                {
                    sitePreset = (String) site.get("preset");
                }
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Exception whilst querying site preset from location: " + err.getMessage());
        }

        return sitePreset;
    }

    /**
     * Get the container node type (e.g. "cm:folder") applicable to this node (if requested via a site-based page context)
     *
     * @param jsonObject JSONObject containing a "node" object as returned from the ApplicationScriptUtils class.
     * @return String container type or null
     */
    public final String getContainerType(JSONObject jsonObject)
    {
        String containerType = null;

        try
        {
            JSONObject location = (JSONObject) jsonObject.get("location");

            if (location != null)
            {
                JSONObject container = (JSONObject) location.get("container");
                if (container != null)
                {
                    containerType = (String) container.get("type");
                }
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Exception whilst querying container type from location: " + err.getMessage());
        }

        return containerType;
    }

    /**
     * Retrieve a JSON value given an accessor string containing dot notation (e.g. "node.isContainer")
     *
     * @param jsonObject JSONObject containing a "node" object as returned from the ApplicationScriptUtils class.
     * @param accessor String containing dotted notation path to value
     * @return Object value or null
     */
    public final Object getJSONValue(JSONObject jsonObject, String accessor)
    {
        String[] keys = accessor.split("\\.");
        Object obj = jsonObject;

        for (String key : keys)
        {
            if (obj instanceof JSONObject)
            {
                obj = ((JSONObject)obj).get(key);
            }
            else if (obj instanceof JSONArray)
            {
                obj = ((JSONArray)obj).get(Integer.parseInt(key));
            }
            else
            {
                return null;
            }
        }
        return obj;
    }
}

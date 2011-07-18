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
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.ExtensionModuleEvaluator;

import java.util.Map;

/**
 * <p>
 * Evaluator used to decide if an extension module (and its {@code<components>} & {@code<customizations>}) shall be
 * used for this request.
 * </p>
 *
 * <p>
 * Makes it possible to decide if we are viewed from a portal and (optional) which portal using a regexp
 * in the {@code<portletUrls>} parameter.
 * </p>
 *
 * <p>
 * Note! The regexp is expressed without using the surrounding // characters.
 * </p>
 *
 * <p>
 * Example:
 * </p>
 *
 * <pre>{@code<evaluator>portlet.module.evaluator</evaluator>}</pre>
 *
 * <p>
 * Will return true of we are viewed from inside a portal otherwise false.
 * </p>
 *
 * <p>
 * Example 2:
 * </p>
 *
 * <pre>{@code
 * <evaluator>portlet.module.evaluator</evaluator>
 * <evaluatorProperties>
 *     <portletUrls>regexp mathing a portlet url</portletUrls>
 * </evaluatorProperties>
 * }</pre>
 *
 * <p>
 * Will return true of we are viewed from inside a portal and that portal's url matches the regexp in the
 * {@code<portletUrls>} parameter.
 * </p>
 *
 * @author ewinlof
 */
public class SlingshotPortletModuleEvaluator implements ExtensionModuleEvaluator
{
    private static Log logger = LogFactory.getLog(SlingshotPortletModuleEvaluator.class);

    /* Evaluator parameters */
    public static final String PORTLET_URLS_FILTER = "portletUrls";

    protected SlingshotEvaluatorUtil util = null;

    public void setSlingshotEvaluatorUtil(SlingshotEvaluatorUtil slingshotExtensibilityUtil)
    {
        this.util = slingshotExtensibilityUtil;
    }

    public String[] getRequiredProperties()
    {
        String[] properties = new String[1];
        properties[0] = PORTLET_URLS_FILTER;
        return properties;
    }

    /**
     * Decides if we are inside a portal or not.
     *
     * @param context
     * @param params
     * @return true if we are in a portlet and its url matches the {@code<portletUrls>} param (defaults to ".*")
     */
    public boolean applyModule(RequestContext context, Map<String, String> params)
    {
        // Find the portlet host
        Boolean portletHost = util.getPortletHost(context);
        String portletUrl = util.getPortletUrl(context);

        // Check if we are viewed from inside a portlet
        if (portletHost)
        {
            // Yes we are viewed from a portlet
            if (portletUrl == null)
            {
                // If no url was provided we set it to something that will match a non existing filter
                portletUrl = "";
            }

            // Match against the url filter
            return portletUrl.matches(util.getEvaluatorParam(params, PORTLET_URLS_FILTER, ".*"));
        }

        // No we are not in a portlet
        return false;
    }

}

/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
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
package org.alfresco.wcm.client.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.Section;
import org.alfresco.wcm.client.WebSite;
import org.alfresco.wcm.client.WebSiteService;
import org.alfresco.wcm.client.exception.PageNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Load application-wide data into the Surf RequestContext and Spring model.
 * 
 * @author Chris Lack
 */
public class ApplicationDataInterceptor extends HandlerInterceptorAdapter
{
    private static final Log log = LogFactory.getLog(ApplicationDataInterceptor.class);
    
    private WebSiteService webSiteService;
    private ModelDecorator modelDecorator;

    /**
     * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle(HttpServletRequest,
     *      HttpServletResponse, Object)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        RequestContext requestContext = ThreadLocalRequestContext.getRequestContext();

        // Get the website object and store it in the surf request context
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        WebSite webSite = webSiteService.getWebSite(serverName, serverPort, contextPath);
        
        if (webSite == null)
        {
            log.warn("Received request for which no configured website can be found: " + 
                    serverName + ":" + serverPort);
            throw new PageNotFoundException(serverName + ":" + serverPort);
        }
            
        WebSiteService.setThreadWebSite(webSite);
        requestContext.setValue("webSite", webSite);
        requestContext.setValue("website", webSite);

        // Get the current asset and section and store them in the surf request
        // context
        String path = request.getPathInfo();
        Asset asset = webSite.getAssetByPath(path);
        requestContext.setValue("asset", asset);

        Section section;
        if (asset != null)
        {
            section = asset.getContainingSection();
        }
        else
        {
            // If asset not found then try just the section
            section = webSite.getSectionByPath(path);
            if (section == null)
            {
                // Else store the root section for use by the 404 page.
                section = webSite.getRootSection();
            }
        }
        requestContext.setValue("section", section);

        return super.preHandle(request, response, handler);
    }

    /**
     * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#postHandle(HttpServletRequest,
     *      HttpServletResponse, Object, ModelAndView)
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception
    {
        super.postHandle(request, response, handler, modelAndView);

        modelDecorator.populate(request, modelAndView);
    }

    public void setWebSiteService(WebSiteService webSiteService)
    {
        this.webSiteService = webSiteService;
    }

    public void setModelDecorator(ModelDecorator modelDecorator)
    {
        this.modelDecorator = modelDecorator;
    }
}

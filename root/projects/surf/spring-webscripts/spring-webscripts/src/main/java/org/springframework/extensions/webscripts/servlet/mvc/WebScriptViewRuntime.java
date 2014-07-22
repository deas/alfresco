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

package org.springframework.extensions.webscripts.servlet.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.config.ServerProperties;
import org.springframework.extensions.webscripts.RuntimeContainer;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.servlet.ServletAuthenticatorFactory;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRuntime;

/**
 * Runtime for WebScript View.
 * 
 * Extends the WebScript servlet runtime, delegates to the resource controller
 * for access to resources and constructs using the supplied MVC view name URL.
 * 
 * @author muzquiano
 * @author kevinr
 */
public class WebScriptViewRuntime extends WebScriptServletRuntime
{
    private String url;
    
    /**
     * Construction
     * 
     * @param url           View name URL
     * @param container
     * @param authFactory
     * @param req
     * @param res
     * @param serverProperties
     */
    public WebScriptViewRuntime(String url, RuntimeContainer container, ServletAuthenticatorFactory authFactory, HttpServletRequest req, HttpServletResponse res, ServerProperties serverProperties)
    {
        super(container, authFactory, req, res, serverProperties);
        this.url = url;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRuntime#getScriptUrl()
     */
    @Override
    protected String getScriptUrl()
    {
        return this.url;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRuntime#createResponse()
     */
    protected WebScriptResponse createResponse()
    {
        servletRes = new WebScriptViewResponse(this, res);
        return servletRes;
    }
}
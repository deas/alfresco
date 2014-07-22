/*
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
package org.springframework.extensions.webscripts;

import java.util.Collections;
import java.util.Map;

public class DefaultURLModel implements URLModel
{
    private final static Map<String, String> emptyArgs = Collections.emptyMap();
    private final WebScriptRequest req;
    
    /**
     * Construct
     * 
     * @param req
     * @param res
     */
    protected DefaultURLModel(WebScriptRequest req)
    {
        this.req = req;
    }

    /**
     * Gets the Server Path
     * 
     * e.g.  http://host:port
     * 
     * @return  server path
     */
    public String getServer()
    {
        return req.getServerPath();
    }
    
    /**
     * Gets the Context Path
     * 
     * e.g. /alfresco
     * 
     * @return  context path
     */
    public String getContext()
    {
        return req.getContextPath();
    }

    /**
     * Gets the Service Context Path
     * 
     * e.g. /alfresco/service
     * 
     * @return  service context path
     */
    public String getServiceContext()
    {
        return req.getServiceContextPath();
    }

    /**
     * Gets the Service Path
     * 
     * e.g. /alfresco/service/search/keyword
     * 
     * @return  service path
     */
    public String getService()
    {
        return req.getServicePath();
    }

    /**
     * Gets the full path
     * 
     * e.g. /alfresco/service/search/keyword?q=term
     * 
     * @return  service path
     */
    public String getFull()
    {
        return req.getURL();
    }
    
    /**
     * Gets the URL arguments (query string)
     * 
     * @return  args (query string)
     */
    public String getArgs()
    {
        String args = req.getQueryString();
        return (args == null) ? "" : args;
    }
    
    /**
     * Gets the matching service path
     * 
     * e.g.
     * a) service registered path = /search/engine
     * b) request path = /search/engine/external
     * 
     * => /search/engine
     * 
     * @return  matching path
     */
    public String getMatch()
    {
        return req.getServiceMatch().getPath();
    }
    
    /**
     * Gets the Service Extension Path
     * 
     * e.g.
     * a) service registered path = /search/engine
     * b) request path = /search/engine/external
     * 
     * => /external
     * 
     * @return  extension path
     */
    public String getExtension()
    {
        return req.getExtensionPath();
    }
    
    /**
     * Gets the template form of this path
     * 
     * @return  template form of path
     */
    public String getTemplate()
    {
        return req.getServiceMatch().getTemplate();
    }
    
    /**
     * Gets the values of template variables
     * 
     * @return  map of value indexed by variable name (or the empty map)
     */
    public Map<String, String> getTemplateArgs()
    {
        Map<String, String> args = req.getServiceMatch().getTemplateVars();
        return (args == null) ? emptyArgs : args;
    }
}

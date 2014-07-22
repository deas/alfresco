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

package org.springframework.extensions.webscripts;

import org.springframework.extensions.webscripts.ui.common.StringUtils;


/**
 * Basic Implementation of a Web Script Request
 * 
 * @author davidc
 */
public abstract class WebScriptResponseImpl implements WebScriptResponse
{
    private Runtime runtime;
    
    /**
     * Construct
     * 
     * @param runtime
     */
    public WebScriptResponseImpl(Runtime runtime)
    {
        this.runtime = runtime;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptResponse#getRuntime()
     */
    public Runtime getRuntime()
    {
        return runtime;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.WebScriptResponse#encodeResourceUrl(java.lang.String)
     */
    public String encodeResourceUrl(String url)
    {
        return url;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptResponse#getEncodeScriptUrlFunction(java.lang.String)
     */
    public String getEncodeResourceUrlFunction(String name)
    {
       String func = ENCODE_RESOURCE_URL_FUNCTION.replace("$name$", name);
       return StringUtils.encodeJavascript(func);
    }

    private static final String ENCODE_RESOURCE_URL_FUNCTION = 
        "{ $name$: function(url) {" + 
        " return url; } }";   
}

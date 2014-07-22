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

import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.webscripts.Runtime;
import org.springframework.extensions.webscripts.servlet.WebScriptServletResponse;
import org.springframework.extensions.webscripts.ui.common.StringUtils;

/**
 * Used by the web script view to hand back correct resource urls that reference
 * the resource controller.
 * 
 * @author muzquiano
 */
public class WebScriptViewResponse extends WebScriptServletResponse
{
    public WebScriptViewResponse(Runtime container, HttpServletResponse res)
    {
        super(container, res);
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.WebScriptResponseImpl#encodeResourceUrl(java.lang.String)
     */
    public String encodeResourceUrl(String url)
    {
        if (url != null)
        {
            return "/res" + (url.startsWith("/") ? url : '/' + url);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.WebScriptResponseImpl#getEncodeResourceUrlFunction(java.lang.String)
     */
    public String getEncodeResourceUrlFunction(String name)
    {
        String s = "{ $name$: function(url) { return '/res' + url; } }".replace("$name$", name);
        return StringUtils.encodeJavascript(s);
    }
}

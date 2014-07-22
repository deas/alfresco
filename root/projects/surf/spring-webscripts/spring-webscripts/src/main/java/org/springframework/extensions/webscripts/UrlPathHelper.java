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

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.WebUtils;

/**
 * Overrides the SpringMVC default UrlPathHelper class to disable the use of the
 * "clean" method which incorrectly removes everything from the URI path after
 * the ; semi-colon character - as after decoding the URI this could well be a valid
 * part of the URI path or a url argument.
 * 
 * @author Kevin Roast
 */
public class UrlPathHelper extends org.springframework.web.util.UrlPathHelper
{
    @Override
    public String getRequestUri(HttpServletRequest request)
    {
        String uri = (String) request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE);
        if (uri == null)
        {
            uri = request.getRequestURI();
        }
        return decodeRequestString(request, uri);
    }
    
    @Override
    public String getOriginatingRequestUri(HttpServletRequest request)
    {
        String uri = (String) request.getAttribute(WebUtils.FORWARD_REQUEST_URI_ATTRIBUTE);
        if (uri == null)
        {
            uri = request.getRequestURI();
        }
        return decodeRequestString(request, uri);
    }
}

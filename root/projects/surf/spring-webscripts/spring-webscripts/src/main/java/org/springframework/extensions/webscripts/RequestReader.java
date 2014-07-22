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

import java.util.HashMap;
import java.util.Map;

import org.springframework.extensions.surf.util.Content;


/**
 * Convert request stream to class org.alfresco.util.Content
 * 
 * @author davidc
 */ 
public class RequestReader implements FormatReader<Content>
{
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.FormatReader#getSourceMimetype()
     */
    public String getSourceMimetype()
    {
        return "*/*";
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.FormatReader#getDestinationClass()
     */
    public Class<Content> getDestinationClass()
    {
        return Content.class;
    }
 
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.FormatReader#read(org.alfresco.web.scripts.WebScriptRequest)
     */
    public Content read(WebScriptRequest req)
    {
    	return req.getContent();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.FormatReader#createScriptParameters(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.WebScriptResponse)
     */
    public Map<String, Object> createScriptParameters(WebScriptRequest req, WebScriptResponse res)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("requestbody", read(req));
        return params;
    }

}

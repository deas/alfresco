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

package org.springframework.extensions.webscripts.servlet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.extensions.webscripts.FormatReader;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;


/**
 * Convert multipart/formdata to class org.alfresco.web.scripts.servlet.FormData
 * 
 * @author davidc
 */
public class FormDataReader implements FormatReader<FormData>
{
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.FormatReader#getSourceMimetype()
     */
    public String getSourceMimetype()
    {
        return "multipart/form-data";
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.FormatReader#getDestinationClass()
     */
    public Class<FormData> getDestinationClass()
    {
        return FormData.class;
    }
 
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.FormatReader#read(org.alfresco.web.scripts.WebScriptRequest)
     */
    public FormData read(WebScriptRequest req)
    {
        if (!(req instanceof WebScriptServletRequest))
        {
            throw new WebScriptException("Failed to convert request to FormData");
        }
        return new FormData(((WebScriptServletRequest)req).getHttpServletRequest());
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.FormatReader#createScriptParameters(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.WebScriptResponse)
     */
    public Map<String, Object> createScriptParameters(WebScriptRequest req, WebScriptResponse res)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        // Because form data is 'special', the request may have already parsed
        // it, so ask the request for the cached content
        params.put("formdata", req.parseContent());
        return params;
    }
}

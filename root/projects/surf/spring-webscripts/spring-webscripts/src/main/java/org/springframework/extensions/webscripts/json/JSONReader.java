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

package org.springframework.extensions.webscripts.json;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.Format;
import org.springframework.extensions.webscripts.FormatReader;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Convert application/json to org.json.JSONObject or org.json.JSONArray
 * 
 * @author Roy Wetherall
 */
public class JSONReader implements FormatReader<Object>
{
    /**
     * @see org.springframework.extensions.webscripts.FormatReader#getDestinationClass()
     */
    public Class<? extends Object> getDestinationClass()
    {
        return Object.class;
    }

    /**
     * @see org.springframework.extensions.webscripts.FormatReader#getSourceMimetype()
     */
    public String getSourceMimetype()
    {
        return Format.JSON.mimetype();
    }

    /**
     * @see org.springframework.extensions.webscripts.FormatReader#read(org.springframework.extensions.webscripts.WebScriptRequest)
     */
    public Object read(WebScriptRequest req)
    {
        Content content = req.getContent();
        if (content == null)
        {
            throw new WebScriptException("Failed to convert request to JSON");
        }
        
        Object result = null;
        try
        {
            String jsonString = content.getContent();
            if (jsonString.startsWith("[") == true)
            {
                result = new JSONArray(jsonString);
            }
            else
            {    
                result = new JSONObject(jsonString);
            }
        }
        catch (Exception exception)
        {
            throw new WebScriptException("Failed to convert request to JSON", exception);
        }        
        return result;
    }
    
    /**
     * @see org.springframework.extensions.webscripts.FormatReader#createScriptParameters(org.springframework.extensions.webscripts.WebScriptRequest, org.springframework.extensions.webscripts.WebScriptResponse)
     */
    public Map<String, Object> createScriptParameters(WebScriptRequest req, WebScriptResponse res)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("json", read(req));
        return params;
    }
}

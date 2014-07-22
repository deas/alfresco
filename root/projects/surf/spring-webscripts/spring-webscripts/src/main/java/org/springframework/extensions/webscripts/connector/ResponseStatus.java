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

package org.springframework.extensions.webscripts.connector;

import java.util.HashMap;
import java.util.Map;

import org.springframework.extensions.webscripts.Status;

/**
 * Wrapper around the Status object that allows the Remote Client to
 * expose header state.
 * 
 * Records the outcome of a call
 * 
 * @author muzquiano
 */
public class ResponseStatus extends Status
{
    private Map<String, String> headers = new HashMap<String, String>(16, 1.0f);
    
    /**
     * Allows for response headers to be stored onto the status
     * 
     * @param headerName name of the header
     * @param headerValue value of the header
     */
    public void setHeader(String headerName, String headerValue)
    {
        this.headers.put(headerName, headerValue);
    }
    
    /**
     * Retrieves response headers
     * 
     * @return map of response headers
     */
    public Map<String, String> getHeaders()
    {
        return this.headers;
    }
}

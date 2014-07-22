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

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Web Script
 * 
 * @author davidc
 */
public interface WebScript
{
    /**
     * Initialise a WebScript
     * 
     * @param container     Parent container
     * @param description   Description of the WebScript provided by the registry
     */
    public void init(Container container, Description description);
    
    /**
     * Gets the Service Description
     * 
     * @return service description
     */
    public Description getDescription();
    
    /**
     * Gets the Service Resources
     * 
     * @return ResourceBundle of services resources or null if none present
     */
    public ResourceBundle getResources();
    
    /**
     * Execute the Service
     * 
     * @param req   WebScriptRequest representing the request to this service
     * @param res   WebScriptResponse encapsulating the result of this service
     * 
     * @throws IOException
     */
    public void execute(WebScriptRequest req, WebScriptResponse res)
        throws IOException;
    
    
    public void setURLModelFactory(URLModelFactory urlModelFactory); 
}

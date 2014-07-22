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

import org.springframework.extensions.webscripts.Description.RequiredAuthentication;


/**
 * Web Script Container extensions for a Web Script Runtime
 * 
 * @author davidc
 */
public interface RuntimeContainer extends Container
{
    /**
     * Execute the script in the context of the provided request and response
     * 
     * @param scriptReq
     * @param scriptRes
     */
    public void executeScript(WebScriptRequest scriptReq, WebScriptResponse scriptRes, Authenticator auth)
        throws IOException;
    
    /**
     * Gets the required container authentication level (for matching web scripts)
     * 
     * @return  the required authentication level
     */
    public RequiredAuthentication getRequiredAuthentication();
    
    /**
     * Pre-authenticate container, if required
     * 
     * @param auth
     * @param required
     */
    public boolean authenticate(Authenticator auth, RequiredAuthentication required);
}

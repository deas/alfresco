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

import java.util.Map;


/**
 * Web Script Runtime
 * 
 * @author davidc
 */
public interface Runtime
{
	/**
	 * Gets the name of the Web Script Runtime
	 * 
	 * @return  name
	 */
    public String getName();
    
    /**
     * Get the Web Script Session
     * 
     * @return  web script session
     */
    public WebScriptSession getSession();
    
    /**
     * Gets the Web Script Container within which this Runtime is hosted
     * 
     * @return  web script container
     */
    public Container getContainer();
    
    /**
     * Gets script parameters
     * 
     * @return  script parameters provided by the runtime
     */
    public Map<String, Object> getScriptParameters();
    
    /**
     * Gets template parameters
     * 
     * @return  template parameters provided by the runtime
     */
    public Map<String, Object> getTemplateParameters();
}

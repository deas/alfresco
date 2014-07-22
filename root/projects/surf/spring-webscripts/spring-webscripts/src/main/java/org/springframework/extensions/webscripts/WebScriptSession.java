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

/**
 * Web Script Session
 *  
 */
public interface WebScriptSession
{
    /**
     * Gets the id of the session
     * 
     * @return session id
     */
    public String getId();
    
    /**
     * Gets a value from the session
     * 
     * @param name value name
     * @return value
     */
    public Object getValue(String name);
    
    /**
     * Sets a value in the session
     * 
     * @param name value name
     * @param value value
     */
    public void setValue(String name, Object value);
    
    /**
     * Remove value from the session
     * 
     * @param name value name
     */
    public void removeValue(String name);
}

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
 * Provides remote sandbox context information to Alfresco Store
 * implementations.
 * 
 * @author muzquiano
 */
public interface PreviewContextProvider
{
    /**
     * Provides the remote sandbox context instance.
     * 
     * @return remote sandbox context
     */
    public PreviewContext provide();
    
    /**
     * Sets the default store id
     * 
     * @param defaultStoreId
     */
    public void setDefaultStoreId(String defaultStoreId);
    
    /**
     * Gets the default store id
     * 
     * @return default store id
     */
    public String getDefaultStoreId();
    
    /**
     * Sets the default webapp id
     * 
     * @param defaultWebappId
     */
    public void setDefaultWebappId(String defaultWebappId);
    
    /**
     * Gets the default webapp id
     * 
     * @return default webapp id
     */
    public String getDefaultWebappId();
}

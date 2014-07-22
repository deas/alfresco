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
 * Provides information about the WCM sandbox which is currently being
 * previewed.
 * 
 * A sandbox context provider may be registered with any of the Alfresco
 * Store types.
 * 
 *    LocalFileSystemStore
 *    RemoteStore
 *    
 * @author muzquiano
 */
public class PreviewContext
{
    public static final String DEFAULT_STORE_ID = "sitestore";
    
    private String storeId = null;
    private String webappId = null;
    private String userId = null;
    
    public PreviewContext()
    {
        this.storeId = DEFAULT_STORE_ID;
    }
    
    /**
     * Constructs a new preview context instance
     * 
     * @param storeId the store id
     */
    public PreviewContext(String storeId)
    {
        this.storeId = storeId;
    }
    
    /**
     * Constructs a new preview context instance
     * 
     * @param storeId the store id
     * @param webappId the WCM web application id
     */
    public PreviewContext(String storeId, String webappId)
    {
        this.storeId = storeId;
        this.webappId = webappId;
    }
    
    /**
     * Constructs a new preview context instance
     * 
     * @param storeId the store id
     * @param webappId the WCM web application id
     * @param userId the user id
     */
    public PreviewContext(String storeId, String webappId, String userId)
    {
        this.storeId = storeId;
        this.webappId = webappId;
        this.userId = userId;
    }
    
    /**
     * Gets the id of the store
     * 
     * @return the store id
     */
    public String getStoreId()
    {
        return this.storeId;
    }
    
    /**
     * Sets the id of the store
     * 
     * @param storeId the store id
     */
    public void setStoreId(String storeId)
    {
        this.storeId = storeId;
    }
    
    /**
     * Gets the WCM web application id
     * 
     * This applies for the case where the store is an AVM store which
     * was built by Alfresco WCM.
     * 
     * @return the WCM web application id
     */
    public String getWebappId()
    {
        return this.webappId;
    }
    
    /**
     * Sets the WCM web application id
     * 
     * This applies for the case where the store is an AVM store which
     * was built by Alfresco WCM.
     * 
     * @param webappId the WCM web application id
     */
    public void setWebappId(String webappId)
    {
        this.webappId = webappId;
    }
    
    /**
     * Gets the user id
     * 
     * @return user id
     */
    public String getUserId()
    {
        return this.userId;
    }
    
    /**
     * Sets the user id
     * 
     * @param userId user id
     */
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
}
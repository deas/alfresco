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

import java.util.Date;


/**
 * Web Script Cache
 *
 * Records the desired cache requirements for the Web Script
 * 
 * @author davidc
 */
public class Cache implements Description.RequiredCache
{
    private boolean neverCache = true;
    private boolean isPublic = false;
    private boolean mustRevalidate = true;
    private Date lastModified = null;
    private String eTag = null;
    private Long maxAge = null;

    
    /**
     * Construct
     */
    public Cache()
    {
    }
    
    /**
     * Construct
     * 
     * @param requiredCache
     */
    public Cache(Description.RequiredCache requiredCache)
    {
        neverCache = requiredCache.getNeverCache();
        isPublic = requiredCache.getIsPublic();
        mustRevalidate = requiredCache.getMustRevalidate();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptDescription.RequiredCache#getNeverCache()
     */
    public boolean getNeverCache()
    {
        return neverCache;
    }
    
    /**
     * @param neverCache
     */
    public void setNeverCache(boolean neverCache)
    {
        this.neverCache = neverCache;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptDescription.RequiredCache#getIsPublic()
     */
    public boolean getIsPublic()
    {
        return isPublic;
    }
    
    /**
     * @param isPublic
     */
    public void setIsPublic(boolean isPublic)
    {
        this.isPublic = isPublic;
    }

    /**
     * @return last modified
     */
    public Date getLastModified()
    {
        return lastModified;
    }
    
    /**
     * @param lastModified
     */
    public void setLastModified(Date lastModified)
    {
        this.lastModified = lastModified;
    }
    
    /**
     * @return  ETag
     */
    public String getETag()
    {
        return eTag;
    }
    
    /**
     * @param tag  ETag
     */
    public void setETag(String tag)
    {
        eTag = tag;
    }
    
    /**
     * @return  Max Age (seconds)
     */
    public Long getMaxAge()
    {
        return maxAge;
    }
    
    /**
     * @param maxAge  Max Age (seconds)
     */
    public void setMaxAge(Long maxAge)
    {
        this.maxAge = maxAge;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptDescription.RequiredCache#getMustRevalidate()
     */
    public boolean getMustRevalidate()
    {
        return mustRevalidate;
    }
    
    /**
     * @param mustRevalidate
     */
    public void setMustRevalidate(boolean mustRevalidate)
    {
        this.mustRevalidate = mustRevalidate;
    }
    
}

/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.util;

import org.alfresco.webdrone.AlfrescoVersion;
import org.springframework.stereotype.Component;
/**
 * Properties used for test cases.
 * 
 * @author Michael Suzuki
 * @since 1.1
 */
@Component
public class ShareTestProperty
{
    private String shareUrl;
    private String gridUrl;
    private String username;
    private String password;
    private AlfrescoVersion alfrescoVersion;
    private Integer maxIteration;
    
    public ShareTestProperty (final String shareUrl, final String username, final String password) 
    {
        this.shareUrl = shareUrl;
        this.username = username;
        this.password = password;
    }
    
    public ShareTestProperty (final String shareUrl) 
    {
        this.shareUrl = shareUrl;
    }
    
    public String getShareUrl()
    {
        return shareUrl;
    }
    
    public void setShareUrl(String shareUrl)
    {
        this.shareUrl = shareUrl;
    }

    public String getGridUrl()
    {
        return gridUrl;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public AlfrescoVersion getAlfrescoVersion()
    {
        return alfrescoVersion;
    }

    public void setAlfrescoVersion(AlfrescoVersion alfrescoVersion)
    {
        this.alfrescoVersion = alfrescoVersion;
    }

    public Integer getMaxIteration()
    {
        return maxIteration;
    }

    public void setMaxIteration(Integer maxIteration)
    {
        this.maxIteration = maxIteration;
    }
    
}

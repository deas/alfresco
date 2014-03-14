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
package org.alfresco.po.share.util;

import org.alfresco.po.share.AlfrescoVersion;

/**
 * Properties used for test cases.
 * 
 * @author Michael Suzuki
 * @since 1.1
 */
public class ShareTestProperty
{
    private final AlfrescoVersion alfrescoVersion;
    private final String shareUrl;
    private final String gridUrl;
    private final String username;
    private final String password;
    private final String googleUserName;
    private final String googlePassword;
    private final String downloadDirectory;
    private final boolean hybridEnabled;
    private String cloudUserName;
    private String cloudUserPassword;
    protected long popupRendertime;
    
    public ShareTestProperty (final String shareUrl,
                              final String gridUrl, 
                              final String username,
                              final String password,
                              final String googleUserName,
                              final String googlePassword,
                              final String alfrescoVersion,
                              final String downloadDirectory,
                              final boolean hybridEnabled) 
    {
        this.shareUrl = shareUrl;
        this.gridUrl = gridUrl;
        this.username = username;
        this.password = password;
        this.googleUserName = googleUserName;
        this.googlePassword = googlePassword;
        this.alfrescoVersion = AlfrescoVersion.fromString(alfrescoVersion);
        this.downloadDirectory = downloadDirectory;
        this.hybridEnabled = hybridEnabled;
    }
    
    public String getShareUrl()
    {
        return shareUrl;
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

    public String getDownloadDirectory()
    {
        return downloadDirectory;
    }
    public String getGoogleUserName()
    {
        return googleUserName;
    }
    
    public String getGooglePassword()
    {
        return googlePassword;
    }

    public boolean isHybridEnabled()
    {
        return hybridEnabled;
    }

    public String getCloudUserName()
    {
        return cloudUserName;
    }

    public void setCloudUserName(String cloudUserName)
    {
        this.cloudUserName = cloudUserName;
    }

    public String getCloudUserPassword()
    {
        return cloudUserPassword;
    }

    public void setCloudUserPassword(String cloudUserPassword)
    {
        this.cloudUserPassword = cloudUserPassword;
    }

    public long getPopupRendertime() {
        return popupRendertime;
    }

    public void setPopupRendertime(long popupRendertime) {
        this.popupRendertime = popupRendertime;
    }
}


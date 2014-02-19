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

import org.alfresco.po.share.AlfrescoVersion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Properties used for test cases.
 * 
 * @author Michael Suzuki
 * @since 1.1
 */
public class ShareTestProperty
{
    private static final Log logger = LogFactory.getLog(ShareTestProperty.class);
    private final String shareUrl;
    private final String cloudUrlForHybrid;
    private final String gridUrl;
    private final String username;
    private final String password;
    private final AlfrescoVersion alfrescoVersion;
    private final String downloadDirectory;
    private final String googleUserName;
    private final String googlePassword;
    private final boolean hybridEnabled;
    private final String uniqueTestRunName;
    private final String domainFree;
    private final String domainPremium;
    private final String domainHybrid;
    private final String defaultUser;
    private final String uniqueTestDataString;
    private final String adminUsername;
    private final String adminPassword;
    private final String superadminUsername;
    private final String superadminPassword;
    private final String headerKey;
    private final int httpSecurePort;
    private final String mimeTypes;

    public ShareTestProperty(
            final String shareUrl, 
            final String gridUrl, 
            final String username, 
            final String password, 
            AlfrescoVersion alfrescoVersion, 
            String cloudUrlForHybrid, 
            final String downloadDirectory, 
            final String googleUserName, 
            final String googlePassword, 
            final boolean hybridEnabled,            
            final String uniqueTestRunName,
            final String domainFree,
            final String domainPremium,
            final String domainHybrid,
            final String defaultUser,
            final String uniqueTestDataString,
            final String adminUsername,
            final String adminPassword,
            final String superadminUsername,
            final String superadminPassword,
            final int httpSecurePort,
            final String headerKey,
            final String mimeTypes
            )
    {
        this.shareUrl = shareUrl;
        this.cloudUrlForHybrid = cloudUrlForHybrid;
        this.gridUrl = gridUrl;
        this.username = username;
        this.password = password;
        this.alfrescoVersion = alfrescoVersion;
        this.downloadDirectory = downloadDirectory;
        this.googleUserName = googleUserName;
        this.googlePassword = googlePassword;
        this.hybridEnabled = hybridEnabled;
        this.uniqueTestRunName = uniqueTestRunName;
        this.domainFree = domainFree;
        this.domainPremium = domainPremium;
        this.domainHybrid = domainHybrid;
        this.defaultUser = defaultUser;
        this.uniqueTestDataString = uniqueTestDataString;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.superadminUsername = superadminUsername;
        this.superadminPassword = superadminPassword;
        this.headerKey = headerKey;
        this.httpSecurePort = httpSecurePort;
        this.mimeTypes = mimeTypes;
    }

    public String getShareUrl()
    {
        return shareUrl;
    }

    /**
     * @return the httpSecurePort
     */
    public int getHttpSecurePort()
    {
        return httpSecurePort;
    }

    /**
     * @return the cloudUrlForHybrid
     */
    public String getCloudUrlForHybrid()
    {
        return this.cloudUrlForHybrid;
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

    /**
     * @return the googleUserName
     */
    public String getGoogleUserName()
    {
        return googleUserName;
    }

    /**
     * @return the googlePassword
     */
    public String getGooglePassword()
    {
        return googlePassword;
    }

    /**
     * @return true if Hybrid Sync is enabled
     */
    public boolean isHybridEnabled()
    {
        return hybridEnabled;
    }
    
    /**
     * @return the uniqueTestRunName
     */
    public String getuniqueTestRunName()
    {
        return uniqueTestRunName;
    }

    /**
     * @return the domainFree
     */
    public String getdomainFree()
    {
        return domainFree;
    }

    /**
     * @return the domainPremium
     */
    public String getdomainPremium()
    {
        return domainPremium;
    }
    
    /**
     * @return the domainHybrid
     */
    public String getdomainHybrid()
    {
        return domainHybrid;
    }

    /**
     * @return the defaultUser
     */
    public String getdefaultUser()
    {
        return defaultUser;
    }

    /**
     * @return the uniqueTestDataString
     */
    public String getuniqueTestDataString()
    {
        return uniqueTestDataString;
    }

    /**
     * @return the adminUsername
     */
    public String getadminUsername()
    {
        return adminUsername;
    }

    /**
     * @return the adminPassword
     */
    public String getadminPassword()
    {
        return adminPassword;
    }
    
    /**
     * @return the headerKey
     */
    public String getHeaderKey()
    {
        return headerKey;
    }
    
    /**
     * @return the adminUsername
     */
    public String getSuperadminUsername()
    {
        return superadminUsername;
    }

    /**
     * @return the adminPassword
     */
    public String getSuperadminPassword()
    {
        return superadminPassword;
    }
    
    /**
     * @return The Mime Types.
     */
    public String getMimeTypes()
    {
        return mimeTypes;
    }

    /**
     * To string method
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "ShareTestProperty [shareUrl=" + shareUrl + ", cloudUrlForHybrid=" + cloudUrlForHybrid + ", gridUrl=" + gridUrl + ", username=" + username
                + ", password=" + password + ", alfrescoVersion=" + alfrescoVersion + ", downloadDirectory=" + downloadDirectory + ", googleUserName="
                + googleUserName + ", googlePassword=" + googlePassword + ", mimeTypes=" + mimeTypes + "]";
    }
}

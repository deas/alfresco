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
    private String shareUrl; // Not final to allow test AbstractUtilsTest to set this
    private final String apiUrl;
    private final String cloudUrlForHybrid;
    private final String pentahoUserConsoleUrl;
    private final String gridUrl;
    private final String username;
    private final String password;
    private final AlfrescoVersion alfrescoVersion;
    private final String downloadDirectory;
    private final String googleUserName;
    private final String googlePassword;
    private final String serviceAccountEmail;
    private final String serviceAccountPKCS12FileName;
    private final String googleDriveUserName;
    private final String googleDrivePassword;
    private final String serviceDriveAccountEmail;
    private final String serviceDriveAccountPKCS12FileName;
    private final boolean hybridEnabled;
    private final String uniqueTestRunName;
    private final String domainFree;
    private final String domainPremium;
    private final String domainHybrid;
    private final String domainLiveSearch;
    private final String defaultUser;
    private final String uniqueTestDataString;
    private final String adminUsername;
    private final String adminPassword;
    private final String superadminUsername;
    private final String superadminPassword;
    private final String headerKey;
    private final int httpSecurePort;
    private final String mimeTypes;
    private String jmxrmiPort;
    private String jmxrmiUser;
    private String jmxrmiPassword;
    private String blogUrl;
    private String blogUsername;
    private String blogPassword;
    private String nodePort;
    private final String licenseShare;
    private final String maxWaitTimeCloudSync;
    private String ftpPort;
    private String sshHost;
    private int serverShhPort;
    private String serverUser;
    private String serverPass;
    private boolean isSecureSession;
    private String pathSharepoint;
    private String layer7Disabled;
    private String apiKey;
    private String apiSecretKey;
    private String replicationEndPointHost;

    public ShareTestProperty(final String shareUrl, final String apiUrl, final String gridUrl, final String username, final String password, String alfrescoVersion,
            String cloudUrlForHybrid, final String pentahoUserConsoleUrl, final String downloadDirectory, final String googleUserName, final String googlePassword,
            final String serviceAccountEmail, final String serviceAccountPKCS12FileName, final String googleDriveUserName, final String googleDrivePassword,
            final String serviceDriveAccountEmail, final String serviceDriveAccountPKCS12FileName, final boolean hybridEnabled,
            final String uniqueTestRunName, final String domainFree, final String domainPremium, final String domainHybrid, final String domainLiveSearch, final String defaultUser,
            final String uniqueTestDataString, final String adminUsername, final String adminPassword, final String superadminUsername,
            final String superadminPassword, final int httpSecurePort, final String headerKey, final String mimeTypes, final String jmxrmiPort, final String jmxrmiUser, final String jmxrmiPassword, final String nodePort, final String licenseShare, final String maxWaitTimeCloudSync,
            final String blogUrl, final String blogUsername, final String blogPassword, final String ftpPort, final int serverShhPort, final String serverUser, final String serverPass, final boolean isSecureSession, final String pathSharepoint, final String replicationEndPointHost,
            final String layer7Disabled, final String apiKey, final String apiSecretKey)
    {
        this.shareUrl = shareUrl;
        this.apiUrl = apiUrl;
        this.cloudUrlForHybrid = cloudUrlForHybrid;
        this.gridUrl = gridUrl;
        this.pentahoUserConsoleUrl = pentahoUserConsoleUrl;
        this.username = username;
        this.password = password;
        this.alfrescoVersion = AlfrescoVersion.fromString(alfrescoVersion);
        this.downloadDirectory = downloadDirectory;
        this.googleUserName = googleUserName;
        this.googlePassword = googlePassword;
        this.serviceAccountEmail = serviceAccountEmail;
        this.serviceAccountPKCS12FileName = serviceAccountPKCS12FileName;
        this.googleDriveUserName = googleDriveUserName;
        this.googleDrivePassword = googleDrivePassword;
        this.serviceDriveAccountEmail = serviceDriveAccountEmail;
        this.serviceDriveAccountPKCS12FileName = serviceDriveAccountPKCS12FileName;
        this.hybridEnabled = hybridEnabled;
        this.uniqueTestRunName = uniqueTestRunName;
        this.domainFree = domainFree;
        this.domainPremium = domainPremium;
        this.domainHybrid = domainHybrid;
        this.domainLiveSearch = domainLiveSearch;
        this.defaultUser = defaultUser;
        this.uniqueTestDataString = uniqueTestDataString;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.superadminUsername = superadminUsername;
        this.superadminPassword = superadminPassword;
        this.headerKey = headerKey;
        this.httpSecurePort = httpSecurePort;
        this.mimeTypes = mimeTypes;
        this.jmxrmiPort = jmxrmiPort;
        this.jmxrmiUser = jmxrmiUser;
        this.jmxrmiPassword = jmxrmiPassword;
        this.blogUrl = blogUrl;
        this.blogUsername = blogUsername;
        this.blogPassword = blogPassword;
        this.nodePort = nodePort;
        this.licenseShare = licenseShare;
        this.maxWaitTimeCloudSync = maxWaitTimeCloudSync;
        this.ftpPort = ftpPort;
        this.serverShhPort = serverShhPort;
        this.serverUser = serverUser;
        this.serverPass = serverPass;
        this.isSecureSession = isSecureSession;
        this.pathSharepoint = pathSharepoint;
        this.replicationEndPointHost = replicationEndPointHost;
        this.layer7Disabled = layer7Disabled;
        this.apiKey = apiKey;
        this.apiSecretKey = apiSecretKey;
    }

    public String getMaxWaitTimeCloudSync()
    {
        return maxWaitTimeCloudSync;
    }

    public String getLicenseShare()
    {
        return licenseShare;
    }

    public String getShareUrl()
    {
        return shareUrl;
    }

    public String getPentahoUserConsoleUrl()
    {
        return pentahoUserConsoleUrl;
    }

    public String getApiUrl()
    {
        return apiUrl;
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
     * @return the serviceAccountEmail
     */
    public String getServiceAccountEmail()
    {
        return serviceAccountEmail;
    }

    /**
     * @return the serviceAccountPKCS12FileName
     */
    public String getServiceAccountPKCS12FileName()
    {
        return serviceAccountPKCS12FileName;
    }

    /**
     * @return the googleDriveUserName
     */
    public String getGoogleDriveUserName()
    {
        return googleDriveUserName;
    }

    /**
     * @return the googleDrivePassword
     */
    public String getGoogleDrivePassword()
    {
        return googleDrivePassword;
    }

    /**
     * @return the serviceDriveAccountEmail
     */
    public String getServiceDriveAccountEmail()
    {
        return serviceDriveAccountEmail;
    }

    /**
     * @return the serviceDriveAccountPKCS12FileName
     */
    public String getServiceDriveAccountPKCS12FileName()
    {
        return serviceDriveAccountPKCS12FileName;
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
     * @return the domainLiveSearch
     */
    public String getdomainLiveSearch()
    {
        return domainLiveSearch;
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
        logger.info("Header key: " + headerKey);
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
     * Set the URL to Share
     *
     * @param shareUrl
     */
    public void setShareUrl(String shareUrl)
    {
        this.shareUrl = shareUrl;
    }

    /**
     * Set the path to Sharepoint
     *
     * @param pathSharepoint
     */
    public void setPathSharepoint(String pathSharepoint)
    {
        this.pathSharepoint = pathSharepoint;
    }

    /**
     * To string method
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "ShareTestProperty [shareUrl=" + shareUrl + "apiUrl=" + apiUrl + ", cloudUrlForHybrid=" + cloudUrlForHybrid + ", gridUrl=" + gridUrl
                + ", username=" + username + ", password=" + password + ", alfrescoVersion=" + alfrescoVersion + ", downloadDirectory=" + downloadDirectory
                + ", googleUserName=" + googleUserName + ", googlePassword=" + googlePassword + ", serviceAccountEmail=" + serviceAccountEmail
                + ", serviceAccountPKCS12FileName=" + serviceAccountPKCS12FileName + ", googleDriveUserName=" + googleDriveUserName
                + ", googleDrivePassword=" + googleDrivePassword + ", serviceDriveAccountEmail=" + serviceDriveAccountEmail
                + ", serviceDriveAccountPKCS12FileName=" + serviceDriveAccountPKCS12FileName + ", mimeTypes=" + mimeTypes + ", jmxrmiPort=" + jmxrmiPort
                + ", jmxrmiUser=" + jmxrmiUser + ", jmxrmiPassword=" + jmxrmiPassword + ", nodePort=" + nodePort + ", ftpPort=" + ftpPort + ", blogUrl=" + blogUrl + ", blogUsername="
                + blogUsername + ", blogPassword=" + blogPassword + ", serverShhPort=" + serverShhPort + ", serverUser="
                + serverUser + ", serverPass=" + serverPass + ", isSecureSession=" + isSecureSession + "]";
    }

    /**
     * @return JMX port
     */
    public String getJmxPort()
    {
        return jmxrmiPort;
    }

    /**
     * @param port
     */
    public void setJmxPort(String port)
    {
        this.jmxrmiPort = port;
    }

    /**
     * @return JMX user
     */
    public String getJmxUser()
    {
        return jmxrmiUser;
    }

    /**
     * @param user
     */
    public void setJmxUser(String user)
    {
        this.jmxrmiUser = user;
    }

    /**
     * @return JMX password
     */
    public String getJmxPassword()
    {
        return jmxrmiPassword;
    }

    /**
     * @param password
     */
    public void setJmxPassword(String password)
    {
        this.jmxrmiPassword = password;
    }

    /**
     * @return JMX port
     */
    public String getNodePort()
    {
        return nodePort;
    }

    /**
     * @param port
     */
    public void setNodePort(String port)
    {
        this.nodePort = port;
    }

    /**
     * @return blog Url
     */
    public String getBlogUrl()
    {
        return blogUrl;
    }

    /**
     * @param blogUrl
     */
    public void setBlogUrl(String blogUrl)
    {
        this.blogUrl = blogUrl;
    }

    /**
     * @return blog Username
     */
    public String getBlogUsername()
    {
        return blogUsername;
    }

    /**
     * @param blogUsername
     */
    public void setBlogUsername(String blogUsername)
    {
        this.blogUsername = blogUsername;
    }

    /**
     * @return blog Password
     */
    public String getBlogPassword()
    {
        return blogPassword;
    }

    /**
     * @param blogPassword
     */
    public void setBlogPassword(String blogPassword)
    {
        this.blogPassword = blogPassword;
    }

    /**
     * @return FTP port
     */
    public String getFtpPort()
    {
        return ftpPort;
    }

    public int getSshPort()
    {
        return serverShhPort;
    }

    public String getSshHost()
    {
        return sshHost;
    }

    public void setSshHost(String sshHost)
    {
        this.sshHost = sshHost;
    }

    public String getSshLogin()
    {
        return serverUser;
    }

    public String getSshPassword()
    {
        return serverPass;
    }

    public boolean isSecureSession()
    {
        return isSecureSession;
    }

    public String getPathSharepoint()
    {
        return pathSharepoint;
    }

    public String getreplicationEndPointHost() { return  replicationEndPointHost; }
    
    /**
     * @return the layer7enabled option
     */
    public String getLayer7Disabled()
    {
        return layer7Disabled;
    }

    /**
     * @return the api Key
     */
    public String getApiKey()
    {
        return apiKey;
    }

    /**
     * @return the secret Key
     */
    public String getApiSecretKey()
    {
        return apiSecretKey;
    }

}

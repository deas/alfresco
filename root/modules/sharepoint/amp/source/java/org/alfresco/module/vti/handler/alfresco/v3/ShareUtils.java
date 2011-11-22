/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.module.vti.handler.alfresco.v3;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.alfresco.repo.SessionUser;
import org.alfresco.repo.admin.SysAdminParams;
import org.springframework.extensions.surf.util.URLEncoder;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Helper class to work with sites through REST API
 * 
 * @author PavelYur
 */
public class ShareUtils
{
    private static Log logger = LogFactory.getLog(ShareUtils.class);

    // constants
    public static final String HEADER_SET_COOKIE = "Set-Cookie";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_REFERER = "Referer";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";
    public static final String UTF_8 = "UTF-8";

    private SysAdminParams sysAdminParams;

    public ShareUtils()
    {
    }

    public void setSysAdminParams(SysAdminParams sysAdminParams)
    {
        this.sysAdminParams = sysAdminParams;
    }

    /**
     * Get share host with port
     * 
     * @return host where share application is running
     */
    public String getShareHostWithPort()
    {
        return sysAdminParams.getShareProtocol() + "://" + sysAdminParams.getShareHost() + ":" + sysAdminParams.getSharePort();
    }

    private String getAlfrescoContext()
    {
        return "/" + sysAdminParams.getAlfrescoContext();
    }

    private String getAlfrescoHostWithPort()
    {
        return sysAdminParams.getAlfrescoProtocol() + "://" + sysAdminParams.getAlfrescoHost() + ":" + sysAdminParams.getAlfrescoPort();
    }
    
    /**
     * Creates new site using REST API, http method is sent to appropriate web script
     * 
     * @param user current user
     * @param sitePreset sitePreset for new site
     * @param shortName shortName for new site
     * @param title title for new site
     * @param description description for new site
     * @param isPublic is new site public?
     * @throws HttpException
     * @throws IOException
     */
    public void createSite(SessionUser user, String sitePreset, String shortName, String title, String description, boolean isPublic) throws HttpException, IOException
    {
        HttpClient httpClient = new HttpClient();

        String createSiteBody = "{\"isPublic\":\"" + isPublic + "\",\"title\":\"" + title + "\",\"shortName\":\"" + shortName + "\"," + "\"description\":\"" + description
                + "\",\"sitePreset\":\"" + sitePreset + "\"" + (isPublic ? ",\"alfresco-createSite-instance-isPublic-checkbox\":\"on\"}" : "}");
        PostMethod createSiteMethod = createPostMethod(getAlfrescoHostWithPort() + getAlfrescoContext() + "/s/api/sites?alf_ticket=" + user.getTicket(), createSiteBody, CONTENT_TYPE_JSON);
        try
        {
            if (logger.isDebugEnabled())
                logger.debug("Trying to create site with name: " + shortName + ". URL: " + createSiteMethod.getURI());

            int createSiteStatus = httpClient.executeMethod(createSiteMethod);
            createSiteMethod.getResponseBody();

            if (logger.isDebugEnabled())
                logger.debug("Create site method returned status: " + createSiteStatus);
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
                logger.debug("Fail to create site with name: " + shortName + ". Message: " + e.getMessage());
            throw new RuntimeException(e);
        }
        finally
        {
            createSiteMethod.releaseConnection();
        }
        
        // create title sites component
        createComponent(httpClient, shortName, user, "title", "title/collaboration-title");

        // create navigation sites component
        createComponent(httpClient, shortName, user, "navigation", "navigation/collaboration-navigation");

        // create component-1-1 sites component
        createComponent(httpClient, shortName, user, "component-1-1", "dashlets/site-welcome");

        // create component-1-2 sites component
        createComponent(httpClient, shortName, user, "component-1-2", "dashlets/docsummary");

        // create component-2-1 sites component
        createComponent(httpClient, shortName, user, "component-2-1", "dashlets/site-profile");

        // create component-2-2 sites component
        createComponent(httpClient, shortName, user, "component-2-2", "dashlets/colleagues");

        // create documentLibrary folder
        createDocumentLibrary(httpClient, shortName, user);

        // create sites dashboard
        createSiteDashboard(httpClient, shortName, user, sitePreset);

        // If it's a Document WorkSpace, create Links and Data Lists
        if ("document-workspace".equalsIgnoreCase(sitePreset))
        {
           // Create the Links Component
           createLinks(httpClient, shortName, user);
           
           // Create the DataLists Component
           createDataLists(httpClient, shortName, user);
        }
        else
        {
           // Otherwise create the Calendar
           createComponent(httpClient, shortName, user, "component-2-3", "dashlets/calendar");
        }
    }
    
    /**
     * Creates site component
     * 
     * @param httpClient HTTP client
     * @param siteName name of the site
     * @param user current user
     * @param componentName name of the component
     * @param componentURL URL of the component
     * @throws UnsupportedEncodingException
     */
    private void createComponent(HttpClient httpClient, String siteName, SessionUser user, String componentName, String componentURL) throws UnsupportedEncodingException
    {
        String url = getAlfrescoHostWithPort() + getAlfrescoContext() + "/s/remoteadm/create/alfresco/site-data/components/page." + componentName + ".site~" + siteName
                + "~dashboard.xml?s=sitestore&alf_ticket=" + user.getTicket();
        
        String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<component>\n" +
                            "<guid>page." + componentName + ".site~" + siteName + "~dashboard</guid>\n" +
                            "<scope>page</scope>\n" +
                            "<region-id>" + componentName + "</region-id>\n" +
                            "<source-id>site/" + siteName + "/dashboard</source-id>\n" +
                            "<url>/components/" + componentURL + "</url>\n" +
                        "</component>";
        
        PostMethod postMethod = createPostMethod(url, body, "application/octet-stream");
        try
        {
            if (logger.isDebugEnabled())
                logger.debug("Trying to create site component with name: " + componentName + ". URL: " + postMethod.getURI());

            int status = httpClient.executeMethod(postMethod);
            postMethod.getResponseBody();

            if (logger.isDebugEnabled())
                logger.debug("Create component with name: " + componentName + ". Method returned status: " + status);
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
                logger.debug("Fail to create site component with name: " + componentName + ". Message: " + e.getMessage());
            throw new RuntimeException(e);
        }
        finally
        {
            postMethod.releaseConnection();
        }
    }
    
    /**
     * Creates site dashboard
     * 
     * @param httpClient HTTP client
     * @param siteName name of the site
     * @param user current user
     * @throws UnsupportedEncodingException
     */
    public void createSiteDashboard(HttpClient httpClient, String siteName, SessionUser user, String sitePreset) throws UnsupportedEncodingException
    {
        String createSiteDashboardBody = "";
        if ("document-workspace".equalsIgnoreCase(sitePreset))
        {
            createSiteDashboardBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<page>\n" +
                "<title>Document Workspace Dashboard</title>\n" +
                "<title-id>page.workspace.title</title-id>\n" +
                "<description>Document Workspace dashboard page</description>\n" +
                "<description-id>page.workspace.description</description-id>\n" +
                "<template-instance>dashboard-2-columns-wide-left</template-instance>\n" +
                "<authentication>user</authentication>\n" + 
                "<properties>\n" +
                    "<sitePages>[ {\"pageId\":\"documentlibrary\"}, {\"pageId\":\"links\"}," +
                    " {\"pageId\":\"data-lists\"} ]</sitePages>\n" +
                "</properties>\n" +
            "</page>";
        }
        else
        {
            createSiteDashboardBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<page>\n" +
                "<title>Meeting Workspace Dashboard</title>\n" +
                "<title-id>page.meeting_workspace.title</title-id>\n" +
                "<description>Meeting Workspace dashboard page</description>\n" +
                "<description-id>page.meeting_workspace.description</description-id>\n" +
                "<template-instance>dashboard-2-columns-wide-left</template-instance>\n" +
                "<authentication>user</authentication>\n" + 
                "<properties>\n" +
                    "<sitePages>[ " +
                       "{\"pageId\":\"documentlibrary\"}, {\"pageId\":\"calendar\"} " +
                    "]</sitePages>\n" +
                "</properties>\n" +
            "</page>";
        }

        PostMethod createSiteDashboardMethod = createPostMethod(
              getAlfrescoHostWithPort() + getAlfrescoContext() + "/s/remoteadm/create/alfresco/site-data/pages/site/" + siteName
                + "/dashboard.xml?s=sitestore&alf_ticket=" + user.getTicket(), createSiteDashboardBody, "application/octet-stream");
        try
        {
            if (logger.isDebugEnabled())
                logger.debug("Trying to create site dashboard. URL: " + createSiteDashboardMethod.getURI());

            int status = httpClient.executeMethod(createSiteDashboardMethod);
            createSiteDashboardMethod.getResponseBody();

            if (logger.isDebugEnabled())
                logger.debug("Create site dashboard method returned status: " + status);
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
                logger.debug("Fail to create site dashboard. Message: " + e.getMessage());
            throw new RuntimeException(e);
        }
        finally
        {
            createSiteDashboardMethod.releaseConnection();
        }
    }
    
    /**
     * Creates documentLibrary folder in site
     * 
     * @param httpClient HTTP client
     * @param siteName short name of site
     * @param user current user
     */
    private void createDocumentLibrary(HttpClient httpClient, String siteName, SessionUser user)
    {
        GetMethod createDocumentLibraryFolderMethod = createGetMethod(getAlfrescoHostWithPort() + getAlfrescoContext() + "/s/slingshot/doclib/doclist/documents/site/" + siteName
                + "/documentLibrary?filter=recentlyModified&max=10&alf_ticket=" + user.getTicket());
        try
        {
            if (logger.isDebugEnabled())
                logger.debug("Trying to create sites documentLibrary folder. URL: " + createDocumentLibraryFolderMethod.getURI());
            
            int status = httpClient.executeMethod(createDocumentLibraryFolderMethod);
            createDocumentLibraryFolderMethod.getResponseBody();
            
            if (logger.isDebugEnabled())
                logger.debug("Create sites documentLibrary folder method returned status: " + status);
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
                logger.debug("Fail to create sites documentLibrary folder. Message: " + e.getMessage());
            throw new RuntimeException(e);
        }
        finally
        {
            createDocumentLibraryFolderMethod.releaseConnection();
        }
    }
    
    /**
     * Creates links folder in site
     * 
     * @param httpClient HTTP client
     * @param siteName short name of site
     * @param user current user
     */
    private void createLinks(HttpClient httpClient, String siteName, SessionUser user)
    {
        GetMethod createLinksFolderMethod = createGetMethod(
              getAlfrescoHostWithPort() + getAlfrescoContext() + 
              "/s/api/links/site/" + siteName + "/links?page=1&pageSize=1&alf_ticket="
              + user.getTicket());
        try
        {
            if (logger.isDebugEnabled())
                logger.debug("Trying to create site links folder. URL: " + createLinksFolderMethod.getURI());
            
            int status = httpClient.executeMethod(createLinksFolderMethod);
            createLinksFolderMethod.getResponseBody();
            
            if (logger.isDebugEnabled())
                logger.debug("Create site links folder method returned status: " + status);
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
                logger.debug("Fail to create site links folder. Message: " + e.getMessage());
            throw new RuntimeException(e);
        }
        finally
        {
            createLinksFolderMethod.releaseConnection();
        }
    }
    
    /**
     * Creates the Data Lists folder in site
     * 
     * @param httpClient HTTP client
     * @param siteName short name of site
     * @param user current user
     */
    private void createDataLists(HttpClient httpClient, String siteName, SessionUser user)
    {
        GetMethod createDataListsFolderMethod = createGetMethod(
              getAlfrescoHostWithPort() + getAlfrescoContext() + 
              "/s/slingshot/datalists/lists/site/" + siteName + "/dataLists?page=1&pageSize=1&alf_ticket="
              + user.getTicket());
        try
        {
            if (logger.isDebugEnabled())
                logger.debug("Trying to create site Data Lists folder. URL: " + createDataListsFolderMethod.getURI());
            
            int status = httpClient.executeMethod(createDataListsFolderMethod);
            createDataListsFolderMethod.getResponseBody();
            
            if (logger.isDebugEnabled())
                logger.debug("Create site Data Lists folder method returned status: " + status);
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
                logger.debug("Fail to create site Data Lists folder. Message: " + e.getMessage());
            throw new RuntimeException(e);
        }
        finally
        {
            createDataListsFolderMethod.releaseConnection();
        }
    }
    
    /**
     * Creates POST method
     * 
     * @param url URL for request
     * @param body body of request
     * @param contentType content type of request
     * @return POST method
     * @throws UnsupportedEncodingException
     */
    private PostMethod createPostMethod(String url, String body, String contentType) throws UnsupportedEncodingException
    {
        PostMethod postMethod = new PostMethod(url);
        postMethod.setRequestHeader(HEADER_CONTENT_TYPE, contentType);
        postMethod.setRequestEntity(new StringRequestEntity(body, CONTENT_TYPE_TEXT_PLAIN, UTF_8));

        return postMethod;
    }
    
    /**
     * Creates GET method
     * 
     * @param url URL for request
     * @return GET method
     */
    private GetMethod createGetMethod(String url)
    {
        GetMethod getMethod = new GetMethod(url);
        return getMethod;
    }
    
    /**
     * Deletes site using REST API, http method is sent to appropriate web script
     * 
     * @param user current user
     * @param shortName shortName of site we are going to delete
     * @throws HttpException
     * @throws IOException
     */
    public void deleteSite(SessionUser user, String shortName) throws HttpException, IOException
    {
        HttpClient httpClient = new HttpClient();
        DeleteMethod deleteSiteMethod = new DeleteMethod(getAlfrescoHostWithPort() + getAlfrescoContext() + "/s/api/sites/" + shortName + "?alf_ticket=" + user.getTicket());
        try
        {
            if (logger.isDebugEnabled())
                logger.debug("Trying to delete site with name: " + shortName);

            int status = httpClient.executeMethod(deleteSiteMethod);
            deleteSiteMethod.getResponseBody();

            if (logger.isDebugEnabled())
                logger.debug("Delete site method returned status: " + status);
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
                logger.debug("Fail to delete site with name: " + shortName);
            throw new RuntimeException(e);
        }
        finally
        {
            deleteSiteMethod.releaseConnection();
        }

        // deletes site dashboard
        deleteSiteDashboard(httpClient, shortName, user);

        // deletes title component
        deleteSiteComponent(httpClient, shortName, user, "title");

        // deletes navigation component
        deleteSiteComponent(httpClient, shortName, user, "navigation");

        // deletes component-2-2 component
        deleteSiteComponent(httpClient, shortName, user, "component-2-2");

        // deletes component-1-1 component
        deleteSiteComponent(httpClient, shortName, user, "component-1-1");

        // deletes component-2-1 component
        deleteSiteComponent(httpClient, shortName, user, "component-2-1");

        // deletes component-1-2 component
        deleteSiteComponent(httpClient, shortName, user, "component-1-2");
    }
    
    /**
     * Deletes component from site
     * 
     * @param httpClient HTTP client
     * @param siteName name of the site
     * @param user current user
     * @param componentName name of the component
     */
    private void deleteSiteComponent(HttpClient httpClient, String siteName, SessionUser user, String componentName)
            {
        DeleteMethod deleteTitleMethod = new DeleteMethod(getAlfrescoHostWithPort() + getAlfrescoContext() + "/s/remoteadm/delete/alfresco/site-data/components/page." + componentName
                + ".site~" + siteName + "~dashboard.xml?s=sitestore&alf_ticket=" + user.getTicket());
        try
        {
            if (logger.isDebugEnabled())
                logger.debug("Trying to delete site component with name: " + siteName);

            int status = httpClient.executeMethod(deleteTitleMethod);
            deleteTitleMethod.getResponseBody();

            if (logger.isDebugEnabled())
                logger.debug("Delete site component method returned status: " + status);
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
                logger.debug("Fail to delete component from site with name: " + siteName);
            throw new RuntimeException(e);
        }
        finally
        {
            deleteTitleMethod.releaseConnection();
        }
    }
    
    /**
     * Deletes site dashboard
     * 
     * @param httpClient HTTP client
     * @param siteName name of the site
     * @param user current user
     */
    private void deleteSiteDashboard(HttpClient httpClient, String siteName, SessionUser user)
    {
        DeleteMethod deleteDashboardMethod = new DeleteMethod(getAlfrescoHostWithPort() + getAlfrescoContext() + "/s/remoteadm/delete/alfresco/site-data/pages/site/" + siteName
                + "/dashboard.xml?s=sitestore&alf_ticket=" + user.getTicket());
        try
        {
            if (logger.isDebugEnabled())
                logger.debug("Trying to delete dashboard from site with name: " + siteName);

            int status = httpClient.executeMethod(deleteDashboardMethod);
            deleteDashboardMethod.getResponseBody();

            if (logger.isDebugEnabled())
                logger.debug("Delete dashboard from site method returned status: " + status);
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
                logger.debug("Fail to delete dashboard from site with name: " + siteName);
            throw new RuntimeException(e);
        }
        finally
        {
            deleteDashboardMethod.releaseConnection();
        }
    }
    
    /**
     * Get share context name
     * 
     * @return String share context name
     */
    public String getShareContext()
    {
        return "/" + sysAdminParams.getShareContext();
    }
    
    /**
     * <p>
     * encode string to share specific manner (all characters with code > 127 will be encoded in %u0... format)
     * </p>
     * 
     * @param value to encode
     * @return encoded value
     * @throws UnsupportedEncodingException
     */
    public static String encode(String value) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder(value.length());

        for (int i = 0; i < value.length(); i++)
        {
            char c = value.charAt(i);
            if (c > 127)
            {
                result.append("%u0" + Integer.toHexString(c).toUpperCase());
            }
            else
            {
                if (c > 'a' && c < 'z' || c > 'A' && c < 'Z' || c == '/' || c == '@' || c == '+')
                {
                    result.append(c);
                }
                else
                {
                    result.append(URLEncoder.encode(c + ""));
                }
            }
        }
        return result.toString();
    }
}

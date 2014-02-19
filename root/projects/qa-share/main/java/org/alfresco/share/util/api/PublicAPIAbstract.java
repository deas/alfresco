/*
 * Copyright (C) 2005-2013s Alfresco Software Limited.
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

package org.alfresco.share.util.api;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.rest.api.tests.TestFixture;
import org.alfresco.rest.api.tests.client.AuthenticatedHttp;
import org.alfresco.rest.api.tests.client.AuthenticationDetailsProvider;
import org.alfresco.rest.api.tests.client.HttpClientProvider;
import org.alfresco.rest.api.tests.client.PublicApiClient;
import org.alfresco.rest.api.tests.client.PublicApiClient.Comments;
import org.alfresco.rest.api.tests.client.PublicApiClient.ExpectedPaging;
import org.alfresco.rest.api.tests.client.PublicApiClient.Favourites;
import org.alfresco.rest.api.tests.client.PublicApiClient.Nodes;
import org.alfresco.rest.api.tests.client.PublicApiClient.Paging;
import org.alfresco.rest.api.tests.client.PublicApiClient.People;
import org.alfresco.rest.api.tests.client.PublicApiClient.SiteMembershipRequests;
import org.alfresco.rest.api.tests.client.PublicApiClient.Sites;
import org.alfresco.rest.api.tests.client.PublicApiClient.Tags;
import org.alfresco.rest.api.tests.client.PublicApiHttpClient;
import org.alfresco.rest.api.tests.client.UserAuthenticationDetailsProviderImpl;
import org.alfresco.rest.api.tests.client.UserData;
import org.alfresco.rest.api.tests.client.UserDataService;
import org.alfresco.rest.workflow.api.tests.WorkflowApiClient;
import org.alfresco.rest.workflow.api.tests.WorkflowApiClient.DeploymentsClient;
import org.alfresco.rest.workflow.api.tests.WorkflowApiClient.ProcessesClient;
import org.alfresco.rest.workflow.api.tests.WorkflowApiClient.TasksClient;
import org.alfresco.rest.workflow.api.tests.WorkflowApiHttpClient;
import org.alfresco.share.util.AbstractTests;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.BeforeClass;

//import org.alfresco.repo.sync.api.SyncApiClient;

/**
 * This class initiates the api clients with host details, http client and user
 * data service.
 * 
 * @author Abhijeet Bharade
 */
public abstract class PublicAPIAbstract extends AbstractTests
{
    private PublicApiHttpClient httpClient;
    static WorkflowApiHttpClient httpClientForWorkflow;
    protected static PublicApiClient publicApiClient;
    protected static WorkflowApiClient workflowApiClient;
    // protected static SubscriptionApiClient desktopSynApiClient;
    // protected static Sync syncClient;

    private String location;
    private int port;
    protected String DOMAIN;

    protected static SiteMembershipRequests siteMembershipRequestsProxy;
    protected static TasksClient taskClient;
    protected static Favourites favouriteProxy;
    protected static Sites sitesProxy;
    protected static ProcessesClient processesClient;
    protected static People peopleClient;;
    protected static DeploymentsClient deploymentsClient;
    protected static Nodes nodesClient;
    protected static Tags tagsClient;
    protected static Comments commentsClient;

    @BeforeClass
    public void beforeClass() throws Exception
    {
        super.setup();
        HttpClientProvider httpClientProvider = (HttpClientProvider) ctx.getBean("httpClientProvider");

        UserDataService userDataService = new UserDataService()
        {
            public UserData findUserByUserName(String userName)
            {
                UserData userData = new UserData();
                userData.setUserName(getAuthDetails(userName)[0]);
                userData.setPassword(getAuthDetails(userName)[1]);
                userData.setId(getAuthDetails(userName)[0]);
                return userData;
            }
        };
        AuthenticationDetailsProvider authenticationDetailsProvider = new UserAuthenticationDetailsProviderImpl(userDataService, "admin", "admin");
        AuthenticatedHttp authenticatedHttp = new AuthenticatedHttp(httpClientProvider, authenticationDetailsProvider);
        configureUrlParams(drone);
        if (dronePropertiesMap.get(drone).getAlfrescoVersion().isCloud())
        {
            this.httpClient = new PublicApiHttpClient("https", location, port, "", "", authenticatedHttp);
            httpClientForWorkflow = new WorkflowApiHttpClient("https", location, port, "", "", authenticatedHttp);
        }
        else
        {
            this.httpClient = new PublicApiHttpClient(location, port, TestFixture.CONTEXT_PATH, TestFixture.PUBLIC_API_SERVLET_NAME, authenticatedHttp);
            httpClientForWorkflow = new WorkflowApiHttpClient(location, port, TestFixture.CONTEXT_PATH, TestFixture.PUBLIC_API_SERVLET_NAME, authenticatedHttp);
        }

        publicApiClient = new PublicApiClient(httpClient, userDataService);
        workflowApiClient = new WorkflowApiClient(httpClientForWorkflow, userDataService);
        // desktopSynApiClient = new SubscriptionApiClient(httpClient, userDataService);

        siteMembershipRequestsProxy = publicApiClient.siteMembershipRequests();
        taskClient = workflowApiClient.tasksClient();
        favouriteProxy = publicApiClient.favourites();
        sitesProxy = publicApiClient.sites();
        peopleClient = publicApiClient.people();
        nodesClient = publicApiClient.nodes();
        tagsClient = publicApiClient.tags();
        commentsClient = publicApiClient.comments();
        processesClient = workflowApiClient.processesClient();
        deploymentsClient = workflowApiClient.deploymentsClient();
        // syncClient = desktopSynApiClient.sync();

        setTenantDomain();

    }

    protected Paging getPaging(Integer skipCount, Integer maxItems, Integer total, Integer expectedTotal)
    {
        ExpectedPaging expectedPaging = ExpectedPaging.getExpectedPaging(skipCount, maxItems, total, expectedTotal);
        return new Paging(skipCount, maxItems, expectedPaging);
    }

    protected Map<String, String> createParams(Paging paging, Map<String, String> otherParams)
    {
        Map<String, String> params = new HashMap<String, String>(2);
        if (paging != null)
        {
            if (paging.getSkipCount() != null)
            {
                params.put("skipCount", String.valueOf(paging.getSkipCount()));
            }
            if (paging.getMaxItems() != null)
            {
                params.put("maxItems", String.valueOf(paging.getMaxItems()));
            }
        }
        if (otherParams != null)
        {
            params.putAll(otherParams);
        }
        return params;
    }

    /**
     * Extracts host and port from the URL.
     * 
     * @param drone
     */
    public void configureUrlParams(WebDrone drone)
    {
        String apiUrl = getAPIURL(drone);
        if (dronePropertiesMap.get(drone).getAlfrescoVersion().isCloud())
        {
            location = StringUtils.substringBetween(apiUrl, "//", "/");
            port = dronePropertiesMap.get(drone).getHttpSecurePort();
        }
        else
        {
            location = StringUtils.substringBetween(apiUrl, "//", ":");
            String url = dronePropertiesMap.get(drone).getShareUrl();
            port = Integer.parseInt(StringUtils.substringBetween(StringUtils.substringAfter(url, ":"), ":", "/share"));
        }
    }

    public void setTenantDomain()
    {
        if (alfrescoVersion.isCloud())
        {
            DOMAIN = DOMAIN_FREE;
        }
        else
        {
            DOMAIN = "-default-";
        }
    }

}

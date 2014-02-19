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

import java.util.Map;

import org.alfresco.rest.api.tests.client.PublicApiClient.ListResponse;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.rest.api.tests.client.RequestContext;
import org.alfresco.rest.api.tests.client.data.FavouriteSite;
import org.alfresco.rest.api.tests.client.data.MemberOfSite;
import org.alfresco.rest.api.tests.client.data.Site;
import org.alfresco.rest.api.tests.client.data.SiteContainer;
import org.alfresco.rest.api.tests.client.data.SiteMember;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Client for the REST API that deals with {@link Site} (/sites) requests.
 * 
 * @author Abhijeet Bharade
 * 
 */
public class SitesAPI extends PublicAPIAbstract
{

    private static Log logger = LogFactory.getLog(SitesAPI.class);

    /**
     * Gets {@link Site} of Info for a specified site id.
     * 
     * @param authUser
     * @param domain
     * @param siteId
     * @return {@link Site}
     * @throws PublicApiException
     */
    public Site getSiteById(String authUser, String domain, String siteId) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        Site site = sitesProxy.getSite(siteId);
        logger.info("Site found received: /n" + site);
        return site;
    }

    /**
     * Gets {@link Site} of list of sites available in a specified domain filtered by params.
     * 
     * @param authUser
     * @param domain
     * @param params
     * @param siteId
     * @return ListResponse populated with {@link Site}
     * @throws PublicApiException
     */
    public ListResponse<Site> getSites(String authUser, String domain, Map<String, String> params) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        ListResponse<Site> sites = sitesProxy.getSites(params);
        logger.info("Site found received: /n" + sites.getList());
        return sites;
    }

    /**
     * Gets {@link Site} list of Site members for a particular domain filtered by params.
     * 
     * @param authUser
     * @param domain
     * @param siteId
     * @param siteId
     * @param memberId 
     * @return ListResponse populated with {@link SiteMember}
     * @throws PublicApiException
     */
    public SiteMember getSiteMemberForId(String authUser, String domain, String siteId, String memberId) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        SiteMember siteMembers = sitesProxy.getSingleSiteMember(siteId, memberId);
        logger.info("Site found received: /n" + siteMembers);
        return siteMembers;
    }

    /**
     * Gets {@link ListResponse} with {@link SiteMember} for specified site id.
     * 
     * @param authUser
     * @param domain
     * @param params
     * @param siteId
     * @param siteId
     * @return ListResponse populated with {@link SiteMember}
     * @throws PublicApiException
     */
    public ListResponse<SiteMember> getSiteMembers(String authUser, String domain, Map<String, String> params, String siteId) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        ListResponse<SiteMember> siteMembers = sitesProxy.getSiteMembers(siteId, params);
        logger.info("Site found received: /n" + siteMembers.getList());
        return siteMembers;
    }

    /**
     * Creates a {@link SiteMember} of as represented by the SiteMember object.
     * 
     * @param authUser
     * @param domain
     * @param siteId
     * @param siteMember
     * @return {@link SiteMember}
     * @throws PublicApiException
     */
    public SiteMember createSiteMember(String authUser, String domain, String siteId, SiteMember siteMember) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        SiteMember siteMemberResp = sitesProxy.createSiteMember(siteId, siteMember);
        logger.info("Site found received: /n" + siteMemberResp);
        return siteMemberResp;
    }

    /**
     * Updates a {@link SiteMember} of as represented by the SiteMember object.
     * 
     * @param authUser
     * @param domain
     * @param siteId
     * @param siteMember
     * @return {@link SiteMember}
     * @throws PublicApiException
     */
    public SiteMember updateSiteMember(String authUser, String domain, String siteId, SiteMember siteMember) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        SiteMember siteMembers = sitesProxy.updateSiteMember(siteId, siteMember);
        logger.info("Site found received: /n" + siteMembers);
        return siteMembers;
    }

    /**
     * Removes the Site membership for the person {@link SiteMember}.
     * @param authUser
     * @param domain
     * @param siteId
     * @param siteMember
     * @throws PublicApiException
     */
    public boolean removeSiteMember(String authUser, String domain, String siteId, SiteMember siteMember) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        sitesProxy.removeSiteMember(siteId, siteMember);
        return true;
    }

    /**
     * Gets {@link SiteContainer} of a site specified by site id.
     * 
     * @param authUser
     * @param domain
     * @param params
     * @param siteId
     * @return {@link ListResponse} with {@link SiteContainer}
     * @throws PublicApiException
     */
    public ListResponse<SiteContainer> getSiteContainers(String authUser, String domain, Map<String, String> params, String siteId) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        ListResponse<SiteContainer> containers = sitesProxy.getSiteContainers(siteId, params);
        logger.info("Site found received: /n" + containers.getList());
        return containers;
    }

    /**
     * Gets {@link SiteContainer} of a site specified by site id and container id.
     * 
     * @param authUser
     * @param domain
     * @param params
     * @param siteId
     * @return {@link SiteContainer}
     * @throws PublicApiException
     */
    public SiteContainer getSiteContainerForId(String authUser, String domain, String containerId, String siteId) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        SiteContainer container = sitesProxy.getSingleSiteContainer(siteId, containerId);
        logger.info("Site found received: /n" + container);
        return container;
    }

    /**
     * Gets {@link ListResponse} with {@link MemberOfSite} for specified person id.
     * 
     * @param authUser
     * @param domain
     * @param params
     * @param personId
     * @return {@link ListResponse} with {@link MemberOfSite}
     * @throws PublicApiException
     */
    public ListResponse<MemberOfSite> getPersonSites(String authUser, String domain, Map<String, String> params, String personId) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        ListResponse<MemberOfSite> response = sitesProxy.getPersonSites(personId, params);
        logger.info("Site found received: /n" + response.getList());
        return response;
    }

    /**
     * Gets {@link MemberOfSite} for specified by site id and person id.
     * 
     * @param authUser
     * @param domain
     * @param personId
     * @param siteId
     * @return {@link MemberOfSite}
     * @throws PublicApiException
     */
    public MemberOfSite getPersonSite(String authUser, String domain, String personId, String siteId) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        MemberOfSite response = sitesProxy.getPersonSite(personId, siteId);
        logger.info("Site found received: /n" + response);
        return response;
    }

    /**
     * Gets {@link ListResponse} with {@link FavouriteSite} for specified person
     * id.
     * 
     * @param authUser
     * @param domain
     * @param params
     * @param personId
     * @return {@link ListResponse} with {@link FavouriteSite}
     * @throws PublicApiException
     */
    public ListResponse<FavouriteSite> getFavouriteSites(String authUser, String domain, Map<String, String> params, String personId) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        ListResponse<FavouriteSite> response = sitesProxy.getFavouriteSites(personId, params);
        logger.info("Site found received: /n" + response.getList());
        return response;
    }

    /**
     * Gets {@link FavouriteSite} for specified by site id and person id.
     * 
     * @param authUser
     * @param domain
     * @param personId
     * @param siteId
     * @return {@link FavouriteSite}
     * @throws PublicApiException
     */
    public FavouriteSite getFavouriteSite(String authUser, String domain, String personId, String siteId) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        FavouriteSite response = sitesProxy.getSingleFavouriteSite(personId, siteId);
        logger.info("Site found received: /n" + response);
        return response;
    }

    /**
     * Creates a {@link FavouriteSite} for the person id.
     * 
     * @param authUser
     * @param domain
     * @param personId
     * @param site
     * @return {@link FavouriteSite}
     * @throws PublicApiException
     */
    public FavouriteSite createFavouriteSite(String authUser, String domain, String personId, FavouriteSite site) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        FavouriteSite response = sitesProxy.createFavouriteSite(personId, site);
        logger.info("Site found received: /n" + response);
        return response;
    }

}

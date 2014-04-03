/*
 * Copyright (C) 2005-2013s Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.api;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.HashMap;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.dashlet.SiteMembersDashlet;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.rest.api.tests.client.HttpResponse;
import org.alfresco.rest.api.tests.client.PublicApiClient.ListResponse;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.rest.api.tests.client.RequestContext;
import org.alfresco.rest.api.tests.client.data.FavouriteSite;
import org.alfresco.rest.api.tests.client.data.MemberOfSite;
import org.alfresco.rest.api.tests.client.data.Site;
import org.alfresco.rest.api.tests.client.data.SiteContainer;
import org.alfresco.rest.api.tests.client.data.SiteMember;
import org.alfresco.rest.api.tests.client.data.SiteRole;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.share.util.api.SitesAPI;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Class to include: Tests for Sites (/site, /site/siteId) and Site Members (/members) apis implemented in alfresco-remote-api.
 * 
 * @author Abhijeet Bharade
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "AlfrescoOne", "Enterprise42", "Cloud2" })
public class SitesAPITests extends SitesAPI
{

    private static final String MAX_ITEMS = "maxItems";
    private static final String SKIP_COUNT = "skipCount";
    private String testUser;
    private String testUserInvalid;
    private String user1;
    private String user2;
    private String user3;
    private String siteName;
    private String siteName1;
    private String siteNameInvalid;
    private String siteName2;
    private String siteName3;
    private String folderName;
    private AlfrescoVersion version;
    private String testUserAnotherDomain;
    private static Log logger = LogFactory.getLog(SitesAPITests.class);

    // int totalSites = 0;

    @Override
    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception
    {
        super.beforeClass();

        testName = this.getClass().getSimpleName() + System.currentTimeMillis();

        testUser = getUserNameFreeDomain(testName);
        testUserInvalid = "invalid" + testUser;

        user1 = getUserNameFreeDomain(testName + "_1");
        user2 = getUserNameFreeDomain(testName + "_2");
        user3 = getUserNameFreeDomain(testName + "_3");
        testUserAnotherDomain = getUserNameForDomain(testName, "domain1.test");

        logger.info("Starting Tests: " + testName);
        siteName = getSiteName(testName);
        siteName1 = getSiteName(testName + "1");
        siteName2 = getSiteName(testName + "2");
        siteName3 = getSiteName(testName + "3");
        siteNameInvalid = "invalid" + siteName;
        folderName = getFolderName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user2);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user3);

        ShareUser.login(drone, testUser);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC, true);
        ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC, true);
        ShareUser.createSite(drone, siteName2, SITE_VISIBILITY_PUBLIC, true);
        ShareUser.createSite(drone, siteName3, SITE_VISIBILITY_PUBLIC, true);

        ShareUser.logout(drone);

        version = drone.getProperties().getVersion();
        if (version.isCloud())
        {
            ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUserAnotherDomain, siteName, UserRole.COLLABORATOR);
        }
    }

    @Test
    public void ALF_147801() throws Exception
    {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(SKIP_COUNT, "0");
        params.put(MAX_ITEMS, "6");
        ListResponse<Site> response = getSites(testUser, DOMAIN, params);
        assertNotNull(response);
        assertTrue(response.getPaging().getMaxItems() == 6);
        
        for (Site respSite : response.getList())
        {
            if (StringUtils.isEmpty(respSite.getSiteId()))
            {
                fail("ALF_147801: Site name - " + respSite.getSiteId() + " should not be empty");
                break;
            }
        }

        try
        {
            response = getSites(testUserInvalid, DOMAIN, params);
            Assert.fail(String.format("ALF_147801: , %s, Expected Result: %s", "Test should fail as invalid username - " + testUserInvalid, 401));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401, e.getHttpResponse().toString());
        }

        // Status: 200 for invited network
        if (version.isCloud())
        {
            response = getSites(testUserAnotherDomain, DOMAIN, params);
            assertNotNull(response);
            assertTrue(response.getList().size() == 6);
            Site site = response.getList().get(0);
            assertTrue(site.getSiteId().contains(siteName), "Site - " + site + " should contain name - " + siteName);
        }
    }

    @Test
    public void ALF_147901() throws Exception
    {
        Site response = getSiteById(testUser, DOMAIN, siteName);
        assertNotNull(response);
        assertEquals(response.getSiteId(), ShareUser.getSiteShortname(siteName));

        try
        {
            response = getSiteById(testUserInvalid, DOMAIN, siteName);
            Assert.fail(String.format("ALF_147901: , %s, Expected Result: %s", "Test should fail as invalid username - " + testUserInvalid, 401));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401, e.getHttpResponse().toString());
        }

        try
        {
            response = getSiteById(testUser, DOMAIN, siteNameInvalid);
            Assert.fail(String.format("ALF_147901: , %s, Expected Result: %s", "Test should fail as invalid site name - " + siteNameInvalid, 404));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }

        // Status: 200 for invited network
        if (version.isCloud())
        {
            response = getSiteById(testUserAnotherDomain, DOMAIN, siteName);
            assertNotNull(response);
            assertEquals(response.getSiteId(), ShareUser.getSiteShortname(siteName));
        }
    }

    @Test
    public void ALF_221001() throws Exception
    {

        HashMap<String, String> params;
        params = new HashMap<String, String>();
        params.put(SKIP_COUNT, "2");
        params.put(MAX_ITEMS, "2147483");
        ListResponse<Site> response = getSites(testUser, DOMAIN, params);

        assertNotNull(response);
        assertFalse(response.getPaging().getHasMoreItems());
        assertEquals(response.getPaging().getMaxItems(), new Integer(2147483));

        params.put(SKIP_COUNT, "0");
        params.put(MAX_ITEMS,"1");
        response = getSites(testUser, DOMAIN, params);

        assertNotNull(response);
        assertTrue(response.getPaging().getHasMoreItems());

    }

    @Test
    public void ALF_219801() throws Exception
    {
        ListResponse<Site> response;

        HashMap<String, String> params;
        try
        {
            params = new HashMap<String, String>();
            params.put(MAX_ITEMS, "a");
            response = getSites(testUser, DOMAIN, params);
            Assert.fail(String.format("ALF_219801: , %s, Expected Result: %s", "invalid params - " + params, 405));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);

        }

        try
        {
            params = new HashMap<String, String>();
            params.put(SKIP_COUNT, "a");
            response = getSites(testUser, DOMAIN, params);
            Assert.fail(String.format("ALF_219801: , %s, Expected Result: %s", "invalid params - " + params, 400));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);

        }

        try
        {
            params = new HashMap<String, String>();
            params.put(MAX_ITEMS, "-1");
            response = getSites(testUser, DOMAIN, params);
            Assert.fail(String.format("ALF_219801: , %s, Expected Result: %s", "invalid params - " + params, 400));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            params = new HashMap<String, String>();
            params.put(MAX_ITEMS, "0");
            response = getSites(testUser, DOMAIN, params);
            Assert.fail(String.format("ALF_219801: , %s, Expected Result: %s", "invalid params - " + params, 400));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            params = new HashMap<String, String>();
            params.put(SKIP_COUNT, "-1");
            response = getSites(testUser, DOMAIN, params);
            Assert.fail(String.format("ALF_219801: , %s, Expected Result: %s", "invalid params - " + params, 400));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        params = new HashMap<String, String>();
        params.put(SKIP_COUNT, "2");
        params.put(MAX_ITEMS, "2147483");
        response = getSites(testUser, DOMAIN, params);

        assertNotNull(response);
        assertEquals(response.getPaging().getSkipCount(), new Integer(2));
        assertEquals(response.getPaging().getMaxItems(), new Integer(2147483));

        params = new HashMap<String, String>();
        params.put(SKIP_COUNT, "2");
        response = getSites(testUser, DOMAIN, params);

        assertNotNull(response);
        assertEquals(response.getPaging().getSkipCount(), new Integer(2));
        assertEquals(response.getPaging().getMaxItems(), new Integer(100));


        params = new HashMap<String, String>();
        params.put(MAX_ITEMS, "3");
        response = getSites(testUser, DOMAIN, params);

        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(3));
        assertEquals(response.getPaging().getSkipCount(), new Integer(0));

        params = new HashMap<String, String>();
        params.put(SKIP_COUNT, "0");
        params.put(MAX_ITEMS, "3");
        response = getSites(testUser, DOMAIN, params);

        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(3));
        assertTrue(response.getList().size() < 4);

        response = getSites(testUser, DOMAIN, null);

        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(100));
        assertEquals(response.getPaging().getSkipCount(), new Integer(0));

        try
        {
            params = new HashMap<String, String>();
            params.put(SKIP_COUNT, "apple");
            params.put(SKIP_COUNT, "pear");
            response = getSites(testUser, DOMAIN, params);
            Assert.fail(String.format("ALF_219801: , %s, Expected Result: %s", "invalid params - " + params, 400));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            params = new HashMap<String, String>();
            params.put(SKIP_COUNT, "-3");
            params.put(SKIP_COUNT, "-5");
            response = getSites(testUser, DOMAIN, params);
            Assert.fail(String.format("ALF_219801: , %s, Expected Result: %s", "invalid params - " + params, 400));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);

        }
    }

    @Test
    public void ALF_196301() throws Exception
    {

        HashMap<String, String> params;
        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            sitesProxy.create("sites", null, null, null, null, "Unable to POST to sites");
            fail("ALF_196301: Invalid method - POST sites.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405, e.getHttpResponse().toString());
        }

        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            sitesProxy.create("sites", siteName, null, null, null, "Unable to POST to sites/siteId");
            fail("ALF_196301: Invalid method - POST sites/siteId.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            sitesProxy.update("sites", null, null, null, null, "Unable to PUT site");
            fail("ALF_196301: Invalid method - PUT sites");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        try
        {
            params = new HashMap<String, String>();
            params.put(MAX_ITEMS, "a");
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            sitesProxy.update("sites", siteName, null, null, null, "Unable to PUT site for site id");
            fail("ALF_196301: Invalid method - PUT sites/siteId.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

    }

    @Test
    public void ALF_148201() throws Exception
    {

        HashMap<String, String> params;
        params = new HashMap<String, String>();
        params.put(MAX_ITEMS, "5");
        ListResponse<SiteMember> response = getSiteMembers(testUser, DOMAIN, params, siteName);
        assertNotNull(response);
        assertNotNull(response.getList().size() > 0);

        try
        {
            getSiteMembers(testUserInvalid, DOMAIN, params, siteName);
            fail("ALF_148201 - Auth details as wrong - " + testUserInvalid + "Expected error: " + 401);
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401);
        }

        try
        {
            getSiteMembers(testUser, DOMAIN, params, siteNameInvalid);
            fail("ALF_148201 - Site details as wrong - " + siteNameInvalid + "Expected error: " + 404);
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }

    }

    @Test
    public void ALF_148301() throws Exception
    {

        ShareUser.login(drone, testUser);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, user3, siteName3, UserRole.CONSUMER);

        HashMap<String, String> params;
        params = new HashMap<String, String>();
        params.put(MAX_ITEMS, "5");

        SiteMember response = getSiteMemberForId(testUser, DOMAIN, siteName3, user3);
        assertNotNull(response);
        assertEquals(response.getMemberId().toLowerCase(), user3.toLowerCase());
        assertEquals(response.getSiteId(), siteName3);

        try
        {
            getSiteMemberForId(testUserInvalid, DOMAIN, siteName3, user3);
            fail("ALF_148301 - This block of code shouldn't be reached. As auth details as wrong. Expected error -" + 401);
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401, e.getHttpResponse().toString());
        }

        try
        {
            getSiteMemberForId(testUser, DOMAIN, siteNameInvalid, user1);
            fail("ALF_148301 - This block of code shouldn't be reached. As site id is wrong. Expected error -" + 404);
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, e.getHttpResponse().getJsonResponse().toJSONString());
        }
    }

    @Test
    public void ALF_148401() throws Exception
    {
        SiteMember response = createSiteMember(testUser, DOMAIN, siteName, new SiteMember(user2, SiteRole.SiteConsumer.toString()));
        response = getSiteMemberForId(user2, DOMAIN, siteName, user2);
        assertNotNull(response);
        assertEquals(response.getMemberId(), user2);
        assertEquals(response.getSiteId(), siteName);


        ShareUser.login(drone, testUser);
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName);
        SiteMembersDashlet dashlet = siteDashBoard.getDashlet("site-members").render();
        org.alfresco.po.share.SiteMember member = dashlet.selectMember(user2);
        Assert.assertNotNull(member);
        Assert.assertTrue(member.getRole().equals(UserRole.CONSUMER), "Member with role - " + member.getRole() + " should have role - " + UserRole.CONSUMER);

        // Status: 404
        try
        {
            createSiteMember(testUser, DOMAIN, siteNameInvalid, new SiteMember(user2, SiteRole.SiteConsumer.toString()));
            fail("ALF_148401 - This block of code shouldn't be reached. As siteid is wrong. Expected error - 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, e.getHttpResponse().getJsonResponse().toJSONString());
        }

        // Status: 404: Invalid PersonId
        try
        {
            createSiteMember(testUser, DOMAIN, siteName, new SiteMember(testUserInvalid, SiteRole.SiteConsumer.toString()));
            fail("ALF_148401 - This block of code shouldn't be reached. As person id is wrong. Expected error - 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, e.getHttpResponse().getJsonResponse().toJSONString());
        }

        // Status: 400: Invalid Role
        try
        {
            createSiteMember(testUser, DOMAIN, siteName, new SiteMember(user2, user1));
            fail("ALF_148401 - This block of code shouldn't be reached. As role is wrong. Expected error - 400");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400, e.getHttpResponse().getJsonResponse().toJSONString());
        }
    }

    @Test
    public void ALF_148501() throws Exception
    {
        SiteMember siteMember = new SiteMember(user1);
        siteMember.setRole(SiteRole.SiteConsumer.toString());
        try
        {
            createSiteMember(testUser, DOMAIN, siteName, siteMember);
        }
        catch (Exception e1)
        {
        }

        // Status: 200
        siteMember.setRole(SiteRole.SiteCollaborator.toString());
        SiteMember response = updateSiteMember(testUser, DOMAIN, siteName, siteMember);
        assertNotNull(response);
        assertNotNull(response.getRole());
        assertEquals(response.getRole(), SiteRole.SiteCollaborator.toString());


        ShareUser.login(drone, testUser);
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName);
        SiteMembersDashlet dashlet = siteDashBoard.getDashlet("site-members").render();

        org.alfresco.po.share.SiteMember profile = dashlet.selectMember(user1);
        assertNotNull(profile);
        assertEquals(profile.getRole().toString(), UserRole.COLLABORATOR.toString());

        try
        {
            siteMember.setRole("wrong role");
            updateSiteMember(testUser, DOMAIN, siteName, siteMember);
            fail("ALF_148501 - This block of code shouldn't be reached. As user role is wrong. Expected error - 400.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }
        try
        {
            updateSiteMember(testUser, DOMAIN, siteNameInvalid, new SiteMember(user3, SiteRole.SiteConsumer.toString()));
            fail("ALF_148501 - This block of code shouldn't be reached. As site id is wrong. Expected error - 404.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }

        // Step for invalid person
        try
        {
            updateSiteMember(testUser, DOMAIN, siteName, new SiteMember(testUserInvalid, SiteRole.SiteCollaborator.toString()));
            fail("ALF_148501 - This block of code shouldn't be reached. As person id is wrong. Expected error - 404.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, e.getHttpResponse().getJsonResponse().toJSONString());
        }

        // Step for valid person but not a member of site
        try
        {
            updateSiteMember(testUser, DOMAIN, siteName3, new SiteMember(user1, SiteRole.SiteCollaborator.toString()));
            fail("ALF_148501 - This block of code shouldn't be reached. As person is non site member. Expected error - 400. For site - " + siteName3
                    + " and user - " + user1 + " JIRA: https://issues.alfresco.com/jira/browse/MNT-10551");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400, e.getHttpResponse().getJsonResponse().toJSONString());
        }
    }

    @Test
    public void ALF_148801() throws Exception
    {
        MemberOfSite response = getPersonSite(testUser, DOMAIN, testUser, siteName);
        assertNotNull(response);
        assertTrue(response.getSiteId().equalsIgnoreCase(siteName), "Retrieved members of sites -" + response);
        assertEquals(response.getRole().toString(), "SiteManager", "Retrieved members of sites -" + response);

        // Status invalid person: 404
        try
        {
            getPersonSite(testUser, DOMAIN, testUserInvalid, siteName);
            fail("ALF_148801 - This block of code shouldn't be reached. As person id is wrong. Expected error - 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, e.getHttpResponse().toString());
        }

        // Status invalid auth: 401
        try
        {
            getPersonSite(testUserInvalid, DOMAIN, testUser, siteName);
            fail("ALF_148801 - This block of code shouldn't be reached. As person id is wrong. Expected error - 401");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401, e.getHttpResponse().toString());
        }

        // Status invalid site: 404
        try
        {
            getPersonSite(testUser, DOMAIN, testUser, siteNameInvalid);
            fail("ALF_148801 - This block of code shouldn't be reached. As site id is wrong. Expected error - 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, e.getHttpResponse().toString());
        }
    }

    @Test
    public void ALF_148701() throws Exception
    {
        ListResponse<MemberOfSite> response = getPersonSites(testUser, DOMAIN, null, testUser);
        assertNotNull(response);
        assertEquals(response.getList().get(0).getRole().toString(), "SiteManager", "Retrieved members of sites -" + response.getList());

        // Status invalid site: 404
        try
        {
            getPersonSites(testUser, DOMAIN, null, testUserInvalid);
            fail("ALF_148701 - This block of code shouldn't be reached. As person id is wrong. Expected error - 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, e.getHttpResponse().toString());
        }

        // Status invalid site: 401
        try
        {
            getPersonSites(testUserInvalid, DOMAIN, null, testUser);
            fail("ALF_148701 - This block of code shouldn't be reached. As person id is wrong. Expected error - 401");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401, e.getHttpResponse().toString());
        }
    }

    @Test
    public void ALF_218501() throws Exception
    {
        SiteMember siteMember = new SiteMember(user3);
        siteMember.setRole(SiteRole.SiteConsumer.toString());
        ShareUser.login(drone, testUser);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, user3, siteName, UserRole.CONSUMER);

        // Status: 204
        boolean status = removeSiteMember(testUser, DOMAIN, siteName, siteMember);
        assertTrue(status);

        ShareUser.login(drone, testUser);
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName);
        SiteMembersDashlet dashlet = siteDashBoard.getDashlet("site-members").render();
        org.alfresco.po.share.SiteMember member;
        try
        {
            member = dashlet.selectMember(user3);
            fail("ALF_218501 - This block of code shouldn't be reached. As the member is removed. Expected exception - PageOperationException.");
        }
        catch (PageOperationException e1)
        {
            assertTrue(e1.getMessage().contains("Could not find site member for name"));
        }

        // Status: 401
        try
        {
            removeSiteMember(testUserInvalid, DOMAIN, siteName, siteMember);
            fail("ALF_218501 - This block of code shouldn't be reached. As auth details as wrong. Expected error - 401");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401, e.getHttpResponse().toString());

        }

        // Status invalid site: 404
        try
        {
            removeSiteMember(testUser, DOMAIN, siteNameInvalid, siteMember);
            fail("ALF_218501 - This block of code shouldn't be reached. As site id is wrong. Expected error - 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, e.getHttpResponse().toString());
        }

        // Status invalid person: 404
        try
        {
            removeSiteMember(testUser, DOMAIN, siteName, new SiteMember(testUserInvalid, SiteRole.SiteConsumer.toString()));
            fail("ALF_218501 - This block of code shouldn't be reached. As person id is wrong. Expected error - 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, e.getHttpResponse().toString());
        }

        // Status person not a member of site: 400
        try
        {
            removeSiteMember(testUser, DOMAIN, siteName3, new SiteMember(user2));
            fail("ALF_218501 - This block of code shouldn't be reached. As person id is wrong. Expected error - 400. For site - " + siteName3 + " and user - "
                    + user2);
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400, e.getHttpResponse().toString());
        }

    }

    @Test
    public void ALF_196501() throws Exception
    {
        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            sitesProxy.create("sites", siteName, "members", user1, null, "Failed to create site member");
            fail("ALF_196501 - This block of code shouldn't be reached.POST for /public/alfresco/vesrions/1/sites/<siteId>/members/<personId> not allowed. Expected error 405");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405, e.getHttpResponse().getJsonResponse().toJSONString());
        }

        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            sitesProxy.update("sites", siteName, "members", null, null, "Failed to update site member");
            fail("ALF_196501 - This block of code shouldn't be reached. PUT for /public/alfresco/vesrions/1/sites/<siteId>/members/ not allowed. Expected error 405");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405, e.getHttpResponse().getJsonResponse().toJSONString());
        }
    }

    @Test
    public void ALF_219401() throws Exception
    {

        HashMap<String, String> params;
        try
        {
            params = new HashMap<String, String>();
            params.put(MAX_ITEMS, "a");
            getSiteMembers(testUser, DOMAIN, params, siteName);
            fail("ALF_219401 - This block of code shouldn't be reached. maxItems=a not allowed. Expected error 400");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400, e.getHttpResponse().getJsonResponse().toJSONString());
        }

        try
        {
            params = new HashMap<String, String>();
            params.put(SKIP_COUNT, "a");
            getSiteMembers(testUser, DOMAIN, params, siteName);
            fail("ALF_219401 - This block of code shouldn't be reached. skipCount  = a not allowed. Expected error 400.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400, e.getHttpResponse().getJsonResponse().toJSONString());
        }

        try
        {
            params = new HashMap<String, String>();
            params.put(MAX_ITEMS, "-1");
            getSiteMembers(testUser, DOMAIN, params, siteName);
            fail("ALF_219401 - This block of code shouldn't be reached. maxItems = -1 not allowed. Expected error 400");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400, e.getHttpResponse().getJsonResponse().toJSONString());
        }

        try
        {
            params = new HashMap<String, String>();
            params.put(SKIP_COUNT, "-3");
            getSiteMembers(testUser, DOMAIN, params, siteName);
            fail("ALF_219401 - This block of code shouldn't be reached. skipCount = -3 not allowed. Erro - 400");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400, e.getHttpResponse().getJsonResponse().toJSONString());
        }

        try
        {
            createSiteMember(testUser, DOMAIN, siteName1, new SiteMember(user1, SiteRole.SiteConsumer.toString()));
            createSiteMember(testUser, DOMAIN, siteName1, new SiteMember(user2, SiteRole.SiteConsumer.toString()));
            createSiteMember(testUser, DOMAIN, siteName1, new SiteMember(user3, SiteRole.SiteConsumer.toString()));
        }
        catch (Exception e1)
        {
        }

        params = new HashMap<String, String>();
        params.put(SKIP_COUNT, "2");
        params.put(MAX_ITEMS, "3");
        ListResponse<SiteMember> members = getSiteMembers(testUser, DOMAIN, params, siteName1);
        assertNotNull(members);
        assertTrue(members.getPaging().getSkipCount() == 2);
        assertTrue(members.getPaging().getMaxItems() == 3);

        params = new HashMap<String, String>();
        params.put(SKIP_COUNT, "2");
        members = getSiteMembers(testUser, DOMAIN, params, siteName1);
        assertNotNull(members);
        assertTrue(members.getPaging().getSkipCount() == 2);
        assertTrue(members.getPaging().getMaxItems() == 100);

        params = new HashMap<String, String>();
        params.put(MAX_ITEMS, "5");
        members = getSiteMembers(testUser, DOMAIN, params, siteName1);
        assertNotNull(members);
        assertTrue(members.getPaging().getSkipCount() == 0);
        assertTrue(members.getPaging().getMaxItems() == 5);

        members = getSiteMembers(testUser, DOMAIN, null, siteName1);
        assertNotNull(members);
        assertTrue(members.getPaging().getSkipCount() == 0);
        assertTrue(members.getPaging().getMaxItems() == 100);

        try
        {
            params = new HashMap<String, String>();
            params.put(SKIP_COUNT, "apple");
            params.put(MAX_ITEMS, "pear");
            getSiteMembers(testUser, DOMAIN, params, siteName1);
            fail("ALF_219401 - This block of code shouldn't be reached. Invalid params not allowed:" + params + ". Expected error - 400");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            params = new HashMap<String, String>();
            params.put(SKIP_COUNT, "-3");
            params.put(MAX_ITEMS, "-5");
            getSiteMembers(testUser, DOMAIN, params, siteName1);
            fail("ALF_219401 - This block of code shouldn't be reached. Invalid params not allowed:" + params + ". Expected error - 400");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }
    }

    @Test
    public void ALF_221201() throws Exception
    {
        try
        {
            createSiteMember(testUser, DOMAIN, siteName2, new SiteMember(user1, SiteRole.SiteConsumer.toString()));
            createSiteMember(testUser, DOMAIN, siteName2, new SiteMember(user2, SiteRole.SiteConsumer.toString()));
            createSiteMember(testUser, DOMAIN, siteName2, new SiteMember(user3, SiteRole.SiteConsumer.toString()));
        }
        catch (Exception e)
        {
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(SKIP_COUNT, "1");
        params.put(MAX_ITEMS, "1");
        ListResponse<SiteMember> members = getSiteMembers(testUser, DOMAIN, params, siteName2);
        assertNotNull(members);
        assertEquals(members.getPaging().getCount().intValue(), 1, members.getPaging().toString());
        assertTrue(members.getPaging().getHasMoreItems(), members.getPaging().toString());

        params.clear();
        params.put(SKIP_COUNT, "1");
        params.put(MAX_ITEMS, "3");
        members = getSiteMembers(testUser, DOMAIN, params, siteName2);
        assertNotNull(members);
        assertEquals(members.getPaging().getCount().intValue(), 3);
        assertFalse(members.getPaging().getHasMoreItems());
    }

    @Test
    public void ALF_148101() throws Exception
    {
        HashMap<String, String> params;
        params = new HashMap<String, String>();
        params.put(SKIP_COUNT, "0");
        params.put(MAX_ITEMS, "5");
        ListResponse<SiteContainer> response = getSiteContainers(testUser, DOMAIN, params, siteName);

        assertNotNull(response);
        assertFalse(response.getPaging().getHasMoreItems());
        assertEquals(response.getPaging().getMaxItems(), new Integer(5));

        boolean containerFound = false;
        for (SiteContainer container : response.getList())
        {
            if (container.getFolderId().equalsIgnoreCase(DOCLIB_CONTAINER))
            {
                containerFound = true;
                break;
            }
        }
        assertTrue(containerFound, "Container " + DOCLIB_CONTAINER + " should have been found in - " + response.getList().toString());

        try
        {
            response = getSiteContainers(testUserInvalid, DOMAIN, params, siteName);
            Assert.fail("ALF_148101. Test should fail as invalid username - " + testUserInvalid + ". Expected error - 401");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401, e.getHttpResponse().toString());
        }

        try
        {
            response = getSiteContainers(testUser, DOMAIN, params, siteNameInvalid);
            Assert.fail("ALF_148101. Test should fail as invalid site - " + siteNameInvalid + ". Expected error - 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, e.getHttpResponse().toString());
        }
    }

    @Test
    public void ALF_148601() throws Exception
    {
        HashMap<String, String> params;
        params = new HashMap<String, String>();
        params.put(SKIP_COUNT, "0");
        params.put(MAX_ITEMS, "5");

        SiteContainer response = getSiteContainerForId(testUser, DOMAIN, DOCLIB_CONTAINER, siteName);

        assertNotNull(response);
        assertEquals(response.getSiteId(), siteName);
        assertEquals(response.getFolderId(), DOCLIB_CONTAINER);

        try
        {
            response = getSiteContainerForId(testUser, DOMAIN, DOCLIB_CONTAINER, siteNameInvalid);
            Assert.fail("ALF_148101. Test should fail as invalid site name - " + siteNameInvalid + ". Expected error - 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, e.getHttpResponse().toString());
        }

        try
        {
            response = getSiteContainerForId(testUser, DOMAIN, "documentLibrary1", siteName);
            Assert.fail("ALF_148101. Test should fail as invalid folder id - " + "documentLibrary1. Expected error 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, e.getHttpResponse().toString());
        }

        try
        {
            response = getSiteContainerForId(testUserInvalid, DOMAIN, DOCLIB_CONTAINER, siteName);
            Assert.fail("ALF_148101. Test should fail as invalid auth - " + testUserInvalid + ". Expected error 401");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401, e.getHttpResponse().toString());
        }
    }

    @Test
    public void ALF_218601() throws Exception
    {
        HashMap<String, String> params;
        params = new HashMap<String, String>();
        params.put("relation", "containers");
        publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
        HttpResponse response = sitesProxy.getAll("sites", siteName, null, null, params, "Failed to get site containers");
        assertNotNull(response);
        assertNotNull(response.getJsonResponse().get("entry"));

        try
        {
            getSiteContainers(testUser, DOMAIN, params, siteNameInvalid);
            Assert.fail("ALF_218601. Test should fail as invalid site id- " + siteNameInvalid + ". Expected error 404");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, e.getHttpResponse().toString());
        }
    }

    @Test
    public void ALF_196401() throws Exception
    {
        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            sitesProxy.create("sites", siteName, "containers", null, null, "Unable to POST to sites");
            fail("ALF_196401: Invalid method - POST /sites/siteId/containers. Expected error 405.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405, e.getHttpResponse().toString());
        }

        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            sitesProxy.create("sites", siteName, "containers", DOCLIB_CONTAINER, null, "Unable to POST to sites");
            fail("ALF_196401: Invalid method - POST container/containerId. Expected error 405.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405, e.getHttpResponse().toString());
        }

        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            sitesProxy.update("sites", siteName, "containers", null, null, "Unable to PUT to sites");
            fail("ALF_196401: Invalid method - PUT.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405, e.getHttpResponse().toString());
        }

        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            sitesProxy.update("sites", siteName, "containers", DOCLIB_CONTAINER, null, "Unable to PUT to sites");
            fail("ALF_196401: Invalid method - PUT container/containerId");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405, e.getHttpResponse().toString());
        }

        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            sitesProxy.remove("sites", siteName, "containers", null, "Unable to DELETE to sites");
            fail("ALF_196401: Invalid method - DELETE.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405, e.getHttpResponse().toString());
        }

        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            sitesProxy.remove("sites", siteName, "containers", DOCLIB_CONTAINER, "Unable to DELETE to sites container");
            fail("ALF_196401: Invalid method - DELETE container/containerId. Expected error 405.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405, e.getHttpResponse().toString());
        }
    }

    @Test
    public void ALF_219901() throws Exception
    {
        ListResponse<SiteContainer> response;

        HashMap<String, String> params;
        try
        {
            params = new HashMap<String, String>();
            params.put(SKIP_COUNT, "a");
            response = getSiteContainers(testUser, DOMAIN, params, testUser);

            Assert.fail("ALF_219901: Invalid params - " + params + ". Expected error 400.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            params = new HashMap<String, String>();
            params.put(MAX_ITEMS, "s");
            response = getSiteContainers(testUser, DOMAIN, params, testUser);
            Assert.fail("ALF_219901: Invalid params - " + params + ". Expected error - 400");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            params = new HashMap<String, String>();
            params.put(MAX_ITEMS, "-1");
            response = getSiteContainers(testUser, DOMAIN, params, testUser);
            Assert.fail("ALF_219901: Invalid params - " + params + ". Expected error - 400");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            params = new HashMap<String, String>();
            params.put(SKIP_COUNT, "-3");
            response = getSiteContainers(testUser, DOMAIN, params, testUser);
            Assert.fail("ALF_219901: Invalid params - " + params + ". Expected error - 400");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        params = new HashMap<String, String>();
        params.put(SKIP_COUNT, "2");
        params.put(MAX_ITEMS, "2147483647");
        response = getSiteContainers(testUser, DOMAIN, params, siteName);

        assertNotNull(response);
        assertEquals(response.getPaging().getSkipCount(), new Integer(2));
        assertEquals(response.getPaging().getMaxItems(), new Integer(2147483647));

        params = new HashMap<String, String>();
        params.put(SKIP_COUNT, "2");
        response = getSiteContainers(testUser, DOMAIN, params, siteName);

        assertNotNull(response);
        assertEquals(response.getPaging().getSkipCount(), new Integer(2));
        assertEquals(response.getPaging().getMaxItems(), new Integer(100));

        params = new HashMap<String, String>();
        params.put(MAX_ITEMS, "4");
        response = getSiteContainers(testUser, DOMAIN, params, siteName);

        assertNotNull(response);
        assertEquals(response.getPaging().getSkipCount(), new Integer(0));
        assertEquals(response.getPaging().getMaxItems(), new Integer(4));

        response = getSiteContainers(testUser, DOMAIN, null, siteName);
        assertNotNull(response);
        assertEquals(response.getPaging().getSkipCount(), new Integer(0));
        assertEquals(response.getPaging().getMaxItems(), new Integer(100));

        try
        {
            params = new HashMap<String, String>();
            params.put(SKIP_COUNT, "apple");
            params.put(MAX_ITEMS, "google");
            response = getSiteContainers(testUser, DOMAIN, params, siteName);
            Assert.fail("ALF_219901: Invalid params - " + params + ". Expected error - 400");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            params = new HashMap<String, String>();
            params.put(SKIP_COUNT, "-1");
            params.put(MAX_ITEMS, "-2");
            response = getSiteContainers(testUser, DOMAIN, params, siteName);
            Assert.fail("ALF_219901: Invalid params - " + params + ". Expected error - 400");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }
    }

    @Test
    public void ALF_221101() throws Exception
    {

        HashMap<String, String> params;
        params = new HashMap<String, String>();
        params.put(SKIP_COUNT, "2");
        params.put(MAX_ITEMS, "2147483");
        ListResponse<SiteContainer> response = getSiteContainers(testUser, DOMAIN, params, siteName);

        assertNotNull(response);
        assertFalse(response.getPaging().getHasMoreItems());
        assertEquals(response.getPaging().getMaxItems(), new Integer(2147483));
        assertEquals(response.getPaging().getSkipCount(), new Integer(2));

        // params.clear();
        // params.put(SKIP_COUNT, "0");
        // params.put("maxItems", "1");
        // response = getSiteContainers(testUser, DOMAIN, params, siteName);
        //
        // assertNotNull(response);
        // assertTrue(response.getPaging().getHasMoreItems());
        // assertEquals(response.getPaging().getMaxItems(), new Integer(3));
        // assertEquals(response.getPaging().getSkipCount(), new Integer(0));
    }

    @Test
    public void ALF_196601() throws Exception
    {
        try
        {
            sitesProxy.create("people", testUser, "sites", null, null, "Unable to POST to sites");
            fail("ALF_196601: Invalid method - POST sites. Expected error 405.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405, e.getHttpResponse().toString());
        }

        try
        {
            sitesProxy.create("people", testUser, "sites", siteName, null, "Unable to POST to sites");
            fail("ALF_196601: Invalid method - POST sites/siteId. Expected error 405.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405, e.getHttpResponse().toString());
        }

        try
        {
            sitesProxy.update("people", testUser, "sites", null, null, "Unable to PUT to sites");
            fail("ALF_196601: Invalid method - PUT sites. Expected error 405.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405, e.getHttpResponse().toString());
        }

        try
        {
            sitesProxy.update("people", testUser, "sites", siteName, null, "Unable to PUT to sites");
            fail("ALF_196601: Invalid method - PUT sites/siteId. Expected error 405.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405, e.getHttpResponse().toString());
        }

        try
        {
            sitesProxy.remove("people", testUser, "sites", null, "Unable to DELETE to sites");
            fail("ALF_196601: Invalid method - DELETE sites. Expected error 405.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405, e.getHttpResponse().toString());
        }

    }

    @Test
    public void ALF_220001() throws Exception
    {
        ListResponse<MemberOfSite> response;

        HashMap<String, String> params;
        try
        {
            params = new HashMap<String, String>();
            params.put(SKIP_COUNT, "a");
            response = getPersonSites(testUser, DOMAIN, params, testUser);
            Assert.fail("ALF_220001: Invalid params - " + params + ". Expected error 400");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            params = new HashMap<String, String>();
            params.put(MAX_ITEMS, "s");
            response = getPersonSites(testUser, DOMAIN, params, testUser);
            Assert.fail("ALF_220001: Invalid params - " + params + ". Expected error 400");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            params = new HashMap<String, String>();
            params.put(MAX_ITEMS, "-1");
            response = getPersonSites(testUser, DOMAIN, params, testUser);
            Assert.fail("ALF_220001: Invalid params - " + params + ". Expected error 400");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            params = new HashMap<String, String>();
            params.put(SKIP_COUNT, "-3");
            response = getPersonSites(testUser, DOMAIN, params, testUser);
            Assert.fail("ALF_220001: Invalid params - " + params + ". Expected error 400");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        params = new HashMap<String, String>();
        params.put(SKIP_COUNT, "2");
        params.put(MAX_ITEMS, "5");
        response = getPersonSites(testUser, DOMAIN, params, testUser);

        assertNotNull(response);
        assertEquals(response.getPaging().getSkipCount(), new Integer(2));
        assertEquals(response.getPaging().getMaxItems(), new Integer(5));

        params = new HashMap<String, String>();
        params.put(SKIP_COUNT, "2");
        response = getPersonSites(testUser, DOMAIN, params, testUser);

        assertNotNull(response);
        assertEquals(response.getPaging().getSkipCount(), new Integer(2));
        assertEquals(response.getPaging().getMaxItems(), new Integer(100));

        params = new HashMap<String, String>();
        params.put(MAX_ITEMS, "2");
        response = getPersonSites(testUser, DOMAIN, params, testUser);

        assertNotNull(response);
        assertEquals(response.getPaging().getSkipCount(), new Integer(0));
        assertEquals(response.getPaging().getMaxItems(), new Integer(2));

        response = getPersonSites(testUser, DOMAIN, null, testUser);
        assertNotNull(response);
        assertEquals(response.getPaging().getSkipCount(), new Integer(0));
        assertEquals(response.getPaging().getMaxItems(), new Integer(100));

        try
        {
            params = new HashMap<String, String>();
            params.put(SKIP_COUNT, "apple");
            params.put(MAX_ITEMS, "google");
            response = getPersonSites(testUser, DOMAIN, params, testUser);
            Assert.fail("ALF_220001: Invalid params - " + params + ". Expected error 400");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            params = new HashMap<String, String>();
            params.put(SKIP_COUNT, "-1");
            params.put(MAX_ITEMS, "-2");
            response = getPersonSites(testUser, DOMAIN, params, testUser);
            Assert.fail("ALF_220001: Invalid params - " + params + ". Expected error 400");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }
    }

    @Test
    public void ALF_221301() throws Exception
    {

        HashMap<String, String> params;
        params = new HashMap<String, String>();
        params.put(SKIP_COUNT, "2");
        params.put(MAX_ITEMS, "2147483");
        ListResponse<MemberOfSite> response = getPersonSites(testUser, DOMAIN, params, testUser);

        assertNotNull(response);
        assertFalse(response.getPaging().getHasMoreItems());
        assertEquals(response.getPaging().getMaxItems(), new Integer(2147483));
        assertEquals(response.getPaging().getSkipCount(), new Integer(2));

        params.put(SKIP_COUNT, "0");
        params.put("maxItems", "3");
        response = getPersonSites(testUser, DOMAIN, params, testUser);

        assertNotNull(response);
        assertTrue(response.getPaging().getHasMoreItems());
        assertEquals(response.getPaging().getMaxItems(), new Integer(3));
        assertEquals(response.getPaging().getSkipCount(), new Integer(0));
    }

    @Test
    public void ALF_149001() throws Exception
    {

        ListResponse<FavouriteSite> response = getFavouriteSites(testUser, DOMAIN, null, testUser);
        assertNotNull(response);
        assertNotNull(response.getList().size() > 0);
        assertTrue(response.getList().get(0) instanceof FavouriteSite);
        boolean siteFound = false;
        for (Site respSite : response.getList())
        {
            if (respSite.getSiteId().equalsIgnoreCase(getSiteShortname(siteName)))
            {
                siteFound = true;
                break;
            }
        }
        assertTrue(siteFound, "Sites - " + response.getList() + " should contain name - " + testName);

        try
        {
            getFavouriteSites(testUser, DOMAIN, null, testUserInvalid);
            fail("ALF_149001 - This block of code shouldn't be reached. As person id wrong. Expected error 401.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }

        try
        {
            getFavouriteSites(testUserInvalid, DOMAIN, null, testUser);
            fail("ALF_149001 - This block of code shouldn't be reached. As auth details as wrong. Expected error 401.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401);
        }
    }

    @Test
    public void ALF_218901() throws Exception
    {
        FavouriteSite site = new FavouriteSite(siteName);
        FavouriteSite response = createFavouriteSite(user1, DOMAIN, user1, site);
        assertNotNull(response);
        assertEquals(response.getSiteId(), siteName);

        response = getFavouriteSite(user1, DOMAIN, user1, siteName);
        assertNotNull(response);
        assertEquals(response.getSiteId(), ShareUser.getSiteShortname(siteName));

        ShareUser.login(drone, user1);
        MySitesDashlet dashlet = ((DashBoardPage) drone.getCurrentPage()).getDashlet("my-sites").render();
        assertTrue(dashlet.isSiteFavourite(siteName), siteName + " should be favourite site for user - " + user1);

        try
        {
            createFavouriteSite(testUserInvalid, DOMAIN, testUser, site);
            fail("ALF_218901 - This block of code shouldn't be reached. As auth details as wrong. Expected error 401.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401);
        }

        // step for 404
        try
        {
            createFavouriteSite(testUser, DOMAIN, testUserInvalid, site);
            fail("ALF_218901 - This block of code shouldn't be reached. As person id is wrong. Expected error 404.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }


        // TODO: Testlink: Separate the steps for accepted strings in siteName
        // TODO: Testlink: Specify what response is expected for not accepted (404?), is it required?
        // TODO: Add missing steps by creating sites with specified characters
    }

    @Test
    public void ALF_196701() throws Exception
    {

        FavouriteSite response = getFavouriteSite(testUser, DOMAIN, testUser, siteName);
        assertNotNull(response);
        assertTrue(response instanceof FavouriteSite);
        assertEquals(response.getSiteId(), ShareUser.getSiteShortname(siteName));

        try
        {
            sitesProxy.create("people", testUser, "favorite-sites", siteName, null, "Unable to POST");
            fail("ALF_196701: Invalid method - POST favorite-sites/siteId");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405, e.getHttpResponse().toString());
        }

        try
        {
            sitesProxy.create("people", testUser, "favorite-sites", null, null, "Unable to POST");
            fail("ALF_196701: Invalid method - POST favorite-sites without site id in request body");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            sitesProxy.update("people", testUser, "favorite-sites", siteName, null, "Unable to PUT");
            fail("ALF_196701: Invalid method - PUT favourite-sites/<siteId>");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405, e.getHttpResponse().toString());
        }

        try
        {
            sitesProxy.update("people", testUser, "favorite-sites", null, null, "Unable to PUT");
            fail("ALF_196701: Invalid method - PUT favourite-sites");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405, e.getHttpResponse().toString());
        }

        try
        {
            sitesProxy.remove("people", testUser, "favorite-sites", null, "Unable to DELETE fav site.");
            fail("ALF_196701: Invalid method - DELETE favourite-sites");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405, e.getHttpResponse().toString());
        }

        HttpResponse resp = sitesProxy.remove("people", testUser, "favorite-sites", siteName2, "Unable to DELETE fav site.");
        assertNotNull(resp);
        assertEquals(resp.getStatusCode(), 204, resp.toString());
    }

    @Test
    public void ALF_220101() throws Exception
    {

        HashMap<String, String> params;
        try
        {
            params = new HashMap<String, String>();
            params.put(MAX_ITEMS, "a");
            getFavouriteSites(testUser, DOMAIN, params, testUser);
            fail("ALF_220101 - This block of code shouldn't be reached. maxItems=a not allowed. Expected error: 400.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400, e.getHttpResponse().getJsonResponse().toJSONString());
        }

        try
        {
            params = new HashMap<String, String>();
            params.put(SKIP_COUNT, "s");
            getFavouriteSites(testUser, DOMAIN, params, testUser);
            fail("ALF_220101 - This block of code shouldn't be reached. skipCount = s not allowed. Expected error: 400.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400, e.getHttpResponse().getJsonResponse().toJSONString());
        }

        try
        {
            params = new HashMap<String, String>();
            params.put(MAX_ITEMS, "-1");
            getFavouriteSites(testUser, DOMAIN, params, testUser);
            fail("ALF_220101 - This block of code shouldn't be reached. maxItems=-1 not allowed. Expected error: 400.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400, e.getHttpResponse().getJsonResponse().toJSONString());
        }

        try
        {
            params = new HashMap<String, String>();
            params.put(SKIP_COUNT, "-3");
            getFavouriteSites(testUser, DOMAIN, params, testUser);
            fail("ALF_220101 - This block of code shouldn't be reached. skipCount=-3 not allowed. Expected error: 400.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400, e.getHttpResponse().getJsonResponse().toJSONString());
        }

        params = new HashMap<String, String>();
        params.put(SKIP_COUNT, "2");
        params.put(MAX_ITEMS, "2147483647");
        ListResponse<FavouriteSite> response = getFavouriteSites(testUser, DOMAIN, params, testUser);

        assertNotNull(response);
        assertEquals(response.getPaging().getSkipCount(), new Integer(2));
        assertEquals(response.getPaging().getMaxItems(), new Integer(2147483647));

        params = new HashMap<String, String>();
        params.put(SKIP_COUNT, "102");
        response = getFavouriteSites(testUser, DOMAIN, params, testUser);

        assertNotNull(response);
        assertEquals(response.getPaging().getSkipCount(), new Integer(102));
        assertEquals(response.getPaging().getMaxItems(), new Integer(100));

        params = new HashMap<String, String>();
        params.put(MAX_ITEMS, "4");
        response = getFavouriteSites(testUser, DOMAIN, params, testUser);
        assertNotNull(response);
        assertEquals(response.getPaging().getSkipCount(), new Integer(0));
        assertEquals(response.getPaging().getMaxItems(), new Integer(4));

        response = getFavouriteSites(testUser, DOMAIN, null, testUser);
        assertNotNull(response);
        assertEquals(response.getPaging().getSkipCount(), new Integer(0));
        assertEquals(response.getPaging().getMaxItems(), new Integer(100));

        try
        {
            params = new HashMap<String, String>();
            params.put(SKIP_COUNT, "apple");
            params.put(MAX_ITEMS, "google");
            response = getFavouriteSites(testUser, DOMAIN, params, testUser);
            Assert.fail("ALF_220101: Invalid params: Alphanumeric - " + params + ". Expected error: 400.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            params = new HashMap<String, String>();
            params.put(SKIP_COUNT, "-1");
            params.put(MAX_ITEMS, "-2");
            response = getFavouriteSites(testUser, DOMAIN, params, testUser);
            Assert.fail("ALF_220101: Invalid params: Values < 0 - " + params + ". Expected error: 400.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }
    }

    @Test
    public void ALF_220401() throws Exception
    {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(MAX_ITEMS, "100");
        ListResponse<FavouriteSite> response = getFavouriteSites(testUser, DOMAIN, params, testUser);
        assertNotNull(response);
        assertEquals(response.getPaging().getSkipCount(), new Integer(0));
        assertEquals(response.getPaging().getMaxItems(), new Integer(100));
        assertFalse(response.getPaging().getHasMoreItems());

        params.clear();
        params.put(MAX_ITEMS, "1");
        response = getFavouriteSites(testUser, DOMAIN, params, testUser);
        assertNotNull(response);
        assertEquals(response.getPaging().getSkipCount(), new Integer(0));
        assertEquals(response.getPaging().getMaxItems(), new Integer(1));
        assertTrue(response.getPaging().getHasMoreItems());
    }
    
}

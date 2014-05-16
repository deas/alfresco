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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.rest.api.tests.client.PublicApiClient.ListResponse;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.rest.api.tests.client.RequestContext;
import org.alfresco.rest.api.tests.client.data.Preference;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.share.util.api.PeopleAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Class to include: Tests for People apis implemented in alfresco-remote-api.
 * 
 * @author Abhijeet Bharade
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "AlfrescoOne", "Cloud2" })
public class PeopleAPITests extends PeopleAPI
{
    private String testName;
    private String testUser;
    private String testUser2;
    private String testUserInvalid;
    private String siteName;
    private Preference testpref;
    private String fileName;
    private String fileName2;
    private String fileName3;
    private static Log logger = LogFactory.getLog(PeopleAPITests.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception
    {
        super.beforeClass();

        testName = this.getClass().getSimpleName();

        testUser = getUserNameFreeDomain(testName);
        testUser2 = getUserNameFreeDomain(testName + "_2");
        testUserInvalid = "invalid" + testUser;

        siteName = getSiteName(testName) + System.currentTimeMillis();
        fileName = getFileName(testName + "_1") + System.currentTimeMillis();
        fileName2 = getFileName(testName + "_2") + System.currentTimeMillis();
        fileName3 = getFileName(testName + "_3") + System.currentTimeMillis();

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2);

        ShareUser.login(drone, testUser);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC, true);

        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser2, siteName, UserRole.COLLABORATOR);

        ShareUser.openSiteDashboard(drone, siteName);

        DocumentLibraryPage doclibPage = ShareUser.uploadFileInFolder(drone, new String[] { fileName });
        FileDirectoryInfo fileDirInfo = doclibPage.getFileDirectoryInfo(fileName);
        fileDirInfo.selectFavourite();

        ShareUser.uploadFileInFolder(drone, new String[] { fileName2 });
        fileDirInfo = doclibPage.getFileDirectoryInfo(fileName2);
        fileDirInfo.selectFavourite();

        ShareUser.uploadFileInFolder(drone, new String[] { fileName3 });
        fileDirInfo = doclibPage.getFileDirectoryInfo(fileName3);
        fileDirInfo.selectFavourite();

        // TODO: Uncomment the block below when fixed:
        // https://issues.alfresco.com/jira/browse/ALF-20594
        /*
         * preferences = getPersonPreferences(testUser, DOMAIN, testUser, null);
         * totalCount = preferences.getPaging().getCount(); if
         * (preferences.getList().size() > 0) { testpref =
         * preferences.getList().get(0); // new Preference(
         * "org.alfresco.ext.sites.favourites.sitemsitesapitests.createdAt",
         * "2013-11-18T10:11:18.480Z"); }
         */
    }

    // TODO: Enable test when fixed:
    // https://issues.alfresco.com/jira/browse/ALF-20594
    @Test(enabled = true)
    public void ALF_149301() throws Exception
    {
        // Correct Personid: Raised issue with Steve
        Preference response = getPersonPreference(testUser, DOMAIN, testUser, testpref.getId());
        assertNotNull(response);
        assertTrue(response.getId().equalsIgnoreCase(testpref.getId()), "The id should be - " + testpref.getId());

        try
        {
            // Incorrect Auth
            getPersonPreference(testUserInvalid, DOMAIN, testUser, testpref.getId());
            Assert.fail(String.format("ALF_149301: , %s, Expected Result: %s", "getFavourites request with incorrect auth", "Error 401"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401);
        }

        try
        {
            // Incorrect Personid
            getPersonPreference(testUser, DOMAIN, testUserInvalid, testpref.getId());
            Assert.fail(String.format("ALF_149301: , %s, Expected Result: %s", "getFavourites request with incorrect personId - " + testUserInvalid, "Error 404"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }

        try
        {
            // Incorrect Pref id
            // TODO: Define invalid Pref Id at class level, its easier to maintain e.g. testUserInvalid
            getPersonPreference(testUser, DOMAIN, testUser, testpref.getId() + "323");
            Assert.fail(String.format("ALF_149301: , %s, Expected Result: %s", "getFavourites request with incorrect preference id", "Error 404"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    @Test
    public void ALF_219501() throws Exception
    {
        Map<String, String> param = new HashMap<String, String>();
        param.put("maxItems", "50");
        param.put("skipCount", "2");

        // TODO: Awaiting fix: https://issues.alfresco.com/jira/browse/ALF-20594
        ListResponse<Preference> response = getPersonPreferences(testUser, DOMAIN, testUser, param);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(50));
        assertEquals(response.getPaging().getSkipCount(), new Integer(2));
        assertFalse(response.getPaging().getHasMoreItems());

        param.clear();
        param.put("maxItems", "3");
        response = getPersonPreferences(testUser, DOMAIN, testUser, param);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(3));
        assertEquals(response.getPaging().getSkipCount(), new Integer(0));
        assertTrue(response.getPaging().getHasMoreItems());
    }

    @Test
    public void ALF_220201() throws Exception
    {
        Map<String, String> param = new HashMap<String, String>();
        try
        {
            param.put("maxItems", "a");
            getPersonPreferences(testUser, DOMAIN, testUser, param);
            Assert.fail(String.format("ALF_220201: , %s, Expected Result: %s", "getFavourites request with incorrect maxItems - " + param, "Error 400 - Awaiting fix: https://issues.alfresco.com/jira/browse/ALF-20594"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            param.clear();
            param.put("skipCount", "s");
            getPersonPreferences(testUser, DOMAIN, testUser, param);
            Assert.fail(String.format("ALF_220201: , %s, Expected Result: %s", "getFavourites request with incorrect skipCount - " + param, "Error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            param.clear();
            param.put("maxItems", "-1");
            getPersonPreferences(testUser, DOMAIN, testUser, param);
            Assert.fail(String.format("ALF_220201: , %s, Expected Result: %s", "getFavourites request with incorrect maxItems - " + param, "Error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }
        try
        {
            param.clear();
            param.put("skipCount", "-2");
            getPersonPreferences(testUser, DOMAIN, testUser, param);
            Assert.fail(String.format("ALF_220201: , %s, Expected Result: %s", "getFavourites request with incorrect skipCount - " + param, "Error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        param.clear();
        param.put("skipCount", "2");
        // TODO: Awaiting fix: https://issues.alfresco.com/jira/browse/ALF-20594
        ListResponse<Preference> response = getPersonPreferences(testUser, DOMAIN, testUser, param);

        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(100));
        assertEquals(response.getPaging().getSkipCount(), new Integer(2));

        param.clear();
        param.put("maxItems", "2");
        response = getPersonPreferences(testUser, DOMAIN, testUser, param);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(2));
        assertEquals(response.getPaging().getSkipCount(), new Integer(0));

        param.clear();
        param.put("maxItems", "2");
        param.put("skipCount", "1");
        response = getPersonPreferences(testUser, DOMAIN, testUser, param);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(2));
        assertEquals(response.getPaging().getSkipCount(), new Integer(1));

        param.clear();
        getPersonPreferences(testUser, DOMAIN, testUser, null);

        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(100));
        assertEquals(response.getPaging().getSkipCount(), new Integer(0));

        try
        {
            param.clear();
            param.put("skipCount", "a");
            param.put("maxItems", "b");
            getPersonPreferences(testUser, DOMAIN, testUser, param);
            Assert.fail(String.format("ALF_220201: , %s, Expected Result: %s", "getFavourites request with incorrect skipCount and maxItems - " + param, "Error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            param.clear();
            param.put("skipCount", "-1");
            param.put("maxItems", "-5");
            getPersonPreferences(testUser, DOMAIN, testUser, param);
            Assert.fail(String.format("ALF_220201: , %s, Expected Result: %s", "getFavourites request with incorrect skipCount and maxItems - " + param, "Error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }
    }

    // TODO: Enable test when fixed:
    // https://issues.alfresco.com/jira/browse/ALF-20594
    @Test(enabled = true)
    public void ALF_196801() throws Exception
    {
        publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));

        // POST: pref
        try
        {
            peopleClient.create("people", testUser, "preferences", null, testpref.toString(), "Failed to create person preferences");
            Assert.fail(String.format("ALF_196801: Post pref: Method not allowed - ERROR - 405"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        // POST: pref/preferenceId
        try
        {
            peopleClient.create("people", testUser, "preferences", testpref.getId(), testpref.toString(), "Failed to create person preferences");
            Assert.fail(String.format("ALF_196801: Post pref/prefId: Method not allowed. ERROR - 405"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        // PUT: pref
        try
        {
            peopleClient.update("people", testUser, "preferences", null, testpref.toString(), "Failed to update person preferences");
            Assert.fail(String.format("ALF_196801: PUT pref: Method not allowed - ERROR - 405"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        // PUT: pref/preferenceId
        try
        {
            peopleClient.update("people", testUser, "preferences", testpref.getId(), testpref.toString(), "Failed to update person preferences");
            Assert.fail(String.format("ALF_196801: Put pref/preferenceId: Method not allowed. ERROR - 405"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        // Delete: pref
        try
        {
            peopleClient.remove("people", testUser, "preferences", null, "Failed to delete person preferences");
            Assert.fail(String.format("ALF_196801: Delete pref: Method not allowed - ERROR - 405"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        // Delete: pref/preferenceId
        try
        {
            peopleClient.remove("people", testUser, "preferences", testpref.getId(), "Failed to delete person preferences");
            Assert.fail(String.format("ALF_196801: Delete pref/preferenceId: Method not allowed. ERROR - 405"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

    }

    @Test
    public void ALF_149201() throws Exception
    {
        // Status: 200
        Map<String, String> param = new HashMap<String, String>();
        param.put("skipCount", "1");
        param.put("maxItems", "2");
        
        ListResponse<Preference> response = getPersonPreferences(testUser, DOMAIN, testUser, null);
        
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(2));
        assertEquals(response.getPaging().getSkipCount(), new Integer(1));
        
        List<Preference> prefs = response.getList();
        boolean prefFound = false;

        for (Preference preference : prefs)
        {
            if (preference.getValue().equals(siteDashAdmin))
                prefFound = true;
        }
        assertTrue(prefFound, "Preference value site name - " + siteName + "should be found in - " + prefs);

        try
        {
            // Incorrect Personid - 404
            getPersonPreferences(testUser, DOMAIN, testUserInvalid, null);
            Assert.fail(String.format("ALF_149201: , %s, Expected Result: %s", "getPersonPreferences request with incorrect personId - " + testUserInvalid,
                    "Error 404"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }

        try
        {
            // Incorrect auth - 401
            getPersonPreferences(testUserInvalid, DOMAIN, testUser, null);
            Assert.fail(String.format("ALF_149201: , %s, Expected Result: %s", "getPersonPreferences request with incorrect auth - " + testUserInvalid,
                    "Error 401"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401);
        }
    }
}

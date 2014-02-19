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
import java.util.Map;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.rest.api.tests.client.HttpResponse;
import org.alfresco.rest.api.tests.client.PublicApiClient.ListResponse;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.rest.api.tests.client.RequestContext;
import org.alfresco.rest.api.tests.client.data.Favourite;
import org.alfresco.rest.api.tests.client.data.FavouritesTarget;
import org.alfresco.rest.api.tests.client.data.InvalidFavouriteTarget;
import org.alfresco.rest.api.tests.client.data.JSONAble;
import org.alfresco.rest.api.tests.client.data.Site;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.share.util.api.FavouritesAPI;
import org.alfresco.share.util.api.SitesAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Class to include: Tests for favourite rest apis implemented in
 * alfresco-remote-api.
 * 
 * @author Abhijeet Bharade
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "AlfrescoOne", "Cloud2" })
public class FavouriteAPITests extends FavouritesAPI
{
    private String testName;
    private String testUser;
    private String testUserInvalid;
    private String docGuid;
    private String siteName;
    private String fileName;
    private String folderGuid;
    private String folder2Guid;
    private String folderName;
    private String folderName2;
    private String testUser2;
    private static Log logger = LogFactory.getLog(FavouriteAPITests.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception
    {
        super.beforeClass();

        testName = this.getClass().getSimpleName();

        testUser = getUserNameFreeDomain(testName);
        testUser2 = getUserNameFreeDomain(testName + "_1");
        testUserInvalid = "invalid" + testUser;

        siteName = getSiteName(testName) + System.currentTimeMillis();
        fileName = getFileName(testName) + System.currentTimeMillis();
        folderName = getFolderName(testName) + System.currentTimeMillis();
        folderName2 = folderName + "-2";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2);

        ShareUser.login(drone, testUser);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC, true);

        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser2, siteName, UserRole.COLLABORATOR);

        ShareUser.openSiteDashboard(drone, siteName);
        ShareUser.uploadFileInFolder(drone, new String[] { fileName });

        docGuid = ShareUser.getGuid(drone, fileName);

        ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);

        folderGuid = ShareUser.getGuid(drone, folderName);

        ShareUser.createFolderInFolder(drone, folderName2, folderName2, DOCLIB);

        folder2Guid = ShareUser.getGuid(drone, folderName2);

        ShareUser.openDocumentLibrary(drone);
    }

    @Test(dependsOnMethods = "ALF_246701")
    public void ALF_245801() throws Exception
    {
        Site site = new SitesAPI().getSiteById(testUser, DOMAIN, siteName);

        ListResponse<Favourite> response = getFavouritesList(testUser2, testUser2, DOMAIN, null);
        assertNotNull(response);
        assertTrue(isGuidPresent(response, docGuid), "Looking for " + docGuid + " in -- " + response.getList().toString());
        assertTrue(isGuidPresent(response, folderGuid), "Looking for " + folderGuid + " in -- " + response.getList().toString());
        assertTrue(isGuidPresent(response, site.getGuid()), "Looking for " + site.getGuid() + " in -- " + response.getList().toString());

        // Check the Response is consistent on Share
        ShareUser.login(drone, testUser2);

        DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, siteName);

        assertTrue(docLib.getFileDirectoryInfo(folderName).isFavourite());
        assertTrue(docLib.getFileDirectoryInfo(fileName).isFavourite());

        DashBoardPage dashboard = ShareUser.openUserDashboard(drone);
        MySitesDashlet dashlet = dashboard.getDashlet("my-sites").render();
        Assert.assertTrue(dashlet.isSiteFavourite(siteName));
    }

    @Test
    public void ALF_246301() throws Exception
    {
        Map<String, String> param = new HashMap<String, String>();
        try
        {
            param.put("maxItems", "a");
            getFavourites(testUser, DOMAIN, param);
            Assert.fail(String.format("Test: , %s, Expected Result: %s", "getFavourites request with incorrect skipCount", "Error 400"));

        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }
        try
        {
            param.clear();
            param.put("skipCount", "s");
            getFavourites(testUser, DOMAIN, param);
            Assert.fail(String.format("Test: , %s, Expected Result: %s", "getFavourites request with incorrect skip Count", "Error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }
        try
        {
            param.clear();
            param.put("skipCount", "s");
            param.put("maxItems", "a");
            getFavourites(testUser, DOMAIN, param);
            Assert.fail(String.format("Test: , %s, Expected Result: %s", "getFavourites request with incorrect skip Count and max items.", "Error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
            logger.error(e.getMessage());
        }

        try
        {
            param.put("maxItems", "-2");
            getFavourites(testUser, DOMAIN, param);
            Assert.fail(String.format("Test: , %s, Expected Result: %s", "getFavourites request with incorrect maxItems", "Error 400"));

        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            param.clear();
            param.put("skipCount", "-2");
            getFavourites(testUser, DOMAIN, param);
            Assert.fail(String.format("Test: , %s, Expected Result: %s", "getFavourites request with incorrect skip Count", "Error 400"));
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
            getFavourites(testUser, DOMAIN, param);
            Assert.fail(String.format("Test: , %s, Expected Result: %s", "getFavourites request with incorrect skip Count and max items.", "Error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }
    }

    @Test
    public void ALF_246401() throws Exception
    {
        try
        {
            getFavourites(testUser, testUserInvalid, DOMAIN, null);
            Assert.fail(String.format("Test: , %s, Expected Result: %s", "getFavourites request with Non Existent UserID.", "Error 404"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    @Test(enabled = true)
    public void ALF_246701() throws Exception
    {
        Map<String, String> param = new HashMap<String, String>();

        param.put("where", "(EXISTS(target/file))");
        ListResponse<Favourite> favs = getFavouritesList(testUser2, testUser2, DOMAIN, param);
        int fileFavCount = favs.getPaging().getCount();

        param.clear();
        param.put("where", "(EXISTS(target/folder))");
        favs = getFavouritesList(testUser2, testUser2, DOMAIN, param);
        int folderFavCount = favs.getPaging().getCount();

        param.clear();
        param.put("where", "(EXISTS(target/site))");
        favs = getFavouritesList(testUser2, testUser2, DOMAIN, param);
        int siteFavCount = favs.getPaging().getCount();

        // Step - 1
        Favourite response = createFavourite(testUser2, testUser2, DOMAIN, docGuid, FavType.FILE);
        assertNotNull(response);

        // Step - 2
        param.clear();
        param.put("where", "(EXISTS(target/file))");
        favs = getFavouritesList(testUser2, testUser2, DOMAIN, param);
        assertNotNull(favs);
        assertTrue(fileFavCount < favs.getPaging().getCount(), "Initial count - " + fileFavCount + " should be lesser than later count - "
                + favs.getPaging().getCount());

        // Step - 3
        response = createFavourite(testUser2, testUser2, DOMAIN, folderGuid, FavType.FOLDER);
        assertNotNull(response);

        // Step - 4
        param.clear();
        param.put("where", "(EXISTS(target/folder))");
        favs = getFavouritesList(testUser2, testUser2, DOMAIN, param);
        assertNotNull(favs);
        assertTrue(fileFavCount < favs.getPaging().getCount(), "Initial count - " + folderFavCount + " should be lesser than later count - "
                + favs.getPaging().getCount());

        // Step - 5
        response = createFavourite(testUser2, testUser2, DOMAIN, siteName, FavType.SITE);
        assertNotNull(response);

        // Step - 6
        param.clear();
        param.put("where", "(EXISTS(target/site))");
        favs = getFavouritesList(testUser2, testUser2, DOMAIN, param);
        assertNotNull(favs);
        assertTrue(fileFavCount < favs.getPaging().getCount(), "Initial count - " + siteFavCount + " should be lesser than later count - "
                + favs.getPaging().getCount());
    }

    @Test
    public void ALF_246801() throws Exception
    {

        try
        {
            JSONAble wikiJSON = new JSONAble()
            {
                @SuppressWarnings("unchecked")
                public JSONObject toJSON()
                {
                    JSONObject json = new JSONObject();
                    json.put("guid", folderGuid);
                    return json;
                }
            };

            FavouritesTarget invalidTarget = new InvalidFavouriteTarget("wiki", wikiJSON, folder2Guid);
            createFavouriteForTarget(testUser, testUser, DOMAIN, invalidTarget);

            Assert.fail("ALF_246801 - Invalid target specified for marking as favorite - " + invalidTarget.toJSON().toString());
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        ListResponse<Favourite> favourites = getFavouritesList(testUser, testUser, DOMAIN, null);
        assertNotNull(favourites);
        assertFalse(isGuidPresent(favourites, folder2Guid), folder2Guid + " was present in - " + favourites);
    }

    @Test
    public void ALF_246901() throws Exception
    {

        try
        {
            createFavourite(testUser, "otherUser", DOMAIN, folder2Guid, FavType.FOLDER);
            Assert.fail("ALF_246801 - Invalid user - " + " - otherUser");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }

        ListResponse<Favourite> favourites = getFavouritesList(testUser, testUser, DOMAIN, null);
        assertNotNull(favourites);
        assertFalse(isGuidPresent(favourites, folder2Guid));
    }

    @Test(dependsOnMethods = "ALF_245801")
    public void ALF_247301() throws Exception
    {

        HttpResponse response = removeFavouriteForGuid(testUser2, testUser2, DOMAIN, docGuid);
        assertNotNull(response);
        assertEquals(response.getStatusCode(), 204);

        Map<String, String> params = null;

        ListResponse<Favourite> favourites = getFavouritesList(testUser2, testUser2, DOMAIN, params);
        assertNotNull(favourites);
        assertFalse(isGuidPresent(favourites, docGuid));

        response = null;
        favourites = null;

        response = removeFavouriteForGuid(testUser2, testUser2, DOMAIN, folderGuid);
        assertNotNull(response);
        assertEquals(response.getStatusCode(), 204);

        favourites = getFavouritesList(testUser2, testUser2, DOMAIN, null);
        assertNotNull(favourites);
        assertFalse(isGuidPresent(favourites, folderGuid));

        response = null;
        favourites = null;
        Site site = new SitesAPI().getSiteById(testUser, DOMAIN, siteName);

        response = removeFavouriteForGuid(testUser2, testUser2, DOMAIN, site.getGuid());
        assertNotNull(response);
        assertEquals(response.getStatusCode(), 204);

        favourites = getFavouritesList(testUser2, testUser2, DOMAIN, null);
        assertNotNull(favourites);
        assertFalse(isGuidPresent(favourites, site.getGuid()));

        // Check the Response is consistent on Share
        ShareUser.login(drone, testUser2);

        DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, siteName);

        assertFalse(docLib.getFileDirectoryInfo(folderName).isFavourite());
        assertFalse(docLib.getFileDirectoryInfo(fileName).isFavourite());

        DashBoardPage dashboard = ShareUser.openUserDashboard(drone);
        MySitesDashlet dashlet = dashboard.getDashlet("my-sites").render();
        Assert.assertFalse(dashlet.isSiteFavourite(siteName));
    }

    @Test
    public void ALF_247401() throws Exception
    {
        Site site = new SitesAPI().getSiteById(testUser, DOMAIN, siteName);
        try
        {
            removeFavouriteForGuid(testUser, "otherUser", DOMAIN, site.getGuid());

            Assert.fail("ALF_247401 - Invalid user - " + " - otherUser");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }

        ListResponse<Favourite> favourites = getFavouritesList(testUser, testUser, DOMAIN, null);
        assertNotNull(favourites);
        assertTrue(isGuidPresent(favourites, site.getGuid()));
    }

    @Test
    public void ALF_247901() throws Exception
    {
        Site site = new SitesAPI().getSiteById(testUser, DOMAIN, siteName);
        publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
        Favourite favourite;
        try
        {
            favourite = makeFolderFavourite(folderGuid);
            favouriteProxy.update("people", testUser, "favorites", null, favourite.toJSON().toString(), "Could not update.");

            Assert.fail("ALF_247901 - Invalid method PUT not allowed for favourite folder.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        try
        {
            favourite = makeFileFavourite(docGuid);
            favouriteProxy.update("people", testUser, "favorites", null, favourite.toJSON().toString(), "Could not update.");

            Assert.fail("ALF_247901 - Invalid method PUT not allowed for favourite file.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        try
        {
            site = sitesProxy.getSite(siteName);
            favourite = makeSiteFavourite(site);
            favouriteProxy.update("people", testUser, "favorites", null, favourite.toJSON().toString(), "Could not update.");

            Assert.fail("ALF_247901 - Invalid method PUT not allowed for favourite site.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

    }

    /**
     * @param Favourites
     * @param targetGuid
     */
    private boolean isGuidPresent(ListResponse<Favourite> favourites, String targetGuid)
    {
        for (Favourite favourite : favourites.getList())
        {
            if (favourite.getTargetGuid().equalsIgnoreCase(targetGuid))
            {
                return true;
            }
        }
        return false;
    }

}

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

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.rest.api.tests.client.HttpResponse;
import org.alfresco.rest.api.tests.client.PublicApiClient.ListResponse;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.rest.api.tests.client.RequestContext;
import org.alfresco.rest.api.tests.client.data.*;
import org.alfresco.share.util.RandomUtil;
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

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

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
    private String docGuidOne;
    private String docGuidA;
    private String docGuidB;
    private String docGuidC;
    private String siteName;
    private String siteNameOne;
    private String siteNameA;
    private String siteNameB;
    private String siteNameC;
    private String fileName;
    private String fileNameOne;
    private String fileNameA;
    private String fileNameB;
    private String fileNameC;
    private String folderGuidA;
    private String folderGuidB;
    private String folderGuidC;
    private String folderGuid;
    private String folderGuidOne;
    private String folder2Guid;
    private String folderName;
    private String folderNameOne;
    private String folderNameA;
    private String folderNameB;
    private String folderNameC;
    private String folderName2;
    private String testUser2;
    private String testUserA;
    private static String adminDomain1;
    private static String adminDomain2;
    private String testUserDomain1;
    private String testUserDomain2;
    private static String domain1 = RandomUtil.getRandomString(4) + ".test";
    private static String domain2 = RandomUtil.getRandomString(4) + ".test";

    private static Log logger = LogFactory.getLog(FavouriteAPITests.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception
    {
        super.beforeClass();

        testName = this.getClass().getSimpleName();

        testUser = getUserNameFreeDomain(testName + getRandomString(3));
        testUser2 = getUserNameFreeDomain(testName + "_1" + getRandomString(3));
        testUserA = getUserNameFreeDomain(testName + "_A" + getRandomString(3));
        testUserInvalid = "invalid" + testUser;

        siteName = getSiteName(testName) + System.currentTimeMillis();
        siteNameA = getSiteName(testName + "A") + System.currentTimeMillis();
        siteNameB = getSiteName(testName + "B") + System.currentTimeMillis();
        siteNameC = getSiteName(testName + "C") + System.currentTimeMillis();
        fileName = getFileName(testName) + System.currentTimeMillis();
        fileNameA = getFileName(testName + "_A") + System.currentTimeMillis();
        fileNameB = getFileName(testName + "_B") + System.currentTimeMillis();
        fileNameC = getFileName(testName + "_C") + System.currentTimeMillis();
        folderName = getFolderName(testName) + System.currentTimeMillis();
        folderNameA = getFolderName(testName + "_A") + System.currentTimeMillis();
        folderNameB = getFolderName(testName + "_B") + System.currentTimeMillis();
        folderNameC = getFolderName(testName + "_C") + System.currentTimeMillis();
        folderName2 = folderName + "-2";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserA);

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

        ShareUser.createSite(drone, siteNameA, SITE_VISIBILITY_PUBLIC, true);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUserA, siteNameA, UserRole.COLLABORATOR);

        ShareUser.openSiteDashboard(drone, siteNameA);
        ShareUser.uploadFileInFolder(drone, new String[] { fileNameA });

        docGuidA = ShareUser.getGuid(drone, fileNameA);

        ShareUser.createFolderInFolder(drone, folderNameA, folderNameA, DOCLIB);

        folderGuidA = ShareUser.getGuid(drone, folderNameA);

        ShareUser.createSite(drone, siteNameB, SITE_VISIBILITY_PUBLIC, true);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUserA, siteNameB, UserRole.COLLABORATOR);

        ShareUser.openSiteDashboard(drone, siteNameB);
        ShareUser.uploadFileInFolder(drone, new String[] { fileNameB });

        docGuidB = ShareUser.getGuid(drone, fileNameB);

        ShareUser.createFolderInFolder(drone, folderNameB, folderNameB, DOCLIB);

        folderGuidB = ShareUser.getGuid(drone, folderNameB);

        ShareUser.createSite(drone, siteNameC, SITE_VISIBILITY_PUBLIC, true);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUserA, siteNameC, UserRole.COLLABORATOR);

        ShareUser.openSiteDashboard(drone, siteNameC);
        ShareUser.uploadFileInFolder(drone, new String[] { fileNameC });

        docGuidC = ShareUser.getGuid(drone, fileNameC);

        ShareUser.createFolderInFolder(drone, folderNameC, folderNameC, DOCLIB);

        folderGuidC = ShareUser.getGuid(drone, folderNameC);

        if (isAlfrescoVersionCloud(drone))
        {
            adminDomain1 = getUserNameForDomain("admin", domain1).replace("user", "");
            adminDomain2 = getUserNameForDomain("admin", domain2).replace("user", "");
            testUserDomain1 = getUserNameForDomain("testA", domain1);
            testUserDomain2 = getUserNameForDomain("testB", domain2);

            fileNameOne = getFileName(testName + "One") + System.currentTimeMillis();
            folderNameOne = getFolderName(testName + "One") + System.currentTimeMillis();
            siteNameOne = getSiteName(testName + "One") + System.currentTimeMillis();

            adminDomain1 = adminDomain1.replace("admin", "adm");
            CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, adminDomain1);
            adminDomain2 = adminDomain2.replace("admin", "adm");
            CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, adminDomain2);

            CreateUserAPI.CreateActivateUser(drone, adminDomain1, testUserDomain1);
            CreateUserAPI.CreateActivateUser(drone, adminDomain2, testUserDomain2);

            ShareUser.login(drone, testUserDomain1, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, siteNameOne, SITE_VISIBILITY_PUBLIC, true);

            ShareUser.openSiteDashboard(drone, siteNameOne);

            ShareUserMembers.inviteUserToSiteWithRole(drone, testUserDomain1, testUserDomain2, siteNameOne, UserRole.COLLABORATOR);

            ShareUser.uploadFileInFolder(drone, new String[] { fileNameOne });

            docGuidOne = ShareUser.getGuid(drone, fileNameOne);

            ShareUser.createFolderInFolder(drone, folderNameOne, folderNameOne, DOCLIB);

            folderGuidOne = ShareUser.getGuid(drone, folderNameOne);
        }

    }

    @Test(dependsOnMethods = "AONE_14299")
    public void AONE_14290() throws Exception
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
    public void AONE_14295() throws Exception
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
    public void AONE_14296() throws Exception
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
    public void AONE_14299() throws Exception
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
    public void AONE_14300() throws Exception
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

            Assert.fail("AONE_14300 - Invalid target specified for marking as favorite - " + invalidTarget.toJSON().toString());
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
    public void AONE_14301() throws Exception
    {

        try
        {
            createFavourite(testUser, "otherUser", DOMAIN, folder2Guid, FavType.FOLDER);
            Assert.fail("AONE_14300 - Invalid user - " + " - otherUser");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }

        ListResponse<Favourite> favourites = getFavouritesList(testUser, testUser, DOMAIN, null);
        assertNotNull(favourites);
        assertFalse(isGuidPresent(favourites, folder2Guid));
    }

    @Test(dependsOnMethods = "AONE_14290")
    public void AONE_14305() throws Exception
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
    public void AONE_14306() throws Exception
    {
        Site site = new SitesAPI().getSiteById(testUser, DOMAIN, siteName);
        try
        {
            removeFavouriteForGuid(testUser, "otherUser", DOMAIN, site.getGuid());

            Assert.fail("AONE_14306 - Invalid user - " + " - otherUser");
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
    public void AONE_14309() throws Exception
    {
        Site site = new SitesAPI().getSiteById(testUser, DOMAIN, siteName);
        publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
        Favourite favourite;
        try
        {
            favourite = makeFolderFavourite(folderGuid);
            favouriteProxy.update("people", testUser, "favorites", null, favourite.toJSON().toString(), "Could not update.");

            Assert.fail("AONE_14309 - Invalid method PUT not allowed for favourite folder.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        try
        {
            favourite = makeFileFavourite(docGuid);
            favouriteProxy.update("people", testUser, "favorites", null, favourite.toJSON().toString(), "Could not update.");

            Assert.fail("AONE_14309 - Invalid method PUT not allowed for favourite file.");
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

            Assert.fail("AONE_14309 - Invalid method PUT not allowed for favourite site.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

    }

    /**
     * Test - AONE-14291:User has no favorites entries.
     * <ul>
     * <li>GET people/<personId>/favorites</li>
     * </ul>
     */
    @Test(dependsOnMethods = "AONE_14305", alwaysRun = true)
    public void AONE_14291() throws Exception
    {
        ListResponse<Favourite> favs;
        try
        {
            favs = getFavouritesList(testUser2, testUser2, DOMAIN, null);
            assertNotNull(favs);
            Assert.assertFalse(favs.getPaging().getHasMoreItems(), "hasMoreItems isn't false");
            Assert.assertTrue(favs.getPaging().getCount().equals(0), "Count isn't '0'");
            Assert.assertTrue(favs.getPaging().getSkipCount().equals(0), "skipCount isn't '0'");
            Assert.assertTrue(favs.getPaging().getMaxItems().equals(100), "maxItems isn't '100'");
            Assert.assertEquals(favs.getList().size(), 0, "List isn't '0'");

        }
        catch (PublicApiException e)
        {
            fail("Get favorites for user should return 200");
        }
    }

    /**
     * Test - AONE-14292:Filter.
     * <ul>
     * <li>GET people/<personId>/favorites</li>
     * </ul>
     */
    @Test(dependsOnMethods = "AONE_14291", alwaysRun = true)
    public void AONE_14292() throws Exception
    {
        ListResponse<Favourite> favs;

        Favourite response = createFavourite(testUser2, testUser2, DOMAIN, docGuid, FavType.FILE);
        assertNotNull(response);

        response = createFavourite(testUser2, testUser2, DOMAIN, folderGuid, FavType.FOLDER);
        assertNotNull(response);

        response = createFavourite(testUser2, testUser2, DOMAIN, siteName, FavType.SITE);
        assertNotNull(response);

        try
        {
            Map<String, String> param = new HashMap<>();

            param.put("where", "(EXISTS(target/file))");
            favs = getFavouritesList(testUser2, testUser2, DOMAIN, param);
            int fileFavCount = favs.getPaging().getCount();
            Assert.assertEquals(fileFavCount, 1, "Count isn't '1'.(FILE)");

            Assert.assertEquals(favs.getList().get(0).getType().toString(), FavType.FILE.toString(), "Target type isn't file");

            param.clear();
            param.put("where", "(EXISTS(target/folder))");
            favs = getFavouritesList(testUser2, testUser2, DOMAIN, param);
            int folderFavCount = favs.getPaging().getCount();
            Assert.assertEquals(folderFavCount, 1, "Count isn't '1'.(FOLDER)");
            Assert.assertEquals(favs.getList().get(0).getType().toString(), FavType.FOLDER.toString(), "Target type isn't folder");

            param.clear();
            param.put("where", "(EXISTS(target/site))");
            favs = getFavouritesList(testUser2, testUser2, DOMAIN, param);
            int siteFavCount = favs.getPaging().getCount();
            Assert.assertEquals(siteFavCount, 1, "Count isn't '1'.(SITE)");
            Assert.assertEquals(favs.getList().get(0).getType().toString(), FavType.SITE.toString(), "Target type isn't site");

            param.clear();
            param.put("where", "(EXISTS(target/file) OR EXISTS(target/folder))");
            favs = getFavouritesList(testUser2, testUser2, DOMAIN, param);
            int fileAndFolderFavCount = favs.getPaging().getCount();
            Assert.assertEquals(fileAndFolderFavCount, 2, "Count isn't '2'.(FILE and FOLDER)");

            Assert.assertEquals(favs.getList().get(0).getType().toString(), FavType.FILE.toString(), "Target type isn't file(file and folder filter)");
            Assert.assertEquals(favs.getList().get(1).getType().toString(), FavType.FOLDER.toString(), "Target type isn't folder(file and folder filter)");

            param.clear();
            param.put("where", "(EXISTS(target/site) OR EXISTS(target/folder))");
            favs = getFavouritesList(testUser2, testUser2, DOMAIN, param);
            int siteAndFolderFavCount = favs.getPaging().getCount();
            Assert.assertEquals(siteAndFolderFavCount, 2, "Count isn't '2'.(SITE and FOLDER)");

            Assert.assertEquals(favs.getList().get(0).getType().toString(), FavType.FOLDER.toString(), "Target type isn't folder(site and folder filter)");
            Assert.assertEquals(favs.getList().get(1).getType().toString(), FavType.SITE.toString(), "Target type isn't site(site and folder filter)");

        }
        catch (PublicApiException e)
        {
            fail("Get favorites for user should return 200");
        }
    }

    /**
     * Test - AONE-14293:Sorted.
     * <ul>
     * <li>GET people/<personId>/favorites</li>
     * </ul>
     */
    @Test(dependsOnMethods = "AONE_14292", alwaysRun = true)
    public void AONE_14293() throws Exception
    {

        Favourite response = createFavourite(testUserA, testUserA, DOMAIN, siteNameA, FavType.SITE);
        assertNotNull(response);
        response = createFavourite(testUserA, testUserA, DOMAIN, docGuidA, FavType.FILE);
        assertNotNull(response);
        response = createFavourite(testUserA, testUserA, DOMAIN, folderGuidA, FavType.FOLDER);
        assertNotNull(response);

        response = createFavourite(testUserA, testUserA, DOMAIN, siteNameB, FavType.SITE);
        assertNotNull(response);
        response = createFavourite(testUserA, testUserA, DOMAIN, docGuidB, FavType.FILE);
        assertNotNull(response);
        response = createFavourite(testUserA, testUserA, DOMAIN, folderGuidB, FavType.FOLDER);
        assertNotNull(response);

        response = createFavourite(testUserA, testUserA, DOMAIN, siteNameC, FavType.SITE);
        assertNotNull(response);
        response = createFavourite(testUserA, testUserA, DOMAIN, docGuidC, FavType.FILE);
        assertNotNull(response);
        response = createFavourite(testUserA, testUserA, DOMAIN, folderGuidC, FavType.FOLDER);
        assertNotNull(response);

        try
        {
            ListResponse<Favourite> favourites = getFavouritesList(testUserA, testUserA, DOMAIN, null);
            assertNotNull(favourites);

            int fileFavCount = favourites.getPaging().getCount();
            Assert.assertEquals(fileFavCount, 9, "Count isn't '9'.");

            Assert.assertTrue(favourites.getList().get(0).getTarget().toString().contains(fileNameC), "Target type isn't file 'itemC' " + fileNameC);
            Assert.assertTrue(favourites.getList().get(1).getTarget().toString().contains(fileNameB), "Target type isn't file 'itemB' " + fileNameB);
            Assert.assertTrue(favourites.getList().get(2).getTarget().toString().contains(fileNameA), "Target type isn't file 'itemA' " + fileNameA);

            Assert.assertTrue(favourites.getList().get(3).getTarget().toString().contains(folderNameC), "Target type isn't folder 'folderC' " + folderNameC);
            Assert.assertTrue(favourites.getList().get(4).getTarget().toString().contains(folderNameB), "Target type isn't folder 'folderB' " + folderNameB);
            Assert.assertTrue(favourites.getList().get(5).getTarget().toString().contains(folderNameA), "Target type isn't folder 'folderA' " + folderNameA);

            Assert.assertTrue(favourites.getList().get(6).getTarget().toString().contains(siteNameC), "Target type isn't site 'siteC' " + siteNameC);
            Assert.assertTrue(favourites.getList().get(7).getTarget().toString().contains(siteNameB), "Target type isn't site 'siteB' " + siteNameB);
            Assert.assertTrue(favourites.getList().get(8).getTarget().toString().contains(siteNameA), "Target type isn't site 'siteA' " + siteNameA);

        }
        catch (PublicApiException e)
        {
            fail("Get favorites for user should return 200");
        }
    }

    /**
     * Test - AONE-14294:Pagination
     * <ul>
     * <li>skipCount, maxItems. GET people/<personId>/favorites</li>
     * </ul>
     */
    @Test(dependsOnMethods = "AONE_14293", alwaysRun = true)
    public void AONE_14294() throws Exception
    {
        Map<String, String> param = new HashMap<>();
        ListResponse<Favourite> favourites;

        // scipCount and maxItems are not specified.
        try
        {
            favourites = getFavouritesList(testUserA, testUserA, DOMAIN, null);
            Assert.assertTrue(favourites.getPaging().getSkipCount().equals(0), "skipCount isn't '0'");
            Assert.assertTrue(favourites.getPaging().getMaxItems().equals(100), "maxItems isn't '100'");

        }
        catch (PublicApiException e)
        {
            fail("Get favorites for user should return 200");
        }

        // skipCount specified as integer value (e.g. 2), maxItems is not specified.
        try
        {
            param.clear();
            param.put("skipCount", "2");
            favourites = getFavouritesList(testUserA, testUserA, DOMAIN, param);
            Assert.assertTrue(favourites.getPaging().getSkipCount().equals(2), "skipCount isn't '2'");
            Assert.assertTrue(favourites.getPaging().getMaxItems().equals(100), "maxItems isn't '100'");

        }
        catch (PublicApiException e)
        {
            fail("Get favorites for user should return 200");
        }

        // skipCount is not specified, maxItems specified as integer value (e.g. 3)
        try
        {
            param.clear();
            param.put("maxItems", "3");
            favourites = getFavouritesList(testUserA, testUserA, DOMAIN, param);
            Assert.assertTrue(favourites.getPaging().getSkipCount().equals(0), "skipCount isn't '0'");
            Assert.assertTrue(favourites.getPaging().getMaxItems().equals(3), "maxItems isn't '3'");
            Assert.assertEquals(favourites.getList().size(), 3, "List isn't '3'");

        }
        catch (PublicApiException e)
        {
            fail("Get favorites for user should return 200");
        }

        // maxItems and skipCount are specified as integer value (e.g. maxItems = 10, skipCount = 2)
        try
        {
            param.clear();
            param.put("skipCount", "2");
            param.put("maxItems", "10");
            favourites = getFavouritesList(testUserA, testUserA, DOMAIN, param);
            Assert.assertTrue(favourites.getPaging().getSkipCount().equals(2), "skipCount isn't '2'");
            Assert.assertTrue(favourites.getPaging().getMaxItems().equals(10), "maxItems isn't '10'");
        }
        catch (PublicApiException e)
        {
            fail("Get favorites for user should return 200");
        }
    }

    /**
     * Test - AONE-14297:Restraint access.
     * <ul>
     * <li>GET people/<personId>/favorites</li>
     * </ul>
     */
    @Test(dependsOnMethods = "AONE_14294", alwaysRun = true)
    public void AONE_14297() throws Exception
    {
        try
        {
            // Verify that UserA can't get UserB's favorites.
            getFavourites(testUserA, testUser, DOMAIN, null);
            Assert.fail("AONE_14297 - Restraint access. UserA " + testUserA + " can get UserB's " + testUser + " favorites.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, "Status Code isn't '404'");
        }

    }

    /**
     * Test - AONE-14297:Restraint access.
     * <ul>
     * <li>GET people/<personId>/favorites</li>
     * </ul>
     */
    @Test(dependsOnMethods = "AONE_14297", alwaysRun = true)
    public void AONE_14298() throws Exception
    {
        ListResponse<Favourite> favourites;

        try
        {
            favourites = getFavouritesList(testUserA, "-me-", DOMAIN, null);
            Assert.assertTrue(favourites.getPaging().getSkipCount().equals(0), "skipCount isn't '0'");
            Assert.assertTrue(favourites.getPaging().getMaxItems().equals(100), "maxItems isn't '100'");

            int fileFavCount = favourites.getPaging().getCount();
            Assert.assertEquals(fileFavCount, 9, "Count isn't '9'.");

            Assert.assertTrue(favourites.getList().get(0).getTarget().toString().contains(fileNameC), "Target type isn't file 'itemC' " + fileNameC);
            Assert.assertTrue(favourites.getList().get(1).getTarget().toString().contains(fileNameB), "Target type isn't file 'itemB' " + fileNameB);
            Assert.assertTrue(favourites.getList().get(2).getTarget().toString().contains(fileNameA), "Target type isn't file 'itemA' " + fileNameA);

            Assert.assertTrue(favourites.getList().get(3).getTarget().toString().contains(folderNameC), "Target type isn't folder 'folderC' " + folderNameC);
            Assert.assertTrue(favourites.getList().get(4).getTarget().toString().contains(folderNameB), "Target type isn't folder 'folderB' " + folderNameB);
            Assert.assertTrue(favourites.getList().get(5).getTarget().toString().contains(folderNameA), "Target type isn't folder 'folderA' " + folderNameA);

            Assert.assertTrue(favourites.getList().get(6).getTarget().toString().contains(siteNameC), "Target type isn't site 'siteC' " + siteNameC);
            Assert.assertTrue(favourites.getList().get(7).getTarget().toString().contains(siteNameB), "Target type isn't site 'siteB' " + siteNameB);
            Assert.assertTrue(favourites.getList().get(8).getTarget().toString().contains(siteNameA), "Target type isn't site 'siteA' " + siteNameA);

        }
        catch (PublicApiException e)
        {
            fail("Get favorites for user should return 200");
        }
    }

    /**
     * Test - AONE-14302:Incorrect type of object
     * <ul>
     * <li>POST people/<personId>/favorites</li>
     * </ul>
     */
    @Test(dependsOnMethods = "AONE_14298", alwaysRun = true)
    public void AONE_14302() throws Exception
    {

        HttpResponse response = removeFavouriteForGuid(testUserA, testUserA, DOMAIN, docGuidC);
        assertNotNull(response);
        assertEquals(response.getStatusCode(), 204);

        // User tries to make favorite created document,
        // but in the POST body user indicates incorrect type (for example, 'folder' instead of 'file')
        try
        {
            createFavourite(testUserA, testUserA, DOMAIN, docGuidC, FavType.FOLDER);
            Assert.fail("AONE_14302 - Incorrect type of object.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, "Status Code isn't '404'");
        }

    }

    /**
     * Test - AONE-14303:Restraint access
     * <ul>
     * <li>POST people/<personId>/favorites</li>
     * </ul>
     */
    @Test(dependsOnMethods = "AONE_14302", alwaysRun = true)
    public void AONE_14303() throws Exception
    {

        // Insufficient permission to designate this as a favorite.
        try
        {
            createFavourite(testUser2, testUser, DOMAIN, docGuidC, FavType.FILE);
            Assert.fail("AONE_14303 - Failed Restraint access");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, "Status Code isn't '404'");
        }

    }

    /**
     * Test - AONE-14304:Make entity favorite twice
     * <ul>
     * <li>POST people/<personId>/favorites</li>
     * </ul>
     */
    @Test(dependsOnMethods = "AONE_14303", alwaysRun = true)
    public void AONE_14304() throws Exception
    {

        try
        {
            // Re-Favourite Site
            Favourite response = createFavourite(testUserA, testUserA, DOMAIN, siteNameA, FavType.SITE);
            assertNotNull(response);
            // Re-Favourite file.
            response = createFavourite(testUserA, testUserA, DOMAIN, docGuidA, FavType.FILE);
            assertNotNull(response);
            // User tries to mark folder as favourite twice
            response = createFavourite(testUserA, testUserA, DOMAIN, folderGuidA, FavType.FOLDER);
            assertNotNull(response);

            ListResponse<Favourite> favourites = getFavouritesList(testUserA, testUserA, DOMAIN, null);
            assertNotNull(favourites);

            int fileFavCount = favourites.getPaging().getCount();
            Assert.assertEquals(fileFavCount, 8, "Count isn't '8'.");

            Assert.assertTrue(favourites.getList().toString().contains(fileNameA), "File 'itemA' " + fileNameA + " isn't favorite");

            Assert.assertTrue(favourites.getList().toString().contains(folderNameA), "Folder 'folderA' " + folderNameA + " isn't favorite");

            Assert.assertTrue(favourites.getList().toString().contains(folderNameA), "Site 'siteA' " + siteNameA + " isn't favorite");
        }
        catch (PublicApiException e)
        {
            fail("Get favorites for user should return 200");
        }

    }

    /**
     * Test - AONE-14307:Unmarked as favorite entity
     * <ul>
     * <li>DELETE people/<personId>/favorites/<targetGuid></li>
     * </ul>
     */
    @Test(dependsOnMethods = "AONE_14304", alwaysRun = true)
    public void AONE_14307() throws Exception
    {

        try
        {
            removeFavouriteForGuid(testUserA, testUserA, DOMAIN, docGuidC);
            Assert.fail("AONE_14307 - Failed Unmarked as favorite entity");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, "Status Code isn't '404'");
        }

    }

    /**
     * Test - AONE-14308:Restraint access
     * <ul>
     * <li>DELETE people/<personId>/favorites/<targetGuid></li>
     * </ul>
     */
    @Test(dependsOnMethods = "AONE_14307", alwaysRun = true)
    public void AONE_14308() throws Exception
    {

        try
        {
            removeFavouriteForGuid(testUser2, testUserA, DOMAIN, docGuidA);
            Assert.fail("AONE_14308 - Failed Restraint access");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404, "Status Code isn't '404'");
        }

    }

    /**
     * Test - AONE-14310:Nonexistent entity
     * <ul>
     * <li>POST people/<personId>/favorites</li>
     * </ul>
     */
    @Test(dependsOnMethods = "AONE_14308", alwaysRun = true)
    public void AONE_14310() throws Exception
    {
        String tempGuid = folderGuidC + "inc";

        try
        {
            createFavourite(testUserA, testUserA, DOMAIN, tempGuid, FavType.FOLDER);
            Assert.fail("AONE_14310 - Failed (mark nonexistent entity as favorite)");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }

        ListResponse<Favourite> favourites = getFavouritesList(testUserA, testUserA, DOMAIN, null);
        assertNotNull(favourites);
        assertFalse(isGuidPresent(favourites, tempGuid), "Nonexistent entity (guid) is presented");

    }

    /**
     * Test - AONE-14312:POST from another domain
     * <ul>
     * <li>AONE-14312:POST people/<personId>/favorites/ from another domain</li>
     * </ul>
     */
    @Test(groups = "CloudOnly", dependsOnMethods = "AONE_14310", alwaysRun = true)
    public void AONE_14312() throws Exception
    {
        try
        {
            Favourite response = createFavourite(testUserDomain2, "-me-", domain1, docGuidOne, FavType.FILE);
            assertNotNull(response);

            response = createFavourite(testUserDomain2, testUserDomain2, domain1, folderGuidOne, FavType.FOLDER);
            assertNotNull(response);

            response = createFavourite(testUserDomain2, "-me-", domain1, siteNameOne, FavType.SITE);
            assertNotNull(response);
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }

        ListResponse<Favourite> favourites = getFavouritesList(testUserDomain2, testUserDomain2, domain1, null);
        assertNotNull(favourites);

        int fileFavCount = favourites.getPaging().getCount();
        Assert.assertEquals(fileFavCount, 3, "Count isn't '3'.");
        Assert.assertTrue(favourites.getList().get(0).getTarget().toString().contains(fileNameOne), "Target type isn't file " + fileNameOne);
        Assert.assertTrue(favourites.getList().get(1).getTarget().toString().contains(folderGuidOne), "Target type isn't folder " + folderNameOne);
        Assert.assertTrue(favourites.getList().get(2).getTarget().toString().contains(siteNameOne), "Target type isn't site " + siteNameOne);
    }

    /**
     * Test - AONE-14313:DELETE from another domain
     * <ul>
     * <li>DELETE people/<personId>/favorites/<targetGuid></li>
     * </ul>
     */
    @Test(groups = "CloudOnly", dependsOnMethods = "AONE_14312", alwaysRun = true)
    public void AONE_14313() throws Exception
    {
        try
        {
            HttpResponse response = removeFavouriteForGuid(testUserDomain2, testUserDomain2, domain1, docGuidOne);
            assertNotNull(response);
            assertEquals(response.getStatusCode(), 204);

            Site site = new SitesAPI().getSiteById(testUserDomain2, domain1, siteNameOne);
            response = removeFavouriteForGuid(testUserDomain2, testUserDomain2, domain1, site.getGuid());
            assertNotNull(response);
            assertEquals(response.getStatusCode(), 204);

            response = removeFavouriteForGuid(testUserDomain2, testUserDomain2, domain1, folderGuidOne);
            assertNotNull(response);
            assertEquals(response.getStatusCode(), 204);

        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }

        ListResponse<Favourite> favourites = getFavouritesList(testUserDomain2, testUserDomain2, domain1, null);
        assertNotNull(favourites);

        Assert.assertFalse(favourites.getPaging().getHasMoreItems(), "hasMoreItems isn't false");
        Assert.assertTrue(favourites.getPaging().getCount().equals(0), "Count isn't '0'");
        Assert.assertTrue(favourites.getPaging().getSkipCount().equals(0), "skipCount isn't '0'");
        Assert.assertTrue(favourites.getPaging().getMaxItems().equals(100), "maxItems isn't '100'");
        Assert.assertEquals(favourites.getList().size(), 0, "List isn't '0'");
    }

    /**
     * Test - AONE-14311:Get list of user's favorites from another domain.
     * <ul>
     * <li>GET people/<personId>/favorites</li>
     * </ul>
     */
    @Test(groups = "CloudOnly", dependsOnMethods = "AONE_14313", alwaysRun = true)
    public void AONE_14311() throws Exception
    {
        ListResponse<Favourite> favourites = getFavouritesList(testUserDomain2, testUserDomain2, domain1, null);
        assertNotNull(favourites);

        Assert.assertFalse(favourites.getPaging().getHasMoreItems(), "hasMoreItems isn't false");
        Assert.assertTrue(favourites.getPaging().getCount().equals(0), "Count isn't '0'");
        Assert.assertTrue(favourites.getPaging().getSkipCount().equals(0), "skipCount isn't '0'");
        Assert.assertTrue(favourites.getPaging().getMaxItems().equals(100), "maxItems isn't '100'");
        Assert.assertEquals(favourites.getList().size(), 0, "List isn't '0'");
    }

    /**
     * @param favourites
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

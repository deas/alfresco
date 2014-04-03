/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.dashlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.po.share.dashlet.SavedSearchDashlet;
import org.alfresco.po.share.dashlet.SiteSearchDashlet;
import org.alfresco.po.share.dashlet.SiteSearchItem;
import org.alfresco.po.share.enums.Dashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DocumentAspect;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Shan Nagarajan
 */
@Listeners(FailedTestListener.class)
public class SiteSearchDashletTest extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(SiteSearchDashletTest.class);
    private String siteDomain = "siteSearch.test";
    private String expectedHelpBallonMsg = "Use this dashlet to perform a site search and view the results.\nClicking the item name takes you to the details page so you can preview or work with the item.";
    private static final String LAST_NAME = "SSLastName";
    private String firstName = "First Name";
    private String lastNameWithSpace = "Last Name";
    private static final String NO_RESULTS_FOUND_MESSAGE = "No results found.";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10608() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10608() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        Assert.assertNotNull(searchDashlet);
        Assert.assertEquals(searchDashlet.getTitle(), Dashlet.SITE_SEARCH.getDashletName());

        searchDashlet.clickOnHelpIcon();
        Assert.assertTrue(searchDashlet.isBalloonDisplayed());
        Assert.assertEquals(searchDashlet.getHelpBalloonMessage(), expectedHelpBallonMsg);
        searchDashlet.closeHelpBallon().render();
        Assert.assertFalse(searchDashlet.isBalloonDisplayed());

        Assert.assertEquals(searchDashlet.getAvailableResultSizes(), Arrays.asList("10", "25", "50", "100"));

        searchDashlet.search("").render();
        Assert.assertEquals(searchDashlet.getContent(), "No results found.");
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10609() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Add Saved Search Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10609() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);

        String searchText = "£$%^&";
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search(searchText);
        Assert.assertEquals(searchDashlet.getContent(), "No results found.");

        // String searchText = ShareUser.getRandomStringWithNumders(1400);
        searchText = ShareUser.getRandomStringWithNumders(1400);

        // Search
        searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search(searchText).render();
        Assert.assertEquals(searchDashlet.getSearchText().length(), 1024);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10610() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        // Add Saved Search Dashlet
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Upload File
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10610() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSiteDashboard(drone, siteName);

        // Search
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, fileName);
        Assert.assertEquals(ShareUser.getName(items.get(0).getItemName()), fileName);

        DocumentDetailsPage detailsPage = items.get(0).getItemName().click().render();
        Assert.assertTrue(detailsPage != null);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10615() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload File
        String fileName = "test.doc";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10615() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = "test.doc";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "test");
        Assert.assertEquals(ShareUser.getName(items.get(0).getItemName()), fileName);

        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "doc");
        Assert.assertEquals(ShareUser.getName(items.get(0).getItemName()), fileName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" }, enabled = false)
    public void dataPrep_ALF_10616() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" }, enabled = false)
    public void alf_10616() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // TODO: Test 10616: Consider Enterprise equivalent test BasicSearch >
        // cloud_421, which essentially does all the dataprep
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10617() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = "12345" + getSiteName(testName);
        String folderName = getFolderName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10617() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = "12345" + getSiteName(testName);
        String folderName = getFolderName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, folderName);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), folderName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10618() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);

    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10618() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + System.currentTimeMillis() + "_1.1.txt";
        String[] fileInfo = { fileName, DOCLIB };

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSiteDashboard(drone, siteName);
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);
        documentLibraryPage.getSiteNav().selectSiteDashBoard().render();
        // Wait till index
        Assert.assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_CONTENT_DASHLET, fileName, true, siteName));

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, ".txt");
        Assert.assertEquals(ShareUser.getName(items.get(0).getItemName()), fileName);

        items = ShareUserDashboard.searchSiteSearchDashlet(drone, fileName);
        Assert.assertEquals(ShareUser.getName(items.get(0).getItemName()), fileName);

        DocumentDetailsPage detailsPage = items.get(0).getItemName().click().render();
        Assert.assertTrue(detailsPage != null);

        String newFileName = new StringBuffer(fileName).insert(4, '_').toString();
        ShareUser.editTextDocument(drone, newFileName, null, null);

        ShareUser.openSiteDashboard(drone, siteName);

        items = ShareUserDashboard.searchSiteSearchDashlet(drone, ".txt");
        Assert.assertEquals(ShareUser.getName(items.get(0).getItemName()), newFileName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10619() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB, "Les études de l'admissibilité de la solution dans cette société financière." };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10619() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName) + ".txt";
        String[] searchTerms = { "l'admissibilité", "l'admissibilite", "études", "etudes", "financière", "financiere" };
        String siteName = getSiteName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items;

        // Loop through the search terms and verify the item is found
        for (String searchTerm : searchTerms)
        {
            items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchTerm);
            Assert.assertEquals(items.size(), 1);
            Assert.assertEquals(ShareUser.getName(items.get(0).getItemName()), fileName);
        }
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10620() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10620() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        List<String> searchTermsList = new ArrayList<String>();
        searchTermsList.add("modified: today" + " and modifier: " + testUser);
        // TODO - MNT-10733
        searchTermsList.add("modified: \"" + getDate("yyyy-MM-dd") + "\"" + " and modifier: " + testUser);
        searchTermsList.add("modified: \"" + getDate("yyyy-MMM-dd") + "\"" + " and modifier: " + testUser);
        searchTermsList.add("modified: [" + getDate("yyyy-MM-dd") + " TO TODAY]" + " and modifier: " + testUser);
        searchTermsList.add("modified: [" + getDate("yyyy-MM-dd") + " TO NOW]" + " and modifier: " + testUser);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload File
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);
        documentLibraryPage.getSiteNav().selectSiteDashBoard().render();
        Assert.assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_CONTENT_DASHLET, fileName, true, siteName));

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        List<SiteSearchItem> items;

        for (String searchTerm : searchTermsList)
        {
            logger.info("Search Term: " + searchTerm);
            items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchTerm);
            Assert.assertEquals(ShareUser.getName(items.get(0).getItemName()), fileName);
        }
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10621() throws Exception
    {
        String testName = getTestName();
        String firstName = testName;
        String userName = firstName + " " + LAST_NAME;
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String[] testUserInfo = new String[] { userName, firstName, LAST_NAME };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10621() throws Exception
    {
        String testName = getTestName();
        String firstName = testName;
        String userName = firstName + " " + LAST_NAME;
        String fileName = getFileName(testName) + ".txt";
        String siteName = getSiteName(testName);

        List<String> searchTermsList = new ArrayList<String>();
        searchTermsList.add("modifier: " + userName);
        searchTermsList.add("modifier: " + firstName);
        searchTermsList.add("modifier: " + LAST_NAME);

        ShareUser.login(drone, userName, DEFAULT_PASSWORD);

        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items;

        for (String searchTerm : searchTermsList)
        {
            items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchTerm);
            Assert.assertEquals(items.size(), 1);
            Assert.assertEquals(ShareUser.getName(items.get(0).getItemName()), fileName);
        }
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10622() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        contentDetails.setDescription(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10622() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;
        String siteName = getSiteName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, fileName1);
        Assert.assertEquals(items.size(), 2);
        List<String> fileNames = Arrays.asList(fileName1, fileName2);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }

        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "description: " + fileName1);
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10623() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;
        String fileName3 = testName + "_10.10.txt";
        String siteName = getSiteName(testName);

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        contentDetails.setDescription(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName3);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10623() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;
        String fileName3 = testName + "_10.10.txt";
        String siteName = getSiteName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        // Search with "file1"
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, fileName1);
        Assert.assertEquals(items.size(), 2);
        List<String> fileNames = Arrays.asList(fileName1, fileName2);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }

        // Search with "name: file1"
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "name: " + fileName1);
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));

        // Search with "file3"
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, fileName3);
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName3));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10624() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Create File1
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Create File2 with File1 as title
        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        contentDetails.setTitle(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10624() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;
        String siteName = getSiteName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        // Search with File name 1 ("file1")
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, fileName1);
        Assert.assertEquals(items.size(), 2);
        List<String> fileNames = Arrays.asList(fileName1, fileName2);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }

        // Search with Title contains File name 1 ("title: file1")
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "title: " + fileName1);
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10625() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        DocumentLibraryPage libraryPage = ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
        DocumentDetailsPage detailsPage = libraryPage.selectFile(fileName2).render();
        EditDocumentPropertiesPage propertiesPage = detailsPage.selectEditProperties().render();
        propertiesPage.setAuthor(testUser);
        propertiesPage.selectSave().render();

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10625() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;
        String siteName = getSiteName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, fileName1);
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));

        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "author: " + testUser);
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10626() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String userName = testName + " " + LAST_NAME;
        String[] testUserInfo = new String[] { userName, firstName, lastNameWithSpace };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10626() throws Exception
    {
        String testName = getTestName();
        String userName = testName + " " + LAST_NAME;
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // Login
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<String> searchTermList = new ArrayList<String>();
        searchTermList.add("creator: " + userName);
        searchTermList.add("creator: " + testName);
        searchTermList.add("creator: " + LAST_NAME);

        List<SiteSearchItem> items;

        for (String searchTerm : searchTermList)
        {
            logger.info("Search Term: " + searchTerm);
            items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchTerm);
            Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));
        }

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10627() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = "file";
        String fileName1 = "Content1";
        String fileName2 = "Content2";
        String fileName3 = "Content3";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create Files
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setTitle(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        contentDetails.setDescription(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName3);
        contentDetails.setContent(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10627() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = "file";
        String fileName1 = "Content1";
        String fileName2 = "Content2";
        String fileName3 = "Content3";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, fileName);
        Assert.assertEquals(items.size(), 4);
        List<String> fileNames = Arrays.asList(fileName, fileName1, fileName2, fileName3);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10628() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10628() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = getFileName(testName);
        String[] fileInfo = { fileName, DOCLIB };

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.uploadFileInFolder(drone, fileInfo);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Wait till file has been indexed
        Assert.assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_CONTENT_DASHLET, fileName, true, siteName));
        // TODO - Remove commented code
        List<String> searchTerms = Arrays.asList("created: today",
        // "created: \"" + getDate("yyyy MM dd") + "\"",
                "created: \"" + getDate("yyyy-MM-dd") + "\"",
                // "created: \"" + getDate("yyyy MMM dd") + "\"",
                "created: \"" + getDate("yyyy-MMM-dd") + "\"", "created: [" + getDate("yyyy-MM-dd") + " TO TODAY]", "created: [" + getDate("yyyy-MM-dd")
                        + " TO NOW]");
        SavedSearchDashlet savedSearchDashlet;
        List<SiteSearchItem> items;

        for (String searchTerm : searchTerms)
        {
            logger.info("Search Term: " + searchTerm);
            items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchTerm);
            Assert.assertEquals(items.size(), 1);
            Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));
        }
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10629() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);
        String folderName = getFolderName(testName);
        String[] fileInfo = { fileName, DOCLIB };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ShareUser.uploadFileInFolder(drone, fileInfo);

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, testName);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10629() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);
        String folderName = getFolderName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "TYPE:\"cm:cmobject\"");
        Assert.assertEquals(items.size(), 2);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, folderName));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10630() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);
        String[] fileInfo = { fileName, DOCLIB };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10630() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        String nodeRef = documentLibraryPage.getFileDirectoryInfo(fileName).getNodeRef();
        documentLibraryPage.getSiteNav().selectSiteDashBoard().render();

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "ID: \"" + nodeRef + "\"");
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10631() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = "file1";
        String fileName2 = "file2";
        String fileName3 = "file3";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName3);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUser.addAspects(drone, Arrays.asList(DocumentAspect.VERSIONABLE), fileName1);
        ShareUser.addAspects(drone, Arrays.asList(DocumentAspect.TAGGABLE), fileName2);
        ShareUser.addAspects(drone, Arrays.asList(DocumentAspect.CLASSIFIABLE), fileName3);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10631() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = "file1";
        String fileName2 = "file2";
        String fileName3 = "file3";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "ASPECT: \"cm:" + DocumentAspect.VERSIONABLE.getValue() + "\"");
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName1);

        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "ASPECT: \"cm:" + DocumentAspect.TAGGABLE.getValue() + "\"");
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName2);

        // Changed to "generalclassifiable" as per MNT-10674
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "ASPECT: \"cm:" + "generalclassifiable" + "\"");
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName3);

        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "ASPECT:*");
        Assert.assertEquals(items.size(), 3);
        List<String> fileNames = Arrays.asList(fileName1, fileName2, fileName3);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10632() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String textFileName = "file1.txt";
        String xmlFileName = "note.xml";
        String htmlFileName = "heading.html";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload TXT File
        String[] textFileInfo = { textFileName };
        ShareUser.uploadFileInFolder(drone, textFileInfo);

        // Uploading XML file.
        String[] xmlFileInfo = { xmlFileName };
        ShareUser.uploadFileInFolder(drone, xmlFileInfo);

        // Uploading HTML file.
        String[] htmlFileInfo = { htmlFileName };
        ShareUser.uploadFileInFolder(drone, htmlFileInfo);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10632() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String textFileName = "file1.txt";
        String xmlFileName = "note.xml";
        String htmlFileName = "heading.html";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "content.mimetype:text\\/xml");
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), xmlFileName);

        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "content.mimetype:text\\/plain");
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), textFileName);

        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "content.mimetype:text\\/html");
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), htmlFileName);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10633() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = "Content.txt";
        String[] fileInfo = { fileName };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10633() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = "Content.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        SiteSearchDashlet siteSearchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        siteSearchDashlet = siteSearchDashlet.search("creator: \"admin\" and name: \"" + fileName + "\"");
        List<SiteSearchItem> items = siteSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 0);
        Assert.assertEquals(siteSearchDashlet.getContent(), NO_RESULTS_FOUND_MESSAGE);

        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "creator: \"" + testUser + "\" and name: \"" + fileName + "\"");
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "creator: \"admin\" or name: \"" + fileName + "\"");
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        siteSearchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        siteSearchDashlet = siteSearchDashlet.search("(creator: \"" + testUser + "\" and name: \"" + fileName + "\") and creator: \"admin\"");
        items = siteSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 0);
        Assert.assertEquals(siteSearchDashlet.getContent(), NO_RESULTS_FOUND_MESSAGE);

        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "(creator: \"" + testUser + "\" and name: \"" + fileName + "\") or creator: \"admin\"");
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "creator: \"" + testUser + "\" and (name: \"" + fileName + "\" or creator: \"admin\")");
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10634() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = "Content.txt";
        String[] fileInfo = { fileName };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10634() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = "Content.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "KEYWORD(" + fileName + ")");
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10635() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = "file1q.txt";
        String title1 = "Acequia";
        String description1 = "Quiz";
        String content1 = "Tequila";

        String fileName2 = "file2.txt";
        String title2 = "title2";
        String description2 = "description2";
        String content2 = "content2";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create 2 File
        ContentDetails contentDetails = new ContentDetails(fileName1, title1, description1, content1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails(fileName2, title2, description2, content2);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10635() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = "file1q.txt";
        String fileName2 = "file2.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "ALL: \"*q*\"");
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10636() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = "Content.txt";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ContentDetails contentDetails = new ContentDetails(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUser.addAspects(drone, Arrays.asList(DocumentAspect.DUBLIN_CORE), fileName);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10636() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = "Content.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, " ASPECT:\"{http://www.alfresco.org/model/content/1.0}dublincore\"");
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10637() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = "1234.txt";
        String[] fileInfo = { fileName };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10637() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = "1234.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<String> searchTerms = new ArrayList<String>();
        searchTerms.add("12?4");
        searchTerms.add("*234");
        searchTerms.add("=" + fileName);
        searchTerms.add("\"1234\"");
        searchTerms.add("????");
        searchTerms.add("\"???4\"");

        List<SiteSearchItem> items;

        for (String searchTerm : searchTerms)
        {
            items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchTerm);
            Assert.assertEquals(items.size(), 1);
            Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));
        }

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10638() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10638() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<String> searchTerms = new ArrayList<String>();
        searchTerms.add("{http://www.alfresco.org/model/content/1.0}name: " + fileName);
        searchTerms.add("@{http://www.alfresco.org/model/content/1.0}name: " + fileName);

        List<SiteSearchItem> items;

        for (String searchTerm : searchTerms)
        {
            items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchTerm);
            Assert.assertEquals(items.size(), 1);
            Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));
        }

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10639() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10639() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "cm_name = " + fileName);
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10640() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = "test.txt";
        String fileName2 = "tab.txt";
        String fileName3 = "alf.txt";
        String[] fileInfo1 = { fileName1 };
        String[] fileInfo2 = { fileName2 };
        String[] fileInfo3 = { fileName3 };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo1);
        ShareUser.uploadFileInFolder(drone, fileInfo2);
        ShareUser.uploadFileInFolder(drone, fileInfo3);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10640() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = "test.txt";
        String fileName2 = "tab.txt";
        String fileName3 = "alf.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "+created:[MIN TO NOW] AND +modifier: (" + testUser
                + ") AND !test AND -(tab)");
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName3));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10641() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName) + "-1.txt";
        String[] fileInfo1 = { fileName1 };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo1);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10641() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName) + "-1.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "cm:initialVersion:true");
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10642() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName) + "-1.txt";
        String fileName2 = getFileName(testName) + "-2.txt";
        String[] fileInfo1 = { fileName1 };
        String[] fileInfo2 = { fileName2 };
        String comment = getComment(fileName1);

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Upload two files and add comment to file1
        ShareUser.uploadFileInFolder(drone, fileInfo1);
        ShareUser.uploadFileInFolder(drone, fileInfo2);
        // Add Comment for File1
        ShareUser.addComment(drone, fileName1, comment);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10642() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName) + "-1.txt";
        String fileName2 = getFileName(testName) + "-2.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "fm:commentCount: 1");
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10643() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName) + "-1.txt";
        String fileName2 = getFileName(testName) + "-2.txt";
        String[] fileInfo1 = { fileName1 };
        String[] fileInfo2 = { fileName2 };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Upload two documents
        ShareUser.uploadFileInFolder(drone, fileInfo1);
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo2);
        // Edit file1 and make some changes (To update the version)
        documentLibraryPage.selectFile(fileName1).render();
        ShareUser.editTextDocument(drone, fileName1, fileName1, fileName1);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10643() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName) + "-1.txt";
        String fileName2 = getFileName(testName) + "-2.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);
        // Verify upon searching with "cm:versionLabel: 1.1", search results
        // show File 1.
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "cm:versionLabel: 1.1");
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10644() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName) + "-1.txt";
        String fileName2 = getFileName(testName) + "-2.txt";
        String fileName3 = getFileName(testName) + "-3.txt";
        String[] fileInfo1 = { fileName1 };
        String[] fileInfo2 = { fileName2 };
        String[] fileInfo3 = { fileName3 };
        String comment = testName + " Comment.";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Upload 3 files
        ShareUser.uploadFileInFolder(drone, fileInfo1);
        ShareUser.uploadFileInFolder(drone, fileInfo2);
        ShareUser.uploadFileInFolder(drone, fileInfo3);

        // Add one comment to File2
        ShareUser.addComment(drone, fileName2, comment);
        // Add two comments to File3
        ShareUser.addComment(drone, fileName3, comment);
        ShareUser.addComment(drone, fileName3, comment);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10644() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName) + "-1.txt";
        String fileName2 = getFileName(testName) + "-2.txt";
        String fileName3 = getFileName(testName) + "-3.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "fm:commentCount:1..2");
        Assert.assertEquals(items.size(), 2);
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName3));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10645() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName) + "-1.txt";
        String fileName2 = getFileName(testName) + "-2.txt";

        String file1Content = "big red apple";
        String file2Content = "big red tasty sweet apple";
        String[] fileInfo1 = { fileName1, DOCLIB, file1Content };
        String[] fileInfo2 = { fileName2, DOCLIB, file2Content };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.uploadFileInFolder(drone, fileInfo1);
        ShareUser.uploadFileInFolder(drone, fileInfo2);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10645() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName) + "-1.txt";
        String fileName2 = getFileName(testName) + "-2.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "TEXT:(big * apple)");
        Assert.assertEquals(items.size(), 2);
        List<String> fileNames = Arrays.asList(fileName1, fileName2);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }

        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "TEXT:(big *(1) apple)");
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName1);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10646() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        String fileContent = "big red apple";
        String[] fileInfo = { fileName, DOCLIB, fileContent };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10646() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "\"big red apple\"^3");
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10647() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName) + "-1.txt";
        String fileName2 = getFileName(testName) + "-2.txt";

        String file1Content = "this is an item";
        String file2Content = "this is the best item";
        String[] fileInfo1 = { fileName1, DOCLIB, file1Content };
        String[] fileInfo2 = { fileName2, DOCLIB, file2Content };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Create Files
        ShareUser.uploadFileInFolder(drone, fileInfo1);
        ShareUser.uploadFileInFolder(drone, fileInfo2);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10647() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName) + "-1.txt";
        String fileName2 = getFileName(testName) + "-2.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "\"this[^] item[$]\"");

        Assert.assertEquals(items.size(), 2);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10648() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        String fileContent = "<type>d:text</type>";
        String[] fileInfo = { fileName, DOCLIB, fileContent };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10648() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "\\<type\\>d\\:text\\</type\\>");
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10649() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        String fileContent = "apple and banana";
        String[] fileInfo = { fileName, DOCLIB, fileContent };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void alf_10649() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "TEXT:apple..banana");
        Assert.assertTrue(items.isEmpty());
        SiteSearchDashlet siteSearchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        Assert.assertEquals(siteSearchDashlet.getContent(), NO_RESULTS_FOUND_MESSAGE);

        ShareUser.logout(drone);
    }
}

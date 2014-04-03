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

import java.util.Arrays;
import java.util.List;

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
import org.alfresco.po.share.site.document.EditTextDocumentPage;
import org.alfresco.po.share.site.document.SelectAspectsPage;
import org.alfresco.share.util.AbstractTests;
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
public class SiteSearchDashletTest extends AbstractTests
{

    private static Log logger = LogFactory.getLog(SiteSearchDashletTest.class);
    private String siteDomain = "siteSearch.test";
    private String expectedHelpBallonMsg = "Use this dashlet to perform a site search and view the results.\nClicking the item name takes you to the details page so you can preview or work with the item.";
    private String lastName = "LastName";
    private String firstName = "First Name";
    private String lastNameWithSpace = "Last Name";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
    }

    // TODO: Specify Group EnterpriseOnly for all methods incl dataprep
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

    @Test(groups = { "Enterprise42" })
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

        Assert.assertEquals(searchDashlet.getAvailableResultSizes(), Arrays.asList(new String[] { "10", "25", "50", "100" }));

        searchDashlet.search("").render();
        Assert.assertEquals(searchDashlet.getContent(), "No results found.");
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10609() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10609() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // TODO: Consider moving site creation and addDashlet in dataPrep
        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);

        // TODO: Testlink: For Step 2, Specify expected result for search with wildcards. No Results? Or add content to site for some results?
        List<SiteSearchItem> resultsSet = ShareUserDashboard.searchSiteSearchDashlet(drone, "*?");
        Assert.assertTrue(resultsSet.isEmpty());

        String searchText = ShareUser.getRandomStringWithNumders(1400);

        // Search
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search(searchText).render();
        Assert.assertEquals(searchDashlet.getSearchText().length(), 1024);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10610() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10610() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload File
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);

        // Search
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, fileName);

        // TODO: TBD: Assumes 1st result is right without assert. Add util <selectLink(contentName) to get the result with contentName
        DocumentDetailsPage detailsPage = items.get(0).getItemName().click().render();
        Assert.assertTrue(detailsPage instanceof DocumentDetailsPage);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10615() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10615() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload File
        String fileName = "test.doc";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);

        searchDashlet.search("test");
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("doc");
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10616() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10616() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // TODO: Test 10616: Consider Enterprise equivalent test BasicSearch > cloud_421, which essentially does all the dataprep
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10617() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10617() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = "12345" + getSiteName(testName + System.currentTimeMillis());
        String folderName = getFolderName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);

        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search(folderName).render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();

        Assert.assertEquals(items.get(0).getItemName().getDescription(), folderName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10618() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10618() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        String fileName = getFileName(testName) + "_1.1.txt";
        
        // TODO: Use upload file for easy maintenance if the feature is made available for cloud
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);

        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search(".txt").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search(fileName).render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        DocumentDetailsPage detailsPage = items.get(0).getItemName().click().render();
        Assert.assertTrue(detailsPage instanceof DocumentDetailsPage);

        // TODO: Replace following code with Edit Properties Util
        EditTextDocumentPage documentPage = detailsPage.selectInlineEdit().render();
        String newFileName = new StringBuffer(fileName).insert(4, '_').toString();
        contentDetails.setName(newFileName);
        documentPage.save(contentDetails).render();

        ShareUser.openSiteDashboard(drone, siteName);

        items = ShareUserDashboard.searchSiteSearchDashlet(drone, ".txt");

        Assert.assertEquals(items.get(0).getItemName().getDescription(), newFileName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10619() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10619() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = getFileName(testName);

        // TODO: Add Missing Steps
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10620() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10620() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = getFileName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        // TODO: Use Upload file
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);

        // TODO: Use searchSiteSearchDashlet util
        String searchTerm = "modified: today";
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchTerm);
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchTerm = "modified: \"" + getDate("yyyy MM dd") + "\"";
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchTerm);
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchTerm = "modified: \"" + getDate("yyyy-MM-dd") + "\"";
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchTerm);
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchTerm = "modified: \"" + getDate("yyyy MMM dd") + "\"";
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchTerm);
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchTerm = "modified: \"" + getDate("yyyy-MMM-dd") + "\"";
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchTerm);
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchTerm = "modified: [" + getDate("yyyy-MM-dd") + " TO TODAY]";
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchTerm);
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchTerm = "modified: [" + getDate("yyyy-MM-dd") + " TO NOW]";
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchTerm);
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10621() throws Exception
    {
        String testName = getTestName();

        // User
        String testUser = getUserNameForDomain(testName, siteDomain);
        String userInfo[] = {testUser, "User Name"};
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10621() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String userFirstName = "User Name";
        String siteName = getSiteName(testName + System.currentTimeMillis());

        // Login
        ShareUser.login(drone, testUser);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload File
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);

        // TODO: Replace with site search util
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("modifier: " + userFirstName);
        List<SiteSearchItem> items = searchDashlet.getSearchItems();

        // TODO: Assert assumes one and only 1 result, use util to check result. true if fileName is visible, false if not, or no results
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("modifier: " + "User");
        items = searchDashlet.getSearchItems();
        // TODO: Assert assumes one and only 1 result, use util to check result. true if fileName is visible, false if not, or no results
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("modifier: " + "Name");
        items = searchDashlet.getSearchItems();
        // TODO: Assert assumes one and only 1 result, use util to check result. true if fileName is visible, false if not, or no results
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10622() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10622() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // TODO: Consider moving site creation, adding dashlet and files to dataprep
        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName("file1");
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName("file2");
        contentDetails.setDescription("file1");
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        // TODO: Use util to getDashlet and search
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("file1").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        
        // TODO: Do not assert results size, if not in test. Check using util file1 and file2 exist in searchResults
        Assert.assertEquals(items.size(), 2);
        List<String> fileNames = Arrays.asList("file1", "file2");
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }
        
        // TODO: Use util to search
        searchDashlet.search("description: file1").render();
        items = searchDashlet.getSearchItems();
        
        // TODO: Do not assert results size, if not in test. Check using util file1 is not displayed and file2 exist in searchResults
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), "file2");
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10623() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10623() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName1 = "file1";
        String fileName2 = "file2";
        String fileName3 = testName + "_10.10.txt";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
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

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search(fileName1).render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 2);
        List<String> fileNames = Arrays.asList("file1", "file2");
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }
        searchDashlet.search("name: " + fileName1).render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName1);
        searchDashlet.search(fileName3).render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName3);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10624() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10624() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName1 = "file1";
        String fileName2 = "file2";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        contentDetails.setTitle(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search(fileName1).render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 2);
        List<String> fileNames = Arrays.asList("file1", "file2");
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }
        searchDashlet.search("title: " + fileName1).render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName2);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10625() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10625() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName1 = "file1";
        String fileName2 = "file2";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        DocumentLibraryPage libraryPage = ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
        DocumentDetailsPage detailsPage = libraryPage.selectFile(fileName2).render();
        EditDocumentPropertiesPage propertiesPage = detailsPage.selectEditProperties().render();
        propertiesPage.setAuthor(testUser);
        detailsPage = propertiesPage.selectSave().render();

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search(fileName1).render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName1);
        searchDashlet.search("author: " + testUser).render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName2);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10626() throws Exception
    {
        String testName = getTestName();

        // User
        String userName = testName + " " + lastName;
        ShareUser.createEnterpriseUser(drone, "", userName, firstName, lastNameWithSpace, ADMIN_PASSWORD);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10626() throws Exception
    {
        String testName = getTestName();
        String testUser = testName + " " + lastName;
        String siteName = getSiteName(testName + System.currentTimeMillis());

        // Login
        ShareUser.login(drone, testUser, ADMIN_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload File
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("creator: " + testUser);
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("creator: " + firstName);
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("creator: " + lastNameWithSpace);
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("creator: " + testName);
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("creator: " + "First");
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("creator: " + "Last");
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("creator: " + "Name");
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10627() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10627() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = "file";
        String fileName1 = "file1";
        String fileName2 = "file2";
        String fileName3 = "file3";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
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

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search(fileName).render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 4);
        List<String> fileNames = Arrays.asList(fileName, fileName1, fileName2, fileName3);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10628() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10628() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = getFileName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("created: today").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("created: \"" + getDate("yyyy MM dd") + "\"").render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("created: \"" + getDate("yyyy-MM-dd") + "\"").render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("created: \"" + getDate("yyyy MMM dd") + "\"").render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("created: \"" + getDate("yyyy-MMM-dd") + "\"").render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("created: [" + getDate("yyyy-MM-dd") + " TO TODAY]").render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("created: [" + getDate("yyyy-MM-dd") + " TO NOW]").render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10629() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10629() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = getFileName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create Folder
        ShareUserSitePage.createFolder(drone, testName, testName);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("TYPE:\"cm:cmobject\"").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 2);
        List<String> fileNames = Arrays.asList(testName, fileName);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10631() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10631() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName1 = "file1";
        String fileName2 = "file2";
        String fileName3 = "file3";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        DocumentLibraryPage libraryPage = ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
        DocumentDetailsPage detailsPage = libraryPage.selectFile(fileName1).render();
        SelectAspectsPage aspectsPage = detailsPage.selectManageAspects().render();
        List<DocumentAspect> aspects = Arrays.asList(DocumentAspect.VERSIONABLE);
        aspectsPage.add(aspects).render();
        aspectsPage.clickApplyChanges().render();

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        libraryPage = ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
        detailsPage = libraryPage.selectFile(fileName2).render();
        aspectsPage = detailsPage.selectManageAspects().render();
        aspects = Arrays.asList(DocumentAspect.TAGGABLE);
        aspectsPage.add(aspects).render();
        aspectsPage.clickApplyChanges().render();

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName3);
        libraryPage = ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
        detailsPage = libraryPage.selectFile(fileName3).render();
        aspectsPage = detailsPage.selectManageAspects().render();
        aspects = Arrays.asList(DocumentAspect.CLASSIFIABLE);
        aspectsPage.add(aspects).render();
        aspectsPage.clickApplyChanges().render();

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("ASPECT: " + DocumentAspect.VERSIONABLE.getValue()).render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName1);

        searchDashlet.search("ASPECT: " + DocumentAspect.TAGGABLE.getValue()).render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName2);

        searchDashlet.search("ASPECT: " + DocumentAspect.CLASSIFIABLE.getValue()).render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName3);

        searchDashlet.search("ASPECT:*").render();
        Assert.assertEquals(items.size(), 3);
        List<String> fileNames = Arrays.asList(fileName1, fileName2, fileName3);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10632() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10632() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String textFileName = "file1.txt";
        String xmlFileName = "note.xml";
        String htmlFileName = "heading.html";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(textFileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Uploading XML file.
        String[] xmlFileInfo = { xmlFileName };
        ShareUser.uploadFileInFolder(drone, xmlFileInfo);

        // Uploading XML file.
        String[] htmlFileInfo = { htmlFileName };
        ShareUser.uploadFileInFolder(drone, htmlFileInfo);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("content.mimetype:text\\/xml").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), xmlFileName);

        searchDashlet.search("content.mimetype:text\\/plain").render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), textFileName);

        searchDashlet.search("content.mimetype:text\\/html ").render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), htmlFileName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10634() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10634() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String textFileName = "Context.txt";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(textFileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("KEYWORD(" + textFileName + ")").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), textFileName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10635() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10635() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = "qfile";
        String fileName1 = "file1";
        String fileName2 = "file2";
        String fileName3 = "file3";
        String fileName4 = "file4";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
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

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName4);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("ALL:\"*q*\"").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 4);
        List<String> fileNames = Arrays.asList(fileName, fileName1, fileName2, fileName3);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10636() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10636() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = getFileName(testName) + ".txt";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        DocumentLibraryPage libraryPage = ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
        DocumentDetailsPage detailsPage = libraryPage.selectFile(fileName).render();
        SelectAspectsPage aspectsPage = detailsPage.selectManageAspects().render();
        List<DocumentAspect> aspects = Arrays.asList(DocumentAspect.DUBLIN_CORE);
        aspectsPage.add(aspects).render();
        aspectsPage.clickApplyChanges().render();

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search(" ASPECT:\"{http://www.alfresco.org/model/content/1.0}dublincore\"").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10637() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10637() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = "1234" + ".txt";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("12?4").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("*234").render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("=" + fileName).render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("\"1234\"").render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("????").render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("\"???4\"").render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10638() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10638() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = getFileName(testName) + "_1";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setContent(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("{http://www.alfresco.org/model/content/1.0}name: " + fileName).render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("@{http://www.alfresco.org/model/content/1.0}name: " + fileName).render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10639() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10639() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = getFileName(testName) + ".txt";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("cm_name = " + fileName).render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10640() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10640() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName1 = "test.txt";
        String fileName2 = "tab.txt";
        String fileName3 = "alf.txt";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName3);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("+created:[MIN TO NOW] AND +modifier: (" + testUser + ") AND !test AND -(tab)").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName3);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10641() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10641() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = getFileName(testName) + ".txt";
        String fileName2 = getFileName(testName) + "_2.txt";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        DocumentLibraryPage libraryPage = ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
        DocumentDetailsPage detailsPage = libraryPage.selectFile(fileName).render();
        SelectAspectsPage aspectsPage = detailsPage.selectManageAspects().render();
        List<DocumentAspect> aspects = Arrays.asList(DocumentAspect.VERSIONABLE);
        aspectsPage.add(aspects).render();
        aspectsPage.clickApplyChanges().render();

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("cm:initialVersion:true").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10642() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10642() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = getFileName(testName) + ".txt";
        String fileName2 = getFileName(testName) + "_2.txt";
        String comment = getComment(fileName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        DocumentLibraryPage libraryPage = ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
        DocumentDetailsPage detailsPage = libraryPage.selectFile(fileName).render();
        detailsPage = detailsPage.addComment(comment).render();

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("fm:commentCount: 1").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10643() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10643() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = getFileName(testName) + ".txt";
        String fileName2 = getFileName(testName) + "_2.txt";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        DocumentLibraryPage libraryPage = ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
        DocumentDetailsPage detailsPage = libraryPage.selectFile(fileName).render();
        SelectAspectsPage aspectsPage = detailsPage.selectManageAspects().render();
        List<DocumentAspect> aspects = Arrays.asList(DocumentAspect.VERSIONABLE);
        aspectsPage.add(aspects).render();
        detailsPage = aspectsPage.clickApplyChanges().render();
        EditTextDocumentPage textDocumentPage = detailsPage.selectInlineEdit().render();
        ContentDetails details = new ContentDetails();
        details.setName(fileName);
        details.setContent(fileName);
        detailsPage = textDocumentPage.save(details).render();

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("cm:versionLabel: 1.1").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10644() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10644() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName1 = getFileName(testName) + "_1";
        String fileName2 = getFileName(testName) + "_2";
        String fileName3 = getFileName(testName) + "_3";
        String comment = getComment(fileName1);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        contentDetails.setDescription(fileName2);
        DocumentLibraryPage libraryPage = ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
        DocumentDetailsPage detailsPage = libraryPage.selectFile(fileName2).render();
        detailsPage = detailsPage.addComment(comment).render();

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName3);
        libraryPage = ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
        detailsPage = libraryPage.selectFile(fileName3).render();
        detailsPage = detailsPage.addComment(comment).render();
        detailsPage = detailsPage.addComment(comment).render();

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("fm:commentCount:1..2").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 2);
        List<String> fileNames = Arrays.asList(fileName2, fileName3);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10645() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10645() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName1 = getFileName(testName) + "_1";
        String file1Content = "big red apple";
        String fileName2 = getFileName(testName) + "_2";
        String file2Content = "big red tasty sweet apple";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setContent(file1Content);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        contentDetails.setContent(file2Content);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("TEXT:(big * apple)").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 2);
        List<String> fileNames = Arrays.asList(fileName1, fileName2);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }

        searchDashlet.search("TEXT:(big *(1) apple)").render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName1);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10646() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10646() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = getFileName(testName) + "_1";
        String fileContent = "big red apple";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setContent(fileContent);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("\"big red apple\"^3").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10647() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10647() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName1 = getFileName(testName) + "_1";
        String file1Content = "this is an item";
        String fileName2 = getFileName(testName) + "_2";
        String file2Content = "this is the best item";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setContent(file1Content);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        contentDetails.setContent(file2Content);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("\"this[^] item[$]\"").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 2);
        List<String> fileNames = Arrays.asList(fileName1, fileName2);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10648() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10648() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = getFileName(testName) + "_1";
        String fileContent = "<type>d:text</type>";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setContent(fileContent);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("\\<type\\>d\\:text\\</type\\>").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet" })
    public void dataPrep_ALF_10649() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42" })
    public void alf_10649() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = getFileName(testName) + "_1";
        String fileContent = "apple and banana";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setContent(fileContent);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("TEXT:apple..banana").render();
        Assert.assertEquals(searchDashlet.getContent(), "No results found.");
    }

}
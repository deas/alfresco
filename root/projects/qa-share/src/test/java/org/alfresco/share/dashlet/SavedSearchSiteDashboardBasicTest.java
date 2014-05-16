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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.dashlet;

import java.util.List;

import org.alfresco.po.share.dashlet.ConfigureSavedSearchDialogBoxPage;
import org.alfresco.po.share.dashlet.SavedSearchDashlet;
import org.alfresco.po.share.dashlet.SearchLimit;
import org.alfresco.po.share.dashlet.SiteSearchItem;
import org.alfresco.po.share.enums.Dashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditTextDocumentPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Ranjith Manyam
 */
@Listeners(FailedTestListener.class)
public class SavedSearchSiteDashboardBasicTest extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(SavedSearchSiteDashboardBasicTest.class);
    private String siteDomain = "savedSearch.test";
    private String expectedHelpBallonMsg = "Use this dashlet to set up a search and view the results.\n"
            + "Configure the dashlet to save the search and set the title text of the dashlet.\n"
            + "Only a Site Manager can configure the search and title - this dashlet is ideal for generating report views in a site.";
    public static final String BALLOON_TEXT_VALUE_NOT_EMPTY = "The value cannot be empty.";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10338() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Add Saved Search Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void ALF_10338() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        SiteDashboardPage siteDashBoard = SiteUtil.openSiteDashboard(drone, siteName);

        // Verify Saved search Dashlet has been added to the Site dashboard
        Assert.assertNotNull(siteDashBoard);
        SavedSearchDashlet searchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        Assert.assertNotNull(searchDashlet);
        Assert.assertEquals(searchDashlet.getTitle(), Dashlet.SAVED_SEARCH.getDashletName());
        Assert.assertEquals(searchDashlet.getContent(), "No results found.");

        // Verify Help balloon message has been displayed correctly
        searchDashlet.clickOnHelpIcon();
        Assert.assertTrue(searchDashlet.isBalloonDisplayed());
        Assert.assertEquals(searchDashlet.getHelpBalloonMessage(), expectedHelpBallonMsg);
        searchDashlet.closeHelpBallon().render();
        Assert.assertFalse(searchDashlet.isBalloonDisplayed());

        // Verify expected Configure Saved search elements are displayed
        ConfigureSavedSearchDialogBoxPage configureSavedSearchPage = searchDashlet.clickOnEditButton().render();
        configureSavedSearchPage.clickOnCloseButton().render();
        searchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        Assert.assertNotNull(searchDashlet);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10339() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String contentPrefix = "Test-";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // TODO: For quicker file creation, consider using
        // ShareUser.createCopyOfAllContent(drone);
        // Upload 30 Files
        for (int i = 1; i <= 30; i++)
        {
            String fileName = contentPrefix + getFileName(testName) + "-" + i + ".txt";
            String[] fileInfo = { fileName, DOCLIB };
            ShareUser.uploadFileInFolder(drone, fileInfo);
        }

        // Add Saved Search Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void ALF_10339() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String searchTerm = "Test-";
        String savedSearchTitle = "SiteDocs";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        SiteUtil.openSiteDashboard(drone, siteName);

        // Configure Saved Search dashlet with "Test", set the SearchLimit to 10
        // and verify Correct number of results displayed
        ShareUserDashboard.configureSavedSearch(drone, searchTerm, savedSearchTitle, SearchLimit.TEN);

        SavedSearchDashlet searchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);

        List<SiteSearchItem> searchResults = searchDashlet.getSearchItems();
        for (SiteSearchItem result : searchResults)
        {
            Assert.assertTrue(result.getItemName().getDescription().startsWith("Test-"));
        }
        Assert.assertEquals(searchDashlet.getTitle(), savedSearchTitle);
        Assert.assertEquals(searchResults.size(), SearchLimit.TEN.getValue());

        // Increase the limit to 25 and verify the number of results
        ConfigureSavedSearchDialogBoxPage configureSavedSearchDialogBoxPage = searchDashlet.clickOnEditButton().render();
        configureSavedSearchDialogBoxPage.setSearchLimit(SearchLimit.TWENTY_FIVE);
        configureSavedSearchDialogBoxPage.clickOnOKButton().render();

        searchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);

        searchResults = searchDashlet.getSearchItems();
        for (SiteSearchItem result : searchResults)
        {
            Assert.assertTrue(result.getItemName().getDescription().startsWith("Test-"));
        }
        Assert.assertEquals(searchDashlet.getTitle(), savedSearchTitle);
        Assert.assertEquals(searchResults.size(), SearchLimit.TWENTY_FIVE.getValue());
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10340() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String contentPrefix = "Test-";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Upload File
        String fileName = contentPrefix + getFileName(testName) + "-.txt";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void ALF_10340() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String searchTerm = "Test-";
        String savedSearchTitle = "SiteDocs";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        SiteUtil.openSiteDashboard(drone, siteName);

        SavedSearchDashlet searchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);

        // Saved Search details before configure
        Assert.assertNotNull(searchDashlet);
        Assert.assertEquals(searchDashlet.getTitle(), Dashlet.SAVED_SEARCH.getDashletName());
        Assert.assertEquals(searchDashlet.getContent(), "No results found.");

        // Configure Saved Search and Cancel
        ConfigureSavedSearchDialogBoxPage configureSavedSearchPage = searchDashlet.clickOnEditButton().render();
        configureSavedSearchPage.setSearchTerm(searchTerm);
        configureSavedSearchPage.setTitle(savedSearchTitle);
        configureSavedSearchPage.setSearchLimit(SearchLimit.TEN);
        configureSavedSearchPage.clickOnCancelButton().render();

        // Verify Saved Search dashlet after cancel
        searchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        Assert.assertNotNull(searchDashlet);
        Assert.assertEquals(searchDashlet.getTitle(), Dashlet.SAVED_SEARCH.getDashletName());
        Assert.assertEquals(searchDashlet.getContent(), "No results found.");
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10341() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Upload File
        String fileName = getFileName(testName) + "-.txt";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void ALF_10341() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String title = "£$%^&";
        String searchTerm = title;

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        SiteUtil.openSiteDashboard(drone, siteName);

        // Configure search with wild chars
        ShareUserDashboard.configureSavedSearch(drone, searchTerm, title, SearchLimit.TEN);
        SavedSearchDashlet searchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);

        Assert.assertNotNull(searchDashlet);
        Assert.assertEquals(searchDashlet.getTitle(), title);
        Assert.assertEquals(searchDashlet.getContent(), "No results found.");

        title = ShareUser.getRandomStringWithNumders(2100);
        searchTerm = title;

        // Configure search with more than 2048 chars
        ShareUserDashboard.configureSavedSearch(drone, searchTerm, title, SearchLimit.TEN);
        searchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);

        Assert.assertNotNull(searchDashlet);
        Assert.assertEquals(searchDashlet.getTitle().length(), 2048);
        Assert.assertEquals(searchDashlet.getTitle(), title.substring(0, 2048));
        Assert.assertEquals(searchDashlet.getContent(), "No results found.");
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10342() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + "-.txt";
        String[] fileInfo = { fileName, DOCLIB };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void ALF_10342() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + "-.txt";
        String searchTerm_Empty = "";
        String searchTerm_Spaces = "   ";
        String searchTerm_Valid = testName;

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        SiteUtil.openSiteDashboard(drone, siteName);
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);

        // Configure Saved Search with blank Search term and verify
        ConfigureSavedSearchDialogBoxPage configureSavedSearchPage = savedSearchDashlet.clickOnEditButton().render();
        configureSavedSearchPage.setSearchTerm(searchTerm_Empty);
        configureSavedSearchPage = configureSavedSearchPage.clickOnOKButton().render();
        Assert.assertTrue(configureSavedSearchPage.isHelpBalloonDisplayed());
        Assert.assertEquals(configureSavedSearchPage.getHelpBalloonMessage(), BALLOON_TEXT_VALUE_NOT_EMPTY);

        // Configure Saved Search with search term as spaces
        configureSavedSearchPage.setSearchTerm(searchTerm_Spaces);
        configureSavedSearchPage = configureSavedSearchPage.clickOnOKButton().render();
        Assert.assertTrue(configureSavedSearchPage.isHelpBalloonDisplayed());
        Assert.assertEquals(configureSavedSearchPage.getHelpBalloonMessage(), BALLOON_TEXT_VALUE_NOT_EMPTY);

        // Configure Saved Search with valid data
        configureSavedSearchPage.setSearchTerm(searchTerm_Valid);
        configureSavedSearchPage.clickOnOKButton().render();
        SavedSearchDashlet searchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);

        Assert.assertNotNull(searchDashlet);
        Assert.assertEquals(searchDashlet.getTitle(), "Saved Search");
        Assert.assertEquals(searchDashlet.getSearchItems().size(), 1);
        Assert.assertEquals(searchDashlet.getSearchItems().get(0).getItemName().getDescription(), fileName);
    }

    // TODO - ALF-10343: Search Term - partially defined query
    // TODO - ALF-10344: Search Term - title (name)
    // TODO - ALF-10345: Search Term - text (description)
    // TODO - ALF-10346: Search Term - tag

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10347() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = "test.doc";
        String[] fileInfo = { fileName, DOCLIB };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void ALF_10347() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = "test.doc";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        // Configure Saved search with file name "test"
        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, "test");
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));
        Assert.assertEquals(items.size(), 1);

        // Configure Saved search with file extension ".doc"
        items = ShareUserDashboard.searchSavedSearchDashlet(drone, "doc");
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));
        Assert.assertEquals(items.size(), 1);
    }

    // TODO - ALF-10348:Search Term - content

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10349() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = "12345" + getSiteName(testName);
        String folderName = testName + "-Folder";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void ALF_10349() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = "12345" + getSiteName(testName);
        String folderName = testName + "-Folder";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);
        // Configure Saved search with Folder Name
        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, folderName);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, folderName));
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10350() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise4.2" })
    public void ALF_10350() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis() + "_1.1.txt";
        String[] fileInfo = { fileName, DOCLIB };

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        // Create site and upload document
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);
        documentLibraryPage.getSiteNav().selectSiteDashBoard().render();
        // Add Saved Search Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);

        // Wait till index
        Assert.assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_CONTENT_DASHLET, fileName, true, siteName));

        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, ".txt");
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

        items = ShareUserDashboard.searchSavedSearchDashlet(drone, fileName);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

        DocumentDetailsPage detailsPage = items.get(0).getItemName().click().render();
        Assert.assertTrue(detailsPage != null);
        EditTextDocumentPage documentPage = detailsPage.selectInlineEdit().render();
        String newFileName = new StringBuffer(fileName).insert(4, '_').toString();
        contentDetails.setName(newFileName);
        documentPage.save(contentDetails).render();
        ShareUser.openSiteDashboard(drone, siteName);

        items = ShareUserDashboard.searchSavedSearchDashlet(drone, ".txt");
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, newFileName));
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10351() throws Exception
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
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void ALF_10351() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String[] searchTerms = { "l'admissibilité", "l'admissibilite", "études", "etudes", "financière", "financiere" };

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items;

        // Loop through the search terms and verify the item is found
        for (String searchTerm : searchTerms)
        {
            items = ShareUserDashboard.searchSavedSearchDashlet(drone, searchTerm);
            Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));
        }
    }
}

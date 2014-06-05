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

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.ConfigureSavedSearchDialogBoxPage;
import org.alfresco.po.share.dashlet.SavedSearchDashlet;
import org.alfresco.po.share.dashlet.SearchLimit;
import org.alfresco.po.share.dashlet.SiteSearchItem;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.document.ContentDetails;
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
 * @author Ranjith Manyam
 */
@Listeners(FailedTestListener.class)
public class SavedSearchMyDashboardBasicTest extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(SavedSearchMyDashboardBasicTest.class);
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
    public void dataPrep_ALF_8650() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Saved Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SAVED_SEARCH);

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void ALF_8650() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        DashBoardPage dashBoardPage = (DashBoardPage) ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Verify Saved search Dashlet has been added to the Site dashboard
        Assert.assertNotNull(dashBoardPage);
        SavedSearchDashlet searchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        Assert.assertNotNull(searchDashlet);
        Assert.assertEquals(searchDashlet.getTitle(), Dashlets.SAVED_SEARCH.getDashletName());
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
    public void dataPrep_ALF_8651() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String contentPrefix = testName + "-Test-";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlets.SAVED_SEARCH);
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
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void ALF_8651() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String savedSearchTitle = "SiteDocs";
        String contentPrefix = testName + "-Test-";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Configure Saved Search dashlet with "Test", set the SearchLimit to 10
        // and verify Correct number of results displayed
        ShareUserDashboard.configureSavedSearch(drone, contentPrefix, savedSearchTitle, SearchLimit.TEN);

        SavedSearchDashlet searchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);

        List<SiteSearchItem> searchResults = searchDashlet.getSearchItems();
        for (SiteSearchItem result : searchResults)
        {
            Assert.assertTrue(result.getItemName().getDescription().startsWith(contentPrefix));
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
            Assert.assertTrue(result.getItemName().getDescription().startsWith(contentPrefix));
        }
        Assert.assertEquals(searchDashlet.getTitle(), savedSearchTitle);
        Assert.assertEquals(searchResults.size(), SearchLimit.TWENTY_FIVE.getValue());
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8652() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String contentPrefix = "Test-";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlets.SAVED_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Upload File
        String fileName = contentPrefix + getFileName(testName) + "-.txt";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void ALF_8652() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String searchTerm = "Test-";
        String savedSearchTitle = "SiteDocs";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        SavedSearchDashlet searchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);

        // Saved Search details before configure
        Assert.assertNotNull(searchDashlet);
        Assert.assertEquals(searchDashlet.getTitle(), Dashlets.SAVED_SEARCH.getDashletName());
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
        Assert.assertEquals(searchDashlet.getTitle(), Dashlets.SAVED_SEARCH.getDashletName());
        Assert.assertEquals(searchDashlet.getContent(), "No results found.");
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8653() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlets.SAVED_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Upload File
        String fileName = getFileName(testName) + "-.txt";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void ALF_8653() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String title = "£$%^&";
        String searchTerm = title;

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

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
    public void dataPrep_ALF_8668() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + "-.txt";
        String[] fileInfo = { fileName, DOCLIB };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlets.SAVED_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void ALF_8668() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName) + "-.txt";
        String searchTerm_Empty = "";
        String searchTerm_Spaces = "   ";
        String searchTerm_Valid = testName;

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
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

    // TODO - ALF-8669: Search Term - partially defined query
    // TODO - ALF-8670: Search Term - title (name)
    // TODO - ALF-8671: Search Term - text (description)
    // TODO - ALF-8672: Search Term - tag

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8673() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = testName + ".doc";
        String[] fileInfo = { fileName, DOCLIB };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlets.SAVED_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void ALF_8673() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = testName + ".doc";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Configure Saved search with file name "test"
        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, testName);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

        // Configure Saved search with file extension ".doc"
        items = ShareUserDashboard.searchSavedSearchDashlet(drone, "doc");
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));
    }

    // TODO - ALF-8674:Search Term - content

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8675() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = "12345" + getSiteName(testName);
        String folderName = testName + "-Folder";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlets.SAVED_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void ALF_8675() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String folderName = testName + "-Folder";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        // Configure Saved search with Folder Name
        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, folderName);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, folderName));
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8676() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        // Add Saved Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SAVED_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void ALF_8676() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + System.currentTimeMillis() + "_1.1.odt";
        String[] fileInfo = { fileName, DOCLIB };

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        // Create site and upload document
        ShareUser.openSiteDashboard(drone, siteName);
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);
        documentLibraryPage.getSiteNav().selectSiteDashBoard().render();

        // Wait till index
        Assert.assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_CONTENT_DASHLET, fileName, true, siteName));
        ShareUser.openUserDashboard(drone);
        ShareUserDashboard.configureSavedSearch(drone, ".odt");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        Assert.assertTrue(savedSearchDashlet.isItemFound(fileName));

        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, fileName);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

        DocumentDetailsPage detailsPage = items.get(0).getItemName().click().render();
        Assert.assertTrue(detailsPage instanceof DocumentDetailsPage);

        String newFileName = new StringBuffer(fileName).insert(4, '_').toString();
        EditDocumentPropertiesPage editDocumentPropertiesPage = detailsPage.selectEditProperties().render();
        editDocumentPropertiesPage.setName(newFileName);
        editDocumentPropertiesPage.selectSave().render();

        ShareUser.openUserDashboard(drone);

        items = ShareUserDashboard.searchSavedSearchDashlet(drone, ".odt", SearchLimit.HUNDRED);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, newFileName));
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8677() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB, "Les études de l'admissibilité de la solution dans cette société financière." };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlets.SAVED_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void ALF_8677() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName) + ".txt";
        String[] searchTerms = { "l'admissibilité", "l'admissibilite", "études", "etudes", "financière", "financiere" };

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        List<SiteSearchItem> items;
        // Loop through the search terms and verify the item is found
        for (String searchTerm : searchTerms)
        {
            items = ShareUserDashboard.searchSavedSearchDashlet(drone, searchTerm, SearchLimit.TWENTY_FIVE);
            Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));
        }
    }
}

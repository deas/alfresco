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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.po.share.dashlet.SavedSearchDashlet;
import org.alfresco.po.share.dashlet.SiteSearchItem;
import org.alfresco.po.share.enums.Dashlet;
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
 * @author Ranjith Manyam
 */
@Listeners(FailedTestListener.class)
public class SavedSearchSiteDashboardAdvancedTest extends AbstractTests
{

    private static Log logger = LogFactory.getLog(SavedSearchSiteDashboardAdvancedTest.class);
    private String siteDomain = "savedSearch.test";
    private static final String NO_RESULTS_FOUND_MESSAGE = "No results found.";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10352() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10352() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        List<String> searchTermsList = new ArrayList<String>();
        searchTermsList.add("modified: today");
        searchTermsList.add("modified: \"" + getDate("yyyy MM dd") + "\"");
        searchTermsList.add("modified: \"" + getDate("yyyy-MM-dd") + "\"");
        searchTermsList.add("modified: \"" + getDate("yyyy MMM dd") + "\"");
        searchTermsList.add("modified: \"" + getDate("yyyy-MMM-dd") + "\"");
        searchTermsList.add("modified: [" + getDate("yyyy-MM-dd") + " TO TODAY]");
        searchTermsList.add("modified: [" + getDate("yyyy-MM-dd") + " TO NOW]");

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload File
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);
        documentLibraryPage.getSiteNav().selectSiteDashBoard().render();
        // Add Saved Search Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);

        List<SiteSearchItem> items;
        SavedSearchDashlet searchDashlet;

        Assert.assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_CONTENT_DASHLET, fileName, true, siteName));

        for (String searchTerm : searchTermsList)
        {
            ShareUserDashboard.configureSavedSearch(drone, searchTerm);
            searchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
            items = searchDashlet.getSearchItems();
            Assert.assertEquals(items.size(), 1);
            Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
        }
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10353() throws Exception
    {
        String testName = getTestName();
        String firstName = testName;
        String userName = firstName + " " + DEFAULT_LASTNAME;
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String[] testUserInfo = new String[] { userName, firstName, DEFAULT_LASTNAME };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10353() throws Exception
    {
        String testName = getTestName();
        String firstName = testName;
        String userName = firstName + " " + DEFAULT_LASTNAME;
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // Login
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);

        ShareUser.openSiteDashboard(drone, siteName);

        List<String> searchTermsList = new ArrayList<String>();
        searchTermsList.add("modifier: " + userName);
        searchTermsList.add("modifier: " + firstName);
        searchTermsList.add("modifier: " + DEFAULT_LASTNAME);

        List<SiteSearchItem> items;
        SavedSearchDashlet searchDashlet;

        for (String searchTerm : searchTermsList)
        {
            ShareUserDashboard.configureSavedSearch(drone, searchTerm);
            searchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
            items = searchDashlet.getSearchItems();
            Assert.assertEquals(items.size(), 1);
            Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
        }
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10354() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName("file1");
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName("file2");
        contentDetails.setDescription("file1");
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10354() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        ShareUserDashboard.configureSavedSearch(drone, "file1");
        SavedSearchDashlet searchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 2);
        List<String> fileNames = Arrays.asList("file1", "file2");
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }

        ShareUserDashboard.configureSavedSearch(drone, "description: file1");
        searchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), "file2");

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10355() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = "file1";
        String fileName2 = "file2";
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

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10355() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = "file1";
        String fileName2 = "file2";
        String fileName3 = testName + "_10.10.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSiteDashboard(drone, siteName);

        // Search with "file1"
        ShareUserDashboard.configureSavedSearch(drone, fileName1);
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 2);
        List<String> fileNames = Arrays.asList("file1", "file2");
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }

        // Search with "name: file1"
        ShareUserDashboard.configureSavedSearch(drone, "name: " + fileName1);
        savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName1);

        // Search with "file3"
        ShareUserDashboard.configureSavedSearch(drone, fileName3);
        savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName3);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10356() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = "file1";
        String fileName2 = "file2";

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

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10356() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = "file1";
        String fileName2 = "file2";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        // Search with File name 1 ("file1")
        ShareUserDashboard.configureSavedSearch(drone, fileName1);
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 2);
        List<String> fileNames = Arrays.asList(fileName1, fileName2);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }

        // Search with Title contains File name 1 ("title: file1")
        ShareUserDashboard.configureSavedSearch(drone, "title: " + fileName1);
        savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName2);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10357() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = "file1";
        String fileName2 = "file2";

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
        detailsPage = propertiesPage.selectSave().render();

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10357() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = "file1";
        String fileName2 = "file2";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSiteDashboard(drone, siteName);

        ShareUserDashboard.configureSavedSearch(drone, fileName1);
        SavedSearchDashlet searchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName1);

        ShareUserDashboard.configureSavedSearch(drone, "author: " + testUser);
        searchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName2);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10358() throws Exception
    {
        String testName = getTestName();
        String firstName = testName;
        String siteName = getSiteName(testName);
        String userName = firstName + " " + DEFAULT_LASTNAME;
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String[] testUserInfo = new String[] { userName, firstName, DEFAULT_LASTNAME };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10358() throws Exception
    {
        String testName = getTestName();
        String firstName = testName;
        String userName = firstName + " " + DEFAULT_LASTNAME;
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        ShareUser.login(drone, userName, DEFAULT_PASSWORD);

        ShareUser.openSiteDashboard(drone, siteName);

        List<String> searchTerms = Arrays.asList("creator: " + userName, "creator: " + firstName, "creator: " + DEFAULT_LASTNAME);

        SavedSearchDashlet savedSearchDashlet;
        List<SiteSearchItem> items;

        for (String searchTerm : searchTerms)
        {
            ShareUserDashboard.configureSavedSearch(drone, searchTerm);
            savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
            items = savedSearchDashlet.getSearchItems();
            Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
        }

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10359() throws Exception
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

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10359() throws Exception
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

        ShareUserDashboard.configureSavedSearch(drone, fileName);
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 4);
        List<String> fileNames = Arrays.asList(fileName, fileName1, fileName2, fileName3);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10360() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10360() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = getFileName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);

        // Wait till file has been indexed
        Assert.assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_CONTENT_DASHLET, fileName, true, siteName));

        List<String> searchTerms = Arrays.asList("created: today",
                                                 "created: \"" + getDate("yyyy MM dd") + "\"",
                                                 "created: \"" + getDate("yyyy-MM-dd") + "\"",
                                                 "created: \"" + getDate("yyyy MMM dd") + "\"",
                                                 "created: \"" + getDate("yyyy-MMM-dd") + "\"",
                                                 "created: [" + getDate("yyyy-MM-dd")+ " TO TODAY]",
                                                 "created: [" + getDate("yyyy-MM-dd") + " TO NOW]");
        SavedSearchDashlet savedSearchDashlet;
        List<SiteSearchItem> items;

        for (String searchTerm : searchTerms)
        {
            ShareUserDashboard.configureSavedSearch(drone, searchTerm);
            savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
            items = savedSearchDashlet.getSearchItems();
            Assert.assertEquals(items.size(), 1);
            Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
        }
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10361() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create Folder
        ShareUserSitePage.createFolder(drone, testName, testName);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10361() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        ShareUserDashboard.configureSavedSearch(drone, "TYPE:\"cm:cmobject\"");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 2);
        List<String> fileNames = Arrays.asList(testName, fileName);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10362() throws Exception
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
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10362() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        String nodeRef = documentLibraryPage.getFileDirectoryInfo(fileName).getNodeRef();
        documentLibraryPage.getSiteNav().selectSiteDashBoard().render();

        ShareUserDashboard.configureSavedSearch(drone, "ID: \"" + nodeRef + "\"");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10363() throws Exception
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

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10363() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = "file1";
        String fileName2 = "file2";
        String fileName3 = "file3";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        ShareUserDashboard.configureSavedSearch(drone, "ASPECT: \"cm:" + DocumentAspect.VERSIONABLE.getValue() + "\"");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName1);

        ShareUserDashboard.configureSavedSearch(drone, "ASPECT: \"cm:" + DocumentAspect.TAGGABLE.getValue() + "\"");
        savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName2);

        // Changed to "generalclassifiable" as per MNT-10674
        ShareUserDashboard.configureSavedSearch(drone, "ASPECT: \"cm:" + "generalclassifiable" + "\"");
        savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName3);

        ShareUserDashboard.configureSavedSearch(drone, "ASPECT:*");
        savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 3);
        List<String> fileNames = Arrays.asList(fileName1, fileName2, fileName3);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10364() throws Exception
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

        // Create TXT File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(textFileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Uploading XML file.
        String[] xmlFileInfo = { xmlFileName };
        ShareUser.uploadFileInFolder(drone, xmlFileInfo);

        // Uploading HTML file.
        String[] htmlFileInfo = { htmlFileName };
        ShareUser.uploadFileInFolder(drone, htmlFileInfo);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10364() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String textFileName = "file1.txt";
        String xmlFileName = "note.xml";
        String htmlFileName = "heading.html";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        ShareUserDashboard.configureSavedSearch(drone, "content.mimetype:text\\/xml");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), xmlFileName);

        ShareUserDashboard.configureSavedSearch(drone, "content.mimetype:text\\/plain");
        savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), textFileName);

        ShareUserDashboard.configureSavedSearch(drone, "content.mimetype:text\\/html");
        savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), htmlFileName);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10365() throws Exception
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
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10365() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = "Content.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        ShareUserDashboard.configureSavedSearch(drone, "creator: \"admin\" and name: \"" + fileName + "\"");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 0);
        Assert.assertEquals(savedSearchDashlet.getContent(), NO_RESULTS_FOUND_MESSAGE);

        ShareUserDashboard.configureSavedSearch(drone, "creator: \"" + testUser + "\" and name: \"" + fileName + "\"");
        savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        ShareUserDashboard.configureSavedSearch(drone, "creator: \"admin\" or name: \"" + fileName + "\"");
        savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        ShareUserDashboard.configureSavedSearch(drone, "(creator: \"" + testUser + "\" and name: \"" + fileName + "\") and creator: \"admin\"");
        savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 0);
        Assert.assertEquals(savedSearchDashlet.getContent(), NO_RESULTS_FOUND_MESSAGE);

        ShareUserDashboard.configureSavedSearch(drone, "(creator: \"" + testUser + "\" and name: \"" + fileName + "\") or creator: \"admin\"");
        savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        ShareUserDashboard.configureSavedSearch(drone, "creator: \"" + testUser + "\" and (name: \"" + fileName + "\" or creator: \"admin\")");
        savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        ShareUser.logout(drone);
    }


    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10366() throws Exception
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
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10366() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = "Content.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        ShareUserDashboard.configureSavedSearch(drone, "KEYWORD(" + fileName + ")");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10367() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = "file1q.txt";
        String title = "Acequia";
        String description = "Quiz";
        String content = "Tequila";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create TXT File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setTitle(title);
        contentDetails.setDescription(description);
        contentDetails.setContent(content);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10367() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = "file1q.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        ShareUserDashboard.configureSavedSearch(drone, "ALL: \"*q*\"");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10368() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = "Content.txt";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        DocumentLibraryPage libraryPage = ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
        DocumentDetailsPage detailsPage = libraryPage.selectFile(fileName).render();
        SelectAspectsPage aspectsPage = detailsPage.selectManageAspects().render();
        List<DocumentAspect> aspects = Arrays.asList(DocumentAspect.DUBLIN_CORE);
        aspectsPage.add(aspects).render();
        aspectsPage.clickApplyChanges().render();

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10368() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = "Content.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        ShareUserDashboard.configureSavedSearch(drone, " ASPECT:\"{http://www.alfresco.org/model/content/1.0}dublincore\"");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10369() throws Exception
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
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10369() throws Exception
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
        searchTerms.add("="+fileName);
        searchTerms.add("\"1234\"");
        searchTerms.add("????");
        searchTerms.add("\"???4\"");

        SavedSearchDashlet savedSearchDashlet;
        List<SiteSearchItem> items;

        for (String searchTerm : searchTerms)
        {
            ShareUserDashboard.configureSavedSearch(drone, searchTerm);
            savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
            items = savedSearchDashlet.getSearchItems();
            Assert.assertEquals(items.size(), 1);
            Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
        }

        ShareUser.logout(drone);
    }



    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10370() throws Exception
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
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10370() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<String> searchTerms = new ArrayList<String>();
        searchTerms.add("{http://www.alfresco.org/model/content/1.0}name: "+fileName);
        searchTerms.add("@{http://www.alfresco.org/model/content/1.0}name: "+fileName);

        SavedSearchDashlet savedSearchDashlet;
        List<SiteSearchItem> items;

        for (String searchTerm : searchTerms)
        {
            ShareUserDashboard.configureSavedSearch(drone, searchTerm);
            savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
            items = savedSearchDashlet.getSearchItems();
            Assert.assertEquals(items.size(), 1);
            Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
        }

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10371() throws Exception
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
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10371() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        ShareUserDashboard.configureSavedSearch(drone, "cm_name = " + fileName);
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10372() throws Exception
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
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10372() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = "test.txt";
        String fileName2 = "tab.txt";
        String fileName3 = "alf.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        ShareUserDashboard.configureSavedSearch(drone, "+created:[MIN TO NOW] AND +modifier: (" + testUser + ") AND !test AND -(tab)");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName3);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10373() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName)+"-1.txt";
        String[] fileInfo1 = { fileName1 };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo1);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10373() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName)+"-1.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        ShareUserDashboard.configureSavedSearch(drone, "cm:initialVersion:true");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName1);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10374() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";
        String[] fileInfo1 = { fileName1 };
        String[] fileInfo2 = { fileName2 };
        String comment = getComment(fileName1);

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Upload two files and add comment to file1
        ShareUser.uploadFileInFolder(drone, fileInfo1);
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo2);
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName1).render();
        detailsPage.addComment(comment).render();

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10374() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        ShareUserDashboard.configureSavedSearch(drone, "fm:commentCount: 1");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName1);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10375() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";
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
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName1).render();
        EditTextDocumentPage textDocumentPage = detailsPage.selectInlineEdit().render();
        ContentDetails details = new ContentDetails();
        details.setName(fileName1);
        details.setContent(fileName1);
        textDocumentPage.save(details).render();

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10375() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);
        // Verify upon searching with "cm:versionLabel: 1.1", search results show File 1.
        ShareUserDashboard.configureSavedSearch(drone, "cm:versionLabel: 1.1");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName1);

        ShareUser.logout(drone);
    }


    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10376() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";
        String fileName3 = getFileName(testName)+"-3.txt";
        String[] fileInfo1 = { fileName1 };
        String[] fileInfo2 = { fileName2 };
        String[] fileInfo3 = { fileName3 };
        String comment = testName+" Comment.";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Upload 3 files
        ShareUser.uploadFileInFolder(drone, fileInfo1);
        ShareUser.uploadFileInFolder(drone, fileInfo2);
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo3);

        // Add one comment to File2
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName2).render();
        detailsPage = detailsPage.addComment(comment).render();
        // Add two comments to File3
        documentLibraryPage = detailsPage.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibraryPage.renderItem(maxWaitTime, fileName3).render();
        detailsPage = documentLibraryPage.selectFile(fileName3).render();
        detailsPage = detailsPage.addComment(comment).render();
        detailsPage = detailsPage.addComment(comment).render();

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10376() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";
        String fileName3 = getFileName(testName)+"-3.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        ShareUserDashboard.configureSavedSearch(drone, "fm:commentCount:1..2");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 2);
        List<String> fileNames = Arrays.asList(fileName2, fileName3);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10377() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";

        String file1Content = "big red apple";
        String file2Content = "big red tasty sweet apple";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Create Files
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setContent(file1Content);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        contentDetails.setContent(file2Content);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10377() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        ShareUserDashboard.configureSavedSearch(drone, "TEXT:(big * apple)");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 2);
        List<String> fileNames = Arrays.asList(fileName1, fileName2);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }

        ShareUserDashboard.configureSavedSearch(drone, "TEXT:(big *(1) apple)");
        savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName1);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10378() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName)+".txt";

        String fileContent = "big red apple";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Create Files
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setContent(fileContent);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10378() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName)+".txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        ShareUserDashboard.configureSavedSearch(drone, "\"big red apple\"^3");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10379() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";

        String file1Content = "this is an item";
        String file2Content = "this is the best item";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Create Files
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setContent(file1Content);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        contentDetails.setContent(file2Content);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10379() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        ShareUserDashboard.configureSavedSearch(drone, "\"this[^] item[$]\"");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 2);
        List<String> fileNames = Arrays.asList(fileName1, fileName2);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10380() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName)+".txt";

        String fileContent = "<type>d:text</type>";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Create Files
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setContent(fileContent);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10380() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName)+".txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        ShareUserDashboard.configureSavedSearch(drone, "\\<type\\>d\\:text\\</type\\>");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10381() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName)+".txt";

        String fileContent = "apple and banana";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Create Files
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setContent(fileContent);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10381() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName)+".txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        ShareUserDashboard.configureSavedSearch(drone, "TEXT:apple..banana");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertTrue(items.isEmpty());
        Assert.assertEquals(savedSearchDashlet.getContent(), NO_RESULTS_FOUND_MESSAGE);

        ShareUser.logout(drone);
    }
}

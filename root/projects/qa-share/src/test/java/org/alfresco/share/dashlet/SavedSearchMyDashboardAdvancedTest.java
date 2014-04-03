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
import org.alfresco.po.share.dashlet.SearchLimit;
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
 * @author Ranjith Manyam
 */
@Listeners(FailedTestListener.class)
public class SavedSearchMyDashboardAdvancedTest extends AbstractTests
{

    private static Log logger = LogFactory.getLog(SavedSearchMyDashboardAdvancedTest.class);
    private String siteDomain = "savedSearch.test";
    private static final String NO_RESULTS_FOUND_MESSAGE = "No results found.";
    private static final String LAST_NAME = "SSLastName";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8681() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        // Add Saved Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);

    }
    // QA-466
    @Test(groups = { "Enterprise4.2" })
    public void alf_8681() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        List<String> searchTermsList = new ArrayList<String>();
        searchTermsList.add("modified: today" + " and modifier: " + testUser);
        // TODO - MNT-10733
        searchTermsList.add("modified: \"" + getDate("yyyy-MM-dd") + "\"" +  " and modifier: " + testUser);
        searchTermsList.add("modified: \"" + getDate("yyyy-MMM-dd") + "\"" +  " and modifier: " + testUser);
        searchTermsList.add("modified: [" + getDate("yyyy-MM-dd") + " TO TODAY]" +  " and modifier: " + testUser);
        searchTermsList.add("modified: [" + getDate("yyyy-MM-dd") + " TO NOW]" +  " and modifier: " + testUser);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload File
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);
        SiteDashboardPage siteDashboardPage = documentLibraryPage.getSiteNav().selectSiteDashBoard().render();
        Assert.assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_CONTENT_DASHLET, fileName, true, siteName));

        siteDashboardPage.getNav().selectMyDashBoard().render();

        SavedSearchDashlet searchDashlet;

        for (String searchTerm : searchTermsList)
        {
            logger.info("Search Term: " + searchTerm);
            ShareUserDashboard.configureSavedSearch(drone, searchTerm, SearchLimit.HUNDRED);
            searchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
            Assert.assertTrue(searchDashlet.isItemFound(fileName));
        }
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8682() throws Exception
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
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_8682() throws Exception
    {
        String testName = getTestName();
        String firstName = testName;
        String userName = firstName + " " + LAST_NAME;
        String fileName = getFileName(testName) + ".txt";

        ShareUser.login(drone, userName, DEFAULT_PASSWORD);

        List<String> searchTermsList = new ArrayList<String>();
        searchTermsList.add("modifier: " + userName);
        searchTermsList.add("modifier: " + firstName);
        searchTermsList.add("modifier: " + LAST_NAME);

        SavedSearchDashlet searchDashlet;

        for (String searchTerm : searchTermsList)
        {
            ShareUserDashboard.configureSavedSearch(drone, searchTerm);
            searchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
            Assert.assertTrue(searchDashlet.isItemFound(fileName));
        }
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8683() throws Exception
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
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        contentDetails.setDescription(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_8683() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.configureSavedSearch(drone, fileName1);
        SavedSearchDashlet searchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 2);
        List<String> fileNames = Arrays.asList(fileName1, fileName2);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }

        ShareUserDashboard.configureSavedSearch(drone, "description: " + fileName1);
        searchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        Assert.assertTrue(searchDashlet.isItemFound(fileName2));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8684() throws Exception
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
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);

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

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_8684() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;
        String fileName3 = testName + "_10.10.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Search with "file1"
        ShareUserDashboard.configureSavedSearch(drone, fileName1);
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 2);
        List<String> fileNames = Arrays.asList(fileName1, fileName2);
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
    public void dataPrep_ALF_8685() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);
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

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_8685() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

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
    public void dataPrep_ALF_8686() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);

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

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_8686() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

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
    public void dataPrep_ALF_8687() throws Exception
    {
        String testName = getTestName();
        String firstName = testName;
        String siteName = getSiteName(testName);
        String userName = firstName + " " + LAST_NAME;
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String[] testUserInfo = new String[] { userName, firstName, LAST_NAME };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_8687() throws Exception
    {
        String testName = getTestName();
        String firstName = testName;
        String userName = firstName + " " + LAST_NAME;
        String fileName = getFileName(testName) + ".txt";

        ShareUser.login(drone, userName, DEFAULT_PASSWORD);

        List<String> searchTerms = Arrays.asList("creator: " + userName, "creator: " + firstName, "creator: " + LAST_NAME);

        SavedSearchDashlet savedSearchDashlet;

        for (String searchTerm : searchTerms)
        {
            ShareUserDashboard.configureSavedSearch(drone, searchTerm);
            savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
            Assert.assertTrue(savedSearchDashlet.isItemFound(fileName));
        }

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8693() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = "file" + testName;
        String fileName1 = "Content1" + testName;
        String fileName2 = "Content2" + testName;
        String fileName3 = "Content3" + testName;

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);

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


        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_8693() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = "file" + testName;
        String fileName1 = "Content1" + testName;
        String fileName2 = "Content2" + testName;
        String fileName3 = "Content3" + testName;

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

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
    public void dataPrep_ALF_8688() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        // Add Saved Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);
        ShareUser.logout(drone);

    }
    // QA-466
    @Test(groups = { "Enterprise4.2" })
    public void alf_8688() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = getFileName(testName) + System.currentTimeMillis();

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        DocumentLibraryPage documentLibraryPage = ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
        documentLibraryPage.getSiteNav().selectSiteDashBoard().render();

        // Wait till file has been indexed
        Assert.assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_CONTENT_DASHLET, fileName, true, siteName));

        List<String> searchTerms = new ArrayList<String>();
        searchTerms.add("created: today" +  " and modifier: " + testUser);
        searchTerms.add("created: \"" + getDate("yyyy-MM-dd") + "\"" +  " and modifier: " + testUser);
        searchTerms.add("created: \"" + getDate("yyyy-MMM-dd") + "\"" +  " and modifier: " + testUser);
        searchTerms.add("created: [" + getDate("yyyy-MM-dd")+ " TO TODAY]" +  " and modifier: " + testUser);
        searchTerms.add("created: [" + getDate("yyyy-MM-dd") + " TO NOW]" +  " and modifier: " + testUser);

        SavedSearchDashlet savedSearchDashlet;

        ShareUser.openUserDashboard(drone);

        // TODO - Issue raised : MNT-10733
        for (String searchTerm : searchTerms)
        {
            logger.info("Search Term: " + searchTerm);
            ShareUserDashboard.configureSavedSearch(drone, searchTerm, SearchLimit.HUNDRED);
            savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
            Assert.assertTrue(savedSearchDashlet.isItemFound(fileName));
        }
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8689() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);
        String folderName = getFolderName(testName);

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);
        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUser.logout(drone);
    }
    // QA-466
    @Test(groups = { "Enterprise4.2" })
    public void alf_8689() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName);
        String folderName = getFolderName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.configureSavedSearch(drone, "TYPE:\"cm:cmobject\"" +  " and modifier: " + testUser, SearchLimit.HUNDRED);
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        Assert.assertTrue(savedSearchDashlet.isItemFound(folderName));
        Assert.assertTrue(savedSearchDashlet.isItemFound(fileName));
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8690() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);
        String[] fileInfo = { fileName, DOCLIB };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_8690() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        String nodeRef = documentLibraryPage.getFileDirectoryInfo(fileName).getNodeRef();
        documentLibraryPage.getNav().selectMyDashBoard().render();

        ShareUserDashboard.configureSavedSearch(drone, "ID: \"" + nodeRef + "\"");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8691() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName("File1" + testName);
        String fileName2 = getFileName("File2" + testName);
        String fileName3 = getFileName("File3" + testName);

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);
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

        ShareUser.logout(drone);
    }
    // QA-466
    @Test(groups = { "Enterprise4.2" })
    public void alf_8691() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = getFileName("File1" + testName);
        String fileName2 = getFileName("File2" + testName);
        String fileName3 = getFileName("File3" + testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.configureSavedSearch(drone, "ASPECT: \"cm:" + DocumentAspect.VERSIONABLE.getValue() + "\"" +  " and modifier: " + testUser, SearchLimit.HUNDRED);
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName3));

        ShareUserDashboard.configureSavedSearch(drone, "ASPECT: \"cm:" + DocumentAspect.TAGGABLE.getValue() + "\"" +  " and modifier: " + testUser, SearchLimit.HUNDRED);
        savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        items = savedSearchDashlet.getSearchItems();
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName3));

        // Changed to "generalclassifiable" as per MNT-10674
        // ShareUserDashboard.configureSavedSearch(drone, "ASPECT: " + DocumentAspect.CLASSIFIABLE.getValue());
        ShareUserDashboard.configureSavedSearch(drone, "ASPECT: \"cm:" + "generalclassifiable" + "\"" +  " and modifier: " + testUser, SearchLimit.HUNDRED);
        savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        items = savedSearchDashlet.getSearchItems();
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName3));

        ShareUserDashboard.configureSavedSearch(drone, "ASPECT:*" + " and modifier: " + testUser, SearchLimit.HUNDRED);
        savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        items = savedSearchDashlet.getSearchItems();
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName3));
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8692() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String textFileName = "TextFile"+ testName + ".txt";
        String xmlFileName = "note"+ testName + ".xml";
        String htmlFileName = "heading" + testName + ".html";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);
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

        ShareUser.logout(drone);
    }
    // QA-466
    @Test(groups = { "Enterprise4.2" })
    public void alf_8692() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String textFileName = "TextFile"+ testName + ".txt";
        String xmlFileName = "note"+ testName + ".xml";
        String htmlFileName = "heading" + testName + ".html";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.configureSavedSearch(drone, "content.mimetype:text\\/xml" + " and modifier: " + testUser, SearchLimit.HUNDRED);
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), xmlFileName);

        ShareUserDashboard.configureSavedSearch(drone, "content.mimetype:text\\/plain" + " and modifier: " + testUser, SearchLimit.HUNDRED);
        savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), textFileName);

        ShareUserDashboard.configureSavedSearch(drone, "content.mimetype:text\\/html" + " and modifier: " + testUser, SearchLimit.HUNDRED);
        savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), htmlFileName);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8694() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);
        String[] fileInfo = { fileName };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_8694() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

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
    public void dataPrep_ALF_8695() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);
        String[] fileInfo = { fileName };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_8695() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.configureSavedSearch(drone, "KEYWORD(" + fileName + ")");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8696() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName("File1q" + testName);
        String title = "Acequia";
        String description = "Quiz";
        String content = "Tequila";
        String fileName2 = getFileName("File2" + testName).replace("q", "");
        String[] file2Info = { fileName2 };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);
        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create TXT File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setTitle(title);
        contentDetails.setDescription(description);
        contentDetails.setContent(content);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUser.uploadFileInFolder(drone, file2Info);

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_8696() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = getFileName("File1q" + testName);
        String fileName2 = getFileName("File2" + testName).replace("q", "");

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.configureSavedSearch(drone, "ALL: \"*q*\"");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8697() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        DocumentLibraryPage libraryPage = ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
        DocumentDetailsPage detailsPage = libraryPage.selectFile(fileName).render();
        SelectAspectsPage aspectsPage = detailsPage.selectManageAspects().render();
        List<DocumentAspect> aspects = Arrays.asList(DocumentAspect.DUBLIN_CORE);
        aspectsPage.add(aspects).render();
        aspectsPage.clickApplyChanges().render();

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_8697() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.configureSavedSearch(drone, " ASPECT:\"{http://www.alfresco.org/model/content/1.0}dublincore\"");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        Assert.assertTrue(savedSearchDashlet.isItemFound(fileName));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8698() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = "8698.txt";
        String[] fileInfo = { fileName };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUser.logout(drone);
    }
    // QA-466
    @Test(groups = { "Enterprise4.2" })
    public void alf_8698() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = "8698.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        List<String> searchTerms = new ArrayList<String>();
        searchTerms.add("86?8");
        searchTerms.add("*698");
        searchTerms.add("="+fileName);
        searchTerms.add("\"8698\"");
        searchTerms.add("????" + " and modifier: " + testUser);
        searchTerms.add("\"???8\"");

        SavedSearchDashlet savedSearchDashlet;

        for (String searchTerm : searchTerms)
        {
            logger.info("Search Term: " + searchTerm);
            ShareUserDashboard.configureSavedSearch(drone, searchTerm, SearchLimit.HUNDRED);
            savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
            Assert.assertTrue(savedSearchDashlet.isItemFound(fileName));
        }

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8699() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_8699() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName) + ".txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        List<String> searchTerms = new ArrayList<String>();
        searchTerms.add("{http://www.alfresco.org/model/content/1.0}name: "+fileName);
        searchTerms.add("@{http://www.alfresco.org/model/content/1.0}name: "+fileName);

        SavedSearchDashlet savedSearchDashlet;

        for (String searchTerm : searchTerms)
        {
            logger.info("Search Term: " + searchTerm);
            ShareUserDashboard.configureSavedSearch(drone, searchTerm);
            savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
            Assert.assertTrue(savedSearchDashlet.isItemFound(fileName));
        }

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8700() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_8700() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName) + ".txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.configureSavedSearch(drone, "cm_name = " + fileName);
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        Assert.assertTrue(savedSearchDashlet.isItemFound(fileName));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8701() throws Exception
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
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo1);
        ShareUser.uploadFileInFolder(drone, fileInfo2);
        ShareUser.uploadFileInFolder(drone, fileInfo3);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_8701() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = "test.txt";
        String fileName2 = "tab.txt";
        String fileName3 = "alf.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.configureSavedSearch(drone, "+created:[MIN TO NOW] AND +modifier: (" + testUser + ") AND !test AND -(tab)");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName3));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8702() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName)+".txt";
        String[] fileInfo = { fileName };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUser.logout(drone);
    }
    // TODO - Used "and name: " + testName
    @Test(groups = { "Enterprise4.2" })
    public void alf_8702() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName)+".txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.configureSavedSearch(drone, "cm:initialVersion:true" + "and modifier: " + testUser, SearchLimit.HUNDRED);
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        Assert.assertTrue(savedSearchDashlet.isItemFound(fileName));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8703() throws Exception
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

        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Upload two files and add comment to file1
        ShareUser.uploadFileInFolder(drone, fileInfo1);
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo2);
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName1).render();
        detailsPage.addComment(comment).render();

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_8703() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.configureSavedSearch(drone, "fm:commentCount: 1", SearchLimit.HUNDRED);
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8704() throws Exception
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
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);

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

        ShareUser.logout(drone);
    }
    // TODO - Added " and creator: " + testUser
    @Test(groups = { "Enterprise4.2" })
    public void alf_8704() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        // Verify upon searching with "cm:versionLabel: 1.1", search results show File 1.
        ShareUserDashboard.configureSavedSearch(drone, "cm:versionLabel: 1.1" + " and creator: " + testUser );
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

        ShareUser.logout(drone);
    }


    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8705() throws Exception
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
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);
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

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_8705() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";
        String fileName3 = getFileName(testName)+"-3.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.configureSavedSearch(drone, "fm:commentCount:1..2"+ " and creator: " + testUser, SearchLimit.HUNDRED);
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName3));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8706() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";

        String file1Content = "big red apple 8706";
        String file2Content = "big red tasty sweet apple 8706";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);

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

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_8706() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.configureSavedSearch(drone, "TEXT:(big * 8706)");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

        ShareUserDashboard.configureSavedSearch(drone, "TEXT:(big *(2) 8706)");
        savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        items = savedSearchDashlet.getSearchItems();
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8707() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName)+".txt";

        String fileContent = "big red apple 8707";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Create Files
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setContent(fileContent);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_8707() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName)+".txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.configureSavedSearch(drone, "\"big red apple 8707\"^3");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        Assert.assertTrue(savedSearchDashlet.isItemFound(fileName));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8708() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";

        String file1Content = "this is an item 8708";
        String file2Content = "this is the best item 8708";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);
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

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_8708() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.configureSavedSearch(drone, "\"this[^] 8708[$]\"");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 2);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8709() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName)+".txt";

        String fileContent = "<type>d:text8709</type>";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Create Files
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setContent(fileContent);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_8709() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName)+".txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.configureSavedSearch(drone, "\\<type\\>d\\:text8709\\</type\\>");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        Assert.assertTrue(savedSearchDashlet.isItemFound(fileName));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_8710() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName)+".txt";

        String fileContent = "apple and banana";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Create Files
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setContent(fileContent);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_8710() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.configureSavedSearch(drone, "TEXT:apple..banana");
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertTrue(items.isEmpty());
        Assert.assertEquals(savedSearchDashlet.getContent(), NO_RESULTS_FOUND_MESSAGE);

        ShareUser.logout(drone);
    }
}

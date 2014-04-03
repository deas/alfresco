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
import org.alfresco.po.share.site.document.DocumentLibraryPage;
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
public class SavedSearchMyDashboardAdvancedTest extends AbstractUtils
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

        List<SiteSearchItem> items;

        for (String searchTerm : searchTermsList)
        {
            logger.info("Search Term: " + searchTerm);
            items = ShareUserDashboard.searchSavedSearchDashlet(drone, searchTerm);
            Assert.assertEquals(items.size(), 1);
            Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));
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

        List<SiteSearchItem> items;

        for (String searchTerm : searchTermsList)
        {
            items = ShareUserDashboard.searchSavedSearchDashlet(drone, searchTerm);
            Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));
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
        ContentDetails contentDetails = new ContentDetails(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails(fileName2, null, fileName1, null);
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

        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, fileName1);
        Assert.assertEquals(items.size(), 2);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

        items = ShareUserDashboard.searchSavedSearchDashlet(drone, "description: "+ fileName1);
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

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

        ContentDetails contentDetails = new ContentDetails(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails(fileName2, null, fileName1, null);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails(fileName3);
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
        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, fileName1);
        Assert.assertEquals(items.size(), 2);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

        // Search with "name: file1"
        items = ShareUserDashboard.searchSavedSearchDashlet(drone, "name: " + fileName1);
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));

        // Search with "file3"
        items = ShareUserDashboard.searchSavedSearchDashlet(drone, fileName3);
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName3));

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
        ContentDetails contentDetails = new ContentDetails(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Create File2 with File1 as title
        contentDetails = new ContentDetails(fileName2, fileName1, null, null);
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
        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, fileName1);
        Assert.assertEquals(items.size(), 2);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

        // Search with Title contains File name 1 ("title: file1")
        items = ShareUserDashboard.searchSavedSearchDashlet(drone, "title: " + fileName1);
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

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
        String[] fileInfo1 = {fileName1};
        String[] fileInfo2 = {fileName2};

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.uploadFileInFolder(drone, fileInfo1);
        ShareUser.uploadFileInFolder(drone, fileInfo2);

        ShareUser.setAuthor(drone, fileName2, testUser);

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

        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, fileName1);
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));

        items = ShareUserDashboard.searchSavedSearchDashlet(drone, "author: " + testUser);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

        ShareUser.logout(drone);
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

        List<SiteSearchItem> items;
        for (String searchTerm : searchTerms)
        {
            items = ShareUserDashboard.searchSavedSearchDashlet(drone, searchTerm);
            Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));
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
        ContentDetails contentDetails = new ContentDetails(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails(fileName1, fileName, null, null);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails(fileName2, null, fileName, null);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails(fileName3, null, null, fileName);
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

        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, fileName);
        Assert.assertEquals(items.size(), 4);
        List<String> fileNames = Arrays.asList(fileName, fileName1, fileName2, fileName3);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }

        ShareUser.logout(drone);
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
        ContentDetails contentDetails = new ContentDetails(fileName);
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

        ShareUser.openUserDashboard(drone);

        List<SiteSearchItem> items;

        for (String searchTerm : searchTerms)
        {
            items = ShareUserDashboard.searchSavedSearchDashlet(drone, searchTerm);
            Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));
        }

        ShareUser.logout(drone);
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
        ContentDetails contentDetails = new ContentDetails(fileName);
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

        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, "TYPE:\"cm:cmobject\"" +  " and modifier: " + testUser);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, folderName));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

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

        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, "ID: \"" + nodeRef + "\"");
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));
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

        ContentDetails contentDetails = new ContentDetails(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails(fileName2);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails(fileName3);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUser.addAspects(drone, Arrays.asList(DocumentAspect.VERSIONABLE), fileName1);
        ShareUser.addAspects(drone, Arrays.asList(DocumentAspect.TAGGABLE), fileName2);
        ShareUser.addAspects(drone, Arrays.asList(DocumentAspect.CLASSIFIABLE), fileName3);

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

        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, "ASPECT: \"cm:" + DocumentAspect.VERSIONABLE.getValue() + "\"" +  " and modifier: " + testUser, SearchLimit.HUNDRED);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName3));

        items = ShareUserDashboard.searchSavedSearchDashlet(drone, "ASPECT: \"cm:" + DocumentAspect.TAGGABLE.getValue() + "\"" +  " and modifier: " + testUser, SearchLimit.HUNDRED);
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName3));

        // Changed to "generalclassifiable" as per MNT-10674
        // ShareUserDashboard.configureSavedSearch(drone, "ASPECT: " + DocumentAspect.CLASSIFIABLE.getValue());
        items = ShareUserDashboard.searchSavedSearchDashlet(drone, "ASPECT: \"cm:" + "generalclassifiable" + "\"" +  " and modifier: " + testUser, SearchLimit.HUNDRED);
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName3));

        items = ShareUserDashboard.searchSavedSearchDashlet(drone, "ASPECT:*" + " and modifier: " + testUser, SearchLimit.HUNDRED);
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

        // Upload TXT File
        String[] textFileInfo = { textFileName };
        ShareUser.uploadFileInFolder(drone, textFileInfo);

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

        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, "content.mimetype:text\\/xml" + " and modifier: " + testUser, SearchLimit.HUNDRED);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, xmlFileName));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, textFileName));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, htmlFileName));

        items = ShareUserDashboard.searchSavedSearchDashlet(drone, "content.mimetype:text\\/plain" + " and modifier: " + testUser, SearchLimit.HUNDRED);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, textFileName));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, htmlFileName));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, xmlFileName));

        items = ShareUserDashboard.searchSavedSearchDashlet(drone, "content.mimetype:text\\/html" + " and modifier: " + testUser, SearchLimit.HUNDRED);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, htmlFileName));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, xmlFileName));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, textFileName));

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

        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, "creator: \"admin\" and name: \"" + fileName + "\"");
        Assert.assertTrue(items.isEmpty());
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        Assert.assertEquals(savedSearchDashlet.getContent(), NO_RESULTS_FOUND_MESSAGE);

        items = ShareUserDashboard.searchSavedSearchDashlet(drone, "creator: \"" + testUser + "\" and name: \"" + fileName + "\"");
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

        items = ShareUserDashboard.searchSavedSearchDashlet(drone, "creator: \"admin\" or name: \"" + fileName + "\"");
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

        items = ShareUserDashboard.searchSavedSearchDashlet(drone, "(creator: \"" + testUser + "\" and name: \"" + fileName + "\") and creator: \"admin\"");
        Assert.assertTrue(items.isEmpty());
        savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        Assert.assertEquals(savedSearchDashlet.getContent(), NO_RESULTS_FOUND_MESSAGE);

        items = ShareUserDashboard.searchSavedSearchDashlet(drone, "(creator: \"" + testUser + "\" and name: \"" + fileName + "\") or creator: \"admin\"");
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

        items = ShareUserDashboard.searchSavedSearchDashlet(drone, "creator: \"" + testUser + "\" and (name: \"" + fileName + "\" or creator: \"admin\")");
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

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

        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, "KEYWORD(" + fileName + ")");
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

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
        ContentDetails contentDetails = new ContentDetails(fileName1, title, description, content);
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

        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, "ALL: \"*q*\"", SearchLimit.HUNDRED);
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

        // Create content
        ContentDetails contentDetails = new ContentDetails(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add aspect
        ShareUser.addAspects(drone, Arrays.asList(DocumentAspect.DUBLIN_CORE), fileName);

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_8697() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, " ASPECT:\"{http://www.alfresco.org/model/content/1.0}dublincore\"");
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

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

        List<SiteSearchItem> items;

        for (String searchTerm : searchTerms)
        {
            logger.info("Search Term: " + searchTerm);

            items = ShareUserDashboard.searchSavedSearchDashlet(drone, searchTerm, SearchLimit.HUNDRED);
            Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));
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

        List<SiteSearchItem> items;

        for (String searchTerm : searchTerms)
        {
            logger.info("Search Term: " + searchTerm);

            items = ShareUserDashboard.searchSavedSearchDashlet(drone, searchTerm, SearchLimit.HUNDRED);
            Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));
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

        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, "cm_name = " + fileName);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

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

        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, "+created:[MIN TO NOW] AND +modifier: (" + testUser + ") AND !test AND -(tab)");
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

        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, "cm:initialVersion:true" + "and modifier: " + testUser, SearchLimit.HUNDRED);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

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
        ShareUser.uploadFileInFolder(drone, fileInfo2);

        // Add Comment for File1
        ShareUser.addComment(drone, fileName1, comment);

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

        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, "fm:commentCount: 1", SearchLimit.HUNDRED);
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
        documentLibraryPage.selectFile(fileName1).render();
        ShareUser.editTextDocument(drone, fileName1, fileName1, fileName1);

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
        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, "cm:versionLabel: 1.1" + " and creator: " + testUser);
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
        ShareUser.uploadFileInFolder(drone, fileInfo3);

        // Add one comment to File2
        ShareUser.addComment(drone, fileName2, comment);
        // Add two comments to File3
        ShareUser.addComment(drone, fileName3, comment);
        ShareUser.addComment(drone, fileName3, comment);

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

        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, "fm:commentCount:1..2"+ " and creator: " + testUser, SearchLimit.HUNDRED);
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
        String[] fileInfo1 = {fileName1, DOCLIB, file1Content};
        String[] fileInfo2 = {fileName2, DOCLIB, file2Content};

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Create Files
        ShareUser.uploadFileInFolder(drone, fileInfo1);
        ShareUser.uploadFileInFolder(drone, fileInfo2);

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

        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, "TEXT:(big * 8706)");
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

        items = ShareUserDashboard.searchSavedSearchDashlet(drone, "TEXT:(big *(2) 8706)");
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
        String[] fileInfo = {fileName, DOCLIB, fileContent};

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Create Files
        ShareUser.uploadFileInFolder(drone, fileInfo);

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_8707() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName)+".txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, "\"big red apple 8707\"^3");
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

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
        String[] fileInfo1 = {fileName1, DOCLIB, file1Content};
        String[] fileInfo2 = {fileName2, DOCLIB, file2Content};

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Create Files
        ShareUser.uploadFileInFolder(drone, fileInfo1);
        ShareUser.uploadFileInFolder(drone, fileInfo2);

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

        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, "\"this[^] 8708[$]\"");
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
        String[] fileInfo = {fileName, DOCLIB, fileContent};

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_8709() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName)+".txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, "\\<type\\>d\\:text8709\\</type\\>");
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

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
        String[] fileInfo = {fileName, DOCLIB, fileContent};

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlet.SAVED_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_8710() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        List<SiteSearchItem> items = ShareUserDashboard.searchSavedSearchDashlet(drone, "TEXT:apple..banana");
        Assert.assertTrue(items.isEmpty());
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        Assert.assertEquals(savedSearchDashlet.getContent(), NO_RESULTS_FOUND_MESSAGE);

        ShareUser.logout(drone);
    }
}

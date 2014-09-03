/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.share.sanity;

import static org.testng.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.po.share.search.SearchResult;
import org.alfresco.po.share.search.SortType;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.share.search.SearchKeys;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSearchPage;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Class includes: Tests from TestLink in Area: Sanity Tests
 * <ul>
 * <li>Test searches using various Properties, content, Folder.</li>
 * </ul>
 * 
 * @author Abhijeet Bharade
 */
@Listeners(FailedTestListener.class)
public class AdvanceSearchSanityTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(AdvanceSearchSanityTest.class);

    Date todayDate = new Date();

    protected String testUser;

    protected String siteName = "";

    private String htmlFileName;

    private static final String TEST_HTML_FILE = "Test1.html";

    /**
     * Class includes: Tests from TestLink in Area: Advanced Search Tests
     * <ul>
     * <li>Test searches using various Properties, content, Folder</li>
     * </ul>
     */
    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Starting Tests: " + testName);
    }

    /**
     * Enterprise40x-6553:Search for items
     * <ul>
     * <li>Search for Content using Keywords search criteria</li>
     * <li>Search for Content using Name search criteria</li>
     * <li>Search for Content using Title search criteria</li>
     * <li>Search for Content using Description search criteria</li>
     * <li>Search for Content using Mimetype search criteria</li>
     * <li>Search for Content using Modified Date search criteria</li>
     * <li>Search for Content using Modifier search criteria</li>
     * <li>Click View in Browser icon</li>
     * <li>Click Download icon</li>
     * <li>Search for Folder using Keywords search criteria</li>
     * <li>Search for Folder using Name search criteria</li>
     * <li>Search for Folder using Title search criteria</li>
     * <li>Search for Folder using Description search criteria</li>
     * <li>Click on folder's path</li>
     * <li>Sort the found items by any criteria</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = {"Sanity","Search"})
    public void enterprise40x_6553() throws Exception
    {
        testName = getTestName();
        String searchTerm = testName + "-" + System.currentTimeMillis();
        Boolean searchOk;
        
        String testUser = getUserNameFreeDomain(searchTerm);
        String siteName = getSiteName(searchTerm);
        String folderName = getFolderName(searchTerm + "-1");
        String folderName2 = getFolderName(searchTerm + "-2");
        String fileName = getFileName(searchTerm);
        
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create a Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();
        
        // Open Document library
        ShareUser.openDocumentLibrary(drone);
        
        // Create 2 Folder
        ShareUserSitePage.createFolder(drone, folderName, folderName+"Title1", folderName+"Desc1");
        ShareUserSitePage.createFolder(drone, folderName2, folderName+"Title2", folderName+"Desc2");
        
        // Create 2 Documents     
        for (int i = 0; i < 2; i++)
        {            
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(fileName + "-Name-0" + i);
            contentDetails.setTitle(fileName + "-Title-0" + i);
            contentDetails.setDescription(fileName + "-Desc-0" + i);
            
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
        }
        htmlFileName = fileName + "-Name-0" + TEST_HTML_FILE;
        ShareUser.uploadFileInFolder(drone, new String[] { htmlFileName });

        /** Start Test */

        // Searching and sorting the content items
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        Map<String, String> keyWordSearchText = new HashMap<String, String>();       

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        String todaysDate = dateFormat.format(cal.getTime());
        cal.add(Calendar.DATE, -1);

        try        
        {
            keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchTerm);
            List<SearchResult> searchResults = null;
            // Content Search with keyword: common part of filename
            searchResults = ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            // Check the Search Results: Search with Retry for Solr
            assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, fileName + "-Name-01", true),
                    "Incorrect Search Results for: Keyword search- " + searchTerm + " file1 not displayed");

            searchOk = ShareUserSearchPage.isSearchItemAvailable(drone, fileName + "-Name-00");
            Assert.assertTrue(searchOk, "Incorrect Search Results for Name search- " + searchTerm + ". Result item - " + fileName + "-Name-00" + " not found");

            keyWordSearchText.clear();

            // Searching for valid Name.
            searchTerm = "-Name-00";
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchTerm);
            searchResults = ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            searchOk = ShareUserSearchPage.isSearchItemAvailable(drone, fileName + searchTerm);
            Assert.assertTrue(searchOk, "Incorrect Search Results for Name search- " + searchTerm);
            
            keyWordSearchText.clear();
            
            // Searching for valid Title string
            searchTerm = "-Title-01";
            keyWordSearchText.put(SearchKeys.TITLE.getSearchKeys(), searchTerm);
            searchResults = ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            searchOk = ShareUserSearchPage.isSearchItemAvailable(drone, fileName + "-Name-01");
            Assert.assertTrue(searchOk, "Incorrect Search Results for Name search- " + searchTerm);

            keyWordSearchText.clear();

            // Searching for valid Description string
            searchTerm = "-Desc-01";
            keyWordSearchText.put(SearchKeys.DESCRIPTION.getSearchKeys(), searchTerm);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            searchOk = ShareUserSearchPage.isSearchItemAvailable(drone, fileName + "-Name-01");
            Assert.assertTrue(searchOk, "Incorrect Search Results for Name search- " + searchTerm);

            keyWordSearchText.clear();

            // Searching for valid MODIFIERTODATE string
            keyWordSearchText.put(SearchKeys.MODIFIERTODATE.getSearchKeys(), todaysDate);
            keyWordSearchText.put(SearchKeys.MODIFIERFROMDATE.getSearchKeys(), todaysDate);
            searchResults = ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            assertTrue(searchResults.size() > 0, "There should be search results for to date - " + todaysDate);
            searchOk = ShareUserSearchPage.isSearchItemAvailable(drone, fileName + "-Name-01");
            Assert.assertTrue(searchOk, "Incorrect Search Results for Name search- " + keyWordSearchText + ". Result item - " + fileName + "-Name-00");

            keyWordSearchText.clear();

            // Searching for valid Name and Mime type.
            keyWordSearchText.put(SearchKeys.MIME.getSearchKeys(), "HTML");
            searchResults = ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            assertTrue(searchResults.size() > 0, "There should be search results for html MIME TYPE.");
            searchOk = ShareUserSearchPage.isSearchItemAvailable(drone, htmlFileName);
            Assert.assertTrue(searchOk, "Incorrect Search Results for Name search- " + keyWordSearchText + ". Result item - " + fileName + "-Name-00"
                    + " not found");

            searchResults.clear();

            keyWordSearchText.clear();

            // Searching for valid Name and Mime type.
            keyWordSearchText.put(SearchKeys.MODIFIER.getSearchKeys(), testUser);
            searchResults = ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            assertTrue(searchResults.size() > 0, "There should be search results for " + testUser + " as modifier.");
            searchOk = ShareUserSearchPage.isSearchItemAvailable(drone, fileName + "-Name-00");
            Assert.assertTrue(searchOk, "Incorrect Search Results for Name search- " + keyWordSearchText + ". Result item - " + fileName + "-Name-00"
                    + " not found");

            searchResults.clear();

            keyWordSearchText.clear();

            // Folder Search: Searching with keyword
            searchInfo = Arrays.asList(ADV_FOLDER_SEARCH);

            searchTerm = folderName;
            keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchTerm);
            searchResults = ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            searchOk = ShareUserSearchPage.isSearchItemAvailable(drone, folderName);
            Assert.assertTrue(searchOk, "Incorrect Search Results for Name search- " + searchTerm + ". Result item - " + fileName + "-Name-00" + " not found");

            keyWordSearchText.clear();

            // Searching for valid Name.
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchTerm);
            searchResults = ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            assertTrue(searchResults.size() > 0, "There should be search results for name on folder.");
            searchOk = ShareUserSearchPage.isSearchItemAvailable(drone, folderName);
            Assert.assertTrue(searchOk, "Incorrect Search Results for Name search- " + searchTerm + ". Result item - " + fileName + "-Name-00" + " not found");

            keyWordSearchText.clear();

            // Searching for valid Title string
            keyWordSearchText.put(SearchKeys.TITLE.getSearchKeys(), searchTerm);
            searchResults = ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            assertTrue(searchResults.size() > 0, "There should be search results for title on folder.");
            searchOk = ShareUserSearchPage.isSearchItemAvailable(drone, folderName);
            Assert.assertTrue(searchOk, "Incorrect Search Results for Name search- " + searchTerm + ". Result item - " + folderName + " not found");
            keyWordSearchText.clear();

            // Searching for valid Description string
            keyWordSearchText.put(SearchKeys.DESCRIPTION.getSearchKeys(), searchTerm);
            searchResults = ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            assertTrue(searchResults.size() > 0, "There should be search results for description on folder.");
            searchOk = ShareUserSearchPage.isSearchItemAvailable(drone, folderName);
            Assert.assertTrue(searchOk, "Incorrect Search Results for Name search- " + searchTerm + ". Result item - " + folderName + " not found");

            // Sorting results by Name
            List<SearchResult> sortedByName = ShareUserSearchPage.sortSearchResults(drone, SortType.NAME);

            List<SearchResult> expectedResultsSortedByName = new ArrayList<SearchResult>(sortedByName);
            Collections.sort(expectedResultsSortedByName, new SortedSearchResultItemByName());

            // Check Sort order
            for (int i = 0; (i < 5 && i < expectedResultsSortedByName.size()); i++)
            {
                assertTrue(expectedResultsSortedByName.get(i).getTitle().equalsIgnoreCase(sortedByName.get(i).getTitle()),
                        "The results are not sorted as expected - " + expectedResultsSortedByName.get(i).getTitle() + " - " + sortedByName.get(i).getTitle());
            }
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            testCleanup(drone, testName);
        }

    }

    /**
     * Comparator for sorting SearchResultItem by name.
     * @author abharade
     *
     */
    private class SortedSearchResultItemByName implements Comparator<SearchResult>
    {
        public int compare(SearchResult item1, SearchResult item2)
        {
            return StringUtils.substringBefore(item1.getTitle(), ".").compareTo(StringUtils.substringBefore(item2.getTitle(), "."));
        }

    }
}
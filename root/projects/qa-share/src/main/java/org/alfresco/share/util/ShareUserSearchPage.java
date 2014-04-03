package org.alfresco.share.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.alfresco.share.search.SearchKeys;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.search.AdvanceSearchCRMPage;
import org.alfresco.po.share.search.AdvanceSearchContentPage;
import org.alfresco.po.share.search.AdvanceSearchFolderPage;
import org.alfresco.po.share.search.AdvanceSearchPage;
import org.alfresco.po.share.search.SearchResultItem;
import org.alfresco.po.share.search.SearchResultsPage;
import org.alfresco.po.share.search.SortType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ShareUserSearchPage extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(ShareUser.class);

    public ShareUserSearchPage()
    {
        if (logger.isTraceEnabled())
        {
            logger.debug(this.getClass().getSimpleName() + " instantiated");
        }
    }

    /**
     * Helper to Perform Search from DashBoard or Search Results Page.
     * 
     * @param driver WebDriver Instance
     * @param searchTerm String
     * @param searchAllSites Boolean <tt>false</tt> indicates search is restricted to opened site,: When <tt>true</tt>, opens dashboard before search, so as to
     * search all sites
     * @return searchResults
     */
    public static List<SearchResultItem> basicSearch(WebDrone driver, String searchTerm, Boolean searchAllSitesFromMyDashBoard)
    {
        SharePage sharePage;

        if (searchAllSitesFromMyDashBoard)
        {
            // Open User DashBoard
            sharePage = ShareUser.openUserDashboard(driver);
        }

        sharePage = ShareUser.getSharePage(driver);

        // Search
        SharePage searchResultsPage = sharePage.getSearch().search(searchTerm).render();
        SearchResultsPage searchResults = searchResultsPage.render(refreshDuration);

        // Sort By: TODO Later

        // Get Results
        List<SearchResultItem> searchOutput = searchResults.getResults();

        return searchOutput;
    }

    /**
     * Helper to identify if the entryToBeFound is found in the <searchResultSet>.
     * 
     * @param driver WebDrone Instance
     * @param entryToBeFound String Case in-sensitive value to look for
     * @return Boolean <tt>true</tt> if entryToBeFound is found in the search results, <tt>false</tt> if not
     */
    public static Boolean isSearchItemAvailable(WebDrone driver, String entryToBeFound)
    {
        try
        {
            SearchResultItem searchEntry = findInSearchResults(driver, entryToBeFound);

            if (searchEntry == null)
            {
                if (entryToBeFound.equals(SERACH_ZERO_CONTENT))
                {
                    return true;
                }
                return false;
            }
        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }

    /**
     * Helper to search for the entryToBeFound in the <searchResultSet>.
     * 
     * @param driver WebDrone Instance
     * @param entryToBeFound String Case in-sensitive value to look for
     * @return SearchResultItem if found, else null
     */
    public static SearchResultItem findInSearchResults(WebDrone driver, String entryToBeFound)
    {
        int position = -1;
        Boolean moreResultPages = true;
        SearchResultItem searchEntry = null;

        // Assumes User is on search Results Page
        SearchResultsPage searchResults = (SearchResultsPage) ShareUser.getSharePage(driver);

        // Check if results are available
        if (!searchResults.hasResults())
        {
            return searchEntry;
        }

        // Start from first page
        while (searchResults.hasPrevioudPage())
        {
            SearchResultsPage searchResultsPage = (SearchResultsPage) searchResults.selectPreviousPage();
            searchResultsPage.render();
        }

        while (moreResultPages)
        {
            // Get Search Results
            List<SearchResultItem> searchOutput = searchResults.getResults();

            for (SearchResultItem item : searchOutput)
            {
                position = position + 1;

                if (item.getTitle().contains(entryToBeFound))
                {
                    searchEntry = item;
                    return searchEntry;
                }
            }

            // Check next Page if available
            moreResultPages = searchResults.hasNextPage();

            if (moreResultPages)
            {
                SearchResultsPage searchResultsPage = (SearchResultsPage) searchResults.selectNextPage();
                searchResultsPage.render();
            }
        }
        return searchEntry;
    }

    /**
     * Helper to check the search results for a SearchTerm on the SearchResultsPage, with configurable retrySearchCount.
     * 
     * @param driver WebDrone Instance
     * @param searchType String Type of search to be performed during retry
     * @param searchTerm String Term used in the search box
     * @param entryToBeFound String entry to look for in the search results
     * @param entryVisible Boolean <tt>true</tt> if we are expecting the entryToBeFound to be visible in searchResults
     * @return <tt>true</tt> if element is found when expected, <tt>false</tt> if found when not expected
     */
    public static Boolean checkSearchResultsWithRetry(WebDrone driver, String searchType, String searchTerm, String entryToBeFound, Boolean entryVisible)
    {
        Boolean found = false;
        Boolean resultAsExpected = false;

        // Code to repeat search until the element is found or Timeout is hit
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            found = isSearchItemAvailable(driver, entryToBeFound);

            // Loop again if result is not as expected: To cater for Solr lag:
            // Eventual consistency
            resultAsExpected = (entryVisible.equals(found));
            if (resultAsExpected)
            {
                break;
            }
            else
            {
                logger.info("Search for: " + searchTerm + " : Entry not found: " + entryToBeFound);
                webDriverWait(driver, refreshDuration);

                repeatSearch(driver, searchType, searchTerm);
            }
        }

        if (!resultAsExpected)
        {
            found = isSearchItemAvailable(driver, entryToBeFound);
            resultAsExpected = (entryVisible.equals(found));
        }

        return resultAsExpected;

    }

    /**
     * Advance Search - Based on the Search Info text it will perform Content Search or Folder Search
     * 
     * @param driver
     * @param info - Contains the Search Type , and where to Search (Site or Network level)
     * @param keyWordSearchText - Different Search types.
     * @return list of Search Results.
     * @throws Exception
     * @throws PageException
     */
    public static List<SearchResultItem> advanceSearch(WebDrone driver, List<String> info, Map<String, String> keyWordSearchText) throws PageException,
            Exception
    {
        SearchResultsPage searchResults = null;
        // loadAdvanceSearch method will return content search page or folder
        // TODO: subs: Why is this render necessary again?
        AdvanceSearchPage advanceSearchPage = navigateToAdvanceSearch(driver, info).render();

        if (keyWordSearchText.get(SearchKeys.KEYWORD.getSearchKeys()) != null)
        {
            advanceSearchPage.inputKeyword(keyWordSearchText.get(SearchKeys.KEYWORD.getSearchKeys()));
        }
        if (keyWordSearchText.get(SearchKeys.TITLE.getSearchKeys()) != null)
        {
            advanceSearchPage.inputTitle(keyWordSearchText.get(SearchKeys.TITLE.getSearchKeys()));
        }
        if (keyWordSearchText.get(SearchKeys.NAME.getSearchKeys()) != null)
        {
            advanceSearchPage.inputName(keyWordSearchText.get(SearchKeys.NAME.getSearchKeys()));
        }
        if (keyWordSearchText.get(SearchKeys.DESCRIPTION.getSearchKeys()) != null)
        {
            advanceSearchPage.inputDescription(keyWordSearchText.get(SearchKeys.DESCRIPTION.getSearchKeys()));
        }
        if (keyWordSearchText.get(SearchKeys.MIME.getSearchKeys()) != null)
        {
            advanceSearchPage.selectMimeType(keyWordSearchText.get(SearchKeys.MIME.getSearchKeys()));
        }
        if (keyWordSearchText.get(SearchKeys.MODIFIERFROMDATE.getSearchKeys()) != null)
        {
            advanceSearchPage.inputFromDate(keyWordSearchText.get(SearchKeys.MODIFIERFROMDATE.getSearchKeys()));
        }
        if (keyWordSearchText.get(SearchKeys.MODIFIERTODATE.getSearchKeys()) != null)
        {
            advanceSearchPage.inputToDate(keyWordSearchText.get(SearchKeys.MODIFIERTODATE.getSearchKeys()));
        }
        if (keyWordSearchText.get(SearchKeys.MODIFIER.getSearchKeys()) != null)
        {
            advanceSearchPage.inputModifier(keyWordSearchText.get(SearchKeys.MODIFIER.getSearchKeys()));
        }
        searchResults = advanceSearchPage.clickSearch().render(refreshDuration);
        List<SearchResultItem> searchOutput = searchResults.getResults();

        return searchOutput;
    }

    /**
     * This method will allow to override the advance search for retry.
     * This will initiate the search again on the same page and returns the Search Results Page.
     * @param driver
     * @param searchType
     * @param searchTerm
     * @return {@link SearchResultsPage}
     */
    public static SearchResultsPage repeatSearch(WebDrone driver, String searchType, String searchTerm)
    {
        SearchResultsPage searchResults = (SearchResultsPage) ShareUser.getSharePage(driver);
        if (searchType.equals(BASIC_SEARCH))
        {
            searchResults.doSearch(searchTerm).render();
        }
        else if (searchType.equals(ADV_FOLDER_SEARCH))
        {
            logger.info("Folder Search retry");
            AdvanceSearchFolderPage folderSearchPage = searchResults.goBackToAdvanceSearch().render();
            searchResults = folderSearchPage.clickSearch().render(refreshDuration);
        }
        else
        {
            logger.info("Content Search retry");
            AdvanceSearchContentPage contentSearchPage = searchResults.goBackToAdvanceSearch().render();
            searchResults = contentSearchPage.clickSearch().render(refreshDuration);
        }

        return searchResults;
    }

    /**
     * This method loads the Advance search Page.
     * 
     * @param driver
     * @param info
     * @return {@link AdvanceSearchPage}
     * @throws PageException
     * @throws Exception
     */
    public static AdvanceSearchPage navigateToAdvanceSearch(WebDrone driver, List<String> info) throws PageException, Exception
    {
        SharePage sharePage;
        if (info == null)
        {
            info = Arrays.asList(ADV_FOLDER_SEARCH);
        }
        
        if (info.contains("searchAllSitesFromMyDashBoard"))
        {
            // Open User DashBoard
            // TODO: Subs: Is this render necessary since the page is already rendered.
            sharePage = ShareUser.openUserDashboard(driver);
        }
        sharePage = ShareUser.getSharePage(driver);
        AdvanceSearchContentPage contentSearchPage = sharePage.getNav().selectAdvanceSearch().render();
        if (info.contains(ADV_CONTENT_SEARCH))
        {
            return (contentSearchPage);
        }
        else
        {
            AdvanceSearchFolderPage folderSearchPage = contentSearchPage.searchLink(SearchKeys.FOLDERS.getSearchKeys()).render();
            return (folderSearchPage);
        }

    }

    /**
     * This method does the clicking on search button present on Advance search.
     * User should be logged in and on the Advance Search Page
     * @param driver
     * @return SearchResultsPage
     * @throws Exception
     */
    public static SearchResultsPage clickSearchOnAdvanceSearch(WebDrone driver)
    {
        AdvanceSearchPage searchPage = (AdvanceSearchPage) ShareUser.getSharePage(driver);
        SearchResultsPage shareResultsPage = (SearchResultsPage) searchPage.clickSearch();
        return shareResultsPage.render();
    }

    /**
     * This method does the Advance search results sorting by given sort item.
     * 
     * @param driver
     * @param sortItem
     * @return List<SearchResultItem>
     * @throws Exception
     */
    // TODO: Chiran: Update JAVA Docs to say starting point is search results Page (user has performed a search)
    public static List<SearchResultItem> sortSearchResults(WebDrone driver, SortType sortItem) throws Exception
    {        
        if (sortItem == null)
        {
            throw new UnsupportedOperationException("sortItem object is required");
        }
        
        SearchResultsPage searchResults = (SearchResultsPage) ShareUser.getSharePage(driver);
        searchResults = (SearchResultsPage) searchResults.sortPage(sortItem);
        searchResults.render();
        return searchResults.getResults();
    }
    
    /**
     * Advance Search - It will perform CRM Attachments Search 
     * User should be logged in.
     * 
     * @param driver
     * @param keyWordSearchText - Different Search types.
     * @return list of Search Results.
     * @throws Exception
     * @throws PageException
     */
    public static List<SearchResultItem> advanceSearchForCRM(WebDrone driver, Map<String, String> keyWordSearchText) throws PageException, Exception
    {
        SearchResultsPage searchResults = null;
        //TODO: Chiran: Amend and use navigateToAdvanceSearch to navigate to appropriate search form

        // Open User DashBoard
        SharePage sharePage = ShareUser.openUserDashboard(driver);
        AdvanceSearchContentPage contentSearchPage = sharePage.getNav().selectAdvanceSearch().render();
        AdvanceSearchCRMPage crmSearchPage = contentSearchPage.searchLink(SearchKeys.CRM_SEARCH.getSearchKeys()).render();

        if (keyWordSearchText.get(SearchKeys.KEYWORD.getSearchKeys()) != null)
        {
            crmSearchPage.inputKeyword(keyWordSearchText.get(SearchKeys.KEYWORD.getSearchKeys()));
        }
        if (keyWordSearchText.get(SearchKeys.ACCOUNT_IDENTIFIER.getSearchKeys()) != null)
        {
            crmSearchPage.inputCrmAccountId(keyWordSearchText.get(SearchKeys.ACCOUNT_IDENTIFIER.getSearchKeys()));
        }
        if (keyWordSearchText.get(SearchKeys.ACCOUNT_NAME.getSearchKeys()) != null)
        {
            crmSearchPage.inputCrmAccountName(keyWordSearchText.get(SearchKeys.ACCOUNT_NAME.getSearchKeys()));
        }
        if (keyWordSearchText.get(SearchKeys.OPPORTUNITY_NAME.getSearchKeys()) != null)
        {
            crmSearchPage.inputCrmOpporName(keyWordSearchText.get(SearchKeys.OPPORTUNITY_NAME.getSearchKeys()));
        }
        if (keyWordSearchText.get(SearchKeys.CONTRACT_NUMBER.getSearchKeys()) != null)
        {
            crmSearchPage.inputCrmContractNumber(keyWordSearchText.get(SearchKeys.CONTRACT_NUMBER.getSearchKeys()));
        }
        if (keyWordSearchText.get(SearchKeys.CONTRACT_NAME.getSearchKeys()) != null)
        {
            crmSearchPage.inputCrmContractName(keyWordSearchText.get(SearchKeys.CONTRACT_NAME.getSearchKeys()));
        }
        if (keyWordSearchText.get(SearchKeys.CASE_NUMBER.getSearchKeys()) != null)
        {
            crmSearchPage.inputCrmCaseNumber(keyWordSearchText.get(SearchKeys.CASE_NUMBER.getSearchKeys()));
        }
        if (keyWordSearchText.get(SearchKeys.CASE_NAME.getSearchKeys()) != null)
        {
            crmSearchPage.inputCrmCaseName(keyWordSearchText.get(SearchKeys.CASE_NAME.getSearchKeys()));
        }

        searchResults = crmSearchPage.clickSearch().render(refreshDuration);
        List<SearchResultItem> searchOutput = searchResults.getResults();

        return searchOutput;
    }
}
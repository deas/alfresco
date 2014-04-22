package org.alfresco.share.util;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.search.LiveSearchDocumentResult;
import org.alfresco.po.share.search.LiveSearchDropdown;
import org.alfresco.po.share.search.LiveSearchPeopleResult;
import org.alfresco.po.share.search.LiveSearchSiteResult;
import org.alfresco.po.share.search.SearchBox;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.log4j.Logger;

/**
 * Utility with helper methods for live search
 * 
 * @author jcule
 *
 */
public class ShareUserLiveSearch extends AbstractUtils
{
    
    private static final Logger logger = Logger.getLogger(ShareUserLiveSearch.class);
    
    
    /**
     * Performs live search and  returns a live search dropdown
     * 
     * @param driver
     * @param searchTerm
     * @param searchFromMyDashBoard
     * @return
     */
    public static LiveSearchDropdown liveSearch(WebDrone driver, String searchTerm)
    {
        SharePage sharePage = ShareUser.getSharePage(driver);
        SearchBox searchBox= sharePage.getSearch();
        LiveSearchDropdown liveSearchDropdown  = searchBox.liveSearch(searchTerm).render();
        return liveSearchDropdown;
    }
   
    /**
     * Returns a list of document search results assuming live search has been done
     * 
     * @param liveSearchDropdown
     * @return
     */
    public static List<LiveSearchDocumentResult> getLiveSearchDocumentResults(LiveSearchDropdown liveSearchDropdown)
    {
        List<LiveSearchDocumentResult> liveSearchDocumentResults = liveSearchDropdown.getSearchDocumentResults();
        return liveSearchDocumentResults;
        
    }
    
    /**
     * Returns the list of live search document results titles
     * 
     * @param liveSearchDocumentResults
     * @return
     */
    public static List<String> getLiveSearchDocumentTitles(List<LiveSearchDocumentResult> liveSearchDocumentResults)
    {
        List<String> documentTitles = new ArrayList<String>();
        for (LiveSearchDocumentResult liveSearchDocumentResult : liveSearchDocumentResults)
        {
            documentTitles.add(liveSearchDocumentResult.getTitle().getDescription());
        }
        return documentTitles;
    }
    
    
    /**
     * Returns the list of live search document results sites names
     * 
     * @param liveSearchDocumentResults
     * @return
     */
    public static List<String> getLiveSearchDocumentSiteNames(List<LiveSearchDocumentResult> liveSearchDocumentResults)
    {
        List<String> siteNames = new ArrayList<String>();
        for (LiveSearchDocumentResult liveSearchDocumentResult : liveSearchDocumentResults)
        {
            siteNames.add(liveSearchDocumentResult.getSiteName().getDescription());
        }
        return siteNames;
    }
    
    
    /**
     * Returns the list of live search document results user names
     * 
     * @param liveSearchDocumentResults
     * @return
     */
    public static List<String> getLiveSearchDocumentUserNames(List<LiveSearchDocumentResult> liveSearchDocumentResults)
    {
        List<String> userNames = new ArrayList<String>();
        for (LiveSearchDocumentResult liveSearchDocumentResult : liveSearchDocumentResults)
        {
            userNames.add(liveSearchDocumentResult.getUserName().getDescription().toLowerCase());
        }
        return userNames;
    }
    
    /**
     * Performs live search and returns a list of sites search results
     * 
     * @param liveSearchDropdown
     * @return
     */
    public static List<LiveSearchSiteResult> getLiveSearchSitesResults(LiveSearchDropdown liveSearchDropdown)
    {
        List<LiveSearchSiteResult> liveSearchSiteResults = liveSearchDropdown.getSearchSitesResults();
        return liveSearchSiteResults;
        
    }  
    
    /**
     * Returns the list of live search sites results names
     * 
     * @param liveSearchSiteResults
     * @return
     */
    public static List<String> getLiveSearchSitesTitles(List<LiveSearchSiteResult> liveSearchSiteResults)
    {
        List<String> sitesTitles = new ArrayList<String>();
        for (LiveSearchSiteResult liveSearchSiteResult : liveSearchSiteResults)
        {
            sitesTitles.add(liveSearchSiteResult.getSiteName().getDescription());
        }
        return sitesTitles;
    }
    
    
    /**
     * Returns the list of live search user names
     * 
     * @param liveSearchPeopleResults
     * @return
     */
    public static List<String> getLiveSearchUserNames(List<LiveSearchPeopleResult> liveSearchPeopleResults)
    {
        List<String> userNames = new ArrayList<String>();
        for (LiveSearchPeopleResult liveSearchPeopleResult : liveSearchPeopleResults)
        {
            userNames.add(liveSearchPeopleResult.getUserName().getDescription());
        }
        return userNames;
    }
    
    
    
    /**
     * Performs live search and returns a list of people search results
     * 
     * @param liveSearchDropdown
     * @return
     */
    public static List<LiveSearchPeopleResult> getLiveSearchPeopleResults(LiveSearchDropdown liveSearchDropdown)
    {
        List<LiveSearchPeopleResult> liveSearchPeopleResults = liveSearchDropdown.getSearchPeopleResults();
        return liveSearchPeopleResults;
    }  
    
    /**
     * Clicks on the document title in document search result after performing live search 
     * 
     * @param liveSearchDropdown
     * @param documentTitle
     * @return
     */
    public static HtmlPage clickOnDocumentSearchResultTitle(LiveSearchDropdown liveSearchDropdown, String documentTitle)
    {
        List<LiveSearchDocumentResult> liveSearchDocumentResults = getLiveSearchDocumentResults(liveSearchDropdown);
        for (LiveSearchDocumentResult liveSearchDocumentResult : liveSearchDocumentResults)
        {
            String title = liveSearchDocumentResult.getTitle().getDescription();
            if (documentTitle.equalsIgnoreCase(title))
            {
                return liveSearchDocumentResult.clickOnDocumentTitle();
            }
        }
        logger.error("Unable to select document title.");
        throw new PageOperationException("Unable to select document title");
    }
    
    /**
     * Clicks on the site title in document search result after live search has been done
     * 
     * @param liveSearchDropdown
     * @param siteTitle
     * @return
     */
    public static HtmlPage clickOnDocumentSiteName(LiveSearchDropdown liveSearchDropdown, String siteTitle)
    {
        List<LiveSearchDocumentResult> liveSearchDocumentResults = getLiveSearchDocumentResults(liveSearchDropdown);
        for (LiveSearchDocumentResult liveSearchDocumentResult : liveSearchDocumentResults)
        {
            String siteName = liveSearchDocumentResult.getSiteName().getDescription();
            if (siteTitle.equalsIgnoreCase(siteName))
            {
                return liveSearchDocumentResult.clickOnDocumentSiteTitle();
            }
        }
        logger.error("Unable to select document site name.");
        throw new PageOperationException("Unable to select document site name");
    }
    
    /**
     * Clicks on the user name in document search result after doing live search
     * 
     * @param liveSearchDropdown
     * @param userName
     * @return
     */
    public static HtmlPage clickOnDocumentUserName(LiveSearchDropdown liveSearchDropdown, String userName)
    {
        List<LiveSearchDocumentResult> liveSearchDocumentResults = getLiveSearchDocumentResults(liveSearchDropdown);
        for (LiveSearchDocumentResult liveSearchDocumentResult : liveSearchDocumentResults)
        {
            String username = liveSearchDocumentResult.getUserName().getDescription();
            if (userName.equalsIgnoreCase(username))
            {
                return liveSearchDocumentResult.clickOnDocumentUserName();
            }
        }
        logger.error("Unable to select document user name.");
        throw new PageOperationException("Unable to select document user name");
    }    
    
    /**
     * 
     * Clicks on site name in live search site result
     * 
     * @param liveSearchDropdown
     * @param siteTitle
     * @return
     */
    public static HtmlPage clickOnSiteResultSiteName(LiveSearchDropdown liveSearchDropdown, String siteTitle)
    {
        List<LiveSearchSiteResult> liveSearchSiteResults = getLiveSearchSitesResults(liveSearchDropdown);
        for (LiveSearchSiteResult liveSearchSiteResult : liveSearchSiteResults)
        {
            String siteName = liveSearchSiteResult.getSiteName().getDescription();
            if (siteTitle.equalsIgnoreCase(siteName))
            {
                return liveSearchSiteResult.clickOnSiteTitle();
            }
        }
        logger.error("Unable to select site name in sites results.");
        throw new PageOperationException("Unable to select site name in sites results. ");
    }
    
    /**
     *  Clicks on user name in live search people result
     * 
     * @param liveSearchDropdown
     * @param userName
     * @return
     */
    public static HtmlPage clickOnPeopleResultUserName(LiveSearchDropdown liveSearchDropdown, String userName)
    {
        List<LiveSearchPeopleResult> liveSearchPeopleResults = getLiveSearchPeopleResults(liveSearchDropdown);
        for (LiveSearchPeopleResult liveSearchPeopleResult : liveSearchPeopleResults)
        {
            String username = liveSearchPeopleResult.getUserName().getDescription();
            if (username.indexOf(userName) != -1)
            {
                return liveSearchPeopleResult.clickOnUserName();
            }
        }
        logger.error("Unable to select user name in people results.");
        throw new PageOperationException("Unable to select user name in people results. ");
    }
    
    
}

package org.alfresco.share.util;

import java.util.List;

import org.alfresco.po.share.CustomiseUserDashboardPage;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.dashlet.ConfigureSavedSearchDialogBoxPage;
import org.alfresco.po.share.dashlet.ConfigureSiteNoticeDialogBoxPage;
import org.alfresco.po.share.dashlet.MyDiscussionsDashlet;
import org.alfresco.po.share.dashlet.SavedSearchDashlet;
import org.alfresco.po.share.dashlet.SearchLimit;
import org.alfresco.po.share.dashlet.SiteNoticeDashlet;
import org.alfresco.po.share.dashlet.SiteSearchDashlet;
import org.alfresco.po.share.dashlet.SiteSearchItem;
import org.alfresco.po.share.enums.Dashlet;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ShareUserDashboard extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(ShareUserDashboard.class);
    protected static final String DEFAULT_FOLDER = "Documents";

    public ShareUserDashboard()
    {
        if (logger.isTraceEnabled())
        {
            logger.debug(this.getClass().getSimpleName() + " instantiated");
        }

    }

    /**
     * This method is used to add the given dashlet to site dashboard page.
     * User should be logged in already.
     * 
     * @param driver
     * @param siteName
     * @return SiteDashboardPage
     */
    public static SiteDashboardPage addDashlet(WebDrone driver, String siteName, Dashlet dashlet)
    {
        SiteDashboardPage siteDashBoard = null;
        SharePage thisPage = ShareUser.getSharePage(driver);

        if (!(thisPage instanceof SiteDashboardPage))
        {
            siteDashBoard = ShareUser.openSiteDashboard(driver, siteName);
        }
        else
        {
            siteDashBoard = (SiteDashboardPage) thisPage;
        }

        CustomiseSiteDashboardPage customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard();
        customiseSiteDashBoard.render();
        siteDashBoard = customiseSiteDashBoard.addDashlet(dashlet, 1).render();
        return siteDashBoard;
    }

    
    /**
     * This method is used to add the given dashlet to user dashboard page.
     * User should be logged in already.
     * 
     * @param driver
     * @param dashlet
     * @return SiteDashboardPage
     */
    public static DashBoardPage addDashlet(WebDrone driver, Dashlet dashlet)
    {
        DashBoardPage dashBoard = null;
        SharePage thisPage = ShareUser.getSharePage(driver);

        if (!(thisPage instanceof DashBoardPage))
        {
            dashBoard = ShareUser.openUserDashboard(driver);
        }
        else
        {
            dashBoard = (DashBoardPage) thisPage;
        }

        CustomiseUserDashboardPage customiseUserDashBoard = dashBoard.getNav().selectCustomizeUserDashboard();
        customiseUserDashBoard.render();
        dashBoard = customiseUserDashBoard.addDashlet(dashlet, 1).render();
        return dashBoard;
    }
     
    
    /**
     * Method to set the Search term and use defaults for non-mandatory fields
     * 
     * @param drone
     * @param searchTerm
     * @return {@link SiteDashboardPage}
     */
    public static SharePage configureSavedSearch(WebDrone drone, String searchTerm)
    {
        return ShareUserDashboard.configureSavedSearch(drone, searchTerm, null, null);
    }

    /**
     * Method to set the Search term, search limit and use defaults for Title
     *
     * @param drone
     * @param searchTerm
     * @param searchLimit
     * @return {@link SiteDashboardPage}
     */
    public static SharePage configureSavedSearch(WebDrone drone, String searchTerm, SearchLimit searchLimit)
    {
        return ShareUserDashboard.configureSavedSearch(drone, searchTerm, null, searchLimit);
    }

    /**
     * Method to configure Saved Search
     * 
     * @param drone
     * @param searchTerm
     * @param title
     * @param searchLimit
     * @return {@link SiteDashboardPage}
     */
    public static SharePage configureSavedSearch(WebDrone drone, String searchTerm, String title, SearchLimit searchLimit)
    {
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getSavedSearchDashlet(drone);
        ConfigureSavedSearchDialogBoxPage configureSavedSearchPage = savedSearchDashlet.clickOnEditButton().render();
        if (searchTerm != null)
        {
            configureSavedSearchPage.setSearchTerm(searchTerm);
        }
        if (title != null)
        {
            configureSavedSearchPage.setTitle(title);
        }
        if (searchLimit != null)
        {
            configureSavedSearchPage.setSearchLimit(searchLimit);
        }
        return configureSavedSearchPage.clickOnOKButton().render();
    }

    /**
     * This method opens the Site Notice Configure dialog box and edits the title and text with given details and clicks on ok button on it.
     * User should be already present on site dashboard and should hold the site notce object.
     * 
     * @param siteNoticeDashlet
     * @param title
     * @param text
     * @param action
     */
    public static void configureSiteNoticeDialogBox(SiteNoticeDashlet siteNoticeDashlet, String title, String text, ConfigureSiteNoticeActions action)
    {
        ConfigureSiteNoticeDialogBoxPage configureSiteNotice = siteNoticeDashlet.clickOnConfigureIcon().render();

        configureSiteNotice.setTitle(title);
        configureSiteNotice.setText(text);

        if (ConfigureSiteNoticeActions.OK.equals(action))
        {
            configureSiteNotice.clickOnOKButton().render();
        }
        else if (ConfigureSiteNoticeActions.CANCEL.equals(action))
        {
            configureSiteNotice.clickOnCancelButton();
        }
        else if (ConfigureSiteNoticeActions.CLOSE.equals(action))
        {
            configureSiteNotice.clickOnCloseButton();
        }
    }

    /**
     * Method to get Shared Search Dashlet
     * 
     * @param drone
     * @return {@link SavedSearchDashlet}
     */
    public static SavedSearchDashlet getSavedSearchDashlet(WebDrone drone)
    {
        SharePage currentPage = ShareUser.getSharePage(drone);

        if(currentPage instanceof SiteDashboardPage)
        {
            SiteDashboardPage siteDashBoard = (SiteDashboardPage) currentPage;
            return siteDashBoard.getDashlet("saved-search").render();
        }
        else if(currentPage instanceof DashBoardPage)
        {
            DashBoardPage dashBoardPage = (DashBoardPage) currentPage;
            return dashBoardPage.getDashlet("saved-search").render();
        }
        else
        {
            throw new PageOperationException("Current page should be either SiteDashBoardPage or DashBoardPage");
        }
    }

    /**
     * Method to get Site Search Dashlet
     *
     * @param drone
     * @return {@link SiteSearchDashlet}
     */
    public static SiteSearchDashlet getSiteSearchDashlet(WebDrone drone)
    {
        SharePage currentPage = ShareUser.getSharePage(drone);

        if(currentPage instanceof SiteDashboardPage)
        {
            SiteDashboardPage siteDashBoard = (SiteDashboardPage) currentPage;
            return siteDashBoard.getDashlet("site-search").render();
        }
        else if(currentPage instanceof DashBoardPage)
        {
            DashBoardPage dashBoardPage = (DashBoardPage) currentPage;
            return dashBoardPage.getDashlet("site-search").render();
        }
        else
        {
            throw new PageOperationException("Current page should be either SiteDashBoardPage or DashBoardPage");
        }
    }

    /**
     * This method is used to get the Site Notice Dashlet page object from the site dashboard page.
     * User should be present on site dashboard page.
     * @param driver
     * @param siteName
     * @return {@link SiteNoticeDashlet}
     */
    public static SiteNoticeDashlet getSiteContentDashlet(WebDrone driver, String siteName)
    {
        SiteDashboardPage siteDashBoard = null;
        SharePage thisPage = ShareUser.getSharePage(driver);

        if (!(thisPage instanceof SiteDashboardPage))
        {
            siteDashBoard = ShareUser.openSiteDashboard(driver, siteName);
        }
        else
        {
            siteDashBoard = (SiteDashboardPage) thisPage;
        }

        return siteDashBoard.getDashlet(SITE_NOTICE).render();
    }
    
    /**
     * This method is used to get the My Discussions Dashlet page object from the site dashboard page.
     * User should be present on site dashboard page.
     * 
     * @return MyDiscussionsDashlet
     */
    public static MyDiscussionsDashlet getMyDiscussionsDashlet(WebDrone driver, String siteName)
    {
        SiteDashboardPage siteDashBoard = null;
        SharePage thisPage = ShareUser.getSharePage(driver);
        
        
        if (thisPage instanceof SiteDashboardPage)
        {
            siteDashBoard = (SiteDashboardPage) thisPage; 
            return siteDashBoard.getDashlet(MY_DISCUSSIONS).render();
        }
        else if (!(thisPage instanceof SiteDashboardPage))
        {
            siteDashBoard = ShareUser.openSiteDashboard(driver, siteName);
            return siteDashBoard.getDashlet(MY_DISCUSSIONS).render();
        }
        else
        {
            throw new PageOperationException("Cannot open My Discussion Dashlet on site dashboard page");
        }
 
    }
    
    /**
     * This method is used to get the MyDiscussionsDashlet page object from the user dashboard page.
     * User should be present on user dashboard page.
     * 
     * @return MyDiscussionsDashlet
     */
    public static MyDiscussionsDashlet getMyDiscussionsDashlet(WebDrone driver)
    {
          
        DashBoardPage dashBoard = null;
        SharePage thisPage = ShareUser.getSharePage(driver);
        
        if (thisPage instanceof DashBoardPage)
        {
            dashBoard = (DashBoardPage) thisPage;
            return dashBoard.getDashlet(MY_DISCUSSIONS).render();          
        } 
        else if (!(thisPage instanceof DashBoardPage))
        {
            dashBoard = ShareUser.openUserDashboard(driver);
            return dashBoard.getDashlet(MY_DISCUSSIONS).render();           
        }
        else
        {
            throw new PageOperationException("Cannot open My Discussion Dashlet on user dashboard page");
        }
        
    }

    /**
     * Method to check if a given file name is displayed in search result results or not
     * @param items
     * @param itemName
     * @return True if Item is displayed
     */
    public static boolean isContentDisplayedInSearchResults(List<SiteSearchItem> items, String itemName)
    {
        if (getSearchResultItem(items, itemName) != null)
        {
            return true;
        }
        return false;
    }
    
    /**
     * Method to get the 1st searchResult with the given contentName
     * @param items
     * @param itemName
     * @return SiteSearchItem
     */
    public static SiteSearchItem getSearchResultItem(List<SiteSearchItem> items, String itemName)
    {
        SiteSearchItem searchItem = null;
        
        if (items.isEmpty())
        {
            // No results
        }
        else
        {
            // Continue
            for (SiteSearchItem item : items)
            {
                if (item.getItemName().getDescription().equals(itemName))
                {
                    return item;
                }
            }
        }
        return searchItem;
    }
   
    /**
     * Method to perform search in Site-Search-Dashlet
     * Util assumes user is logged in and Site Dashboard is open
     * 
     * @param drone
     * @param searchTerm
     * @return List<SiteSearchItem> Search Results displayed
     */
    public static List<SiteSearchItem> searchSiteSearchDashlet(WebDrone drone, String searchTerm)
    {
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search(searchTerm);
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        return items;
    }

    /**
     * Method to get the 1st searchResult with the given contentName
     * @param items
     * @param itemName
     * @return SiteSearchItem
     */
    public static HtmlPage selectItem(List<SiteSearchItem> items, String itemName)
    {
        if (items.isEmpty())
        {
            throw new UnsupportedOperationException("Items list is empty");
        }
        if (StringUtils.isEmpty(itemName))
        {
            throw new UnsupportedOperationException("Item name cannot be empty");
        }
        for (SiteSearchItem item : items)
        {
            if (item.getItemName().getDescription().equals(itemName))
            {
                return item.getItemName().click();
            }
        }
        throw new PageOperationException("Unable to select item");
    }
    
    /**
     * Waits for the dashlet entries to appear in the my discussions dashlet
     * 
     * @param driver
     * @param entry
     * @param entryPresent
     * @return
     */
    public static Boolean searchMyDiscussionDashletWithRetry(WebDrone driver, String [] entries, Boolean entryPresent)
    {
        Boolean found = true;
        Boolean resultAsExpected = false;

        // Repeat search until the element is found or Timeout is hit
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            if (searchCount > 1)
            {
                webDriverWait(driver, refreshDuration);
                driver.refresh();
            }

            DashBoardPage dashBoardPage = (DashBoardPage) getSharePage(driver);
            MyDiscussionsDashlet myDiscussionsDashlet = dashBoardPage.getDashlet(MY_DISCUSSIONS).render(); 

            for (String entry : entries)
            {
                found =  found && myDiscussionsDashlet.isTopicTitleDisplayed(entry);
            }
            

            resultAsExpected = (entryPresent.equals(found));
            if (resultAsExpected)
            {
                break;
            }
        }

        return resultAsExpected;
    }
    

}

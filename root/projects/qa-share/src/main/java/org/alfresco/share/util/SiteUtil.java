/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.share.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.enums.SiteVisibility;
import org.alfresco.po.share.site.CreateSitePage;
import org.alfresco.po.share.site.EditSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.NoSuchElementException;
import org.testng.SkipException;

import javax.imageio.ImageIO;

/**
 * Utility class to manage site related operations
 * <ul>
 * <li>Creates site using Share UI</li>
 * <li>Deletes site using Share UI</li>
 * <li>Navigates to and Opens Site DashBoard using My-Sites Dashlet</li>
 * <li>Navigates to and Opens Site DocLib using URL pattern</li>
 * <li>Navigates to and Opens Site DocLib using URL</li>
 * </ul>
 * 
 * @author Michael Suzuki, Meenal Bhave
 * @since 1.0
 */
public class SiteUtil extends AbstractUtils
{
    private static final String SITE_DASH_LOCATION_SUFFIX = "/page/site/";
    private static final String SITE_DASH_SUFFIX = "dashboard";
    private static final String SITE_DOCLIB_SUFFIX = "documentlibrary";
    private final static Log logger = LogFactory.getLog(SiteUtil.class);
    private final static String ERROR_MESSAGE_PATTERN = "Failed to create a new site %n Site Name: %s";

    /**
     * Constructor.
     */
    private SiteUtil()
    {
    }

    /**
     * Prepare a file in system temp directory to be used in test for uploads.
     * 
     * @return {@link File} simple text file.
     */
    public static File prepareFile()
    {

        File file = null;
        OutputStreamWriter writer = null;
        try
        {

            file = File.createTempFile("myfile", ".txt");

            writer = new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8").newEncoder());
            writer.write("this is a sample test upload file");
            writer.close();
        }
        catch (IOException ioe)
        {
            logger.error("Unable to create sample file", ioe);
        }
        catch (Exception e)
        {
            logger.error("Unable to create site", e);
        }
        finally
        {
            if (writer != null)
            {
                try
                {
                    writer.close();
                }
                catch (IOException ioe)
                {
                    logger.error("Unable to close properly", ioe);
                }
            }
        }
        return file;
    }

    /**
     * This method create in Temp directory jpg file for uploading.
     *
     * @param jpgName
     * @return File object for created Image.
     */
    public static File prepareJpg(String jpgName)
    {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.drawString("Test Publish file.", 5, 10);
        g.drawString(jpgName, 5, 50);
        try
        {
            File jpgFile = File.createTempFile(jpgName, ".jpg");
            ImageIO.write(image, "jpg", jpgFile);
            return jpgFile;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        throw new SkipException("Can't create JPG file");
    }

    /**
     * Create site using share
     * 
     * @param drone
     * @param siteName String site name
     * @param siteVisibility
     * @return true if site created
     * @throws Exception if error
     */
    public static boolean createSite(WebDrone drone, final String siteName, String desc, String siteVisibility)
    {
        if (siteName == null || siteName.isEmpty())
        {
            throw new IllegalArgumentException("site name is required");
        }
        boolean siteCreated = false;
        DashBoardPage dashBoard;
        SiteDashboardPage site = null;
        try
        {
            SharePage page = drone.getCurrentPage().render();
            dashBoard = page.getNav().selectMyDashBoard().render();
            CreateSitePage createSite = dashBoard.getNav().selectCreateSite().render();
            if (siteVisibility == null)
            {
                siteVisibility = SITE_VISIBILITY_PUBLIC;
            }
            if (siteVisibility.equalsIgnoreCase(SITE_VISIBILITY_MODERATED))
            {
                site = createSite.createModerateSite(siteName, desc).render();
            }
            else if (siteVisibility.equalsIgnoreCase(SITE_VISIBILITY_PRIVATE))
            {
                site = createSite.createPrivateSite(siteName, desc).render();
            }
            // Will create public site
            else
            {
                site = createSite.createNewSite(siteName, desc).render();
            }

            site.render();

            if (siteName.equalsIgnoreCase(site.getPageTitle()))
            {
                siteCreated = true;
            }
            return siteCreated;
        }
        catch (UnsupportedOperationException une)
        {
            String msg = String.format(ERROR_MESSAGE_PATTERN, siteName);
            throw new RuntimeException(msg, une);
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    public static boolean createSite(WebDrone drone, final String siteName, String siteVisibility)
    {
        return createSite(drone, siteName, null, siteVisibility);
    }

    /***
     * Deletes site using share
     * 
     * @param siteName String site name
     * @return true if site deleted
     */
    public static boolean deleteSite(WebDrone drone, final String siteName)
    {
        if (siteName == null || siteName.isEmpty())
        {
            throw new IllegalArgumentException("site name is required");
        }
        try
        {
            SiteFinderPage siteFinder = searchSiteWithRetry(drone, siteName, true);

            siteFinder = siteFinder.deleteSite(siteName).render();
            return (!siteFinder.hasResults());
        }
        catch (UnsupportedOperationException une)
        {
            String msg = String.format(ERROR_MESSAGE_PATTERN, siteName);
            throw new RuntimeException(msg, une);
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }

    }

    /***
     * Attempts to delete specified site(s) using share
     * 
     * @param loginUserName
     * @param siteName String site name
     */
    public static void deleteSitesAsUser(WebDrone drone, String loginUserName, Set<String> siteNames)
    {
        try
        {
            if (siteNames == null)
            {
                throw new IllegalArgumentException("site name is required");
            }

            ShareUser.login(drone, loginUserName, DEFAULT_PASSWORD);

            for (String site : siteNames)
            {
                try
                {
                    SiteFinderPage siteFinderPage = searchSite(drone, site);
                    siteFinderPage.deleteSite(site);

                    if (logger.isTraceEnabled())
                    {
                        logger.info("Deleted Site: " + site);
                    }
                }
                catch (Exception e)
                {
                    logger.info("Unable to delete Site(s): " + site);
                }
            }
        }
        catch (Exception e)
        {
            logger.info("Error deleting sites");
        }
    }

    public static void deleteSites(WebDrone drone, String searchStringForsites)
    {
        if ((searchStringForsites == null) || (searchStringForsites.isEmpty()))
        {
            throw new IllegalArgumentException("site name is required");
        }

        SiteFinderPage siteResults = searchSite(drone, searchStringForsites);

        List<String> siteList = siteResults.getSiteList();

        for (String site : siteList)
        {
            try
            {
                siteResults.deleteSite(site);

                logger.info("deleted Site: " + site);
            }
            catch (Exception e)
            {
                logger.info("Error deleting sites:" + site);
            }
        }
    }

    /***
     * Search site using share.
     * 
     * @param siteName String site name
     * @return site name
     */
    public static SiteFinderPage searchSite(WebDrone drone, final String siteName)
    {

        if (siteName == null || siteName.isEmpty())
        {
            throw new IllegalArgumentException("site name is required");
        }
        try
        {
            SiteFinderPage siteFinder = getSiteFinder(drone);
            siteFinder = siteFinder.searchForSite(siteName).render();
            return siteFinder;
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Site not found!");
        }

        throw new PageException("Page is not found!!");

    }

    public static SiteFinderPage getSiteFinder(WebDrone driver)
    {
        SharePage share = ShareUser.getSharePage(driver);
        SiteFinderPage siteFinder = share.getNav().selectSearchForSites();
        return siteFinder.render();
    }

    /**
     * Method to search for a site and select the site from search results
     * 
     * @param drone
     * @param siteName
     * @return {@link SiteDashBoardPage}
     */
    public static SiteDashboardPage openSiteFromSearch(WebDrone drone, String siteName)
    {
        SiteFinderPage siteFinderPage = SiteUtil.searchSiteWithRetry(drone, siteName, true);
        SiteDashboardPage siteDashboardPage = siteFinderPage.selectSite(siteName);
        return siteDashboardPage;
    }

    /**
     * Method to navigate to site dashboard url, based on siteshorturl, rather than sitename
     * This is to be used to navigate only as a util, not to test getting to the site dashboard
     * 
     * @param drone
     * @param siteShortURL
     * @return {@link SiteDashBoardPage}
     */
    public static SiteDashboardPage openSiteURL(WebDrone drone, String siteShortURL)
    {
        String url = drone.getCurrentUrl();
        String target = url.substring(0, url.indexOf("/page/")) + SITE_DASH_LOCATION_SUFFIX + getSiteShortname(siteShortURL) + "/dashboard";
        drone.navigateTo(target);
        SiteDashboardPage siteDashboardPage = ShareUser.getSharePage(drone).render();

        return siteDashboardPage.render();
    }

    /**
     * From the User DashBoard, navigate to the Site DashBoard using the 'Site' in My-Sites Dashlet.
     * Assumes User is logged in.
     * 
     * @param driver WebDrone Instance
     * @param siteName String Name of the site to be opened
     * @return SiteDashboardPage
     * @throws PageException
     */
    public static SiteDashboardPage openSiteDashboard(WebDrone driver, String siteName) throws PageException
    {
        // Assumes User is logged in

        // Open User DashBoard
        DashBoardPage dashBoard = ShareUser.openUserDashboard(driver);

        MySitesDashlet dashlet = dashBoard.getDashlet(DASHLET_SITES).render(refreshDuration);

        SiteDashboardPage siteDashPage = dashlet.selectSite(siteName).click().render(maxWaitTime);

        logger.info("Opened Site Dashboard: " + siteName);

        return siteDashPage;
    }

    /**
     * Method to navigate to sites document library url, based on siteshorturl, rather than sitename
     * This is to be used to navigate only as a util, not to test getting to the site document library
     * 
     * @param drone
     * @param siteShortURL
     * @return {@link SiteDashBoardPage}
     */
    public static DocumentLibraryPage openSiteDocumentLibraryURL(WebDrone drone, String siteShortURL)
    {
        openSiteURL(drone, siteShortURL);
        String doclibUrl = drone.getCurrentUrl().replace(SITE_DASH_SUFFIX, SITE_DOCLIB_SUFFIX);
        drone.navigateTo(doclibUrl);
        DocumentLibraryPage siteDocLibPage = (DocumentLibraryPage) ShareUser.getSharePage(drone);

        return siteDocLibPage;
    }

    /**
     * Create a new site or handle exception if site already exists.
     * 
     * @param drone
     * @param siteName
     * @param desc
     * @param siteVisibility
     * @param handleDuplicateSite
     * @return
     */
    public static boolean createSite(WebDrone drone, final String siteName, String desc, String siteVisibility, boolean handleDuplicateSite)
    {
        if (siteName == null || siteName.isEmpty())
        {
            throw new IllegalArgumentException("site name is required");
        }
        boolean siteCreated = false;
        DashBoardPage dashBoard;
        SharePage site = null;
        try
        {
            SharePage page = drone.getCurrentPage().render();
            dashBoard = page.getNav().selectMyDashBoard().render();
            CreateSitePage createSite = dashBoard.getNav().selectCreateSite().render();
            if (siteVisibility == null)
            {
                siteVisibility = SITE_VISIBILITY_PUBLIC;
            }
            if (siteVisibility.equalsIgnoreCase(SITE_VISIBILITY_MODERATED))
            {
                site = createSite.createModerateSite(siteName, desc).render();
            }
            else if (siteVisibility.equalsIgnoreCase(SITE_VISIBILITY_PRIVATE))
            {
                site = createSite.createPrivateSite(siteName, desc).render();
            }
            // Will create public site
            else
            {
                site = createSite.createNewSite(siteName, desc).render();
            }

            site.render();

            if (siteName.equalsIgnoreCase(site.getPageTitle()))
            {
                siteCreated = true;
            }
            else
            {

                siteCreated = false;
                openSiteURL(drone, getSiteShortname(siteName));
            }
            return siteCreated;
        }
        catch (UnsupportedOperationException une)
        {
            String msg = String.format(ERROR_MESSAGE_PATTERN, siteName);
            throw new RuntimeException(msg, une);
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Util method to change the visibility of the site.
     * 
     * @param isPrivate
     * @param isModerated
     */
    public static void changeSiteVisibility(WebDrone drone, String siteName, boolean isPrivate, boolean isModerated)
    {
        SiteDashboardPage site = ShareUser.openSiteDashboard(drone, siteName).render();
        EditSitePage editSitePage = site.getSiteNav().selectEditSite().render();
        editSitePage.selectSiteVisibility(isPrivate, isModerated);
        editSitePage.selectOk();
    }

    /**
     * Create many sites for the logged in user
     * @param drone
     * @param prefix
     * @param siteVisibility
     * @param numOfSites
     * @return {@link Set} of SiteNames
     */
    public static Set<String> createManySites(WebDrone drone, String prefix, SiteVisibility siteVisibility, int numOfSites)
    {
        Set<String> siteNames = new HashSet<String>();

        for (int i = 0; i < numOfSites; i++)
        {
            String siteName = getSiteName(prefix + i);
            boolean siteCreated = createSite(drone, siteName, siteVisibility.getDisplayValue());
            if (siteCreated)
            {
                siteNames.add(siteName);
            }
        }
        return siteNames;
    }

    /**
     * Util searches for the site using specified string and returns the listed sites
     * @param drone
     * @param siteName
     * @return
     */
    public static List<String> getSiteList(WebDrone drone, String siteName)
    {
        SiteFinderPage siteFinderPage = searchSite(drone, siteName);
        return siteFinderPage.getSiteList();    
    }

    /**
     * Util returns true if site name is found in the SiteFinder Results
     * @param drone
     * @param siteName
     * @return false if site name is not found in the SiteFinder Results
     */
    public static boolean isSiteFound(WebDrone drone, String siteName)
    {
        if (getSiteList(drone, siteName).contains(siteName))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /***
     * Search site using share: Retry for solr's eventual consistency until the site is listed / not .
     * 
     * @param drone WebDrone instance
     * @param siteName String site name
     * @param isSiteExpected Boolean <tt>true</tt> if siteName is expected to be found in the search results 
     * @return SiteFinderPage
     */
    public static SiteFinderPage searchSiteWithRetry(WebDrone drone, String siteName, Boolean isSiteExpected)
    {        
        Boolean found = false;
        
        // Attempt 1
        SiteFinderPage siteFinderPage = searchSite(drone, siteName);

        // Code to repeat search until the element is found or Timeout is hit
        for (int searchCount = 1; searchCount < retrySearchCount; searchCount++)
        {
            found = isSiteFound(drone, siteName);
            if (found == isSiteExpected)
            {
                break;
            }
            else
            {
                // Wait for solr indexing
                logger.info("Wait for solr indexing: " + siteName);
                webDriverWait(drone, refreshDuration);
                siteFinderPage = searchSite(drone, siteName);
            }
        }
        
        return siteFinderPage;
            
    }
}

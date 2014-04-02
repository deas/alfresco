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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.CreateSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.NoSuchElementException;

/**
 * Utility class to manage site related operations
 * <ul>
 * <li>Creates site by calling REST API.</li>
 * <li>Deletes site by calling REST API.</li>
 * <li>Gets NodeRef value by site name.</li>
 * </ul>
 * 
 * @author Michael Suzuki
 * @author Shan Nagarajan
 * @since 1.0
 */
public class SiteUtil 
{
    private static final String SITE_FINDER_LOCATION_SUFFIX = "/page/site-finder";
    private final static Log logger = LogFactory.getLog(SiteUtil.class);
    private final static String ERROR_MESSAGE_PATTERN = "Failed to create a new site %n Site Name: %s%n Create Site API URL: %s%n";

    /**
     * Constructor.
     */
    private SiteUtil(){}
    
    /**
     * Prepare a file in system temp directory to be used
     * in test for uploads.
     * @return {@link File} simple text file.
     */
    public static File prepareFile()
    {
    	return prepareFile(null);
    }
    
    /**
     * Prepare a file in system temp directory to be used
     * in test for uploads.
     * @param String name to give a file.
     * @return {@link File} simple text file.
     */
    public static File prepareFile(final String name, String data)
    {

        File file = null;
        OutputStreamWriter writer = null;
        try
        {
            String fileName = (name != null && !name.isEmpty() ? name :"myfile");
            file = File.createTempFile(fileName, ".txt");
            
            writer = new OutputStreamWriter(new FileOutputStream(file),
                                                            Charset.forName("UTF-8").newEncoder());
            writer.write(data);
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
    public static File prepareFile(final String name){
        return prepareFile(name , "this is a sample test upload file");

    }

    
    /**
     * Create site using share
     * 
     * @param webDrone
     * @param siteName String site name
     * @param SiteVisiblity
     * @return true if site created
     * @throws Exception if error
     */
    public static boolean createSite(WebDrone drone , final String siteName, String desc, String siteVisibility)
    {
       if (siteName == null || siteName.isEmpty()) throw new UnsupportedOperationException("site name is required");
       boolean siteCreated = false;
       DashBoardPage dashBoard;
       SiteDashboardPage site= null;
       try
       {
           SharePage page = drone.getCurrentPage().render();
           dashBoard = page.getNav().selectMyDashBoard().render();
           CreateSitePage createSite;
           try
           {
               createSite = dashBoard.getNav().selectCreateSite().render();
           }
           catch (PageRenderTimeException e)
           {
               if(logger.isTraceEnabled())
               {
                   logger.trace("Unable to see create site modal, retry create site");
               }
               createSite = dashBoard.getNav().selectCreateSite().render();
           }
           if(siteVisibility.equalsIgnoreCase("Moderated"))
           {
               site = createSite.createModerateSite(siteName, desc).render();
           }
           else if(siteVisibility.equalsIgnoreCase("Public"))
           {
                site = createSite.createNewSite(siteName,desc).render();
           }
           else if(siteVisibility.equalsIgnoreCase("Private"))
           {
                site = createSite.createPrivateSite(siteName,desc).render();
           }
           
           if(siteName.equalsIgnoreCase(site.getPageTitle()))
           {    
               siteCreated = true;
           }       
           return siteCreated;
       }
       catch (UnsupportedOperationException une)
       {
           String msg = String.format(ERROR_MESSAGE_PATTERN,siteName);
           throw new RuntimeException(msg, une);
       }
       catch (NoSuchElementException nse)
       {
           return false;
       }
     }
    
    public static boolean createSite(WebDrone drone , final String siteName, String siteVisibility) 
    {
        return createSite(drone,siteName,null,siteVisibility);
    }
    
    /***
     * Deletes site using share
     * 
     * @param siteName String site name
     * @return true if site deleted
     */
    public static boolean deleteSite(WebDrone drone, final String siteName)
    {
        if (drone == null) throw new IllegalArgumentException("WebDrone is required");
        if (siteName == null || siteName.isEmpty()) throw new IllegalArgumentException("site name is required");
       
        try
        {
            // Alfresco.cloud.constants.CURRENT_TENANT
            String url = drone.getCurrentUrl();
            String target = url.replaceFirst("^*/page.*", SITE_FINDER_LOCATION_SUFFIX);
            drone.navigateTo(target);
            int count = 0;
            while(count < 5)
            {
                if(target.equalsIgnoreCase(drone.getCurrentUrl()))
                {
                    break;
                }
                count ++;
            }
            SiteFinderPage siteFinder = drone.getCurrentPage().render();
            siteFinder = siteFinder.searchForSite(siteName).render();
            if (siteFinder.hasResults())
            {
                siteFinder = siteFinder.deleteSite(siteName).render();
                return !siteFinder.hasResults();
            }
        }
        catch (UnsupportedOperationException une)
        {
            String msg = String.format(ERROR_MESSAGE_PATTERN, siteName);
            throw new RuntimeException(msg, une);
        }
        catch (PageException e) {}
        return false;
    }
    
    /***
     * Search site using share.
     * 
     * @param siteName String site name
     * @return site name
     */
    public static SiteFinderPage searchSite(WebDrone drone, final String siteName)
    {
        
        if (siteName == null || siteName.isEmpty()) throw new UnsupportedOperationException("site name is required");
        try
        {
            //Alfresco.cloud.constants.CURRENT_TENANT 
            String url = drone.getCurrentUrl();
            String target = url.substring(0, url.indexOf("/page/")) + SITE_FINDER_LOCATION_SUFFIX;
            drone.navigateTo(target);
            SiteFinderPage siteFinder = drone.getCurrentPage().render(); 
            siteFinder = siteFinder.searchForSite(siteName).render();
            return siteFinder;               
        }
        catch (UnsupportedOperationException une)
        {
            String msg = String.format(ERROR_MESSAGE_PATTERN, siteName);
            throw new RuntimeException(msg, une);
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Site not found!");
        }
        
        throw new PageException("Page is not found!!");

    }
}

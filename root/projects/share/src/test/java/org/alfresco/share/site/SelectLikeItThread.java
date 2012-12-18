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
package org.alfresco.share.site;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.alfresco.webdrone.AlfrescoVersion;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneImpl;
import org.alfresco.webdrone.WebDroneUtil;
import org.alfresco.webdrone.share.DashBoardPage;
import org.alfresco.webdrone.share.dashlet.MySitesDashlet;
import org.alfresco.webdrone.share.site.SiteDashboardPage;
import org.alfresco.webdrone.share.site.document.DocumentDetailsPage;
import org.alfresco.webdrone.share.site.document.DocumentLibraryPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.firefox.FirefoxDriver;

public class SelectLikeItThread implements Callable<Boolean>
{
    private Log logger = LogFactory.getLog(this.getClass());
    
    private String url;
    private String siteName;
    private String fileName;
    private AlfrescoVersion alfrescoVersion;
    private WebDrone drone;
    private CountDownLatch startPoint;
    
    public SelectLikeItThread(final AlfrescoVersion alfrescoVersion, 
                             final String url, 
                             final String siteName, 
                             final String fileName,
                             CountDownLatch startPoint)
    {
        this.alfrescoVersion = alfrescoVersion;
        this.url = url;
        this.siteName = siteName;
        this.fileName = fileName;
        this.startPoint = startPoint;
    }

    @Override
    public Boolean call() throws Exception
    {
        boolean completed = false;
        drone = new WebDroneImpl(new FirefoxDriver(), alfrescoVersion);
        try
        {
            if(logger.isDebugEnabled()) logger.debug(Thread.currentThread().getName() + " starting run");
            DashBoardPage dashBoard = WebDroneUtil.loginAs(drone, url, "admin", "admin").render();
            MySitesDashlet dashlet = dashBoard.getDashlet("my-sites").render();

            SiteDashboardPage site = dashlet.selectSite(siteName).render();
            if(logger.isDebugEnabled()) logger.debug(Thread.currentThread().getName() + " entering document library page");
            DocumentLibraryPage documentLibPage = site.getSiteNav().selectSiteDocumentLibrary().render();
            if(logger.isDebugEnabled()) logger.debug(Thread.currentThread().getName() + " entering document details page");
            DocumentDetailsPage documentDetailsPage = documentLibPage.selectFile(fileName).render();
            documentDetailsPage.selectLike();
            completed = true;

        }
        catch (Exception e)
        {
            logger.error("thread error: " + e);
        } 
        finally
        {
            if(logger.isDebugEnabled()) logger.debug(Thread.currentThread().getName() + "closing web drone");
            drone.quit();
        }
        if(completed) startPoint.countDown();
        return completed;
    }
}

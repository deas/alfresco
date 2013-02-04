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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.alfresco.webdrone.AlfrescoVersion;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneImpl;
import org.alfresco.webdrone.WebDroneUtil;
import org.alfresco.webdrone.share.DashBoardPage;
import org.alfresco.webdrone.share.ShareLink;
import org.alfresco.webdrone.share.dashlet.MySitesDashlet;
import org.alfresco.webdrone.share.site.SiteDashboardPage;
import org.alfresco.webdrone.share.site.document.DocumentDetailsPage;
import org.alfresco.webdrone.share.site.document.DocumentLibraryPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.firefox.FirefoxDriver;
/**
 * Thread that starts {@link WebDrone} and navigates to 
 * Alfresco's share document library. 
 * 
 * Once the thread has reached the page it wait till it
 * recieves the go ahead to check if like is selected
 * on the document, this is repeated by the maxRefreshCount.
 * 
 * The thread will continue until the refreh count has exceeded
 * or continueTest has been set to false by parent thread.
 * 
 * @author Michael Suzuki
 *
 */
public class CheckLikeItThread extends Thread
{
    private Log logger = LogFactory.getLog(this.getClass());
    
    private final AtomicInteger refreshCount = new AtomicInteger(0);
    private final AtomicBoolean exceededCount = new AtomicBoolean(false);
    private final int maxRefreshCount = 2;
    private final String url;
    private final String siteName;
    private final String fileName;
    private final AlfrescoVersion alfrescoVersion;
    private WebDrone drone;
    private CountDownLatch startPoint;
    private CountDownLatch finished;
    private CountDownLatch checkSignal;
    private CountDownLatch pauseSignal;
    
    AtomicBoolean continueTest;
    AtomicInteger likeCount = new AtomicInteger();
    
    public CheckLikeItThread(final AlfrescoVersion alfrescoVersion, 
                             final String url, 
                             final String siteName, 
                             final String fileName,
                             CountDownLatch start, 
                             CountDownLatch finished,
                             CountDownLatch pauseSignal,
                             CountDownLatch checkSignal,
                             AtomicBoolean continueTest)
    {
        this.alfrescoVersion = alfrescoVersion;
        this.url = url;
        this.siteName = siteName;
        this.fileName = fileName;
        this.startPoint = start;
        this.finished = finished;
        this.checkSignal = checkSignal;
        this.pauseSignal = pauseSignal;
        this.continueTest = continueTest;
    }
    
    @Override
    public void run()
    {
        drone = new WebDroneImpl(new FirefoxDriver(), alfrescoVersion);
        try
        {
            if(logger.isDebugEnabled()) logger.debug(Thread.currentThread().getName() + " starting run");
            DashBoardPage dashBoard = WebDroneUtil.loginAs(drone, url, "admin", "admin").render();
            MySitesDashlet dashlet = dashBoard.getDashlet("my-sites").render();

            ShareLink siteLink = dashlet.selectSite(siteName);
            SiteDashboardPage site = siteLink.click().render();
            if(logger.isDebugEnabled()) logger.debug(Thread.currentThread().getName() + " entering document library page");
            DocumentLibraryPage documentLibPage = site.getSiteNav().selectSiteDocumentLibrary().render();
            if(logger.isDebugEnabled()) logger.debug(Thread.currentThread().getName() + " entering document details page");
            documentLibPage.selectFile(fileName);
            checkLikeCount();
            startPoint.countDown();
            pauseSignal.await();
            //wait till the update happend
            while(continueTest.get() && !exceededCount.get())
            {
                incrementCount();
                checkLikeCount();
            } 
            checkSignal.countDown();
            finished.countDown();
        }
        catch (Exception e)
        {
            logger.error(String.format("%s thread error: %s",Thread.currentThread().getName(), e.getMessage()));
        } 
        finally
        {
            if(logger.isDebugEnabled()) logger.debug(Thread.currentThread().getName() + "closing web drone");
            drone.quit();
        }
    }
    /**
     * Increments the current count and checks 
     * against the max refresh count to validate
     * if the work should stop.
     */
    private void incrementCount()
    {
        refreshCount.getAndIncrement();
        if(logger.isDebugEnabled())
        {
            logger.debug(String.format("%S refresh count: %d",Thread.currentThread().getName(), refreshCount.get()));
        }
        boolean exceeded = refreshCount.get() > maxRefreshCount;
        if(exceeded)
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("refresh count exceeded: " + exceeded);
            }
            exceededCount.set(true);
        }
    }

    /**
     * Refreshes the page and verifies the like count
     * on the document details page.
     */
    public void checkLikeCount()
    {
        drone.refresh();
        DocumentDetailsPage documentDetailsPage = drone.getCurrentPage().render();
        Integer likes = new Integer(documentDetailsPage.getLikeCount());
        likeCount.set(likes);
    }
}

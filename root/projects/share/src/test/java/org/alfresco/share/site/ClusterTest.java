package org.alfresco.share.site;
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


import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.Assert;

import org.alfresco.share.site.AbstractSiteTest;
import org.alfresco.share.site.CheckLikeItThread;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.webdrone.AlfrescoVersion;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.share.site.SiteDashboardPage;
import org.alfresco.webdrone.share.site.UploadFilePage;
import org.alfresco.webdrone.share.site.document.DocumentLibraryPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Functional test to verify an update to a node of alfresco
 * is replicated to other alfresco nodes in a cluster.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class ClusterTest extends AbstractSiteTest
{
    private Log logger = LogFactory.getLog(ClusterTest.class);
    private static String siteName;
    private static File file;

    /**
     * Pre test setup using defualt admin user:
     * <ul>
     *     <li> Make up a site name. </li>
     *     <li> Create a dummy txt file. </li>
     *     <li> Create site using {@link WebDrone}. </li>
     *     <li> Upload dummy file to newly created site. </li>
     *     <li> Log user out </li>
     * </ul>
     */
    @BeforeClass 
    public static void setup() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();
        file = SiteUtil.prepareFile();
        getWebDrone();
        try
        {
            createSite(siteName);
            //Upload the doucment
            SiteDashboardPage site = drone.getCurrentPage().render();
            DocumentLibraryPage docPage = site.getSiteNav().selectSiteDocumentLibrary().render();
            UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload();
            docPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();   
        } 
        finally
        {
            quitWebDrone();
        }
    }

    @AfterClass
    public static void clean() throws Exception
    {
        SiteUtil.deleteSite(siteName, isCloud, shareUrl);
    }
    
    @Test
    public void checkUpdatingLike() throws Exception
    {
      AlfrescoVersion alfrescoVersion = AlfrescoVersion.Cloud;
      CountDownLatch endSignal = new CountDownLatch(maxIteration);
      CountDownLatch startSignal = new CountDownLatch(maxIteration);
      CountDownLatch triggerSignal = new CountDownLatch(maxIteration);
      CountDownLatch pauseTest = new CountDownLatch(1);
      AtomicBoolean continueTest = new AtomicBoolean(true);
      Thread[] threads = new CheckLikeItThread[maxIteration];
      try
      {
          //Start thread and get them to document detail page
          for(int i = 0; i < maxIteration; i++)
          {
              CheckLikeItThread thread = new CheckLikeItThread(alfrescoVersion,
                                                               shareUrl,
                                                               siteName,
                                                               file.getName(),
                                                               startSignal,
                                                               endSignal,
                                                               pauseTest,
                                                               triggerSignal,
                                                               continueTest);
              thread.setDaemon(true);
              threads[i] = thread;
              thread.start();
          }
          //Wait till all threads are on the document details page.
          startSignal.await();
          //assert no likes on the document details page
          for(int i =0; i < threads.length; i++)
          {
              CheckLikeItThread thread = (CheckLikeItThread) threads[i];
              Assert.assertEquals(0, thread.likeCount.get());
          }
          //Have a thread set like 
          logger.info("sent trigger signal wait");
          new SelectLikeItThread(alfrescoVersion, shareUrl, siteName, file.getName(), pauseTest).start();
          //pause the test to allow threads to check
          pauseTest.await();
          logger.debug("pause point passed");
          //Continue to pasue the test to allow thread to finish checking likes
          triggerSignal.await();
          //Refresh the other browsers
          logger.debug("assertion time");
          //check like count matches all thread
          for(int i =0; i < threads.length; i++)
          {
              CheckLikeItThread thread = (CheckLikeItThread) threads[i];
              Assert.assertEquals(1, thread.likeCount.get());
          }
      }
      finally
      {
          //End Threads
          if(logger.isDebugEnabled())
          {
              logger.debug("============ ending threads =============");
          }
          if(pauseTest.getCount() > 0)
          {
              pauseTest.countDown();
          }
          continueTest.set(false);
          endSignal.await();
          if(logger.isDebugEnabled())
          {
              logger.debug("============ test over =============");
          }
      }
    }
}

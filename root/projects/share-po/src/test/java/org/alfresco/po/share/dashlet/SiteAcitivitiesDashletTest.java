///*
// * Copyright (C) 2005-2012 Alfresco Software Limited.
// *
// * This file is part of Alfresco
// *
// * Alfresco is free software: you can redistribute it and/or modify
// * it under the terms of the GNU Lesser General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * Alfresco is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public License
// * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
// */
//package org.alfresco.po.share.dashlet;
//
//import java.io.IOException;
//import java.util.List;
//
//import org.alfresco.po.share.ShareLink;
//import org.alfresco.po.share.dashlet.MyActivitiesDashlet.LinkType;
//import org.alfresco.po.share.site.document.DocumentDetailsPage;
//import org.alfresco.po.share.util.SiteUtil;
//import org.alfresco.po.share.util.FailedTestListener;
//import org.alfresco.webdrone.RenderTime;
//import org.alfresco.webdrone.exception.PageException;
//import org.testng.Assert;
//import org.testng.annotations.AfterClass;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.BeforeTest;
//import org.testng.annotations.Listeners;
//import org.testng.annotations.Test;
//
///**
// * Integration test site activity dashlet page elements.
// * 
// * @author Michael Suzuki
// * @since 1.5
// */
//@Test(groups={"check", "alfresco-one"})
//@Listeners(FailedTestListener.class)
//public class SiteAcitivitiesDashletTest extends AbstractSiteDashletTest
//{
//    private static final String SITE_ACTIVITY = "site-activities";
//    
//    @BeforeTest
//    public void prepare() throws Exception
//    {
//        siteName = "SiteActivitiesDashletTests" + System.currentTimeMillis();
//    }
//
//    @BeforeClass
//    public void loadFile() throws Exception
//    {
//        uploadDocument();
//        navigateToSiteDashboard();
//    }
//
//    @AfterClass(alwaysRun=true)
//    public void deleteSite()
//    {
//        SiteUtil.deleteSite(drone, siteName);
//    }
//    
//   @Test
//    public void instantiateDashlet()
//    {
//    	SiteActivitiesDashlet dashlet = new SiteActivitiesDashlet(drone);
//        Assert.assertNotNull(dashlet);
//    }
//    
//    /**
//     * Test process of accessing my documents
//     * dashlet from the dash board view.
//     * @throws Exception 
//     */
//    @Test(dependsOnMethods="selectFake")
//    public void selectSiteactivityDashlet() throws Exception
//    {
//        SiteActivitiesDashlet dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
//        final String title = dashlet.getDashletTitle();
//        Assert.assertEquals(title, "Site Activities");
//    }
//    
//    @Test(dependsOnMethods="selectSiteactivityDashlet")
//    public void getActivities() throws IOException
//    {
//        SiteActivitiesDashlet dashlet = new SiteActivitiesDashlet(drone).render();
//        RenderTime timer = new RenderTime(60000);
//        while(true)
//        {
//            timer.start();
//            try
//            {
//                drone.refresh();
//                dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
//                if(!dashlet.getSiteActivities(LinkType.User).isEmpty()) break;
//            }
//            catch (PageException e) {}
//            finally
//            {
//                timer.end();
//            }
//        }
//        List<ShareLink> userLinks = dashlet.getSiteActivities(LinkType.User);
//        List<ShareLink> documentLinks = dashlet.getSiteActivities(LinkType.Document);
//        Assert.assertNotNull(userLinks);
//        if(userLinks.isEmpty()) saveScreenShot("getActivities.empty");
//        Assert.assertFalse(userLinks.isEmpty());
//        
//        Assert.assertNotNull(documentLinks);
//        Assert.assertFalse(documentLinks.isEmpty());
//    }
//    @Test(dependsOnMethods="getActivities")
//    public void selectActivity() throws Exception
//    {
//        DocumentDetailsPage page = null;
//        // This dashlet should not take over a minute to display activity list.
//        RenderTime timer = new RenderTime(60000);
//        SiteActivitiesDashlet dashlet;
//        while (true)
//        {
//            timer.start();
//            try
//            {
//                drone.refresh();
//                dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
//                if (!dashlet.getSiteActivities(LinkType.User).isEmpty()) break;
//            }
//            catch (PageException e)
//            {
//            }
//            finally
//            {
//                timer.end();
//            }
//        }
//        List<ShareLink> users = dashlet.getSiteActivities(LinkType.User);
//        Assert.assertNotNull(users);
//        Assert.assertFalse(users.isEmpty());
//
//        List<ShareLink> documents = dashlet.getSiteActivities(LinkType.Document);
//        Assert.assertNotNull(documents);
//        Assert.assertFalse(documents.isEmpty());
//
//        page = dashlet.selectActivityDocument(fileName).click().render();
//        Assert.assertNotNull(page);
//        Assert.assertEquals(true, page.isDocumentDetailsPage());
//    }
//
//    @Test(dependsOnMethods="instantiateDashlet",expectedExceptions = PageException.class)
//    public void selectFake() throws Exception
//    {
//        SiteActivitiesDashlet dashlet = siteDashBoard.getDashlet(siteName).render();
//        dashlet.selectActivityDocument("bla");
//    }
//}

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
package org.alfresco.po.share.dashlet;

import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify My Site dash let page elements are in place.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
@Listeners(FailedTestListener.class)
@Test(groups={"alfresco-one"})
public class MySiteDashletTest extends AbstractTest
{
    private static Log logger = LogFactory.getLog(MySiteDashletTest.class);
    private DashBoardPage dashBoard;
    private String siteName;
    
    @BeforeClass
    public void setup()throws Exception
    {
        siteName = "MySiteTests" + System.currentTimeMillis();
        dashBoard = loginAs(username, password);
        SiteUtil.createSite(drone, siteName, "description", "Public");
    }

    @AfterClass(alwaysRun=true)
    public void deleteSite()
    {
        try
        {
            SiteUtil.deleteSite(drone , siteName);
        }
        catch(Exception e)
        {
            logger.error("tear down was unable to delete site", e);
        }
        
    }
    
    @Test
    public void instantiateMySiteDashlet()
    {
        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();
        MySitesDashlet dashlet = new MySitesDashlet(drone);
        Assert.assertNotNull(dashlet);
    }
    
    @Test(dependsOnMethods="instantiateMySiteDashlet")
    public void getSites() throws Exception
    {
        MySitesDashlet dashlet = new MySitesDashlet(drone).render();
        if (dashlet.getSites().isEmpty()) saveScreenShot("MySiteDashletTest.getSites.empty");
        List<ShareLink> sites = dashlet.getSites();
        Assert.assertNotNull(sites);
        Assert.assertEquals(false, sites.isEmpty());
    }
    
    /**
     * Test process of accessing my site
     * dash let from the dash board view.
     * @throws Exception 
     */
    @Test(dependsOnMethods="getSites")
    public void selectMySiteDashlet() throws Exception
    {
        MySitesDashlet dashlet = dashBoard.getDashlet("my-sites").render();
        final String title = dashlet.getDashletTitle();
        Assert.assertEquals("My Sites", title);
    }
    
    @Test(dependsOnMethods="selectFakeSite")
    public void selectSite() throws Exception
    {
        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();
        MySitesDashlet dashlet = dashBoard.getDashlet("my-sites").render();
        ShareLink link = dashlet.selectSite(siteName);
        SitePage sitePage = link.click().render();

        Assert.assertNotNull(sitePage);
        Assert.assertEquals(true, sitePage.isSite(siteName));
    }
    
    @Test(dependsOnMethods="selectMySiteDashlet" ,expectedExceptions = PageException.class)
    public void selectFakeSite() throws Exception
    {
        MySitesDashlet dashlet = dashBoard.getDashlet("my-sites").render();
        dashlet.selectSite("bla");
    }

    /**
     * Checks the site is favourite.
     * 
     * @return
     */
    @Test(dependsOnMethods = "selectSite")
    public void isSiteFavouriteTest()
    {
        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();
        MySitesDashlet dashlet = dashBoard.getDashlet("my-sites").render();
        Assert.assertTrue(dashlet.isSiteFavourite(siteName));
    }
}

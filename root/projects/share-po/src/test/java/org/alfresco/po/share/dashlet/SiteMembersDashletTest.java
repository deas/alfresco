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

import java.io.IOException;
import java.util.List;

import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.webdrone.exception.PageOperationException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify Site members dashlet page elements are in place.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
@Listeners(FailedTestListener.class)
@Test(groups={"check", "alfresco-one"})
public class SiteMembersDashletTest extends AbstractSiteDashletTest
{
    String loginName;
    @BeforeClass(groups={"check", "alfresco-one"})
    public void setup() throws Exception
    {
        siteName = "SiteMemberTests" + System.currentTimeMillis();
        loginAs(username,password);
        String fname = anotherUser.getfName();
        String lname = anotherUser.getlName();
        loginName = fname +" "+ lname;
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
    }
    
    @AfterClass(groups={"check", "alfresco-one"})
    public void deleteSite()
    {
     SiteUtil.deleteSite(drone , siteName);
    }
    
    @Test
    public void instantiateMySiteDashlet()
    {
        MySitesDashlet dashlet = new MySitesDashlet(drone);
        Assert.assertNotNull(dashlet);
    }
    
    @Test
    public void getMembers() throws IOException
    {
        SiteMembersDashlet dashlet = new SiteMembersDashlet(drone).render();
        if (dashlet.getMembers().isEmpty()) saveScreenShot("SiteMembersDashletTest.getMembers.empty");
        List<ShareLink> members = dashlet.getMembers();
        Assert.assertNotNull(members);
        Assert.assertFalse(members.isEmpty());
    }
    
    /**
     * Test process of accessing my site
     * dash let from the dash board view.
     * @throws Exception 
     */
    @Test
    public void selectMySiteDashlet() throws Exception
    {
        SiteMembersDashlet dashlet = siteDashBoard.getDashlet("site-members").render();
        final String title = dashlet.getDashletTitle();
        Assert.assertEquals("Site Members", title);
    }
    
//    @Test
//    public void selectMember() throws Exception
//    {
//        SiteMembersDashlet dashlet = siteDashBoard.getDashlet("site-members").render();
//        //TODO fix below to be testng parameter
//       // String name = drone.getAlfrescoVersion().isCloud() ? "Auto Account" :"Administrator"; 
//        String name = alfrescoVersion.isCloud() ? loginName :"Administrator"; 
//        SiteMember link = dashlet.selectMember(name);
//        SharePage page = link.click().render();
//        
//        Assert.assertNotNull(page);
//        Assert.assertTrue(page.getPageTitle().contains("Profile"));
//        navigateToSiteDashboard();
//    }
    
    @Test(expectedExceptions = PageOperationException.class)
    public void selectFakeMember() throws Exception
    {
        SiteMembersDashlet dashlet = siteDashBoard.getDashlet("site-members").render();
        dashlet.selectMember("bla");
    }
}

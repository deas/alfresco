/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.share.dashlet;

import static org.alfresco.share.util.ShareUser.openSiteDashboard;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.alfresco.po.share.dashlet.SiteContentDashlet;
import org.alfresco.po.share.dashlet.SiteContentFilter;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Shan Nagarajan
 */
public class RecentlyModifiedSiteContentDashlet extends AbstractTests
{

    private static Log logger = LogFactory.getLog(RecentlyModifiedSiteContentDashlet.class);
    
    @Override
    @BeforeClass(alwaysRun=true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
    }
    
    @Test(groups={"DataPrepDashlets"})
    public void dataPrep_Dashlet_7934() throws Exception
    {        
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] {testUser};
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone,  testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);        
    }
    
    @Test
    public void enterprise40x_7934()
    {
        
        try
        {         
            String testName = getTestName();
            String testUser = getUserNameFreeDomain(testName);
            String siteName = getSiteName(testName);

            String expectedHelpBalloonMessage = "This dashlet makes it easy to keep track of your recent changes to library content in this site. Clicking the item name or thumbnail takes you to the details page so you can preview or work with the item.There are two views for this dashlet. The detailed view lets you:Mark an item as a favorite so it appears in Favorites lists for easy accessLike (and unlike) an itemJump to the item details page to leave a comment";           
            String expectedEmptyContentHeading = "Keep track of content changes";            
            String expectedContentDetails = "Easily see which document library items site members have been working on. In the detailed view you can like an item and mark it as a favorite. You can also jump to the details page to leave a comment.";
            
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            // Open Site DashBoard
            SiteDashboardPage siteDashBoardPage = openSiteDashboard(drone, siteName);
           
            SiteContentDashlet siteContentDashlet = siteDashBoardPage.getDashlet(SITE_CONTENT_DASHLET).render();
            
            siteDashBoardPage = siteContentDashlet.selectFilter(SiteContentFilter.I_HAVE_RECENTLY_MODIFIED);
            
            assertTrue(siteContentDashlet.isHelpButtonDisplayed());
            assertTrue(siteContentDashlet.isSimpleButtonDisplayed());
            assertTrue(siteContentDashlet.isDetailButtonDisplayed());
           
            siteContentDashlet = siteContentDashlet.render();
            List<String> contentsDetails = siteContentDashlet.getContentsDetails();
            
            assertEquals(contentsDetails.size(), 1);
            assertEquals(contentsDetails.get(0), expectedContentDetails);
            
            siteContentDashlet.clickHelpButton();
            assertTrue(siteContentDashlet.isBalloonDisplayed());
            
            String actualHelpBalloonMessage = siteContentDashlet.getHelpBalloonMessage();
            assertEquals(actualHelpBalloonMessage, expectedHelpBalloonMessage);
            
            siteContentDashlet.closeHelpBallon();
            assertFalse(siteContentDashlet.isBalloonDisplayed());
            assertEquals(siteContentDashlet.getEmptyContentHeading(), expectedEmptyContentHeading);
            
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            testCleanup(drone, testName);
        }
        
    }
    
}

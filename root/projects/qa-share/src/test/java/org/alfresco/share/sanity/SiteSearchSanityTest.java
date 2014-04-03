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
package org.alfresco.share.sanity;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Date;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SiteFinderPage.ButtonType;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Class includes: Tests from TestLink in Area: Sanity Tests
 * <ul>
 * <li>Test searches for Sites on Site Finder page.</li>
 * </ul>
 * 
 * @author Abhijeet Bharade
 */
@Listeners(FailedTestListener.class)
public class SiteSearchSanityTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(SiteSearchSanityTest.class);

    Date todayDate = new Date();

    private static String testPassword = DEFAULT_PASSWORD;

    protected String testUser;

    protected String siteName = "";


    /**
     * Class includes: Tests from TestLink in Area: Advanced Search Tests
     * <ul>
     * <li>Test searches using various Properties, content, Folder</li>
     * </ul>
     */
    @Override
    @BeforeClass
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Starting Tests: " + testName);
    }

    /**
     * Enterprise40x-6548:Search for Sites
     * <ul>
     * <li>Create any PUBLIC site from Sites menu</li>
     * <li>Create any MODERATED site from Sites menu</li>
     * <li>Create any PRIVATE site from sites menu</li>
     * <li>Log in to Share as second user</li>
     * <li>Click Search button from Site Finder page</li>
     * <li>Click on PUBLIC site</li>
     * <li>Click on MODERATED site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "Sanity")
    public void enterprise_6548() throws Exception
    {

        testName = getTestName();
        
        String testUser = getUserNameFreeDomain(testName) + "a";
        String otherUser = getUserNameFreeDomain("other" + testName) + "a";
        
        String publicSite = getSiteName("pub" + System.currentTimeMillis());
        String publicModSite = getSiteName("mod" + System.currentTimeMillis());
        String privateSite = getSiteName("private" + System.currentTimeMillis());

        try
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, otherUser);
            
            ShareUser.login(drone, testUser);
            
            SiteDashboardPage sitePage = ShareUser.createSite(drone, publicSite, SITE_VISIBILITY_PUBLIC).render();
            assertTrue(sitePage.isSiteTitle(publicSite), "Site Dashboad page should be opened for - " + publicSite);
            // TODO: Add a step to check if the site is favourite

            sitePage = ShareUser.createSite(drone, publicModSite, SITE_VISIBILITY_MODERATED).render();
            assertTrue(sitePage.isSiteTitle(publicModSite), "Site Dashboad page should be opened - " + publicModSite);
            // TODO: Add a step to check if the site is favourite
            
            sitePage = ShareUser.createSite(drone, privateSite, SITE_VISIBILITY_PRIVATE).render();
            assertTrue(sitePage.isSiteTitle(privateSite), "Site Dashboad page should be opened");
            // TODO: Add a step to check if the site is favourite

            ShareUser.logout(drone);
            
            SharePage sharePage = ShareUser.login(drone, otherUser, testPassword);

            SiteFinderPage siteFinderPage = sharePage.getNav().selectSearchForSites().render();
            siteFinderPage = siteFinderPage.searchForSite("").render();

            assertTrue(siteFinderPage.isButtonForSitePresent(publicSite, ButtonType.Join), "Join button should be present for site");
            assertTrue(siteFinderPage.isButtonForSitePresent(publicModSite, ButtonType.RequestToJoin), "Request to Join button should be present for site");

            sitePage = SiteUtil.openSiteFromSearch(drone, publicSite);
            assertTrue(sitePage.isSiteTitle(publicSite), "Site Dashboad page should be opened");

            logger.info("Trying to open mod site.");

            try
            {
                sitePage = SiteUtil.openSiteFromSearch(drone, publicModSite).render(5000);
                fail("Page should not render as the content of the site is not visible.");
            }
            catch (Exception e)
            {
                assertTrue(e instanceof PageRenderTimeException, "Page should not render as site is moderated site.");
            }
            logger.info("Trying to open site finder page.");
            siteFinderPage = sitePage.getNav().selectSearchForSites();
            siteFinderPage.searchForSite(privateSite);
            assertTrue(siteFinderPage.getSiteList().size() < 1, "Site should not be found - " + siteFinderPage.getSiteList().size());
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
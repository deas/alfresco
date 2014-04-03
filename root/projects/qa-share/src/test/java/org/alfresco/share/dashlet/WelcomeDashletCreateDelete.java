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

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.dashlet.SiteWelcomeDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Welcome Widget Remove test case (7950).
 * 
 * <li>1. Login & Create Site.</li> <li>2. Remove the welcome widget.</li> <li>
 * 3. Remove the site.</li> <li>4. Create the site with the same name.</li> <li>
 * 5. Open site dashboard & verify the welcome widget.</li> <li>6. Delete the
 * site again.</li>
 * 
 * @author Shan Nagarajan
 */
public class WelcomeDashletCreateDelete extends AbstractTests
{
    private static Log logger = LogFactory.getLog(WelcomeDashletCreateDelete.class);

    protected String testUser;
    protected String testUserPass = DEFAULT_PASSWORD;

    private List<String> optionsDesc;
    private int expectedOptionsSize;

    DashBoardPage dashBoard;

    /**
     * <li>Create site name based on the current system time & login.</li> <li>
     * Login to using user name & password from property file.</li> <li>Set
     * Expected Option on welcome screen based on the Environment.</li>
     * 
     * @throws Exception
     */
    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testUser + "@" + DOMAIN_FREE;
        logger.info("[Suite ] : Start Tests in: " + testName);

        optionsDesc = new ArrayList<String>();

        if (isAlfrescoVersionCloud(drone))
        {

            optionsDesc.add("Customize the site dashboard");
            optionsDesc.add("Invite to Site");
            optionsDesc.add("Upload content");
            optionsDesc.add("Customize your dashboard");
        }
        else if (alfrescoVersion.equals(AlfrescoVersion.Enterprise41)
                || alfrescoVersion.equals(AlfrescoVersion.Enterprise42))
        {
            optionsDesc.add("Customize the site dashboard");
            optionsDesc.add("Invite people");
            optionsDesc.add("Upload content");
            optionsDesc.add("Sign up");
        }
        expectedOptionsSize = optionsDesc.size();

    }

    @Test(groups =
    { "DataPrepDashlets" })
    public void dataPrep_Dashlets_7950() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        // User
        String[] testUserInfo = new String[]
        { testUser };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

    }

    /**
     * Create the Site & Remove the Welcome Dashlet validate the dashlet not
     * present.
     * 
     * @throws Exception
     */
    @Test
    public void enterprise40x_7950() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName) + System.currentTimeMillis();

        try
        {
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            // Site
            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

            // Check Welcome Dashlet
            SiteWelcomeDashlet welcomeDashlet = checkWelcomeDashlet(siteName);

            // Remove Dashlet
            welcomeDashlet.removeDashlet();

            ShareUser.openUserDashboard(drone);

            // Delete Site
            SiteUtil.deleteSite(drone, siteName);

            // DeleteSite completes with success and createSite with same name
            // completes with success implicitly proves the site was deleted
            // successfully by deleteSite

            // Create Site
            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

            // Check Welcome Dashlet
            checkWelcomeDashlet(siteName);
        }

        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            // Delete Site
            SiteUtil.deleteSite(drone, siteName);
            testCleanup(drone, testName);
        }
    }

    /**
     * Create site & validate the expected options present in welcome dashlet.
     * 
     * @return @link {@link SiteWelcomeDashlet}
     * @throws Exception
     */
    protected SiteWelcomeDashlet checkWelcomeDashlet(String siteName) throws Exception
    {

        SiteWelcomeDashlet welcomeDashlet;

        SiteDashboardPage dashBoard = ShareUser.openSiteDashboard(drone, siteName);

        try
        {

            welcomeDashlet = dashBoard.getDashlet("welcome-site").render();

            List<ShareLink> optionShareLinks = welcomeDashlet.getOptions();

            Assert.assertEquals(optionShareLinks.size(), expectedOptionsSize);

            int expectedCount = 0;

            for (ShareLink shareLink : optionShareLinks)
            {

                Assert.assertEquals(shareLink.getDescription(), optionsDesc.get(expectedCount));
                expectedCount++;

            }
        }
        catch (Throwable e)
        {
            saveScreenShot(drone, "SiteTest.deleteSite-error");
            throw new Exception("Welcome Dashlet Checks failed", e);
        }

        return welcomeDashlet;

    }

}
package org.alfresco.share.sanity;

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

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.share.util.api.PublicAPIRestClient;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Class to include: Tests for shareuser util methods and internal rest apis implemented in Utils > api
 * 
 * @author Meenal Bhave
 */
@Listeners(FailedTestListener.class)
public class InternalAPITest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(InternalAPITest.class);

    // If default user is not yet created: Set to ADMIN_USERNAME
    protected String testUser;

    @Override
    @BeforeClass(alwaysRun=true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Starting Tests: " + testName);
    }

    // Internal API SanityTests
    /**
     * Happy Path
     */
    @Test(groups = { "Sanity", "AlfrescoOne" })
    public void unitTestHappyPath_1()
    {
        try
        {
            /** Start Test */
            String testName = getTestName();
            String testUser = getUserNameFreeDomain(testName + System.currentTimeMillis() + "1");
            String[] testUserInfo = new String[] { testUser };

            String testUser2 = getUserNameFreeDomain(testName + System.currentTimeMillis() + "2");
            String[] testUserInfo2 = new String[] { testUser2 };

            String testUser3 = getUserNamePremiumDomain(testName + System.currentTimeMillis() + "3");
            String[] testUserInfo3 = new String[] { testUser3 };

            String siteName = getSiteName(testName + System.currentTimeMillis());
            String siteNameModerated = siteName + "moderated";
            String siteNamePrivate = siteName + "private";

            String folderName = testName;
            String[] fileInfo = new String[] { testName };

            HttpResponse response;
            Boolean result;
            /** Test Steps */

            Assert.assertTrue(CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo));
            Assert.assertTrue(CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2));
            Assert.assertTrue(CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo3));                      

            if (alfrescoVersion.isCloud())
            {
                response = CreateUserAPI.upgradeCloudAccount(drone, ADMIN_USERNAME, getUserDomain(testUser), "1000");

                checkResult(response, 200);
            }

            result = CreateUserAPI.promoteUserAsAdmin(drone, ADMIN_USERNAME, testUser, getUserDomain(testUser));

            Assert.assertTrue(result);

            result = CreateUserAPI.promoteUserAsAdmin(drone, ADMIN_USERNAME, testUser3, getUserDomain(testUser3));

            Assert.assertTrue(result);
            
            // Login 1
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            // Create Site
            ShareUser.createSite(drone, siteNamePrivate, SITE_VISIBILITY_PRIVATE);
            ShareUser.createSite(drone, siteNameModerated, SITE_VISIBILITY_MODERATED);
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

            // Create Content
            ShareUser.openDocumentLibrary(drone);
            ShareUser.createFolderInFolder(drone, folderName, "", "");

            ShareUser.uploadFileInFolder(drone, fileInfo);

            // User 1 sends the invite to User 3 to join Site
            ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser3, siteName, UserRole.COLLABORATOR);
            
            // Logout
            ShareUser.logout(drone);

            // User 2 sends Site Membership Request
            Assert.assertEquals(PublicAPIRestClient.requestSiteMembership(drone, testUser2, getUserDomain(testUser2), testUser2, siteNamePrivate).getStatusLine().getStatusCode(), 404, "Check if the environment is setup with no layer 7.");
            Assert.assertEquals(PublicAPIRestClient.requestSiteMembership(drone, testUser2, getUserDomain(testUser2), testUser2, siteNameModerated).getStatusLine().getStatusCode(), 201);
            Assert.assertEquals(PublicAPIRestClient.requestSiteMembership(drone, testUser2, getUserDomain(testUser2), testUser2, siteName).getStatusLine().getStatusCode(), 201);

            // User 1 Invites User 2 to join the private site
            
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            
            ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser2, siteNamePrivate, UserRole.COLLABORATOR);
            
            ShareUser.logout(drone);

            // Login 2
            ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

            ShareUser.openSiteDashboard(drone, siteName);

            ShareUser.openDocumentLibrary(drone);

            // Logout
            ShareUser.logout(drone);

            // Login 3
            ShareUser.login(drone, testUser3, DEFAULT_PASSWORD);
            
            if(alfrescoVersion.isCloud())
            {
                ShareUser.selectTenant(drone, getUserDomain(testUser));
            }

            ShareUser.openSiteDashboard(drone, siteName);
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

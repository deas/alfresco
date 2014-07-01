/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.reports;

import java.util.List;

import org.alfresco.po.share.dashlet.TopSiteContributorDashlet;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;



/**
 * 
 * Top site contributor report dashlet tests
 * 
 * @author jcule
 *
 */

@Listeners(FailedTestListener.class)
public class TopSiteContributorReportTest extends AbstractUtils
{
    private static final Logger logger = Logger.getLogger(TopSiteContributorReportTest.class);

    private static String testPassword = DEFAULT_PASSWORD;
    protected String testUser;
    protected String siteName = "";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Starting Tests: " + testName);
    }

    /**
     * 1) Create test user
     * 2) Login as test user
     * 3) Create site
     * 4) Create user1
     * 5) Add user1 with write permissions to write to the site
     * 6) Test user logs out
     * 7) User1 logs in
     * 8) User1 creates txt files
     * 9) User1 logs out
     * 10) Steps 2,4,5,6,7,8,9 repeated for user2, user3, user4 and user5
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepTopSiteContributorReport" })
    public void dataPrep_TopSiteContributor_ALF_1055() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, DOMAIN_FREE);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        int firstNumberOfFiles = 7;
        int secondNumberOfFiles = 4;
        int thirdNumberOfFiles = 1;
        int fourthNumberOfFiles = 6;
        int fifthNumberOfFiles = 10;

        // Create test user
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);

        // Login as created user
        ShareUser.login(drone, testUser, testPassword);

        // Create site
        SiteUtil.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PRIVATE);

        // first user
        String testUser1 = getUserNameForDomain(testName + "-1", DOMAIN_FREE);
        String[] testUserInfo1 = new String[] { testUser1 };

        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo1);

        // add user with write permissions to write to the site
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser1, siteName, UserRole.COLLABORATOR);

        // Inviting user logs out
        ShareUser.logout(drone);

        // Invited User logs in
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // first user creates files
        createUsersAndUploadFiles(firstNumberOfFiles, siteName);

        // first user logs out
        ShareUser.logout(drone);

        // second user
        String testUser2 = getUserNameForDomain(testName + "-2", DOMAIN_FREE);
        String[] testUserInfo2 = new String[] { testUser2 };

        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo2);

        // add user with write permissions to the site
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser2, siteName, UserRole.COLLABORATOR);

        // Inviting user logs out
        ShareUser.logout(drone);

        // Invited User logs in
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        // second user creates files
        createUsersAndUploadFiles(secondNumberOfFiles, siteName);

        // second user logs out
        ShareUser.logout(drone);

        // third user
        String testUser3 = getUserNameForDomain(testName + "-3", DOMAIN_FREE);
        String[] testUserInfo3 = new String[] { testUser3 };

        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo3);

        // add user with write permissions to the site
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser3, siteName, UserRole.COLLABORATOR);

        // Inviting user logs out
        ShareUser.logout(drone);

        // Invited User logs in
        ShareUser.login(drone, testUser3, DEFAULT_PASSWORD);

        // first user creates files
        createUsersAndUploadFiles(thirdNumberOfFiles, siteName);

        // third user logs out
        ShareUser.logout(drone);

        // fourth user
        String testUser4 = getUserNameForDomain(testName + "-4", DOMAIN_FREE);
        String[] testUserInfo4 = new String[] { testUser4 };

        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo4);

        // add user with write permissions to the site
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser4, siteName, UserRole.COLLABORATOR);

        // Inviting user logs out
        ShareUser.logout(drone);

        // Invited User logs in
        ShareUser.login(drone, testUser4, DEFAULT_PASSWORD);

        // fourth user creates files
        createUsersAndUploadFiles(fourthNumberOfFiles, siteName);

        // fourth user logs out
        ShareUser.logout(drone);

        // fifth user
        String testUser5 = getUserNameForDomain(testName + "-5", DOMAIN_FREE);
        String[] testUserInfo5 = new String[] { testUser5 };

        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo5);

        // add user with write permissions to the site
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser5, siteName, UserRole.COLLABORATOR);

        // Inviting user logs out
        ShareUser.logout(drone);

        // Invited User logs in
        ShareUser.login(drone, testUser5, DEFAULT_PASSWORD);

        // first user creates files
        createUsersAndUploadFiles(fifthNumberOfFiles, siteName);

        // first user logs out
        ShareUser.logout(drone);

    }

    /**
     * 1) Test user (site creator) logs in
     * 2) Test user (site creator) adds Top Site Contributor Dashlet to site's dashboard
     * 3) Checks the number of top site contributors is correct
     */
    @Test(groups = { "TopSiteContributorReport" })
    public void ALF_1055()
    {
        // test user (site creator) logs in
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, DOMAIN_FREE);
        String siteName = getSiteName(testName);
        ShareUser.login(drone, testUser, testPassword);

        // test user (site creator) adds Top Site Contributor Dashlet to site's dashboard
        TopSiteContributorDashlet topSiteContributorDashlet = ShareUserDashboard.getTopSiteContributorDashlet(drone, siteName);
        List<String> topSiteContributorUsers = topSiteContributorDashlet.getTopSiteContributorUsers();

        String testUser1 = getUserNameForDomain(testName + "1", DOMAIN_FREE);
        String testUser2 = getUserNameForDomain(testName + "2", DOMAIN_FREE);
        String testUser3 = getUserNameForDomain(testName + "3", DOMAIN_FREE);
        String testUser4 = getUserNameForDomain(testName + "4", DOMAIN_FREE);
        String testUser5 = getUserNameForDomain(testName + "5", DOMAIN_FREE);

        // assert the users
        Assert.assertTrue(topSiteContributorUsers.contains(testUser1.replaceAll("[^A-Za-z0-9]", "")));
        Assert.assertTrue(topSiteContributorUsers.contains(testUser2.replaceAll("[^A-Za-z0-9]", "")));
        Assert.assertTrue(topSiteContributorUsers.contains(testUser3.replaceAll("[^A-Za-z0-9]", "")));
        Assert.assertTrue(topSiteContributorUsers.contains(testUser4.replaceAll("[^A-Za-z0-9]", "")));
        Assert.assertTrue(topSiteContributorUsers.contains(testUser5.replaceAll("[^A-Za-z0-9]", "")));

        Assert.assertTrue(topSiteContributorUsers.size() == 5);

    }

    /**
     * Uploads files to site's document library
     * 
     * @param numberOfFiles
     * @param siteName
     * @throws Exception
     */
    private void createUsersAndUploadFiles(int numberOfFiles, String siteName) throws Exception
    {
        String[] userFiles = new String[numberOfFiles];
        for (int i = 0; i < userFiles.length; i++)
        {
            userFiles[i] = getFileName(testName + "_" + i + "." + "txt");
        }

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // UpLoad Files
        for (int index = 0; index <= userFiles.length - 1; index++)
        {
            String[] fileInfo = { userFiles[index] };
            ShareUser.uploadFileInFolder(drone, fileInfo);
        }

    }
}

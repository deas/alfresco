/*
 * Copyright (C) 2005-2013s Alfresco Software Limited.
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

import static org.testng.AssertJUnit.assertTrue;

import java.util.List;

import org.alfresco.po.share.ChangePasswordPage;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.PeopleFinderPage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.UserProfilePage;
import org.alfresco.po.share.dashlet.MyActivitiesDashlet;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class PeopleFinderTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(PeopleFinderTest.class);

    protected String testUser;

    protected String siteName = "";

    private String anotherUser;

    private String fileName;

    /**
     * Class includes: Tests from TestLink in Area: Advanced Search Tests
     * <ul>
     * <li>Test searches using various Properties, content, Proximity, Range Queries</li>
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
     * DataPreparation method - Enterprise40x-6549
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepSearch"})
    public void dataPrep_PeopleFinder_6549() throws Exception
    {
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        anotherUser = getUserNameFreeDomain(testName + 1);

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser });
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { anotherUser });

            ShareUser.logout(drone);
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

    /**
     * Enterprise40x-6549:People finder
     * <ul>
     * <li>Click People link on toolbar</li>
     * <li>Search for any user (e.g. for yourself)</li>
     * <li>Click on user's name link</li>
     * <li>Edit user's profile</li>
     * <li>Click Sites link</li>
     * <li>Click Content link</li>
     * <li>Click Change Password link and change the password</li>
     * <li>Search for any othe user (e.g TestUser)</li>
     * <li>Click Follow button for the user</li>
     * <li>As TestUser log in Share, create any site, upload a new document, change the status, search for previously logged in user and click Follow for him</li>
     * <li>Log in previously logged in user and go to Activities dashlet, Select I'm following filter</li>
     * <li>Click Following Me link on My Profile page</li> *
     * </ul>
     */
    // TODO: Abhijet: Pl enable test, comment out psuedo part, if not implemented
    @Test(enabled = false)
    public void PeopleFinder_6549()
    {
        /** Start Test */
        String testName = getTestName();
        fileName = getFileName(testName);
        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);

        try
        {
            // Login
            DashBoardPage dashPage = (DashBoardPage) ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            PeopleFinderPage peopleFinderPage = dashPage.getNav().selectPeople();
            peopleFinderPage.searchFor(testUser);

            List<ShareLink> searchLinks = peopleFinderPage.getResults();
            if (!searchLinks.isEmpty())
            {
                ShareLink userLink = findUserLink(testUser, searchLinks);
                userLink.click();
                UserProfilePage userProfilePage = drone.getCurrentPage().render();

                // TODO - below code WEBDRONE-208 & WEBDRONE-210 required.
                // EditProfilePage editProfilePage =
                // userProfilePage.selectEditProfile().render();

                // ProfileSubNav profileSubNav = editProfilePage.getSubNav();

                // profileSubNav.selectSites().render();
                assertTrue("Sites should be selected", ((DashBoardPage) drone.getCurrentPage()).isBrowserTitle("User Sites List"));

                // profileSubNav.selectContent().render();
                assertTrue("Sites should be selected", ((DashBoardPage) drone.getCurrentPage()).isBrowserTitle("User Edited COntent"));

                ChangePasswordPage changePasswordPage = null;
                // changePasswordPage =
                // profileSubNav.selectChangePassword().render();
                assertTrue("Sites should be selected", changePasswordPage.isBrowserTitle("Change User Password"));

                peopleFinderPage = changePasswordPage.getNav().selectPeople().render();
                peopleFinderPage.searchFor(anotherUser).render();

                // TODO Webdrone impl required.
                // peopleFinderPage.selectFollowUser(anotherUser);
                // kassertTrue("Unfollow button should be displayed",
                // peopleFinderPage.isUserFollowed(anotherUser));

                ShareUser.logout(drone).render();
                ShareUser.login(drone, anotherUser, DEFAULT_PASSWORD).render();
                ShareUser.createSite(drone, testName, "Public").render();
                ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime).render();

                // Uploading file.
                ShareUser.uploadFileInFolder(drone, new String[] { fileName });
                peopleFinderPage = changePasswordPage.getNav().selectPeople().render();
                peopleFinderPage.searchFor(testUser).render();

                // TODO Webdrone impl required.
                // peopleFinderPage.selectFollowUser(testUser);
                // kassertTrue("Unfollow button should be displayed",
                // peopleFinderPage.isUserFollowed(testUser));
                dashPage = peopleFinderPage.getNav().selectMyDashBoard().render();
                MyActivitiesDashlet myActivitiesDashlet = dashPage.getDashlet(DASHLET_ACTIVITIES).render();

                // TODO need to look at MyActivitiesDashlet dashlet.
                // myActivitiesDashlet.selectActivityUser(name);

                MyProfilePage myProfilePage = dashPage.getNav().selectMyProfile().render();
                // TODO - WEBDRONE-214
                // profileSubNav = myProfilePage.getSubNav();
                // FollowersPage followersPage =
                // profileSubNav.selectFollowingMe();
            }
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

    /**
     * @param testUser
     * @param searchLinks
     */
    // TODO: Abhijeet: Can this be Implemented as a method for page object
    private ShareLink findUserLink(String testUser, List<ShareLink> searchLinks)
    {
        for (ShareLink shareLink : searchLinks)
        {
            if (shareLink.getDescription().contains(testUser))
            {
                return shareLink;
            }
        }
        return null;
    }

}

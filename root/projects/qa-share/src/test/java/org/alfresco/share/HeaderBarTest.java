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
package org.alfresco.share;

import org.alfresco.share.util.AbstractUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author nshah
 * Dated: 06/03/2014
 */
public class HeaderBarTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(SiteDashBoardTest.class);

    protected String testUser;

    protected String siteName = "";

    /**
     * Class includes: Tests from TestLink in Area: Site DashBoard Tests
     * <ul>
     * <li>Perform an Activity on Site</li>
     * <li>Site DashBoard shows Activity Feed</li>
     * </ul>
     */
    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("[Suite ] : Start Tests in: " + testName);
    }
    
    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_SiteDashBoard_9296() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        // User
        String[] testUserInfo = new String[] { testUser };
        
        // Create one user
        
        // Login through created user.
        
        // Navigate to My sites- Dashlet page
        
        // Verify/assert to check Create site link is present

        // Create a site by clicking on create site link.
        
        // Verify/Assert for new site is present in dashlates.
        
        // Navigate back to Site Dashlat page.
        
        // Click on Delete icon of newly created site. 
        
        //Verify that newly creats site does not exist anymore.
        
        // Verify create site link is till present.
        
        // Create another site.
        
        // Verify another site is created.
    }
    
    
    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_SiteDashBoard_9297() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        // Create one user
        
        // Login through created user.
        
        // Navigate to My sites- Dashlet page
        
        // Create 3 sites with private, public ,publi with Moderated Site Memebrship.
        
        // NAvigate to Site Finder's page.
        
        // search site and check available options are present.
        
        //Click on available site.
        
        // Verify Site finder option is present in Sites menu

    }
    
    
    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_SiteDashBoard_9298() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        // Create one user
        
        // Login through created user.
        
//        1. Click Tab key;
//
//        2. Open Home page again c lick Right arrow key then Enter key;
//
//        3. Open Home page again and click Tab key thrice then Enter key;
//
//        4. Open Home page again click Tab then Right arrow key trice then Enter key;
//
//        5. With Down Arrow key hightlight the site created in preconditions;
//
//        6. Open Home page again click Tab then Right arrow key thrice then Enter key;
//
//        7. With Down Arrow key choose Site Finder option;
//
//        8. Open Home page again click Tab then Right arrow key thrice then Enter key;
//
//        9. With Down Arrow key choose Create Site option;
//
//        10. Close the form and Click Tab then Right arrow key trice thhen Enter key;
//
//        11. With Down arrow key choose Favourites and click Right arrow key;
//
//        12. Click Tab then Right arrow key four time then Enter key;
//
//        13. Click Enter;
//
//        14. Open Home page again and with Tab key (click several times) navigate to People link -> press Enter;
//
//        15. Open Home page again with Tab key (click several times) navigate to Repository link -> press Enter;
//
//        16. Open Home page again with Tab key (click several times) navigate to Admin Tools link -> press Enter;
//
//        17. Open Home page again with Tab key (click several times) navigate to <user_name> link -> press Down arrow;
//
//        18 . Click Tab and down arrow key;
//
//        19. Click Enter key;
//
//        20. Open Home page again and click Tab key several times to Navigate to Search input field;

    }
    
    //TODO: for cloud as well 
    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_SiteDashBoard_14186() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
  
        // Login through Admin user.
        
        // Navigate to User DashBoard
        
        // Verify Presence of Tasks link in the header bar.
        
        // Click on Tasks link.
        
        // Verify Two drop down list with Two links{My Task, Workflows}
        
        // Click on My Tasks link.
        
        // Verify My Tasks page.
        
        // Go to Tasks > and click  Workflows i have started 

        // Verify Workflows i have started page.     
    }
    
    
    
    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_SiteDashBoard_9291() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        // Create one user
        
        // Login through created user.
        
        //Navigate to User DashBoard 
        
        // Verify My Dash let is present
        
        // Create a different new site.
        
        // log out user
        
        // Create a any new user using admin
        
        // login as a new user
        
        // Verify that there is no site exists in Sites > recent site menu 
        
    }
    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_SiteDashBoard_9292() throws Exception
    {
        // Create one user1
        
        // Login through created user.
        
        // Navigate to User DashBoard. 
        
        // Verify My Dash let is present.
        
        // Create a different new site.
        
        // log out user
        
        // Create a any new user2 using admin
        
        // invite user2 to new site created, user2 will accept the invite
        
        // login as a new user2
        
        // Verify that there is newly created(invited) site exists in Sites > recent site menu 
        
        
       
    }
    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_SiteDashBoard_9293() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        // Create one user1
        
        // Login through created user.
        
        // Navigate to User DashBoard. 
        
        // Verify My Dash let is present.
        
        // Create a 6 new site.
        
        // log out user
        
        // Create a any new user2 using admin
        
        // invite user2 to new site created, user2 will accept the invite
        
        // login as a new user2
        
        // Join other 5 sites.
        
        // VErify recent sites show maximum 5 sites shown.
       
    }
    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_SiteDashBoard_9294() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        // Create one user1
        
        // Login through created user.
        
        // Navigate to User DashBoard. 
        
        // Verify My Dash let is present.
        
        // Create a 6 new site.
        
        // log out user
        
        // Create a any new user2 using admin
        
        // invite user2 to new site created, user2 will accept the invite
        
        // login as a new user2
        
        // Join other 5 sites.
        
        // VErify recent sites show the last join site on the top.
        
    }
    
    
    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_SiteDashBoard_9304() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        // Create one user1
       
        // Login as Admin
        
        // Navigate to User DashBoard. 
        
        // Verify My Sites Dash let is present.
        
        // Create a new site1, site2.
        
        // invite user1 oto join new site
        
        // log out
        
        // login as user2 and join the other site.
        
        // Add both the sites as favourites
        
        // Verify added sites are displayed as part of favourite group. 
        
        
    }
    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_SiteDashBoard_9305() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        // Create one user1
        
        // Login as Admin
        
        // Navigate to User DashBoard. 
        
        // Verify My Sites Dash let is present.
        
        // Create a new site.

        // Invited user1 to join the site.
        
        // user1 logs in 
        
        // user1 does not make joined site as its favourite.
        
        //Verify that user1 does not see joined site in its favourites option. 
        
    }
    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_SiteDashBoard_9306() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        // Create one user1
        
        // Login as Admin
        
        // Navigate to User DashBoard. 
        
        // Verify My Sites Dash let is present.
        
        // Create 10 new site.

        // Invited user1 to join the site.
        
        // user1 logs in 
        
        // user1 does make all 10 joined site as its favourite.
        
        //Verify that user1 does  see joined site in its favourites option.
        
    }
    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_SiteDashBoard_9307() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        // Create one user1
        
        // Login as Admin
        
        // Navigate to User DashBoard. 
        
        // Verify My Sites Dash let is present.
        
        // Create 10 new site.

        // Invited user1 to join the site.
        
        // user1 logs in 
        
        // user1 does make all 10 joined site as its favourite.
        
        // USer1 naivgates to one of the site
        
        // USer1 verifies that Sites menu has option "Add current site as favourite"
        
        // Add current site to faourite.
        
        // verify added site appears as favourite site
        
    }
    
    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_SiteDashBoard_9308() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        // Create one user1
        
        // Login as Admin
        
        // Navigate to User DashBoard. 
        
        // Verify My Sites Dash let is present.
        
        // Create 10 new site.

        // Invited user1 to join the site.
        
        // user1 logs in 
        
        // user1 does make all 10 joined site as its favourite.
        
        // USer1 naivgates to one of the site
        
        // USer1 verifies that Sites menu has option "Add current site as favourite"
        
        // Add current site to faourite.
        
        // verify added site appears as favourite site
        
        // USer1 removes the favourite site from favourites list.
        
        // Verify favourite site is not favourite any more. 
        
    }
    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_SiteDashBoard_9309() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        // Create one user1
        
        // Login as Admin
        
        // Navigate to User DashBoard. 
        
        // Verify My Sites Dash let is present.
        
        // Create  new site.
        
        // user1 logs in     
        
        // USer1 naivgates to one of the site created by Admin
        
        // User1 verifies that Sites menu has option "Add current site as favourite"
        
        // Add current site to faourite.
        
        // verify added site appears as favourite site
        
    }
}

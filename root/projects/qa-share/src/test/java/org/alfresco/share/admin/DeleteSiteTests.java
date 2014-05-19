/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.admin;

/**
 *
 * @author Charu
 * @since
 */
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.admin.ActionsSet;
import org.alfresco.po.share.admin.ManageSitesPage;
import org.alfresco.po.share.admin.ManagedSiteRow;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserAdmin;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class DeleteSiteTests extends AbstractUtils
{
    private static final Logger logger = Logger.getLogger(SiteAdminGroupTests.class);
    public String siteAdmin = "SITE_ADMINISTRATORS";
    public DashBoardPage dashBoard;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups = { "DataPrepAdmin" })
    public void dataPrep_ALF_2949() throws Exception
    {
        String testName = getTestName();
        String user2 = getUserNameFreeDomain(testName + "1");
        String[] testUser2 = new String[] { user2 };

        // Create normal User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2);

    }

    /**
     * Test:
     * <ul>
     * <li>Delete a site on Manage sites page by RepoAdmin</li>
     * <li>Verify deleted site is not present in the list of sites</li>
     * <li></li>
     * </ul>
     */

    @Test(groups = "EnterpriseOnly")
    public void ALF_2949() throws Exception
    {
        String testName = getTestName();
        
        String user2 = getUserNameFreeDomain(testName + "1");
        String[] testUser2 = new String[] { user2 };
        
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String actionName = "Delete Site";

        // Login as user2(Not a member of siteAdmin group)
        ShareUser.login(drone, testUser2);

        // Create public site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Logout User2
        ShareUser.logout(drone);

        // Login as RepoAdmin
        ShareUser.login(drone, ADMIN_USERNAME);

        // Navigate to manageSites page
        ManageSitesPage manageSitesPage = ShareUserAdmin.navigateToManageSites(drone);

        // Find the created site
        ManagedSiteRow manageSiteRow = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(siteName);
        
        ActionsSet actionsSet = manageSiteRow.getActions();

        // Verify delete action is present
        Assert.assertTrue(actionsSet.hasActionByName(actionName), "Delete action is present");

        // Confirm to delete created site
        actionsSet.clickActionByNameAndDialogByButtonName(actionName, "OK");        

        ManagedSiteRow result = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(siteName);

        // Verify the site is deleted and removed from the list of sites
        Assert.assertNull(result);
    }

    @Test(groups = { "DataPrepAdmin" })
    public void dataPrep_ALF_2950() throws Exception
    {
        String testName1 = getTestName();
        
        String testUser1 = getUserNameForDomain(testName1, DOMAIN_PREMIUM);
        
        String testName2 = getTestName();
        String testUser2 = getUserNameForDomain(testName2 + "1",DOMAIN_PREMIUM);      

        // Create User1 and add to SiteAdmin group               
        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, siteAdmin, testUser1);        
        
        // Create User2 and add to SiteAdmin group
        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, siteAdmin, testUser2);

    }

    /**
     * Test:
     * <ul>
     * <li>Delete a site on Manage sites page by SiteAdmin/Network Admin</li>
     * <li>Verify deleted site is not present in the list of sites</li>
     * <li></li>
     * </ul>
     */

    @Test(groups = "AlfrescoOne")
    public void ALF_2950() throws Exception
    {
        String testName1 = getTestName();
        String testUser1 = getUserNameForDomain(testName1, DOMAIN_PREMIUM);
        
        String testName2 = getTestName();
        String testUser2 = getUserNameForDomain(testName2 + "1", DOMAIN_PREMIUM);        
        
        String site1 = getSiteName(testName1) + System.currentTimeMillis();
        String actionName = "Delete Site";

        // Login as User2
        ShareUser.login(drone, testUser2);

        // Create public site
        ShareUser.createSite(drone, site1, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Logout User2
        ShareUser.logout(drone);

        // Login as SiteAdmin
        ShareUser.login(drone, testUser1);

        // Navigate to manageSites page
        ShareUser.openUserDashboard(drone);
        ManageSitesPage manageSitesPage = ShareUserAdmin.navigateToManageSites(drone);

        // Find the created site
        ManagedSiteRow manageSiteRow = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(site1);
        
        ActionsSet actionsSet = manageSiteRow.getActions();

        // Verify delete action is present
        Assert.assertTrue(actionsSet.hasActionByName(actionName), "Delete action is present");

        // Confirm to delete created site
        actionsSet.clickActionByNameAndDialogByButtonName(actionName, "OK");        

        ManagedSiteRow result = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(site1);

        // Verify the site is deleted and removed from the list of sites
        Assert.assertNull(result);
    }

    
    @Test(groups = { "DataPrepAdmin" })
    public void dataPrep_ALF_2952() throws Exception
    {
        String testName2 = getTestName() + "1";
        String testUser2 = getUserNameFreeDomain(testName2);
        String[] testUser2Info = new String[] { testUser2 };

        // Create User2
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2Info);

    }

    /**
     * Test:
     * <ul>
     * <li>Confirm Cancel Delete a site on Manage sites page by RepoAdmin</li>
     * <li></li>
     * </ul>
     */

    @Test(groups = "EnterpriseOnly")
    public void ALF_2952() throws Exception
    {
        String testName1 = getTestName();
        
        String testName2 = getTestName() + "1";
        String testUser2 = getUserNameFreeDomain(testName2);
        String[] testUser2Info = new String[] { testUser2 };
        
        String site1 = getSiteName(testName1) + System.currentTimeMillis();
        String actionName = "Delete Site";        

        // Login as User2(Not a member of siteAdmin group)
        ShareUser.login(drone, testUser2Info);

        // Create public site
        ShareUser.createSite(drone, site1, AbstractUtils.SITE_VISIBILITY_PRIVATE);

        // Logout User2
        ShareUser.logout(drone);

        // Login as RepoAdmin
        ShareUser.login(drone, ADMIN_USERNAME);

        // Navigate to manageSites page
        ShareUser.openUserDashboard(drone);
        
        // TODO: Create a util to return ManageSitesPage for Ent and cloud user. This way only 1 test can be implemented for both versions
        ManageSitesPage manageSitesPage = ShareUserAdmin.navigateToManageSites(drone);

        // Find the created site
        ManagedSiteRow manageSiteRow = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(site1);        
        
        ActionsSet actionsSet = manageSiteRow.getActions();

        // Verify delete action is present
        Assert.assertTrue(actionsSet.hasActionByName(actionName), "Delete action is present");

        // Confirm cancel delete of created site
        actionsSet.clickActionByNameAndDialogByButtonName(actionName, "Cancel");
        
        ManagedSiteRow result = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(site1);

        // Verify the site is not deleted and present in the list of sites
        Assert.assertNotNull(result);
    }    
    
    @Test(groups = { "DataPrepAdmin" })
    public void dataPrep_ALF_2953() throws Exception
    {
        String testName1 = getTestName();        
        String testUser1 = getUserNameForDomain(testName1, DOMAIN_PREMIUM);        

        // Create User1
        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, siteAdmin, testUser1);

    }

    /**
     * Test:
     * <ul>
     * <li>Confirm site cannot be deleted by Site/Network Admin when content in site is in off line edit</li>
     * <li></li>
     * </ul>
     */

    @Test(groups = "AlfrescoOne")
    public void ALF_2953() throws Exception
    {
        String testName1 = getTestName();
        String testUser1 = getUserNameForDomain(testName1, DOMAIN_PREMIUM);        
        
        String site1 = getSiteName(testName1) + System.currentTimeMillis();
        
        String actionName = "Delete Site";
        
        String fileName = getTestName();

        //Login as RepoAdmin
        ShareUser.login(drone, testUser1);

        // Create public site
        ShareUser.createSite(drone, site1, AbstractUtils.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

        // Upload file in Site Document library
        String[] fileInfo = { fileName };
        ShareUser.uploadFileInFolder(drone, fileInfo).render(maxWaitTime);
        DocumentLibraryPage libPage = ShareUser.openDocumentLibrary(drone);

        // Click off line edit
        DocumentLibraryPage libpage = libPage.getFileDirectoryInfo(fileName).selectEditOffline().render();
        Assert.assertTrue(libpage.getFileDirectoryInfo(fileName).isEdited(), "The file is blocked for editing");

        // Navigate to Manage sites page
        ManageSitesPage manageSitesPage = ShareUserAdmin.navigateToManageSites(drone);

        // Find site1 in the list of sites
        ManagedSiteRow manageSiteRow = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(site1);        
        
        ActionsSet actionsSet = manageSiteRow.getActions();
        Assert.assertTrue(actionsSet.hasActionByName(actionName), "Delete action is present");

        // Confirm to delete created site
        actionsSet.clickActionByNameAndDialogByButtonName(actionName, "ok");        

        ManagedSiteRow result = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(site1);

        // Verifying site1 not deleted when file is in off line edit
        Assert.assertNotNull(result);
    }

}

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
package org.alfresco.share.site.document;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Test the Default view of Document Library. Share Refresh > FilmStrip View
 * 
 * @author Abhijeet Bharade
 */
@Listeners(FailedTestListener.class)
public class DefaultViewDocLibTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(DefaultViewDocLibTest.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
    }

    /**
     * DataPreparation method - ALF_16715
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_ALF_16715() throws Exception
    {
        String testName = getTestName();

        createSiteData(testName, true);
    }

    /**
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne")
    public void ALF_16715() throws Exception
    {
        /** Start Test */
        String testName = getTestName();

        // Login as User1 (Manager)
        runTestForUser(testName, UserRole.MANAGER);
    }

    /**
     * DataPreparation method - ALF_16715
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_ALF_16718() throws Exception
    {
        String testName = getTestName();

        createSiteData(testName, true);
    }

    /**
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne")
    public void ALF_16718() throws Exception
    {
        /** Start Test */
        String testName = getTestName();

        // Login as User1 (Manager)
        runTestForUser(testName, UserRole.COLLABORATOR);
    }

    /**
     * DataPreparation method - ALF_16715
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_ALF_16719() throws Exception
    {
        String testName = getTestName();

        createSiteData(testName, true);
    }

    /**
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne")
    public void ALF_16719() throws Exception
    {
        /** Start Test */
        String testName = getTestName();

        // Login as CONTRIBUTOR
        runTestForUser(testName, UserRole.CONTRIBUTOR);
    }

    /**
     * DataPreparation method - ALF_16720
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_ALF_16720() throws Exception
    {
        String testName = getTestName();

        createSiteData(testName, true);
    }

    /**
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne")
    public void ALF_16720() throws Exception
    {
        /** Start Test */
        String testName = getTestName();

        // Login as CONSUMER
        runTestForUser(testName, UserRole.CONSUMER);
    }

    /**
     * DataPreparation method - ALF_16726
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_ALF_16726() throws Exception
    {
        String testName = getTestName();

        createSiteData(testName, false);
    }

    /**
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne")
    public void ALF_16726() throws Exception
    {
        /** Start Test */
        String testName = getTestName();

        String siteName = getSiteName(testName);

        String testUserManager = getUserNameFreeDomain(testName + UserRole.MANAGER);
        String testUserCollab = getUserNameFreeDomain(testName + UserRole.COLLABORATOR);
        String testUserContri = getUserNameFreeDomain(testName + UserRole.CONTRIBUTOR);

        String folderName = getFolderName(testName);
        String subFolderNameCollab = getFolderName("collab" + testName);
        String subFolderNameContri = getFolderName("contri" + testName);

        // Login as User1 (Manager)
        ShareUser.login(drone, testUserManager);

        ShareUser.openSiteDashboard(drone, siteName);

        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // Expand the "Options" menu, click the any view (e.g. "Gallery View")
        DocumentLibraryPage docLibPage = ShareUserSitePage.selectView(drone, ViewType.GALLERY_VIEW);
        assertEquals(docLibPage.getViewType(), ViewType.GALLERY_VIEW);

        // "Set "ANY View" as default for this folder " ("Gallery View")
        docLibPage = docLibPage.getNavigation().selectSetCurrentViewToDefault().render();
        assertTrue(docLibPage.getNavigation().isRemoveDefaultViewVisible());

        // Login as User2 (Collaborator)
        ShareUser.login(drone, testUserCollab);

        ShareUser.openSiteDashboard(drone, siteName);

        docLibPage = ShareUser.createFolderInFolder(drone, subFolderNameCollab, subFolderNameCollab, folderName);

        // Select OTHER view (e.g. "Table View") for SubFolder
        docLibPage.selectFolder(subFolderNameCollab).render();
        docLibPage = ShareUserSitePage.selectView(drone, ViewType.TABLE_VIEW);
        assertEquals(docLibPage.getViewType(), ViewType.TABLE_VIEW);

        // "Set "ANY View" as default for this folder " ("Table View")
        docLibPage = docLibPage.getNavigation().selectSetCurrentViewToDefault().render();
        assertTrue(docLibPage.getNavigation().isRemoveDefaultViewVisible());

        // Login as User3 (Contributor)
        ShareUser.login(drone, testUserContri);

        ShareUser.openSiteDashboard(drone, siteName);
        docLibPage = ShareUser.createFolderInFolder(drone, subFolderNameContri, subFolderNameContri, folderName + File.separator + subFolderNameCollab);

        // Select different view (e.g. "Table View") for subFolder
        docLibPage.selectFolder(subFolderNameContri).render();
        docLibPage = ShareUserSitePage.selectView(drone, ViewType.TABLE_VIEW);
        assertEquals(docLibPage.getViewType(), ViewType.TABLE_VIEW);

        // "Set "ANY View" as default for this folder " ("Table View")
        docLibPage = docLibPage.getNavigation().selectSetCurrentViewToDefault().render();
        assertTrue(docLibPage.getNavigation().isRemoveDefaultViewVisible());
    }

    /**
     * @param testName
     * @param createData
     * @throws Exception
     */
    private void createSiteData(String testName, boolean createData) throws Exception
    {
        List<String> accessList = new ArrayList<String>();

        String siteName = getSiteName(testName);

        String fileName1 = getFileName(testName + "1");
        String fileName3 = getFileName(testName + "3");

        String folderName = getFolderName(testName);
        String subFolderName = getFolderName("sub" + testName);

        accessList.add(UserRole.MANAGER.toString());
        accessList.add(UserRole.COLLABORATOR.toString());
        accessList.add(UserRole.CONTRIBUTOR.toString());
        accessList.add(UserRole.CONSUMER.toString());

        for (String access : accessList)
        {
            String testUserWithAccess = getUserNameFreeDomain(testName + access);
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUserWithAccess });
        }

        for (String access : accessList)
        {
            // User
            String testUserWithAccess = getUserNameFreeDomain(testName + access);

            if (UserRole.CONSUMER.toString().equalsIgnoreCase(access))
            {
                testUserWithAccess = getUserNameFreeDomain(testName + UserRole.MANAGER);
            }

            ShareUser.login(drone, testUserWithAccess, DEFAULT_PASSWORD);

            if (UserRole.MANAGER.toString().equals(access))
            {
                // Site creation
                ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC, true);
                ShareUserMembers.inviteUserToSiteWithRole(drone, testUserWithAccess, getUserNameFreeDomain(testName + UserRole.COLLABORATOR), siteName,
                        UserRole.COLLABORATOR);
                ShareUser.openSiteDashboard(drone, siteName);
                ShareUserMembers.inviteUserToSiteWithRole(drone, testUserWithAccess, getUserNameFreeDomain(testName + UserRole.CONTRIBUTOR), siteName,
                        UserRole.CONTRIBUTOR);
                ShareUser.openSiteDashboard(drone, siteName);
                ShareUserMembers.inviteUserToSiteWithRole(drone, testUserWithAccess, getUserNameFreeDomain(testName + UserRole.CONSUMER), siteName,
                        UserRole.CONSUMER);
            }
            
            if (createData)
            {
                ShareUser.openSitesDocumentLibrary(drone, siteName);
                String folderNameWithAccess = folderName + access;
                String subFolderPath = folderNameWithAccess + SLASH + subFolderName;
                ShareUser.createFolderInFolder(drone, folderNameWithAccess, folderNameWithAccess, DOCLIB_CONTAINER);
                ShareUser.openSiteDashboard(drone, siteName);
                ShareUser.createFolderInFolder(drone, subFolderName, subFolderName, folderNameWithAccess);
                ShareUser.uploadFileInFolder(drone, new String[] { fileName1, folderNameWithAccess });
                ShareUser.uploadFileInFolder(drone, new String[] { fileName3, subFolderPath });
            }
            ShareUser.logout(drone);
        }
    }

    /**
     * @param testName
     * @param role
     */
    private void runTestForUser(String testName, UserRole role)
    {
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String subFolderName = getFolderName("sub" + testName);
        String testUser = getUserNameFreeDomain(testName + role);

        ShareUser.login(drone, testUser);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Select different View: "Simple View"
        docLibPage = ShareUserSitePage.selectView(drone, ViewType.SIMPLE_VIEW);
        assertEquals(docLibPage.getViewType(), ViewType.SIMPLE_VIEW);

        if (role.equals(UserRole.MANAGER))
        {
            // "Set "ANY View" as default for this folder " ("Simple View")
            docLibPage = docLibPage.getNavigation().selectSetCurrentViewToDefault().render();
            assertTrue(docLibPage.getNavigation().isRemoveDefaultViewVisible());
        }
        else
        {
            assertFalse(docLibPage.getNavigation().isSetDefaultViewVisible());
        }
        
        // Check View for: User1_folder
        docLibPage = docLibPage.selectFolder(folderName + role).render();
        assertEquals(docLibPage.getViewType(), ViewType.SIMPLE_VIEW);

        // Step 6 - 9
        visitFolders(UserRole.MANAGER, folderName, subFolderName, docLibPage, role);

        // Navigate to User2_folder , User3_folder, User4_folder and repeat steps 6-9 using other views
        // "Set default view" are applayed for User2_folder , User3_folder, User4_folder and subfolders.
        visitFolders(UserRole.COLLABORATOR, folderName, subFolderName, docLibPage, role);
        visitFolders(UserRole.CONTRIBUTOR, folderName, subFolderName, docLibPage, role);
        visitFolders(UserRole.CONSUMER, folderName, subFolderName, docLibPage, role);
    }

    /**
     * @param forRole
     * @param folderName
     * @param subFolderName
     * @param docLibPage
     * @param loginRole
     */
    private void visitFolders(UserRole forRole, String folderName, String subFolderName, DocumentLibraryPage docLibPage, UserRole loginRole)
    {
        folderName = folderName + forRole;
        docLibPage = ShareUser.openDocumentLibrary(drone);

        // Change view to Detailed View for: folder User1_folder
        docLibPage = docLibPage.selectFolder(folderName).render();
        docLibPage = ShareUserSitePage.selectView(drone, ViewType.DETAILED_VIEW);
        assertEquals(docLibPage.getViewType(), ViewType.DETAILED_VIEW);

        // Set default view is allowed for only manager or author of that folder. Consumer cannot create folder.
        if (loginRole.equals(UserRole.CONSUMER) || (!loginRole.equals(UserRole.MANAGER) && !loginRole.equals(forRole)))
        {
            assertFalse(docLibPage.getNavigation().isSetDefaultViewVisible());
        }
        else
        {
            // "Set "OTHER View" as default for this folder " ("Detailed View")
            docLibPage = docLibPage.getNavigation().selectSetCurrentViewToDefault().render();
            assertTrue(docLibPage.getNavigation().isRemoveDefaultViewVisible());
        }

        // Navigate to folder User1_subfolder: View is set "Detail VIew"
        docLibPage = docLibPage.selectFolder(subFolderName).render();
        assertEquals(docLibPage.getViewType(), ViewType.DETAILED_VIEW);

        // Set default view is allowed for only manager or author of that folder. Consumer cannot create folder.
        if (loginRole.equals(UserRole.CONSUMER) || (!loginRole.equals(UserRole.MANAGER) && !loginRole.equals(forRole)))
        {
            assertFalse(docLibPage.getNavigation().isSetDefaultViewVisible());
        }
        else
        {
            // Options "Set "OTHER View" as default for this folder " ("Detailed View") is displayed
            assertTrue(docLibPage.getNavigation().isSetDefaultViewVisible());
        }
    }

}
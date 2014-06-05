/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.share.repository;


import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.document.ConfirmDeletePage;
import org.alfresco.po.share.site.document.ConfirmDeletePage.Action;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserRepositoryPage;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Test folders from repository.
 * @author cganesh
 *
 */
@Listeners(FailedTestListener.class)
@Test(groups = {"SharePOBug" })
public class RepositoryFolderTests extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(RepositoryFolderTests.class);

    protected String testUser;
    protected String testUserPass = DEFAULT_PASSWORD;
    protected String baseFolderName = "Folderht-RepositoryFolderTests";
    protected String baseFolderPath;
    protected String baseFolderTitle = "Base folder title for FolderTests";
    protected String description = "Base folder description for FolderTests";

    /**
     * Class includes: Tests from TestLink in Area: Repository Tests
     * <ul>
     * <li>Test User logged in Navigates to repository</li>
     * <li>Test Logged user can create new folder in main page of repository</li>
     * </ul>
     */
    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();

        testUser = getUserNameFreeDomain(testName);        
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUser);
        
        logger.info("[Suite ] : Start Tests in: " + testName);
        dataPrepRepositoryFolderTests(testName);       
    }

    private void dataPrepRepositoryFolderTests(String testName) throws Exception
    {
        baseFolderName = getFolderName(testName);
        String baseFolderTitle = "Base folder for FolderTests";
        String description = "Base folder for FolderTests";
        baseFolderPath = REPO + SLASH + baseFolderName;

        // login as admin
        ShareUser.login(drone, testUser);
        
        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Create new folder
        if (!repositorypage.isFileVisible(baseFolderName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, baseFolderName, baseFolderTitle, description);
        }   
        
        ShareUtil.logout(drone);

    }

    @AfterMethod(alwaysRun=true)
    public void quit() throws Exception
    {
        ShareUser.logout(drone);
    }

    /**
     * Test:
     * <ul>
     * <li>Create new folder in main page of repository</li>
     * <li>User can add comment to folder in repository</li>
     * </ul>
     */
    @Test(groups = { "Repository" })
    public void Enterprise40x_5403() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String description = (testName) + System.currentTimeMillis();

        ShareUser.login(drone, testUser);
        
        // Navigate to repository page
        ShareUserRepositoryPage.openRepository(drone);

        RepositoryPage repositorypage = ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folderName, description, baseFolderPath);

        // verify created folder is present in the main repository
        Assert.assertTrue(repositorypage.isFileVisible(folderName), "verifying folder present in repository");

        // test case Enterprise40x-5406: Add Comment to folder in folder details page

        FolderDetailsPage folderdetailsPage = repositorypage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();

        // Adding comment in folder details page
        folderdetailsPage = folderdetailsPage.addComment(description).render();

        // Verify comment added successfully in folder details page
        Assert.assertTrue(folderdetailsPage.isCommentLinkPresent(), "Verify comment link is present");

    }

    /**
     * Test:
     * <ul>
     * <li>Type folder details in new folder form and click cancel</li>
     * </ul>
     */

    @Test(groups = { "Repository" })
    public void Enterprise40x_5404()
    {
        String testName = getTestName();
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String folderTitle = (testUser) + System.currentTimeMillis();
        String description = (testName) + System.currentTimeMillis();
        
        ShareUser.login(drone, testUser);

        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepository(drone);

        // Type folder details in new folder form and click cancel button
        NewFolderPage newFolderPage = repositorypage.getNavigation().selectCreateNewFolder().render();
        newFolderPage.typeName(folderName);
        newFolderPage.typeTitle(folderTitle);
        newFolderPage.typeDescription(description);
        repositorypage = newFolderPage.selectCancel().render();

        // verify created folder is present in the main repository
        Assert.assertFalse(repositorypage.isFileVisible(folderName), "verifying folder present in repository");

    }

    /**
     * Test:
     * <ul>
     * <li>Verify copy to action for multi-selected files- copy to repository</li>
     * </ul>
     */
    @Test(groups = { "Repository" })
    public void Enterprise40x_5338() throws Exception
    {
        String testName = getTestName();
        String folder1 = getFolderName(testName + System.currentTimeMillis());
        String file1 = getFolderName(testName + System.currentTimeMillis() + "3");
        String file2 = getFolderName(testName + System.currentTimeMillis() + "4");
        String Title1 = getTestName() + System.currentTimeMillis();
        String Description1 = getTestName() + System.currentTimeMillis();
        String Content1 = getTestName() + System.currentTimeMillis() + "1";
        String Title2 = testName + System.currentTimeMillis() + "1";
        String Description2 = getTestName() + System.currentTimeMillis() + "1";
        String Content2 = getTestName() + System.currentTimeMillis() + "2";
        String description1 = testName + System.currentTimeMillis();
        String guestHomePath = REPO + SLASH + "Guest Home";

        ShareUser.login(drone, testUser);
        
        // Navigate to repository page
        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Create new folder1 in root folder
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folder1, description1, baseFolderPath);

        // verify created folder1 is displayed in root folder
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        
        String[] baseFolderPath = new String[] { baseFolderName };
        RepositoryPage repoPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, baseFolderPath);
        
        Assert.assertTrue(repoPage.isFileVisible(folder1), "verifying folder present in baseFolderPath");

        // Create content1 in folder1
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(file1);
        contentDetails.setTitle(Title1);
        contentDetails.setDescription(Description1);
        contentDetails.setContent(Content1);

        // Create content 1 in folder1
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        
        String[] subfolderPath = new String[] { baseFolderName, folder1 };
        ShareUserRepositoryPage.createContentInFolder(drone, contentDetails, ContentType.PLAINTEXT, subfolderPath);

        // Create content2 in folder1
        ContentDetails contentdetails = new ContentDetails();
        contentdetails.setName(file2);
        contentdetails.setTitle(Title2);
        contentdetails.setDescription(Description2);
        contentdetails.setContent(Content2);

        // Create content 2 in folder1
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        
        String[] subFolderPath = new String[] { baseFolderName, folder1 };
        ShareUserRepositoryPage.createContentInFolder(drone, contentdetails, ContentType.PLAINTEXT, subFolderPath);

        // Navigate to folder1
        repoPage = repoPage.getNavigation().selectAll().render();

        // Select copy to from top menu selected items
        CopyOrMoveContentPage copyOrMoveContentPage = repoPage.getNavigation().selectCopyTo().render();
        copyOrMoveContentPage = copyOrMoveContentPage.selectPath("Repository", "Guest Home").render();
        copyOrMoveContentPage.selectOkButton().render();

        // Verify folders are copied successfully to guest home path
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        
        repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, guestHomePath);
        
        Assert.assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, file1, "isContentVisible", "", true));
        Assert.assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, file2, "isContentVisible", "", true));

    }

    /**
     * Test:
     * <ul>
     * <li>Verify Copy to-action for multi selected files- copy to sites</li>
     * </ul>
     */
    @Test(groups = { "Repository" })
    public void Enterprise40x_5339() throws Exception
    {
        String testName = getTestName();
        String folder = getFolderName(testName + System.currentTimeMillis());
        String description = testName + System.currentTimeMillis();
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String folder1 = getFolderName(testName + "1" + System.currentTimeMillis());
        String description1 = testName + System.currentTimeMillis();
        String file1 = getFolderName(testName + System.currentTimeMillis() + "3");
        String file2 = getFolderName(testName + System.currentTimeMillis() + "4");
        String Title1 = getTestName() + System.currentTimeMillis();
        String Description1 = getTestName() + System.currentTimeMillis();
        String Content1 = getTestName() + System.currentTimeMillis() + "1";
        String Content2 = getTestName() + System.currentTimeMillis() + "1";
        String Title2 = testName + System.currentTimeMillis() + "1";
        String Description2 = getTestName() + System.currentTimeMillis() + "1";

        opSiteName = "A" + opSiteName;
        
        ShareUser.login(drone, testUser);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open site from document library page
        ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        // Create new folder1 in site
        ShareUserSitePage.createFolder(drone, folder1, description1);

        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Create new folder in repository root folder
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folder, description, baseFolderPath);

        // Create content1 in folder
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(file1);
        contentDetails.setTitle(Title1);
        contentDetails.setDescription(Description1);
        contentDetails.setContent(Content1);

        // Create content 1 in folder
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        
        String[] subFolderPath = new String[] { baseFolderName, folder };
        ShareUserRepositoryPage.createContentInFolder(drone, contentDetails, ContentType.PLAINTEXT, subFolderPath);

        // Create content2 in folder
        ContentDetails contentdetails = new ContentDetails();
        contentdetails.setName(file2);
        contentdetails.setTitle(Title2);
        contentdetails.setDescription(Description2);
        contentdetails.setContent(Content2);

        // Create content 2 in folder
        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        String[] subfolderPath = new String[] { baseFolderName, folder };
        ShareUserRepositoryPage.createContentInFolder(drone, contentdetails, ContentType.PLAINTEXT, subfolderPath);

        // Select all in folder and copy to site doclib folder1
        RepositoryPage repoPage = repositorypage.getNavigation().selectAll().render();
        CopyOrMoveContentPage copyOrMoveContentPage = repoPage.getNavigation().selectCopyTo().render();
        copyOrMoveContentPage = copyOrMoveContentPage.selectPath("Repository", "Sites", opSiteName, "documentLibrary", folder1).render();
        copyOrMoveContentPage.selectOkButton().render();

        // Open folder1 from site document library page
        ShareUser.openSitesDocumentLibrary(drone, opSiteName);
        ShareUserSitePage.selectContent(drone, folder1).render();

        // verify files are copied successfully
        Assert.assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, file1, "isContentVisible", "", true),"Copied file : file1 not found");
        Assert.assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, file2, "isContentVisible", "", true), "Copied file : file2 not found");
    }

    /**
     * Test:
     * <ul>
     * <li>Verify Copy to-action for multi selected folders- copy to sites</li>
     * </ul>
     */
    @Test(groups = { "Repository", "SharePOBug" })
    public void Enterprise40x_5340() throws Exception
    {
        String testName = getTestName();
        String folder = getFolderName(testName + System.currentTimeMillis());
        String description = testName + System.currentTimeMillis();
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String folder1 = getFolderName(testName + System.currentTimeMillis() + "1");
        String description1 = testName + System.currentTimeMillis();
        String subfolder1 = getFolderName(testName + System.currentTimeMillis() + "3");
        String subfolder2 = getFolderName(testName + System.currentTimeMillis() + "4");
        String Description1 = getTestName() + System.currentTimeMillis();
        String Description2 = getTestName() + System.currentTimeMillis() + "1";

        opSiteName = "A" + opSiteName;
        
        ShareUser.login(drone, testUser);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open site from document library page
        ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        // Create new folder in site
        ShareUserSitePage.createFolder(drone, folder1, description1);

        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Create new folder
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folder, description, baseFolderPath);

        // Navigate to folder
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        
        String[] basefolderPath = new String[] { baseFolderName, folder };
        repositorypage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, basefolderPath);

        // Create sub folder1 in folder
        ShareUserSitePage.createFolder(drone, subfolder1, Description1);

        // Create sub folder2 in folder
        ShareUserSitePage.createFolder(drone, subfolder2, Description2);

        // Select all from Top menu and copy to folder1 in site doclib
        RepositoryPage repoPage = repositorypage.getNavigation().selectAll().render();
        CopyOrMoveContentPage copyOrMoveContentPage = repoPage.getNavigation().selectCopyTo().render();
        copyOrMoveContentPage = copyOrMoveContentPage.selectPath("Repository", "Sites", opSiteName, "documentLibrary", folder1).render();
        copyOrMoveContentPage.selectOkButton().render();

        // Open site from document library page
        ShareUser.openSitesDocumentLibrary(drone, opSiteName);
        ShareUserSitePage.selectContent(drone, folder1).render();

        // verify sub folder1 & sub folder2 are copied successfully
        Assert.assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, subfolder1, "isContentVisible", "", true), "Copied folder : 1 not found");
        Assert.assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, subfolder2, "isContentVisible", "", true), "Copied folder : 2 not found");
    }

    /**
     * Test:
     * <ul>
     * <li>Verify Copy to-action for multi selected folders- copy to repository</li>
     * </ul>
     */
    @Test(groups = { "Repository" })
    public void Enterprise40x_5341() throws Exception
    {
        String testName = getTestName();
        String folder = getFolderName(testName + System.currentTimeMillis());
        String description = testName + System.currentTimeMillis();
        String subfolder1 = getFolderName(testName + System.currentTimeMillis() + "3");
        String subfolder2 = getFolderName(testName + System.currentTimeMillis() + "4");
        String Description1 = getTestName() + System.currentTimeMillis();
        String Description2 = getTestName() + System.currentTimeMillis() + "1";
        String guestHomePath = REPO + SLASH + "Guest Home";
        
        ShareUser.login(drone, testUser);

        // Navigate to repository page
        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Create new folder
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folder, description, baseFolderPath);

        // Navigate to folder
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] basefolderPath = new String[] { baseFolderName, folder };
        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, basefolderPath);

        // Create sub folder1 in folder
        ShareUserSitePage.createFolder(drone, subfolder1, Description1);

        // Create sub folder2 in folder
        RepositoryPage repositorypage = ShareUserRepositoryPage.createFolderInRepository(drone, subfolder2, Description2);

        // Select all from Top menu and copy to Guest Home
        RepositoryPage repoPage = repositorypage.getNavigation().selectAll().render();
        CopyOrMoveContentPage copyOrMoveContentPage = repoPage.getNavigation().selectCopyTo().render();
        copyOrMoveContentPage = copyOrMoveContentPage.selectPath("Repository", "Guest Home").render();
        copyOrMoveContentPage.selectOkButton().render();

        // verify sub folder1 & 2 is copied successfully
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, guestHomePath);
        
        Assert.assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, subfolder1, "isContentVisible", "", true),"Verifying copied folder is present in site doclib");
        Assert.assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, subfolder2, "isContentVisible", "", true),"Verifying copied folder is present in site doclib");
    }

    /**
     * Test:
     * <ul>
     * <li>Verify Move to action for multi-selected files</li>
     * </ul>
     */
    @Test(groups = { "Repository" })
    public void Enterprise40x_5342() throws Exception
    {
        String testName = getTestName();
        String folder = getFolderName(testName + System.currentTimeMillis());
        String description = testName + System.currentTimeMillis();
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String folder1 = getFolderName(testName + "1" + System.currentTimeMillis());
        String description1 = testName + System.currentTimeMillis();
        String file1 = getFolderName(testName + System.currentTimeMillis() + "3");
        String file2 = getFolderName(testName + System.currentTimeMillis() + "4");
        String Title1 = getTestName() + System.currentTimeMillis();
        String Description1 = getTestName() + System.currentTimeMillis();
        String Content1 = getTestName() + System.currentTimeMillis() + "1";
        String Content2 = getTestName() + System.currentTimeMillis() + "1";
        String Title2 = testName + System.currentTimeMillis() + "1";
        String Description2 = getTestName() + System.currentTimeMillis() + "1";

        opSiteName = "A" + opSiteName;
        
        ShareUser.login(drone, testUser);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open site from document library page
        ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        // Create new folder in site
        ShareUserSitePage.createFolder(drone, folder1, description1);

        // Navigate to repository page
        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Create new folder
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folder, description, baseFolderPath);

        // Create content1 in folder
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(file1);
        contentDetails.setTitle(Title1);
        contentDetails.setDescription(Description1);
        contentDetails.setContent(Content1);

        // Create content 1 in folder
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        
        String[] subFolderPath = new String[] { baseFolderName, folder };
        ShareUserRepositoryPage.createContentInFolder(drone, contentDetails, ContentType.PLAINTEXT, subFolderPath);

        // Create content2 in folder
        ContentDetails contentdetails = new ContentDetails();
        contentdetails.setName(file2);
        contentdetails.setTitle(Title2);
        contentdetails.setDescription(Description2);
        contentdetails.setContent(Content2);

        // Create content 2 in folder
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] subfolderPath = new String[] { baseFolderName, folder };
        RepositoryPage repositorypage = ShareUserRepositoryPage.createContentInFolder(drone, contentdetails, ContentType.PLAINTEXT, subfolderPath);

        // Select all from Top menu in folder3 and move to folder1 in site doclib
        RepositoryPage repoPage = repositorypage.getNavigation().selectAll().render();
        CopyOrMoveContentPage copyOrMoveContentPage = repoPage.getNavigation().selectMoveTo().render();
        copyOrMoveContentPage = copyOrMoveContentPage.selectPath("Repository", "Sites", opSiteName, "documentLibrary", folder1).render();
        copyOrMoveContentPage.selectOkButton().render();

        // Open site from document library page
         ShareUser.openSitesDocumentLibrary(drone, opSiteName);
         
         ShareUserSitePage.selectContent(drone, folder1).render();

        // verify file1 & file2 are moved successfully
         
         Assert.assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, file1, "isContentVisible", "", true),"Verifying moved file is present in site doclib");
         Assert.assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, file2, "isContentVisible", "", true),"Verifying moved file is present in site doclib");
    }

    /**
     * Test:
     * <ul>
     * <li>Verify Move to action for multi-selected folders</li>
     * </ul>
     */
    @Test(groups = { "Repository", "SharePOBug" })
    public void Enterprise40x_5343() throws Exception
    {
        String testName = getTestName();
        String folder = getFolderName(testName + System.currentTimeMillis());
        String description = testName + System.currentTimeMillis();
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String folder1 = getFolderName(testName + System.currentTimeMillis() + "1");
        String description1 = testName + System.currentTimeMillis();
        String subfolder1 = getFolderName(testName + System.currentTimeMillis() + "3");
        String subfolder2 = getFolderName(testName + System.currentTimeMillis() + "4");
        String Description1 = getTestName() + System.currentTimeMillis();
        String Description2 = getTestName() + System.currentTimeMillis() + "1";

        opSiteName = "A" + opSiteName;
        
        ShareUser.login(drone, testUser);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open site from document library page
        ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        // Create new folder in site
        ShareUserSitePage.createFolder(drone, folder1, description1);

        // Navigate to repository page
        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Create new folder
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folder, description, baseFolderPath);

        // Navigate to folder
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] basefolderPath = new String[] { baseFolderName, folder };
        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, basefolderPath);

        // Create sub folder1 in folder
        ShareUserRepositoryPage.createFolderInRepository(drone, subfolder1, Description1);

        // Create sub folder2 in folder
        RepositoryPage repositorypage = ShareUserRepositoryPage.createFolderInRepository(drone, subfolder2, Description2);

        // Select all from Top menu and move to folder1 in site doclib
        RepositoryPage repopag = repositorypage.getNavigation().selectAll().render();
        CopyOrMoveContentPage copyOrMoveContentPage = repopag.getNavigation().selectMoveTo().render();
        copyOrMoveContentPage = copyOrMoveContentPage.selectPath("Repository", "Sites", opSiteName, "documentLibrary", folder1).render();
        copyOrMoveContentPage.selectOkButton().render();

        // Open folder1 in site document library page
        ShareUser.openSitesDocumentLibrary(drone, opSiteName);
        ShareUserSitePage.selectContent(drone, folder1).render();

        // verify sub folder1 & sub folder2 is copied successfully
        
        Assert.assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, subfolder1, "isContentVisible", "", true),"Verifying moved folder is present in site doclib");
        Assert.assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, subfolder2, "isContentVisible", "", true),"Verifying moved folder is present in site doclib");
    }

    /**
     * Test:
     * <ul>
     * <li>Select delete from selected items in top menu</li>
     * <li>Click delete on confirmation pop up</li>
     * <li>Verify selected items are not deleted on cancel delete</li>
     * <li>Verify selected items are deleted on delete</li>
     * </ul>
     */
    @Test(groups = { "Repository" })
    public void Enterprise40x_5344() throws Exception
    {
        String testName = getTestName();
        String folder1 = getFolderName(testName + System.currentTimeMillis());
        String folder2 = getFolderName(testName + System.currentTimeMillis() + "1");
        String folder3 = getFolderName(testName + System.currentTimeMillis() + "2");
        String file1 = getFolderName(testName + System.currentTimeMillis() + "3");
        String file2 = getFolderName(testName + System.currentTimeMillis() + "4");
        String Title1 = getTestName() + System.currentTimeMillis();
        String Description1 = getTestName() + System.currentTimeMillis();
        String Content1 = getTestName() + System.currentTimeMillis() + "1";
        String Title2 = testName + System.currentTimeMillis() + "1";
        String Description2 = getTestName() + System.currentTimeMillis() + "1";
        String Content2 = getTestName() + System.currentTimeMillis() + "2";
        String description2 = testName + System.currentTimeMillis() + "1";
        String description3 = testName + System.currentTimeMillis() + "2";

        ShareUser.login(drone, testUser);
        
        // Navigate to repository page
        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Create new folder
        RepositoryPage repoPage = ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folder1, description, baseFolderPath);

        // verify created folder is present in the main repository
        Assert.assertTrue(repoPage.isFileVisible(folder1), "verifying folder1 present in repository");

        // Navigate to folder
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        
        String[] basefolderPath = new String[] { baseFolderName, folder1 };
        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, basefolderPath);

        // Create folder2, folder3 in folder1
        ShareUserRepositoryPage.createFolderInRepository(drone, folder2, description2);
        ShareUserRepositoryPage.createFolderInRepository(drone, folder3, description3);

        // Create content1 in folder1
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(file1);
        contentDetails.setTitle(Title1);
        contentDetails.setDescription(Description1);
        contentDetails.setContent(Content1);

        // Create content 1 in folder1
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        
        String[] subFolderPath = new String[] { baseFolderName, folder1 };
        ShareUserRepositoryPage.createContentInFolder(drone, contentDetails, ContentType.PLAINTEXT, subFolderPath);

        // Create content2 in folder1
        ContentDetails contentdetails = new ContentDetails();
        contentdetails.setName(file2);
        contentdetails.setTitle(Title2);
        contentdetails.setDescription(Description2);
        contentdetails.setContent(Content2);

        // Create content 2 in folder1
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        
        String[] subfolderPath = new String[] { baseFolderName, folder1 };
        ShareUserRepositoryPage.createContentInFolder(drone, contentdetails, ContentType.PLAINTEXT, subfolderPath);

        // Select all from Top menu
        repoPage = repoPage.getNavigation().selectAll().render();

        // Confirm cancel delete
        ConfirmDeletePage confirmDeletePage = repoPage.getNavigation().selectDelete();
        confirmDeletePage.selectAction(Action.Cancel).render();

        // Verify folders are not deleted
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        
        repoPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, basefolderPath);
        Assert.assertTrue(repoPage.isFileVisible(folder2));
        Assert.assertTrue(repoPage.isFileVisible(folder3));
        Assert.assertTrue(repoPage.isFileVisible(file1));
        Assert.assertTrue(repoPage.isFileVisible(file2));

        // Select all from Top menu
        repoPage = repoPage.getNavigation().selectAll().render();

        // Confirm delete
        ConfirmDeletePage confirmdeletePage = repoPage.getNavigation().selectDelete();
        confirmdeletePage.selectAction(Action.Delete).render();

        // Verify folders are deleted
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        
        repoPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, basefolderPath);
        Assert.assertFalse(repoPage.isFileVisible(folder1));
        Assert.assertFalse(repoPage.isFileVisible(folder2));
        Assert.assertFalse(repoPage.isFileVisible(folder3));
        Assert.assertFalse(repoPage.isFileVisible(file1));
        Assert.assertFalse(repoPage.isFileVisible(file2));

    }

}

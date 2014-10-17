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
import org.alfresco.po.share.site.document.*;
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

import java.util.ArrayList;
import java.util.List;

import static org.alfresco.po.share.site.document.DocumentAspect.*;

/**
 * @author cganesh
 *
 */
@Listeners(FailedTestListener.class)
public class RepositoryFolderTests2 extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(RepositoryFolderTests2.class);

    protected String testUser;
    protected String baseFolderName = "Folderht-RepositoryFolderTests2";
    protected String baseFolderPath;
    protected String description = "Base folder for FolderTests";

    /**
     * Class includes: Tests from TestLink in Area: Repository Tests
     * <ul>
     * <li>Test User logged in Navigates to repository</li>
     * <li>Test Logged user can create new folder in main page of repository</li>
     * </ul>
     */
    @Override
    @BeforeClass(alwaysRun=true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = getUserNameFreeDomain(testName);
        
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUser);
        
        logger.info("[Suite ] : Start Tests in: " + testName);
        dataPrepRepositoryFolderTests2(testName);
        ShareUtil.logout(drone);

    }

    private void dataPrepRepositoryFolderTests2(String testName) throws Exception
    {
        baseFolderName = getFolderName(testName);        
        String baseFolderTitle = "Base folder for FolderTests";
        String description = "Base folder for FolderTests";
        baseFolderPath = REPO + SLASH + baseFolderName;        
        
        //Login as admin
        ShareUser.login(drone, testUser);
        
        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepositorySimpleView(drone);               

        // Create new folder
        if (!repositorypage.isFileVisible(baseFolderName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, baseFolderName, baseFolderTitle, description);
        }
        
    }
    
    @AfterMethod(alwaysRun=true)
    public void logout() throws Exception
    {
        ShareUser.logout(drone);
    }
    
    /**
     * Test:
     * <ul>
     * <li>Copy to any folder from more actions in repository page</li>
     * </ul>
     */
    @Test(groups = { "Repository", "SharePOBug" })
    public void AONE_3556() throws Exception
    {
        String testName = getTestName();
        System.out.println("5409testname" + testName);
        String folder1 = getFolderName(testName + System.currentTimeMillis());
        String folder2 = getFolderName(testName + "1" + System.currentTimeMillis());
        String description = testName + System.currentTimeMillis();        

        ShareUser.login(drone, testUser);
        
        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Create new folder1,2 in root folder
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folder1, description, baseFolderPath);
        ShareUserSitePage.createFolder(drone, folder2, description);

        // verify created folders 1,2 are present in the main repository
        Assert.assertTrue(repositorypage.isFileVisible(folder1), "verifying folder present in baseFolderPath");
        Assert.assertTrue(repositorypage.isFileVisible(folder2), "verifying folder present in baseFolderPath");

        // Select more options in folder1 and copy to destination folder2
        String[] destinationFolder = { baseFolderName, folder2 };
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] basefolderPath = new String[] { baseFolderName };
        repositorypage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, basefolderPath);
        ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryOk(drone, folder1, destinationFolder, true);

        // Click on folder2
        FileDirectoryInfo fileDirectoryInfo = repositorypage.getFileDirectoryInfo(folder2);
        fileDirectoryInfo.clickOnTitle();

        // verify folder1 is copied successfully
        Assert.assertTrue(repositorypage.isFileVisible(folder1),"Verifying copied folder is present in the destination folder");

    }

    /**
     * Test:
     * <ul>
     * <li>Copy to any destination site in document Library page from more
     * actions in repository page</li>
     * </ul>
     */
    @Test(groups = { "Repository", "SharePOBug"  })
    public void AONE_3557() throws Exception
    {
        String testName = getTestName();
        String folder = getFolderName(testName + System.currentTimeMillis());
        String description = testName + System.currentTimeMillis();
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();

        opSiteName = "A" + opSiteName;

        ShareUser.login(drone, testUser);
        
        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Create new folder
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folder, description, baseFolderPath);

        // verify created folder is present in the main repository
        Assert.assertTrue(repositorypage.isFileVisible(folder), "verifying folder present in repository");        

        // Select more options in folder and copy to site
        String[] destinationFolder = new String[]
        { "Sites", opSiteName, "documentLibrary" };
        ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryOk(drone, folder, destinationFolder, true);

        // Open site from document library page
        DocumentLibraryPage documentlibrarypage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        // verify folder is copied successfully
        Assert.assertTrue(documentlibrarypage.isFileVisible(folder),"Verifying copied folder is present in site doclib");

    }

    /**
     * Test:
     * <ul>
     * <li>Copy to any folder from more actions in repository page and click
     * cancel</li>
     * </ul>
     */
    @Test (groups = { "Repository", "SharePOBug"  })
    public void AONE_3558() throws Exception
    {
        String testName = getTestName();
        String folder1 = getFolderName(testName + System.currentTimeMillis());
        String folder2 = getFolderName(testName + System.currentTimeMillis() + "1");        
        String description = testName + System.currentTimeMillis();     
        
        ShareUser.login(drone, testUser);       
        
        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Create new folder1,2 in root folder
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folder1, description, baseFolderPath);
        ShareUserSitePage.createFolder(drone, folder2, description);

        // Verify created folders are present in the main repository
        Assert.assertTrue(repositorypage.isFileVisible(folder1), "verifying folder present in repository");
        Assert.assertTrue(repositorypage.isFileVisible(folder2), "verifying folder present in repository");

        // Select more options in folder1 and copy to destination folder2 and cancel
        String[] destinationFolder = {baseFolderName, folder2};
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] basefolderPath = new String[] {baseFolderName};
        repositorypage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, basefolderPath);
        ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryCancel(drone,folder1,destinationFolder,true);                     
            
        // Click on folder2            
        FileDirectoryInfo fileDirectoryInfo = repositorypage.getFileDirectoryInfo(folder2);
        fileDirectoryInfo.clickOnTitle();            

        // Verify folder1 is not copied successfully
        Assert.assertFalse(repositorypage.isFileVisible(folder1),"Verifying copied folder is not present in the destination folder");
        
    }    

    /**
     * Test:
     * <ul>
     * <li>Move to any folder from more actions in repository page</li>
     * </ul>
     */
    @Test (groups = { "Repository", "SharePOBug"  })
    public void AONE_3560() throws Exception
    {
        String testName = getTestName();
        String folder1 = getFolderName(testName + System.currentTimeMillis());
        String folder2 = getFolderName(testName + System.currentTimeMillis() + "1");       
        String description = testName + System.currentTimeMillis();   
        
        ShareUser.login(drone, testUser);
        
        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Create new folder1
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folder1, description, baseFolderPath);
        ShareUserSitePage.createFolder(drone, folder2, description);

         // verify created folder1,2 are present in the main repository
         Assert.assertTrue(repositorypage.isFileVisible(folder1), "verifying folder present in repository");
         Assert.assertTrue(repositorypage.isFileVisible(folder2), "verifying folder present in repository");

         // Select more options in folder1 and move to destination folder2 
         String[] destinationFolder = {baseFolderName, folder2};
         ShareUserRepositoryPage.openRepositorySimpleView(drone);
         String[] basefolderPath = new String[] {baseFolderName};
         repositorypage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, basefolderPath);
            
         ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryOk(drone,folder1,destinationFolder,false);         

         //verify folder1 is not present in base folder 
         Assert.assertFalse(repositorypage.isFileVisible(folder1),"Verifying moved folder is not present in base folder");
            
         // Click on folder2
         FileDirectoryInfo fileDirectoryInfo = repositorypage.getFileDirectoryInfo(folder2);
         fileDirectoryInfo.clickOnTitle();
         RepositoryPage repositoryPage = drone.getCurrentPage().render();

         // verify folder1 is moved successfully
         Assert.assertTrue(repositoryPage.isFileVisible(folder1),"Verifying moved folder is present in the destination folder");            
        
    }
    
    /**
     * Test:
     * <ul>
     * <li>Move to any destination site in document Library page from more
     * actions in repository page</li>
     * </ul>
     */
    @Test (groups = { "Repository", "SharePOBug"  })
    public void AONE_3561() throws Exception
    {
        String testName = getTestName();
        String folder = getFolderName(testName + System.currentTimeMillis());        
        String description = testName + System.currentTimeMillis();
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();     
                 
        opSiteName = "A" + opSiteName;

        ShareUser.login(drone, testUser);
        
        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Create new folder
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folder, description, baseFolderPath);

        // verify created folder is present in the main repository root folder
        Assert.assertTrue(repositorypage.isFileVisible(folder), "verifying folder present in repository root folder");
                 
        // Select more options in folder and move to site
        String[] destinationFolder = new String[] {"Sites",opSiteName,"documentLibrary"};            
        ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryOk(drone,folder,destinationFolder,false);
            
        //verify moved folder is not present in base folder
        Assert.assertFalse(repositorypage.isFileVisible(folder),"Verifying moved folder is not present in base folder");
            
        // Select site from repository page
        DocumentLibraryPage documentlibrarypage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        //verify folder is moved successfully to doclib
        Assert.assertTrue(documentlibrarypage.isFileVisible(folder),"Verifying moved folder is present in site doclib");

    }  

    /**
     * Test:
     * <ul>
     * <li>Move to any folder and click cancel from more actions in repository
     * page</li>
     * </ul>
     */
    @Test (groups = { "Repository", "SharePOBug"  })
    public void AONE_3562() throws Exception
    {
        String testName = getTestName();
        String folder1 = getFolderName(testName + System.currentTimeMillis());
        String folder2 = getFolderName(testName + System.currentTimeMillis() + "1");        
        String description = testName + System.currentTimeMillis();      
                              
        ShareUser.login(drone, testUser);
        
        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Create new folder1,2 in repository root folder
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folder1, description, baseFolderPath);
        ShareUserSitePage.createFolder(drone, folder2, description);

        // verify created folders are present in the main repository
        Assert.assertTrue(repositorypage.isFileVisible(folder1), "verifying folder present in repository");
        Assert.assertTrue(repositorypage.isFileVisible(folder2), "verifying folder present in repository");

        // Select more options in folder1 and move to destination folder2 and select cancel
        String[] destinationFolder = {baseFolderName, folder2};
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] basefolderPath = new String[] {baseFolderName};
        repositorypage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, basefolderPath);
        ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryCancel(drone,folder1,destinationFolder,false);            

        // verify cancel moved folder1 is present in base folder            
        Assert.assertTrue(repositorypage.isFileVisible(folder1),"Verifying folder is present in base folder");
            
        // Click on folder2
        FileDirectoryInfo fileDirectoryInfo = repositorypage.getFileDirectoryInfo(folder2);
        fileDirectoryInfo.clickOnTitle();
        RepositoryPage repositoryPage = drone.getCurrentPage().render();

        // verify folder1 is not moved successfully            
        Assert.assertFalse(repositoryPage.isFileVisible(folder1),"Verifying folder is not present in the destination folder");
        
    }    
   
    /**
     * Test:
     * <ul>
     * <li>Select More options for any folder in repository</li>
     * <li>Click on Manage Aspects from More options</li>
     * <li>Verify Aspects for folder name is displayed</li>
     * <li>Move any aspect from left hand side to right hand side</li>
     * <li>Click apply changes</li>
     * <li>verify aspects are applied successfully</li>
     * </ul>
     */
    @Test (groups = { "Repository" })
    public void AONE_3567() throws Exception
    {
        String testName = getTestName();
        String folder1 = getFolderName(testName + System.currentTimeMillis());        
        String description = testName + System.currentTimeMillis();    
               
        ShareUser.login(drone, testUser);
        
        // Navigate to repository page
        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Create new folder
        RepositoryPage repositorypage = ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folder1, description, baseFolderPath);

        // verify created folders are present in the main repository
        Assert.assertTrue(repositorypage.isFileVisible(folder1), "verifying folder present in repository");

        // Select more options in folder1 and click on Manage permissions
        SelectAspectsPage selectAspectsPage = repositorypage.getFileDirectoryInfo(folder1).selectManageAspects().render();

        // Get several aspects in left hand side
        List<DocumentAspect> aspects = new ArrayList<>();
        aspects.add(VERSIONABLE);
        aspects.add(CLASSIFIABLE);
        aspects.add(EFFECTIVITY);

        // Add several aspects to right hand side
        selectAspectsPage = selectAspectsPage.add(aspects).render();

        // Verify assert added to currently selected right hand side
        Assert.assertTrue(selectAspectsPage.getSelectedAspects().contains(DocumentAspect.VERSIONABLE));
        Assert.assertTrue(selectAspectsPage.getSelectedAspects().contains(DocumentAspect.CLASSIFIABLE));
        Assert.assertTrue(selectAspectsPage.getSelectedAspects().contains(DocumentAspect.EFFECTIVITY));

        // remove aspects from list
        aspects.remove(CLASSIFIABLE);
        aspects.remove(EFFECTIVITY);

        // Remove aspect from right hand side
        selectAspectsPage = selectAspectsPage.remove(aspects).render();

        // Verify assert added to currently selected right hand side
        Assert.assertFalse(selectAspectsPage.getSelectedAspects().contains(DocumentAspect.VERSIONABLE));

        // Click on Apply changes on select aspects page
        selectAspectsPage.clickApplyChanges().render();

        // Select more options in folder1 and click on Manage Aspects
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        
        String[] basefolderPath = new String[] {baseFolderName};        
        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, basefolderPath);
        
        SelectAspectsPage selectaspectsPage = ShareUserSitePage.getFileDirectoryInfo(drone, folder1).selectManageAspects().render();

        // Verify changes are updated successfully
        Assert.assertTrue(selectaspectsPage.getSelectedAspects().contains(DocumentAspect.CLASSIFIABLE));
        Assert.assertTrue(selectaspectsPage.getSelectedAspects().contains(DocumentAspect.EFFECTIVITY));
        
    }
    
    /**
     * Test:
     * <ul>
     * <li>Select More options for any folder in repository</li>
     * <li>Click on Manage Aspects from More options</li>
     * <li>Verify Aspects for folder name is displayed</li>
     * <li>Move any aspect from left hand side to right hand side</li>
     * <li>Click apply changes and cancel</li>
     * <li>verify aspects are not applied successfully</li>
     * </ul>
     */
    @Test (groups = { "Repository" })
    public void AONE_3568() throws Exception
    {
        String testName = getTestName();
        String folder1 = getFolderName(testName + System.currentTimeMillis());        
        String description = testName + System.currentTimeMillis();
        
        ShareUser.login(drone, testUser);
        
        // Navigate to repository page
        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Create new folder
        RepositoryPage repositorypage = ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folder1, description, baseFolderPath);

        // verify created folders are present in the main repository
        Assert.assertTrue(repositorypage.isFileVisible(folder1), "verifying folder present in repository");

        // Select more options in folder1 and click on Manage Aspects
        SelectAspectsPage selectAspectsPage = repositorypage.getFileDirectoryInfo(folder1).selectManageAspects().render();

        // Get several aspects in left hand side
        List<DocumentAspect> aspects = new ArrayList<>();
        aspects.add(VERSIONABLE);
        aspects.add(CLASSIFIABLE);
        aspects.add(EFFECTIVITY);

        // Add several aspects to right hand side
        selectAspectsPage = selectAspectsPage.add(aspects).render();

        // Verify assert added to currently selected right hand side
        Assert.assertTrue(selectAspectsPage.getSelectedAspects().contains(DocumentAspect.VERSIONABLE));
        Assert.assertTrue(selectAspectsPage.getSelectedAspects().contains(DocumentAspect.CLASSIFIABLE));
        Assert.assertTrue(selectAspectsPage.getSelectedAspects().contains(DocumentAspect.EFFECTIVITY));

        // remove aspects from list
        aspects.remove(VERSIONABLE);
        aspects.remove(EFFECTIVITY);

        // Remove aspect from right hand side
        selectAspectsPage = selectAspectsPage.remove(aspects).render();

        // Verify assert added to currently selected right hand side
        Assert.assertTrue(selectAspectsPage.getSelectedAspects().contains(DocumentAspect.VERSIONABLE));
        Assert.assertTrue(selectAspectsPage.getSelectedAspects().contains(DocumentAspect.EFFECTIVITY));

        // Click on cancel button in select aspects page
        selectAspectsPage.clickCancel().render();
            
        // Select more options in folder1 and click on Manage Aspects             
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        
        String[] basefolderPath = new String[] {baseFolderName};
        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, basefolderPath);
        
        SelectAspectsPage selectaspectsPage = ShareUserSitePage.getFileDirectoryInfo(drone, folder1).selectManageAspects().render();

        // Verify changes are updated successfully
        Assert.assertTrue(selectaspectsPage.getAvailableAspects().contains(DocumentAspect.VERSIONABLE));
        Assert.assertTrue(selectaspectsPage.getAvailableAspects().contains(DocumentAspect.EFFECTIVITY));
        Assert.assertTrue(selectaspectsPage.getAvailableAspects().contains(DocumentAspect.CLASSIFIABLE));        
    }

    /**
     * Test:
     * <ul>
     * <li>Copy any folder with documents from repository to User Home page and cancel</li>
     * </ul>
     */
    @Test (groups = { "Repository", "SharePOBug"  })
    public void AONE_3574() throws Exception
    {
        String testName = getTestName();
        String folder = getFolderName(testName + System.currentTimeMillis());
        String description = testName + System.currentTimeMillis();
        String fileName1 = getTestName() + System.currentTimeMillis();
        String Title1 = getTestName() + System.currentTimeMillis();
        String Description1 = getTestName() + System.currentTimeMillis();
        String Content1 = getTestName() + System.currentTimeMillis();
        String fileName2 = getTestName()  + "1" + System.currentTimeMillis();
        String Title2 = getTestName()  + "1" + System.currentTimeMillis();
        String Description2 = getTestName() + "1" + System.currentTimeMillis();
        String Content2 = getTestName() + "1" + System.currentTimeMillis();

        ShareUser.login(drone, testUser);

        // Navigate to repository page
        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Create new folder
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folder, description, baseFolderPath);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        String[] basefolderPath = new String[] {baseFolderName};
        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, basefolderPath);

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setTitle(Title1);
        contentDetails.setDescription(Description1);
        contentDetails.setContent(Content1);

        //Create content 1 in folder
        ShareUserRepositoryPage.createContentInFolder(drone, contentDetails, ContentType.PLAINTEXT, folder);

        ContentDetails contentdetails = new ContentDetails();
        contentdetails.setName(fileName2);
        contentdetails.setTitle(Title2);
        contentdetails.setDescription(Description2);
        contentdetails.setContent(Content2);

        //Create content2 in folder
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, basefolderPath);

        ShareUserRepositoryPage.createContentInFolder(drone, contentdetails, ContentType.PLAINTEXT, folder);

        // Select more options in folder1 and move to destination folder2
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, basefolderPath);

        // click Copy To action from More actions menu for the folder created in pre-conditions
        CopyOrMoveContentPage copyOrMoveContentPage = ShareUserSitePage.getFileDirectoryInfo(drone, folder).selectCopyTo().render();

        // There are 6 buttons for select destination:
        List<String> destinations = copyOrMoveContentPage.getDestinations();

        for(String destination : new String[]{"Recent Sites", "Favorite Sites", "All Sites", "Repository", "Shared Files", "My Files"})
            Assert.assertTrue(destinations.contains(destination));

        Assert.assertTrue(copyOrMoveContentPage.getDialogTitle().equals("Copy " + folder + " to..."));

        // two enabled buttons Copy and Cancel
        Assert.assertTrue(copyOrMoveContentPage.isOkButtonPresent());
        Assert.assertTrue(copyOrMoveContentPage.isCancelButtonPresent());

        // click My Files button and click Cancel button
        copyOrMoveContentPage.selectDestination("My Files").render().selectCancelButton().render();

        // open user's home folder (Repository->User Homes-><user_name>);
        RepositoryPage repositorypage = ShareUserRepositoryPage.openUserFromUserHomesFolderOfRepository(drone, testUser);

        // verify copied item isn't present in the folder;
        Assert.assertFalse(repositorypage.isFileVisible(folder));

        ShareUser.logout(drone);
    }

    /**
     * Test:
     * <ul>
     * <li>Move any folder with documents from repository to User Home page</li>
     * </ul>
     */
    @Test (groups = { "Repository", "SharePOBug" })
    public void AONE_3575() throws Exception
    {
        String testName = getTestName();
        String folder = getFolderName(testName + System.currentTimeMillis());
        String description = testName + System.currentTimeMillis();
        String fileName1 = getTestName() + System.currentTimeMillis();
        String Content1 = getTestName() + System.currentTimeMillis();
        String fileName2 = getTestName()  + "1" + System.currentTimeMillis();
        String Content2 = getTestName() + "1" + System.currentTimeMillis();

        ShareUser.login(drone, testUser);

        // Navigate to repository page
        ShareUserRepositoryPage.openRepositoryDetailedView(drone);

        // Create new folder
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folder, description, baseFolderPath);

        // marked folder as favorite
        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, folder);
        fileInfo.selectFavourite();

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        String[] basefolderPath = new String[] {baseFolderName};
        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, basefolderPath);

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setContent(Content1);

        //Create content 1 in folder
        ShareUserRepositoryPage.createContentInFolder(drone, contentDetails, ContentType.PLAINTEXT, folder);

        ContentDetails contentdetails = new ContentDetails();
        contentdetails.setName(fileName2);
        contentdetails.setContent(Content2);

        //Create content2 in folder
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, basefolderPath);

        ShareUserRepositoryPage.createContentInFolder(drone, contentdetails, ContentType.PLAINTEXT, folder);

        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, baseFolderName, folder).render();

        // mark files as favorite
        for(String file : new String[] {fileName1, fileName2})
        {
            fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, file);
            fileInfo.selectFavourite();
            webDriverWait(drone, 3000);
        }

        // Select more options in folder and move to My Files
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, basefolderPath);

        // click Move To action from More actions menu for the folder created in pre-conditions
        CopyOrMoveContentPage copyOrMoveContentPage = ShareUserSitePage.getFileDirectoryInfo(drone, folder).selectMoveTo().render();

        // There are 6 buttons for select destination:
        List<String> destinations = copyOrMoveContentPage.getDestinations();

        for(String destination : new String[]{"Recent Sites", "Favorite Sites", "All Sites", "Repository", "Shared Files", "My Files"})
            Assert.assertTrue(destinations.contains(destination));

        Assert.assertTrue(copyOrMoveContentPage.getDialogTitle().equals("Move " + folder + " to..."));

        // two enabled buttons Copy and Cancel
        Assert.assertTrue(copyOrMoveContentPage.isOkButtonPresent());
        Assert.assertTrue(copyOrMoveContentPage.isCancelButtonPresent());

        // click My Files button and click Ok button
        RepositoryPage repositorypage = copyOrMoveContentPage.selectDestination("My Files").render().selectOkButton().render();

        // verify that folder is not present already
        Assert.assertFalse(repositorypage.isFileVisible(folder));

        // open user's home folder (Repository->User Homes-><user_name>);
        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        repositorypage = ShareUserRepositoryPage.openUserFromUserHomesFolderOfRepository(drone, testUser);

        // verify moved item isn't present in the folder;
        Assert.assertTrue(repositorypage.isFileVisible(folder));

        // verify that folder marked as favorite and item's in it marked the same too
        Assert.assertTrue(ShareUserSitePage.getFileDirectoryInfo(drone, folder).isFavourite());

        repositorypage.selectFolder(folder).render();

        Assert.assertTrue(ShareUserSitePage.getFileDirectoryInfo(drone, fileName1).isFavourite());
        Assert.assertTrue(ShareUserSitePage.getFileDirectoryInfo(drone, fileName2).isFavourite());

        // try to Move folder to the same place
        ShareUserRepositoryPage.openUserFromUserHomesFolderOfRepository(drone, testUser);
        webDriverWait(drone, 2000);
        // click Move To action from More actions menu for the folder created in pre-conditions
        copyOrMoveContentPage = ShareUserSitePage.getFileDirectoryInfo(drone, folder).selectMoveTo().render();

        // There are 6 buttons for select destination:
        for(String destination : new String[]{"Recent Sites", "Favorite Sites", "All Sites", "Repository", "Shared Files", "My Files"})
            Assert.assertTrue(destinations.contains(destination));

        Assert.assertTrue(copyOrMoveContentPage.getDialogTitle().equals("Move " + folder + " to..."));

        // two enabled buttons Copy and Cancel
        Assert.assertTrue(copyOrMoveContentPage.isOkButtonPresent());
        Assert.assertTrue(copyOrMoveContentPage.isCancelButtonPresent());

        // click My Files button and click Ok button
        copyOrMoveContentPage.selectDestination("My Files").render().selectOkButton().render();
        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        repositorypage = ShareUserRepositoryPage.openUserFromUserHomesFolderOfRepository(drone, testUser);

        // verify that folder is present already
        Assert.assertTrue(repositorypage.isFileVisible(folder));
        Assert.assertTrue(ShareUserSitePage.getFileDirectoryInfo(drone, folder).isFavourite());

        ShareUser.logout(drone);

    }

    /**
     * Test:
     * <ul>
     * <li>Move any folder with documents from repository to User Home page and cancel</li>
     * </ul>
     */
    @Test (groups = { "Repository" , "SharePOBug" })
    public void AONE_3576() throws Exception
    {
        String testName = getTestName();
        String folder = getFolderName(testName + System.currentTimeMillis());
        String description = testName + System.currentTimeMillis();
        String fileName1 = getTestName() + System.currentTimeMillis();
        String Content1 = getTestName() + System.currentTimeMillis();
        String fileName2 = getTestName()  + "1" + System.currentTimeMillis();
        String Content2 = getTestName() + "1" + System.currentTimeMillis();

        ShareUser.login(drone, testUser);

        // Navigate to repository page
        ShareUserRepositoryPage.openRepositoryDetailedView(drone).render();

        // Create new folder
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folder, description, baseFolderPath);

        // marked folder as favorite
        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, folder);
        fileInfo.selectFavourite();

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, baseFolderName);

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setContent(Content1);

        //Create content 1 in folder
        ShareUserRepositoryPage.createContentInFolder(drone, contentDetails, ContentType.PLAINTEXT, folder);

        ContentDetails contentdetails = new ContentDetails();
        contentdetails.setName(fileName2);
        contentdetails.setContent(Content2);

        //Create content2 in folder
        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, baseFolderName);

        ShareUserRepositoryPage.createContentInFolder(drone, contentdetails, ContentType.PLAINTEXT, folder);

        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, baseFolderName, folder).render();

        // mark files as favorite
        for(String file : new String[] {fileName1, fileName2})
        {
            fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, file);
            fileInfo.selectFavourite();
            webDriverWait(drone, 3000);
        }

        // Select more options in folder and move to My Files
        ShareUserRepositoryPage.openRepositorySimpleView(drone).render();

        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, baseFolderName);

        // click Move To action from More actions menu for the folder created in pre-conditions
        CopyOrMoveContentPage copyOrMoveContentPage = ShareUserSitePage.getFileDirectoryInfo(drone, folder).selectMoveTo().render();

        // There are 6 buttons for select destination:
        List<String> destinations = copyOrMoveContentPage.getDestinations();

        for(String destination : new String[]{"Recent Sites", "Favorite Sites", "All Sites", "Repository", "Shared Files", "My Files"})
            Assert.assertTrue(destinations.contains(destination));

        Assert.assertTrue(copyOrMoveContentPage.getDialogTitle().equals("Move " + folder + " to..."));

        // two enabled buttons Copy and Cancel
        Assert.assertTrue(copyOrMoveContentPage.isOkButtonPresent());
        Assert.assertTrue(copyOrMoveContentPage.isCancelButtonPresent());

        // click My Files button and click Cancel button
        RepositoryPage repositorypage = copyOrMoveContentPage.selectDestination("My Files").render().selectCancelButton().render();

        // verify that folder is present
        Assert.assertTrue(repositorypage.isFileVisible(folder));

        repositorypage = ShareUserRepositoryPage.openUserFromUserHomesFolderOfRepository(drone, testUser);

        // verify that folder isn't present
        Assert.assertFalse(repositorypage.isFileVisible(folder));

        ShareUser.logout(drone);

    }
}
   
    
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



import static org.alfresco.po.share.site.document.DocumentAspect.CLASSIFIABLE;
import static org.alfresco.po.share.site.document.DocumentAspect.EFFECTIVITY;
import static org.alfresco.po.share.site.document.DocumentAspect.VERSIONABLE;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DocumentAspect;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.SelectAspectsPage;
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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author cganesh
 *
 */
@Listeners(FailedTestListener.class)
public class RepositoryFolderTests2 extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(RepositoryFolderTests2.class);

    protected String testUser;
    protected String testUserPass = DEFAULT_PASSWORD;
    protected String baseFolderName = "Folderht-RepositoryFolderTests2";
    protected String baseFolderPath;
    protected String baseFolderTitle = "Base folder for FolderTests";
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
    public void Enterprise40x_5409() throws Exception
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
    public void Enterprise40x_5410() throws Exception
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
    public void Enterprise40x_5411() throws Exception
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
    public void Enterprise40x_5413() throws Exception
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
    public void Enterprise40x_5414() throws Exception
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
    public void Enterprise40x_5415() throws Exception
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
    public void Enterprise40x_5420() throws Exception
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
        List<DocumentAspect> aspects = new ArrayList<DocumentAspect>();
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
    public void Enterprise40x_5421() throws Exception
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
        List<DocumentAspect> aspects = new ArrayList<DocumentAspect>();
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
    public void Enterprise40x_5427() throws Exception
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
        String userHomePath = REPO + SLASH + "User Homes";              
       
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
        String[] file1Path = new String[] {folder};
        ShareUserRepositoryPage.createContentInFolder(drone, contentDetails, ContentType.PLAINTEXT, file1Path);

        ContentDetails contentdetails = new ContentDetails();
        contentdetails.setName(fileName2);
        contentdetails.setTitle(Title2);
        contentdetails.setDescription(Description2);
        contentdetails.setContent(Content2);
            
        //Create content2 in folder
        ShareUserRepositoryPage.openRepositorySimpleView(drone);         
        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, basefolderPath);
        
        String[] filepath = new String[] {folder};
        ShareUserRepositoryPage.createContentInFolder(drone, contentdetails, ContentType.PLAINTEXT, filepath);                
                            
        // Select more options in folder1 and move to destination folder2 
        String[] destinationFolder = {"User Homes"};
        ShareUserRepositoryPage.openRepositorySimpleView(drone);            
        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, basefolderPath);            
        ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryCancel(drone,folder,destinationFolder,false);
                       
        // Navigate to User Homes
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        RepositoryPage Repositorypage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, userHomePath);            
            
        // verify folder is not copied successfully
        Assert.assertFalse(Repositorypage.isFileVisible(folder),"Verifying copied folder is not present in the destination folder");                    
    }
    
    /**
     * Test:
     * <ul>
     * <li>Move any folder with documents from repository to User Home page</li>
     * </ul>
     */
    @Test (groups = { "Repository", "SharePOBug" })
    public void Enterprise40x_5428() throws Exception
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
        //String userHomePath = REPO + SLASH + "User Homes";
        //String baseFolderName = "Folderht1-RepositoryFolderTests2";
        
        ShareUser.login(drone, testUser);
        
        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepositorySimpleView(drone);          
            
        // Create new folder
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folder, description, baseFolderPath);
            
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
    
        String[] basefolderPath = new String[] {baseFolderName};
        repositorypage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, basefolderPath);            
           
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setTitle(Title1);
        contentDetails.setDescription(Description1);
        contentDetails.setContent(Content1);
            
        //Create content 1 in folder
        String[] file1Path = new String[] {folder};
        ShareUserRepositoryPage.createContentInFolder(drone, contentDetails, ContentType.PLAINTEXT, file1Path);

        ContentDetails contentdetails = new ContentDetails();
        contentdetails.setName(fileName2);
        contentdetails.setTitle(Title2);
        contentdetails.setDescription(Description2);
        contentdetails.setContent(Content2);
            
        //Create content2 in folder
        ShareUserRepositoryPage.openRepositorySimpleView(drone);         
        
        repositorypage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, basefolderPath);
        
        String[] filepath = new String[] {folder};
        ShareUserRepositoryPage.createContentInFolder(drone, contentdetails, ContentType.PLAINTEXT, filepath);                 
                          
        // Select more options in folder and move to UserHomes
        String[] destinationFolder = {"User Homes"};
        ShareUserRepositoryPage.openRepositorySimpleView(drone);            
        
        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, basefolderPath);            
        repositorypage = ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryOk(drone,folder,destinationFolder,false);
            
        //Verify moved folder not present in base folder
        Assert.assertFalse(repositorypage.isFileVisible(folder),"Verifying moved folder is not present in base folder");
           
        // Navigate to User Home
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        
        String[] userHomePath = new String[] {"User Homes"};
        RepositoryPage Repositorypage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, userHomePath);            
            
        // verify folder is moved successfully
        Assert.assertTrue(Repositorypage.isFileVisible(folder),"Verifying moved folder is present in the destination folder");            
        
    }    /**
     * Test:
     * <ul>
     * <li>Move any folder with documents from repository to User Home page and cancel</li>
     * </ul>
     */
    @Test (groups = { "Repository" , "SharePOBug" })
    public void Enterprise40x_5429() throws Exception
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
        String userHomePath = REPO + SLASH + "User Homes";        
        
        ShareUser.login(drone, testUser);
        
        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepositorySimpleView(drone);          
            
        // Create new folder
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folder, description, baseFolderPath);
            
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        
        String[] basefolderPath = new String[] {baseFolderName};
        repositorypage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, basefolderPath);            
           
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setTitle(Title1);
        contentDetails.setDescription(Description1);
        contentDetails.setContent(Content1);
            
        //Create content 1 in folder
        String[] file1Path = new String[] {folder};
        ShareUserRepositoryPage.createContentInFolder(drone, contentDetails, ContentType.PLAINTEXT, file1Path);

        ContentDetails contentdetails = new ContentDetails();
        contentdetails.setName(fileName2);
        contentdetails.setTitle(Title2);
        contentdetails.setDescription(Description2);
        contentdetails.setContent(Content2);
            
        //Create content2 in folder
        ShareUserRepositoryPage.openRepositorySimpleView(drone);         
        
        repositorypage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, basefolderPath);
        
        String[] filepath = new String[] {folder};
        ShareUserRepositoryPage.createContentInFolder(drone, contentdetails, ContentType.PLAINTEXT, filepath);                    
            
        // Select more options in folder and move to UserHomes
        String[] destinationFolder = {"User Homes"};
        ShareUserRepositoryPage.openRepositorySimpleView(drone);            
        
        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, basefolderPath);            
        
        repositorypage = ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryCancel(drone,folder,destinationFolder,false);
            
        //Verify moved folder present in base folder after move cancel
        Assert.assertTrue(repositorypage.isFileVisible(folder),"Verifying moved folder is not present in base folder");
                       
        //Navigate to User Homes
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        
        repositorypage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, userHomePath);            
            
        // Verify folder is not moved successfully
        Assert.assertFalse(repositorypage.isFileVisible(folder),"Verifying move cancelled folder is not present in the destination folder");
            
    }    
}
   
    
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.site.document;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.workflow.*;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDroneImpl;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 * @author Roman.Chul
 */
@Test(enabled = true)
@Listeners(FailedTestListener.class)
public class ManageDocumentsTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(ManageDocumentsTest.class);
    protected String testUser;
    protected String siteName = "";
    
    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        // super.setupCustomDrone(WebDroneType.DownLoadDrone);
        testName = this.getClass().getSimpleName();
        testUser = getUserNameFreeDomain(testName);
        logger.info("Start Tests in: " + testName);

    }

    // TODO: If all tests are EnterpriseOnly, move test class to Enterprise package
    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_ALF_2898() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "EnterpriseOnly")
    public void ALF_2898()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName = getFileName(testName) + ".txt";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Edit Offline
        ShareUserSitePage.getFileDirectoryInfo(drone, fileName).selectEditOffline().render();
        assertEquals(ShareUserSitePage.getFileDirectoryInfo(drone, fileName).getContentInfo(), "This document is locked by you for offline editing.");

        // Navigate to Details page of document and Edit Properties
        ShareUserSitePage.selectContent(drone, fileName);

        // TODO: Implement a share-po method to click on the edit-metadata icon in properties panel
        // NavigateTo is used as DetailsPage.selectEditProperties is not available for edited offline files; but available from Properties panel;
        String url = drone.getCurrentUrl();
        drone.navigateTo(url.replace("document-details", "edit-metadata"));
        EditDocumentPropertiesPage editDocumentPropertiesPage = drone.getCurrentPage().render();
        
        // Try to rename file: Field Name is disabled, user can not edit it
        try
        {
            editDocumentPropertiesPage.setName(fileName + "edited");
            Assert.fail("Error: Filename should not be editable for files opened for offline editing");
        }
        catch (Exception ex)
        {
            logger.info("Editing not available as file input is disabled");
        }

        // Check File is not renamed
        ShareUser.openDocumentLibrary(drone);
        
        Assert.assertNotNull(ShareUserSitePage.getFileDirectoryInfo(drone, fileName));
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_ALF_2897() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String fileName = getFileName(testName) + "\u65E5\u672C\u8A9E\u30D5\u30A1.txt";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "EnterpriseOnly")
    public void ALF_2897()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName = getFileName(testName) + "\u65E5\u672C\u8A9E\u30D5\u30A1.txt";

        try
        {
            // TODO: If customDrone can be used for all tests in this class, consider setting up customDrone in before class.
            customDrone = ((WebDroneImpl) ctx.getBean(WebDroneType.DownLoadDrone.getName()));
            dronePropertiesMap.put(customDrone, (ShareTestProperty) ctx.getBean("shareTestProperties"));
            maxWaitTime = ((WebDroneImpl) customDrone).getMaxPageRenderWaitTime();

            // Login
            ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);
            
            ShareUser.openSitesDocumentLibrary(customDrone, siteName);

            // Download file
            ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName).selectDownload();

            // Check the file is downloaded successfully
            getSharePage(customDrone).waitForFile(downloadDirectory + fileName);
            
            List<String> extractedChildFilesOrFolders = ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory);
            assertTrue(extractedChildFilesOrFolders.contains(fileName));
        }
        finally
        {
            try
            {
                customDrone.quit();
            }
            catch (Throwable ex)
            {
                logger.info(ex.getMessage());
            }
        }
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_ALF_2896() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);

        // User
        String testUser1 = getUserNameFreeDomain(testName + "1");
        String[] testUserInfo1 = new String[] { testUser1 };

        String testUser2 = getUserNameFreeDomain(testName + "2");
        String[] testUserInfo2 = new String[] { testUser2 };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        // Login
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // invite userB to Site as Collaborator
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName, UserRole.COLLABORATOR);
        
        ShareUser.logout(drone);
    }

    @Test(groups = "EnterpriseOnly")
    public void ALF_2896() throws Exception
    {
        testName = getTestName();

        String siteName = getSiteName(testName);
        
        String fileName = getFileName(testName) + ".txt";
        
        String testUser1 = getUserNameFreeDomain(testName + "1");
        String testUser2 = getUserNameFreeDomain(testName + "2");
        DocumentLibraryPage documentLibraryPage;

        try
        {
            customDrone = ((WebDroneImpl) ctx.getBean(WebDroneType.DownLoadDrone.getName()));
            dronePropertiesMap.put(customDrone, (ShareTestProperty) ctx.getBean("shareTestProperties"));
            maxWaitTime = ((WebDroneImpl) customDrone).getMaxPageRenderWaitTime();

            // Login
            ShareUser.login(customDrone, testUser1, DEFAULT_PASSWORD);
            ShareUser.openSitesDocumentLibrary(customDrone, siteName).render();

            // Upload File
            String[] fileInfo = { fileName, DOCLIB };
            ShareUser.uploadFileInFolder(customDrone, fileInfo);

            // Edit Offline
            documentLibraryPage = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName).selectEditOffline();

            // Check the file is downloaded successfully
            String editedFileName = getFileName(testName) + " (Working Copy).txt";
            documentLibraryPage.waitForFile(downloadDirectory + editedFileName);

            // Login user 2
            ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
            ShareUser.openSitesDocumentLibrary(drone, siteName);
            ShareUserSitePage.getFileDirectoryInfo(drone,fileName).isLocked();
            assertTrue(ShareUserSitePage.getFileDirectoryInfo(drone, fileName).getContentInfo().contains(String.format("This document is locked by %s", testUser1)));

            // Navigate to Details page of document and click Edit Properties;
            ShareUserSitePage.selectContent(drone, fileName);
            
            // TODO: Replace with Share-PO method
            String url = drone.getCurrentUrl();
            drone.navigateTo(url.replace("document-details", "edit-metadata"));
            EditDocumentPropertiesPage editDocumentPropertiesPage = drone.getCurrentPage().render();
            try
            {
                editDocumentPropertiesPage.setName(fileName + "edited");
                // TODO: Implement assert.fail if there is not exception
            }
            // TODO: Catch exception: not throwable
            catch (Throwable ex)
            {
                logger.info("Editing not available as file input is disabled");
            }

            ShareUser.openDocumentLibrary(drone);
            Assert.assertNotNull(ShareUserSitePage.getFileDirectoryInfo(drone, fileName));

        }
        finally
        {
            // Cancel Editing
            // TODO: Remove this if not required, as new file is being used for each test run
            try
            {
                ShareUser.login(customDrone, testUser1, DEFAULT_PASSWORD);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName).render();
                // Click Edit Offline button;
                if (!documentLibraryPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent())
                {
                    documentLibraryPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectCancelEditing().render();
                }
                ConfirmDeletePage confirmDeletePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectDelete().render();
                confirmDeletePage.selectAction(ConfirmDeletePage.Action.Delete).render();
            }
            catch (Throwable ex)
            {
                logger.info(ex.getMessage());
            }
            testCleanup(drone, testName);
            try
            {
                customDrone.quit();
            }
            catch (Throwable ex)
            {
                logger.info(ex.getMessage());
            }
        }
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_ALF_2899() throws Exception
    {

        String testName = getTestName();
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // User
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openDocumentLibrary(drone).render();

        // Upload File
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        ShareUser.logout(drone);

    }

    @Test(groups = "EnterpriseOnly")
    public void ALF_2899() throws Exception
    {
        testName = getTestName();

        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String testUser = getUserNameFreeDomain(testName);

        DocumentLibraryPage documentLibraryPage;

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // start the new workflow
        // TODO: Add / Use util from ShareUserWorkflow in all tests where applicable to start-cancel workflow
        StartWorkFlowPage startWorkFlowPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectStartWorkFlow().render();
        NewWorkflowPage newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW)).render();
        String workFlowName1 = testName + System.currentTimeMillis() + "-1-WF";
        String dueDate = new DateTime().plusDays(2).toString("dd/MM/yyyy");
        List<String> reviewers = new ArrayList<String>();
        reviewers.add(username);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails(siteName, siteName, reviewers);
        formDetails.setMessage(workFlowName1);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        documentLibraryPage = newWorkflowPage.startWorkflow(formDetails).render();

        // check the document is marked with icon
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the file is part of a workflow");

        // check the workflow is not present in MyTasks - active tasks
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        assertFalse(myTasksPage.isTaskPresent(workFlowName1));
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_ALF_2900() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // User
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openDocumentLibrary(drone).render();

        // Upload File
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        ShareUser.logout(drone);
    }

    @Test(groups = "EnterpriseOnly")
    public void ALF_2900() throws Exception
    {
        testName = getTestName();

        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        
        String testUser = getUserNameFreeDomain(testName);
        DocumentLibraryPage documentLibraryPage;

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // start the new workflow
        StartWorkFlowPage startWorkFlowPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectStartWorkFlow().render();
        NewWorkflowPage newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW)).render();
        String workFlowName1 = testName + System.currentTimeMillis() + "-1-WF";
        String dueDate = new DateTime().plusDays(2).toString("dd/MM/yyyy");
        List<String> reviewers = new ArrayList<String>();
        reviewers.add(username);
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails(siteName, siteName, reviewers);
        formDetails.setMessage(workFlowName1);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        newWorkflowPage.cancelCreateWorkflow(formDetails).render();

        // check the document is not marked with icon
        assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the file is part of a workflow");

        // check the workflow is not present in MyTasks - active tasks
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        assertFalse(myTasksPage.isTaskPresent(workFlowName1));
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_5607() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);
        String testUser = getUserNameFreeDomain(testName);

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openDocumentLibrary(drone).render();

        // Upload File
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        for (String tag : TEST_TAGS)
        {
            ShareUserSitePage.addTag(drone, fileName, tag);
        }
        
        ShareUser.logout(drone);
    }

    private static String[] TEST_TAGS = { "+)(&^%$#@ ~", "Bedürfnis",
            "373410584570611755554713645806507805287482426" +
                    "817707865250365585821787087425238236324155636041" +
                    "57722674806351147348453726037052504863468162" +
                    "7501815347734737524048848388541525267811054775115" +
                    "310312540808668588760550376431211218838055672752085" +   //I am ashamed, but not another way.
                    "340575362251174378", "justTag" };

    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_5607()
    {
        testName = getTestName();

        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);
        String testUser = getUserNameFreeDomain(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        FileDirectoryInfo fileDirectoryInfo = ShareUserSitePage.getFileDirectoryInfo(drone, fileName);

        fileDirectoryInfo.clickOnAddTag();
        
        for (String tag : TEST_TAGS)
        {
            assertTrue(fileDirectoryInfo.removeTagButtonIsDisplayed(tag), String.format("Remove button is absent for tag [%s]", tag));
            fileDirectoryInfo.clickOnTagRemoveButton(tag);
        }
        
        fileDirectoryInfo.clickOnTagSaveButton();
        int tagsCount = fileDirectoryInfo.getTags().size();
        assertEquals(tagsCount, 0, String.format("Not all tags are removed. Actual tags count is %s", tagsCount));

    }

    
        }
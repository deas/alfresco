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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.site.document;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.enums.TinyMceColourCode;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.workflow.*;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.alfresco.po.share.site.document.TinyMceEditor.FormatType.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Roman.Chul
 */

@Listeners(FailedTestListener.class)
public class DocumentDetailsActionsTest extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(DocumentDetailsActionsTest.class);
    protected String testUser;
    protected String siteName = "";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_AONE_14994() throws Exception
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
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "EnterpriseOnly")
    public void AONE_14994()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName = getFileName(testName) + ".txt";

        DocumentLibraryPage documentLibraryPage;

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

            DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName);
            
            // Click 'Change Type' action
            ChangeTypePage changeTypePage = detailsPage.selectChangeType().render();
            assertTrue(changeTypePage.isChangeTypeDisplayed());
            
            // Select any type if present and click Cancel            
            // The behavior for standard install is: no types are available -> click cancel
                changeTypePage.selectCancel();

    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_AONE_14993() throws Exception
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
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

    }

    @Test(groups = "EnterpriseOnly")
    public void AONE_14993()throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName = getFileName(testName) + getRandomStringWithNumders(3) + ".txt";

        DocumentLibraryPage documentLibraryPage;


            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

            // Upload File
            String[] fileInfo = { fileName, DOCLIB };
            ShareUser.uploadFileInFolder(drone, fileInfo);

            DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName);
            
            // Click "Edit Properties" in Actions section;
            EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
            editPropertiesPage.selectMimeType(MimeType.XML);
            detailsPage = editPropertiesPage.selectSave().render();
            detailsPage.render();

            Map<String, Object> properties = detailsPage.getProperties();
            Assert.assertEquals(properties.get("Mimetype"), "XML");

    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_AONE_14998() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "EnterpriseOnly")
    public void AONE_14998() throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName = getFileName(testName) + ".txt";

        DocumentLibraryPage documentLibraryPage;
        
        String workFlowName1 = testName + System.currentTimeMillis() + "-1-WF";
        String dueDate = new DateTime().plusDays(2).toString("dd/MM/yyyy");

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

            DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName);

            // Click Start Workflow from Actions section

            StartWorkFlowPage startWorkFlowPage = ShareUserWorkFlow.selectStartWorkFlowFromDetailsPage(drone).render();

            NewWorkflowPage newWorkflowPage = startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW).render();
                        
            List<String> reviewers = new ArrayList<String>();
            reviewers.add(username);
            
            WorkFlowFormDetails formDetails = new WorkFlowFormDetails(siteName, siteName, reviewers);
            formDetails.setMessage(workFlowName1);
            formDetails.setDueDate(dueDate);
            formDetails.setTaskPriority(Priority.MEDIUM);
            //detailsPage = newWorkflowPage.startWorkflow(formDetails).render();

            newWorkflowPage.startWorkflow(formDetails).render();

            // check the document is marked with icon
            assertTrue(detailsPage.isPartOfWorkflow(), "Verifying the file is part of a workflow");

            // site creator logs out and assignee user logs in
            ShareUser.logout(drone);
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

            // check the task is in MyTasks for site creator
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
            assertTrue(myTasksPage.isTaskPresent(workFlowName1));

    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_AONE_14999() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "EnterpriseOnly")
    public void AONE_14999()throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName = getFileName(testName) + ".txt";

        DocumentLibraryPage documentLibraryPage;

        String workFlowName1 = testName + System.currentTimeMillis() + "-1-WF";
        String dueDate = new DateTime().plusDays(2).toString("dd/MM/yyyy");

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

            DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName);
            // Click Start Workflow from Actions section

            StartWorkFlowPage startWorkFlowPage = ShareUserWorkFlow.selectStartWorkFlowFromDetailsPage(drone).render();
            NewWorkflowPage newWorkflowPage = startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW).render();

            List<String> reviewers = new ArrayList<String>();
            reviewers.add(username);
            WorkFlowFormDetails formDetails = new WorkFlowFormDetails(siteName, siteName, reviewers);
            formDetails.setMessage(workFlowName1);
            formDetails.setDueDate(dueDate);
            formDetails.setTaskPriority(Priority.MEDIUM);
            detailsPage = newWorkflowPage.cancelCreateWorkflow(formDetails).render();

            // check the document is marked with icon
            assertFalse(detailsPage.isPartOfWorkflow(), "Verifying the file is part of a workflow");

            // site creator logs out and assignee user logs in
            ShareUser.logout(drone);
            
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

            // check the task is not in MyTasks for site creator
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
            assertFalse(myTasksPage.isTaskPresent(workFlowName1));
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_AONE_15006() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "EnterpriseOnly")
    public void AONE_15006()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName = getFileName(testName) + ".txt";
        String text = getRandomString(5);
        DocumentLibraryPage documentLibraryPage;

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

            DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName);
            AddCommentForm addCommentForm = detailsPage.clickAddCommentButton();

            // 1. Type any text and make it bold;
            TinyMceEditor tinyMceEditor = addCommentForm.getTinyMceEditor();
            tinyMceEditor.setText(text);

            tinyMceEditor.clickTextFormatter(BOLD);
            assertEquals(tinyMceEditor.getContent(), String.format("<p><b>%s</b></p>", text), "The text didn't mark as bold.");

            tinyMceEditor.clickTextFormatter(ITALIC);
            assertEquals(tinyMceEditor.getContent(), String.format("<p><i><b>%s</b></i></p>", text), "The text didn't italic.");

            tinyMceEditor.clickTextFormatter(BULLET);
            assertEquals(tinyMceEditor.getContent(), String.format("<ul><li><i><b>%s</b></i></li></ul>", text), "List didn't display.");

            tinyMceEditor.clickTextFormatter(NUMBER);
            assertEquals(tinyMceEditor.getContent(), String.format("<ol><li><i><b>%s</b></i></li></ol>", text), "Numbered list didn't display.");

            tinyMceEditor.clickColorCode(TinyMceColourCode.BLUE);
            assertEquals(tinyMceEditor.getColourAttribute(), "BLUE", "The text didn't highlight with any color.");

            addCommentForm.clickAddCommentButton();
            assertTrue(detailsPage.isCommentCorrect(text), "Comment didn't create");
        }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_AONE_15007() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "EnterpriseOnly")
    public void AONE_15007()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName = getFileName(testName) + ".txt";
        String text = getRandomString(5);
        DocumentLibraryPage documentLibraryPage;

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
            // Click Comment link near created file
            DocumentDetailsPage detailsPage = documentLibraryPage.getFileDirectoryInfo(fileName).clickCommentsLink().render();
            // Write some text in text box and click Add Comment button
            detailsPage.addComment(text);
            
            // Go to document library and verify comment counter

            documentLibraryPage = ShareUser.openDocumentLibrary(drone);
            Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName).getCommentsCount(), 1);

            // Go to Details page and delete the comment
            detailsPage = documentLibraryPage.selectFile(fileName);
            detailsPage.removeComment(text);

            // Go to document library and verify comment counter

            documentLibraryPage = ShareUser.openDocumentLibrary(drone);
            Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName).getCommentsCount(), 0);

        }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_AONE_14987() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Upload File
        String[] fileInfo = { fileName, DOCLIB };
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Upload New version of the document.
        for (int i = 0; i < 2; i++)
        {
            UpdateFilePage updatePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectUploadNewVersion().render();
            updatePage.selectMajorVersionChange();
            String fileContents = String.format("New version of document %s: %s", i + 2, fileName);
            File newFileName = newFile(DATA_FOLDER + (fileName + getRandomString(3)), fileContents);
            updatePage.uploadFile(newFileName.getCanonicalPath());
            updatePage.setComment(fileName);
            SitePage sitePage = updatePage.submit().render();
            documentLibraryPage = (DocumentLibraryPage) sitePage.render();
            FileUtils.forceDelete(newFileName);
        }
    }

    @Test(groups = "EnterpriseOnly")
    public void AONE_14987() throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName = getFileName(testName) + ".txt";
        DocumentLibraryPage documentLibraryPage;
        String filePath = downloadDirectory + fileName;

            setupCustomDrone(WebDroneType.DownLoadDrone);

            // Login
            ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

            documentLibraryPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);
            // Document details page has been opened;
            DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName).render();
            Assert.assertFalse(detailsPage.getDocumentVersion().equals("1.0"));

            // Click Download icon for previous version;
            detailsPage.selectDownloadPreviousVersion("2.0");

            // Check the file is downloaded successfully
            detailsPage.waitForFile(maxWaitTime, downloadDirectory + fileName);

            String body = FileUtils.readFileToString(new File(filePath));
            if (body.length() == 0)
            {
                body = FileUtils.readFileToString(new File(filePath));
            }

            Assert.assertEquals(body, String.format("New version of document %s: %s", 2, fileName));

        }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_AONE_15008() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "EnterpriseOnly")
    public void AONE_15008() throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName = getFileName(testName) + ".txt";

        DocumentLibraryPage documentLibraryPage;
        
        String workFlowName1 = testName + System.currentTimeMillis() + "-1-WF";
        String dueDate = new DateTime().plusDays(2).toString("dd/MM/yyyy");

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

           DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName);

            // Click Start Workflow from Actions section

            StartWorkFlowPage startWorkFlowPage = ShareUserWorkFlow.selectStartWorkFlowFromDetailsPage(drone);
            NewWorkflowPage newWorkflowPage = startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW).render();

            List<String> reviewers = new ArrayList<>();
            reviewers.add(username);
            WorkFlowFormDetails formDetails = new WorkFlowFormDetails(siteName, siteName, reviewers);
            formDetails.setMessage(workFlowName1);
            formDetails.setDueDate(dueDate);
            formDetails.setTaskPriority(Priority.MEDIUM);
            detailsPage = newWorkflowPage.startWorkflow(formDetails).render();

            // check the document is marked with icon
            assertTrue(detailsPage.isPartOfWorkflow(), "Verifying the file is part of a workflow");

            // site creator logs out and assignee user logs in
            ShareUser.logout(drone);
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

            // check the task is in MyTasks for site creator
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
            assertTrue(myTasksPage.isTaskPresent(workFlowName1));

    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_AONE_15009() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "EnterpriseOnly")
    public void AONE_15009() throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName = getFileName(testName) + ".txt";

        DocumentLibraryPage documentLibraryPage;

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

            // TODO: Use util as adobe
            DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName);
            
            // Click Start Workflow from Actions section
            StartWorkFlowPage startWorkFlowPage = detailsPage.selectStartWorkFlowIcon().render();
            
            NewWorkflowPage newWorkflowPage = startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW).render();
            
            String workFlowName1 = testName + System.currentTimeMillis() + "-1-WF";
            String dueDate = new DateTime().plusDays(2).toString("dd/MM/yyyy");
            
            List<String> reviewers = new ArrayList<>();
            reviewers.add(username);
            
            WorkFlowFormDetails formDetails = new WorkFlowFormDetails(siteName, siteName, reviewers);
            formDetails.setMessage(workFlowName1);
            formDetails.setDueDate(dueDate);
            formDetails.setTaskPriority(Priority.MEDIUM);
            detailsPage = newWorkflowPage.cancelCreateWorkflow(formDetails).render();

            // check the document is marked with icon
            assertFalse(detailsPage.isPartOfWorkflow(), "Verifying the file is part of a workflow");

            // site creator logs out and assignee user logs in
            ShareUser.logout(drone);
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

            // check the task is not in MyTasks for site creator
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
            assertFalse(myTasksPage.isTaskPresent(workFlowName1));

    }

    @AfterMethod(alwaysRun = true)
    public void logout()
    {
        ShareUser.logout(drone);
        logger.info("User logged out - drone.");
    }
}

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
package org.alfresco.share.site.document;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.workflow.*;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDroneImpl;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.alfresco.po.share.site.document.TinyMceEditor.FormatType.*;
import static org.alfresco.share.util.RandomUtil.getRandomString;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Roman.Chul
 */

@Listeners(FailedTestListener.class)
public class DocumentDetailsActionsTest extends AbstractTests
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
    public void dataPrep_Enterprise40x_14032() throws Exception
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
        ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_14032()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName = getFileName(testName) + ".txt";

        DocumentLibraryPage documentLibraryPage;

        try
        {
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

            DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName);
            // Click 'Change Type' action
            ChangeTypePage changeTypePage = detailsPage.selectChangeType().render();
            assertTrue(changeTypePage.isChangeTypeDisplayed());
            // Select any type if present and click Cancel
            long actualPropertiesSize = detailsPage.getProperties().size();
            List<String> types = changeTypePage.getTypes();
            if (types.size() == 1)
            {
                changeTypePage.selectCancel();
            }
            else
            {
                changeTypePage.selectChangeType(types.get(1));
                changeTypePage.selectSave();

                drone.refresh();
                long properties = detailsPage.getProperties().size();
                Assert.assertNotEquals(actualPropertiesSize, properties);
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

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13863() throws Exception
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
        ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

    }

    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_13863()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName = getFileName(testName) + getRandomStringWithNumders(3) + ".txt";

        DocumentLibraryPage documentLibraryPage;

        try
        {
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
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            testCleanup(drone, testName);
        }
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13861() throws Exception
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
        ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_13861()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName = getFileName(testName) + ".txt";

        DocumentLibraryPage documentLibraryPage;

        try
        {
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

            DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName);
            // Click Start Workflow from Actions section
            StartWorkFlowPage startWorkFlowPage = detailsPage.selectStartWorkFlowPage().render();
            NewWorkflowPage newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW)).render();
            String workFlowName1 = testName + System.currentTimeMillis() + "-1-WF";
            String dueDate = new DateTime().plusDays(2).toString("dd/MM/yyyy");
            List<String> reviewers = new ArrayList<String>();
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
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            testCleanup(drone, testName);
        }
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13862() throws Exception
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
        ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_13862()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName = getFileName(testName) + ".txt";

        DocumentLibraryPage documentLibraryPage;

        try
        {
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

            DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName);
            // Click Start Workflow from Actions section
            StartWorkFlowPage startWorkFlowPage = detailsPage.selectStartWorkFlowPage().render();
            NewWorkflowPage newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW)).render();
            String workFlowName1 = testName + System.currentTimeMillis() + "-1-WF";
            String dueDate = new DateTime().plusDays(2).toString("dd/MM/yyyy");
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
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            testCleanup(drone, testName);
        }
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_3910() throws Exception
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
        ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_3910()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName = getFileName(testName) + ".txt";
        String text = getRandomString(5);
        DocumentLibraryPage documentLibraryPage;

        try
        {
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

            tinyMceEditor.clickColorCode();
            assertEquals(tinyMceEditor.getColourAttribute(), "BLUE", "The text didn't highlight with any color.");

            addCommentForm.clickAddCommentButton();
            drone.refresh();
            assertTrue(detailsPage.isCommentCorrect(text), "Comment didn't create");
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

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_5675() throws Exception
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
        ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_5675()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName = getFileName(testName) + ".txt";
        String text = getRandomString(5);
        DocumentLibraryPage documentLibraryPage;

        try
        {
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
            // Click Comment link near created file
            DocumentDetailsPage detailsPage = documentLibraryPage.getFileDirectoryInfo(fileName).clickCommentsLink().render();
            // Write some text in text box and click Add Comment button
            detailsPage.addComment(text);
            // Go to document library and verify comment counter
            documentLibraryPage = detailsPage.getSiteNav().selectSiteDocumentLibrary().render();
            Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName).getCommentsCount(), 1);
            // Go to Details page and delete the comment
            detailsPage = documentLibraryPage.selectFile(fileName);
            detailsPage.removeComment(text);
            // Go to document library and verify comment counter
            documentLibraryPage = detailsPage.getSiteNav().selectSiteDocumentLibrary().render();
            Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName).getCommentsCount(), 0);

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

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_3960() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // User
        String[] testUserInfo = new String[] { testUser };
        //CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        //ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
        //ShareUser.openSiteDashboard(drone, siteName);
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
            File newFileName = newFile(DATA_FOLDER + (fileName+getRandomString(3)), fileContents);
            updatePage.uploadFile(newFileName.getCanonicalPath());
            updatePage.setComment(fileName);
            SitePage sitePage = updatePage.submit().render();
            documentLibraryPage = (DocumentLibraryPage) sitePage.render();
            FileUtils.forceDelete(newFileName);
        }
    }

    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_3960()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName = getFileName(testName) + ".txt";
        DocumentLibraryPage documentLibraryPage;
        String filePath = downloadDirectory + fileName;

        try
        {
            if (new File(filePath).exists())
            {
                FileUtils.forceDelete(new File(filePath));
            }

            customDrone = ((WebDroneImpl) ctx.getBean(WebDroneType.DownLoadDrone.getName()));
            dronePropertiesMap.put(customDrone, (ShareTestProperty) ctx.getBean("shareTestProperties"));
            maxWaitTime = ((WebDroneImpl) customDrone).getMaxPageRenderWaitTime();

            // Login
            ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

            documentLibraryPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);
            //Document details page has been opened;
            DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName).render();
            Assert.assertFalse(detailsPage.getDocumentVersion().equals("1.0"));

            // Click Download icon for previous version;
            detailsPage.selectDownloadPreviousVersion("2.0");

            // Check the file is downloaded successfully
            detailsPage.waitForFile(maxWaitTime, downloadDirectory + fileName);

            String body = FileUtils.readFileToString(new File(filePath));
            if (body.length() == 0)
            {
                Thread.sleep(maxWaitTime);
                body = FileUtils.readFileToString(new File(filePath));
            }

            Assert.assertEquals(body, String.format("New version of document %s: %s", 2, fileName));

        }
        catch (Throwable e)
        {
            reportError(customDrone, testName, e);
        }
        finally
        {
            testCleanup(customDrone, testName);
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


}

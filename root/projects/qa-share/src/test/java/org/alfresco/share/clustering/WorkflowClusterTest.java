/*
 * Copyright (C) 2005-2013s Alfresco Software Limited.
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

package org.alfresco.share.clustering;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.task.TaskDetailsPage;
import org.alfresco.po.share.task.TaskItem;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.workflow.*;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.exception.PageOperationException;
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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
public class WorkflowClusterTest extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(WorkflowClusterTest.class);
    private static String node1Url;
    private static String node2Url;
    private String testUser1;
    private String testUser2;
    private String testUser3;
    private static final String regexUrl = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";

    protected String siteName = "";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();

        testUser1 = getUserNameFreeDomain("user1" + testName);
        testUser2 = getUserNameFreeDomain("user2" + testName);
        testUser3 = getUserNameFreeDomain("user3" + testName);
        logger.info("Starting Tests: " + testName);

        String[] testUserInfo1 = new String[] { testUser1 };
        String[] testUserInfo2 = new String[] { testUser2 };
        String[] testUserInfo3 = new String[] { testUser3 };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo3);

        /*
         * String shareJmxPort = getAlfrescoServerProperty("Alfresco:Type=Configuration,Category=sysAdmin,id1=default", "share.port").toString();
         * boolean clustering_enabled_jmx = (boolean) getAlfrescoServerProperty("Alfresco:Name=Cluster,Tool=Admin", "ClusteringEnabled");
         * if (clustering_enabled_jmx)
         * {
         * Object clustering_url = getAlfrescoServerProperty("Alfresco:Name=Cluster,Tool=Admin", "ClusterMembers");
         * try
         * {
         * CompositeDataSupport compData = (CompositeDataSupport) ((TabularDataSupport) clustering_url).values().toArray()[0];
         * String clusterIP = compData.values().toArray()[0] + ":" + shareJmxPort;
         * CompositeDataSupport compData2 = (CompositeDataSupport) ((TabularDataSupport) clustering_url).values().toArray()[1];
         * String clusterIP2 = compData2.values().toArray()[0] + ":" + shareJmxPort;
         * node1Url = shareUrl.replace(shareIP, clusterIP);
         * node2Url = shareUrl.replace(shareIP, clusterIP2);
         * }
         * catch (Throwable ex)
         * {
         * throw new SkipException("Skipping as pre-condition step(s) fail");
         * }
         * }
         */
        SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();

        RepositoryServerClusteringPage clusteringPage = sysSummaryPage.openConsolePage(AdminConsoleLink.RepositoryServerClustering).render();

        Assert.assertTrue(clusteringPage.isClusterEnabled(), "Cluster isn't enabled");

        List<String> clusterMembers = clusteringPage.getClusterMembers();
        if (clusterMembers.size() >= 2)
        {
            node1Url = shareUrl.replaceFirst(regexUrl, clusterMembers.get(0) + ":" + nodePort);
            node2Url = shareUrl.replaceFirst(regexUrl, clusterMembers.get(1) + ":" + nodePort);
        }
        else
        {
            throw new PageOperationException("Number of cluster members is less than two");
        }
    }

    /**
     * Test - AONE_9178:Create a workflow
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Any content was added to the Document Library on nodeA</li>
     * <li>Start workflow page is opened (nodeA)</li>
     * <li>Start Workflow</li>
     * <li>Log in as assignee user on the other node (node B)</li>
     * <li>Verify Task is successfully displayed on both of nodes</li>
     * <li>My Workflow page and the document belong to workflow is marked with appropriate icon on both of nodes</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9178() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        String workFlowName = testName + System.currentTimeMillis() + "-1-WF";

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload any file
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);

        // Start workflow
        documentLibraryPage = startNewWorkflow(documentLibraryPage, fileName, testUser2, workFlowName);

        // Verify the document belong to workflow is marked with appropriate icon at the server A (for creator)
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the file is part of a workflow. Server A");

        // Verify My Workflow page at the server A (for creator)
        MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
        Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName), "Workflow isn't presented on ' My Workflow page' for creator. Server A");

        ShareUser.logout(drone);

        DashBoardPage dashBoardPage = ShareUser.login(drone, testUser2, DEFAULT_PASSWORD).render();

        // Verify My Tasks dashlet for assignee user. Server A
        assertTrue(getTasksDescriptionList(dashBoardPage).contains(workFlowName), "Task isn't displayed on My Tasks dashlet for assignee user. Server A");

        // Verify 'My task' page for assignee. Server A
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        assertTrue(myTasksPage.isTaskPresent(workFlowName), "Task isn't presented on ' My Task page' for assignee user. Server A");

        // Verify My Workflow page at the server A
        myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
        Assert.assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName), "Workflow is presented on ' My Workflow page' for assignee user. Server A");

        ShareUser.logout(drone);

        // verify that workflow is displayed at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify the document belong to workflow is marked with appropriate icon at the server B (for creator)
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the file is part of a workflow. Server B");

        // Verify My Workflow page at the server B
        myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
        Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName), "Workflow isn't presented on ' My Workflow page' for creator. Server B");

        // Verify 'My task' page for creator. Server B
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        assertFalse(myTasksPage.isTaskPresent(workFlowName), "Task is presented on ' My Task page' for creator user. Server B");

        ShareUser.logout(drone);

        dashBoardPage = ShareUser.login(drone, testUser2, DEFAULT_PASSWORD).render();

        // Verify My Tasks dashlet for assignee user. Server B
        assertTrue(getTasksDescriptionList(dashBoardPage).contains(workFlowName), "Task isn't displayed on My Tasks dashlet for assignee user. Server B");

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        // Verify 'My task' page for assignee. Server B
        assertTrue(myTasksPage.isTaskPresent(workFlowName), "Task isn't presented on ' My Task page' for assignee user. Server B");

        // Verify My Workflow page at the server B
        myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
        Assert.assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName), "Workflow is presented on ' My Workflow page' for assignee user. Server B");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE_9179:Edit task
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Any content was added to the Document Library on nodeA</li>
     * <li>Any workflow page has been started</li>
     * <li>Assignee user logged into Share</li>
     * <li>Click button "Edit task"</li>
     * <li>Enter any comment</li>
     * <li>Change the status</li>
     * <li>Click Save and Close button</li>
     * <li>Task has been edited successfully</li>
     * <li>Log into Share on other node and verify task details (node B)</li>
     * <li>Changes to the task are applied to both of nodes</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9179() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        String workFlowName = testName + System.currentTimeMillis() + "-1-WF";
        String userComments = "firstUserComments";

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload any file
        DocumentLibraryPage documentLibraryPage;
        documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);

        // Start workflow
        documentLibraryPage = startNewWorkflow(documentLibraryPage, fileName, testUser2, workFlowName);

        // Verify the document belong to workflow is marked with appropriate icon at the server A (for creator)
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the file is part of a workflow. Server A");

        // Edit task by assigner user at the server A
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD).render();

        // Navigate to MyTasks page and Verify Task is displayed
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

        // Verify 'My task' page for assignee. Server A
        assertTrue(myTasksPage.isTaskPresent(workFlowName), "Task isn't presented on ' My Task page' for assignee user. Server A");

        // Edit Task Details
        TaskDetailsPage taskDetailsPage = ShareUserWorkFlow.navigateToTaskDetailsPage(drone, workFlowName);
        EditTaskPage editTaskPage = taskDetailsPage.selectEditButton().render();
        // Change the status
        editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
        // Enter any comment
        editTaskPage.enterComment(userComments);

        // Click Save and Close button
        editTaskPage.selectSaveButton();

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        TaskDetailsPage taskDetails = myTasksPage.selectViewTasks(workFlowName).render();

        // Changes to the task are applied at the server A
        Assert.assertTrue(taskDetails.getComment().equalsIgnoreCase(userComments), "Changes for comment isn't applied at the server A");
        Assert.assertTrue(taskDetails.getTaskStatus().equals(TaskStatus.COMPLETED), "Changes for task status isn't applied at the server A");

        ShareUser.logout(drone);

        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        taskDetails = myTasksPage.selectViewTasks(workFlowName).render();

        // Changes to the task are applied at the server B
        Assert.assertTrue(taskDetails.getComment().equalsIgnoreCase(userComments), "Changes for comment isn't applied at the server B");
        Assert.assertTrue(taskDetails.getTaskStatus().equals(TaskStatus.COMPLETED), "Changes for task status isn't applied at the server B");

    }

    /**
     * Test - AONE_9180:Add files button
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Any content was added to the Document Library on nodeA</li>
     * <li>Any workflow page has been started</li>
     * <li>Assignee user logged into Share on one of the nodes (e.g. nodeA)</li>
     * <li>Click button "Edit task"</li>
     * <li>Click Add button</li>
     * <li>Navigate to the folder where the content item is located</li>
     * <li>Click Add button</li>
     * <li>Click Save and close button</li>
     * <li>Log in to Share on other node and verify added item(s) to the task</li>
     * <li>Changes to the task are applied to both of nodes</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9180() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String fileName = getRandomString(10) + ".txt";
        String fileName1 = getRandomString(10) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        String[] fileInfo1 = { fileName1, DOCLIB };
        String workFlowName = testName + System.currentTimeMillis() + "-1-WF";

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload any file
        DocumentLibraryPage documentLibraryPage;
        ShareUser.uploadFileInFolder(drone, fileInfo);
        documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo1);

        // Start workflow
        documentLibraryPage = startNewWorkflow(documentLibraryPage, fileName, testUser2, workFlowName);

        // Verify the document belong to workflow is marked with appropriate icon at the server A (for creator)
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the file is part of a workflow. Server A");

        // Edit task by assigner user at the server A
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD).render();

        // Navigate to MyTasks page and Verify Task is displayed
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

        // Verify 'My task' page for assignee. Server A
        assertTrue(myTasksPage.isTaskPresent(workFlowName), "Task isn't presented on ' My Task page' for assignee user. Server A");

        // Edit Task Details
        TaskDetailsPage taskDetailsPage = ShareUserWorkFlow.navigateToTaskDetailsPage(drone, workFlowName);
        EditTaskPage editTaskPage = taskDetailsPage.selectEditButton().render();
        // Add new item
        editTaskPage.selectItem(fileName1, siteName);

        // Click Save and Close button
        editTaskPage.selectSaveButton();

        // Verify that Added item are displayed at the task page server A
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        TaskDetailsPage taskDetails = myTasksPage.selectViewTasks(workFlowName).render();

        List<TaskItem> tasksItems = taskDetails.getTaskItems();
        List<String> addFiles = new ArrayList<>();

        for (TaskItem taskItem : tasksItems)
        {
            addFiles.add(taskItem.getItemName());
        }
        // Changes to the task are applied at the server A
        Assert.assertTrue(addFiles.contains(fileName), "Verify that primary item " + fileName + " are displayed at the task page server A");
        Assert.assertTrue(addFiles.contains(fileName1), "Verify that Added item " + fileName1 + " are displayed at the task page server A");

        ShareUser.logout(drone);

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify the document belong to workflow is marked with appropriate icon at the server B (for creator)
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the file is part of a workflow. Server B");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the file is part of a workflow. Server B");

        ShareUser.logout(drone);

        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        // Changes to the task are applied at the server A
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        taskDetails = myTasksPage.selectViewTasks(workFlowName).render();

        tasksItems = taskDetails.getTaskItems();
        addFiles = new ArrayList<>();
        // Changes to the task are applied at the server B
        for (TaskItem taskItem : tasksItems)
        {
            addFiles.add(taskItem.getItemName());
        }

        // Changes to the task are applied at the server B
        Assert.assertTrue(addFiles.contains(fileName), "Verify that primary item " + fileName + " are displayed at the task page server B");
        Assert.assertTrue(addFiles.contains(fileName1), "Verify that Added item " + fileName1 + " are displayed at the task page server B");

        ShareUser.logout(drone);

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify the document belong to workflow is marked with appropriate icon at the server B (for creator)
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the file is part of a workflow. Server B");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the file is part of a workflow. Server B");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE_9181:Reassign
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>At least 3 users exist</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Any content was added to the Document Library on nodeA</li>
     * <li>Any workflow page has been started</li>
     * <li>Assignee user logged into Share on one of the nodes (e.g. nodeA)</li>
     * <li>My tasks page is opened</li>
     * <li>Click button "Edit task"</li>
     * <li>Click Reassign button</li>
     * <li>Find any user and select him</li>
     * <li>On the other node(e.g. node B) log in as the assignee user (to whom workflow was assigned initially)</li>
     * <li>The task is not displayed in the My tasks dahslet and at the My tasks page</li>
     * <li>On the other node(e.g. node B) log in as the assignee user (to whom workflow was reassigned)</li>
     * <li>The task reassigned on the node A is successfully displayed on the node A and node B in My Tasks Dashlet and at the My tasks page</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9181() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String fileName = getRandomString(10) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        String workFlowName = testName + System.currentTimeMillis() + "-1-WF";

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload any file
        DocumentLibraryPage documentLibraryPage;
        documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);

        // Start workflow
        documentLibraryPage = startNewWorkflow(documentLibraryPage, fileName, testUser2, workFlowName);

        // Verify the document belong to workflow is marked with appropriate icon at the server A (for creator)
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the file is part of a workflow. Server A");

        // Edit task by assigner user at the server A
        DashBoardPage dashBoardPage = ShareUser.login(drone, testUser2, DEFAULT_PASSWORD).render();

        // Verify My Tasks dashlet for assignee user. Server A
        assertTrue(getTasksDescriptionList(dashBoardPage).contains(workFlowName), "Task isn't displayed on My Tasks dashlet for assignee user. Server A");

        // Navigate to MyTasks page and Verify Task is displayed
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

        // Verify 'My task' page for assignee. Server A
        assertTrue(myTasksPage.isTaskPresent(workFlowName), "Task isn't presented on ' My Task page' for assignee user. Server A");

        // Edit Task Details
        TaskDetailsPage taskDetailsPage = ShareUserWorkFlow.navigateToTaskDetailsPage(drone, workFlowName);
        EditTaskPage editTaskPage = taskDetailsPage.selectEditButton().render();
        // Add new item
        Assert.assertTrue(editTaskPage.isReAssignButtonDisplayed(), "Reassign button isn't displayed for assigner. Server A");

        // Reassign task for another user
        myTasksPage = editTaskPage.selectReassign(testUser3);

        // The task is not displayed in the My tasks dahslet and at the My tasks page
        assertFalse(myTasksPage.isTaskPresent(workFlowName), "Task is presented on ' My Task page' for assignee user (after reassign). Server A");

        ShareUser.logout(drone);

        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        dashBoardPage = ShareUser.login(drone, testUser2, DEFAULT_PASSWORD).render();

        assertFalse(getTasksDescriptionList(dashBoardPage).contains(workFlowName),
                "Task isn't displayed on My Tasks dashlet for assignee user (after reassign). Server B");

        // Navigate to MyTasks page and Verify Task is displayed
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

        // The task is not displayed in the My tasks dahslet and at the My tasks page
        assertFalse(myTasksPage.isTaskPresent(workFlowName), "Task is presented on ' My Task page' for assignee user (after reassign). Server B");

        ShareUser.logout(drone);

        // On the other node(e.g. node B) log in as the assignee user (to whom workflow was reassigned)
        dashBoardPage = ShareUser.login(drone, testUser3, DEFAULT_PASSWORD).render();

        assertTrue(getTasksDescriptionList(dashBoardPage).contains(workFlowName), "Task isn't displayed for reassign user on My Tasks dashlet. Server B");

        // Navigate to MyTasks page and Verify Task is displayed
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

        // Verify 'My task' page for assignee. Server B
        assertTrue(myTasksPage.isTaskPresent(workFlowName), "Task isn't presented on ' My Task page' for reassign user. Server B");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE_9182:Verify transition button
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Any content was added to the Document Library on nodeA</li>
     * <li>Any workflow page has been started</li>
     * <li>Assignee user logged into Share on other node (e.g. node B)</li>
     * <li>Click button "Edit task"</li>
     * <li>Enter any comment</li>
     * <li>Change the status</li>
     * <li>Click any transition button (Task Done/ Approve/ Reject) Button</li>
     * <li>Log into Share as task creator on the other node</li>
     * <li>Verify My Tasks Dashlet and My tasks page on both of nodes</li>
     * <li>Taken task is available for the task's creator on both of nodes</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9182() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        String workFlowName = testName + System.currentTimeMillis() + "-1-WF";
        String userComments = "firstUserComments";

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload any file
        DocumentLibraryPage documentLibraryPage;
        documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);

        // Start workflow
        documentLibraryPage = startNewWorkflow(documentLibraryPage, fileName, testUser2, workFlowName);

        // Verify the document belong to workflow is marked with appropriate icon at the server A (for creator)
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the file is part of a workflow. Server A");

        // Edit task by assigner user at the server A
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD).render();

        // Navigate to MyTasks page and Verify Task is displayed
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

        // Verify 'My task' page for assignee. Server A
        assertTrue(myTasksPage.isTaskPresent(workFlowName), "Task isn't presented on ' My Task page' for assignee user. Server A");

        // Edit Task Details
        TaskDetailsPage taskDetailsPage = ShareUserWorkFlow.navigateToTaskDetailsPage(drone, workFlowName);
        EditTaskPage editTaskPage = taskDetailsPage.selectEditButton().render();
        // Change the status
        editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
        // Enter any comment
        editTaskPage.enterComment(userComments);

        // Click Task Done button
        editTaskPage.selectTaskDoneButton().render();

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        assertFalse(myTasksPage.isTaskPresent(workFlowName),
                "Task is presented on ' My Task page' for assignee user (after click on transition button). Server A");

        ShareUser.logout(drone);

        // Log into Share as task creator on the Server A and verify My Tasks Dashlet and My tasks page on both of nodes
        DashBoardPage dashBoardPage = ShareUser.login(drone, testUser1, DEFAULT_PASSWORD).render();

        // Verify My Tasks dashlet for creator user. Server A
        assertTrue(getTasksDescriptionList(dashBoardPage).contains(workFlowName),
                "Task isn't displayed for creator user (after click on transition button) on My Tasks dashlet. Server A");

        // Verify My tasks page
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

        assertTrue(myTasksPage.isTaskPresent(workFlowName),
                "Task isn't presented on ' My Task page' for creator user (after click on transition button). Server A");

        ShareUser.logout(drone);

        // Log into Share as task creator on the Server B and verify My Tasks Dashlet and My tasks page on both of nodes
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        dashBoardPage = ShareUser.login(drone, testUser1, DEFAULT_PASSWORD).render();

        // Verify My Tasks dashlet for creator user. Server B
        assertTrue(getTasksDescriptionList(dashBoardPage).contains(workFlowName),
                "Task isn't displayed for creator user (after click on transition button) on My Tasks dashlet. Server B");

        // Verify My tasks page
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

        assertTrue(myTasksPage.isTaskPresent(workFlowName),
                "Task isn't presented on ' My Task page' for creator user (after click on transition button). Server B");

        ShareUser.logout(drone);

    }

    /**
     * Test - AONE_9183:Cancel workflow
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Any content was added to the Document Library on nodeA</li>
     * <li>Start workflow page is opened (nodeA)</li>
     * <li>Start Workflow</li>
     * <li>Task creator logged into Share</li>
     * <li>Workflow I've started page is opened on the node A</li>
     * <li>Click button Cancel workflow</li>
     * <li>Click "Yes"</li>
     * <li>Workflow I've stared page is opened. Workflow is removed</li>
     * <li>Log into Share on the other node (e.g. nodeB) as task creator</li>
     * <li>Workflow is removed</li>
     * <li>Log into Share on the other node (e.g. nodeB) as assignee user</li>
     * <li>Verify the task is not displayed in My tasks dashlet and My tasks pages</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9183() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        String workFlowName = testName + System.currentTimeMillis() + "-1-WF";

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload any file
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);

        // Start workflow
        documentLibraryPage = startNewWorkflow(documentLibraryPage, fileName, testUser2, workFlowName);

        // Verify the document belong to workflow is marked with appropriate icon at the server A (for creator)
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the file is part of a workflow. Server A");

        // Verify My Workflow page at the server A (for creator)
        MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
        Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName), "Workflow isn't presented on ' My Workflow page' for creator. Server A");

        WorkFlowDetailsPage workFlowDetailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName).render();

        // Cancel workflow
        workFlowDetailsPage.selectCancelWorkFlow().render();

        myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

        // Verify Workflow is removed
        Assert.assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName),
                "Workflow is presented on ' My Workflow page' for creator (after canceling). Server A");

        ShareUser.logout(drone);

        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        // Log into Share on the other node (e.g. nodeB) as task creator
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD).render();

        // Verify Workflow I've started page
        myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

        // Verify Workflow is removed
        Assert.assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName),
                "Workflow is presented on ' My Workflow page' for creator (after canceling). Server B");

        ShareUser.logout(drone);

        // Log into Share on the other node (e.g. nodeB) as assignee user
        DashBoardPage dashBoardPage = ShareUser.login(drone, testUser2, DEFAULT_PASSWORD).render();

        // verify the task is not displayed in My tasks dashlet. Server B
        assertFalse(getTasksDescriptionList(dashBoardPage).contains(workFlowName), "Task is displayed for assignee user on My Tasks dashlet. Server B");

        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

        // verify the task is not displayed in My tasks page. Server B
        assertFalse(myTasksPage.isTaskPresent(workFlowName), "Task is presented on ' My Task page' for assignee user. Server B");

        ShareUser.logout(drone);
    }

    private List<String> getTasksDescriptionList(DashBoardPage dashBoardPage)
    {
        MyTasksDashlet myTasksDashlet = dashBoardPage.getDashlet("my-tasks").render();
        List<ShareLink> tasks = myTasksDashlet.getTasks();
        List<String> tasksDescriptionList = new ArrayList<>();

        for (ShareLink task : tasks)
        {
            tasksDescriptionList.add(task.getDescription());
        }
        return tasksDescriptionList;
    }

    private DocumentLibraryPage startNewWorkflow(DocumentLibraryPage documentLibraryPage, String fileName, String reviewer, String workFlowName)
            throws InterruptedException
    {
        StartWorkFlowPage startWorkFlowPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectStartWorkFlow().render();
        NewWorkflowPage newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW)).render();

        String dueDate = new DateTime().plusDays(2).toString("dd/MM/yyyy");
        List<String> reviewers = new ArrayList<>();
        reviewers.add(reviewer);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails(siteName, siteName, reviewers);
        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        documentLibraryPage = newWorkflowPage.startWorkflow(formDetails).render();
        return documentLibraryPage;
    }

}
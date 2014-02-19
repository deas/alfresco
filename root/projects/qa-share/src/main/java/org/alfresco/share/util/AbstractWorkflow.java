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
package org.alfresco.share.util;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * @author Abhijeet Bharade
 */
public abstract class AbstractWorkflow extends AbstractCloudSyncTest
{
    protected String onPremUser;
    protected String siteName = "";
    protected DocumentLibraryPage libPage;
    protected static String taskName;
    protected SharePage sharePage;
    protected List<String> reviewers = new ArrayList<String>();

    private static Log logger = LogFactory.getLog(AbstractWorkflow.class);

    protected final String APPROVAL_PERCENTAGE_HELP_TEXT = "This field must have a value between 1 and 100.";

    /**
     * 
     */
    public AbstractWorkflow()
    {
        super();
    }

//    /**
//     * Use hybrid drone to complete the task on cloud.
//     * 
//     * @param user
//     */
//    protected void completeTaskOnCloud(String user)
//    {
//
//        ShareUser.login(hybridDrone, user, DEFAULT_PASSWORD);
//
//        try
//        {
//            sharePage = openMyTaskPage(hybridDrone);
//        } catch (InterruptedException e)
//        {
//
//        }
//        sharePage = completeTask(taskName);
//        ShareUser.logout(hybridDrone);
//    }

    /**
     * Assumes user has just logged in and is on dashboard. This method tries to
     * open the active tasks tab. It clicks on completed then active as for
     * first time we get Loding error.
     * 
     * @param drone
     * @throws InterruptedException
     */
    public static MyTasksPage openMyTaskPage(WebDrone drone) throws InterruptedException
    {
        if (logger.isInfoEnabled())
            logger.info("openTask - " + taskName);
        DashBoardPage dashBoard = drone.getCurrentPage().render();

        MyTasksPage myTasksPage = dashBoard.getNav().selectMyTasks().render(maxWaitTime);
        // Observed on enterprise that there is loading error everytime we load
        // My Tasks page.
        if (!isAlfrescoVersionCloud(drone))
        {
            myTasksPage.selectCompletedTasks();
            drone.refresh();
            myTasksPage.render(maxWaitTime);
            myTasksPage.selectActiveTasks();
            myTasksPage.render(maxWaitTime);
        }
        assertTrue(myTasksPage.getTitle().contains("My Tasks"));
        return myTasksPage;
    }

//    /**
//     * Assumes flow is on My task page. Opens the given task and completes it.
//     * 
//     * @return {@link SharePage} depends from where task started.
//     */
//    protected SharePage completeTask(String taskName)
//    {
//        EditTaskPage editTasksPage = null;
//        logger.info("completeTask - " + taskName);
//
//        MyTasksPage myTasksPage = (MyTasksPage) drone.getCurrentPage();
//        // Implement loop
//        for (int i = 0; i < retrySearchCount; i++)
//        {
//            try
//            {
//                editTasksPage = myTasksPage.navigateToEditTaskPage(taskName).render(maxWaitTime);
//                break;
//            } catch (PageException e)
//            {
//                webDriverWait(drone, maxWaitTime);
//                drone.refresh();
//            }
//        }
//        editTasksPage.selectStatusDropDown(TaskStatus.COMPLETED);
//        sharePage = editTasksPage.selectTaskDoneButton().render(maxWaitTime);
//
//        return sharePage;
//    }



//    /**
//     * Creates a file and uploads it.
//     * 
//     * @param fileName
//     * @throws Exception
//     */
//    protected void createAndUploadFile(String fileName) throws Exception
//    {
//        String[] fileInfo =
//        { fileName, DOCLIB };
//
//        // Open Site DashBoard
//        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//        HtmlPage htmlPage = drone.getCurrentPage();
//
//        if (!(htmlPage instanceof SiteDashboardPage))
//        {
//            ShareUser.openSiteDashboard(drone, siteName);
//        }
//
//        // uploadFile
//        libPage = ShareUser.uploadFileInFolder(drone, fileInfo);
//    }

//    /**
//     * Purpose of this method is to login with an enterprise user and start a
//     * cloud task or review workflow.
//     * 
//     * @throws InterruptedException
//     */
//    protected void initiateCloudReviewWorkflow(int approvalPercentage, TaskType taskType, String fileName, String... reviewers) throws InterruptedException
//    {
//        sharePage = drone.getCurrentPage().render();
//
//        sharePage.getNav().selectMyDashBoard().render();
//        assertTrue(sharePage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
//
//        // Start workflow from library
//        StartWorkFlowPage workFlowPage = startWorkFLow(fileName).render();
//        assertTrue(workFlowPage.isBrowserTitle("Start workflow"));
//
//        // Create cloud/review workflow
//        CloudTaskOrReviewPage cloudTaskOrReviewPage = ((CloudTaskOrReviewPage) workFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW)).render();
//        assertTrue(cloudTaskOrReviewPage.isBrowserTitle("Start workflow"));
//
//        sharePage = submitFormDetails(siteName, reviewers, approvalPercentage, taskName);
//
//    }

//    /**
//     * This method goes into doc lib and creates
//     * 
//     * @param fileName
//     * @return {@link StartWorkFlowPage}
//     */
//    protected StartWorkFlowPage startWorkFLow(String fileName)
//    {
//        // Visit document library
//        libPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
//        DocumentDetailsPage documentDetailsPage = libPage.selectFile(fileName);
//        return documentDetailsPage.selectStartWorkFlowPage().render();
//    }

//    /**
//     * Creates the bean for {@link WorkFlowFormDetails} and creates a new
//     * workflow.
//     * 
//     * @param siteName
//     * @param cloudUser
//     * @param approvalPercentage
//     * @param message
//     * 
//     * @return
//     * @throws InterruptedException
//     */
//    public SharePage submitFormDetails(String siteName, String[] cloudUser, int approvalPercentage, String message) throws InterruptedException
//    {
//        if (StringUtils.isEmpty(siteName) || cloudUser.length < 1 || StringUtils.isEmpty(message))
//        {
//            throw new UnsupportedOperationException("siteName or message or cloudUsers cannot be blank");
//        }
//        CloudTaskOrReviewPage cloudTaskOrReviewPage = ((StartWorkFlowPage) drone.getCurrentPage()).getCurrentPage().render();
//        WorkFlowFormDetails formDetails = createWorkFlowDetails(siteName, approvalPercentage, cloudUser);
//        cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);
//
//        return cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxWaitTime);
//    }

    /**
     * @param siteName
     * @param cloudUser
     * @param approvalPercentage
     * @return {@link WorkFlowFormDetails}
     */
    protected WorkFlowFormDetails createWorkFlowDetails(String siteName, int approvalPercentage, String... cloudUsers)
    {
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setSiteName(siteName);
        formDetails.setReviewers(Arrays.asList(cloudUsers));
        formDetails.setMessage(taskName);
        formDetails.setApprovalPercentage(approvalPercentage);
        return formDetails;
    }

//    /**
//     * @param siteName
//     * @param cloudUser
//     * @return {@link WorkFlowFormDetails}
//     */
//    protected WorkFlowFormDetails createWorkFlowDetails(String siteName, String... cloudUsers)
//    {
//        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
//        formDetails.setSiteName(siteName);
//        formDetails.setReviewers(Arrays.asList(cloudUsers));
//        formDetails.setMessage(taskName);
//        return formDetails;
//    }

    /**
     * This method creates 5 reviewers in HYBRID_DOMAIN. Also upgrades the
     * HYBRID_DOMAIN.
     * 
     * @param drone
     * @throws Exception
     */
    protected void initialUsersCreation() throws Exception
    {
        for (int i = 2; i <= 5; i++)
        {
            // creates 5 users like user1@hybrid.test
            String reviewer = getUserNameForDomain(Integer.toString(i), DOMAIN_HYBRID);
            reviewers.add(reviewer);
            logger.info("Trying to create user - " + reviewer + " on " + DOMAIN_HYBRID + " network");
            boolean createActivateUser = false;
            try
            {
                createActivateUser = CreateUserAPI.CreateActivateUser(hybridDrone, DEFAULT_PREMIUMNET_USER, reviewer);
            } catch (Exception e)
            {
            }
            if (createActivateUser)
            {
                logger.info("Successfully created user - " + reviewer + " on " + DOMAIN_HYBRID + " network");
                if (i == 1)
                {
                    HttpResponse response = CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
                        logger.info("Domain " + DOMAIN_HYBRID + " successfully upgraded");
                    else
                    {
                        logger.info("Could not upgrade " + DOMAIN_HYBRID);
                        throw new Exception("Could not upgrade " + DOMAIN_HYBRID);
                    }
                }
            }

        }
    }

//    /**
//     * This method creates site on Enterprise and sync OP user with cloud user.
//     * 
//     * @param onPremUser
//     *            -
//     * @param siteForSyncing
//     * 
//     * @throws Exception
//     */
//    protected void createSiteAndSyncOPUser(String onPremUser, String siteForSyncing) throws Exception
//    {
//        if (alfrescoVersion.equals(AlfrescoVersion.Enterprise42) && StringUtils.isNotEmpty(onPremUser) && StringUtils.isNotEmpty(siteForSyncing))
//        {
//            // On enterprise
//            ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//            ShareUser.createSite(drone, siteForSyncing, AbstractTests.SITE_VISIBILITY_PUBLIC);
//            signInToAlfrescoInTheCloud(drone, reviewers.get(0), DEFAULT_PASSWORD);
//            ShareUser.logout(drone);
//        } else
//        {
//            throw new UnsupportedOperationException("Valid only for Enterprise42. Also onPremUser and siteForSyncing cannot be null.");
//        }
//    }

//    /**
//     * Navigate to CloudTaskOrReviewPage from MyTasks page -> Start Workflow.
//     * 
//     * @return {@link CloudTaskOrReviewPage}
//     */
//    protected CloudTaskOrReviewPage getCloudReviewPageFromMyTasks()
//    {
//        if (alfrescoVersion.equals(AlfrescoVersion.Enterprise42))
//        {
//            MyTasksPage myTasksPage = ((DashBoardPage) drone.getCurrentPage()).getNav().selectMyTasks().render();
//            StartWorkFlowPage startWorkFlowPage = myTasksPage.selectStartWorkflowButton().render();
//            CloudTaskOrReviewPage cloudTaskOrReviewPage = ((CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW))
//                    .render();
//            return cloudTaskOrReviewPage;
//        } else
//        {
//            throw new UnsupportedOperationException("Valid only for Enterprise42");
//        }
//    }

    /**
     * Method to get Task Details header for Simple Cloud Task
     * 
     * @param workFlowName
     * @return
     */
    protected String getWorkFlowDetailsHeader(String workFlowName)
    {
        return "Details: " + workFlowName + " (Start a task or review on Alfresco Cloud)";
    }

    /**
     * Method to get Task Details header for Simple Cloud Task
     * 
     * @param taskName
     * @return
     */
    protected String getSimpleCloudTaskDetailsHeader(String taskName)
    {
        return "Details: " + taskName + " (Task)";
    }

    /**
     * Method to get Task Details header for Cloud Review Task
     * 
     * @param taskName
     * @return
     */
    protected String getCloudReviewTaskDetailsHeader(String taskName)
    {
        return "Details: " + taskName + " (Review)";
    }

    /**
     * Method to return List of Task Status
     * 
     * @return
     */
    protected List<TaskStatus> getTaskStatusList()
    {
        return Arrays.asList(TaskStatus.values());
    }

    /**
     * Method to get the Due Date (Today's date +2) in string format
     * 
     * @return
     */
    protected String getDueDateString()
    {
        return new DateTime().plusDays(2).toString("dd/MM/yyyy");
    }

    /**
     * Method to get date in DateTime format for a given string
     * 
     * @param due
     * @return
     */
    protected DateTime getDueDate(String due)
    {
        return DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime(due);
    }

    /**
     * This method is used to check if the task is present or not. If the task
     * is not displayed, retry for defined time(maxWaitTime_CloudSync)
     * 
     * @param drone
     * @param fileName
     * @param isTaskExpected
     * @return boolean
     */
    public static boolean checkIfTaskIsPresent(WebDrone drone, String taskName, boolean isTaskExpected)
    {
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        myTasksPage.render();

        RenderTime t = new RenderTime(maxWaitTime_CloudSync);
        try
        {
            while (true)
            {
                t.start();
                try
                {
                    if (isTaskExpected)
                    {
                        if (myTasksPage.isTaskPresent(taskName))
                        {
                            return true;
                        }
                    } else
                    {
                        if (!myTasksPage.isTaskPresent(taskName))
                        {
                            return true;
                        }
                    }

                    webDriverWait(drone, 1000);
                    drone.refresh();
                } finally
                {
                    t.end();
                }
            }
        } catch (PageRenderTimeException p)
        {
        }

        return false;
    }
}
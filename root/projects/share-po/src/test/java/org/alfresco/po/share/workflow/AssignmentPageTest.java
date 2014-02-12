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
package org.alfresco.po.share.workflow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.share.util.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify MyWorkFlowsPage.
 * 
 * @author Ranjith Manyam
 * @since 1.7.1
 */
@Listeners(FailedTestListener.class)
public class AssignmentPageTest extends AbstractTest
{
    DashBoardPage dashBoardPage;
    MyWorkFlowsPage myWorkFlowsPage;
    StartWorkFlowPage startWorkFlowPage;
    CloudTaskOrReviewPage cloudTaskOrReviewPage;
    AssignmentPage assignmentPage;
    WorkFlowDetailsPage workFlowDetailsPage;
    MyTasksPage myTasksPage;
    EditTaskPage editTaskPage;

    String workFlow1;
    String workFlow2;
    String dueDate;
    String workFlowComment;

    String cloudNetwork;
    String cloudUserSite;
    String cloudFolder;

    private static String siteName;
    private File wfTestFile;

    /**
     * Pre test to create a site and document content with properties set and navigate to StartWorkFlow  page.
     *
     * @throws Exception
     */
    @SuppressWarnings("unused")
    @BeforeClass(groups = "Hybrid")
    private void prepare() throws Exception
    {
        dashBoardPage = loginAs(username, password);

        workFlow1 = "MyWF-" + System.currentTimeMillis() + "-1";
        workFlow2 = "MyWF-" + System.currentTimeMillis() + "-2";
        dueDate = "17/09/2015";
        workFlowComment = System.currentTimeMillis() + "-Comment";

        cloudNetwork = cloudUserName.split("@")[1];
        cloudUserSite = "Auto Account's Home";
        cloudFolder = "Documents";

        siteName = "site" + System.currentTimeMillis();
        wfTestFile = SiteUtil.prepareFile("WorkFlowTestFile");
        SiteUtil.createSite(drone, siteName, "description", "Public");

        SiteDashboardPage siteDashboardPage = drone.getCurrentPage().render();
        DocumentLibraryPage documentLibraryPage = siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();
        UploadFilePage uploadForm = documentLibraryPage.getNavigation().selectFileUpload().render();
        uploadForm.uploadFile(wfTestFile.getCanonicalPath()).render();

        signInToCloud(drone, cloudUserName, cloudUserPassword);
    }

    @AfterClass (alwaysRun = true)
    public void tearDown()
    {
        SiteUtil.deleteSite(drone, siteName);
        disconnectCloudSync(drone);
        SharePage sharePage = drone.getCurrentPage().render();
        myWorkFlowsPage = sharePage.getNav().selectWorkFlowsIHaveStarted().render();
        myWorkFlowsPage.render();
        if(myWorkFlowsPage.isWorkFlowPresent(workFlow1))
        {
            myWorkFlowsPage.cancelWorkFlow(workFlow1);
        }
        myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();
        if(myWorkFlowsPage.isWorkFlowPresent(workFlow1))
        {
            myWorkFlowsPage.deleteWorkFlow(workFlow1);
        }


    }

    @Test(groups = "Hybrid")
    public void enterRequiredApprovalPercentage() throws Exception
    {
        myWorkFlowsPage = dashBoardPage.getNav().selectWorkFlowsIHaveStarted().render();

        startWorkFlowPage = myWorkFlowsPage.selectStartWorkflowButton().render();
        cloudTaskOrReviewPage = (CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW);

        cloudTaskOrReviewPage.enterMessageText(workFlow1);
        cloudTaskOrReviewPage.enterDueDateText(dueDate);
        cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);
        cloudTaskOrReviewPage.enterRequiredApprovalPercentage("100");
    }

    @Test(groups = "Hybrid", dependsOnMethods = "enterRequiredApprovalPercentage")
    public void verifyDestinationDetails()
    {
        DestinationAndAssigneePage destinationAndAssigneePage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();
        destinationAndAssigneePage.selectNetwork(cloudNetwork);
        destinationAndAssigneePage.selectSite(cloudUserSite);
        destinationAndAssigneePage.selectFolder(cloudFolder);
        destinationAndAssigneePage.selectSubmitButtonToSync();

        cloudTaskOrReviewPage.render();

        Assert.assertEquals(cloudTaskOrReviewPage.getDestinationNetwork(), cloudNetwork);
        Assert.assertEquals(cloudTaskOrReviewPage.getDestinationSite(), cloudUserSite);
        Assert.assertEquals(cloudTaskOrReviewPage.getDestinationFolder(), cloudFolder+"/");
    }

    @Test(groups = "Hybrid", dependsOnMethods = "verifyDestinationDetails")
    public void isNoItemsFoundMessageDisplayed()
    {
        assignmentPage = cloudTaskOrReviewPage.selectAssignmentPage().render();
        Assert.assertTrue(assignmentPage.isNoItemsFoundMessageDisplayed("RandomUserString"));
        Assert.assertFalse(assignmentPage.isNoItemsFoundMessageDisplayed(cloudUserName));
    }

    @Test(groups = "Hybrid", dependsOnMethods = "isNoItemsFoundMessageDisplayed")
    public void isUserFound()
    {
        Assert.assertFalse(assignmentPage.isUserFound("RandomUserString"));
        Assert.assertTrue(assignmentPage.isUserFound(cloudUserName));

        List<String> userNames = new ArrayList<String>();
        userNames.add(cloudUserName);

        assignmentPage.selectAssignment(userNames);
    }

    @Test(groups = "Hybrid", dependsOnMethods = "isUserFound")
    public void selectItem()
    {
        cloudTaskOrReviewPage.render();
        cloudTaskOrReviewPage.selectItem(wfTestFile.getName(), siteName);
    }

    @Test(groups = "Hybrid", dependsOnMethods = "selectItem")
    public void selectStartWorkflow()
    {
        cloudTaskOrReviewPage.render();
        cloudTaskOrReviewPage.selectStartWorkflow();
        myWorkFlowsPage.render();
    }

    @Test(groups = "Hybrid", dependsOnMethods = "selectStartWorkflow")
    public void getAssignee()
    {
        workFlowDetailsPage = myWorkFlowsPage.selectWorkFlow(workFlow1).render();
        Assert.assertTrue(workFlowDetailsPage.getAssignee().contains(cloudUserName));
    }

    // Workflow1 is created and currently the user is on WorkFlowDetailsPage
}

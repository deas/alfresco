package org.alfresco.share.workflow.cloudReviewTask;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.task.TaskDetailsPage;
import org.alfresco.po.share.task.TaskInfo;
import org.alfresco.po.share.task.TaskItem;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.task.EditTaskPage.Button;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.CurrentTaskType;
import org.alfresco.po.share.workflow.KeepContentStrategy;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.po.share.workflow.Priority;
import org.alfresco.po.share.workflow.SendEMailNotifications;
import org.alfresco.po.share.workflow.TaskHistoryPage;
import org.alfresco.po.share.workflow.TaskType;
import org.alfresco.po.share.workflow.WorkFlowDescription;
import org.alfresco.po.share.workflow.WorkFlowDetailsCurrentTask;
import org.alfresco.po.share.workflow.WorkFlowDetailsHistory;
import org.alfresco.po.share.workflow.WorkFlowDetailsItem;
import org.alfresco.po.share.workflow.WorkFlowDetailsPage;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.po.share.workflow.WorkFlowHistoryOutCome;
import org.alfresco.po.share.workflow.WorkFlowHistoryType;
import org.alfresco.po.share.workflow.WorkFlowStatus;
import org.alfresco.po.share.workflow.WorkFlowTitle;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.tika.parser.prt.PRTParser;
import org.jboss.netty.channel.socket.nio.ShareableWorkerPool;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class EditCloudReviewTaskTests extends AbstractWorkflow {

	private String testDomain;
	private String opUser;
	private String cloudUser;
	private String cloudSite;
	private String opSite;
	private String fileName;
	private String folderName;

	@Override
	@BeforeClass(alwaysRun = true)
	public void setup() throws Exception {

		super.setup();
		testName = this.getClass().getSimpleName();
		testDomain = DOMAIN_HYBRID;

		opUser = getUserNameForDomain(testName + "opUser", testDomain);
		cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);

		folderName = getFolderName(testName);
		cloudSite = getSiteName(testName + "CL4");
		opSite = getSiteName(testName + "OP4");

	}

	@BeforeClass(groups = "DataPrepHybridWorkflow", dependsOnMethods = "setup")
	public void dataPrep_createUsers() throws Exception {

		String opUser = getUserNameForDomain(testName + "opUser", testDomain);
		String[] userInfo1 = new String[] { opUser };
		String cloudUser = getUserNameForDomain(testName + "cloudUser",
				testDomain);
		String[] cloudUserInfo1 = new String[] { cloudUser };

		folderName = getFolderName(testName);

		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

		// Create User1 (Cloud)
		CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME,
				cloudUserInfo1);
		CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME,
				testDomain, "1000");

		// Login to User1, set up the cloud sync
		ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
		signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
		ShareUser.createSite(drone, opSite, SITE_VISIBILITY_PUBLIC);
		ShareUser.logout(drone);

		ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
		ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
		ShareUser.logout(hybridDrone);

	}

	@Test(groups = "DataPrepHybrid")
	public void dataPrep_15622() throws Exception {

		folderName = getFolderName(testName);
		fileName = getFileName(testName) + "-15622Test1" + ".txt";
		String workFlowName = "Cloud Review Task test message" + testName
				+ "-15622CL1";

		String[] fileInfo = { fileName, DOCLIB };

		ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
		ShareUser.openSiteDashboard(drone, opSite);
		ShareUser.uploadFileInFolder(drone, fileInfo).render();
		ShareUser.openSitesDocumentLibrary(drone, opSite).render();

		CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow
				.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

		List<String> userNames = new ArrayList<String>();
		userNames.add(cloudUser);

		WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

		formDetails.setMessage(workFlowName);
		formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
		formDetails.setTaskPriority(Priority.MEDIUM);
		formDetails.setSiteName(cloudSite);
		formDetails.setReviewers(userNames);
		formDetails.setApprovalPercentage(100);
		formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

		// Create Workflow using File1
		cloudTaskOrReviewPage.startWorkflow(formDetails).render();

		ShareUser.logout(drone);

	}

	/**
	 * AONE-15622:Cloud Review Task - Edit Task Details (Cloud)
	 */
	@Test(groups = "Hybrid", enabled = true)
	public void AONE_15622() throws Exception {

		String workFlowName = "Cloud Review Task test message" + testName
				+ "-15622CL";
		fileName = getFileName(testName) + "-15622Test1" + ".txt";

		try {
			
			ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
			
			// --- Step 1 ---
			// --- Step action ---
			// Cloud Click on Edit button.
			// --- Expected results ---
			// The button is pressed. Edit Task page is opened.

	        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
	        EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage(workFlowName).render();
	        Assert.assertTrue(editTaskPage.getTitle().contains("Edit Task"));
			
			// --- Step 2 ---
			// --- Step action ---
			// Verify the available controls on Edit Task page.
			// --- Expected results ---
			// The following additional controls are present:
		    // Status drop-down list
		    // View More Actions button for the document
		    // Comment field
		    // Approve button
		    // Reject button
		    // Save and close button
		    // Cancel button
	        
	        List<TaskStatus> statusOptions = editTaskPage.getStatusOptions();
            assertEquals(statusOptions.size(), TaskStatus.values().length);
     
            List<TaskItem> taskItems = editTaskPage.getTaskItem(fileName);
            assertTrue(taskItems.get(0).getViewMoreActionsLink().getDescription().contains("View More Actions"));         
            assertTrue(editTaskPage.isCommentTextAreaDisplayed());
            assertTrue(editTaskPage.isButtonsDisplayed(Button.APPROVE));
            assertTrue(editTaskPage.isButtonsDisplayed(Button.REJECT));
            assertTrue(editTaskPage.isButtonsDisplayed(Button.SAVE_AND_CLOSE));
            assertTrue(editTaskPage.isButtonsDisplayed(Button.CANCEL));

			// --- Step 3 ---
			// --- Step action ---
			// Verify the Status drop-down list.
			// --- Expected results ---
			// The following values are available:
			// Not yet started (set by default)
			// In Progress
			// On Hold
			// Canceled
			// Completed

            assertTrue(statusOptions.containsAll(getTaskStatusList()));

			// --- Step 4 ---
			// --- Step action ---
			// Specify any value in the Status drop-down list, e.g. 'In Progress'.
			// --- Expected results ---
			// Performed correctly.

            editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.INPROGRESS);

			// --- Step 5 ---
			// --- Step action ---
			// Add any data into the Comment field, e.g. "test comment".
			// --- Expected results ---
			// Performed correctly.
            editTaskPage.enterComment("test comment");
            assertEquals(editTaskPage.readCommentFromCommentTextArea(), "test comment");
			
			// --- Step 6 ---
			// --- Step action ---
			// Click on Cancel button.
			// --- Expected results ---
			// Edit Task page is closed. Task Details are displayed. No data was changed.
			// Comment: (None)
            myTasksPage = editTaskPage.selectCancelButton().render();
            
            TaskDetailsPage taskDetailsPage = myTasksPage.selectViewTasks(workFlowName);
            assertEquals(taskDetailsPage.getComment(), NONE);
			
			// --- Step 7 ---
			// --- Step action ---
			// Repeat steps 1-5.
			// --- Expected results ---
			// Performed correctly.
            
            taskDetailsPage.selectEditButton().render();
            editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);
            editTaskPage.enterComment("test comment");
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.INPROGRESS);
            assertEquals(editTaskPage.readCommentFromCommentTextArea(), "test comment");
			
			// --- Step 7 ---
			// --- Step action ---
			// Click on Save and Close button.
			// --- Expected results ---
			// Edit Task page is closed. Task Details are displayed. The specified data was changed.
            taskDetailsPage = editTaskPage.selectSaveButton().render();
            assertTrue(taskDetailsPage.isBrowserTitle("Task Details"));
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.INPROGRESS);
            assertEquals(taskDetailsPage.getComment(), "test comment");
            
        	ShareUser.logout(hybridDrone);
			
		} catch (Throwable t) {
			reportError(hybridDrone, testName + "-HY", t);
		}

	}
	
	@Test(groups = "DataPrepHybrid")
	public void dataPrep_15623() throws Exception {

		folderName = getFolderName(testName);
		fileName = getFileName(testName) + "-15623Test2" + ".txt";
		String workFlowName = "Cloud Review Task test message" + testName
				+ "-15623CL1";

		String[] fileInfo = { fileName, DOCLIB };

		ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
		ShareUser.openSiteDashboard(drone, opSite);
		ShareUser.uploadFileInFolder(drone, fileInfo).render();
		ShareUser.openSitesDocumentLibrary(drone, opSite).render();

		CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow
				.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

		List<String> userNames = new ArrayList<String>();
		userNames.add(cloudUser);

		WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

		formDetails.setMessage(workFlowName);
		formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
		formDetails.setTaskPriority(Priority.MEDIUM);
		formDetails.setSiteName(cloudSite);
		formDetails.setReviewers(userNames);
		formDetails.setApprovalPercentage(100);
		formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

		// Create Workflow using File1
		cloudTaskOrReviewPage.startWorkflow(formDetails).render();

		ShareUser.logout(drone);

	}
	
	/**
	 * AONE-15623:Cloud Review Task - Edit Task Details (Cloud)
	 */
	@Test(groups = "Hybrid", enabled = true)
	public void AONE_15623() throws Exception {

		String workFlowName = "Cloud Review Task test message" + testName
				+ "-15623CL1";

		try {
			
			ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
			
			// --- Step 1 ---
			// --- Step action ---
			// Cloud Click on Edit button.
			// --- Expected results ---
			// The button is pressed. Edit Task page is opened.

	        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
	        EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage(workFlowName).render();
	        Assert.assertTrue(editTaskPage.getTitle().contains("Edit Task"));
			
			// --- Step 2 ---
			// --- Step action ---
			// Try to add any item to the existing set. 
			// --- Expected results ---
			// It is not possible to add any item to the task. 
	        
	        Assert.assertFalse(editTaskPage.isButtonsDisplayed(Button.ADD));

			// --- Step 3 ---
			// --- Step action ---
			// Try to remove the existing item from the task. 
			// --- Expected results ---
			// It is not possible to remove item from the task. 
            
	        Assert.assertFalse(editTaskPage.isButtonsDisplayed(Button.REMOVE_ALL));
            
        	ShareUser.logout(hybridDrone);
			
		} catch (Throwable t) {
			reportError(hybridDrone, testName + "-HY", t);
		}

	}


}

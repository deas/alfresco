
package org.alfresco.share.util;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.task.TaskDetailsPage;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.po.share.workflow.NewWorkflowPage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.po.share.workflow.TaskHistoryPage;
import org.alfresco.po.share.workflow.TaskType;
import org.alfresco.po.share.workflow.WorkFlow;
import org.alfresco.po.share.workflow.WorkFlowDetailsPage;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.po.share.workflow.WorkFlowType;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * @author Ranjith Manyam
 * 
 */
public class ShareUserWorkFlow extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(ShareUserWorkFlow.class);

    /**
     * Default Constructor
     */
    public ShareUserWorkFlow()
    {
        if (logger.isTraceEnabled())
        {
            logger.debug(this.getClass().getSimpleName() + " instantiated");
        }
    }

    /**
     * Method to Navigate to MyTasksPage
     * 
     * @param drone
     * @return MyTasksPage
     */
    public static MyTasksPage navigateToMyTasksPage(WebDrone drone)
    {
        return ShareUser.getSharePage(drone).getNav().selectMyTasks().render();
    }

    /**
     * Method to Navigate to WorkFlows I've Started (MyWorkFlows) page
     * 
     * @param drone
     * @return MyWorkFlowsPage
     */
    public static MyWorkFlowsPage navigateToMyWorkFlowsPage(WebDrone drone)
    {
        return ShareUser.getSharePage(drone).getNav().selectWorkFlowsIHaveStarted().render();
    }

    /**
     * Method to select StartWorkflow from MyTasksPage
     * 
     * @param drone
     * @return StartWorkFlowPage
     */
    public static StartWorkFlowPage selectStartWorkFlowFromMyTasksPage(WebDrone drone)
    {
        MyTasksPage myTasksPage = navigateToMyTasksPage(drone);
        return myTasksPage.selectStartWorkflowButton().render();
    }

    /**
     * Method to select StartWorkflow from MyWorkFlowsPage
     * 
     * @param drone
     * @return StartWorkFlowPage
     */
    public static StartWorkFlowPage selectStartWorkFlowFromMyWorkFlowsPage(WebDrone drone)
    {
        MyWorkFlowsPage myWorkFlowsPage = navigateToMyWorkFlowsPage(drone);
        return myWorkFlowsPage.selectStartWorkflowButton().render();
    }

    /**
     * Method to select StartWorkflow from DocumentDetailsPage
     *
     * @param drone
     * @return StartWorkFlowPage
     */

    public static StartWorkFlowPage selectStartWorkFlowFromDetailsPage(WebDrone drone)
    {
        DocumentDetailsPage documentDetailsPage = drone.getCurrentPage().render();
        StartWorkFlowPage startWorkFlowPage = documentDetailsPage.selectStartWorkFlowPage();
        return startWorkFlowPage.render();
    }

    /**
     * Method to Navigate to MyWorkFlowsPage (WorkFlows I've started), select
     * Start WorkFlow button, Select Cloud Task Or Review from workflows
     * dropdown and select given TaskType from Type drop down
     * 
     * @param drone
     * @param taskType
     * @return CloudTaskOrReviewPage
     */
    private static CloudTaskOrReviewPage startCloudTaskOrReviewWorkFlow(WebDrone drone, TaskType taskType)
    {
        StartWorkFlowPage startWorkFlowPage = selectStartWorkFlowFromMyWorkFlowsPage(drone);
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ((CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW)).render();
        cloudTaskOrReviewPage.selectTask(taskType);
        return cloudTaskOrReviewPage;
    }

    /**
     * Method to Start Simple Cloud Task from any share page
     * 
     * @param drone
     * @return CloudTaskOrReviewPage
     */
    public static CloudTaskOrReviewPage startSimpleCloudTaskWorkFlow(WebDrone drone)
    {
        return startCloudTaskOrReviewWorkFlow(drone, TaskType.SIMPLE_CLOUD_TASK);
    }

    /**
     * Method to start Cloud Review Task from any share page
     * 
     * @param drone
     * @return CloudTaskOrReviewPage
     */
    public static CloudTaskOrReviewPage startCloudReviewTaskWorkFlow(WebDrone drone)
    {
        return startCloudTaskOrReviewWorkFlow(drone, TaskType.CLOUD_REVIEW_TASK);
    }

    /**
     * Method to select Start WorkFlow from Document Library page by clicking on
     * More Actions of a content item
     * 
     * @param drone
     * @param fileName
     * @return {@link CloudTaskOrReviewPage}
     */
    public static CloudTaskOrReviewPage startWorkFlowFromDocumentLibraryPage(WebDrone drone, String fileName)
    {
        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();
        StartWorkFlowPage startWorkFlowPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectStartWorkFlow().render();
        return ((CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW)).render();
    }

    /**
     * @param drone
     * @param userName
     * @param workFlowName
     * @return
     */
    public static MyTasksPage completeWorkFlow(WebDrone drone, String userName, String workFlowName)
    {
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        // Edit task and mark it as complete
        EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage(workFlowName, userName).render();
        editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);

        return editTaskPage.selectTaskDoneButton().render();
    }

    /**
     * This methos is used to approve or reject the task from the cloud edit
     * task page. User should be logged in already.
     * 
     * @param drone
     * @param userName
     * @param workFlowName
     * @param comments
     * @return MyTasksPage
     */
    public static MyTasksPage approveOrRejectTask(WebDrone drone, String userName, String workFlowName, String comments, EditTaskAction task_APPROVE_REJECT)
    {
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        // Edit task and mark it as complete
        EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage(workFlowName, userName).render();
        editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);

        if (!StringUtils.isEmpty(comments))
        {
            editTaskPage.enterComment(comments);
        }

        if (task_APPROVE_REJECT.equals(EditTaskAction.APPROVE))
        {
            myTasksPage = editTaskPage.selectApproveButton().render();
        }
        else if (task_APPROVE_REJECT.equals(EditTaskAction.REJECT))
        {
            myTasksPage = editTaskPage.selectRejectButton().render();
        }

        return myTasksPage;
    }

    /**
     * This method assumes user is already in Edit Task page.
     * 
     * @param drone
     * @param taskStatus
     * @param comment
     * @param action
     * @return
     */
    public static HtmlPage completeTask(WebDrone drone, TaskStatus taskStatus, String comment, EditTaskAction action)
    {
        EditTaskPage editTaskPage = ShareUser.getSharePage(drone).render();
        editTaskPage.selectStatusDropDown(taskStatus);

        if (comment != null)
        {
            editTaskPage.enterComment(comment);
        }

        switch (action)
        {
        case APPROVE:
            editTaskPage.selectApproveButton();
            break;
        case REJECT:
            editTaskPage.selectRejectButton();
            break;
        case TASK_DONE:
            editTaskPage.selectTaskDoneButton();
            break;
        case SAVE:
            editTaskPage.selectSaveButton();
            break;
        case CANCEL:
            editTaskPage.selectCancelButton();
            break;
        }
        return ShareUser.getSharePage(drone).render();
    }

    /**
     * Method to Complete task with out comment
     * 
     * @param drone
     * @param taskStatus
     * @param action
     * @return {@link HtmlPage}
     */
    public static HtmlPage completeTask(WebDrone drone, TaskStatus taskStatus, EditTaskAction action)
    {
        return completeTask(drone, taskStatus, null, action);
    }

    /**
     * Method to complete task from MyTasks page
     * 
     * @param drone
     * @param taskName
     * @param taskStatus
     * @param comment
     * @param action
     * @return {@link MyTasksPage}
     */
    public static MyTasksPage completeTaskFromMyTasksPage(WebDrone drone, String taskName, TaskStatus taskStatus, String comment, EditTaskAction action)
    {
        MyTasksPage myTasksPage = ShareUser.getSharePage(drone).render();
        myTasksPage.navigateToEditTaskPage(taskName).render();

        myTasksPage = completeTask(drone, taskStatus, comment, action).render();
        return myTasksPage;
    }

    /**
     * Method to complete the task from MyTasks page
     * 
     * @param drone
     * @param taskName
     * @param taskStatus
     * @param action
     * @return {@link MyTasksPage}
     */
    public static MyTasksPage completeTaskFromMyTasksPage(WebDrone drone, String taskName, TaskStatus taskStatus, EditTaskAction action)
    {
        return completeTaskFromMyTasksPage(drone, taskName, taskStatus, null, action);
    }

    /**
     * Method to complete the task from Task Details Page
     * 
     * @param drone
     * @param taskStatus
     * @param comment
     * @param action
     * @return {@link TaskDetailsPage}
     */
    public static TaskDetailsPage completeTaskFromTaskDetailsPage(WebDrone drone, TaskStatus taskStatus, String comment, EditTaskAction action)
    {
        TaskDetailsPage taskDetailsPage = ShareUser.getSharePage(drone).render();
        taskDetailsPage.selectEditButton().render();

        taskDetailsPage = completeTask(drone, taskStatus, comment, action).render();
        return taskDetailsPage;
    }

    /**
     * Method to complete the task from Task Details page
     * 
     * @param drone
     * @param taskStatus
     * @param action
     * @return {@link TaskDetailsPage}
     */
    public static TaskDetailsPage completeTaskFromTaskDetailsPage(WebDrone drone, TaskStatus taskStatus, EditTaskAction action)
    {
        return completeTaskFromTaskDetailsPage(drone, taskStatus, null, action);
    }

    /**
     * Cancel Task from MyTask Page
     * 
     * @param drone
     * @param taskName
     * @return {@link MyTasksPage}
     */
    public static MyTasksPage cancelTaskFromMyTasksPage(WebDrone drone, String taskName)
    {
        MyTasksPage myTasksPage = ShareUser.getSharePage(drone).render();
        TaskHistoryPage taskHistoryPage = myTasksPage.selectTaskHistory(taskName).render();
        myTasksPage = taskHistoryPage.selectCancelWorkFlow().render();
        return myTasksPage;
    }

    /**
     * Navigate to Task History Page
     * 
     * @param drone
     * @param taskName
     * @return {@link TaskHistoryPage}
     */
    public static TaskHistoryPage navigateToTaskHistoryPage(WebDrone drone, String taskName)
    {
        HtmlPage currentPage = ShareUser.getSharePage(drone).render();
        if (currentPage instanceof MyTasksPage)
        {
            MyTasksPage myTasksPage = currentPage.render();
            return myTasksPage.selectTaskHistory(taskName).render();
        }
        else if (currentPage instanceof TaskDetailsPage)
        {
            TaskDetailsPage taskDetailsPage = currentPage.render();
            return taskDetailsPage.selectTaskHistoryLink().render();
        }
        else
        {
            MyTasksPage myTasksPage = navigateToMyTasksPage(drone);
            return myTasksPage.selectTaskHistory(taskName).render();
        }
    }

    /**
     * Method to navigate to Task Details Page
     * 
     * @param drone
     * @param taskName
     * @return {@link TaskDetailsPage}
     */
    public static TaskDetailsPage navigateToTaskDetailsPage(WebDrone drone, String taskName)
    {
        HtmlPage currentPage = ShareUser.getSharePage(drone).render();
        if (currentPage instanceof MyTasksPage)
        {
            MyTasksPage myTasksPage = currentPage.render();
            return myTasksPage.selectViewTasks(taskName).render();
        }
        else
        {
            MyTasksPage myTasksPage = navigateToMyTasksPage(drone);
            return myTasksPage.selectViewTasks(taskName).render();
        }
    }

    /**
     * Method to Cancel a Workflow
     * 
     * @param drone
     * @param workFlowName
     * @return {@link MyWorkFlowsPage}
     */
    public static MyWorkFlowsPage cancelWorkFlow(WebDrone drone, String workFlowName)
    {
        HtmlPage currentPage = ShareUser.getSharePage(drone).render();
        MyWorkFlowsPage myWorkFlowsPage;
        if (currentPage instanceof MyWorkFlowsPage)
        {
            myWorkFlowsPage = currentPage.render();
        }
        else
        {
            myWorkFlowsPage = navigateToMyWorkFlowsPage(drone);
        }
        return myWorkFlowsPage.cancelWorkFlow(workFlowName).render();
    }

    /**
     * Method to navigate to specified workflow's Details Page Method is
     * Enterprise only and not supported for cloud
     * 
     * @param drone
     * @param workFlowName
     * @return {@link WorkFlowDetailsPage}
     */
    public static WorkFlowDetailsPage navigateToWorkFlowDetailsPage(WebDrone drone, String workFlowName)
    {
        if (isAlfrescoVersionCloud(drone))
        {
            throw new UnsupportedOperationException("This util is not supported for Cloud");
        }
        HtmlPage currentPage = ShareUser.getSharePage(drone).render();
        MyWorkFlowsPage myWorkFlowsPage;
        if (currentPage instanceof MyWorkFlowsPage)
        {
            myWorkFlowsPage = currentPage.render();
        }
        else
        {
            myWorkFlowsPage = navigateToMyWorkFlowsPage(drone);
        }
        return myWorkFlowsPage.selectWorkFlow(workFlowName).render();
    }

    /**
     * Method to Start New Workflow, navigate to My Tasks and selects New
     * Workflow
     * 
     * @param drone
     * @return {@link NewWorkflowPage}
     */
    public static NewWorkflowPage startNewWorkFlow(WebDrone drone)
    {
        StartWorkFlowPage startWorkFlowPage = selectStartWorkFlowFromMyTasksPage(drone);
        NewWorkflowPage newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW)).render();
        return newWorkflowPage;
    }
    
    /**
     * Util method to create a {@link WorkFlowType} of task for
     * {@link WorkFlowFormDetails}
     * 
     * @param drone
     * @param newWorkflow
     * @param formDetails
     * @throws InterruptedException
     */
    public static MyTasksPage startWorkflow(WebDrone drone, WorkFlowType newWorkflow, WorkFlowFormDetails formDetails) throws InterruptedException
    {
        StartWorkFlowPage startWorkFlowPage = selectStartWorkFlowFromMyTasksPage(drone);
        WorkFlow workFlow = startWorkFlowPage.getWorkflowPage(newWorkflow);
        MyTasksPage tasksPage = workFlow.startWorkflow(formDetails).render();
        return tasksPage;
    }

    /**
     * Method to get Due Date format on MyTasks page
     * @param dueDateString
     * @return
     */
    public static String getDueDateOnMyTaskPage(String dueDateString)
    {
        try
        {
            DateTime date = DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime(dueDateString);
            return date.toString(DateTimeFormat.forPattern("dd MMMM, yyyy"));
        }
        catch (IllegalArgumentException ie)
        {
            return NONE;
        }
    }

}
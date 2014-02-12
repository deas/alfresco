package org.alfresco.po.share.workflow;

import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

  /**
 * My tasks page Integration test
 * 
 * @author Siva Kaliyappan
 * @since 1.6.2
 */
@Listeners(FailedTestListener.class)
public class MyTasksTest extends AbstractTest
{
    private String siteName;
    private String message;
    DocumentLibraryPage documentLibraryPage;
    MyTasksPage myTasksPage;


    @SuppressWarnings("unused")
    @BeforeClass(groups = "Enterprise4.2")
    private void prepare() throws Exception
    {
        siteName = "AdhocReassign" + System.currentTimeMillis();
        message = siteName;
        loginAs(username, password);
        myTasksPage = ((DashBoardPage) drone.getCurrentPage()).getNav().selectMyTasks().render();
        StartWorkFlowPage startWorkFlowPage = myTasksPage.selectStartWorkflowButton().render();
        NewWorkflowPage newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW)).render();

        List<String> reviewers = new ArrayList<String>();
        reviewers.add(username);
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails(siteName, message, reviewers);
        newWorkflowPage.startWorkflow(formDetails).render();
    }

    @BeforeMethod
    public void navigateToMytasks()
    {
        myTasksPage = myTasksPage.getNav().selectMyTasks().render();
    }

    @Test(groups = "Enterprise4.2")
    public void selectActiveTasksTest()
    {
        SharePage returnedPage = myTasksPage.selectActiveTasks();
        assertTrue("Returned page should be instance of MyTaskPage", returnedPage instanceof MyTasksPage);
    }

    @Test(groups = "Enterprise4.2")
    public void selectCompletedTasksTest()
    {
        SharePage returnedPage = myTasksPage.selectCompletedTasks();
        assertTrue("Returned page should be instance of MyTaskPage", returnedPage instanceof MyTasksPage);
    }
}

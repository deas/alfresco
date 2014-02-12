package org.alfresco.po.share.workflow;

import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.ShareErrorPopup;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.user.CloudSignInPage;
import org.alfresco.po.share.user.CloudSyncPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.webdrone.HtmlPage;
import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify CloudTaskOrReviewPage page load.
 *
 * @author Siva Kaliyappan
 * @since 1.6.2
 */
@Test(groups = {"Hybrid"})
@Listeners(FailedTestListener.class)
public class CloudTaskOrReviewPageTest extends AbstractTest
{
    private String siteName;
    private File file;
    DocumentLibraryPage documentLibraryPage;
    protected long maxPageLoadingTime = 20000;
    private StartWorkFlowPage startWorkFlowPage;
    private CloudTaskOrReviewPage cloudTaskOrReviewPage;

    /**
     * Pre test to create a site and document content with properties set and navigate to StartWorkFlow  page.
     *
     * @throws Exception
     */
    @SuppressWarnings("unused")
    @BeforeClass
    private void prepare() throws Exception
    {
        siteName = "CloudTaskOrReviewPage" + System.currentTimeMillis();
        file = SiteUtil.prepareFile();
        loginAs(username, password);
        MyProfilePage myProfilePage = ((SharePage) drone.getCurrentPage()).getNav().selectMyProfile().render();
        CloudSyncPage cloudSyncPage = myProfilePage.getProfileNav().selectCloudSyncPage().render();
        if (!cloudSyncPage.isDisconnectButtonDisplayed())
        {
            signInToCloud(cloudUserName, cloudUserPassword).render();
        }
        SiteUtil.createSite(drone, siteName, "Public");
        SitePage site = drone.getCurrentPage().render();
        DocumentLibraryPage documentLibPage = site.getSiteNav().selectSiteDocumentLibrary().render();

        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file.getCanonicalPath()).render();
        drone.refresh();

        DocumentDetailsPage documentDetailsPage = documentLibPage.selectFile(file.getName()).render();
        startWorkFlowPage = documentDetailsPage.selectStartWorkFlowPage().render();
        cloudTaskOrReviewPage = ((CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW)).render();
    }

    @AfterClass(alwaysRun = true)
    public void teardown() throws Exception
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    /**
     * This test is to assert and fill the cloud task page form.
     *
     * @throws Exception
     */
    @Test(dependsOnMethods = "selectViewMoreActionsBtnTest")
    public void completeCloudTaskOrReviewPage() throws Exception
    {
        cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);
        assertTrue(cloudTaskOrReviewPage.isTaskTypeSelected(TaskType.CLOUD_REVIEW_TASK));
        // assertFalse(isButtonSubmitted());
        WorkFlowFormDetails formDetails = createWorkflowForm();
        // Fill form Detail
        DocumentDetailsPage documentDetailsPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxPageLoadingTime);
        Assert.assertTrue(documentDetailsPage.isDocumentDetailsPage());
    }

    /**
     * This test is to assert and fill the cloud task page form.
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "completeCloudTaskOrReviewPage")
    public void checkExceptionForNullDocs() throws Exception
    {
        MyTasksPage myTasksPage = ((DashBoardPage) drone.getCurrentPage()).getNav().selectMyTasks().render();
        StartWorkFlowPage startWorkFlowPage = myTasksPage.selectStartWorkflowButton().render();
        cloudTaskOrReviewPage = ((CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW)).render();

        WorkFlowFormDetails formDetails = createWorkflowForm();
        ShareErrorPopup returnedPage = (ShareErrorPopup) cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxPageLoadingTime);
        Assert.assertTrue("Error should be displayed", returnedPage.isShareErrorDisplayed());
    }

    @Test
    public void selectViewMoreActionsBtnTest()
    {
        cloudTaskOrReviewPage.render();
        SharePage returnedPage = cloudTaskOrReviewPage.selectViewMoreActionsBtn(file.getName()).render();
        assertTrue("A document details page should be returned.", returnedPage instanceof DocumentDetailsPage);
        startWorkFlowPage = ((DocumentDetailsPage) returnedPage).selectStartWorkFlowPage().render();
        cloudTaskOrReviewPage = ((CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW)).render();
    }

    /**
     * Created bean {@link WorkFlowFormDetails}.
     * 
     * @return {@link WorkFlowFormDetails}
     */
    private WorkFlowFormDetails createWorkflowForm()
    {
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setSiteName("test");
        List<String> reviewers = new ArrayList<String>();
        reviewers.add(cloudUserName);
        formDetails.setReviewers(reviewers);
        formDetails.setMessage(siteName);
        formDetails.setDueDate("01/10/2015");
        formDetails.setLockOnPremise(false);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
        formDetails.setTaskPriority(Priority.HIGH);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        return formDetails;
    }
    
    /**
     * Method to sign in Cloud page and return Cloud Sync page
     *
     * @return boolean
     */
    public HtmlPage signInToCloud(final String username, final String password)
    {
        final By SIGN_IN_BUTTON = By.cssSelector("button#template_x002e_user-cloud-auth_x002e_user-cloud-auth_x0023_default-button-signIn-button");
        drone.findAndWait(SIGN_IN_BUTTON).click();
        CloudSignInPage cloudSignInPage = new CloudSignInPage(drone);
        cloudSignInPage.loginAs(username, password);
        return drone.getCurrentPage();
    }

}

package org.alfresco.po.share.workflow;
///*
// * Copyright (C) 2005-2013 Alfresco Software Limited.
// *
// * This file is part of Alfresco
// *
// * Alfresco is free software: you can redistribute it and/or modify
// * it under the terms of the GNU Lesser General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * Alfresco is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public License
// * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
// */
//package org.alfresco.webdrone.share.workflow;
//
//import static org.testng.AssertJUnit.assertTrue;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.alfresco.webdrone.share.AbstractTest;
//import org.alfresco.webdrone.share.site.UploadFilePage;
//import org.alfresco.webdrone.share.site.document.DocumentDetailsPage;
//import org.alfresco.webdrone.share.site.document.DocumentLibraryPage;
//import org.alfresco.webdrone.share.site.document.FileDirectoryInfo;
//import org.alfresco.webdrone.testng.listener.FailedTestListener;
//import org.alfresco.webdrone.util.SiteUtil;
//import org.testng.annotations.AfterClass;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Listeners;
//import org.testng.annotations.Test;
//
///**
// * Integration test to verify CloudTaskOrReviewPage page load.
// * 
// * @author Abhijeet Bharade
// * @since 1.6.2
// */
//@Listeners(FailedTestListener.class)
//public class NewWorkflowPageTest extends AbstractTest
//{
//    private String siteName;
//    NewWorkflowPage newWorkflowPage = null;
//    private File fileForWorkflow;
//    private DocumentLibraryPage documentLibPage;
//
//
//    /**
//     * Pre test to create a site and document content with properties set and navigate to StartWorkFlow  page.
//     *
//     * @throws Exception
//     */
//    @SuppressWarnings("unused")
//    @BeforeClass(groups = "Enterprise4.2")
//    private void prepare() throws Exception
//    {
//        // assertTrue(WebDroneUtilTest.checkAlfrescoVersionBeforeClassRun(drone));
//        siteName = "AdhocReassign" + System.currentTimeMillis();
//        fileForWorkflow = SiteUtil.prepareFile("SyncFailFile");
//        loginAs(username, password);
//        SiteUtil.createSite(drone, siteName, "Public");
//        documentLibPage = openSiteDocumentLibraryFromSearch(drone, siteName);
//        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
//        documentLibPage = uploadForm.uploadFile(fileForWorkflow.getCanonicalPath()).render();
//        DocumentDetailsPage documentDetailsPage = documentLibPage.selectFile(fileForWorkflow.getName()).render();
//        StartWorkFlowPage startWorkFlowPage = documentDetailsPage.selectStartWorkFlowPage().render();
//        newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW)).render();
//    }
//
//    @AfterClass
//    public void afterClass()
//    {
//        SiteUtil.deleteSite(drone, siteName);
//    }
//
//    /**
//     * This test is to assert and fill the cloud task page form.
//     *
//     * @throws Exception
//     */
//    @Test(groups = "Enterprise4.2")
//    public void startAdhocReview() throws Exception
//    {
//        List<String> reviewers = new ArrayList<String>();
//        reviewers.add(username);
//        WorkFlowFormDetails formDetails = new WorkFlowFormDetails(siteName, siteName, reviewers);
//        newWorkflowPage.startWorkflow(formDetails).render();
//        openSiteDocumentLibraryFromSearch(drone, siteName);
//        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(fileForWorkflow.getName());
//        assertTrue("Document should be part of workflow.", thisRow.isPartOfWorkflow());
//    }
//
//}

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

import static org.testng.Assert.assertTrue;

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

    @BeforeClass(groups = "Enterprise4.2")
    public void prepare() throws Exception
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
        assertTrue(returnedPage instanceof MyTasksPage, "Returned page should be instance of MyTaskPage");
    }

    @Test(groups = "Enterprise4.2")
    public void selectCompletedTasksTest()
    {
        SharePage returnedPage = myTasksPage.selectCompletedTasks();
        assertTrue(returnedPage instanceof MyTasksPage, "Returned page should be instance of MyTaskPage");
    }
}

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
package org.alfresco.po.share.task;

import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests the page object - <code>EditTaskPage</code>
 * 
 * @author Abhijeet Bharade
 * @since 1.6.2
 */
@Listeners(FailedTestListener.class)
public class EditTaskPageTest extends AbstractTaskTest
{
    private EditTaskPage pageUnderTest;
    private String otherUser = "";
    protected String modSiteName;
    private SharePage returnedPage;
    private SiteFinderPage siteFinder;
    private String testUserName;


    @BeforeClass(groups = "Enterprise4.2")
    public void setUp() throws Throwable
    {
        otherUser = "otherUser"+ System.currentTimeMillis();
        siteName = "AdhocReassign" + System.currentTimeMillis();
        modSiteName = "modSiteName" + System.currentTimeMillis();
        taskName = siteName;
        testUserName = "reviewer" + System.currentTimeMillis();
        createEnterpriseUser(testUserName);
        createEnterpriseUser(otherUser);
        createTask(testUserName, "password");
        pageUnderTest = myTasksPage.navigateToEditTaskPage(taskName, testUserName).render();
    }
    @AfterClass(groups = "Enterprise4.2")
    public void deleteSite()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test(groups = "Enterprise4.2")
    public void selectStatusDropDownTest()
    {
        assertTrue(TaskStatus.NOTYETSTARTED.equals(pageUnderTest.getSelectedStatusFromDropDown()),
                "The selected status should be not yet started");
        pageUnderTest.selectStatusDropDown(TaskStatus.COMPLETED).render();
        assertTrue(TaskStatus.COMPLETED.equals(pageUnderTest.getSelectedStatusFromDropDown()), "The selected status should be completed");
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectStatusDropDownTest")
    public void selectTaskDoneButtonTest()
    {
        pageUnderTest.enterComment("Task Completed");
        returnedPage = pageUnderTest.selectTaskDoneButton().render();
        assertTrue(returnedPage instanceof MyTasksPage, "The return page should be an instance of MyTasksPage page");
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectTaskDoneButtonTest")
    public void selectRejectButtonTest() throws Exception
    {
        SiteUtil.createSite(drone, modSiteName, "Moderated");
        SiteDashboardPage sitePage = drone.getCurrentPage().render();
        assertTrue(sitePage.isSiteTitle(modSiteName), "Site Dashboad page should be opened for - " + modSiteName);
        ShareUtil.logout(drone);

        DashBoardPage dash = loginAs(otherUser, "password");
        siteFinder = dash.getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite(modSiteName).render();
        siteFinder.joinSite(modSiteName).render();
        ShareUtil.logout(drone);
        // Rejecting the request to join
        dash = loginAs(testUserName, "password");
        myTasksPage = dash.getNav().selectMyTasks().render();
        pageUnderTest = myTasksPage.navigateToEditTaskPage(modSiteName).render();
        returnedPage = pageUnderTest.selectRejectButton().render();
        assertTrue(returnedPage instanceof MyTasksPage, "The return page should be an instance of MyTasksPage page");
        ShareUtil.logout(drone);
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectRejectButtonTest")
    public void selectApproveButtonTest() throws Exception
    {

        DashBoardPage dash = loginAs(otherUser, "password");
        siteFinder = dash.getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite(modSiteName).render();
        siteFinder.joinSite(modSiteName).render();
        ShareUtil.logout(drone);

        // Approving request to join.
        dash = loginAs(testUserName, "password");
        myTasksPage = dash.getNav().selectMyTasks();
        pageUnderTest = myTasksPage.navigateToEditTaskPage(modSiteName).render();
        returnedPage = pageUnderTest.selectApproveButton().render();
        assertTrue(returnedPage instanceof MyTasksPage, "The return page should be an instance of MyTasksPage page");
    }
}

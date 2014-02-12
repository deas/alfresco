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
 * Alfresco is distributed in the hope that it will be useful,3
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.task;

import static org.testng.Assert.assertTrue;

import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.task.SelectAssigneePage.AssigneeResultsRow;
import org.alfresco.po.share.workflow.NewWorkflowPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Purpose of this test is to test the <code>SelectAssigneePage</code>.
 * 
 * @author Abhijeet Bharade
 * @since v1.6.2
 */
public class SelectAssigneePageTest extends AbstractTaskTest
{
    private SelectAssigneePage pageUnderTest;
    NewWorkflowPage newWorkflowPage = null;

    List<AssigneeResultsRow> searchResultList;
    private String testUserName = "reviewer";
    private EditTaskPage editTaskPage;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass(groups = "Enterprise4.2")
    public void setUp() throws Exception
    {
        taskName = siteName = "AdhocReassign" + System.currentTimeMillis();
        testUserName += System.currentTimeMillis();
        createEnterpriseUser(testUserName);
        createTask(testUserName,"password");
        editTaskPage = myTasksPage.navigateToEditTaskPage(taskName).render();
        pageUnderTest = editTaskPage.selectReassignButton().render();
    }
    @Test(expectedExceptions = { UnsupportedOperationException.class }, groups = "Enterprise4.2")
    public void testSelectUserAsAssigneeWithNull()
    {
        pageUnderTest.selectUserAsAssignee(null, testUserName);
    }
    @Test(groups = "Enterprise4.2", dependsOnMethods = "testSearchPeopleWithUnknownUsers")
    public void testSearchPeopleWithLoggedInUser()
    {
        pageUnderTest = new EditTaskPage(drone).render().selectReassignButton().render();
        searchResultList = pageUnderTest.searchPeople(testUserName);
        assertTrue(searchResultList.size() > 0, "Users should be found.");

        SharePage returnPage = null;
        for (AssigneeResultsRow user : searchResultList)
        {
            if (user.getUsername().equals(testUserName))
            {
                returnPage = pageUnderTest.selectUserAsAssignee(user, testUserName).render();
                break;
            }
        }
        assertTrue(returnPage instanceof EditTaskPage, "Expected to return to Edit Task Page");
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "testSearchPeopleWithLoggedInUser")
    public void testSearchPeopleWithExistingUsers()
    {
        drone.refresh();
        editTaskPage = new EditTaskPage(drone).render();
        pageUnderTest = editTaskPage.selectReassignButton().render();
        searchResultList = pageUnderTest.searchPeople(testUserName);
        assertTrue(searchResultList.size() > 0, "Users should be found.");
        SharePage returnPage = null;
        for (AssigneeResultsRow user : searchResultList)
        {
            if (user.getUsername().contains(testUserName))
            {
                returnPage = pageUnderTest.selectUserAsAssignee(user, testUserName).render();
                break;
            }
        }
        assertTrue(returnPage instanceof EditTaskPage, "Expected to return to My Task Page");
    }
    @Test(groups = "Enterprise4.2")
    public void testSearchPeopleWithUnknownUsers()
    {
        searchResultList = pageUnderTest.searchPeople("sdfgsdfgssgdf");
        assertTrue(searchResultList.size() == 0, "No users should be found.");
        pageUnderTest.closePage();
    }

}

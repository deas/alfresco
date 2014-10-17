/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share;

import org.alfresco.po.share.util.FailedTestListener;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author maryia zaichanka
 */
@Listeners(FailedTestListener.class)
public class AddUserGroupPageTest extends AbstractTest
{
    private DashBoardPage dashBoard;
    private String groupName = "Add_Group";
    private String ADD_BUTTON = "td[class*='yui-dt-col-actions'] button";
    private String user = "user" + System.currentTimeMillis() + "@test.com";

    @BeforeClass(groups = "Enterprise-only")
    public void setup() throws Exception
    {
        dashBoard = loginAs("admin", "admin");
        UserSearchPage userPage = dashBoard.getNav().getUsersPage().render();
        NewUserPage newPage = userPage.selectNewUser().render();
        newPage.createEnterpriseUser(user, user, user, user, user);

    }

    @Test(groups = "Enterprise-only")
    public void testsSearchUser() throws Exception
    {
        GroupsPage groupsPage = dashBoard.getNav().getGroupsPage();
        groupsPage = groupsPage.clickBrowse().render();
        NewGroupPage newGroupPage = groupsPage.navigateToNewGroupPage().render();
        newGroupPage.createGroup(groupName, groupName, NewGroupPage.ActionButton.CREATE_GROUP).render();
        groupsPage = drone.getCurrentPage().render();
        groupsPage.selectGroup(groupName);
        AddUserGroupPage addUser = groupsPage.selectAddUser();
        addUser.searchUser(user).render(3000);
        Assert.assertTrue(drone.isElementDisplayed(By.cssSelector(ADD_BUTTON)));
        addUser.clickClose();

    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "testsSearchUser")
    public void testClickAddUserButton() throws Exception
    {
        GroupsPage groupsPage = dashBoard.getNav().getGroupsPage();
        groupsPage.clickBrowse();
        drone.getCurrentPage().render();
        groupsPage.selectGroup(groupName);
        AddUserGroupPage addUser = groupsPage.selectAddUser().render();
        addUser.searchUser(user);
        addUser.clickAddUserButton();
        groupsPage = drone.getCurrentPage().render();
        groupsPage.clickBrowse();
        groupsPage.selectGroup(groupName).render();
        drone.getCurrentPage().render();
        List<String> users = groupsPage.getUserList();
        Assert.assertTrue(users.contains(user + " " + user + " (" + user + ")"), "Added user isn't displayed in a group");

    }

}
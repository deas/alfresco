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
package org.alfresco.po.share;

import org.alfresco.po.share.NewGroupPage.ActionButton;
import org.alfresco.po.share.util.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
/**
 *
 * 
 * @author nshah
 * @since 1.6.1
 */
@Listeners(FailedTestListener.class)
public class GroupPageTest extends AbstractTest
{
    private DashBoardPage dashBoard;
    private String groupName = "testGrp"+System.currentTimeMillis();

    @BeforeClass(groups = "Enterprise-only")
    public void setup() throws Exception
    {
        dashBoard = loginAs(username, password);
    }

    @Test(groups = "Enterprise-only")
    public void testNewGroup() throws Exception
    {         
        GroupsPage page = dashBoard.getNav().getGroupsPage();
        page = page.navigateToAddAndEditGroups().render();
        NewGroupPage newGroupPage = page.navigateToNewGroupPage().render();
        page = newGroupPage.createGroup(groupName, groupName, ActionButton.CREATE_GROUP).render();
        Assert.assertTrue(page.getGroupList().contains(groupName), String.format("Group: %s can not be found", groupName));
       
    }
    
}

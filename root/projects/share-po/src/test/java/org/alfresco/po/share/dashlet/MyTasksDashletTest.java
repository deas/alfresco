/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share.dashlet;

import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.webdrone.exception.PageException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Integration test my activities dashlet page elements.
 * 
 * @author Michael Suzuki
 * @since 1.3
 */
public class MyTasksDashletTest extends AbstractTest
{
    private DashBoardPage dashBoard;
    
    @BeforeClass(groups={"alfresco-one"})
    public void prepare() throws Exception
    {
        dashBoard = loginAs(username, password).render();
    }
    
    @Test(groups={"alfresco-one"})
    public void instantiateMyTasksDashlet()
    {
        MyTasksDashlet dashlet = new MyTasksDashlet(drone);
        Assert.assertNotNull(dashlet);
    }
    
    /**
     * Gets empty collection when no tasks are visible.
     */
    @Test(dependsOnMethods="selectMyTasksDashlet", groups={"alfresco-one"})
    public void getTasksShouldBeEmpty()
    {
        MyTasksDashlet dashlet = new MyTasksDashlet(drone).render();
    	List<ShareLink> tasks = dashlet.getTasks();
        
        Assert.assertNotNull(tasks);
        Assert.assertTrue(tasks.isEmpty());
    }

    /**
     * Test process of accessing my documents
     * dashlet from the dash board view.
     * @throws Exception 
     */
    @Test(dependsOnMethods="selectFake", groups={"alfresco-one"})
    public void selectMyTasksDashlet() throws Exception
    {
    	MyTasksDashlet dashlet = dashBoard.getDashlet("tasks").render();
        final String title = dashlet.getDashletTitle();
        Assert.assertEquals("My Tasks",title);
    }
    
    @Test(dependsOnMethods="instantiateMyTasksDashlet",expectedExceptions = PageException.class, groups={"alfresco-one"})
    public void selectFake() throws Exception
    {
    	MyTasksDashlet dashlet = dashBoard.getDashlet("tasks").render();
        dashlet.selectTask("bla");
    }
    
    @Test(dependsOnMethods="getTasksShouldBeEmpty", groups={"Enterprise4.2"})
    public void selectStartWorkFlow() throws Exception
    {
        MyTasksDashlet dashlet = dashBoard.getDashlet("tasks").render();
        StartWorkFlowPage startWorkFlow = dashlet.selectStartWorkFlow().render();
        
        Assert.assertNotNull(startWorkFlow);
        Assert.assertTrue(startWorkFlow.getTitle().contains("Start Workflow"));
    }
}

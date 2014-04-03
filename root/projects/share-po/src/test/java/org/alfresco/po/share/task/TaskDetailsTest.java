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
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Purpose of this test is to test the <code>TaskDetails</code> Class.
 * 
 * @author Ranjith Manyam
 */
public class TaskDetailsTest
{

    @Test(groups="unit", enabled = true)
    public void testSetDueDateString()
    {
        TaskDetails td = new TaskDetails();
        td.setDue("(None)");
        Assert.assertEquals(td.getDue(), "(None)");
        td.setDue("06 February, 2014");
        Assert.assertEquals(td.getDue(), "06 February, 2014");
    }
}

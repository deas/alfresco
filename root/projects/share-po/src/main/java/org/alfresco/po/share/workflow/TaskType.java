package org.alfresco.po.share.workflow;
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

import org.openqa.selenium.By;

/**
 * This enum holda the task type details needed for workflow form  P
 *
 * @author Siva Kaliyappan
 * @since 1.6.2
 */
public enum TaskType
{

    SIMPLE_CLOUD_TASK(By.cssSelector("option[value='task']")),
    CLOUD_REVIEW_TASK(By.cssSelector("option[value='review']"));
    
    public By getSelector()
    {
        return selector;
    }

    By selector;

    TaskType(By selector)
    {
        this.selector = selector;
    }
}



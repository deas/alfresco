/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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

package org.alfresco.repo.publishing;

import org.alfresco.test_category.OwnJVMTestsCategory;
import org.junit.experimental.categories.Category;


/**
 * @author Nick Smith
 * @author Frederik Heremans
 * @since 4.0
 */
@Category(OwnJVMTestsCategory.class)
public class PublishWebContentJbpmTest extends PublishWebContentProcessTest
{
    private static final String DEF_NAME = "jbpm$publishWebContent";
    
    @Override
    protected String getWorkflowDefinitionName()
    {
        return DEF_NAME;
    }
}

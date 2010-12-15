/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.module.blogIntegration.ui;

import org.alfresco.module.blogIntegration.BlogIntegrationModel;
import org.alfresco.web.action.evaluator.BaseActionEvaluator;
import org.alfresco.web.bean.repository.Node;


/**
 * Remove blog details evaluator
 * 
 * @author Roy Wetherall
 */
public class RemoveBlogDetailsEvaluator extends BaseActionEvaluator implements BlogIntegrationModel
{
    /**
     * @see org.alfresco.web.action.ActionEvaluator#evaluate(org.alfresco.web.bean.repository.Node)
     */
    public boolean evaluate(Node node)
    {
        boolean result = false;
        if (node.hasAspect(ASPECT_BLOG_DETAILS) == true)
        {
            result = true;
        }
        return result;
    }
}

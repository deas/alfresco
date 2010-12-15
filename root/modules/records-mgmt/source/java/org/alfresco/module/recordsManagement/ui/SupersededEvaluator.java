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
package org.alfresco.module.recordsManagement.ui;

import org.alfresco.module.recordsManagement.RecordsManagementModel;
import org.alfresco.web.bean.repository.Node;


/**
 * @author Roy Wetherall
 */
public class SupersededEvaluator extends BaseEvaluator
{
    public boolean evaluate(Node node)
    {
        boolean result = false;
        if (node.hasAspect(RecordsManagementModel.ASPECT_RECORD) == true &&
            node.hasAspect(RecordsManagementModel.ASPECT_CUTOFF) == false &&
            node.hasAspect(RecordsManagementModel.ASPECT_HELD) == false &&
            node.hasAspect(RecordsManagementModel.ASPECT_SUPERSEDED) == false &&
            isRecordsManager() == true)
        {
            result = true;            
        }
        return result;
    }
}

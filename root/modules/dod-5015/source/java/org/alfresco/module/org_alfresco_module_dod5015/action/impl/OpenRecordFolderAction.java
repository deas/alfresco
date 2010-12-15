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
package org.alfresco.module.org_alfresco_module_dod5015.action.impl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_dod5015.action.RMActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * Action to re-open the records folder
 * 
 * @author Roy Wetherall
 */
public class OpenRecordFolderAction extends RMActionExecuterAbstractBase
{

    /**
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action,
     *      org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {
        // TODO check that the user in question has the correct permission to re-open a records folder

        if (this.recordsManagementService.isRecordFolder(actionedUponNodeRef) == true)
        {
            Boolean isClosed = (Boolean) this.nodeService.getProperty(actionedUponNodeRef, PROP_IS_CLOSED);
            if (Boolean.TRUE.equals(isClosed) == true)
            {
                this.nodeService.setProperty(actionedUponNodeRef, PROP_IS_CLOSED, false);
            }
        }
        else
        {
            throw new AlfrescoRuntimeException("Can not perform the open record folder action on a node that is not a record folder. (" + actionedUponNodeRef.toString() + ")");
        }
    }

    /**
     * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(java.util.List)
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public Set<QName> getProtectedProperties()
    {
        HashSet<QName> qnames = new HashSet<QName>();
        qnames.add(PROP_IS_CLOSED);
        return qnames;
    }

    @Override
    protected boolean isExecutableImpl(NodeRef filePlanComponent, Map<String, Serializable> parameters, boolean throwException)
    {
        if (this.recordsManagementService.isRecordFolder(filePlanComponent) == true)
        {
            return true;
        }
        else
        {
            if (throwException)
            {
                throw new AlfrescoRuntimeException("Can not perform the open record folder action on a node that is not a record folder. (" + filePlanComponent.toString() + ")");
            }
            else
            {
                return false;
            }
        }
    }

}

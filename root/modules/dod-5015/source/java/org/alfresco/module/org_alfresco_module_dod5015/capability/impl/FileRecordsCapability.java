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
package org.alfresco.module.org_alfresco_module_dod5015.capability.impl;

import net.sf.acegisecurity.vote.AccessDecisionVoter;

import org.alfresco.module.org_alfresco_module_dod5015.capability.RMPermissionModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AccessStatus;

/**
 * @author andyh
 *
 */
public class FileRecordsCapability extends AbstractCapability
{

    public FileRecordsCapability()
    {
        super();
    }

    @Override
    protected int hasPermissionImpl(NodeRef nodeRef)
    {
        return evaluate(nodeRef);
    }

    public int evaluate(NodeRef nodeRef)
    {
        if (isRm(nodeRef))
        {
            if (checkFilingUnfrozenUncutoffOpenUndeclared(nodeRef, false) == AccessDecisionVoter.ACCESS_GRANTED)
            {
                if (isRecord(nodeRef) || isFileable(nodeRef))
                {
                    if (voter.getPermissionService().hasPermission(nodeRef, RMPermissionModel.FILE_RECORDS) == AccessStatus.ALLOWED)
                    {
                        return AccessDecisionVoter.ACCESS_GRANTED;
                    }
                }
            }
            else if ((checkFilingUnfrozenUncutoff(nodeRef, false) == AccessDecisionVoter.ACCESS_GRANTED) && isClosed(nodeRef) && !isDeclared(nodeRef))
            {
                if (isRecord(nodeRef) || isFileable(nodeRef))
                {
                    if (voter.getPermissionService().hasPermission(getFilePlan(nodeRef), RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS) == AccessStatus.ALLOWED)
                    {
                        return AccessDecisionVoter.ACCESS_GRANTED;
                    }
                }
            }
            else if ((checkFilingUnfrozen(nodeRef, false) == AccessDecisionVoter.ACCESS_GRANTED) && isCutoff(nodeRef))
            {
                if (isRecord(nodeRef) || isFileable(nodeRef))
                {
                    if (voter.getPermissionService().hasPermission(getFilePlan(nodeRef), RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS) == AccessStatus.ALLOWED)
                    {
                        return AccessDecisionVoter.ACCESS_GRANTED;
                    }
                }
            }
            return AccessDecisionVoter.ACCESS_DENIED;

        }
        else
        {
            return AccessDecisionVoter.ACCESS_ABSTAIN;
        }
    }

    public String getName()
    {
        return RMPermissionModel.FILE_RECORDS;
    }
}

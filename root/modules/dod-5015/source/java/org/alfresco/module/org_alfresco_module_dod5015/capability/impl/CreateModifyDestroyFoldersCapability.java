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
package org.alfresco.module.org_alfresco_module_dod5015.capability.impl;

import net.sf.acegisecurity.vote.AccessDecisionVoter;

import org.alfresco.module.org_alfresco_module_dod5015.capability.RMPermissionModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.namespace.QName;

public class CreateModifyDestroyFoldersCapability extends AbstractCapability
{

    public CreateModifyDestroyFoldersCapability()
    {
        super();
    }

    @Override
    protected int hasPermissionImpl(NodeRef nodeRef)
    {
        return evaluate(nodeRef, null);
    }

    public int evaluate(NodeRef nodeRef, QName type)
    {
        if (isRm(nodeRef))
        {
            if (type == null)
            {
                if (checkFilingUnfrozenUncutoff(nodeRef, true) == AccessDecisionVoter.ACCESS_GRANTED)
                {
                    if (isRecordFolder(voter.getNodeService().getType(nodeRef)))
                    {
                        if (voter.getPermissionService().hasPermission(getFilePlan(nodeRef), RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS) == AccessStatus.ALLOWED)
                        {
                            return AccessDecisionVoter.ACCESS_GRANTED;
                        }
                    }
                    else if (isRecordCategory(voter.getNodeService().getType(nodeRef)))
                    {
                        if (voter.getPermissionService().hasPermission(getFilePlan(nodeRef), RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS) == AccessStatus.ALLOWED)
                        {
                            return AccessDecisionVoter.ACCESS_GRANTED;
                        }
                    }
                }

            }
            else
            {
                if (checkFilingUnfrozenUncutoff(nodeRef, true) == AccessDecisionVoter.ACCESS_GRANTED)
                {
                    if (isRecordCategory(voter.getNodeService().getType(nodeRef)))
                    {
                        if (isRecordFolder(type))
                        {
                            if (voter.getPermissionService().hasPermission(getFilePlan(nodeRef), RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS) == AccessStatus.ALLOWED)
                            {
                                return AccessDecisionVoter.ACCESS_GRANTED;
                            }
                        }
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
        return RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS;
    }

}
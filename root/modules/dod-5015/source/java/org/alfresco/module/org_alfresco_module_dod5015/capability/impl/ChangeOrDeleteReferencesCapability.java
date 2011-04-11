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

public class ChangeOrDeleteReferencesCapability extends AbstractCapability
{

    public ChangeOrDeleteReferencesCapability()
    {
        super();
    }

    @Override
    protected int hasPermissionImpl(NodeRef nodeRef)
    {
        // Best guess based on current nodeRef
        if (isRm(nodeRef))
        {
            if (checkFilingUnfrozen(nodeRef, false) == AccessDecisionVoter.ACCESS_GRANTED)
            {
                if (voter.getPermissionService().hasPermission(getFilePlan(nodeRef), RMPermissionModel.CHANGE_OR_DELETE_REFERENCES) == AccessStatus.ALLOWED)
                {
                    return AccessDecisionVoter.ACCESS_ABSTAIN;
                }
            }

            return AccessDecisionVoter.ACCESS_DENIED;
        }
        else
        {
            return AccessDecisionVoter.ACCESS_ABSTAIN;
        }
    }

    public int evaluate(NodeRef source, NodeRef target)
    {
        if (isRm(source))
        {
            if (target != null)
            {
                if (isRm(target))
                {
                    if (checkFilingUnfrozen(source, false) == AccessDecisionVoter.ACCESS_GRANTED)
                    {
                        if (checkFilingUnfrozen(target, false) == AccessDecisionVoter.ACCESS_GRANTED)
                        {
                            if ((voter.getPermissionService().hasPermission(getFilePlan(source), RMPermissionModel.CHANGE_OR_DELETE_REFERENCES) == AccessStatus.ALLOWED)
                                    && (voter.getPermissionService().hasPermission(getFilePlan(target), RMPermissionModel.CHANGE_OR_DELETE_REFERENCES) == AccessStatus.ALLOWED))
                            {
                                return AccessDecisionVoter.ACCESS_GRANTED;
                            }
                        }
                    }
                }
            }
            else
            {
                if (checkFilingUnfrozen(source, false) == AccessDecisionVoter.ACCESS_GRANTED)
                {
                    if ((voter.getPermissionService().hasPermission(getFilePlan(source), RMPermissionModel.CHANGE_OR_DELETE_REFERENCES) == AccessStatus.ALLOWED)
                            && (voter.getPermissionService().hasPermission(getFilePlan(target), RMPermissionModel.CHANGE_OR_DELETE_REFERENCES) == AccessStatus.ALLOWED))
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
        return RMPermissionModel.CHANGE_OR_DELETE_REFERENCES;
    }
}
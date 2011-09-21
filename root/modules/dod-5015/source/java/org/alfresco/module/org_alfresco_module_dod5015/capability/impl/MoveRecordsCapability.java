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
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.QName;

public class MoveRecordsCapability extends AbstractCapability
{

    public MoveRecordsCapability()
    {
        super();
    }

    @Override
    protected int hasPermissionImpl(NodeRef nodeRef)
    {
        // no way to know ...
        return AccessDecisionVoter.ACCESS_ABSTAIN;
    }
    
    public int evaluate(NodeRef movee, NodeRef destination)
    {
        int state = AccessDecisionVoter.ACCESS_ABSTAIN;

        if (isRm(destination))
        {
            state = checkRead(movee, true);
            if (state != AccessDecisionVoter.ACCESS_GRANTED)
            {
                return AccessDecisionVoter.ACCESS_DENIED;
            }

            if (isRm(movee))
            {
                state = voter.getDeleteCapability().evaluate(movee);
            }
            else
            {
                if (voter.getPermissionService().hasPermission(getFilePlan(movee), PermissionService.DELETE) == AccessStatus.ALLOWED)
                {
                    state = AccessDecisionVoter.ACCESS_GRANTED;
                }
            }

            if (state == AccessDecisionVoter.ACCESS_GRANTED)
            {
                QName type = voter.getNodeService().getType(movee);
                // now we know the node - we can abstain for certain types and aspects (eg, rm)
                state = voter.createCapability.evaluate(destination, movee, type, null);

                if (state == AccessDecisionVoter.ACCESS_GRANTED)
                {
                    if (isRm(movee))
                    {
                        if (voter.getPermissionService().hasPermission(getFilePlan(movee), RMPermissionModel.MOVE_RECORDS) == AccessStatus.ALLOWED)
                        {
                            return AccessDecisionVoter.ACCESS_GRANTED;
                        }
                    }
                    else
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
        return RMPermissionModel.MOVE_RECORDS;
    }
}
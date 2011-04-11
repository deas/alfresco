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
package org.alfresco.module.org_alfresco_module_dod5015.capability.group;

import net.sf.acegisecurity.vote.AccessDecisionVoter;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * @author andyh
 *
 */
public class DeleteCapability extends AbstractGroupCapability
{

    /* (non-Javadoc)
     * @see org.alfresco.module.org_alfresco_module_dod5015.capability.impl.AbstractCapability#hasPermissionImpl(org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected int hasPermissionImpl(NodeRef nodeRef)
    {
        return evaluate(nodeRef);
    }

    /* (non-Javadoc)
     * @see org.alfresco.module.org_alfresco_module_dod5015.capability.Capability#getName()
     */
    public String getName()
    {
       return "Delete";
    }
    
    public int evaluate(NodeRef deletee)
    {
        if (voter.getDestroyRecordsScheduledForDestructionCapability().evaluate(deletee) == AccessDecisionVoter.ACCESS_GRANTED)
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }
        if (voter.getDestroyRecordsCapability().evaluate(deletee) == AccessDecisionVoter.ACCESS_GRANTED)
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }
        if (voter.getDeleteRecordsCapability().evaluate(deletee) == AccessDecisionVoter.ACCESS_GRANTED)
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }
        if (voter.getCreateModifyDestroyFileplanMetadataCapability().evaluate(deletee) == AccessDecisionVoter.ACCESS_GRANTED)
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }
        if (voter.getCreateModifyDestroyFoldersCapability().evaluate(deletee, null) == AccessDecisionVoter.ACCESS_GRANTED)
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }
        return AccessDecisionVoter.ACCESS_DENIED;
    }

}

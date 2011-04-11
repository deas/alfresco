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
public class DeclareCapability extends AbstractGroupCapability
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
       return "Declare";
    }
    
    public int evaluate(NodeRef declaree)
    {
        if ((voter.getDeclareRecordsCapability().checkActionConditionsIfPresent(declaree) == AccessDecisionVoter.ACCESS_GRANTED) && voter.getDeclareRecordsCapability().evaluate(declaree) == AccessDecisionVoter.ACCESS_GRANTED)
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }
        if ((voter.getDeclareRecordsInClosedFoldersCapability().checkActionConditionsIfPresent(declaree) == AccessDecisionVoter.ACCESS_GRANTED) &&   voter.getDeclareRecordsInClosedFoldersCapability().evaluate(declaree) == AccessDecisionVoter.ACCESS_GRANTED)
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }
        return AccessDecisionVoter.ACCESS_DENIED;
    }

}

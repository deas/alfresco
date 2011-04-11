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

import java.io.Serializable;
import java.util.Map;

import net.sf.acegisecurity.vote.AccessDecisionVoter;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * @author andyh
 */
public class UpdatePropertiesCapability extends AbstractGroupCapability
{

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.module.org_alfresco_module_dod5015.capability.impl.AbstractCapability#hasPermissionImpl(org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected int hasPermissionImpl(NodeRef nodeRef)
    {
        return evaluate(nodeRef, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.module.org_alfresco_module_dod5015.capability.Capability#getName()
     */
    public String getName()
    {
        return "UpdateProperties";
    }

    public int evaluate(NodeRef nodeRef, Map<QName, Serializable> properties)
    {
        if ((properties != null) && (voter.includesProtectedPropertyChange(nodeRef, properties)))
        {
            return AccessDecisionVoter.ACCESS_DENIED;
        }        
        if (voter.getCreateModifyDestroyFoldersCapability().evaluate(nodeRef, null) == AccessDecisionVoter.ACCESS_GRANTED)
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }
        if (voter.getCreateModifyDestroyFileplanMetadataCapability().evaluate(nodeRef) == AccessDecisionVoter.ACCESS_GRANTED)
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }
        if (voter.getEditDeclaredRecordMetadataCapability().evaluate(nodeRef) == AccessDecisionVoter.ACCESS_GRANTED)
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }
        if (voter.getEditNonRecordMetadataCapability().evaluate(nodeRef) == AccessDecisionVoter.ACCESS_GRANTED)
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }
        if (voter.getEditRecordMetadataCapability().evaluate(nodeRef) == AccessDecisionVoter.ACCESS_GRANTED)
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }
        if (voter.getCreateModifyRecordsInCuttoffFoldersCapability().evaluate(nodeRef) == AccessDecisionVoter.ACCESS_GRANTED)
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }
        return AccessDecisionVoter.ACCESS_DENIED;
    }
}

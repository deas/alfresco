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
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AccessStatus;

public class EditRecordMetadataCapability extends AbstractCapability
{

    public EditRecordMetadataCapability()
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
                if (isRecord(nodeRef))
                {
                    if (voter.getPermissionService().hasPermission(getFilePlan(nodeRef), RMPermissionModel.EDIT_RECORD_METADATA) == AccessStatus.ALLOWED)
                    {
                        return AccessDecisionVoter.ACCESS_GRANTED;
                    }
                    
                    // Since we know this is undeclared if you are the owner then you should be able to 
                    // edit the records meta-data (otherwise how can it be declared by the user?)
                    if (voter.getOwnableService().hasOwner(nodeRef) == true)
                    {
                    	String user = AuthenticationUtil.getFullyAuthenticatedUser();
                    	if (user != null &&
                    	    voter.getOwnableService().getOwner(nodeRef).equals(user) == true)
                    	{
                    		return AccessDecisionVoter.ACCESS_GRANTED;
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
        return RMPermissionModel.EDIT_RECORD_METADATA;
    }

}
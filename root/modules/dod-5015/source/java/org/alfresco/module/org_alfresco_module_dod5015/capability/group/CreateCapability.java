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

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.capability.RMPermissionModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.namespace.QName;

public class CreateCapability extends AbstractGroupCapability
{

    public CreateCapability()
    {
        super();
    }

    @Override
    protected int hasPermissionImpl(NodeRef nodeRef)
    {
        return evaluate(nodeRef, null, null, null);
    }

    public int evaluate(NodeRef destination, NodeRef linkee, QName type, QName assocType)
    {
        if (linkee != null)
        {
            int state = checkRead(linkee, true);            
            if (state != AccessDecisionVoter.ACCESS_GRANTED)
            {
                return AccessDecisionVoter.ACCESS_DENIED;
            }
        }
        if (isRm(destination))
        {
            if ( ((assocType == null) || !assocType.equals(ContentModel.ASSOC_CONTAINS)))
            {
                if(linkee == null)
                {
                    if(isRecord(destination) && !isDeclared(destination))
                    {
                        if (voter.getPermissionService().hasPermission(destination, RMPermissionModel.FILE_RECORDS) == AccessStatus.ALLOWED)
                        {
                            return AccessDecisionVoter.ACCESS_GRANTED;
                        }
                    }   
                }
                else
                {
                    if(isRecord(linkee) && isRecord(destination) && !isDeclared(destination))
                    {
                        if (voter.getPermissionService().hasPermission(destination, RMPermissionModel.FILE_RECORDS) == AccessStatus.ALLOWED)
                        {
                            return AccessDecisionVoter.ACCESS_GRANTED;
                        }
                    }
                }
              
            }
            if (checkFilingUnfrozenUncutoffOpen(destination, false) == AccessDecisionVoter.ACCESS_GRANTED)
            {
                if (isRecordFolder(voter.getNodeService().getType(destination)))
                {
                    if (voter.getPermissionService().hasPermission(destination, RMPermissionModel.FILE_RECORDS) == AccessStatus.ALLOWED)
                    {
                        return AccessDecisionVoter.ACCESS_GRANTED;
                    }
                }
            }
            if ((checkFilingUnfrozenUncutoff(destination, false) == AccessDecisionVoter.ACCESS_GRANTED) && isClosed(destination))
            {
                if (isRecordFolder(voter.getNodeService().getType(destination)))
                {
                    if (voter.getPermissionService().hasPermission(getFilePlan(destination), RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS) == AccessStatus.ALLOWED)
                    {
                        return AccessDecisionVoter.ACCESS_GRANTED;
                    }
                }
            }
            if ((checkFilingUnfrozen(destination, false) == AccessDecisionVoter.ACCESS_GRANTED) && isCutoff(destination))
            {
                if (isRecordFolder(voter.getNodeService().getType(destination)))
                {
                    if (voter.getPermissionService().hasPermission(getFilePlan(destination), RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS) == AccessStatus.ALLOWED)
                    {
                        return AccessDecisionVoter.ACCESS_GRANTED;
                    }
                }
            }
        }
        if (voter.getCreateModifyDestroyFoldersCapability().evaluate(destination, type) == AccessDecisionVoter.ACCESS_GRANTED)
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }
        if (voter.getDeclareRecordsInClosedFoldersCapability().evaluate(destination) == AccessDecisionVoter.ACCESS_GRANTED)
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }
        if (voter.createModifyRecordsInCuttoffFoldersCapability.evaluate(destination) == AccessDecisionVoter.ACCESS_GRANTED)
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }
        if (voter.createModifyDestroyFileplanMetadataCapability.evaluate(destination) == AccessDecisionVoter.ACCESS_GRANTED)
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }
        if (voter.getChangeOrDeleteReferencesCapability().evaluate(destination, linkee) == AccessDecisionVoter.ACCESS_GRANTED)
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }
        return AccessDecisionVoter.ACCESS_DENIED;
    }

    public String getName()
    {
        return "Create";
    }

}
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sf.acegisecurity.vote.AccessDecisionVoter;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.DOD5015Model;
import org.alfresco.module.org_alfresco_module_dod5015.DispositionAction;
import org.alfresco.module.org_alfresco_module_dod5015.DispositionActionDefinition;
import org.alfresco.module.org_alfresco_module_dod5015.DispositionSchedule;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementAction;
import org.alfresco.module.org_alfresco_module_dod5015.capability.Capability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.RMEntryVoter;
import org.alfresco.module.org_alfresco_module_dod5015.capability.RMPermissionModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author andyh
 */
public abstract class AbstractCapability implements Capability
{
    private static Log logger = LogFactory.getLog(AbstractCapability.class);

    protected RMEntryVoter voter;

    protected List<RecordsManagementAction> actions = new ArrayList<RecordsManagementAction>(1);

    protected List<String> actionNames = new ArrayList<String>(1);

    public AbstractCapability()
    {
        super();
    }

    public void setVoter(RMEntryVoter voter)
    {
        this.voter = voter;
    }

    public void registerAction(RecordsManagementAction action)
    {
        this.actions.add(action);
        this.actionNames.add(action.getName());
        voter.addProtectedAspects(action.getProtectedAspects());
        voter.addProtectedProperties(action.getProtectedProperties());
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.capability.Capability#isGroupCapability()
     */
    public boolean isGroupCapability()
    {
        return false;
    }

    AccessStatus translate(int vote)
    {
        switch (vote)
        {
        case AccessDecisionVoter.ACCESS_ABSTAIN:
            return AccessStatus.UNDETERMINED;
        case AccessDecisionVoter.ACCESS_GRANTED:
            return AccessStatus.ALLOWED;
        case AccessDecisionVoter.ACCESS_DENIED:
            return AccessStatus.DENIED;
        default:
            return AccessStatus.UNDETERMINED;
        }
    }

    public int checkActionConditionsIfPresent(NodeRef nodeRef)
    {
        String prefix = "checkActionConditionsIfPresent" + getName();
        int result = getTransactionCache(prefix, nodeRef);
        if (result != NOSET_VALUE)
        {
            return result;
        }
        
        if (actions.size() > 0)
        {
            for (RecordsManagementAction action : actions)
            {
                if (action.isExecutable(nodeRef, null))
                {
                    return setTransactionCache(prefix, nodeRef, AccessDecisionVoter.ACCESS_GRANTED);
                }
            }
            return setTransactionCache(prefix, nodeRef, AccessDecisionVoter.ACCESS_DENIED);
        }
        else
        {
            return setTransactionCache(prefix, nodeRef, AccessDecisionVoter.ACCESS_GRANTED);
        }
    }

    public AccessStatus hasPermission(NodeRef nodeRef)
    {
        return translate(hasPermissionRaw(nodeRef));
    }
        
    public int hasPermissionRaw(NodeRef nodeRef)
    {
        String prefix = "hasPermissionRaw" + getName();
        int result = getTransactionCache(prefix, nodeRef);
        if (result != NOSET_VALUE)
        {
            return result;
        }
        
        if (checkRmRead(nodeRef) == AccessDecisionVoter.ACCESS_DENIED)
        {
            result = AccessDecisionVoter.ACCESS_DENIED;
        }
        else if (checkActionConditionsIfPresent(nodeRef) == AccessDecisionVoter.ACCESS_DENIED)
        {
            result = AccessDecisionVoter.ACCESS_DENIED;
        }
        else
        {
            result = hasPermissionImpl(nodeRef);
        }
        
        return setTransactionCache(prefix, nodeRef, result);
    }
    
    protected abstract int hasPermissionImpl(NodeRef nodeRef);

    public List<String> getActionNames()
    {
        return actionNames;
    }

    public List<RecordsManagementAction> getActions()
    {
        return actions;
    }

    public NodeRef getFilePlan(NodeRef nodeRef)
    {
        NodeRef result = null;        
        if (nodeRef != null)
        {
            result = voter.getRecordsManagementService().getRecordsManagementRoot(nodeRef);
        }
        return result;
    }
    
    public int checkFilingUnfrozen(NodeRef nodeRef, boolean checkChildren)
    {
        int status;
        status = checkFiling(nodeRef);
        if (status != AccessDecisionVoter.ACCESS_GRANTED)
        {
            return status;
        }
        return checkUnfrozen(nodeRef, checkChildren);

    }

    
    public int checkFilingUnfrozenUncutoff(NodeRef nodeRef, boolean checkChildren)
    {
        int status;
        status = checkFilingUnfrozen(nodeRef, checkChildren);
        if (status != AccessDecisionVoter.ACCESS_GRANTED)
        {
            return status;
        }
        return checkUncutoff(nodeRef);
    }
    
    public int checkFilingUnfrozenUncutoffOpen(NodeRef nodeRef, boolean checkChildren)
    {
        int status;
        status = checkFilingUnfrozenUncutoff(nodeRef, checkChildren);
        if (status != AccessDecisionVoter.ACCESS_GRANTED)
        {
            return status;
        }
        return checkOpen(nodeRef);
    }
    
    public int checkFilingUnfrozenUncutoffOpenUndeclared(NodeRef nodeRef, boolean checkChildren)
    {
        int status;
        status = checkFilingUnfrozenUncutoffOpen(nodeRef, checkChildren);
        if (status != AccessDecisionVoter.ACCESS_GRANTED)
        {
            return status;
        }
        return checkUndeclared(nodeRef);
    }
    
    public int checkFilingUnfrozenUncutoffUndeclared(NodeRef nodeRef, boolean checkChildren)
    {
        int status;
        status = checkFilingUnfrozenUncutoff(nodeRef, checkChildren);
        if (status != AccessDecisionVoter.ACCESS_GRANTED)
        {
            return status;
        }
        return checkUndeclared(nodeRef);
    }

    public int checkRead(NodeRef nodeRef, boolean allowDMRead)
    {
        if (voter.getNodeService().hasAspect(nodeRef, RecordsManagementModel.ASPECT_FILE_PLAN_COMPONENT))
        {
            return checkRmRead(nodeRef);
        }
        else
        {
            if (allowDMRead)
            {
                // Check DM read for copy etc
                // DM does not grant - it can only deny
                if (voter.getPermissionService().hasPermission(nodeRef, PermissionService.READ) == AccessStatus.DENIED)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("\t\tPermission is denied");
                        Thread.dumpStack();
                    }
                    return AccessDecisionVoter.ACCESS_DENIED;
                }
                else
                {
                    return AccessDecisionVoter.ACCESS_GRANTED;
                }
            }
            else
            {
                return AccessDecisionVoter.ACCESS_ABSTAIN;
            }
        }
    }    
    
    private int NOSET_VALUE = -100;
    
    private int setTransactionCache(String prefix, NodeRef nodeRef, int value)
    {
        String user = AuthenticationUtil.getRunAsUser();
        AlfrescoTransactionSupport.bindResource(prefix + nodeRef.toString() + user, Integer.valueOf(value));
        return value;        
    }
    
    private int getTransactionCache(String prefix, NodeRef nodeRef)
    {
        int result = NOSET_VALUE;
        String user = AuthenticationUtil.getRunAsUser();
        Integer value = (Integer)AlfrescoTransactionSupport.getResource(prefix + nodeRef.toString() + user);
        if (value != null)
        {
            result = value.intValue();
        }
        return result;
    }
    
    public int checkRmRead(NodeRef nodeRef)
    {           
        int result = getTransactionCache("checkRmRead", nodeRef);
        if (result != NOSET_VALUE)
        {
            return result;
        }
        
        // Get the file plan for the node
        NodeRef filePlan = getFilePlan(nodeRef);
        
        // Admin role
        if (voter.getPermissionService().hasPermission(filePlan, RMPermissionModel.ROLE_ADMINISTRATOR) == AccessStatus.ALLOWED)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("\t\tAdmin access");
                Thread.dumpStack();
            }
            return setTransactionCache("checkRmRead", nodeRef, AccessDecisionVoter.ACCESS_GRANTED);            
        }

        if (voter.getPermissionService().hasPermission(nodeRef, RMPermissionModel.READ_RECORDS) == AccessStatus.DENIED)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("\t\tPermission is denied");
                Thread.dumpStack();
            }
            return setTransactionCache("checkRmRead", nodeRef, AccessDecisionVoter.ACCESS_DENIED); 
        }

        if (voter.getPermissionService().hasPermission(filePlan, RMPermissionModel.VIEW_RECORDS) == AccessStatus.DENIED)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("\t\tPermission is denied");
                Thread.dumpStack();
            }
            return setTransactionCache("checkRmRead", nodeRef, AccessDecisionVoter.ACCESS_DENIED); 
        }

        if (voter.getCaveatConfigComponent().hasAccess(nodeRef))
        {
            return setTransactionCache("checkRmRead", nodeRef, AccessDecisionVoter.ACCESS_GRANTED); 
        }
        else
        {
            return setTransactionCache("checkRmRead", nodeRef, AccessDecisionVoter.ACCESS_DENIED); 
        }

    }

    public int checkRead(NodeRef nodeRef)
    {
        if (nodeRef != null)
        {
            // now we know the node - we can abstain for certain types and aspects (eg, rm)
            return checkRead(nodeRef, false);
        }

        return AccessDecisionVoter.ACCESS_ABSTAIN;
    }

    public int checkFiling(NodeRef nodeRef)
    {
        // A read check is not required
        if (voter.getNodeService().hasAspect(nodeRef, RecordsManagementModel.ASPECT_FILE_PLAN_COMPONENT))
        {
            if (voter.getPermissionService().hasPermission(getFilePlan(nodeRef), RMPermissionModel.ROLE_ADMINISTRATOR) == AccessStatus.ALLOWED)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("\t\tAdmin access");
                    Thread.dumpStack();
                }
                return AccessDecisionVoter.ACCESS_GRANTED;
            }

            if (logger.isDebugEnabled())
            {
                logger.debug("\t\tNode ref is not null");
            }

            // include records and content in the RM world as it may not yet be filed
            if (isRecord(nodeRef) || isFileable(nodeRef))
            {
                // Multifiling - if you have filing rights to any of the folders in which the record resides
                // then you have filing rights.
                for (ChildAssociationRef car : voter.getNodeService().getParentAssocs(nodeRef))
                {
                    if (car != null)
                    {
                        if (voter.getPermissionService().hasPermission(car.getParentRef(), RMPermissionModel.FILE_RECORDS) == AccessStatus.ALLOWED)
                        {
                            return AccessDecisionVoter.ACCESS_GRANTED;
                        }
                    }
                }
                return AccessDecisionVoter.ACCESS_DENIED;

            }
            else if (isRecordFolder(voter.getNodeService().getType(nodeRef)))
            {
                if (voter.getPermissionService().hasPermission(nodeRef, RMPermissionModel.FILE_RECORDS) == AccessStatus.DENIED)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("\t\tPermission is denied");

                        Thread.dumpStack();
                    }

                    return AccessDecisionVoter.ACCESS_DENIED;
                }

                return AccessDecisionVoter.ACCESS_GRANTED;

            }
            else if (isRecordCategory(voter.getNodeService().getType(nodeRef)))
            {
                if (voter.getPermissionService().hasPermission(nodeRef, RMPermissionModel.FILE_RECORDS) == AccessStatus.DENIED)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("\t\tPermission is denied");

                        Thread.dumpStack();
                    }

                    return AccessDecisionVoter.ACCESS_DENIED;
                }

                if (voter.getPermissionService().hasPermission(getFilePlan(nodeRef), RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS) == AccessStatus.DENIED)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("\t\tPermission is denied");

                        Thread.dumpStack();
                    }

                    return AccessDecisionVoter.ACCESS_DENIED;
                }

                return AccessDecisionVoter.ACCESS_GRANTED;

            }
            // else other file plan component
            else
            {
                if (voter.getPermissionService().hasPermission(nodeRef, RMPermissionModel.FILE_RECORDS) == AccessStatus.DENIED)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("\t\tPermission is denied");
                        Thread.dumpStack();
                    }
                    return AccessDecisionVoter.ACCESS_DENIED;
                }

                if (voter.getPermissionService().hasPermission(getFilePlan(nodeRef), RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA) == AccessStatus.DENIED)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("\t\tPermission is denied");
                        Thread.dumpStack();
                    }
                    return AccessDecisionVoter.ACCESS_DENIED;
                }

                return AccessDecisionVoter.ACCESS_GRANTED;

            }
        }

        return AccessDecisionVoter.ACCESS_ABSTAIN;

    }

    public int checkUnfrozen(NodeRef nodeRef, boolean checkChildren)
    {
        if (isRm(nodeRef) == true)
        {
            if (isFrozen(nodeRef, checkChildren) == true)
            {
                return AccessDecisionVoter.ACCESS_DENIED;
            }
            else
            {
                return AccessDecisionVoter.ACCESS_GRANTED;
            }
        }
        return AccessDecisionVoter.ACCESS_ABSTAIN;
    }

    public int checkUncutoff(NodeRef nodeRef)
    {
        if (isRm(nodeRef))
        {
            if (isCutoff(nodeRef))
            {
                return AccessDecisionVoter.ACCESS_DENIED;
            }
            else
            {
                return AccessDecisionVoter.ACCESS_GRANTED;
            }
        }
        return AccessDecisionVoter.ACCESS_ABSTAIN;
    }
    
    public int checkUntransfered(NodeRef nodeRef)
    {
        int result = AccessDecisionVoter.ACCESS_ABSTAIN;
        if (isRm(nodeRef) == true)
        {
            if (isTransfered(nodeRef) == true)
            {
                result = AccessDecisionVoter.ACCESS_DENIED;
            }
            else
            {
                result = AccessDecisionVoter.ACCESS_GRANTED;
            }
        }
        return result;
    }
    
    public int checkUndestroyed(NodeRef nodeRef)
    {
        int result = AccessDecisionVoter.ACCESS_ABSTAIN;
        if (isRm(nodeRef) == true)
        {
            if (isDestroyed(nodeRef) == true)
            {
                result = AccessDecisionVoter.ACCESS_DENIED;
            }
            else
            {
                result = AccessDecisionVoter.ACCESS_GRANTED;
            }
        }
        return result;
    }

    public int checkOpen(NodeRef nodeRef)
    {
        if (isRm(nodeRef))
        {
            if (isClosed(nodeRef))
            {
                return AccessDecisionVoter.ACCESS_DENIED;
            }
            else
            {
                return AccessDecisionVoter.ACCESS_GRANTED;
            }
        }
        return AccessDecisionVoter.ACCESS_ABSTAIN;
    }

    public int checkUndeclared(NodeRef nodeRef)
    {
        if (isRm(nodeRef))
        {
            if (isDeclared(nodeRef))
            {
                return AccessDecisionVoter.ACCESS_DENIED;
            }
            else
            {
                return AccessDecisionVoter.ACCESS_GRANTED;
            }
        }
        return AccessDecisionVoter.ACCESS_ABSTAIN;
    }

    public int checkDelete(NodeRef nodeRef)
    {
        if (voter.getNodeService().hasAspect(nodeRef, RecordsManagementModel.ASPECT_FILE_PLAN_COMPONENT))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("\t\tNode ref is not null");
            }

            if (isRecord(nodeRef))
            {

                // We can delete anything

                if (voter.getPermissionService().hasPermission(getFilePlan(nodeRef), RMPermissionModel.DELETE_RECORDS) == AccessStatus.ALLOWED)
                {
                    return AccessDecisionVoter.ACCESS_GRANTED;
                }

                DispositionSchedule dispositionSchedule = voter.getRecordsManagementService().getDispositionSchedule(nodeRef);
                for (DispositionActionDefinition dispositionActionDefinition : dispositionSchedule.getDispositionActionDefinitions())
                {
                    if (dispositionActionDefinition.getName().equals("destroy"))
                    {
                        if (voter.getPermissionService().hasPermission(getFilePlan(nodeRef), RMPermissionModel.DESTROY_RECORDS) == AccessStatus.ALLOWED)
                        {
                            return AccessDecisionVoter.ACCESS_GRANTED;
                        }
                    }
                }

                // The record is all set up for destruction
                DispositionAction nextDispositionAction = voter.getRecordsManagementService().getNextDispositionAction(nodeRef);
                if (nextDispositionAction != null)
                {
                    DispositionActionDefinition def = nextDispositionAction.getDispositionActionDefinition();
                    if (def != null && def.getName().equals("destroy"))
                    {
                        if (voter.getRecordsManagementService().isNextDispositionActionEligible(nodeRef))
                        {
                            if (voter.getPermissionService().hasPermission(getFilePlan(nodeRef), RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION) == AccessStatus.ALLOWED)
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
                if (voter.getPermissionService().hasPermission(getFilePlan(nodeRef), RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA) == AccessStatus.DENIED)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("\t\tPermission is denied");
                        Thread.dumpStack();
                    }
                    return AccessDecisionVoter.ACCESS_DENIED;
                }
                else
                {
                    return AccessDecisionVoter.ACCESS_GRANTED;
                }
            }

        }

        return AccessDecisionVoter.ACCESS_ABSTAIN;
    }

    public boolean isRecord(NodeRef nodeRef)
    {
        return voter.getNodeService().hasAspect(nodeRef, RecordsManagementModel.ASPECT_RECORD);
    }

    public boolean isFileable(NodeRef nodeRef)
    {
        QName type = voter.getNodeService().getType(nodeRef);
        return voter.getDictionaryService().isSubClass(type, ContentModel.TYPE_CONTENT);
    }

    public boolean isVitalRecord(NodeRef nodeRef)
    {
        return isRecord(nodeRef) && voter.getNodeService().hasAspect(nodeRef, RecordsManagementModel.ASPECT_VITAL_RECORD);
    }
    
    public boolean isVitalRecordFolder(NodeRef nodeRef)
    {
        boolean result = false;
        QName type = voter.getNodeService().getType(nodeRef);
        if (isRecordFolder(type) == true)
        {
            Boolean value = (Boolean)voter.getNodeService().getProperty(nodeRef, RecordsManagementModel.PROP_VITAL_RECORD_INDICATOR);
            if (value != null)
            {
                result = value.booleanValue();
            }
        }
        return result;
    }

    public boolean isRecordFolder(QName type)
    {
        return voter.getDictionaryService().isSubClass(type, RecordsManagementModel.TYPE_RECORD_FOLDER);
    }

    public boolean isRecordCategory(QName type)
    {
        return voter.getDictionaryService().isSubClass(type, DOD5015Model.TYPE_RECORD_CATEGORY);
    }

    public boolean isCutoff(NodeRef nodeRef)
    {
        return voter.getNodeService().hasAspect(nodeRef, RecordsManagementModel.ASPECT_CUT_OFF);
    }
    
    public boolean isTransfered(NodeRef nodeRef)
    {
        return voter.getNodeService().hasAspect(nodeRef, RecordsManagementModel.ASPECT_TRANSFERRED);
    }
    
    public boolean isDestroyed(NodeRef nodeRef)
    {
        return voter.getNodeService().hasAspect(nodeRef, DOD5015Model.ASPECT_GHOSTED);
    }

    public boolean isClosed(NodeRef nodeRef)
    {
        if (isRecordFolder(voter.getNodeService().getType(nodeRef)))
        {
            Serializable serializableValue = voter.getNodeService().getProperty(nodeRef, RecordsManagementModel.PROP_IS_CLOSED);
            if (serializableValue == null)
            {
                return false;
            }
            Boolean isClosed = DefaultTypeConverter.INSTANCE.convert(Boolean.class, serializableValue);
            return isClosed;
        }
        else if (isRecord(nodeRef))
        {
            for (ChildAssociationRef car : voter.getNodeService().getParentAssocs(nodeRef))
            {
                Serializable serializableValue = voter.getNodeService().getProperty(car.getParentRef(), RecordsManagementModel.PROP_IS_CLOSED);
                if (serializableValue == null)
                {
                    return false;
                }
                Boolean isClosed = DefaultTypeConverter.INSTANCE.convert(Boolean.class, serializableValue);
                if (!isClosed)
                {
                    return false;
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isRm(NodeRef nodeRef)
    {
        return voter.getNodeService().hasAspect(nodeRef, RecordsManagementModel.ASPECT_FILE_PLAN_COMPONENT);
    }
    
    public boolean isFrozen(NodeRef nodeRef, boolean checkChildren)
    {
        boolean result = voter.getNodeService().hasAspect(nodeRef, RecordsManagementModel.ASPECT_FROZEN);
        if (checkChildren == true &&
            result == false && 
            isRecordFolder(voter.getNodeService().getType(nodeRef)) == true)
        {
            // Query for any children that are frozen
            String query = "+PARENT:\"" + nodeRef.toString() + "\" +ASPECT:\"{http://www.alfresco.org/model/recordsmanagement/1.0}frozen\"";
            SearchService searchService = voter.getSearchService();
            
            // Create the search parameters
            SearchParameters searchParameters = new SearchParameters();
            searchParameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
            searchParameters.setQuery(query);
            searchParameters.setLanguage(SearchService.LANGUAGE_LUCENE);
            
            // Execute search
            ResultSet resultSet = searchService.query(searchParameters);
            
            // If there are any results then there must be a frozen child so return true
            if (resultSet.length() != 0)
            {
                result = true;
            }            
        }
        return result;
    }

    public boolean isDeclared(NodeRef nodeRef)
    {
        return voter.getNodeService().hasAspect(nodeRef, RecordsManagementModel.ASPECT_DECLARED_RECORD);
    }

    public boolean isScheduledForCutoff(NodeRef nodeRef)
    {
        DispositionSchedule dispositionSchedule = voter.getRecordsManagementService().getDispositionSchedule(nodeRef);
        if (dispositionSchedule == null)
        {
            return true;
        }
        for (DispositionActionDefinition dispositionActionDefinition : dispositionSchedule.getDispositionActionDefinitions())
        {
            if (dispositionActionDefinition.getName().equals("cutoff"))
            {
                if (voter.getRecordsManagementService().isNextDispositionActionEligible(nodeRef))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isScheduledForDestruction(NodeRef nodeRef)
    {
        boolean result = false;
        
        // The record is all set up for destruction
        DispositionAction nextDispositionAction = voter.getRecordsManagementService().getNextDispositionAction(nodeRef);
        if (nextDispositionAction != null)
        {
            // Get the disposition actions name
            String actionName = nextDispositionAction.getName();            
            if (actionName.equals("destroy") == true &&
                voter.getRecordsManagementService().isNextDispositionActionEligible(nodeRef) == true)
            {
                result = true;                
            }
        }
        
        return result;
    }

    public boolean mayBeScheduledForDestruction(NodeRef nodeRef)
    {
        DispositionSchedule dispositionSchedule = voter.getRecordsManagementService().getDispositionSchedule(nodeRef);
        if (dispositionSchedule == null)
        {
            // There is no disposition schedule so can't be scheduled for destruction
            return false;
        }
        
        boolean isRecordLevelDisposition = dispositionSchedule.isRecordLevelDisposition();
        if (isRecord(nodeRef) && isRecordLevelDisposition == false)
        {
            return false;
        }
        if (isRecordFolder(voter.getNodeService().getType(nodeRef)) && isRecordLevelDisposition == true)
        {
            return false;
        }
        for (DispositionActionDefinition dispositionActionDefinition : dispositionSchedule.getDispositionActionDefinitions())
        {
            if (dispositionActionDefinition.getName().equals("destroy"))
            {
                return true;
            }
        }
        return false;
    }

    public boolean hasDispositionSchedule(NodeRef nodeRef)
    {
        DispositionSchedule dispositionSchedule = voter.getRecordsManagementService().getDispositionSchedule(nodeRef);
        return dispositionSchedule != null;
    }

    public boolean isRecordLevelDisposition(NodeRef nodeRef)
    {
        DispositionSchedule dispositionSchedule = voter.getRecordsManagementService().getDispositionSchedule(nodeRef);
        return dispositionSchedule.isRecordLevelDisposition();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AbstractCapability other = (AbstractCapability) obj;
        if (getName() == null)
        {
            if (other.getName() != null)
                return false;
        }
        else if (!getName().equals(other.getName()))
            return false;
        return true;
    }

}

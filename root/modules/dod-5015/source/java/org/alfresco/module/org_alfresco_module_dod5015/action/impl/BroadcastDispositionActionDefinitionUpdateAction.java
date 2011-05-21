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
package org.alfresco.module.org_alfresco_module_dod5015.action.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.DispositionAction;
import org.alfresco.module.org_alfresco_module_dod5015.DispositionSchedule;
import org.alfresco.module.org_alfresco_module_dod5015.EventCompletionDetails;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.action.RMActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.Period;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Action to implement the consequences of a change to the value of the DispositionActionDefinition
 * properties. When these properties are changed on a disposition schedule, then any associated
 * disposition actions may need to be updated as a consequence.
 * 
 * @author Neil McErlean
 */
public class BroadcastDispositionActionDefinitionUpdateAction extends RMActionExecuterAbstractBase
{
    /** Logger */
    private static Log logger = LogFactory.getLog(BroadcastDispositionActionDefinitionUpdateAction.class);
    
    public static final String NAME = "broadcastDispositionActionDefinitionUpdate";
    public static final String CHANGED_PROPERTIES = "changedProperties";

    /**
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action,
     *      org.alfresco.service.cmr.repository.NodeRef)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {
        if (RecordsManagementModel.TYPE_DISPOSITION_ACTION_DEFINITION.equals(nodeService.getType(actionedUponNodeRef)) == false)
        {
            return;
        }
        
        List<QName> changedProps = (List<QName>)action.getParameterValue(CHANGED_PROPERTIES);

        // Navigate up the containment hierarchy to get the record category grandparent and schedule.
        NodeRef dispositionScheduleNode = nodeService.getPrimaryParent(actionedUponNodeRef).getParentRef();
        NodeRef recordCategoryNode = nodeService.getPrimaryParent(dispositionScheduleNode).getParentRef();
        DispositionSchedule dispositionSchedule = recordsManagementService.getDispositionSchedule(recordCategoryNode);
        boolean isRecordLevelDisposition = dispositionSchedule.isRecordLevelDisposition();
        
        List<NodeRef> recordFolders = getRecordFoldersForCategory(recordCategoryNode);
        for (NodeRef recordFolder : recordFolders)
        {
            if (isRecordLevelDisposition == false)
            {
                if (this.nodeService.hasAspect(recordFolder, ASPECT_DISPOSITION_LIFECYCLE))
                {
                    // disposition lifecycle already exists for node so process changes
                    processActionDefinitionChanges(actionedUponNodeRef, changedProps, recordFolder);
                }
                else
                {
                    // disposition lifecycle does not exist on the node so setup disposition
                    updateNextDispositionAction(recordFolder);
                }
            }
            else
            {
                List<NodeRef> records = getRecordsForFolder(recordFolder);
                for (NodeRef nextRecord : records)
                {
                    if (this.nodeService.hasAspect(nextRecord, ASPECT_DISPOSITION_LIFECYCLE))
                    {
                        // disposition lifecycle already exists for node so process changes
                        processActionDefinitionChanges(actionedUponNodeRef, changedProps, nextRecord);
                    }
                    else
                    {
                        // disposition lifecycle does not exist on the node so setup disposition
                        updateNextDispositionAction(nextRecord);
                    }
                }
            }
        }
    }

    /**
     * Processes all the changes applied to the given disposition
     * action definition node for the given record or folder node.
     * 
     * @param dispositionActionDef The disposition action definition node
     * @param changedProps The set of properties changed on the action definition
     * @param recordOrFolder The record or folder the changes potentially need to be applied to
     */
    private void processActionDefinitionChanges(NodeRef dispositionActionDef, List<QName> changedProps, NodeRef recordOrFolder)
    {
        // check that the step being edited is the current step for the folder,
        // if not, the change has no effect on the current step so ignore
        DispositionAction nextAction = recordsManagementService.getNextDispositionAction(recordOrFolder);
        if (doesChangedStepAffectNextAction(dispositionActionDef, nextAction))
        {
            // the change does effect the nextAction for this node
            // so go ahead and determine what needs updating
            if (changedProps.contains(PROP_DISPOSITION_PERIOD))
            {
                persistPeriodChanges(dispositionActionDef, nextAction);
            }
            
            if (changedProps.contains(PROP_DISPOSITION_EVENT) || changedProps.contains(PROP_DISPOSITION_EVENT_COMBINATION))
            {
                persistEventChanges(dispositionActionDef, nextAction);
            }
            
            if (changedProps.contains(PROP_DISPOSITION_ACTION_NAME))
            {
                String action = (String)nodeService.getProperty(dispositionActionDef, PROP_DISPOSITION_ACTION_NAME);
                nodeService.setProperty(nextAction.getNodeRef(), PROP_DISPOSITION_ACTION, action);
            }
        }
    }
    
    /**
     * Determines whether the disposition action definition (step) being
     * updated has any effect on the given next action
     *  
     * @param dispositionActionDef The disposition action definition node
     * @param nextAction The next disposition action 
     * @return true if the step change affects the next action
     */
    private boolean doesChangedStepAffectNextAction(NodeRef dispositionActionDef, 
                DispositionAction nextAction)
    {
        boolean affectsNextAction = false;
        
        if (dispositionActionDef != null && nextAction != null)
        {
            // check whether the id of the action definition node being changed
            // is the same as the id of the next action
            String nextActionId = nextAction.getId();
            if (dispositionActionDef.getId().equals(nextActionId))
            {
                affectsNextAction = true;
            }
        }
        
        return affectsNextAction;
    }
    
    /**
     * Persists any changes made to the period on the given disposition action
     * definition on the given next action.
     *
     * @param dispositionActionDef The disposition action definition node
     * @param nextAction The next disposition action
     */
    private void persistPeriodChanges(NodeRef dispositionActionDef, DispositionAction nextAction)
    {
        Date newAsOfDate = null;
        Period dispositionPeriod = (Period)nodeService.getProperty(dispositionActionDef, PROP_DISPOSITION_PERIOD);
        
        if (dispositionPeriod != null)
        {
            // calculate the new as of date as we have been provided a new period
            Date now = new Date();
            newAsOfDate = dispositionPeriod.getNextDate(now);
        }
        
        if (logger.isDebugEnabled())
        {
            logger.debug("Set disposition as of date for next action '" + nextAction.getName() + 
                        "' (" + nextAction.getNodeRef() + ") to: " + newAsOfDate);
        }
        
        this.nodeService.setProperty(nextAction.getNodeRef(), PROP_DISPOSITION_AS_OF, newAsOfDate);
    }
    
    /**
     * Persists any changes made to the events on the given disposition action
     * definition on the given next action.
     *
     * @param dispositionActionDef The disposition action definition node
     * @param nextAction The next disposition action
     */
    @SuppressWarnings("unchecked")
    private void persistEventChanges(NodeRef dispositionActionDef, DispositionAction nextAction)
    {
        // go through the current events on the next action and remove any that are not present any more
        List<String> stepEvents = (List<String>)nodeService.getProperty(dispositionActionDef, PROP_DISPOSITION_EVENT);
        List<EventCompletionDetails> eventsList = nextAction.getEventCompletionDetails();
        List<String> nextActionEvents = new ArrayList<String>(eventsList.size());
        for (EventCompletionDetails event : eventsList)
        {
            // take note of the event names present on the next action
            String eventName = event.getEventName();
            nextActionEvents.add(eventName);
            
            // if the event has been removed delete from next action
            if (stepEvents != null && stepEvents.contains(event.getEventName()) == false)
            {
                // remove the child association representing the event
                nodeService.removeChild(nextAction.getNodeRef(), event.getNodeRef());
                
                if (logger.isDebugEnabled())
                    logger.debug("Removed '" + eventName + "' from next action '" + nextAction.getName() + 
                                "' (" + nextAction.getNodeRef() + ")");
            }
        }
        
        // go through the disposition action definition step events and add any new ones
        if (stepEvents != null)
        {
	        for (String eventName : stepEvents)
	        {
	            if (!nextActionEvents.contains(eventName))
	            {
	                createEvent(recordsManagementEventService.getEvent(eventName), nextAction.getNodeRef());
	                
	                if (logger.isDebugEnabled())
	                {
	                    logger.debug("Added '" + eventName + "' to next action '" + nextAction.getName() + 
	                                "' (" + nextAction.getNodeRef() + ")");
	                }
	            }
	        }
        }
        
        // finally since events may have changed re-calculate the events eligible flag
        boolean eligible = updateEventEligible(nextAction);
        
        if (logger.isDebugEnabled())
        {
            logger.debug("Set events eligible flag to '" + eligible + "' for next action '" + nextAction.getName() + 
                        "' (" + nextAction.getNodeRef() + ")");
        }
    }
    
    /**
     * This method finds all the children contained under the specified recordCategoryNode
     * which are record folders.
     * 
     * @param recordCategoryNode
     * @return List of NodeRefs representing record folders in the given record category
     */
    private List<NodeRef> getRecordFoldersForCategory(NodeRef recordCategoryNode)
    {
        List<NodeRef> result = new ArrayList<NodeRef>();
        // This recordCategory could contain 0..n RecordFolder children.
        List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(recordCategoryNode, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
        for (ChildAssociationRef nextAssoc : childAssocs)
        {
            NodeRef nextChild = nextAssoc.getChildRef();
            if (recordsManagementService.isRecordFolder(nextChild))
            {
                result.add(nextChild);
            }
        }
        return result;
    }

    /**
     * This method finds all the children contained under the specified recordFolderNode
     * which are records.
     * 
     * @param recordFolderNode The record folder node to search
     * @return List of NodeRefs representing records in the given record folder
     */
    private List<NodeRef> getRecordsForFolder(NodeRef recordFolderNode)
    {
        List<NodeRef> result = new ArrayList<NodeRef>();
        // This recordFolder could contain 0..n Record children.
        List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(recordFolderNode, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
        for (ChildAssociationRef nextAssoc : childAssocs)
        {
            NodeRef nextChild = nextAssoc.getChildRef();
            if (recordsManagementService.isRecord(nextChild))
            {
                result.add(nextChild);
            }
        }
        return result;
    }
    
    /**
     * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(java.util.List)
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {
        // Intentionally empty
    }

    @Override
    public boolean isExecutableImpl(NodeRef filePlanComponent, Map<String, Serializable> parameters, boolean throwException)
    {
        return true;
    }

    @Override
    public Set<QName> getProtectedProperties()
    {
        HashSet<QName> qnames = new HashSet<QName>();
        qnames.add(PROP_DISPOSITION_AS_OF);
        qnames.add(PROP_DISPOSITION_EVENT);
        qnames.add(PROP_DISPOSITION_EVENT_COMBINATION);
        qnames.add(PROP_DISPOSITION_EVENTS_ELIGIBLE);
        return qnames;
    }

    @Override
    public Set<QName> getProtectedAspects()
    {
        return Collections.emptySet();
    }

}

/**
 * 
 */
package org.alfresco.module.org_alfresco_module_dod5015.disposition;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * Disposition service
 * 
 * @author Roy Wetherall
 */
public interface DispositionService
{
    /** ========= Disposition Schedule Methods ========= */
    
    /**
     * Get the disposition schedule for a given record management node.  Traverses the hierarchy to 
     * find the first disposition schedule in the primary hierarchy.
     * 
     * @param nodeRef   node reference to rm container, record folder or record
     * @return {@link DispositionSchedule}  disposition schedule
     */
    DispositionSchedule getDispositionSchedule(NodeRef nodeRef);
    
    // Gets all the disposition schedules, not just the first in the primary parent path.
    // TODO List<DispositionSchedule> getAllDispositionSchedules(NodeRef nodeRef);
    
    /**
     * Get the disposition schedule directly associated with the node specified.  Returns
     * null if none.
     * 
     * @param nodeRef   node reference
     * @return {@link DispositionSchedule}  disposition schedule directly associated with the node reference, null if none
     */
    DispositionSchedule getAssociatedDispositionSchedule(NodeRef nodeRef);
    
    /**
     * Gets the records management container that is directly associated with the disposition schedule.
     * 
     * @param dispositionSchedule   disposition schedule
     * @return {@link NodeRef}  node reference of the associated container
     */
    NodeRef getAssociatedRecordsManagementContainer(DispositionSchedule dispositionSchedule);
    
    /**
     * Indicates whether a disposition schedule has any disposable items under its management
     * 
     * @param dispositionSchdule	disposition schedule
     * @return boolean	true if there are disposable items being managed by, false otherwise
     */
    boolean hasDisposableItems(DispositionSchedule dispositionSchdule);
    
    /**
     * Gets a list of all the disposable items (records, record folders) that are under the control of 
     * the disposition schedule.
     * 
     * @param dispositionSchedule   disposition schedule
     * @return {@link List}<{@link NodeRef}>    list of disposable items
     */
    List<NodeRef> getDisposableItems(DispositionSchedule dispositionSchedule);
    
    /**
     * Creates a disposition schedule on the given rm container node.
     * 
     * @param nodeRef
     * @param props
     * @return {@link DispositionSchedule}
     */
    DispositionSchedule createDispositionSchedule(NodeRef nodeRef, Map<QName, Serializable> props);
    
    // TODO DispositionSchedule updateDispositionSchedule(DispositionScedule, Map<QName, Serializable> props)

    // TODO void removeDispositionSchedule(NodeRef nodeRef); - can only remove if no disposition items
    
    /** ========= Disposition Action Definition Methods ========= */
    
    /**
     * Adds a new disposition action definition to the given disposition schedule.
     * 
     * @param schedule The DispositionSchedule to add to
     * @param actionDefinitionParams Map of parameters to use to create the action definition
     */
    DispositionActionDefinition addDispositionActionDefinition(
                DispositionSchedule schedule, 
                Map<QName, Serializable> actionDefinitionParams);
    
    /**
     * Removes the given disposition action definition from the given disposition
     * schedule.
     * 
     * @param schedule The DispositionSchedule to remove from
     * @param actionDefinition The DispositionActionDefinition to remove
     */
    void removeDispositionActionDefinition(
                DispositionSchedule schedule, 
                DispositionActionDefinition actionDefinition);
    
    /**
     * Updates the given disposition action definition belonging to the given disposition
     * schedule.
     * 
     * @param actionDefinition The DispositionActionDefinition to update
     * @param actionDefinitionParams Map of parameters to use to update the action definition
     * @return The updated DispositionActionDefinition
     */
    DispositionActionDefinition updateDispositionActionDefinition(
                DispositionActionDefinition actionDefinition,
                Map<QName, Serializable> actionDefinitionParams);
    
    
    /**
     * TODO MOVE THIS FROM THIS API
     * 
     * @param nodeRef
     * @return
     */     
    boolean isNextDispositionActionEligible(NodeRef nodeRef);
  
    /** ========= Disposition Action Methods ========= */
    
    /**
     * Gets the next disposition action for a given node
     *  
     * @param nodeRef
     * @return
     */
    DispositionAction getNextDispositionAction(NodeRef nodeRef);
     
    
    /** ========= Disposition Action History Methods ========= */
    
    /**
     * Gets a list of all the completed disposition action in the order they occured.
     * 
     * @param nodeRef                       record/record folder 
     * @return List<DispositionAction>      list of completed disposition actions
     */
    List<DispositionAction> getCompletedDispositionActions(NodeRef nodeRef);
    
    /**
     * Helper method to get the last completed disposition action.  Returns null 
     * if there is none.
     * 
     * @param nodeRef               record/record folder
     * @return DispositionAction    last completed disposition action, null if none
     */
    DispositionAction getLastCompletedDispostionAction(NodeRef nodeRef);
    
    /** =========  ========= */
    
    /**
     * Returns the list of disposition period properties
     * 
     * @return list of disposition period properties
     */
    List<QName> getDispositionPeriodProperties();

}

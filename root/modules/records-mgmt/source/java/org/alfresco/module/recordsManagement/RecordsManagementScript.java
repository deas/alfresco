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
package org.alfresco.module.recordsManagement;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.jscript.Scopeable;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.jscript.ValueConverter;
import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.mozilla.javascript.Scriptable;

/**
 * Script implementation containing the commonly used record management functions.
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementScript extends BaseProcessorExtension implements Scopeable, RecordsManagementModel 
{
    /** The disposition action names and parameters */
    public static final String TRANSFER_DISPOSITION_ACTION = "org_alfresco_module_RecordsManagement_transferDispositionAction";
    public static final String DESTROY_DISPOSITION_ACTION = "org.alfresco.module.recordsManagement.action.DestroyDispositionAction";
    public static final String ACCESSION_DISPOSITION_ACTION = "org_alfresco_module_RecordsManagement_accessionDispositionAction";
    public static final String PARAM_LOCATION = "location";
    
   /** Scriptable scope object */
	private Scriptable scope;
	
	/** The service registry */
	private ServiceRegistry services;
    
    /** Value converter */
    private ValueConverter valueConverter = new ValueConverter();
    	
	/**
	 * Set the service registry
	 * 
	 * @param services	the service registry
	 */
	public void setServiceRegistry(ServiceRegistry services) 
	{
		this.services = services;
	}
	
	/**
	 * Set the scope
	 * 
	 * @param scope	the script scope
	 */
	public void setScope(Scriptable scope) 
	{
		this.scope = scope;
	}
    
    public Serializable incrementDateByYear(Serializable startDate, int value)
    {
        Date date = (Date)this.valueConverter.convertValueForRepo(startDate);
        
        Calendar calendar = Calendar.getInstance();     
        calendar.setTime(date);
        
        calendar.add(Calendar.YEAR, value);
        
        return valueConverter.convertValueForScript(services, scope, null, calendar.getTime());
    }
	
	/**
	 * Calculates the next interval date for a given type of date interval.
	 * 
	 * @param reviewPeriod	review period catetegory value as a node
	 * @param fromDate	    the date from which the next interval date should be calculated
	 * @return				the next interval date
	 */
	public Serializable calculateDateInterval(Serializable datePeriodUnit, int datePeriodValue, Serializable fromDate)
	{
		Date date = (Date)this.valueConverter.convertValueForRepo(fromDate);
		NodeRef nodeRef = (NodeRef)valueConverter.convertValueForRepo(datePeriodUnit);
		if (nodeRef == null)
        {
		    return null;
        }
        
		Calendar calendar = Calendar.getInstance();		
		calendar.setTime(date);
		
        // Do nothing for reviewPriod-0 (None) and reviewPeriod-1 (TBD) 
        if ((nodeRef.getId().equals("rm:datePeriod-0") == true) ||
            (nodeRef.getId().equals("rm:datePeriod-1") == true))
        {
            return null;
        }
        else if (nodeRef.getId().equals("rm:datePeriod-2") == true) 
		{
		    // Daily calculation
            calendar.add(Calendar.DAY_OF_YEAR, datePeriodValue);
		} 
		else if (nodeRef.getId().equals("rm:datePeriod-3") == true) 
		{
            // Weekly calculation
            calendar.add(Calendar.WEEK_OF_YEAR, datePeriodValue);
		} 
        else if (nodeRef.getId().equals("rm:datePeriod-4") == true) 
        {
            // Monthly calculation
            calendar.add(Calendar.MONTH, datePeriodValue);
        }
        else if (nodeRef.getId().equals("rm:datePeriod-5") == true) 
        {
            // Annual calculation
            calendar.add(Calendar.YEAR, datePeriodValue);
        }
		else if (nodeRef.getId().equals("rm:datePeriod-6") == true) 
		{
		    // Month end calculation
            calendar.add(Calendar.MONTH, datePeriodValue);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            
            // Set the time one minute to midnight
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
		} 
		else if (nodeRef.getId().equals("rm:datePeriod-7") == true) 
		{
            // Quater end calculation
            calendar.add(Calendar.MONTH, datePeriodValue*3);
            int currentMonth = calendar.get(Calendar.MONTH);
            if (currentMonth >= 0 && currentMonth <= 2)
            {
                calendar.set(Calendar.MONTH, 0);
            }
            else if (currentMonth >= 3 && currentMonth <= 5)
            {
                calendar.set(Calendar.MONTH, 3);
            }
            else if (currentMonth >= 6 && currentMonth <= 8)
            {
                calendar.set(Calendar.MONTH, 6);
            }
            else if (currentMonth >= 9 && currentMonth <= 11)
            {
                calendar.set(Calendar.MONTH, 9);
            }
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DAY_OF_YEAR, -1);

            // Set the time one minute to midnight
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
		} 
		else if (nodeRef.getId().equals("rm:datePeriod-8") == true) 
		{
            // Year end calculation
            calendar.add(Calendar.YEAR, datePeriodValue);
            calendar.set(Calendar.MONTH, 0);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DAY_OF_YEAR, -1);

            // Set the time one minute to midnight
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
		} 
		else if (nodeRef.getId().equals("rm:datePeriod-9") == true) 
		{
            // Financial year end calculation
            throw new RuntimeException("Finacial year end is currently unsupported.");

            // Set the time one minute to midnight 
            //calendar.set(Calendar.HOUR_OF_DAY, 23);
            //calendar.set(Calendar.MINUTE, 59);
		} 
        		
		return valueConverter.convertValueForScript(services, scope, null, calendar.getTime());
	}
    
    /**
     * Indicates wehther a given node is a filePlan
     * 
     * @param node  the node
     * @return      true if it is a filePlan, false otherwise
     */
    public boolean isFilePlan(ScriptNode node)
    {
        NodeRef nodeRef = (NodeRef)this.valueConverter.convertValueForRepo(node);
        QName nodeType = this.services.getNodeService().getType(nodeRef);
        return this.services.getDictionaryService().isSubClass(nodeType, RecordsManagementModel.TYPE_FILE_PLAN);
    }
    
    /**
     * Link a record to a file plan
     * 
     * @param recordNode    the record node
     * @param filePlanNode  the file plan node
     */
    public void linkToFilePlan(ScriptNode recordNode, ScriptNode filePlanNode)
    {
        NodeRef record = (NodeRef)this.valueConverter.convertValueForRepo(recordNode);
        NodeRef filePlan = (NodeRef)this.valueConverter.convertValueForRepo(filePlanNode);        
        this.services.getNodeService().createAssociation(record, filePlan, RecordsManagementModel.ASSOC_FILE_PLAN);
    }
    
    /**
     * Gets the file plan for the given node.  If none can be round null is returned.
     * 
     * @param node  the node
     * @return      the file plan node, null if none found
     */
    public ScriptNode getFilePlan(ScriptNode node)
    {
        ScriptNode result = null;
        NodeRef nodeRef = (NodeRef)this.valueConverter.convertValueForRepo(node);
        NodeRef filePlanNodeRef = getFilePlanNodeRef(nodeRef);
        
        if (filePlanNodeRef != null)
        {
            result = (ScriptNode)this.valueConverter.convertValueForScript(this.services, this.scope, null, filePlanNodeRef);            
        }
        
        return result;
    }
    
    private NodeRef getFilePlanNodeRef(NodeRef nodeRef)
    {
        NodeRef filePlan = null;
        
        if (this.services.getNodeService().hasAspect(nodeRef, RecordsManagementModel.ASPECT_RECORD) == true)
        {
            List<AssociationRef> assocs = this.services.getNodeService().getTargetAssocs(nodeRef, RecordsManagementModel.ASSOC_FILE_PLAN);
            if (assocs.size() == 1)
            {
                filePlan = assocs.get(0).getTargetRef();                
            }
            else if (assocs.size() > 1)
            {
                throw new AlfrescoRuntimeException("Multiple file plans are not currently supported.");
            }
        }
        
        return filePlan;
    }
    
    public void setCutoffPermissions(ScriptNode recordNode)
    {
        final NodeRef record = (NodeRef)this.valueConverter.convertValueForRepo(recordNode);
        AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>()
        {

            public Object doWork() throws Exception
            {
                RecordsManagementScript.this.services.getPermissionService().setInheritParentPermissions(record, false);        
                RecordsManagementScript.this.services.getPermissionService().setPermission(record, "GROUP_Record Managers", "All", true);
                RecordsManagementScript.this.services.getOwnableService().setOwner(record, "admin");
                return null;
            }
            
        }, AuthenticationUtil.getSystemUserName());
    }
    
    public void processImmediateDispositions(ScriptNode recordNode)
    {
        NodeRef record = (NodeRef)this.valueConverter.convertValueForRepo(recordNode);
        NodeRef filePlan = getFilePlanNodeRef(record);
        if (filePlan != null)
        {
            // Execute the transfer if instructions have been set and it should be done immediately
            if (this.services.getNodeService().hasAspect(filePlan, RecordsManagementModel.ASPECT_TRANSFER_INSTRUCTIONS) == true &&
                ((Boolean)this.services.getNodeService().getProperty(filePlan, RecordsManagementModel.PROP_TRANSFER_IMMEDIATELY)).equals(Boolean.TRUE) == true)
            {
                executeTransfer(filePlan, record);
            }
            
            // TODO sort out the accession here ...
            
            // Execute the destroy if instructions have been set and it should be done immediately
            if (this.services.getNodeService().hasAspect(filePlan, RecordsManagementModel.ASPECT_DESTROY_INSTRUCTIONS) == true &&
                    ((Boolean)this.services.getNodeService().getProperty(filePlan, RecordsManagementModel.PROP_DESTROY_IMMEDIATELY)).equals(Boolean.TRUE) == true)
            {
                executeDestroy(filePlan, record);
            }
        }
    }
    
    private void executeTransfer(NodeRef filePlan, NodeRef record)
    {
        // Get the transfer location
        String transferLocation = (String)this.services.getNodeService().getProperty(filePlan, RecordsManagementModel.PROP_TRANSFER_LOCATION);
        if (transferLocation != null)
        {        
            Action transferAction = this.services.getActionService().createAction(TRANSFER_DISPOSITION_ACTION);
            transferAction.setParameterValue(PARAM_LOCATION, transferLocation);
            this.services.getActionService().executeAction(transferAction, record, false, true);
        }
    }
    
    private void executeDestroy(NodeRef filePlan, NodeRef record)
    {
        Action transferAction = this.services.getActionService().createAction(DESTROY_DISPOSITION_ACTION);
        this.services.getActionService().executeAction(transferAction, record, false, true);
    }
}

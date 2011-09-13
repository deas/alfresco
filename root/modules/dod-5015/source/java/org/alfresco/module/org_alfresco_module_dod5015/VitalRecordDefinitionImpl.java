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
package org.alfresco.module.org_alfresco_module_dod5015;

import java.util.Date;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_dod5015.model.RecordsManagementModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.Period;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * Vital record definition implementation class
 * 
 * @author Roy Wetherall
 */
public class VitalRecordDefinitionImpl implements VitalRecordDefinition, RecordsManagementModel
{
    /** I18N */
    private static final String MSG_VITAL_DEF_MISSING = "rm.service.vital-def-missing";
    
    /** Service registry */
    private ServiceRegistry services;
    
    /** Node reference containing the vital record definition aspect */
    private NodeRef nodeRef;
    
    /**
     * Constructor
     * 
     * @param services  service registry
     * @param nodeRef   node reference
     */
    public VitalRecordDefinitionImpl(ServiceRegistry services, NodeRef nodeRef)
    {
        // Set the services reference
        this.services = services;
        
        // Check that we have a node that has the vital record definition aspect attached
        if (this.services.getNodeService().hasAspect(nodeRef, ASPECT_VITAL_RECORD_DEFINITION) == false)
        {
            throw new AlfrescoRuntimeException(I18NUtil.getMessage(MSG_VITAL_DEF_MISSING, nodeRef));
        }        
        this.nodeRef = nodeRef;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.VitalRecordDefinition#getNextReviewDate()
     */
    public Date getNextReviewDate()
    {
        return getReviewPeriod().getNextDate(new Date());
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.VitalRecordDefinition#getReviewPeriod()
     */
    public Period getReviewPeriod()
    {
        return (Period)this.services.getNodeService().getProperty(this.nodeRef, PROP_REVIEW_PERIOD);
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.VitalRecordDefinition#isVitalRecord()
     */
    public boolean isVitalRecord()
    {
        // Default value set in model so this is safe
        return ((Boolean)this.services.getNodeService().getProperty(this.nodeRef, PROP_VITAL_RECORD_INDICATOR)).booleanValue();
    }

}

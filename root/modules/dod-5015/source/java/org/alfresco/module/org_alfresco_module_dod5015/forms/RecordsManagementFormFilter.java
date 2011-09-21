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
package org.alfresco.module.org_alfresco_module_dod5015.forms;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementServiceRegistry;
import org.alfresco.module.org_alfresco_module_dod5015.model.DOD5015Model;
import org.alfresco.repo.forms.Field;
import org.alfresco.repo.forms.FieldGroup;
import org.alfresco.repo.forms.Form;
import org.alfresco.repo.forms.FormData;
import org.alfresco.repo.forms.processor.AbstractFilter;
import org.alfresco.repo.forms.processor.node.FieldUtils;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.ParameterCheck;

/**
 * Abstract base class for records management related form filter
 * implementations.
 * 
 * @author Gavin Cornwell
 */
public abstract class RecordsManagementFormFilter<ItemType> extends AbstractFilter<ItemType, NodeRef> implements
            DOD5015Model
{
    /** Logger */
    private static Log logger = LogFactory.getLog(RecordsManagementFormFilter.class);

    public static final String CUSTOM_RM_FIELD_GROUP_ID = "rm-custom";

    protected static final FieldGroup CUSTOM_RM_FIELD_GROUP = new FieldGroup(CUSTOM_RM_FIELD_GROUP_ID, null, false,
                false, null);

    protected NamespaceService namespaceService;
    protected NodeService nodeService;
    protected RecordsManagementServiceRegistry rmServiceRegistry;
    protected RecordsManagementService rmService;
    protected RecordsManagementAdminService rmAdminService;

    /**
     * Sets the NamespaceService instance
     * 
     * @param namespaceService The NamespaceService instance
     */
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    /**
     * Sets the node service
     * 
     * @param nodeService The NodeService instance
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Sets the RecordsManagementServiceRegistry instance
     * 
     * @param rmServiceRegistry The RecordsManagementServiceRegistry instance
     */
    public void setRecordsManagementServiceRegistry(RecordsManagementServiceRegistry rmServiceRegistry)
    {
        this.rmServiceRegistry = rmServiceRegistry;
    }
    
    /**
     * Sets the RecordsManagementService instance
     * 
     * @param rmService The RecordsManagementService instance
     */
    public void setRecordsManagementService(RecordsManagementService rmService)
    {
        this.rmService = rmService;
    }
    
    /**
     * Sets the RecordsManagementAdminService instance
     * 
     * @param rmAdminService The RecordsManagementAdminService instance
     */
    public void setRecordsManagementAdminService(RecordsManagementAdminService rmAdminService)
    {
        this.rmAdminService = rmAdminService;
    }

    /*
     * @see
     * org.alfresco.repo.forms.processor.Filter#beforePersist(java.lang.Object,
     * org.alfresco.repo.forms.FormData)
     */
    public void beforePersist(ItemType item, FormData data)
    {
        // ignored
    }

    /*
     * @see
     * org.alfresco.repo.forms.processor.Filter#beforeGenerate(java.lang.Object,
     * java.util.List, java.util.List, org.alfresco.repo.forms.Form,
     * java.util.Map)
     */
    public void beforeGenerate(ItemType item, List<String> fields, List<String> forcedFields, Form form,
                Map<String, Object> context)
    {
        // ignored
    }

    /*
     * @see
     * org.alfresco.repo.forms.processor.Filter#afterPersist(java.lang.Object,
     * org.alfresco.repo.forms.FormData, java.lang.Object)
     */
    public void afterPersist(ItemType item, FormData data, NodeRef persistedObject)
    {
        // ignored
    }

    /**
     * Adds a property definition for each of the custom properties for the
     * given RM type to the given form.
     * 
     * @param rmTypeCustomAspect Enum representing the RM type to add custom
     *            properties for
     * @param form The form to add the properties to
     */
    protected void addCustomRMProperties(QName customisableType, Form form)
    {
    	ParameterCheck.mandatory("customisableType", customisableType);
    	ParameterCheck.mandatory("form", form);
    	        
        Map<QName, PropertyDefinition> customProps = rmAdminService.getCustomPropertyDefinitions(customisableType);

        if (logger.isDebugEnabled() == true)
        {
            logger.debug("Found " + customProps.size() + " custom properties for customisable type " + customisableType);
        }

        // setup field definition for each custom property
        Collection<PropertyDefinition> properties = customProps.values();
        List<Field> fields = FieldUtils.makePropertyFields(properties, CUSTOM_RM_FIELD_GROUP, namespaceService);
        form.addFields(fields);
    }
}

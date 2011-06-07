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
package org.alfresco.module.org_alfresco_module_dod5015.forms;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.model.RecordsManagementModel;
import org.alfresco.repo.forms.FieldDefinition;
import org.alfresco.repo.forms.FieldGroup;
import org.alfresco.repo.forms.Form;
import org.alfresco.repo.forms.FormData;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of a form processor Filter.
 * <p>
 * The filter implements the <code>afterGenerate</code> method to ensure a
 * default unique identifier is provided for the <code>rma:identifier</code>
 * property.
 * </p>
 * <p>
 * The filter also ensures that any custom properties defined for the records
 * management type are provided as part of the Form.
 * </p>
 * 
 * @author Gavin Cornwell
 */
public class RecordsManagementTypeFormFilter extends RecordsManagementFormFilter<TypeDefinition>
{
    /** Logger */
    private static Log logger = LogFactory.getLog(RecordsManagementTypeFormFilter.class);

    protected static final String NAME_FIELD_GROUP_ID = "name";
    protected static final String TITLE_FIELD_GROUP_ID = "title";
    protected static final String DESC_FIELD_GROUP_ID = "description";
    protected static final String OTHER_FIELD_GROUP_ID = "other";

    protected static final FieldGroup NAME_FIELD_GROUP = new FieldGroup(NAME_FIELD_GROUP_ID, null, false, false, null);
    protected static final FieldGroup TITLE_FIELD_GROUP = new FieldGroup(TITLE_FIELD_GROUP_ID, null, false, false, null);
    protected static final FieldGroup DESC_FIELD_GROUP = new FieldGroup(DESC_FIELD_GROUP_ID, null, false, false, null);
    protected static final FieldGroup OTHER_FIELD_GROUP = new FieldGroup(OTHER_FIELD_GROUP_ID, null, false, false, null);
    
    /*
     * @see
     * org.alfresco.repo.forms.processor.Filter#afterGenerate(java.lang.Object,
     * java.util.List, java.util.List, org.alfresco.repo.forms.Form,
     * java.util.Map)
     */
    public void afterGenerate(TypeDefinition type, List<String> fields, List<String> forcedFields, Form form,
                Map<String, Object> context)
    {
        QName typeName = type.getName();

        // add any custom properties for the type being created (we don't need
        // to deal with the record type in here as records are typically uploaded 
        // and then their metadata edited after the fact)
//        if (TYPE_RECORD_SERIES.equals(typeName))
//        {
//            addCustomRMProperties(CustomisableRmElement.RECORD_SERIES, form);
//            groupFields(form);
//        }
//        else if (TYPE_RECORD_CATEGORY.equals(typeName))
//        {
//            addCustomRMProperties(CustomisableRmElement.RECORD_CATEGORY, form);
//            groupFields(form);
//        }
//        else if (TYPE_RECORD_FOLDER.equals(typeName))
//        {
//            addCustomRMProperties(CustomisableRmElement.RECORD_FOLDER, form);
//            groupFields(form);
//        }
        
        if (rmAdminService.isCustomisable(typeName) == true)
        {
        	addCustomRMProperties(typeName, form);
        	groupFields(form);        	
        }
    }

    /*
     * @see org.alfresco.repo.forms.processor.Filter#afterPersist(java.lang.Object, org.alfresco.repo.forms.FormData, java.lang.Object)
     */
    public void afterPersist(TypeDefinition item, FormData data, final NodeRef nodeRef)
    {
        // Once an RM container type has been persisted generate a default
        // identifer for it.
        if (this.nodeService.hasAspect(nodeRef, ASPECT_FILE_PLAN_COMPONENT))
        {
            if (logger.isDebugEnabled())
                logger.debug("Generating unique identifier for "
                            + this.nodeService.getType(nodeRef).toPrefixString(this.namespaceService));

            AuthenticationUtil.runAs(
            		new RunAsWork<Object>()
            		{
						public Object doWork() throws Exception 
						{
							nodeService.setProperty(nodeRef, RecordsManagementModel.PROP_IDENTIFIER, generateIdentifier(nodeRef));
							return null;
						}}, 
            		AuthenticationUtil.getAdminUserName());                       
        }
    }

    /**
     * Generates a unique identifier for the given node (based on the dbid).
     * 
     * @param nodeRef The NodeRef to generate a unique id for
     * @return The identifier
     */
    protected String generateIdentifier(NodeRef nodeRef)
    {
        Calendar fileCalendar = Calendar.getInstance();
        String year = Integer.toString(fileCalendar.get(Calendar.YEAR));
        Long dbId = (Long) this.nodeService.getProperty(nodeRef, ContentModel.PROP_NODE_DBID);
        String identifier = year + "-" + padString(dbId.toString(), 10);

        if (logger.isDebugEnabled()) logger.debug("Generated '" + identifier + "' for unique identifier");

        return identifier;
    }

    /**
     * Function to pad a string with zero '0' characters to the required length
     * 
     * @param s String to pad with leading zero '0' characters
     * @param len Length to pad to
     * @return padded string or the original if already at >=len characters
     */
    protected String padString(String s, int len)
    {
        String result = s;

        for (int i = 0; i < (len - s.length()); i++)
        {
            result = "0" + result;
        }

        return result;
    }
    
    /**
     * Puts all fields in a group to workaround ALF-6089.
     * 
     * @param form The form being generated
     */
    protected void groupFields(Form form)
    {
        // to control the order of the fields add the name, title and description fields to
        // a field group containing just that field, all other fields that are not already 
        // in a group go into an "other" field group. The client config can then declare a 
        // client side set with the same id and order them correctly.
        
        List<FieldDefinition> fieldDefs = form.getFieldDefinitions();
        for (FieldDefinition fieldDef : fieldDefs)
        {
            FieldGroup group = fieldDef.getGroup();
            if (group == null)
            {
                if (fieldDef.getName().equals(ContentModel.PROP_NAME.toPrefixString(this.namespaceService)))
                {
                    fieldDef.setGroup(NAME_FIELD_GROUP);
                }
                else if (fieldDef.getName().equals(ContentModel.PROP_TITLE.toPrefixString(this.namespaceService)))
                {
                    fieldDef.setGroup(TITLE_FIELD_GROUP);
                }
                else if (fieldDef.getName().equals(ContentModel.PROP_DESCRIPTION.toPrefixString(this.namespaceService)))
                {
                    fieldDef.setGroup(DESC_FIELD_GROUP);
                }
                else
                {
                    fieldDef.setGroup(OTHER_FIELD_GROUP);
                }
            }
        }
    }
}

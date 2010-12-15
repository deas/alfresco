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
package org.alfresco.module.org_alfresco_module_dod5015.script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.alfresco.module.org_alfresco_module_dod5015.CustomisableRmElement;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class provides the implementation for the custompropdefinitions.get webscript.
 * 
 * @author Neil McErlean
 */
public class CustomPropertyDefinitionsGet extends DeclarativeWebScript
{
    private static Log logger = LogFactory.getLog(CustomPropertyDefinitionsGet.class);
    
    private static final String ELEMENT = "element";
    private static final String PROP_ID = "propId";
    private RecordsManagementAdminService rmAdminService;
    
    public void setRecordsManagementAdminService(RecordsManagementAdminService rmAdminService)
    {
        this.rmAdminService = rmAdminService;
    }

    @Override
    public Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>();
        
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        String propId = templateVars.get(PROP_ID);
        String elementName = req.getParameter(ELEMENT);

        if (logger.isDebugEnabled() && elementName != null)
        {
            logger.debug("Getting custom property definitions for elementName " + elementName);
        }
        else if (logger.isDebugEnabled() && propId != null)
        {
            logger.debug("Getting custom property definition for propId " + propId);
        }
        
        // If propId has been provided then this is a request for a single custom-property-defn.
        // else it is a request for all defined on the specified element.
        List<PropertyDefinition> propData = new ArrayList<PropertyDefinition>();
        if (propId != null)
        {
            QName propQName = rmAdminService.getQNameForClientId(propId);
            PropertyDefinition propDefn = rmAdminService.getCustomPropertyDefinitions().get(propQName);
            if (propQName == null || propDefn == null)
            {
                throw new WebScriptException(Status.STATUS_NOT_FOUND, "Property definition for " + propId + " not found.");
            }
            propData.add(propDefn);
        }
        else if (elementName != null)
        {
            CustomisableRmElement elem = CustomisableRmElement.getEnumFor(elementName);
            Map<QName, PropertyDefinition> currentCustomProps = rmAdminService.getCustomPropertyDefinitions(elem);

            for (Entry<QName, PropertyDefinition> entry : currentCustomProps.entrySet())
            {
                propData.add(entry.getValue());
            }
        }
        else
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Either elementName or propId must be specified.");
        }

    	if (logger.isDebugEnabled())
    	{
    		logger.debug("Retrieved custom property definitions: " + propData);
    	}

    	model.put("customProps", propData);
    	
        return model;
    }
}
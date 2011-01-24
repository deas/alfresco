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
package org.alfresco.cmis.client.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.DatatypeFactory;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.PolicyImpl;
import org.apache.chemistry.opencmis.client.runtime.RelationshipImpl;
import org.apache.chemistry.opencmis.client.runtime.SessionImpl;
import org.apache.chemistry.opencmis.client.runtime.repository.ObjectFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;

public class AlfrescoObjectFactoryImpl extends ObjectFactoryImpl
{
    private static final long serialVersionUID = 1L;

    private Session session = null;

    /**
     * Default constructor.
     */
    public AlfrescoObjectFactoryImpl()
    {
    }

    public void initialize(Session session, Map<String, String> parameters)
    {
        super.initialize(session, parameters);
        this.session = session;
    }

    public Properties convertProperties(Map<String, ?> properties, ObjectType type, Set<Updatability> updatabilityFilter)
    {
        // check input
        if (properties == null)
        {
            return null;
        }

        // get the object and aspect types
        Object typeId = properties.get(PropertyIds.OBJECT_TYPE_ID);
        String typeIdStr = null;

        if (typeId instanceof String)
        {
            typeIdStr = typeId.toString();
        } else if (typeId instanceof Property<?>)
        {
            Object propValue = ((Property<?>) typeId).getFirstValue();
            typeIdStr = (propValue == null ? null : propValue.toString());
        } else
        {
            throw new IllegalArgumentException("Type property must be set!");
        }

        ObjectType objectType = null;
        List<ObjectType> aspectTypes = new ArrayList<ObjectType>();
        if (typeIdStr.indexOf(',') == -1)
        {
            objectType = session.getTypeDefinition(typeIdStr);
        } else
        {
            String[] typeIds = typeIdStr.split(",");
            objectType = session.getTypeDefinition(typeIds[0].trim());

            for (int i = 1; i < typeIds.length; i++)
            {
                aspectTypes.add(session.getTypeDefinition(typeIds[i].trim()));
            }
        }

        // split type properties from aspect properties
        Map<String, Object> typeProperties = new HashMap<String, Object>();
        Map<String, Object> aspectProperties = new HashMap<String, Object>();
        Map<String, PropertyDefinition<?>> aspectPropertyDefinition = new HashMap<String, PropertyDefinition<?>>();
        for (Map.Entry<String, ?> property : properties.entrySet())
        {
            if ((property == null) || (property.getKey() == null))
            {
                continue;
            }

            String id = property.getKey();
            Object value = property.getValue();

            if (PropertyIds.OBJECT_TYPE_ID.equals(id))
            {
                if (type == null)
                {
                    typeProperties.put(id, objectType.getId());
                }
            } else if (objectType.getPropertyDefinitions().containsKey(id))
            {
                typeProperties.put(id, value);
            } else
            {
                aspectProperties.put(id, value);

                for (ObjectType aspectType : aspectTypes)
                {
                    PropertyDefinition<?> propDef = aspectType.getPropertyDefinitions().get(id);
                    if (propDef != null)
                    {
                        aspectPropertyDefinition.put(id, propDef);
                        break;
                    }
                }
            }
        }

        // prepare type properties
        Properties result = super.convertProperties(typeProperties, type, updatabilityFilter);

        // prepare extensions
        List<CmisExtensionElement> alfrescoExtensionList = new ArrayList<CmisExtensionElement>();

        // prepare aspects
        for (ObjectType aspectType : aspectTypes)
        {
            alfrescoExtensionList.add(AlfrescoAspectsUtils.createAspectsToAddExtension(aspectType));
        }

        // prepare aspect properties
        if (!aspectProperties.isEmpty())
        {
            List<CmisExtensionElement> propertrtyExtensionList = new ArrayList<CmisExtensionElement>();

            for (Map.Entry<String, Object> property : aspectProperties.entrySet())
            {
                PropertyDefinition<?> propDef = aspectPropertyDefinition.get(property.getKey());
                if (propDef == null)
                {
                    throw new IllegalArgumentException("Unknown aspect property: " + property.getKey());
                }

                CmisExtensionElement element = AlfrescoAspectsUtils.createAspectPropertyExtension(propDef,
                        property.getValue());
                if (element != null)
                {
                    propertrtyExtensionList.add(element);
                }
            }

            alfrescoExtensionList.add(AlfrescoAspectsUtils.createAspectPropertiesExtension(propertrtyExtensionList));
        }

        if (!alfrescoExtensionList.isEmpty())
        {
            result.setExtensions(Collections.singletonList(AlfrescoAspectsUtils
                    .createSetAspectsExtension(alfrescoExtensionList)));
        }

        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Map<String, Property<?>> convertProperties(ObjectType objectType, Properties properties)
    {
        Map<String, Property<?>> result = super.convertProperties(objectType, properties);

        // find the Alfresco extensions
        List<CmisExtensionElement> alfrescoExtensions = AlfrescoAspectsUtils.findAlfrescoExtensions(properties
                .getExtensions());

        if (alfrescoExtensions == null)
        {
            // no Alfresco extensions found
            return result;
        }

        // get the aspect types
        Collection<ObjectType> aspectTypes = AlfrescoAspectsUtils.getAspectTypes(session, alfrescoExtensions);

        for (CmisExtensionElement extension : alfrescoExtensions)
        {
            if (!extension.getName().equals("properties"))
            {
                continue;
            }

            for (CmisExtensionElement property : extension.getChildren())
            {
                String id = property.getAttributes().get("propertyDefinitionId");

                // find the aspect type
                ObjectType aspectType = AlfrescoAspectsUtils.findAspect(aspectTypes, id);
                if (aspectType == null)
                {
                    throw new IllegalArgumentException("Unknown aspect property: " + id);
                }

                // convert values
                PropertyDefinition propDef = aspectType.getPropertyDefinitions().get(id);
                List values = new ArrayList();
                DatatypeFactory df = null;
                try
                {
                    for (CmisExtensionElement propertyValues : property.getChildren())
                    {
                        switch (propDef.getPropertyType())
                        {
                        case BOOLEAN:
                            values.add(Boolean.parseBoolean(propertyValues.getValue()));
                            break;
                        case DATETIME:
                            if (df == null)
                            {
                                df = DatatypeFactory.newInstance();
                            }
                            values.add(df.newXMLGregorianCalendar(propertyValues.getValue()));
                            break;
                        case DECIMAL:
                            values.add(new BigDecimal(propertyValues.getValue()));
                            break;
                        case INTEGER:
                            values.add(new BigInteger(propertyValues.getValue()));
                            break;
                        default:
                            values.add(propertyValues.getValue());
                        }
                    }
                } catch (Exception e)
                {
                    throw new IllegalArgumentException("Aspect conversation exception: " + e.getMessage(), e);
                }

                // add property
                result.put(id, createProperty(propDef, values));
            }
        }

        return result;
    }

    public CmisObject convertObject(ObjectData objectData, OperationContext context)
    {
        if (objectData == null)
        {
            throw new IllegalArgumentException("Object data is null!");
        }

        ObjectType type = getTypeFromObjectData(objectData);

        /* determine type */
        switch (objectData.getBaseTypeId())
        {
        case CMIS_DOCUMENT:
            return new AlfrescoDocumentImpl((SessionImpl) this.session, type, objectData, context);
        case CMIS_FOLDER:
            return new AlfrescoFolderImpl((SessionImpl) this.session, type, objectData, context);
        case CMIS_POLICY:
            return new PolicyImpl((SessionImpl) this.session, type, objectData, context);
        case CMIS_RELATIONSHIP:
            return new RelationshipImpl((SessionImpl) this.session, type, objectData, context);
        default:
            throw new CmisRuntimeException("unsupported type: " + objectData.getBaseTypeId());
        }
    }
}

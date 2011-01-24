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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.alfresco.cmis.client.TransientAlfrescoDocument;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.TransientDocumentImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.enums.Updatability;

public class TransientAlfrescoDocumentImpl extends TransientDocumentImpl implements TransientAlfrescoDocument
{
    protected TransientAlfrescoAspectsImpl aspects;

    @Override
    protected void initialize(Session session, CmisObject object)
    {
        super.initialize(session, object);
        aspects = new TransientAlfrescoAspectsImpl(session, object);
    }

    @SuppressWarnings("unchecked")
    public <T> void setPropertyValue(String id, Object value)
    {
        ObjectType aspectType = aspects.findAspect(id);

        if (aspectType == null)
        {
            super.setPropertyValue(id, value);
            return;
        }

        PropertyDefinition<T> propertyDefinition = (PropertyDefinition<T>) aspectType.getPropertyDefinitions().get(id);
        if (propertyDefinition == null)
        {
            throw new IllegalArgumentException("Unknown property '" + id + "'!");
        }
        // check updatability
        if (propertyDefinition.getUpdatability() == Updatability.READONLY)
        {
            throw new IllegalArgumentException("Property is read-only!");
        }

        List<T> values = AlfrescoAspectsUtils.checkProperty(propertyDefinition, value);

        // create and set property
        Property<T> newProperty = getObjectFactory().createProperty(propertyDefinition, values);
        properties.put(id, newProperty);

        isPropertyUpdateRequired = true;
        isModified = true;
    }

    public boolean hasAspect(String id)
    {
        return aspects.hasAspect(id);
    }

    public boolean hasAspect(ObjectType type)
    {
        return aspects.hasAspect(type);
    }

    public Collection<ObjectType> getAspects()
    {
        return aspects.getAspects();
    }

    public ObjectType findAspect(String propertyId)
    {
        return aspects.findAspect(propertyId);
    }

    public void addAspect(String... id)
    {
        aspects.addAspect(id);
    }

    public void addAspect(ObjectType... type)
    {
        aspects.addAspect(type);
    }

    public void removeAspect(String... id)
    {
        aspects.removeAspect(id);
    }

    public void removeAspect(ObjectType... type)
    {
        aspects.removeAspect(type);
    }

    @Override
    public ObjectId save()
    {
        ObjectId id = super.save();
        if (!isMarkedForDelete)
        {
            aspects.save();
        }

        return id;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Properties prepareProperties()
    {
        ObjectType type = getType();
        PropertyDefinition<String> propDef = (PropertyDefinition<String>) type.getPropertyDefinitions().get(
                PropertyIds.OBJECT_TYPE_ID);
        String objectTypeIdValue = AlfrescoAspectsUtils.createObjectTypeIdValue(type, getAspects());
        Property<String> objectTypeIdProperty = getObjectFactory().createProperty(propDef,
                Collections.singletonList(objectTypeIdValue));

        properties.put(PropertyIds.OBJECT_TYPE_ID, objectTypeIdProperty);

        return super.prepareProperties();
    }
}

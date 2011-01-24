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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.cmis.client.AlfrescoAspects;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.enums.ExtensionLevel;

public class AlfrescoAspectsImpl implements AlfrescoAspects
{
    private Session session;
    private CmisObject object;
    private Map<String, ObjectType> aspectTypes;

    public AlfrescoAspectsImpl(Session session, CmisObject object)
    {
        this.session = session;
        this.object = object;

        List<CmisExtensionElement> alfrescoExtensions = AlfrescoAspectsUtils.findAlfrescoExtensions(object
                .getExtensions(ExtensionLevel.PROPERTIES));

        if (alfrescoExtensions == null)
        {
            aspectTypes = Collections.emptyMap();
        } else
        {
            aspectTypes = new HashMap<String, ObjectType>();
            for (ObjectType type : AlfrescoAspectsUtils.getAspectTypes(session, alfrescoExtensions))
            {
                if (type != null)
                {
                    aspectTypes.put(type.getId(), type);
                }
            }
        }
    }

    public boolean hasAspect(String id)
    {
        return aspectTypes.containsKey(id);
    }

    public boolean hasAspect(ObjectType type)
    {
        return type == null ? false : hasAspect(type.getId());
    }

    public Collection<ObjectType> getAspects()
    {
        return aspectTypes.values();
    }

    public ObjectType findAspect(String propertyId)
    {
        return AlfrescoAspectsUtils.findAspect(aspectTypes.values(), propertyId);
    }

    public void addAspect(String... id)
    {
        if (id == null || id.length == 0)
        {
            throw new IllegalArgumentException("Id must be set!");
        }

        ObjectType[] types = new ObjectType[id.length];
        for (int i = 0; i < id.length; i++)
        {
            types[i] = session.getTypeDefinition(id[i]);
        }

        addAspect(types);
    }

    public void addAspect(ObjectType... type)
    {
        if (type == null || type.length == 0)
        {
            throw new IllegalArgumentException("Type must be set!");
        }

        AlfrescoAspectsUtils.updateAspects(session, object.getId(), type, null);
    }

    public void removeAspect(String... id)
    {
        if (id == null || id.length == 0)
        {
            throw new IllegalArgumentException("Id must be set!");
        }

        ObjectType[] types = new ObjectType[id.length];
        for (int i = 0; i < id.length; i++)
        {
            types[i] = session.getTypeDefinition(id[i]);
        }

        removeAspect(types);
    }

    public void removeAspect(ObjectType... type)
    {
        if (type == null || type.length == 0)
        {
            throw new IllegalArgumentException("Type must be set!");
        }

        AlfrescoAspectsUtils.updateAspects(session, object.getId(), null, type);
    }
}

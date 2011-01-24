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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.cmis.client.AlfrescoAspects;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.enums.ExtensionLevel;

public class TransientAlfrescoAspectsImpl implements AlfrescoAspects
{
    private Session session;
    private CmisObject object;
    private Map<String, ObjectType> aspectTypes;
    private Map<String, ObjectType> addAspectTypes;
    private Map<String, ObjectType> removeAspectTypes;

    public TransientAlfrescoAspectsImpl(Session session, CmisObject object)
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

        addAspectTypes = new HashMap<String, ObjectType>();
        removeAspectTypes = new HashMap<String, ObjectType>();
    }

    public boolean hasAspect(String id)
    {
        return (aspectTypes.containsKey(id) || addAspectTypes.containsKey(id)) && (!removeAspectTypes.containsKey(id));
    }

    public boolean hasAspect(ObjectType type)
    {
        return type == null ? false : hasAspect(type.getId());
    }

    public Collection<ObjectType> getAspects()
    {
        Collection<ObjectType> result = new ArrayList<ObjectType>();
        Set<String> addTypes = new HashSet<String>(addAspectTypes.keySet());

        for (String typeId : aspectTypes.keySet())
        {
            if (!removeAspectTypes.containsKey(typeId))
            {
                result.add(aspectTypes.get(typeId));
            }
            addTypes.remove(typeId);
        }

        for (String typeId : addTypes)
        {
            result.add(addAspectTypes.get(typeId));
        }

        return result;
    }

    public ObjectType findAspect(String propertyId)
    {
        return AlfrescoAspectsUtils.findAspect(getAspects(), propertyId);
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

        for (ObjectType t : type)
        {
            if (t != null)
            {
                addAspectTypes.put(t.getId(), t);
                removeAspectTypes.remove(t.getId());
            }
        }
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

        for (ObjectType t : type)
        {
            if (t != null)
            {
                addAspectTypes.remove(t.getId());
                removeAspectTypes.put(t.getId(), t);
            }
        }
    }

    public void save()
    {
        if (addAspectTypes.isEmpty() && removeAspectTypes.isEmpty())
        {
            return;
        }

        AlfrescoAspectsUtils.updateAspects(session, object.getId(), addAspectTypes.values().toArray(new ObjectType[0]),
                removeAspectTypes.values().toArray(new ObjectType[0]));
    }
}

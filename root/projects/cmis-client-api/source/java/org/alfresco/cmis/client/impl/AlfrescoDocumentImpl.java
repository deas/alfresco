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
import java.util.List;
import java.util.Map;

import org.alfresco.cmis.client.AlfrescoDocument;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Policy;
import org.apache.chemistry.opencmis.client.api.TransientCmisObject;
import org.apache.chemistry.opencmis.client.runtime.DocumentImpl;
import org.apache.chemistry.opencmis.client.runtime.SessionImpl;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.ObjectData;

public class AlfrescoDocumentImpl extends DocumentImpl implements AlfrescoDocument
{
    private static final long serialVersionUID = 1L;

    protected AlfrescoAspectsImpl aspects;

    public AlfrescoDocumentImpl(SessionImpl session, ObjectType objectType, ObjectData objectData,
            OperationContext context)
    {
        super(session, objectType, objectData, context);
    }

    @Override
    protected void initialize(SessionImpl session, ObjectType objectType, ObjectData objectData,
            OperationContext context)
    {
        super.initialize(session, objectType, objectData, context);
        aspects = new AlfrescoAspectsImpl(session, this);
    }

    @Override
    protected TransientCmisObject createTransientCmisObject()
    {
        TransientAlfrescoDocumentImpl td = new TransientAlfrescoDocumentImpl();
        td.initialize(getSession(), this);

        return td;
    }

    @Override
    public ObjectId updateProperties(Map<String, ?> properties, boolean refresh)
    {
        return super.updateProperties(
                AlfrescoAspectsUtils.preparePropertiesForUpdate(properties, getType(), getAspects()), refresh);
    }

    @Override
    public ObjectId checkIn(boolean major, Map<String, ?> properties, ContentStream contentStream,
            String checkinComment, List<Policy> policies, List<Ace> addAces, List<Ace> removeAces)
    {
        return super.checkIn(major,
                AlfrescoAspectsUtils.preparePropertiesForUpdate(properties, getType(), getAspects()), contentStream,
                checkinComment, policies, addAces, addAces);
    }

    public boolean hasAspect(String id)
    {
        readLock();
        try
        {
            return aspects.hasAspect(id);
        } finally
        {
            readUnlock();
        }
    }

    public boolean hasAspect(ObjectType type)
    {
        readLock();
        try
        {
            return aspects.hasAspect(type);
        } finally
        {
            readUnlock();
        }
    }

    public Collection<ObjectType> getAspects()
    {
        readLock();
        try
        {
            return aspects.getAspects();
        } finally
        {
            readUnlock();
        }
    }

    public ObjectType findAspect(String propertyId)
    {
        readLock();
        try
        {
            return aspects.findAspect(propertyId);
        } finally
        {
            readUnlock();
        }
    }

    public void addAspect(String... id)
    {
        readLock();
        try
        {
            aspects.addAspect(id);
        } finally
        {
            readUnlock();
        }
        refresh();
    }

    public void addAspect(ObjectType... type)
    {
        readLock();
        try
        {
            aspects.addAspect(type);
        } finally
        {
            readUnlock();
        }
        refresh();
    }

    public void removeAspect(String... id)
    {
        readLock();
        try
        {
            aspects.removeAspect(id);
        } finally
        {
            readUnlock();
        }
        refresh();
    }

    public void removeAspect(ObjectType... type)
    {
        readLock();
        try
        {
            aspects.removeAspect(type);
        } finally
        {
            readUnlock();
        }
        refresh();
    }
}

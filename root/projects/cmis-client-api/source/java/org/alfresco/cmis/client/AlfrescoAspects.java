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
package org.alfresco.cmis.client;

import java.util.Collection;

import org.apache.chemistry.opencmis.client.api.ObjectType;

/**
 * Alfresco aspects interface.
 */
public interface AlfrescoAspects
{
    /**
     * Returns if the given aspect is applied to this object.
     * 
     * @param id
     *            the aspect id
     * 
     * @return <code>true</code> if the aspect is applied, <code>false</code>
     *         otherwise
     */
    boolean hasAspect(String id);

    /**
     * Returns if the given aspect is applied to this object.
     * 
     * @param type
     *            the aspect object type
     * 
     * @return <code>true</code> if the aspect is applied, <code>false</code>
     *         otherwise
     */
    boolean hasAspect(ObjectType type);

    /**
     * Returns all applied aspects. If no aspect is applied, an empty collection
     * is returned.
     * 
     * @return collection of the applied aspects
     */
    Collection<ObjectType> getAspects();

    /**
     * Returns the aspect type that defines the given property.
     * 
     * @param propertyId
     *            the property id
     * 
     * @return the aspect type if the property id is defined in an applied
     *         aspect, <code>null</code> otherwise
     */
    ObjectType findAspect(String propertyId);

    /**
     * Adds one or more aspects to the object.
     * 
     * @param id
     *            the aspect id or ids
     */
    void addAspect(String... id);

    /**
     * Adds one or more aspects to the object.
     * 
     * @param id
     *            the aspect type or types
     */
    void addAspect(ObjectType... type);

    /**
     * Removes one or more aspects from the object.
     * 
     * @param id
     *            the aspect id or ids
     */
    void removeAspect(String... id);

    /**
     * Removes one or more aspects from the object.
     * 
     * @param id
     *            the aspect type or types
     */
    void removeAspect(ObjectType... type);
}

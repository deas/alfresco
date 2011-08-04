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
package org.alfresco.opencmis.dictionary;

import java.io.Serializable;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;

public interface CMISNodeInfo
{
    String getObjectId();

    CMISObjectVariant getObjectVariant();

    boolean isVariant(CMISObjectVariant var);

    NodeRef getNodeRef();

    String getCurrentNodeId();

    NodeRef getCurrentNodeNodeRef();

    String getCurrentObjectId();

    boolean isCurrentVersion();

    boolean isPWC();

    boolean hasPWC();

    boolean isVersion();

    boolean isLatestVersion();

    boolean isLatestMajorVersion();

    boolean isMajorVersion();

    String getVersionLabel();

    String getCheckinComment();

    AssociationRef getAssociationRef();

    TypeDefinitionWrapper getType();

    boolean isFolder();

    boolean isRootFolder();

    boolean isDocument();

    boolean isRelationship();

    String getName();

    String getPath();

    Serializable getCreationDate();

    Serializable getModificationDate();

    Serializable getPropertyValue(String id);

    boolean containsPropertyValue(String id);

    void putPropertyValue(String id, Serializable value);
}
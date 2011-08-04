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
package org.alfresco.opencmis.dictionary;

public enum CMISObjectVariant
{
    INVALID_ID, // not a valid object id
    NOT_EXISTING, // valid id but object doesn't exist
    NOT_A_CMIS_OBJECT, // object is not mapped to CMIS
    FOLDER, // object is a folder
    CURRENT_VERSION, // object is a document (current version)
    VERSION, // object is a version (not updatable)
    PWC, // object is a PWC
    ASSOC, // object is a relationship
    PERMISSION_DENIED
    // user has no permissions
}

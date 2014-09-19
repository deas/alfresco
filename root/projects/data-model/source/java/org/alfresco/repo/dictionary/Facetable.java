/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.repo.dictionary;

/**
 * How faceting is to be supported
 * 
 * @author Andy
 *
 */
public enum Facetable
{
    /**
     * TRUE - faceting is required and enhanced support for this is provided if possible
     */
    TRUE,
    /**
     * UNSET - facet support is unset, standard support is assumed
     */
    UNSET,
    /**
     * FALSE - feceting is not required and will not be supported
     */
    FALSE;
    
    public static String serializer(Facetable facetable) {
        return facetable.toString();
    }

    public static Facetable deserializer(String value) {
        if (value == null) {
            return null;
        } else if (value.equalsIgnoreCase(TRUE.toString())) {
            return TRUE;
        } else if (value.equalsIgnoreCase(FALSE.toString())) {
            return FALSE;
        } else if (value.equalsIgnoreCase(UNSET.toString())) {
            return UNSET;
        } else {
            throw new IllegalArgumentException(
                    "Invalid facetable enum value: " + value);
        }
    }
}

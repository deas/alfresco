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
package org.alfresco.webservice.util;

import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.Store;

/**
 * This class provides a number of common utility methods usful when using the web service API
 * 
 * @author Roy Wetherall
 */
public class Utils
{
	/**
	 * Creates a single valued named value.
	 * 
	 * @param name			the name 
	 * @param value			the value
	 * @return NamedValue	the created NamedValue object
	 */
    public static NamedValue createNamedValue(String name, String value)
    {
        NamedValue namedValue = new NamedValue();
        namedValue.setName(name);
        namedValue.setIsMultiValue(false);
        namedValue.setValue(value);
        return namedValue;
    }
    
    /**
     * Creates a multi-valued named value
     * 
     * @param name			the name
     * @param values		the array of values
     * @return NamedValue	the create NamedValue object
     */
    public static NamedValue createNamedValue(String name, String[] values)
    {
        NamedValue namedValue = new NamedValue();
        namedValue.setName(name);
        namedValue.setIsMultiValue(true);
        namedValue.setValues(values);
        return namedValue;
    }
    
    /**
     * Gets the store reference string for a given Store object.
     * 
     * @param store		the store
     * @return String	the store reference string
     */
    public static String getStoreRef(Store store)
    {
    	return store.getScheme() + "://" + store.getAddress();
    }

    /**
     * Gets the node reference string for a given Reference object
     * 
     * @param reference		the reference
     * @return String		the node reference string
     */
    public static String getNodeRef(Reference reference)
    {
    	return getStoreRef(reference.getStore()) + "/" + reference.getUuid();
    }
}

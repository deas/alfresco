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
package org.alfresco.module.org_alfresco_module_dod5015;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This enum gives the set of allowed customisable types (aspects) where a custom
 * property can be defined.
 */
public enum CustomisableRmElement
{
    RECORD_SERIES   ("rmc:customRecordSeriesProperties"),
    RECORD_CATEGORY ("rmc:customRecordCategoryProperties"),
    RECORD_FOLDER   ("rmc:customRecordFolderProperties"),
    RECORD          ("rmc:customRecordProperties");

    private static Log logger = LogFactory.getLog(CustomisableRmElement.class);
    private final String aspectName;
    private CustomisableRmElement(String aspectName)
    {
        this.aspectName = aspectName;
    }
    
    public static CustomisableRmElement getEnumFor(String elementName)
    {
    	// Two elementName formats are accepted here.
    	//
    	// 1. That used in JSON          e.g. recordSeries
    	// 2. That used in enum.toString e.g. RECORD_SERIES
        if ("recordSeries".equalsIgnoreCase(elementName) ||
        		RECORD_SERIES.toString().equals(elementName))
        {
            return RECORD_SERIES;
        }
        else if ("recordCategory".equalsIgnoreCase(elementName) ||
    		RECORD_CATEGORY.toString().equals(elementName))
        {
            return RECORD_CATEGORY;
        }
        else if ("recordFolder".equalsIgnoreCase(elementName) ||
    		RECORD_FOLDER.toString().equals(elementName))
        {
            return RECORD_FOLDER;
        }
        else if ("record".equalsIgnoreCase(elementName) ||
    		RECORD.toString().equals(elementName))
        {
            return RECORD;
        }
        else
        {
        	throw new IllegalArgumentException("Unknown elementName for CustomisableRmElement: " + elementName);
        }
    }
    
    /**
     * This method returns the String form of the aspect name which is used to house
     * the custom properties for this element.
     * 
     * @return The String form of the corresponding aspect name e.g. "rmc:customRecordProperties"
     */
    public String getCorrespondingAspect()
    {
        return aspectName;
    }
}
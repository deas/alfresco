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

package org.alfresco.module.vti.metadata.dic;

import java.util.EnumSet;

/**
 * <p>Used to define the behavior of file upload operations.</p>
 * 
 * @author Michael Shavnev
 */
public enum PutOption
{    
    /**
     * The client MUST send "none" if it does not want to specify any of the options given 
     * by a RENAME-OPTION-VAL.
     */
    none,
    
    /**
     * Specified, the server does all the needed checking to ensure that all the files can be 
     * updated before changing the first one
     */
    atomic,
    
    /**
     * Used to support long-term checkout operations
     */
    checkin,
    
    /**
     * Valid only if checkin is specified. Notifies the source control of the new content (checkin),
     * but keeps the document checked out.
     */
    checkout,
    
    /**
     * The parent directory is created if it does not exist.
     */
    createdir,
    
    /**
     * Uses the date and time the document was last modified to determine whether the 
     * item has been concurrently modified by another user. This flag is used to prevent 
     * race conditions where two users could edit the same data
     */
    edit,
    
    /**
     * Acts as though versioning is enabled, even if it is not
     */
    forceversions,
    
    /**
     * Requests that metadata be returned for <b>thicket</b> supporting files.
     */
    listthickets,
    
    /**
     * Preserves information about who created the file and when.
     */
    migrationsemantics,
    
    /**
     * Does not add the document to source control.
     */
    noadd,
    
    /**
     * Uses the date and time the document was last modified, as specified in the inbound metainfo, rather than the extent of time on the server.
     */
    overwrite,
    
    /**
     * Specifies that the associated file is a thicket supporting file. 
     */
    thicket;
    
    
    public static EnumSet<PutOption> getOptions(String stringValues)
    {
        EnumSet<PutOption> enumSet = null;
        
        if (stringValues == null || stringValues.trim().length() == 0) 
        {
            enumSet = EnumSet.of(PutOption.none);
        } 
        else 
        {
            String[] values = stringValues.split(",");
            enumSet = EnumSet.noneOf(PutOption.class);
            
            for (String value : values)
            {
                enumSet.add(valueOf(value));
            }
        }

        return enumSet;
    }
    
    
}
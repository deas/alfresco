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
 *
 * Script: onAddAspect_rmaCutoff.js
 * Author: Roy Wetherall
 * 
 * Behaviour script executed when the cutoff aspect is added.
 */ 
 
var record = behaviour.args[0];

if (record.hasAspect(rm.ASPECT_RECORD) == true)
{
    var filePlan = rm.getFilePlan(record);    
    
    if (filePlan.properties[rm.PROP_PROCESS_HOLD]) 
    {
        /// Hold the record
        record.addAspect(rm.ASPECT_HELD);
    }
    else
    {
        // Process any dispositions that should occure immediatly
        rm.processImmediateDispositions(record);
    }
    
    // Ensure that once a record is cutoff only the record managers can see it
    rm.setCutoffPermissions(record);
}
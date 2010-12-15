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
 * Script: onAddAspect_rmaSuperseeded.js
 * Author: Roy Wetherall
 * 
 * Behaviour script executed when the superseeded aspect is added.
 */
 
var record = behaviour.args[0];
if (record.hasAspect("rma:record") == true)
{ 
    var filePlan = rm.getFilePlan(record);
    if (filePlan != null)
    {
        if (filePlan.properties[rm.PROP_CUTOFF_ON_SUPERSEDED] == true)        
        {
            // Add the cutoff aspect to the record
            record.addAspect(rm.ASPECT_CUTOFF);
       }
    }
}
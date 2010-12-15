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
 * Script: onAddAspect_rmaHeld.js
 * Author: Roy Wetherall
 * 
 * Behaviour script executed when the held aspect is added.
 */

var record = behaviour.args[0];

if (record.hasAspect(rm.ASPECT_RECORD) == true)
{
    var filePlan = rm.getFilePlan(record);
    
    // Set the hold schedule details
    record.properties[rm.PROP_HOLD_UNTIL_EVENT] = filePlan.properties[rm.PROP_DISPOSITION_INSTRUCTIONS];
    record.properties[rm.PROP_FROZEN] = false;  
     
    var cutoffDateTime = null;    
    if (filePlan.properties[rm.PROP_DISCRETIONARY_HOLD] == true) 
    {
        logger.log("Creating discretionary hold.");
        cutoffDateTime = new Date();
        var newYear = cutoffDateTime.getFullYear() + 100;
        cutoffDateTime.setFullYear(newYear);
    }
    else
    {
        logger.log("Normal hold");
        cutoffDateTime = rm.calculateDateInterval(
                                      filePlan.properties[rm.PROP_HOLD_PERIOD_UNIT], 
                                      filePlan.properties[rm.PROP_HOLD_PERIOD_VALUE], 
                                      new Date());
    }
    
    if (cutoffDateTime != null)
    {
        record.properties[rm.PROP_HOLD_UNTIL] = cutoffDateTime;
    }
    
    record.save();
}
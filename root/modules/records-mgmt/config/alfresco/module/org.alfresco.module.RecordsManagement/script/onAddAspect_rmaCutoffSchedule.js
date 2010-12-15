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
 * Script: onAddAspect_rmaCutoffSchedule.js
 * Author: Roy Wetherall
 * 
 * Behaviour script executed when the cutoffSchedule aspect is added.
 */
 
var record = behaviour.args[0];
if (record.hasAspect(rm.ASPECT_RECORD) == true)
{ 
    var filePlan = rm.getFilePlan(record);
    if (filePlan != null)
    { 
        logger.log("Found the record an file plan and populating the cutoff schedule properties");
        
        record.properties[rm.CUTOFF_EVENT] = filePlan.properties[rm.EVENT_TRIGGER];
        
        // Calculate the next cutoff period
        var cutoffPeriod = rm.calculateDateInterval(
                filePlan.properties[rm.PROP_CUTOFF_PERIOD_UNIT],
                filePlan.properties[rm.PROP_CUTOFF_PERIOD_VALUE],
                new Date());
                
        if (cutoffPeriod != null)        
        {
           logger.log("CutoffDate: " + cutoffPeriod);   
           record.properties[rm.PROP_CUTOFF_DATE_TIME] = cutoffPeriod.getTime();
        }
        
        record.save();
    }
}
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
 * Script: scheduled_cutoff.js
 * Author: Roy Wetherall
 * 
 * Checks for record reaedy for scheduled cutoff and applied the cutoff aspect is appropriate.
 */
 
 logger.log("Executing scheduled cutoff");
 
// Calculate the date range used in the query
var newDate = new Date();
var currentdate = String(newDate.getFullYear()) + "-"
                  + utils.pad(String(newDate.getMonth()+1),2) + "-"
                  + utils.pad(String(newDate.getDate()),2)
                  + "T00:00:00.00Z";                  
var dateRange = "[\"1970-01-01T00:00:00.00Z\" TO \"" + currentdate + "\"]";

// Execute the query and process the results
var query = "+ASPECT:\"rma:record\" +ASPECT:\"rma:cutoffSchedule\" -ASPECT:\"rma:cutoff\" +@rma\\:cutoffDateTime:" + dateRange;      
logger.log(query);

var records = search.luceneSearch(query);   
logger.log(records.length);
for (var i=0; i<records.length; i++) 
{
    // Get the record
    record = records[i];         
    
    // Cut the record off
    record.addAspect(rm.ASPECT_CUTOFF);
}
                  
                  
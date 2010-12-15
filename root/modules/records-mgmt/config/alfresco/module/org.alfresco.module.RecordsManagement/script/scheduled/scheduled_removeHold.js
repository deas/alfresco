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
 * Script: scheduled_removeHold.js
 * Author: Roy Wetherall
 * 
 * Checks for held records that have passed their hold untill date and are not frozen and remove the hold.
 */
 
 logger.log("Executing scheduled remove hold");
 
// Calculate the date range used in the query
var newDate = new Date();
var currentdate = String(newDate.getFullYear()) + "-"
                  + utils.pad(String(newDate.getMonth()+1),2) + "-"
                  + utils.pad(String(newDate.getDate()),2)
                  + "T00:00:00.00Z";                  
var dateRange = "[\"1970-01-01T00:00:00.00Z\" TO \"" + currentdate + "\"]";

// Execute the query and process the results
var query = "+ASPECT:\"rma:record\" +ASPECT:\"rma:held\" +ASPECT:\"rma:cutoff\" +@rma\\:frozen:FALSE +@rma\\:holdUntil:" + dateRange;      

logger.log(query);

var records = search.luceneSearch(query);   
for (var i=0; i<records.length; i++) 
{
    // Get the record
    record = records[i];         
    
    // Remove the hold
    record.removeAspect(rm.ASPECT_HELD);
}
                  
                  
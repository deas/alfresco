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
 * Script: onCreateChildAssociation_rmaFilePlan_cmContains.js
 * Author: Roy Wetherall
 * 
 * Behaviour script executed when a contains assoc is created under a file plan.
 */

/**
 * Calculates the name of the record based on the counter and information
 * retrieved from the passed file plan.
 * 
 * @param record    the record
 * @param filePlan  the file plan
 */
function calculateRecordName(record, filePlan)
{
    // Get the category id
    var catid = filePlan.properties["rma:recordCategoryIdentifier"];
    // TODO test that the cat has been set .. 
       
    // Get the current record count
    var countAction = actions.create("counter");
    countAction.execute(filePlan);
    var recordCounter = filePlan.properties["cm:counter"];
       
    // Pad to get the db id
    var dbid = utils.pad(String(recordCounter), 4);
    logger.log(record.name + ": " + "catid = " + catid + " and dbid = " + dbid);
         
    // Set the property values based on the parent file plan
    record.properties["rma:recordIdentifier"] = catid + "-" + dbid;
    if (record.isContainer == true)
    {
        record.properties["cm:name"] = "Folder " + dbid + " - " + record.properties["cm:name"];
    }
    else
    {
        record.properties["cm:name"] = catid + "-" + dbid + " " + record.properties["cm:name"];
    }  
    
    record.save();  
}

/**
 * Initialise the property values of the record based on the passed file plan
 * 
 * @param record    the record
 * @param filePlan  the file plan
 */
function initialiseRecordProperties(record, filePlan)
{
    // Get the title and description of the record
    var title = record.properties["cm:title"];
    var description = record.properties["cm:description"];

    // Set the property values of the record based on the file plan
    record.properties["cm:title"] = record.properties["cm:name"];
    if (description == "" || description == null) 
    {
        description = title;
    }
    record.properties["rma:subject"] = description;
    record.properties["rma:originatingOrganization"] = filePlan.properties["rma:defaultOriginatingOrganization"];
    record.properties["rma:recordNote"]              = filePlan.properties["rma:filePlanNote"];
    record.properties["rma:supplementalMarkingList"] = filePlan.properties["rma:defaultMarkingList"];
    record.properties["rma:mediaFormat"]             = filePlan.properties["rma:defaultMediaType"];
    record.properties["rma:originatingOrganization"] = filePlan.properties["rma:defaultOriginatingOrganization"];
    record.properties["cm:description"]              = filePlan.properties["rma:dispositionInstructions"];
    record.properties["rma:privacyActSystem"] = filePlan.properties["rma:privacyActSystem"]; 
    if (record.isContent == true)
    {
        record.properties["rma:format"] = record.mimetype;   
    }
    record.properties["rma:dateFiled"]          = record.properties["cm:modified"];
    record.properties["rma:publicationDate"]    = record.properties["cm:modified"];   
    
    if (record.properties["cm:author"] == null || record.properties["cm:author"] == "") 
    {
        record.properties["rma:originator"] = record.properties["cm:creator"];
        record.properties["rma:addressee"] = record.properties["cm:creator"];
    }
    else 
    {
        record.properties["rma:originator"] = record.properties["cm:author"];
        record.properties["rma:addressee"] = record.properties["cm:author"];
    }

    record.properties["rma:dateReceived"] = record.properties["cm:modified"].getTime();

    record.save();
}

function applyLifeCycleAspects(record, fileplan)
{
    // Add the vital record apsect if required
    if (filePlan.properties[rm.PROP_VITAL_RECORD_INDICATOR] == true) 
    {
        record.addAspect("rma:vitalrecord"); 
    }
    
    // Add the process cuttof aspect if required
    if (filePlan.properties["rma:processCutoff"] == true) 
    {
        record.addAspect("rma:cutoffSchedule");
    }     
}

// Get the file plan and record nodes
var filePlan = behaviour.args[0].parent;
var record = behaviour.args[0].child;

if (record.hasAspect("sys:temporary") == false)
{   
    // Add the record aspect if it has not already been added
    if (record.hasAspect("rma:record") == false)
    {
        logger.log("Adding rma:record aspect");
        record.addAspect("rma:record");
        
        // Link to the parent file plan
        rm.linkToFilePlan(record, filePlan);
    }
    
    // Calcuate record name
    logger.log("Calculating record name");
    calculateRecordName(record, filePlan);
    
    // Initialise the records property values
    logger.log("Initialising record properties");
    initialiseRecordProperties(record, filePlan);
    
    // Add the required lifecycle aspects
    logger.log("Applying life cycle aspects");
    applyLifeCycleAspects(record, filePlan);
    
    // Save any changes made to the record
    record.save();   
}
   
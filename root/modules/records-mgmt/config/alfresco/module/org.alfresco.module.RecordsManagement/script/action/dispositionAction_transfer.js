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
 * Script: dispositionAction_accession.js
 * Author: Roy Wetherall
 * 
 * Transfer disposition action implementation.
 */
 
 var record = document;
 if (record.hasAspect(rm.ASPECT_TRANSFERED) == false)
 {
     var transferLocation = action.parameters[rm.PARAM_LOCATION];
     
     // TODO: do we need to ensure that is actually a record?
     
     // TODO: for now we assume that this is a valid path location to a folder in the spaces store of the repository
     // Resolve the path to a node
     var nodes = search.xpathSearch(transferLocation);
     if (nodes.length == 1)
     {
         var node = nodes[0];
         record.move(node);
         record.addAspect("rma:transfered");
     }
     else
     {
         // TODO how do we handle exceptions
         logger.log("An invalid transfer location has been set: " + transferLocation);
     }
 }
 else
 {
     logger.log("This record has already been transfered. (" + record.id + ")");
 }
 
 
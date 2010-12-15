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

import java.util.List;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Disposition schedule interface
 * 
 * @author Roy Wetherall
 */
public interface DispositionSchedule
{
    /**
     * Get the NodeRef that represents the disposition schedule
     * 
     * @return NodeRef of disposition schedule
     */
    NodeRef getNodeRef();
    
    /**
     * Get the disposition authority
     * 
     * @return  String  disposition authority
     */
    String getDispositionAuthority();
    
    /**
     * Get the disposition instructions
     * 
     * @return  String  disposition instructions
     */
    String getDispositionInstructions();
    
    /**
     * Indicates whether the disposal occurs at record level or not
     * 
     * @return  boolean true if at record level, false otherwise
     */
    boolean isRecordLevelDisposition();
    
    /**
     * Gets all the disposition action definitions for the schedule
     * 
     * @return  List<DispositionActionDefinition>   disposition action definitions
     */
    List<DispositionActionDefinition> getDispositionActionDefinitions();
    
    /**
     * Get the disposition action definition
     * 
     * @param id    the action definition id
     * @return DispositionActionDefinition  disposition action definition
     */
    DispositionActionDefinition getDispositionActionDefinition(String id);
}

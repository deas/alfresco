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
package org.alfresco.module.org_alfresco_module_dod5015.audit;

/**
 * Class to represent an audit event
 *
 * @author Gavin Cornwell
 */
public class AuditEvent
{
    private final String name;
    private final String label;
    
    /**
     * Constructor 
     * 
     * @param name The audit event name
     * @param label The audit event label
     */
    public AuditEvent(String name, String label)
    {
        this.name = name;
        this.label = label;
    }

    /**
     * 
     * @return The audit event name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * 
     * @return The audit event label
     */
    public String getLabel()
    {
        return this.label;
    }
}

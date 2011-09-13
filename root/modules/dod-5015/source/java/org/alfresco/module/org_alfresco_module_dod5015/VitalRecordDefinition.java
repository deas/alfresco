/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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

import java.util.Date;

import org.alfresco.service.cmr.repository.Period;

/**
 * Vital record definition interface
 * 
 * @author Roy Wetherall
 */
public interface VitalRecordDefinition
{
    /**
     * Vital record indicator
     * 
     * @return  boolean     true if vital records, false otherwise
     */
    boolean isVitalRecord();
    
    /**
     * Review period for vital records
     * 
     * @return Period   review period
     */
    Period getReviewPeriod();
    
    /**
     * Gets the next review date based on the review period
     * 
     * @return Date date of the next review
     */
    Date getNextReviewDate();
}

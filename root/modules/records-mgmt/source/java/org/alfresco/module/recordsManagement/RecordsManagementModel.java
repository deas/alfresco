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
package org.alfresco.module.recordsManagement;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;

/**
 * Helper class containing records management qualified names
 * 
 * @author Roy Wetherall
 */
public interface RecordsManagementModel 
{
	// Spaces store
	public static StoreRef SPACES_STORE = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
	
	// Namespace details
	public static String RM_URI = "http://www.alfresco.org/model/record/1.0";
	public static String RM_PREFIX = "rma";
    
    // File plan type 
    public static QName TYPE_FILE_PLAN = QName.createQName(RM_URI, "filePlan");
    public static QName PROP_PROCESS_HOLD = QName.createQName(RM_URI, "processHold");
    public static QName PROP_DISPOSITION_INSTRUCTIONS = QName.createQName(RM_URI, "dispositionInstructions");
    public static QName PROP_DISCRETIONARY_HOLD = QName.createQName(RM_URI, "discretionaryHold");
    public static QName PROP_HOLD_PERIOD_UNIT = QName.createQName(RM_URI, "holdPeriodUnit");
    public static QName PROP_HOLD_PERIOD_VALUE = QName.createQName(RM_URI, "holdPeriodValue");
    public static QName PROP_CUTOFF_ON_SUPERSEDED = QName.createQName(RM_URI, "cutoffOnSuperseded");
    public static QName PROP_CUTOFF_ON_OBSOLETE = QName.createQName(RM_URI, "cutoffOnObsolete");
    public static QName PROP_VITAL_RECORD_INDICATOR = QName.createQName(RM_URI, "vitalRecordIndicator");
    public static QName PROP_VITAL_RECORD_REVIEW_PERIOD_UNIT = QName.createQName(RM_URI, "vitalRecordReviewPeriodUnit");
    public static QName PROP_VITAL_RECORD_REVIEW_PERIOD_VALUE = QName.createQName(RM_URI, "vitalRecordReviewPeriodValue");
    public static QName PROP_PROCESS_CUTOFF = QName.createQName(RM_URI, "processCutoff");
    public static QName PROP_EVENT_TRIGGER = QName.createQName(RM_URI, "eventTrigger");
    public static QName PROP_CUTOFF_PERIOD_UNIT = QName.createQName(RM_URI, "cutoffPeriodUnit");
    public static QName PROP_CUTOFF_PERIOD_VALUE = QName.createQName(RM_URI, "cutoffPeriodValue");
    
    // Transfer instructions
    public static QName ASPECT_TRANSFER_INSTRUCTIONS = QName.createQName(RM_URI, "transferInstructions");
    public static QName PROP_TRANSFER_IMMEDIATELY = QName.createQName(RM_URI, "transferImmediately");
    public static QName PROP_TRANSFER_LOCATION = QName.createQName(RM_URI, "transferLocation");
    
    // Destroy instructions
    public static QName ASPECT_DESTROY_INSTRUCTIONS = QName.createQName(RM_URI, "destroyInstructions");
    public static QName PROP_DESTROY_IMMEDIATELY = QName.createQName(RM_URI, "destroyImmediately");
    
    // Accession instructions
    public static QName ASPECT_ACCESSION_INSTRUCTIONS = QName.createQName(RM_URI, "accessionInstructions");
    public static QName PROP_ACCESSION_IMMEDIATELY = QName.createQName(RM_URI, "accessionImmediately");
	
	// Record aspect	
	public static QName ASPECT_RECORD = QName.createQName(RM_URI, "record");
    public static QName ASSOC_FILE_PLAN = QName.createQName(RM_URI, "filePlan");
    public static QName PROP_RECORD_CATEGORY_IDENTIFIER = QName.createQName(RM_URI, "recordCategoryIdentifier");
    public static QName PROP_SUBJECT = QName.createQName(RM_URI, "subject");
    
    // Vital record aspect
	public static QName ASPECT_VITAL_RECORD = QName.createQName(RM_URI, "vitalrecord");
    public static QName PROP_NEXT_REVIEW_DATE = QName.createQName(RM_URI, "nextReviewDate");
    
    // Cutoff schedule aspect
	public static QName ASPECT_CUTOFF_SCHEDULE = QName.createQName(RM_URI, "cutoffSchedule");
    public static QName PROP_CUTOFF_DATE_TIME = QName.createQName(RM_URI, "cutoffDateTime");    
    public static QName PROP_CUTOFF_EVENT = QName.createQName(RM_URI, "cutoffEvent");
    
    // Obsolete aspect
    public static QName ASPECT_OBSOLETE = QName.createQName(RM_URI, "obsolete");
    
    // Superseded aspect
    public static QName ASPECT_SUPERSEDED = QName.createQName(RM_URI, "superseded");
    
    // Cutoff aspect
    public static QName ASPECT_CUTOFF = QName.createQName(RM_URI, "cutoff");
    
    // Held aspect
    public static QName ASPECT_HELD = QName.createQName(RM_URI, "held");
    public static QName PROP_HOLD_UNTIL_EVENT = QName.createQName(RM_URI, "holdUntilEvent");    
    public static QName PROP_FROZEN = QName.createQName(RM_URI, "frozen");
    public static QName PROP_HOLD_UNTIL = QName.createQName(RM_URI, "holdUntil");
    
    // Transfered aspect
    public static QName ASPECT_TRANSFERED = QName.createQName(RM_URI, "transfered");
	
	// Categories
    public static NodeRef CAT_DATEPERIOD_NONE = new NodeRef(SPACES_STORE, "rm:datePeriod-0");
    public static NodeRef CAT_DATEPERIOD_TBD = new NodeRef(SPACES_STORE, "rm:datePeriod-1");
    public static NodeRef CAT_DATEPERIOD_DAILY = new NodeRef(SPACES_STORE, "rm:datePeriod-2");
    public static NodeRef CAT_DATEPERIOD_WEEKLY = new NodeRef(SPACES_STORE, "rm:datePeriod-3");
    public static NodeRef CAT_DATEPERIOD_MONTHLY = new NodeRef(SPACES_STORE, "rm:datePeriod-4");
    public static NodeRef CAT_DATEPERIOD_ANNUALLY = new NodeRef(SPACES_STORE, "rm:datePeriod-5");
    public static NodeRef CAT_DATEPERIOD_MONTHEND = new NodeRef(SPACES_STORE, "rm:datePeriod-6");
    public static NodeRef CAT_DATEPERIOD_QUATEREND = new NodeRef(SPACES_STORE, "rm:datePeriod-7");
    public static NodeRef CAT_DATEPERIOD_YEAREND = new NodeRef(SPACES_STORE, "rm:datePeriod-8");
    public static NodeRef CAT_DATEPERIOD_FINYEAREND = new NodeRef(SPACES_STORE, "rm:datePeriod-9");
    
    // Records manager group
    public static String RM_GROUP = "GROUP_Record Managers";
	
}

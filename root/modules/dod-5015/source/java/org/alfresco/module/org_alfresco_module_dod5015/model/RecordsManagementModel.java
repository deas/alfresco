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
package org.alfresco.module.org_alfresco_module_dod5015.model;

import org.alfresco.service.namespace.QName;

/**
 * Helper class containing records management qualified names
 * 
 * @author Roy Wetherall
 */
public interface RecordsManagementModel extends RecordsManagementCustomModel
{	
	// Namespace details
	public static String RM_URI = "http://www.alfresco.org/model/recordsmanagement/1.0";
	public static String RM_PREFIX = "rma";
    
    // Model
    public static QName RM_MODEL = QName.createQName(RM_URI, "recordsmanagement");
    
    // RM Site
    public static QName TYPE_RM_SITE = QName.createQName(RM_URI, "rmsite");
    
    // Caveat config
    public static QName TYPE_CAVEAT_CONFIG = QName.createQName(RM_URI, "caveatConfig");
    
    public static QName ASPECT_CAVEAT_CONFIG_ROOT = QName.createQName(RM_URI, "caveatConfigRoot");
    public static QName ASSOC_CAVEAT_CONFIG = QName.createQName(RM_URI, "caveatConfigAssoc");
    
    // Email config
    public static QName TYPE_EMAIL_CONFIG = QName.createQName(RM_URI, "emailConfig");    
    public static QName ASPECT_EMAIL_CONFIG_ROOT = QName.createQName(RM_URI, "emailConfigRoot");
    public static QName ASSOC_EMAIL_CONFIG = QName.createQName(RM_URI, "emailConfigAssoc");

    // Records management container
    public static QName TYPE_RECORDS_MANAGEMENT_CONTAINER = QName.createQName(RM_URI, "recordsManagementContainer");
    
    // Records management root container
    public static QName TYPE_RECORDS_MANAGEMENT_ROOT_CONTAINER = QName.createQName(RM_URI, "recordsManagementRootContainer");
    
    // Disposition instructions aspect
    public static QName ASPECT_SCHEDULED = QName.createQName(RM_URI, "scheduled");
    public static QName ASSOC_DISPOSITION_SCHEDULE = QName.createQName(RM_URI, "dispositionSchedule");
    
    // Disposition definition type
    public static QName TYPE_DISPOSITION_SCHEDULE = QName.createQName(RM_URI, "dispositionSchedule");
    public static QName PROP_DISPOSITION_AUTHORITY = QName.createQName(RM_URI, "dispositionAuthority");
    public static QName PROP_DISPOSITION_INSTRUCTIONS = QName.createQName(RM_URI, "dispositionInstructions");
    public static QName PROP_RECORD_LEVEL_DISPOSITION = QName.createQName(RM_URI, "recordLevelDisposition");
    public static QName ASSOC_DISPOSITION_ACTION_DEFINITIONS = QName.createQName(RM_URI, "dispositionActionDefinitions");    
    
    // Disposition action type
    public static QName TYPE_DISPOSITION_ACTION_DEFINITION = QName.createQName(RM_URI, "dispositionActionDefinition");
    public static QName PROP_DISPOSITION_ACTION_NAME = QName.createQName(RM_URI, "dispositionActionName");
    public static QName PROP_DISPOSITION_DESCRIPTION = QName.createQName(RM_URI, "dispositionDescription");
    public static QName PROP_DISPOSITION_PERIOD = QName.createQName(RM_URI, "dispositionPeriod");
    public static QName PROP_DISPOSITION_PERIOD_PROPERTY = QName.createQName(RM_URI, "dispositionPeriodProperty");
    public static QName PROP_DISPOSITION_EVENT = QName.createQName(RM_URI, "dispositionEvent");
    public static QName PROP_DISPOSITION_EVENT_COMBINATION = QName.createQName(RM_URI, "dispositionEventCombination");
    public static QName PROP_DISPOSITION_LOCATION = QName.createQName(RM_URI, "dispositionLocation");
    
    // Records folder
    public static QName TYPE_RECORD_FOLDER = QName.createQName(RM_URI, "recordFolder");
    public static QName PROP_IS_CLOSED = QName.createQName(RM_URI, "isClosed");
    
    // Declared record aspect
    public static QName ASPECT_DECLARED_RECORD = QName.createQName(RM_URI, "declaredRecord");
    public static QName PROP_DECLARED_AT = QName.createQName(RM_URI, "declaredAt");
    public static QName PROP_DECLARED_BY = QName.createQName(RM_URI, "declaredBy");
    
    // Record aspect
    public static QName ASPECT_RECORD = QName.createQName(RM_URI, "record");
    public static QName PROP_DATE_FILED = QName.createQName(RM_URI, "dateFiled");
    public static QName PROP_ORIGINATOR = QName.createQName(RM_URI, "originator");
    public static QName PROP_ORIGINATING_ORGANIZATION = QName.createQName(RM_URI, "originatingOrganization");
    public static QName PROP_PUBLICATION_DATE = QName.createQName(RM_URI, "publicationDate");
    public static QName PROP_MEDIA_TYPE = QName.createQName(RM_URI, "mediaType");
    public static QName PROP_FORMAT = QName.createQName(RM_URI, "format");
    public static QName PROP_DATE_RECEIVED = QName.createQName(RM_URI, "dateReceived");  
    
    // Common record details
    public static QName PROP_LOCATION = QName.createQName(RM_URI, "location");
    
    // Fileable aspect
    public static QName ASPECT_FILABLE = QName.createQName(RM_URI, "fileable");
    
    // Record component identifier aspect
    public static QName ASPECT_RECORD_COMPONENT_ID = QName.createQName(RM_URI, "recordComponentIdentifier");
    public static QName PROP_IDENTIFIER = QName.createQName(RM_URI, "identifier");
    public static QName PROP_DB_UNIQUENESS_ID = QName.createQName(RM_URI, "dbUniquenessId");
    
    // Vital record definition aspect
    public static QName ASPECT_VITAL_RECORD_DEFINITION = QName.createQName(RM_URI, "vitalRecordDefinition");
    public static QName PROP_VITAL_RECORD_INDICATOR = QName.createQName(RM_URI, "vitalRecordIndicator");
    public static QName PROP_REVIEW_PERIOD = QName.createQName(RM_URI, "reviewPeriod");
     
    // Vital record aspect
    public static QName ASPECT_VITAL_RECORD = QName.createQName(RM_URI, "vitalRecord");
    public static QName PROP_REVIEW_AS_OF = QName.createQName(RM_URI, "reviewAsOf");
    public static QName PROP_NOTIFICATION_ISSUED = QName.createQName(RM_URI, "notificationIssued");
    
    // Cut off aspect
    public static QName ASPECT_CUT_OFF = QName.createQName(RM_URI, "cutOff");
    public static QName PROP_CUT_OFF_DATE = QName.createQName(RM_URI, "cutOffDate");
    
    // Transferred aspect
    public static QName ASPECT_TRANSFERRED = QName.createQName(RM_URI, "transferred");
    
    // Ascended aspect
    public static QName ASPECT_ASCENDED = QName.createQName(RM_URI, "ascended");
    
    // Disposition schedule aspect
    public static QName ASPECT_DISPOSITION_LIFECYCLE = QName.createQName(RM_URI, "dispositionLifecycle");
    public static QName ASSOC_NEXT_DISPOSITION_ACTION = QName.createQName(RM_URI, "nextDispositionAction");
    public static QName ASSOC_DISPOSITION_ACTION_HISTORY = QName.createQName(RM_URI, "dispositionActionHistory");
    
    // Disposition action type
    public static QName TYPE_DISPOSITION_ACTION = QName.createQName(RM_URI, "dispositionAction");
    public static QName PROP_DISPOSITION_ACTION_ID = QName.createQName(RM_URI, "dispositionActionId");
    public static QName PROP_DISPOSITION_ACTION = QName.createQName(RM_URI, "dispositionAction");
    public static QName PROP_DISPOSITION_AS_OF = QName.createQName(RM_URI, "dispositionAsOf");
    public static QName PROP_DISPOSITION_EVENTS_ELIGIBLE = QName.createQName(RM_URI, "dispositionEventsEligible");
    public static QName PROP_DISPOSITION_ACTION_STARTED_AT = QName.createQName(RM_URI, "dispositionActionStartedAt");
    public static QName PROP_DISPOSITION_ACTION_STARTED_BY = QName.createQName(RM_URI, "dispositionActionStartedBy");
    public static QName PROP_DISPOSITION_ACTION_COMPLETED_AT = QName.createQName(RM_URI, "dispositionActionCompletedAt");
    public static QName PROP_DISPOSITION_ACTION_COMPLETED_BY = QName.createQName(RM_URI, "dispositionActionCompletedBy");
    public static QName ASSOC_EVENT_EXECUTIONS = QName.createQName(RM_URI, "eventExecutions");
    
    // Event execution type
    public static QName TYPE_EVENT_EXECUTION = QName.createQName(RM_URI, "eventExecution");
    public static QName PROP_EVENT_EXECUTION_NAME = QName.createQName(RM_URI, "eventExecutionName");
    public static QName PROP_EVENT_EXECUTION_AUTOMATIC = QName.createQName(RM_URI, "eventExecutionAutomatic");
    public static QName PROP_EVENT_EXECUTION_COMPLETE = QName.createQName(RM_URI, "eventExecutionComplete");
    public static QName PROP_EVENT_EXECUTION_COMPLETED_BY = QName.createQName(RM_URI, "eventExecutionCompletedBy");
    public static QName PROP_EVENT_EXECUTION_COMPLETED_AT = QName.createQName(RM_URI, "eventExecutionCompletedAt");
    
    // Custom RM data aspect
    public static QName ASPECT_CUSTOM_RM_DATA = QName.createQName(RM_URI, "customRMData");
    
    // marker aspect on all RM objercts (except caveat root)
    public static QName ASPECT_FILE_PLAN_COMPONENT = QName.createQName(RM_URI, "filePlanComponent");
    public static QName PROP_ROOT_NODEREF = QName.createQName(RM_URI, "rootNodeRef");
	
    // Non-electronic document
	public static QName TYPE_NON_ELECTRONIC_DOCUMENT = QName.createQName(RM_URI, "nonElectronicDocument");
	
	// Records management root aspect
	public static QName ASPECT_RECORDS_MANAGEMENT_ROOT = QName.createQName(RM_URI, "recordsManagementRoot");
    public static QName ASSOC_HOLDS = QName.createQName(RM_URI, "holds");
	public static QName ASSOC_TRANSFERS = QName.createQName(RM_URI, "transfers");
	
	// Hold type
	public static QName TYPE_HOLD = QName.createQName(RM_URI, "hold");
	public static QName PROP_HOLD_REASON = QName.createQName(RM_URI, "holdReason");
	public static QName ASSOC_FROZEN_RECORDS = QName.createQName(RM_URI, "frozenRecords");
	
	// Record meta data aspect
	public static QName ASPECT_RECORD_META_DATA = QName.createQName(RM_URI, "recordMetaData");
	
	// Frozen aspect
	public static QName ASPECT_FROZEN = QName.createQName(RM_URI, "frozen");
	public static QName PROP_FROZEN_AT = QName.createQName(RM_URI, "frozenAt");
	public static QName PROP_FROZEN_BY = QName.createQName(RM_URI, "frozenBy");
	
	// Transfer aspect
	public static QName TYPE_TRANSFER = QName.createQName(RM_URI, "transfer");
	public static QName PROP_TRANSFER_ACCESSION_INDICATOR = QName.createQName(RM_URI, "transferAccessionIndicator");
	public static QName PROP_TRANSFER_PDF_INDICATOR = QName.createQName(RM_URI, "transferPDFIndicator");
	public static QName PROP_TRANSFER_LOCATION = QName.createQName(RM_URI, "transferLocation");
    public static QName ASSOC_TRANSFERRED = QName.createQName(RM_URI, "transferred");
    
    // Versioned record aspect
    public static QName ASPECT_VERSIONED_RECORD = QName.createQName(RM_URI, "versionedRecord");
    
    // Unpublished update aspect
    public static QName ASPECT_UNPUBLISHED_UPDATE = QName.createQName(RM_URI, "unpublishedUpdate");
    public static QName PROP_UNPUBLISHED_UPDATE = QName.createQName(RM_URI, "unpublishedUpdate");
    public static QName PROP_UPDATE_TO = QName.createQName(RM_URI, "updateTo");
    public static QName PROP_UPDATED_PROPERTIES = QName.createQName(RM_URI, "updatedProperties");
    public static QName PROP_PUBLISH_IN_PROGRESS = QName.createQName(RM_URI, "publishInProgress");
    public static String UPDATE_TO_DISPOSITION_ACTION_DEFINITION = "dispositionActionDefinition"; 
}

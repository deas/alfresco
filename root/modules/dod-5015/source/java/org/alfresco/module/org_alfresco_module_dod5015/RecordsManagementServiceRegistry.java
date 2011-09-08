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

import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementActionService;
import org.alfresco.module.org_alfresco_module_dod5015.audit.RecordsManagementAuditService;
import org.alfresco.module.org_alfresco_module_dod5015.disposition.DispositionService;
import org.alfresco.module.org_alfresco_module_dod5015.event.RecordsManagementEventService;
import org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService;
import org.alfresco.service.NotAuditable;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Records management service registry
 * 
 * @author Roy Wetherall
 */
public interface RecordsManagementServiceRegistry extends ServiceRegistry
{
    /** Service QName constants */
    static final QName RECORDS_MANAGEMENT_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "RecordsManagementService");
    static final QName DISPOSITION_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "DispositionService");
    static final QName RECORDS_MANAGEMENT_ADMIN_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "RecordsManagementAdminService");
    static final QName RECORDS_MANAGEMENT_ACTION_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "RecordsManagementActionService");
    static final QName RECORDS_MANAGEMENT_EVENT_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "RecordsManagementEventService");
    static final QName RECORDS_MANAGEMENT_SECURITY_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "RecordsManagementSecurityService");
    static final QName RECORDS_MANAGEMENT_AUDIT_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "RecordsManagementAuditService");
    
    /**
     * @return  records management service
     */
    @NotAuditable
    RecordsManagementService getRecordsManagementService();
    
    /**
     * @return disposition service
     */
    @NotAuditable
    DispositionService getDispositionService();
    
    /**
     * @return  records management admin service
     */
    @NotAuditable
    RecordsManagementAdminService getRecordsManagementAdminService();
    
    /**
     * @return  records management action service
     */
    @NotAuditable
    RecordsManagementActionService getRecordsManagementActionService();
    
    /**
     * @return  records management event service
     */
    @NotAuditable
    RecordsManagementEventService getRecordsManagementEventService();
    
    /**
     * @return  records management security service
     */
    @NotAuditable
    RecordsManagementSecurityService getRecordsManagementSecurityService();
    
    /**
     * @return  records management audit service
     */
    @NotAuditable
    RecordsManagementAuditService getRecordsManagementAuditService();
}

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
package org.alfresco.module.org_alfresco_module_dod5015.capability;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.ConfigAttribute;
import net.sf.acegisecurity.ConfigAttributeDefinition;
import net.sf.acegisecurity.vote.AccessDecisionVoter;

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementAction;
import org.alfresco.module.org_alfresco_module_dod5015.capability.group.CreateCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.group.DeclareCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.group.DeleteCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.group.UpdateCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.group.UpdatePropertiesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.group.WriteContentCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.AccessAuditCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.AddModifyEventDatesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.ApproveRecordsScheduledForCutoffCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.AttachRulesToMetadataPropertiesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.AuthorizeAllTransfersCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.AuthorizeNominatedTransfersCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.ChangeOrDeleteReferencesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CloseFoldersCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CreateAndAssociateSelectionListsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CreateModifyDestroyClassificationGuidesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CreateModifyDestroyEventsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CreateModifyDestroyFileplanMetadataCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CreateModifyDestroyFileplanTypesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CreateModifyDestroyFoldersCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CreateModifyDestroyRecordTypesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CreateModifyDestroyReferenceTypesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CreateModifyDestroyRolesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CreateModifyDestroyTimeframesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CreateModifyDestroyUsersAndGroupsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CreateModifyRecordsInCuttoffFoldersCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CycleVitalRecordsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.DeclareAuditAsRecordCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.DeclareRecordsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.DeclareRecordsInClosedFoldersCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.DeleteAuditCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.DeleteLinksCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.DeleteRecordsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.DestroyRecordsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.DestroyRecordsScheduledForDestructionCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.DisplayRightsReportCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.EditDeclaredRecordMetadataCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.EditNonRecordMetadataCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.EditRecordMetadataCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.EditSelectionListsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.EnableDisableAuditByTypesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.ExportAuditCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.ExtendRetentionPeriodOrFreezeCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.FileRecordsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.MakeOptionalPropertiesMandatoryCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.ManageAccessControlsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.ManageAccessRightsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.ManuallyChangeDispositionDatesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.MapClassificationGuideMetadataCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.MapEmailMetadataCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.MoveRecordsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.PasswordControlCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.PlanningReviewCyclesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.ReOpenFoldersCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.SelectAuditMetadataCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.TriggerAnEventCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.UndeclareRecordsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.UnfreezeCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.UpdateClassificationDatesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.UpdateExemptionCategoriesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.UpdateTriggerDatesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.UpdateVitalRecordCycleInformationCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.UpgradeDowngradeAndDeclassifyRecordsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.ViewRecordsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.ViewUpdateReasonsForFreezeCapability;
import org.alfresco.module.org_alfresco_module_dod5015.caveat.RMCaveatConfigComponent;
import org.alfresco.module.org_alfresco_module_dod5015.disposition.DispositionService;
import org.alfresco.module.org_alfresco_module_dod5015.model.RecordsManagementModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.permissions.impl.SimplePermissionReference;
import org.alfresco.repo.security.permissions.impl.acegi.ACLEntryVoterException;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.EqualsHelper;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class RMEntryVoter implements AccessDecisionVoter, InitializingBean, ApplicationContextAware
{
    private static Log logger = LogFactory.getLog(RMEntryVoter.class);

    private static final String RM = "RM";

    private static final String RM_ALLOW = "RM_ALLOW";

    private static final String RM_DENY = "RM_DENY";

    private static final String RM_CAP = "RM_CAP";

    private static final String RM_ABSTAIN = "RM_ABSTAIN";

    private static final String RM_QUERY = "RM_QUERY";

    NamespacePrefixResolver nspr;

    private NodeService nodeService;

    private PermissionService permissionService;

    private RMCaveatConfigComponent caveatConfigComponent;

    private DictionaryService dictionaryService;

    private RecordsManagementService recordsManagementService;
    
    private DispositionService dispositionService;
    
    private SearchService searchService;

    private static HashMap<String, Policy> policies = new HashMap<String, Policy>();

    private HashSet<QName> protectedProperties = new HashSet<QName>();

    private HashSet<QName> protectedAspects = new HashSet<QName>();

    private static HashMap<String, Capability> capabilities = new HashMap<String, Capability>();

    //

    private ViewRecordsCapability viewRecordsCapability;

    private FileRecordsCapability fileRecordsCapability;

    private DeclareRecordsCapability declareRecordsCapability;

    private CreateModifyDestroyFoldersCapability createModifyDestroyFoldersCapability;

    private EditRecordMetadataCapability editRecordMetadataCapability;

    private EditNonRecordMetadataCapability editNonRecordMetadataCapability;

    private AddModifyEventDatesCapability addModifyEventDatesCapability;

    private CloseFoldersCapability closeFoldersCapability;

    private DeclareRecordsInClosedFoldersCapability declareRecordsInClosedFoldersCapability;

    private ReOpenFoldersCapability reOpenFoldersCapability;

    public CycleVitalRecordsCapability cycleVitalRecordsCapability;

    public PlanningReviewCyclesCapability planningReviewCyclesCapability;

    public UpdateTriggerDatesCapability updateTriggerDatesCapability;

    public CreateModifyDestroyEventsCapability createModifyDestroyEventsCapability;

    public ManageAccessRightsCapability manageAccessRightsCapability;

    public MoveRecordsCapability moveRecordsCapability;

    public ChangeOrDeleteReferencesCapability changeOrDeleteReferencesCapability;

    public DeleteLinksCapability deleteLinksCapability;

    public EditDeclaredRecordMetadataCapability editDeclaredRecordMetadataCapability;

    public ManuallyChangeDispositionDatesCapability manuallyChangeDispositionDatesCapability;

    public ApproveRecordsScheduledForCutoffCapability approveRecordsScheduledForCutoffCapability;

    public CreateModifyRecordsInCuttoffFoldersCapability createModifyRecordsInCuttoffFoldersCapability;

    public ExtendRetentionPeriodOrFreezeCapability extendRetentionPeriodOrFreezeCapability;

    public UnfreezeCapability unfreezeCapability;

    public ViewUpdateReasonsForFreezeCapability viewUpdateReasonsForFreezeCapability;

    public DestroyRecordsScheduledForDestructionCapability destroyRecordsScheduledForDestructionCapability;

    public DestroyRecordsCapability destroyRecordsCapability;

    public UpdateVitalRecordCycleInformationCapability updateVitalRecordCycleInformationCapability;

    public UndeclareRecordsCapability undeclareRecordsCapability;

    public DeclareAuditAsRecordCapability declareAuditAsRecordCapability;

    public DeleteAuditCapability deleteAuditCapability;

    public CreateModifyDestroyTimeframesCapability createModifyDestroyTimeframesCapability;

    public AuthorizeNominatedTransfersCapability authorizeNominatedTransfersCapability;

    public EditSelectionListsCapability editSelectionListsCapability;

    public AuthorizeAllTransfersCapability authorizeAllTransfersCapability;

    public CreateModifyDestroyFileplanMetadataCapability createModifyDestroyFileplanMetadataCapability;

    public CreateAndAssociateSelectionListsCapability createAndAssociateSelectionListsCapability;

    public AttachRulesToMetadataPropertiesCapability attachRulesToMetadataPropertiesCapability;

    public CreateModifyDestroyFileplanTypesCapability createModifyDestroyFileplanTypesCapability;

    public CreateModifyDestroyRecordTypesCapability createModifyDestroyRecordTypesCapability;

    public MakeOptionalPropertiesMandatoryCapability makeOptionalPropertiesMandatoryCapability;

    public MapEmailMetadataCapability mapEmailMetadataCapability;

    public DeleteRecordsCapability deleteRecordsCapability;

    public CreateModifyDestroyRolesCapability createModifyDestroyRolesCapability;

    public CreateModifyDestroyUsersAndGroupsCapability createModifyDestroyUsersAndGroupsCapability;

    public PasswordControlCapability passwordControlCapability;

    public EnableDisableAuditByTypesCapability enableDisableAuditByTypesCapability;

    public SelectAuditMetadataCapability selectAuditMetadataCapability;

    public DisplayRightsReportCapability displayRightsReportCapability;

    public AccessAuditCapability accessAuditCapability;

    public ExportAuditCapability exportAuditCapability;

    public CreateModifyDestroyReferenceTypesCapability createModifyDestroyReferenceTypesCapability;

    public UpdateClassificationDatesCapability updateClassificationDatesCapability;

    public CreateModifyDestroyClassificationGuidesCapability createModifyDestroyClassificationGuidesCapability;

    public UpgradeDowngradeAndDeclassifyRecordsCapability upgradeDowngradeAndDeclassifyRecordsCapability;

    public UpdateExemptionCategoriesCapability updateExemptionCategoriesCapability;

    public MapClassificationGuideMetadataCapability mapClassificationGuideMetadataCapability;

    public ManageAccessControlsCapability manageAccessControlsCapability;

    public TriggerAnEventCapability triggerAnEventCapability;

    //

    public CreateCapability createCapability;

    public DeleteCapability deleteCapability;

    public UpdateCapability updateCapability;

    public UpdatePropertiesCapability updatePropertiesCapability;

    public DeclareCapability declareCapability;

    public WriteContentCapability writeContentCapability;

    static
    {
        policies.put("Read", new ReadPolicy());
        policies.put("Create", new CreatePolicy());
        policies.put("Move", new MovePolicy());
        policies.put("Update", new UpdatePolicy());
        policies.put("Delete", new DeletePolicy());
        policies.put("UpdateProperties", new UpdatePropertiesPolicy());
        policies.put("Assoc", new AssocPolicy());
        policies.put("WriteContent", new WriteContentPolicy());
        policies.put("Capability", new CapabilityPolicy());
        policies.put("Declare", new DeclarePolicy());
        policies.put("ReadProperty", new ReadPropertyPolicy());

        // restrictedProperties.put(RecordsManagementModel.PROP_IS_CLOSED, value)

    }

    public void init()
    {

    }

    /**
     * Set the permission service
     * 
     * @param permissionService
     */
    public void setPermissionService(PermissionService permissionService)
    {
        this.permissionService = permissionService;
    }

    /**
     * Set the node service
     * 
     * @param nodeService
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * Get the search service
     * @return  search service
     */
    public SearchService getSearchService()
    {
        if (searchService == null)
        {
            searchService = (SearchService)applicationContext.getBean("SearchService");
        }
        return searchService;
    }
    
    /**
     * Set the name space prefix resolver
     * 
     * @param nspr
     */
    public void setNamespacePrefixResolver(NamespacePrefixResolver nspr)
    {
        this.nspr = nspr;
    }

    public void setCaveatConfigComponent(RMCaveatConfigComponent caveatConfigComponent)
    {
        this.caveatConfigComponent = caveatConfigComponent;
    }

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public ViewRecordsCapability getViewRecordsCapability()
    {
        return viewRecordsCapability;
    }

    public void setViewRecordsCapability(ViewRecordsCapability viewRecordsCapability)
    {
        this.viewRecordsCapability = viewRecordsCapability;
        capabilities.put(viewRecordsCapability.getName(), viewRecordsCapability);
    }

    public FileRecordsCapability getFileRecordsCapability()
    {
        return fileRecordsCapability;
    }

    public void setFileRecordsCapability(FileRecordsCapability fileRecordsCapability)
    {
        this.fileRecordsCapability = fileRecordsCapability;
        capabilities.put(fileRecordsCapability.getName(), fileRecordsCapability);
    }

    public DeclareRecordsCapability getDeclareRecordsCapability()
    {
        return declareRecordsCapability;
    }

    public void setDeclareRecordsCapability(DeclareRecordsCapability declareRecordsCapability)
    {
        this.declareRecordsCapability = declareRecordsCapability;
        capabilities.put(declareRecordsCapability.getName(), declareRecordsCapability);
    }

    public CreateModifyDestroyFoldersCapability getCreateModifyDestroyFoldersCapability()
    {
        return createModifyDestroyFoldersCapability;
    }

    public void setCreateModifyDestroyFoldersCapability(CreateModifyDestroyFoldersCapability createModifyDestroyFoldersCapability)
    {
        this.createModifyDestroyFoldersCapability = createModifyDestroyFoldersCapability;
        capabilities.put(createModifyDestroyFoldersCapability.getName(), createModifyDestroyFoldersCapability);
    }

    public EditRecordMetadataCapability getEditRecordMetadataCapability()
    {
        return editRecordMetadataCapability;
    }

    public void setEditRecordMetadataCapability(EditRecordMetadataCapability editRecordMetadataCapability)
    {
        this.editRecordMetadataCapability = editRecordMetadataCapability;
        capabilities.put(editRecordMetadataCapability.getName(), editRecordMetadataCapability);
    }

    public EditNonRecordMetadataCapability getEditNonRecordMetadataCapability()
    {
        return editNonRecordMetadataCapability;
    }

    public void setEditNonRecordMetadataCapability(EditNonRecordMetadataCapability editNonRecordMetadataCapability)
    {
        this.editNonRecordMetadataCapability = editNonRecordMetadataCapability;
        capabilities.put(editNonRecordMetadataCapability.getName(), editNonRecordMetadataCapability);
    }

    public AddModifyEventDatesCapability getAddModifyEventDatesCapability()
    {
        return addModifyEventDatesCapability;
    }

    public void setAddModifyEventDatesCapability(AddModifyEventDatesCapability addModifyEventDatesCapability)
    {
        this.addModifyEventDatesCapability = addModifyEventDatesCapability;
        capabilities.put(addModifyEventDatesCapability.getName(), addModifyEventDatesCapability);
    }

    public CloseFoldersCapability getCloseFoldersCapability()
    {
        return closeFoldersCapability;
    }

    public void setCloseFoldersCapability(CloseFoldersCapability closeFoldersCapability)
    {
        this.closeFoldersCapability = closeFoldersCapability;
        capabilities.put(closeFoldersCapability.getName(), closeFoldersCapability);
    }

    public DeclareRecordsInClosedFoldersCapability getDeclareRecordsInClosedFoldersCapability()
    {
        return declareRecordsInClosedFoldersCapability;
    }

    public void setDeclareRecordsInClosedFoldersCapability(DeclareRecordsInClosedFoldersCapability declareRecordsInClosedFoldersCapability)
    {
        this.declareRecordsInClosedFoldersCapability = declareRecordsInClosedFoldersCapability;
        capabilities.put(declareRecordsInClosedFoldersCapability.getName(), declareRecordsInClosedFoldersCapability);
    }

    public ReOpenFoldersCapability getReOpenFoldersCapability()
    {
        return reOpenFoldersCapability;
    }

    public void setReOpenFoldersCapability(ReOpenFoldersCapability reOpenFoldersCapability)
    {
        this.reOpenFoldersCapability = reOpenFoldersCapability;
        capabilities.put(reOpenFoldersCapability.getName(), reOpenFoldersCapability);
    }

    public CycleVitalRecordsCapability getCycleVitalRecordsCapability()
    {
        return cycleVitalRecordsCapability;
    }

    public void setCycleVitalRecordsCapability(CycleVitalRecordsCapability cycleVitalRecordsCapability)
    {
        this.cycleVitalRecordsCapability = cycleVitalRecordsCapability;
        capabilities.put(cycleVitalRecordsCapability.getName(), cycleVitalRecordsCapability);
    }

    public PlanningReviewCyclesCapability getPlanningReviewCyclesCapability()
    {
        return planningReviewCyclesCapability;
    }

    public void setPlanningReviewCyclesCapability(PlanningReviewCyclesCapability planningReviewCyclesCapability)
    {
        this.planningReviewCyclesCapability = planningReviewCyclesCapability;
        capabilities.put(planningReviewCyclesCapability.getName(), planningReviewCyclesCapability);
    }

    public UpdateTriggerDatesCapability getUpdateTriggerDatesCapability()
    {
        return updateTriggerDatesCapability;
    }

    public void setUpdateTriggerDatesCapability(UpdateTriggerDatesCapability updateTriggerDatesCapability)
    {
        this.updateTriggerDatesCapability = updateTriggerDatesCapability;
        capabilities.put(updateTriggerDatesCapability.getName(), updateTriggerDatesCapability);
    }

    public CreateModifyDestroyEventsCapability getCreateModifyDestroyEventsCapability()
    {
        return createModifyDestroyEventsCapability;
    }

    public void setCreateModifyDestroyEventsCapability(CreateModifyDestroyEventsCapability createModifyDestroyEventsCapability)
    {
        this.createModifyDestroyEventsCapability = createModifyDestroyEventsCapability;
        capabilities.put(createModifyDestroyEventsCapability.getName(), createModifyDestroyEventsCapability);
    }

    public ManageAccessRightsCapability getManageAccessRightsCapability()
    {
        return manageAccessRightsCapability;
    }

    public void setManageAccessRightsCapability(ManageAccessRightsCapability manageAccessRightsCapability)
    {
        this.manageAccessRightsCapability = manageAccessRightsCapability;
        capabilities.put(manageAccessRightsCapability.getName(), manageAccessRightsCapability);
    }

    public MoveRecordsCapability getMoveRecordsCapability()
    {
        return moveRecordsCapability;
    }

    public void setMoveRecordsCapability(MoveRecordsCapability moveRecordsCapability)
    {
        this.moveRecordsCapability = moveRecordsCapability;
        capabilities.put(moveRecordsCapability.getName(), moveRecordsCapability);
    }

    public ChangeOrDeleteReferencesCapability getChangeOrDeleteReferencesCapability()
    {
        return changeOrDeleteReferencesCapability;
    }

    public void setChangeOrDeleteReferencesCapability(ChangeOrDeleteReferencesCapability changeOrDeleteReferencesCapability)
    {
        this.changeOrDeleteReferencesCapability = changeOrDeleteReferencesCapability;
        capabilities.put(changeOrDeleteReferencesCapability.getName(), changeOrDeleteReferencesCapability);
    }

    public DeleteLinksCapability getDeleteLinksCapability()
    {
        return deleteLinksCapability;
    }

    public void setDeleteLinksCapability(DeleteLinksCapability deleteLinksCapability)
    {
        this.deleteLinksCapability = deleteLinksCapability;
        capabilities.put(deleteLinksCapability.getName(), deleteLinksCapability);
    }

    public EditDeclaredRecordMetadataCapability getEditDeclaredRecordMetadataCapability()
    {
        return editDeclaredRecordMetadataCapability;
    }

    public void setEditDeclaredRecordMetadataCapability(EditDeclaredRecordMetadataCapability editDeclaredRecordMetadataCapability)
    {
        this.editDeclaredRecordMetadataCapability = editDeclaredRecordMetadataCapability;
        capabilities.put(editDeclaredRecordMetadataCapability.getName(), editDeclaredRecordMetadataCapability);
    }

    public ManuallyChangeDispositionDatesCapability getManuallyChangeDispositionDatesCapability()
    {
        return manuallyChangeDispositionDatesCapability;
    }

    public void setManuallyChangeDispositionDatesCapability(ManuallyChangeDispositionDatesCapability manuallyChangeDispositionDatesCapability)
    {
        this.manuallyChangeDispositionDatesCapability = manuallyChangeDispositionDatesCapability;
        capabilities.put(manuallyChangeDispositionDatesCapability.getName(), manuallyChangeDispositionDatesCapability);
    }

    public ApproveRecordsScheduledForCutoffCapability getApproveRecordsScheduledForCutoffCapability()
    {
        return approveRecordsScheduledForCutoffCapability;
    }

    public void setApproveRecordsScheduledForCutoffCapability(ApproveRecordsScheduledForCutoffCapability approveRecordsScheduledForCutoffCapability)
    {
        this.approveRecordsScheduledForCutoffCapability = approveRecordsScheduledForCutoffCapability;
        capabilities.put(approveRecordsScheduledForCutoffCapability.getName(), approveRecordsScheduledForCutoffCapability);
    }

    public CreateModifyRecordsInCuttoffFoldersCapability getCreateModifyRecordsInCuttoffFoldersCapability()
    {
        return createModifyRecordsInCuttoffFoldersCapability;
    }

    public void setCreateModifyRecordsInCuttoffFoldersCapability(CreateModifyRecordsInCuttoffFoldersCapability createModifyRecordsInCuttoffFoldersCapability)
    {
        this.createModifyRecordsInCuttoffFoldersCapability = createModifyRecordsInCuttoffFoldersCapability;
        capabilities.put(createModifyRecordsInCuttoffFoldersCapability.getName(), createModifyRecordsInCuttoffFoldersCapability);
    }

    public ExtendRetentionPeriodOrFreezeCapability getExtendRetentionPeriodOrFreezeCapability()
    {
        return extendRetentionPeriodOrFreezeCapability;
    }

    public void setExtendRetentionPeriodOrFreezeCapability(ExtendRetentionPeriodOrFreezeCapability extendRetentionPeriodOrFreezeCapability)
    {
        this.extendRetentionPeriodOrFreezeCapability = extendRetentionPeriodOrFreezeCapability;
        capabilities.put(extendRetentionPeriodOrFreezeCapability.getName(), extendRetentionPeriodOrFreezeCapability);
    }

    public UnfreezeCapability getUnfreezeCapability()
    {
        return unfreezeCapability;
    }

    public void setUnfreezeCapability(UnfreezeCapability unfreezeCapability)
    {
        this.unfreezeCapability = unfreezeCapability;
        capabilities.put(unfreezeCapability.getName(), unfreezeCapability);
    }

    public ViewUpdateReasonsForFreezeCapability getViewUpdateReasonsForFreezeCapability()
    {
        return viewUpdateReasonsForFreezeCapability;
    }

    public void setViewUpdateReasonsForFreezeCapability(ViewUpdateReasonsForFreezeCapability viewUpdateReasonsForFreezeCapability)
    {
        this.viewUpdateReasonsForFreezeCapability = viewUpdateReasonsForFreezeCapability;
        capabilities.put(viewUpdateReasonsForFreezeCapability.getName(), viewUpdateReasonsForFreezeCapability);
    }

    public DestroyRecordsScheduledForDestructionCapability getDestroyRecordsScheduledForDestructionCapability()
    {
        return destroyRecordsScheduledForDestructionCapability;
    }

    public void setDestroyRecordsScheduledForDestructionCapability(DestroyRecordsScheduledForDestructionCapability destroyRecordsScheduledForDestructionCapability)
    {
        this.destroyRecordsScheduledForDestructionCapability = destroyRecordsScheduledForDestructionCapability;
        capabilities.put(destroyRecordsScheduledForDestructionCapability.getName(), destroyRecordsScheduledForDestructionCapability);
    }

    public DestroyRecordsCapability getDestroyRecordsCapability()
    {
        return destroyRecordsCapability;
    }

    public void setDestroyRecordsCapability(DestroyRecordsCapability destroyRecordsCapability)
    {
        this.destroyRecordsCapability = destroyRecordsCapability;
        capabilities.put(destroyRecordsCapability.getName(), destroyRecordsCapability);
    }

    public UpdateVitalRecordCycleInformationCapability getUpdateVitalRecordCycleInformationCapability()
    {
        return updateVitalRecordCycleInformationCapability;
    }

    public void setUpdateVitalRecordCycleInformationCapability(UpdateVitalRecordCycleInformationCapability updateVitalRecordCycleInformationCapability)
    {
        this.updateVitalRecordCycleInformationCapability = updateVitalRecordCycleInformationCapability;
        capabilities.put(updateVitalRecordCycleInformationCapability.getName(), updateVitalRecordCycleInformationCapability);
    }

    public UndeclareRecordsCapability getUndeclareRecordsCapability()
    {
        return undeclareRecordsCapability;
    }

    public void setUndeclareRecordsCapability(UndeclareRecordsCapability undeclareRecordsCapability)
    {
        this.undeclareRecordsCapability = undeclareRecordsCapability;
        capabilities.put(undeclareRecordsCapability.getName(), undeclareRecordsCapability);
    }

    public DeclareAuditAsRecordCapability getDeclareAuditAsRecordCapability()
    {
        return declareAuditAsRecordCapability;
    }

    public void setDeclareAuditAsRecordCapability(DeclareAuditAsRecordCapability declareAuditAsRecordCapability)
    {
        this.declareAuditAsRecordCapability = declareAuditAsRecordCapability;
        capabilities.put(declareAuditAsRecordCapability.getName(), declareAuditAsRecordCapability);
    }

    public DeleteAuditCapability getDeleteAuditCapability()
    {
        return deleteAuditCapability;
    }

    public void setDeleteAuditCapability(DeleteAuditCapability deleteAuditCapability)
    {
        this.deleteAuditCapability = deleteAuditCapability;
        capabilities.put(deleteAuditCapability.getName(), deleteAuditCapability);
    }

    public CreateModifyDestroyTimeframesCapability getCreateModifyDestroyTimeframesCapability()
    {
        return createModifyDestroyTimeframesCapability;
    }

    public void setCreateModifyDestroyTimeframesCapability(CreateModifyDestroyTimeframesCapability createModifyDestroyTimeframesCapability)
    {
        this.createModifyDestroyTimeframesCapability = createModifyDestroyTimeframesCapability;
        capabilities.put(createModifyDestroyTimeframesCapability.getName(), createModifyDestroyTimeframesCapability);
    }

    public AuthorizeNominatedTransfersCapability getAuthorizeNominatedTransfersCapability()
    {
        return authorizeNominatedTransfersCapability;
    }

    public void setAuthorizeNominatedTransfersCapability(AuthorizeNominatedTransfersCapability authorizeNominatedTransfersCapability)
    {
        this.authorizeNominatedTransfersCapability = authorizeNominatedTransfersCapability;
        capabilities.put(authorizeNominatedTransfersCapability.getName(), authorizeNominatedTransfersCapability);
    }

    public EditSelectionListsCapability getEditSelectionListsCapability()
    {
        return editSelectionListsCapability;
    }

    public void setEditSelectionListsCapability(EditSelectionListsCapability editSelectionListsCapability)
    {
        this.editSelectionListsCapability = editSelectionListsCapability;
        capabilities.put(editSelectionListsCapability.getName(), editSelectionListsCapability);
    }

    public AuthorizeAllTransfersCapability getAuthorizeAllTransfersCapability()
    {
        return authorizeAllTransfersCapability;
    }

    public void setAuthorizeAllTransfersCapability(AuthorizeAllTransfersCapability authorizeAllTransfersCapability)
    {
        this.authorizeAllTransfersCapability = authorizeAllTransfersCapability;
        capabilities.put(authorizeAllTransfersCapability.getName(), authorizeAllTransfersCapability);
    }

    public CreateModifyDestroyFileplanMetadataCapability getCreateModifyDestroyFileplanMetadataCapability()
    {
        return createModifyDestroyFileplanMetadataCapability;
    }

    public void setCreateModifyDestroyFileplanMetadataCapability(CreateModifyDestroyFileplanMetadataCapability createModifyDestroyFileplanMetadataCapability)
    {
        this.createModifyDestroyFileplanMetadataCapability = createModifyDestroyFileplanMetadataCapability;
        capabilities.put(createModifyDestroyFileplanMetadataCapability.getName(), createModifyDestroyFileplanMetadataCapability);
    }

    public CreateAndAssociateSelectionListsCapability getCreateAndAssociateSelectionListsCapability()
    {
        return createAndAssociateSelectionListsCapability;
    }

    public void setCreateAndAssociateSelectionListsCapability(CreateAndAssociateSelectionListsCapability createAndAssociateSelectionListsCapability)
    {
        this.createAndAssociateSelectionListsCapability = createAndAssociateSelectionListsCapability;
        capabilities.put(createAndAssociateSelectionListsCapability.getName(), createAndAssociateSelectionListsCapability);
    }

    public AttachRulesToMetadataPropertiesCapability getAttachRulesToMetadataPropertiesCapability()
    {
        return attachRulesToMetadataPropertiesCapability;
    }

    public void setAttachRulesToMetadataPropertiesCapability(AttachRulesToMetadataPropertiesCapability attachRulesToMetadataPropertiesCapability)
    {
        this.attachRulesToMetadataPropertiesCapability = attachRulesToMetadataPropertiesCapability;
        capabilities.put(attachRulesToMetadataPropertiesCapability.getName(), attachRulesToMetadataPropertiesCapability);
    }

    public CreateModifyDestroyFileplanTypesCapability getCreateModifyDestroyFileplanTypesCapability()
    {
        return createModifyDestroyFileplanTypesCapability;
    }

    public void setCreateModifyDestroyFileplanTypesCapability(CreateModifyDestroyFileplanTypesCapability createModifyDestroyFileplanTypesCapability)
    {
        this.createModifyDestroyFileplanTypesCapability = createModifyDestroyFileplanTypesCapability;
        capabilities.put(createModifyDestroyFileplanTypesCapability.getName(), createModifyDestroyFileplanTypesCapability);
    }

    public CreateModifyDestroyRecordTypesCapability getCreateModifyDestroyRecordTypesCapability()
    {
        return createModifyDestroyRecordTypesCapability;
    }

    public void setCreateModifyDestroyRecordTypesCapability(CreateModifyDestroyRecordTypesCapability createModifyDestroyRecordTypesCapability)
    {
        this.createModifyDestroyRecordTypesCapability = createModifyDestroyRecordTypesCapability;
        capabilities.put(createModifyDestroyRecordTypesCapability.getName(), createModifyDestroyRecordTypesCapability);
    }

    public MakeOptionalPropertiesMandatoryCapability getMakeOptionalPropertiesMandatoryCapability()
    {
        return makeOptionalPropertiesMandatoryCapability;
    }

    public void setMakeOptionalPropertiesMandatoryCapability(MakeOptionalPropertiesMandatoryCapability makeOptionalPropertiesMandatoryCapability)
    {
        this.makeOptionalPropertiesMandatoryCapability = makeOptionalPropertiesMandatoryCapability;
        capabilities.put(makeOptionalPropertiesMandatoryCapability.getName(), makeOptionalPropertiesMandatoryCapability);
    }

    public MapEmailMetadataCapability getMapEmailMetadataCapability()
    {
        return mapEmailMetadataCapability;
    }

    public void setMapEmailMetadataCapability(MapEmailMetadataCapability mapEmailMetadataCapability)
    {
        this.mapEmailMetadataCapability = mapEmailMetadataCapability;
        capabilities.put(mapEmailMetadataCapability.getName(), mapEmailMetadataCapability);
    }

    public DeleteRecordsCapability getDeleteRecordsCapability()
    {
        return deleteRecordsCapability;
    }

    public void setDeleteRecordsCapability(DeleteRecordsCapability deleteRecordsCapability)
    {
        this.deleteRecordsCapability = deleteRecordsCapability;
        capabilities.put(deleteRecordsCapability.getName(), deleteRecordsCapability);
    }

    public CreateModifyDestroyRolesCapability getCreateModifyDestroyRolesCapability()
    {
        return createModifyDestroyRolesCapability;
    }

    public void setCreateModifyDestroyRolesCapability(CreateModifyDestroyRolesCapability createModifyDestroyRolesCapability)
    {
        this.createModifyDestroyRolesCapability = createModifyDestroyRolesCapability;
        capabilities.put(createModifyDestroyRolesCapability.getName(), createModifyDestroyRolesCapability);
    }

    public CreateModifyDestroyUsersAndGroupsCapability getCreateModifyDestroyUsersAndGroupsCapability()
    {
        return createModifyDestroyUsersAndGroupsCapability;
    }

    public void setCreateModifyDestroyUsersAndGroupsCapability(CreateModifyDestroyUsersAndGroupsCapability createModifyDestroyUsersAndGroupsCapability)
    {
        this.createModifyDestroyUsersAndGroupsCapability = createModifyDestroyUsersAndGroupsCapability;
        capabilities.put(createModifyDestroyUsersAndGroupsCapability.getName(), createModifyDestroyUsersAndGroupsCapability);
    }

    public PasswordControlCapability getPasswordControlCapability()
    {
        return passwordControlCapability;
    }

    public void setPasswordControlCapability(PasswordControlCapability passwordControlCapability)
    {
        this.passwordControlCapability = passwordControlCapability;
        capabilities.put(passwordControlCapability.getName(), passwordControlCapability);
    }

    public EnableDisableAuditByTypesCapability getEnableDisableAuditByTypesCapability()
    {
        return enableDisableAuditByTypesCapability;
    }

    public void setEnableDisableAuditByTypesCapability(EnableDisableAuditByTypesCapability enableDisableAuditByTypesCapability)
    {
        this.enableDisableAuditByTypesCapability = enableDisableAuditByTypesCapability;
        capabilities.put(enableDisableAuditByTypesCapability.getName(), enableDisableAuditByTypesCapability);
    }

    public SelectAuditMetadataCapability getSelectAuditMetadataCapability()
    {
        return selectAuditMetadataCapability;
    }

    public void setSelectAuditMetadataCapability(SelectAuditMetadataCapability selectAuditMetadataCapability)
    {
        this.selectAuditMetadataCapability = selectAuditMetadataCapability;
        capabilities.put(selectAuditMetadataCapability.getName(), selectAuditMetadataCapability);
    }

    public DisplayRightsReportCapability getDisplayRightsReportCapability()
    {
        return displayRightsReportCapability;
    }

    public void setDisplayRightsReportCapability(DisplayRightsReportCapability displayRightsReportCapability)
    {
        this.displayRightsReportCapability = displayRightsReportCapability;
        capabilities.put(displayRightsReportCapability.getName(), displayRightsReportCapability);
    }

    public AccessAuditCapability getAccessAuditCapability()
    {
        return accessAuditCapability;
    }

    public void setAccessAuditCapability(AccessAuditCapability accessAuditCapability)
    {
        this.accessAuditCapability = accessAuditCapability;
        capabilities.put(accessAuditCapability.getName(), accessAuditCapability);
    }

    public ExportAuditCapability getExportAuditCapability()
    {
        return exportAuditCapability;
    }

    public void setExportAuditCapability(ExportAuditCapability exportAuditCapability)
    {
        this.exportAuditCapability = exportAuditCapability;
        capabilities.put(exportAuditCapability.getName(), exportAuditCapability);
    }

    public CreateModifyDestroyReferenceTypesCapability getCreateModifyDestroyReferenceTypesCapability()
    {
        return createModifyDestroyReferenceTypesCapability;
    }

    public void setCreateModifyDestroyReferenceTypesCapability(CreateModifyDestroyReferenceTypesCapability createModifyDestroyReferenceTypesCapability)
    {
        this.createModifyDestroyReferenceTypesCapability = createModifyDestroyReferenceTypesCapability;
        capabilities.put(createModifyDestroyReferenceTypesCapability.getName(), createModifyDestroyReferenceTypesCapability);
    }

    public UpdateClassificationDatesCapability getUpdateClassificationDatesCapability()
    {
        return updateClassificationDatesCapability;
    }

    public void setUpdateClassificationDatesCapability(UpdateClassificationDatesCapability updateClassificationDatesCapability)
    {
        this.updateClassificationDatesCapability = updateClassificationDatesCapability;
        capabilities.put(updateClassificationDatesCapability.getName(), updateClassificationDatesCapability);
    }

    public CreateModifyDestroyClassificationGuidesCapability getCreateModifyDestroyClassificationGuidesCapability()
    {
        return createModifyDestroyClassificationGuidesCapability;
    }

    public void setCreateModifyDestroyClassificationGuidesCapability(CreateModifyDestroyClassificationGuidesCapability createModifyDestroyClassificationGuidesCapability)
    {
        this.createModifyDestroyClassificationGuidesCapability = createModifyDestroyClassificationGuidesCapability;
        capabilities.put(createModifyDestroyClassificationGuidesCapability.getName(), createModifyDestroyClassificationGuidesCapability);
    }

    public UpgradeDowngradeAndDeclassifyRecordsCapability getUpgradeDowngradeAndDeclassifyRecordsCapability()
    {
        return upgradeDowngradeAndDeclassifyRecordsCapability;
    }

    public void setUpgradeDowngradeAndDeclassifyRecordsCapability(UpgradeDowngradeAndDeclassifyRecordsCapability upgradeDowngradeAndDeclassifyRecordsCapability)
    {
        this.upgradeDowngradeAndDeclassifyRecordsCapability = upgradeDowngradeAndDeclassifyRecordsCapability;
        capabilities.put(upgradeDowngradeAndDeclassifyRecordsCapability.getName(), upgradeDowngradeAndDeclassifyRecordsCapability);
    }

    public UpdateExemptionCategoriesCapability getUpdateExemptionCategoriesCapability()
    {
        return updateExemptionCategoriesCapability;
    }

    public void setUpdateExemptionCategoriesCapability(UpdateExemptionCategoriesCapability updateExemptionCategoriesCapability)
    {
        this.updateExemptionCategoriesCapability = updateExemptionCategoriesCapability;
        capabilities.put(updateExemptionCategoriesCapability.getName(), updateExemptionCategoriesCapability);
    }

    public MapClassificationGuideMetadataCapability getMapClassificationGuideMetadataCapability()
    {
        return mapClassificationGuideMetadataCapability;
    }

    public void setMapClassificationGuideMetadataCapability(MapClassificationGuideMetadataCapability mapClassificationGuideMetadataCapability)
    {
        this.mapClassificationGuideMetadataCapability = mapClassificationGuideMetadataCapability;
        capabilities.put(mapClassificationGuideMetadataCapability.getName(), mapClassificationGuideMetadataCapability);
    }

    public ManageAccessControlsCapability getManageAccessControlsCapability()
    {
        return manageAccessControlsCapability;
    }

    public void setManageAccessControlsCapability(ManageAccessControlsCapability manageAccessControlsCapability)
    {
        this.manageAccessControlsCapability = manageAccessControlsCapability;
        capabilities.put(manageAccessControlsCapability.getName(), manageAccessControlsCapability);
    }

    public TriggerAnEventCapability getTriggerAnEventCapability()
    {
        return triggerAnEventCapability;
    }

    public void setTriggerAnEventCapability(TriggerAnEventCapability triggerAnEventCapability)
    {
        this.triggerAnEventCapability = triggerAnEventCapability;
        capabilities.put(triggerAnEventCapability.getName(), triggerAnEventCapability);
    }

    public CreateCapability getCreateCapability()
    {
        return createCapability;
    }

    public void setCreateCapability(CreateCapability createCapability)
    {
        this.createCapability = createCapability;
        capabilities.put(createCapability.getName(), createCapability);
    }

    public DeleteCapability getDeleteCapability()
    {
        return deleteCapability;
    }

    public void setDeleteCapability(DeleteCapability deleteCapability)
    {
        this.deleteCapability = deleteCapability;
        capabilities.put(deleteCapability.getName(), deleteCapability);

    }

    public UpdateCapability getUpdateCapability()
    {
        return updateCapability;
    }

    public void setUpdateCapability(UpdateCapability updateCapability)
    {
        this.updateCapability = updateCapability;
        capabilities.put(updateCapability.getName(), updateCapability);
    }

    public UpdatePropertiesCapability getUpdatePropertiesCapability()
    {
        return updatePropertiesCapability;
    }

    public void setUpdatePropertiesCapability(UpdatePropertiesCapability updatePropertiesCapability)
    {
        this.updatePropertiesCapability = updatePropertiesCapability;
        capabilities.put(updatePropertiesCapability.getName(), updatePropertiesCapability);
    }

    public DeclareCapability getDeclareCapability()
    {
        return declareCapability;
    }

    public void setDeclareCapability(DeclareCapability declareCapability)
    {
        this.declareCapability = declareCapability;
        capabilities.put(declareCapability.getName(), declareCapability);
    }

    public WriteContentCapability getWriteContentCapability()
    {
        return writeContentCapability;
    }

    public void setWriteContentCapability(WriteContentCapability writeContentCapability)
    {
        this.writeContentCapability = writeContentCapability;
        capabilities.put(writeContentCapability.getName(), writeContentCapability);
    }

    public boolean supports(ConfigAttribute attribute)
    {
        if ((attribute.getAttribute() != null)
                && (attribute.getAttribute().equals(RM_ABSTAIN)
                        || attribute.getAttribute().equals(RM_QUERY) || attribute.getAttribute().equals(RM_ALLOW) || attribute.getAttribute().equals(RM_DENY)
                        || attribute.getAttribute().startsWith(RM_CAP) || attribute.getAttribute().startsWith(RM)))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean supports(Class clazz)
    {
        return (MethodInvocation.class.isAssignableFrom(clazz));
    }

    public void addProtectedProperties(Set<QName> properties)
    {
        protectedProperties.addAll(properties);
    }

    public void addProtectedAspects(Set<QName> aspects)
    {
        protectedAspects.addAll(aspects);
    }

    public Set<QName> getProtectedProperties()
    {
        return Collections.unmodifiableSet(protectedProperties);
    }

    public Set<QName> getProtetcedAscpects()
    {
        return Collections.unmodifiableSet(protectedAspects);
    }

    public int vote(Authentication authentication, Object object, ConfigAttributeDefinition config)
    {
        if (logger.isDebugEnabled())
        {
            MethodInvocation mi = (MethodInvocation) object;
            logger.debug("Method: " + mi.getMethod().toString());
        }
        // The system user can do anything
        if (AuthenticationUtil.isRunAsUserTheSystemUser())
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Access granted for the system user");
            }
            return AccessDecisionVoter.ACCESS_GRANTED;
        }

        List<ConfigAttributeDefintion> supportedDefinitions = extractSupportedDefinitions(config);

        // No RM definitions so we do not vote
        if (supportedDefinitions.size() == 0)
        {
            return AccessDecisionVoter.ACCESS_ABSTAIN;
        }

        MethodInvocation invocation = (MethodInvocation) object;

        Method method = invocation.getMethod();
        Class[] params = method.getParameterTypes();

        // If there are only capability (RM_CAP) and policy (RM) entries non must deny 
        // If any abstain we deny
        // All present must vote to allow unless an explicit direction comes first (e.g. RM_ALLOW)
       
        for (ConfigAttributeDefintion cad : supportedDefinitions)
        {
            // Whatever is found first takes precedence
            if (cad.typeString.equals(RM_DENY))
            {
                return AccessDecisionVoter.ACCESS_DENIED;
            }
            else if (cad.typeString.equals(RM_ABSTAIN))
            {
                return AccessDecisionVoter.ACCESS_ABSTAIN;
            }
            else if (cad.typeString.equals(RM_ALLOW))
            {
                return AccessDecisionVoter.ACCESS_GRANTED;
            }
            // RM_QUERY is a special case - the entry is allowed and filtering sorts out the results
            // It is distinguished from RM_ALLOW so query may have additional behaviour in the future
            else if (cad.typeString.equals(RM_QUERY))
            {
                return AccessDecisionVoter.ACCESS_GRANTED;
            }
            // Ignore config that references method arguments that do not exist
            // Arguably we should deny here but that requires a full impact analysis
            // These entries effectively abstain
            else if (((cad.parameters.get(0) != null) && (cad.parameters.get(0) >= invocation.getArguments().length))            		                                      
                    || ((cad.parameters.get(1) != null) && (cad.parameters.get(1) >= invocation.getArguments().length)))
            {
                continue;
            }
            else if (cad.typeString.equals(RM_CAP))
            {
                switch(checkCapability(invocation, params, cad))
                {
                case  AccessDecisionVoter.ACCESS_DENIED:
                    return AccessDecisionVoter.ACCESS_DENIED;
                case AccessDecisionVoter.ACCESS_ABSTAIN:
                    if(logger.isDebugEnabled())
                    {
                        if(logger.isTraceEnabled())
                        {
                            logger.trace("Capability " + cad.required + " abstained for " + invocation.getMethod(), new IllegalStateException());
                        }
                        else
                        {
                            logger.debug("Capability " + cad.required + " abstained for " + invocation.getMethod());
                        }
                    }
                    // abstain denies
                    return AccessDecisionVoter.ACCESS_DENIED;
                case AccessDecisionVoter.ACCESS_GRANTED:
                    break;
                }
            }
            else if (cad.typeString.equals(RM))
            {
                switch(checkPolicy(invocation, params, cad))
                {
                case  AccessDecisionVoter.ACCESS_DENIED:
                    return AccessDecisionVoter.ACCESS_DENIED;
                case AccessDecisionVoter.ACCESS_ABSTAIN:
                    if(logger.isDebugEnabled())
                    {
                        if(logger.isTraceEnabled())
                        {
                            logger.trace("Policy " + cad.policyName + " abstained for " + invocation.getMethod(), new IllegalStateException());
                        }
                        else
                        {
                            logger.debug("Policy " + cad.policyName + " abstained for " + invocation.getMethod());
                        }
                    }
                    // abstain denies
                    return AccessDecisionVoter.ACCESS_DENIED;
                case AccessDecisionVoter.ACCESS_GRANTED:
                    break;
                }
            }
        }
        
        // all voted to allow

        return AccessDecisionVoter.ACCESS_GRANTED;

    }

    private int checkCapability(MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
    {
        NodeRef testNodeRef = getTestNode(getNodeService(), getRecordsManagementService(), invocation, params, cad.parameters.get(0), cad.parent);
        if (testNodeRef == null)
        {
            return AccessDecisionVoter.ACCESS_ABSTAIN;
        }
        Capability capability = capabilities.get(cad.required.getName());
        if (capability == null)
        {
            return AccessDecisionVoter.ACCESS_DENIED;
        }
        return capability.hasPermissionRaw(testNodeRef);

    }

    public Map<Capability, AccessStatus> getCapabilities(NodeRef nodeRef)
    {        
        HashMap<Capability, AccessStatus> answer = new HashMap<Capability, AccessStatus>();
        for (Capability capability : capabilities.values())
        {
            AccessStatus status = capability.hasPermission(nodeRef);
            if (answer.put(capability, status) != null)
            {
                throw new IllegalStateException();
            }
        }
        return answer;
    }
    
    public Map<Capability, AccessStatus> getCapabilities(NodeRef nodeRef, List<String> capabilities)
    {
        HashMap<Capability, AccessStatus> answer = new HashMap<Capability, AccessStatus>();
        for (String capabilityName : capabilities)
        {
            Capability capability = getCapability(capabilityName);
            if (capability != null)
            {
                AccessStatus status = capability.hasPermission(nodeRef);
                if (answer.put(capability, status) != null)
                {
                    throw new IllegalStateException();
                }
            }
        }
        return answer;        
    }

    public Collection<Capability> getAllCapabilities()
    {
        return capabilities.values();
    }

    public Capability getCapability(String name)
    {
        return capabilities.get(name);
    }

    private static QName getType(NodeService nodeService, MethodInvocation invocation, Class[] params, int position, boolean parent)
    {
        if (QName.class.isAssignableFrom(params[position]))
        {
            if (invocation.getArguments()[position] != null)
            {
                QName qname = (QName) invocation.getArguments()[position];
                return qname;
            }
        }
        else if (NodeRef.class.isAssignableFrom(params[position]))
        {
            if (invocation.getArguments()[position] != null)
            {
                NodeRef nodeRef = (NodeRef) invocation.getArguments()[position];
                return nodeService.getType(nodeRef);
            }
        }

        return null;
    }

    private static QName getQName(MethodInvocation invocation, Class[] params, int position)
    {
        if (QName.class.isAssignableFrom(params[position]))
        {
            if (invocation.getArguments()[position] != null)
            {
                QName qname = (QName) invocation.getArguments()[position];
                return qname;
            }
        }
        throw new ACLEntryVoterException("Unknown type");
    }

    private static Serializable getProperty(MethodInvocation invocation, Class[] params, int position)
    {
        if (invocation.getArguments()[position] == null)
        {
            return null;
        }
        if (Serializable.class.isAssignableFrom(params[position]))
        {
            if (invocation.getArguments()[position] != null)
            {
                Serializable property = (Serializable) invocation.getArguments()[position];
                return property;
            }
        }
        throw new ACLEntryVoterException("Unknown type");
    }

    private static Map<QName, Serializable> getProperties(MethodInvocation invocation, Class[] params, int position)
    {
        if (invocation.getArguments()[position] == null)
        {
            return null;
        }
        if (Map.class.isAssignableFrom(params[position]))
        {
            if (invocation.getArguments()[position] != null)
            {
                Map<QName, Serializable> properties = (Map<QName, Serializable>) invocation.getArguments()[position];
                return properties;
            }
        }
        throw new ACLEntryVoterException("Unknown type");
    }

    private static NodeRef getTestNode(NodeService nodeService, RecordsManagementService rmService, MethodInvocation invocation, Class[] params, int position, boolean parent)
    {
        NodeRef testNodeRef = null;
        if (position < 0)
        {
        	// Test against the fileplan root node
        	List<NodeRef> rmRoots = rmService.getRecordsManagementRoots();
        	if (rmRoots.size() != 0)
        	{
        		// TODO for now we can take the first one as we only support a single rm site
        		testNodeRef = rmRoots.get(0);
        		
        		if (logger.isDebugEnabled())
                {
                    logger.debug("\tPermission test against the rm root node " + nodeService.getPath(testNodeRef));
                }
        	}
        }
        else if (StoreRef.class.isAssignableFrom(params[position]))
        {
            if (invocation.getArguments()[position] != null)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("\tPermission test against the store - using permissions on the root node");
                }
                StoreRef storeRef = (StoreRef) invocation.getArguments()[position];
                if (nodeService.exists(storeRef))
                {
                    testNodeRef = nodeService.getRootNode(storeRef);
                }
            }
        }
        else if (NodeRef.class.isAssignableFrom(params[position]))
        {
            testNodeRef = (NodeRef) invocation.getArguments()[position];
            if (parent)
            {
                testNodeRef = nodeService.getPrimaryParent(testNodeRef).getParentRef();
                if (logger.isDebugEnabled())
                {
                    if (nodeService.exists(testNodeRef))
                    {
                        logger.debug("\tPermission test for parent on node " + nodeService.getPath(testNodeRef));
                    }
                    else
                    {
                        logger.debug("\tPermission test for parent on non-existing node " + testNodeRef);
                    }
                    logger.debug("\tPermission test for parent on node " + nodeService.getPath(testNodeRef));
                }
            }
            else
            {
                if (logger.isDebugEnabled())
                {
                    if (nodeService.exists(testNodeRef))
                    {
                        logger.debug("\tPermission test on node " + nodeService.getPath(testNodeRef));
                    }
                    else
                    {
                        logger.debug("\tPermission test on non-existing node " + testNodeRef);
                    }
                }
            }
        }
        else if (ChildAssociationRef.class.isAssignableFrom(params[position]))
        {
            if (invocation.getArguments()[position] != null)
            {
                if (parent)
                {
                    testNodeRef = ((ChildAssociationRef) invocation.getArguments()[position]).getParentRef();
                }
                else
                {
                    testNodeRef = ((ChildAssociationRef) invocation.getArguments()[position]).getChildRef();
                }
                if (logger.isDebugEnabled())
                {
                    if (nodeService.exists(testNodeRef))
                    {
                        logger.debug("\tPermission test on node " + nodeService.getPath(testNodeRef));
                    }
                    else
                    {
                        logger.debug("\tPermission test on non-existing node " + testNodeRef);
                    }
                }
            }
        }
        else if (AssociationRef.class.isAssignableFrom(params[position]))
        {
            if (invocation.getArguments()[position] != null)
            {
                if (parent)
                {
                    testNodeRef = ((AssociationRef) invocation.getArguments()[position]).getSourceRef();
                }
                else
                {
                    testNodeRef = ((AssociationRef) invocation.getArguments()[position]).getTargetRef();
                }
                if (logger.isDebugEnabled())
                {
                    if (nodeService.exists(testNodeRef))
                    {
                        logger.debug("\tPermission test on node " + nodeService.getPath(testNodeRef));
                    }
                    else
                    {
                        logger.debug("\tPermission test on non-existing node " + testNodeRef);
                    }
                }
            }
        }
        return testNodeRef;
    }

    private int checkPolicy(MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
    {
        Policy policy = policies.get(cad.policyName);
        if (policy == null)
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }
        else
        {
            return policy.evaluate(this, invocation, params, cad);
        }
    }

    public void afterPropertiesSet() throws Exception
    {
        // TODO Auto-generated method stub

    }

    private List<ConfigAttributeDefintion> extractSupportedDefinitions(ConfigAttributeDefinition config)
    {
        List<ConfigAttributeDefintion> definitions = new ArrayList<ConfigAttributeDefintion>(2);
        Iterator iter = config.getConfigAttributes();

        while (iter.hasNext())
        {
            ConfigAttribute attr = (ConfigAttribute) iter.next();

            if (this.supports(attr))
            {
                definitions.add(new ConfigAttributeDefintion(attr));
            }

        }
        return definitions;
    }

    /**
     * @return the nodeService
     */
    public NodeService getNodeService()
    {
        return nodeService;
    }

    /**
     * @return the permissionService
     */
    public PermissionService getPermissionService()
    {
        return permissionService;
    }

    /**
     * @return the caveatConfigService
     */
    public RMCaveatConfigComponent getCaveatConfigComponent()
    {
        return caveatConfigComponent;
    }

    /**
     * @param recordsManagementService
     *            the recordsManagementService to set
     */
    public void setRecordsManagementService(RecordsManagementService recordsManagementService)
    {
        this.recordsManagementService = recordsManagementService;
    }

    /**
     * @return the recordsManagementService
     */
    public RecordsManagementService getRecordsManagementService()
    {
        return recordsManagementService;
    }
    
    public void setDispositionService(DispositionService dispositionService)
    {
        this.dispositionService = dispositionService;
    }
    
    public DispositionService getDispositionService()
    {
        return dispositionService;
    }

    /**
     * @return the dictionaryService
     */
    public DictionaryService getDictionaryService()
    {
        return dictionaryService;
    }

    public boolean isProtectedAspect(NodeRef nodeRef, QName aspectQName)
    {
        if(protectedAspects.contains(aspectQName))
        {
            for(Capability capability : capabilities.values())
            {
                for(RecordsManagementAction action : capability.getActions())
                {
                    if(action.getProtectedAspects().contains(aspectQName))
                    {
                        if(action.isExecutable(nodeRef, null))
                        {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isProtectedProperty(NodeRef nodeRef, QName propertyQName)
    {
        if(protectedProperties.contains(propertyQName))
        {
            for(Capability capability : capabilities.values())
            {
                for(RecordsManagementAction action : capability.getActions())
                {
                    if(action.getProtectedProperties().contains(propertyQName))
                    {
                        if(action.isExecutable(nodeRef, null))
                        {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean includesProtectedPropertyChange(NodeRef nodeRef, Map<QName, Serializable> properties)
    {
        Map<QName, Serializable> originals = nodeService.getProperties(nodeRef);
        for (QName test : properties.keySet())
        {
            if (isProtectedProperty(nodeRef, test))
            {
                if (!EqualsHelper.nullSafeEquals(originals.get(test), properties.get(test)))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private class ConfigAttributeDefintion
    {
        String typeString;

        String policyName;

        SimplePermissionReference required;

        HashMap<Integer, Integer> parameters = new HashMap<Integer, Integer>(2, 1.0f);

        boolean parent = false;

        ConfigAttributeDefintion(ConfigAttribute attr)
        {
            StringTokenizer st = new StringTokenizer(attr.getAttribute(), ".", false);
            if (st.countTokens() < 1)
            {
                throw new ACLEntryVoterException("There must be at least one token in a config attribute");
            }
            typeString = st.nextToken();

            if (!(typeString.equals(RM) || typeString.equals(RM_ALLOW) || typeString.equals(RM_CAP) || typeString.equals(RM_DENY) || typeString.equals(RM_QUERY) || typeString
                    .equals(RM_ABSTAIN)))
            {
                throw new ACLEntryVoterException("Invalid type: must be ACL_NODE, ACL_PARENT or ACL_ALLOW");
            }

            if (typeString.equals(RM))
            {
                policyName = st.nextToken();
                int position = 0;
                while (st.hasMoreElements())
                {
                    String numberString = st.nextToken();
                    Integer value = Integer.parseInt(numberString);
                    parameters.put(position, value);
                    position++;
                }
            }
            else if (typeString.equals(RM_CAP))
            {
                String numberString = st.nextToken();
                String qNameString = st.nextToken();
                String permissionString = st.nextToken();

                Integer value = Integer.parseInt(numberString);
                parameters.put(0, value);

                QName qName = QName.createQName(qNameString, nspr);

                required = SimplePermissionReference.getPermissionReference(qName, permissionString);

                if (st.hasMoreElements())
                {
                    parent = true;
                }
            }
        }
    }

    interface Policy
    {
        int evaluate(RMEntryVoter voter, MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad);
    }

    private static class ReadPolicy implements Policy
    {

        public int evaluate(RMEntryVoter voter, MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
        {
            NodeRef testNodeRef = getTestNode(voter.getNodeService(), voter.getRecordsManagementService(), invocation, params, cad.parameters.get(0), cad.parent);
            return voter.getViewRecordsCapability().evaluate(testNodeRef);
        }

    }

    private static class CreatePolicy implements Policy
    {

        public int evaluate(RMEntryVoter voter, MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
        {

            NodeRef destination = getTestNode(voter.getNodeService(), voter.getRecordsManagementService(), invocation, params, cad.parameters.get(0), cad.parent);
            QName type = getType(voter.getNodeService(), invocation, params, cad.parameters.get(1), cad.parent);
            // linkee is not null for creating secondary child assocs
            NodeRef linkee = getTestNode(voter.getNodeService(), voter.getRecordsManagementService(), invocation, params, cad.parameters.get(1), cad.parent);
            QName assocType = null;
            if(cad.parameters.size() > 2)
            {
                assocType = getType(voter.getNodeService(), invocation, params, cad.parameters.get(2), cad.parent);
            }

            return voter.getCreateCapability().evaluate(destination, linkee, type, assocType);
        }

    }

    private static class MovePolicy implements Policy
    {

        public int evaluate(RMEntryVoter voter, MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
        {

            NodeRef movee = null;
            if (cad.parameters.get(0) > -1)
            {
                movee = getTestNode(voter.getNodeService(), voter.getRecordsManagementService(), invocation, params, cad.parameters.get(0), cad.parent);
            }

            NodeRef destination = null;
            if (cad.parameters.get(1) > -1)
            {
                destination = getTestNode(voter.getNodeService(), voter.getRecordsManagementService(), invocation, params, cad.parameters.get(1), cad.parent);
            }

            if ((movee != null) && (destination != null))
            {
                return voter.getMoveRecordsCapability().evaluate(movee, destination);
            }
            else
            {
                return AccessDecisionVoter.ACCESS_DENIED;
            }

        }
    }

    private static class UpdatePolicy implements Policy
    {

        public int evaluate(RMEntryVoter voter, MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
        {
            NodeRef updatee = getTestNode(voter.getNodeService(), voter.getRecordsManagementService(), invocation, params, cad.parameters.get(0), cad.parent);
            QName aspectQName = null;
            if (cad.parameters.size() > 1)
            {
                if (cad.parameters.get(1) > -1)
                {
                    aspectQName = getQName(invocation, params, cad.parameters.get(1));
                }
            }
            Map<QName, Serializable> properties = null;
            if (cad.parameters.size() > 2)
            {
                if (cad.parameters.get(2) > -1)
                {
                    properties = getProperties(invocation, params, cad.parameters.get(2));
                }
            }
            return voter.getUpdateCapability().evaluate(updatee, aspectQName, properties);
        }

    }

    private static class DeletePolicy implements Policy
    {

        public int evaluate(RMEntryVoter voter, MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
        {
            NodeRef deletee = null;
            if (cad.parameters.get(0) > -1)
            {
                deletee = getTestNode(voter.getNodeService(), voter.getRecordsManagementService(), invocation, params, cad.parameters.get(0), cad.parent);
            }
            if (deletee != null)
            {

                return voter.getDeleteCapability().evaluate(deletee);

            }
            else
            {
                return AccessDecisionVoter.ACCESS_DENIED;
            }
        }

    }

    private static class UpdatePropertiesPolicy implements Policy
    {

        public int evaluate(RMEntryVoter voter, MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
        {
            NodeRef updatee = getTestNode(voter.getNodeService(), voter.getRecordsManagementService(), invocation, params, cad.parameters.get(0), cad.parent);
            Map<QName, Serializable> properties;
            if (QName.class.isAssignableFrom(params[cad.parameters.get(1)]))
            {
                // single update/delete
                // We have a specific property
                QName propertyQName = getQName(invocation, params, cad.parameters.get(1));
                properties = new HashMap<QName, Serializable>(1, 1.0f);
                if (cad.parameters.size() > 2)
                {
                    properties.put(propertyQName, getProperty(invocation, params, cad.parameters.get(2)));
                }
                else
                {
                    properties.put(propertyQName, null);
                }
            }
            else
            {
                properties = getProperties(invocation, params, cad.parameters.get(1));
            }

            return voter.getUpdatePropertiesCapability().evaluate(updatee, properties);
        }

    }

    private static class AssocPolicy implements Policy
    {

        public int evaluate(RMEntryVoter voter, MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
        {
            Policy policy = policies.get("Read");
            if (policy == null)
            {
                return AccessDecisionVoter.ACCESS_DENIED;
            }
            else
            {
                return policy.evaluate(voter, invocation, params, cad);
            }
        }

    }

    private static class WriteContentPolicy implements Policy
    {

        public int evaluate(RMEntryVoter voter, MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
        {
            NodeRef updatee = getTestNode(voter.getNodeService(), voter.getRecordsManagementService(), invocation, params, cad.parameters.get(0), cad.parent);
            return voter.getWriteContentCapability().evaluate(updatee);
        }

    }

    private static class CapabilityPolicy implements Policy
    {

        public int evaluate(RMEntryVoter voter, MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
        {
            NodeRef assignee = getTestNode(voter.getNodeService(), voter.getRecordsManagementService(), invocation, params, cad.parameters.get(0), cad.parent);
            return voter.getManageAccessControlsCapability().evaluate(assignee);
        }

    }

    private static class DeclarePolicy implements Policy
    {

        public int evaluate(RMEntryVoter voter, MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
        {
            NodeRef declaree = getTestNode(voter.getNodeService(), voter.getRecordsManagementService(), invocation, params, cad.parameters.get(0), cad.parent);
            return voter.getDeclareCapability().evaluate(declaree);
        }

    }
    
    private static class ReadPropertyPolicy implements Policy
    {

        public int evaluate(RMEntryVoter voter, MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
        {
            NodeRef nodeRef = getTestNode(voter.getNodeService(), voter.getRecordsManagementService(), invocation, params, cad.parameters.get(0), cad.parent);
            QName propertyQName = getQName(invocation, params, cad.parameters.get(1));
            if(propertyQName.equals(RecordsManagementModel.PROP_HOLD_REASON))
            {
                return voter.getViewUpdateReasonsForFreezeCapability().evaluate(nodeRef);
            }
            else
            {
                return AccessDecisionVoter.ACCESS_GRANTED;
            }
        }

    }

    private ApplicationContext applicationContext;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException
    {
        this.applicationContext = applicationContext;
    }
    
}

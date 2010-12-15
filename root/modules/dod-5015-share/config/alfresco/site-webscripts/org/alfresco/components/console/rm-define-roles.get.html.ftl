<#if !hasAccess>
   <#include "./rm-console-access.ftl">
<#else>
<#if (action='new' || action='edit')>

<script type="text/javascript">//<![CDATA[
   new Alfresco.admin.RMRoles("manageRoles").setOptions(
   {
      action: "${action?js_string}",
      roleId: "${(roleId!"")?js_string}"
   }).setMessages(${messages});
//]]></script>

<div id="manageRoles">
   <#if (action='new')>
   <h2>${msg('label.new-role')}</h2>
   <#else>
   <h2>${msg('label.edit-role')}</h2>
   </#if>
   
   <form id="roleForm" action="">
      <div>
         <label for="roleName">${msg('label.name')}:</label>
         <input type="text" name="roleName" value="" id="roleName" maxlength="100" />
      </div>
      
      <h3>${msg('label.capabilities')}</h3>
      
      <div>
         <button id="group01SelectAll" value="group01SelectAll" class="selectAll action">${msg('label.select-all')}</button>
         <fieldset>
            <legend>${msg('label.group.records')}</legend>
            <ul id="group01Capabilities" class="capabilities">
               <li><input name="DeclareRecords" type="checkbox" id="DeclareRecords" /><label for="DeclareRecords">${msg('label.role.DeclareRecords')}</label></li>
               <li><input name="ViewRecords" type="checkbox" id="ViewRecords" /><label for="ViewRecords">${msg('label.role.ViewRecords')}</label></li>
               <li><input name="UndeclareRecords" type="checkbox" id="UndeclareRecords" /><label for="UndeclareRecords">${msg('label.role.UndeclareRecords')}</label></li>
            </ul>
         </fieldset>
      </div>
      
      <div>
         <button id="group02SelectAll" value="group02SelectAll" class="selectAll action">${msg('label.select-all')}</button>
         <fieldset>
            <legend>${msg('label.group.metadata-control')}</legend>
            <ul id="group02Capabilities" class="capabilities">
               <li><input name="EditRecordMetadata" type="checkbox" id="EditRecordMetadata" /><label for="EditRecordMetadata">${msg('label.role.EditRecordMetadata')}</label></li>
               <li><input name="EditDeclaredRecordMetadata" type="checkbox" id="EditDeclaredRecordMetadata" /><label for="EditDeclaredRecordMetadata">${msg('label.role.EditDeclaredRecordMetadata')}</label></li>
               <li><input name="EditNonRecordMetadata" type="checkbox" id="EditNonRecordMetadata" /><label for="EditNonRecordMetadata">${msg('label.role.EditNonRecordMetadata')}</label></li>
               <li><input name="MoveRecords" type="checkbox" id="MoveRecords" /><label for="MoveRecords">${msg('label.role.MoveRecords')}</label></li>
            </ul>
         </fieldset>
      </div>
      
      <div>
         <button id="group03SelectAll" value="group03SelectAll" class="selectAll action">${msg('label.select-all')}</button>
         <fieldset>
            <legend>${msg('label.group.folder-control')}</legend>
            <ul id="group03Capabilities" class="capabilities">
               <li><input name="CreateModifyDestroyFolders" type="checkbox" id="CreateModifyDestroyFolders" /><label for="CreateModifyDestroyFolders">${msg('label.role.CreateModifyDestroyFolders')}</label></li>
               <li><input name="CloseFolders" type="checkbox" id="CloseFolders" /><label for="CloseFolders">${msg('label.role.CloseFolders')}</label></li>
               <li><input name="ReOpenFolders" type="checkbox" id="ReOpenFolders" /><label for="ReOpenFolders">${msg('label.role.ReOpenFolders')}</label></li>
               <li><input name="DeclareRecordsInClosedFolders" type="checkbox" id="DeclareRecordsInClosedFolders" /><label for="DeclareRecordsInClosedFolders">${msg('label.role.DeclareRecordsInClosedFolders')}</label></li>
            </ul>
         </fieldset>
      </div>
      
      <div>
         <button id="group04SelectAll" value="group04SelectAll" class="selectAll action">${msg('label.select-all')}</button>
         <fieldset>
            <legend>${msg('label.group.vital-records')}</legend>
            <ul id="group04Capabilities" class="capabilities">
               <li><input name="UpdateVitalRecordCycleInformation" type="checkbox" id="UpdateVitalRecordCycleInformation" /><label for="UpdateVitalRecordCycleInformation">${msg('label.role.UpdateVitalRecordCycleInformation')}</label></li>
               <li><input name="CycleVitalRecords" type="checkbox" id="CycleVitalRecords" /><label for="CycleVitalRecords">${msg('label.role.CycleVitalRecords')}</label></li>
               <li><input name="PlanningReviewCycles" type="checkbox" id="PlanningReviewCycles" /><label for="PlanningReviewCycles">${msg('label.role.PlanningReviewCycles')}</label></li>
            </ul>
         </fieldset>
      </div>
      
      <div>
         <button id="group05SelectAll" value="group05SelectAll" class="selectAll action">${msg('label.select-all')}</button>
         <fieldset>
            <legend>${msg('label.group.references-links')}</legend>
            <ul id="group05Capabilities" class="capabilities">
               <li><input name="ChangeOrDeleteReferences" type="checkbox" id="ChangeOrDeleteReferences" /><label for="ChangeOrDeleteReferences">${msg('label.role.ChangeOrDeleteReferences')}</label></li>
               <li><input name="DeleteLinks" type="checkbox" id="DeleteLinks" /><label for="DeleteLinks">${msg('label.role.DeleteLinks')}</label></li>
            </ul>
         </fieldset>
      </div>
      
      <div>
         <button id="group06SelectAll" value="group06SelectAll" class="selectAll action">${msg('label.select-all')}</button>
         <fieldset>
            <legend>${msg('label.group.events')}</legend>
            <ul id="group06Capabilities" class="capabilities">
               <li><input name="CreateModifyDestroyEvents" type="checkbox" id="CreateModifyDestroyEvents" /><label for="CreateModifyDestroyEvents">${msg('label.role.CreateModifyDestroyEvents')}</label></li>
               <li><input name="AddModifyEventDates" type="checkbox" id="AddModifyEventDates" /><label for="AddModifyEventDates">${msg('label.role.AddModifyEventDates')}</label></li>
            </ul>
         </fieldset>
      </div>
      
      <div>
         <button id="group07SelectAll" value="group07SelectAll" class="selectAll action">${msg('label.select-all')}</button>
         <fieldset>
            <legend>${msg('label.group.cutoff')}</legend>
            <ul id="group07Capabilities" class="capabilities">
               <li><input name="ApproveRecordsScheduledForCutoff" type="checkbox" id="ApproveRecordsScheduledForCutoff" /><label for="ApproveRecordsScheduledForCutoff">${msg('label.role.ApproveRecordsScheduledForCutoff')}</label></li>
               <li><input name="CreateModifyRecordsInCutoffFolders" type="checkbox" id="CreateModifyRecordsInCutoffFolders" /><label for="CreateModifyRecordsInCutoffFolders">${msg('label.role.CreateModifyRecordsInCutoffFolders')}</label></li>
            </ul>
         </fieldset>
      </div>
      
      <div>
         <button id="group08SelectAll" value="group08SelectAll" class="selectAll action">${msg('label.select-all')}</button>
         <fieldset>
            <legend>${msg('label.group.disposition-transfers')}</legend>
            <ul id="group08Capabilities" class="capabilities">
               <li><input name="UpdateTriggerDates" type="checkbox" id="UpdateTriggerDates" /><label for="UpdateTriggerDates">${msg('label.role.UpdateTriggerDates')}</label></li>
               <li><input name="ManuallyChangeDispositionDates" type="checkbox" id="ManuallyChangeDispositionDates" /><label for="ManuallyChangeDispositionDates">${msg('label.role.ManuallyChangeDispositionDates')}</label></li>
               <li><input name="AuthorizeNominatedTransfers" type="checkbox" id="AuthorizeNominatedTransfers" /><label for="AuthorizeNominatedTransfers">${msg('label.role.AuthorizeNominatedTransfers')}</label></li>
               <li><input name="AuthorizeAllTransfers" type="checkbox" id="AuthorizeAllTransfers" /><label for="AuthorizeAllTransfers">${msg('label.role.AuthorizeAllTransfers')}</label></li>
               <li><input name="DestroyRecordsScheduledForDestruction" type="checkbox" id="DestroyRecordsScheduledForDestruction" /><label for="DestroyRecordsScheduledForDestruction">${msg('label.role.DestroyRecordsScheduledForDestruction')}</label></li>
               <li><input name="DestroyRecords" type="checkbox" id="DestroyRecords" /><label for="DestroyRecords">${msg('label.role.DestroyRecords')}</label></li>
               <li><input name="DeleteRecords" type="checkbox" id="DeleteRecords" /><label for="DeleteRecords">${msg('label.role.DeleteRecords')}</label></li>
               <li><input name="TriggerAnEvent" type="checkbox" id="TriggerAnEvent" /><label for="TriggerAnEvent">${msg('label.role.TriggerAnEvent')}</label></li>
            </ul>
         </fieldset>
      </div>
      
      <div>
         <button id="group09SelectAll" value="group09SelectAll" class="selectAll action">${msg('label.select-all')}</button>
         <fieldset>
            <legend>${msg('label.group.hold-controls')}</legend>
            <ul id="group09Capabilities" class="capabilities">
               <li><input name="ExtendRetentionPeriodOrFreeze" type="checkbox" id="ExtendRetentionPeriodOrFreeze" /><label for="ExtendRetentionPeriodOrFreeze">${msg('label.role.ExtendRetentionPeriodOrFreeze')}</label></li>
               <li><input name="Unfreeze" type="checkbox" id="Unfreeze" /><label for="Unfreeze">${msg('label.role.Unfreeze')}</label></li>
               <li><input name="ViewUpdateReasonsForFreeze" type="checkbox" id="ViewUpdateReasonsForFreeze" /><label for="ViewUpdateReasonsForFreeze">${msg('label.role.ViewUpdateReasonsForFreeze')}</label></li>
            </ul>
         </fieldset>
      </div>
      
      <div>
         <button id="group10SelectAll" value="group10SelectAll" class="selectAll action">${msg('label.select-all')}</button>
         <fieldset>
            <legend>${msg('label.group.audit')}</legend>
            <ul id="group10Capabilities" class="capabilities">
               <li><input name="DeclareAuditAsRecord" type="checkbox" id="DeclareAuditAsRecord" /><label for="DeclareAuditAsRecord">${msg('label.role.DeclareAuditAsRecord')}</label></li>
               <li><input name="EnableDisableAuditByTypes" type="checkbox" id="EnableDisableAuditByTypes" /><label for="EnableDisableAuditByTypes">${msg('label.role.EnableDisableAuditByTypes')}</label></li>
               <li><input name="DeleteAudit" type="checkbox" id="DeleteAudit" /><label for="DeleteAudit">${msg('label.role.DeleteAudit')}</label></li>
               <li><input name="SelectAuditMetadata" type="checkbox" id="SelectAuditMetadata" /><label for="SelectAuditMetadata">${msg('label.role.SelectAuditMetadata')}</label></li>
               <li><input name="AccessAudit" type="checkbox" id="AccessAudit" /><label for="AccessAudit">${msg('label.role.AccessAudit')}</label></li>
               <li><input name="ExportAudit" type="checkbox" id="ExportAudit" /><label for="ExportAudit">${msg('label.role.ExportAudit')}</label></li>
            </ul>
         </fieldset>
      </div>
      
      <div>
         <button id="group11SelectAll" value="group11SelectAll" class="selectAll action">${msg('label.select-all')}</button>
         <fieldset>
            <legend>${msg('label.group.roles-access-rights')}</legend>
            <ul id="group11Capabilities" class="capabilities">
               <li><input name="CreateModifyDestroyRoles" type="checkbox" id="CreateModifyDestroyRoles" /><label for="CreateModifyDestroyRoles">${msg('label.role.CreateModifyDestroyRoles')}</label></li>
               <li><input name="CreateModifyDestroyUsersAndGroups" type="checkbox" id="CreateModifyDestroyUsersAndGroups" /><label for="CreateModifyDestroyUsersAndGroups">${msg('label.role.CreateModifyDestroyUsersAndGroups')}</label></li>
               <li><input name="PasswordControl" type="checkbox" id="PasswordControl" /><label for="PasswordControl">${msg('label.role.PasswordControl')}</label></li>
               <li><input name="DisplayRightsReport" type="checkbox" id="DisplayRightsReport" /><label for="DisplayRightsReport">${msg('label.role.DisplayRightsReport')}</label></li>
               <li><input name="ManageAccessControls" type="checkbox" id="ManageAccessControls" /><label for="ManageAccessControls">${msg('label.role.ManageAccessControls')}</label></li>
               <li><input name="ManageAccessRights" type="checkbox" id="ManageAccessRights" /><label for="ManageAccessRights">${msg('label.role.ManageAccessRights')}</label></li>
            </ul>
         </fieldset>
      </div>
      
      <div>
         <button id="group12SelectAll" value="group12SelectAll" class="selectAll action">${msg('label.select-all')}</button>
         <fieldset>
            <legend>${msg('label.group.metadata-lists-email')}</legend>
            <ul id="group12Capabilities" class="capabilities">
               <li><input name="CreateModifyDestroyFileplanMetadata" type="checkbox" id="CreateModifyDestroyFileplanMetadata" /><label for="CreateModifyDestroyFileplanMetadata">${msg('label.role.CreateModifyDestroyFileplanMetadata')}</label></li>
               <li><input name="CreateModifyDestroyFileplanTypes" type="checkbox" id="CreateModifyDestroyFileplanTypes" /><label for="CreateModifyDestroyFileplanTypes">${msg('label.role.CreateModifyDestroyFileplanTypes')}</label></li>
               <li><input name="CreateModifyDestroyRecordTypes" type="checkbox" id="CreateModifyDestroyRecordTypes" /><label for="CreateModifyDestroyRecordTypes">${msg('label.role.CreateModifyDestroyRecordTypes')}</label></li>
               <li><input name="CreateAndAssociateSelectionLists" type="checkbox" id="CreateAndAssociateSelectionLists" /><label for="CreateAndAssociateSelectionLists">${msg('label.role.CreateAndAssociateSelectionLists')}</label></li>
               <li><input name="EditSelectionLists" type="checkbox" id="EditSelectionLists" /><label for="EditSelectionLists">${msg('label.role.EditSelectionLists')}</label></li>
               <li><input name="CreateModifyDestroyReferenceTypes" type="checkbox" id="CreateModifyDestroyReferenceTypes" /><label for="CreateModifyDestroyReferenceTypes">${msg('label.role.CreateModifyDestroyReferenceTypes')}</label></li>
               <li><input name="AttachRulesToMetadataProperties" type="checkbox" id="AttachRulesToMetadataProperties" /><label for="AttachRulesToMetadataProperties">${msg('label.role.AttachRulesToMetadataProperties')}</label></li>
               <li><input name="MakeOptionalParametersMandatory" type="checkbox" id="MakeOptionalParametersMandatory" /><label for="MakeOptionalParametersMandatory">${msg('label.role.MakeOptionalParametersMandatory')}</label></li>
               <li><input name="MapEmailMetadata" type="checkbox" id="MapEmailMetadata" /><label for="MapEmailMetadata">${msg('label.role.MapEmailMetadata')}</label></li>
            </ul>
         </fieldset>
      </div>
      
      <div>
         <button id="group13SelectAll" value="group13SelectAll" class="selectAll action">${msg('label.select-all')}</button>
         <fieldset>
            <legend>${msg('label.group.classified-records')}</legend>
            <ul id="group13Capabilities" class="capabilities">
               <li><input name="UpdateClassificationDates" type="checkbox" id="UpdateClassificationDates" /><label for="UpdateClassificationDates">${msg('label.role.UpdateClassificationDates')}</label></li>
               <li><input name="CreateModifyDestroyClassificationGuides" type="checkbox" id="CreateModifyDestroyClassificationGuides" /><label for="CreateModifyDestroyClassificationGuides">${msg('label.role.CreateModifyDestroyClassificationGuides')}</label></li>
               <li><input name="UpgradeDowngradeAndDeclassifyRecords" type="checkbox" id="UpgradeDowngradeAndDeclassifyRecords" /><label for="UpgradeDowngradeAndDeclassifyRecords">${msg('label.role.UpgradeDowngradeAndDeclassifyRecords')}</label></li>
               <li><input name="UpdateExemptionCategories" type="checkbox" id="UpdateExemptionCategories" /><label for="UpdateExemptionCategories">${msg('label.role.UpdateExemptionCategories')}</label></li>
               <li><input name="MapClassificationGuideMetadata" type="checkbox" id="MapClassificationGuideMetadata" /><label for="MapClassificationGuideMetadata">${msg('label.role.MapClassificationGuideMetadata')}</label></li>
               <li><input name="CreateModifyDestroyTimeframes" type="checkbox" id="CreateModifyDestroyTimeframes" /><label for="CreateModifyDestroyTimeframes">${msg('label.role.CreateModifyDestroyTimeframes')}</label></li>
            </ul>
         </fieldset>
      </div>
      
      <div>
         <button name="submit" value="submit" id="submit"><#if (action='new')>${msg('label.create')}<#else>${msg('label.save')}</#if></button>
         <button name="submitCancel" value="Cancel" id="submitCancel" class="cancel">${msg('label.cancel')}</button>
      </div>
   </form>

</div>

<#else>

<script type="text/javascript">//<![CDATA[
   new Alfresco.admin.RMViewRoles("viewRoles").setOptions(
   {
      selectedRoleId: "${(roleId!"")?js_string}"
   }).setMessages(${messages});
//]]></script>

<div id="viewRoles">

   <div>
      <button id="newRole" value="newRole" class="action">${msg('label.new-role')}</button>
      <h2>${msg('label.roles')}</h2>
   </div>
   <div class="yui-gf view-role-list">
      <div id="roleSelection" class="yui-u first">
         <div class="list-header">
            <h3>${msg('label.roles')}</h3>
         </div>
         <div id="roles">
            <ul>
            </ul>
         </div>
      </div>
      
      <div id="roleContent" class="yui-u">
         <div class="list-header">
            <div class="editRoleButton">
               <button id="editRole" class="action">${msg('label.edit-role')}</button>
            </div>
            <div class="deleteRoleButton">
               <button id="deleteRole" class="action">${msg('label.delete-role')}</button>
            </div>
            <div class="capabilities-header">${msg('label.capabilities')}</div>
         </div>
         <div class="roleCapabilities">
            <ul class="capabilities-list" id="capabilities-list">
            </ul>
         </div>
      </div>
   </div>
</div>

</#if>
</#if>
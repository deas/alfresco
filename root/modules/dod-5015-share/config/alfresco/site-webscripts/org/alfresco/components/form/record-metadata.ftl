<#if formUI == "true">
   <@formLib.renderFormsRuntime formId=formId />
</#if>
    
<div id="${formId}-container" class="form-container">
 
   <#if form.showCaption?exists && form.showCaption>
      <div id="${formId}-caption" class="caption"><span class="mandatory-indicator">*</span>${msg("form.required.fields")}</div>
   </#if>
    
   <#if form.mode != "view">
      <form id="${formId}" method="${form.method}" accept-charset="utf-8" enctype="${form.enctype}" action="${form.submissionUrl}">
   </#if>
   
   <div id="${formId}-fields" class="form-fields">
      <div class="set-panel">
         <div class="set-panel-heading">${msg("label.set.idStatus")}</div>
         <div class="set-panel-body">
            <@formLib.renderField field=form.fields["prop_cm_name"] />
            <@formLib.renderField field=form.fields["prop_rma_identifier"] />
            <#if form.mode == "view">
               <@formLib.renderField field=form.fields["prop_rmCategoryIdentifier"] />
            </#if>
            <@formLib.renderField field=form.fields["prop_cm_title"] />
            <@formLib.renderField field=form.fields["prop_cm_description"] />
            <#if form.fields["prop_cm_owner"]?? && form.mode == "view">
               <@formLib.renderField field=form.fields["prop_cm_owner"] />
            </#if>
            <#if form.fields["prop_rmDeclared"]?? && form.mode == "view">
               <@formLib.renderField field=form.fields["prop_rmDeclared"] />
            </#if>
            <#if form.fields["prop_rma_declaredAt"]?? && form.mode == "view">
               <@formLib.renderField field=form.fields["prop_rma_declaredAt"] />
            </#if>
            <#if form.fields["prop_rma_declaredBy"]?? && form.mode == "view">
               <@formLib.renderField field=form.fields["prop_rma_declaredBy"] />
            </#if>
            <#if form.fields["prop_cm_author"]??>
               <@formLib.renderField field=form.fields["prop_cm_author"] />
            </#if>
         </div>
      </div>
      <#if form.fields["prop_mimetype"]?? || form.mode == "view">
      <div class="set-panel">
         <div class="set-panel-heading">${msg("label.set.general")}</div>
         <div class="set-panel-body">
            <#if form.mode == "view">
               <@formLib.renderField field=form.fields["prop_cm_creator"] />
               <@formLib.renderField field=form.fields["prop_cm_created"] />
               <@formLib.renderField field=form.fields["prop_cm_modifier"] />
               <@formLib.renderField field=form.fields["prop_cm_modified"] />
               <#if form.fields["prop_size"]??>
                  <@formLib.renderField field=form.fields["prop_size"] />
               </#if>
            </#if>
            <#if form.fields["prop_mimetype"]??>
               <@formLib.renderField field=form.fields["prop_mimetype"] />
            </#if>
         </div>
      </div>
      </#if>
      <div class="set-panel">
         <div class="set-panel-heading">${msg("label.set.record")}</div>
         <div class="set-panel-body">
            <#if form.fields["prop_rmRecordType"]?? && form.mode == "view">
               <@formLib.renderField field=form.fields["prop_rmRecordType"] />
            </#if>
            <@formLib.renderField field=form.fields["prop_rma_originator"] />
            <@formLib.renderField field=form.fields["prop_rma_originatingOrganization"] />
            <#if form.mode == "view">
               <@formLib.renderField field=form.fields["prop_rma_dateFiled"] />
            </#if>
            <@formLib.renderField field=form.fields["prop_rma_publicationDate"] />
            <@formLib.renderField field=form.fields["prop_rma_location"] />
            <@formLib.renderField field=form.fields["prop_rma_mediaType"] />
            <@formLib.renderField field=form.fields["prop_rma_format"] />
            <!-- Scanned Record Fields -->
            <#if form.fields["prop_dod_scannedFormatVersion"]??>
               <@formLib.renderField field=form.fields["prop_dod_scannedFormatVersion"] />
               <@formLib.renderField field=form.fields["prop_dod_resolutionX"] />
               <@formLib.renderField field=form.fields["prop_dod_resolutionY"] />
               <@formLib.renderField field=form.fields["prop_dod_scannedBitDepth"] />
            </#if>
            <!-- PDF Record Fields -->
            <#if form.fields["prop_dod_producingApplication"]??>
               <@formLib.renderField field=form.fields["prop_dod_producingApplication"] />
               <@formLib.renderField field=form.fields["prop_dod_producingApplicationVersion"] />
               <@formLib.renderField field=form.fields["prop_dod_pdfVersion"] />
               <@formLib.renderField field=form.fields["prop_dod_creatingApplication"] />
               <@formLib.renderField field=form.fields["prop_dod_documentSecuritySettings"] />
            </#if>
            <!-- Digital Photograph Record Fields -->
            <#if form.fields["prop_dod_caption"]??>
               <@formLib.renderField field=form.fields["prop_dod_caption"] />
               <@formLib.renderField field=form.fields["prop_dod_photographer"] />
               <@formLib.renderField field=form.fields["prop_dod_copyright"] />
               <@formLib.renderField field=form.fields["prop_dod_bitDepth"] />
               <@formLib.renderField field=form.fields["prop_dod_imageSizeX"] />
               <@formLib.renderField field=form.fields["prop_dod_imageSizeY"] />
               <@formLib.renderField field=form.fields["prop_dod_imageSource"] />
               <@formLib.renderField field=form.fields["prop_dod_compression"] />
               <@formLib.renderField field=form.fields["prop_dod_iccIcmProfile"] />
               <@formLib.renderField field=form.fields["prop_dod_exifInformation"] />
            </#if>
            <!-- Web Record Fields -->
            <#if form.fields["prop_dod_webFileName"]??>
               <@formLib.renderField field=form.fields["prop_dod_webFileName"] />
               <@formLib.renderField field=form.fields["prop_dod_webPlatform"] />
               <@formLib.renderField field=form.fields["prop_dod_webSiteName"] />
               <@formLib.renderField field=form.fields["prop_dod_webSiteURL"] />
               <@formLib.renderField field=form.fields["prop_dod_captureMethod"] />
               <@formLib.renderField field=form.fields["prop_dod_captureDate"] />
               <@formLib.renderField field=form.fields["prop_dod_contact"] />
               <@formLib.renderField field=form.fields["prop_dod_contentManagementSystem"] />
            </#if>
         </div>
      </div>
      <div class="set-panel">
         <div class="set-panel-heading">${msg("label.set.correspondence")}</div>
         <div class="set-panel-body">
            <@formLib.renderField field=form.fields["prop_rma_dateReceived"] />
            <@formLib.renderField field=form.fields["prop_rma_address"] />
            <@formLib.renderField field=form.fields["prop_rma_otherAddress"] />
         </div>
      </div>
      <div class="set-panel">
         <div class="set-panel-heading">${msg("label.set.security")}</div>
         <div class="set-panel-body">
            <@formLib.renderField field=form.fields["prop_rmc_supplementalMarkingList"] />
         </div>
      </div>
      <#if form.fields["prop_rma_vitalRecordIndicator"]?? || form.fields["prop_rma_reviewPeriod"]?? ||
           (form.fields["prop_rma_reviewAsOf"]?? && form.mode == "view")>
         <div class="set-panel">
            <div class="set-panel-heading">${msg("label.set.vitalRecord")}</div>
            <div class="set-panel-body">
               <#if form.fields["prop_rma_vitalRecordIndicator"]??>
                  <@formLib.renderField field=form.fields["prop_rma_vitalRecordIndicator"] />
               </#if>
               <#if form.fields["prop_rma_reviewPeriod"]??>
                  <@formLib.renderField field=form.fields["prop_rma_reviewPeriod"] />
               </#if>
               <#if form.fields["prop_rma_reviewAsOf"]?? && form.mode == "view">
                  <@formLib.renderField field=form.fields["prop_rma_reviewAsOf"] />
               </#if>
            </div>
         </div>
      </#if>
      
      <#if form.mode == "view">
         <div class="set-panel">
            <div class="set-panel-heading">${msg("label.set.disposition")}</div>
            <div class="set-panel-body">
               <@formLib.renderField field=form.fields["prop_rmDispositionInstructions"] />
               <#if form.fields["prop_rma_recordSearchDispositionActionAsOf"]??>
                  <@formLib.renderField field=form.fields["prop_rma_recordSearchDispositionActionAsOf"] />
               </#if>
               <#if form.fields["prop_rma_cutOffDate"]??>
                  <@formLib.renderField field=form.fields["prop_rma_cutOffDate"] />
               </#if>
            </div>
         </div>
      </#if>
      
      <#list form.structure as item>
         <#if item.kind == "set" && item.id == "rm-custom">
            <@formLib.renderSet set=item />
            <#break>
         </#if>
      </#list>
   </div>
    
   <#if form.mode != "view">
      <@formLib.renderFormButtons formId=formId />
      </form>
   </#if>
   
</div>
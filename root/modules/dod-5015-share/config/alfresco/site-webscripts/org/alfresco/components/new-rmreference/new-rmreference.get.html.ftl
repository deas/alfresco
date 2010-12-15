<#assign controlId = htmlid + "-cntrl">
<#assign pickerId = controlId + "-picker">
<script type="text/javascript">//<![CDATA[
   new Alfresco.RM.NewReference("${args.htmlid}").setOptions(
   {
      controlId: "${controlId}",
      pickerId: "selection",
      nodeRef: "${(page.url.args.nodeRef)?js_string}",
      siteId: "${page.url.templateArgs.site!""}",
      parentNodeRef: "${(page.url.args.parentNodeRef)?js_string}",
      docName: "${(page.url.args.docName!"")?js_string}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div class="new-rmreference">
   <div class="heading">
      ${msg("label.title-new-reference")}
   </div>
</div>

<div class="form-container">
   <div class="caption"><span class="mandatory-indicator">*</span>${msg('label.required')}</div>
   <form action="action" method="post">
   <div class="form-fields" id="template_x002e_dod5015-edit-metadata_x002e_edit-metadata-form-fields">
      <div class="form-field">   
         <label for="new-ref-name">${msg("label.name")}:<span class="mandatory-indicator">*</span></label>
         <input id="new-ref-name" name="new-ref-name" value="" type="text">
      </div>

      <div class="form-field">
         <label for="${controlId}-showPicker-button">${msg("label.record-link")}:<span class="mandatory-indicator">*</span></label>
         <p id="selection"></p>
         <button id="${htmlid}-docPicker-showPicker-button" name="${htmlid}-docPicker-showPicker-button" >${msg("label.select")}</button>
      </div>

      <div class="form-field">
         <label for="record-rel">${msg("label.record-rel")}:<span class="mandatory-indicator">*</span></label>
         <select name="record-rel" id="record-rel">
            <#list reference_types as ref>
            <#if (ref.label?exists)>
               <option value="${ref.refId}">${ref.label?html}</option>
            <#else>
               <option value="${ref.refId}">${ref.source?html} / ${ref.target?html}</option>                  
            </#if>
            </#list>
         </select>   
      </div>
   </div>
   <div class="form-buttons">
      <button name="submitCreate" value="${msg('label.create')}" id="submitCreate" class="submitCreate" disabled>${msg('label.create')}</button>
      <button id="cancelCreate" value="${msg('label.cancel')}" class="cancelCreate">${msg('label.cancel')}</button>    
   </div>
   <input type="hidden" name="${controlId}" value="" id="${controlId}" />
   </form>   
</div>
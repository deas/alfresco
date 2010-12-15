<#assign isTrue=false>
<#if field.value?exists && field.value?is_boolean><#assign isTrue=field.value></#if>

<div class="form-field">
   <#if form.mode == "view">
      <div class="viewmode-field">
         <span class="viewmode-label">${field.label?html}:</span>
         <span class="viewmode-value"><#if isTrue>${msg("form.control.checkbox.yes")}<#else>${msg("form.control.checkbox.no")}</#if></span>
      </div>
   <#else>
      <input id="${fieldHtmlId}" type="hidden" name="${field.name}" value="<#if isTrue>true<#else>false</#if>" />
      <input id="${fieldHtmlId}-entry" type="checkbox" name="-" <#if field.description?exists>title="${field.description}"</#if>
             <#if isTrue> value="true" checked="checked"</#if> <#if field.disabled>disabled="true"</#if> 
             <#if field.control.params.styleClass?exists>class="${field.control.params.styleClass}"</#if> 
             onchange='javascript:YAHOO.util.Dom.get("${fieldHtmlId}").value=YAHOO.util.Dom.get("${fieldHtmlId}-entry").checked;YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);' />
      <label for="${fieldHtmlId}-entry" class="checkbox">${field.label?html}<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
   </#if>
</div>
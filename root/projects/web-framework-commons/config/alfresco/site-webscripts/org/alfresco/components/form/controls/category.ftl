<#include "common/picker.inc.ftl" />

<#assign controlId = fieldHtmlId + "-cntrl">

<script type="text/javascript">//<![CDATA[
(function()
{
   <@renderPickerJS field "picker" />
   picker.setOptions(
   {
      itemType: "cm:category",
      multipleSelectMode: ${(field.control.params.multipleSelectMode!true)?string},
      parentNodeRef: "alfresco://category/root",
      itemFamily: "category",
      maintainAddedRemovedItems: false,
      params: "${field.control.params.params!""}",
      createNewItemUri: "${field.control.params.createNewItemUri!}",
      createNewItemIcon: "${field.control.params.createNewItemIcon!}"
   });
})();
//]]></script>

<div class="form-field inlineable">
   <#if form.mode == "view">
      <div id="${controlId}" class="viewmode-field inlineable">
         <#if field.mandatory!false && field.value == "">
            <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
         </#if>
         <#if field.label != ""><span class="viewmode-label">${field.label?html}:</span></#if>
         <span id="${controlId}-currentValueDisplay" class="viewmode-value current-values"></span>
      </div>
   <#else>
      <#if field.label != "">
      <label for="${controlId}">${field.label?html}:<#if field.mandatory!false><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
      </#if>
      
      <div id="${controlId}" class="object-finder inlineable">
         
         <div id="${controlId}-currentValueDisplay" class="current-values inlineable"></div>
         
         <#if field.disabled == false>
            <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
            <div id="${controlId}-itemGroupActions" class="show-picker inlineable"></div>
         
            <@renderPickerHTML controlId />
         </#if>
      </div>
   </#if>
</div>

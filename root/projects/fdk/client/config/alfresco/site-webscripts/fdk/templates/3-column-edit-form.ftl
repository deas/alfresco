<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />

<#-- NOTE: Using this form in Share will require some custom CSS
           to reduce the default size of input fields.  -->

<#if formUI == "true">
   <@formLib.renderFormsRuntime formId=formId />
</#if>

<@formLib.renderFormContainer formId=formId>
   <#list form.structure as item>
      <#if item.kind == "set">
         <@renderSetWithColumns set=item />
      <#else>
         <@formLib.renderField field=form.fields[item.id] />
      </#if>
   </#list>
</@>


<#macro renderSetWithColumns set>
   <div class="set">
   <#if set.appearance?exists>
      <#if set.appearance == "fieldset">
         <fieldset><legend>${set.label}</legend>
      <#elseif set.appearance == "bordered-panel">
         <div class="set-bordered-panel">
            <div class="set-bordered-panel-heading">${set.label}</div>
            <div class="set-bordered-panel-body">
      <#elseif set.appearance == "panel">
         <div class="set-panel">
            <div class="set-panel-heading">${set.label}</div>
            <div class="set-panel-body">
      <#elseif set.appearance == "title">
         <div class="set-title">${set.label}</div>
      <#elseif set.appearance == "whitespace">
         <div class="set-whitespace"></div>
      </#if>
   </#if>
   
   <#list set.children as item>
      <#if item.kind == "set">
         <@renderSetWithColumns set=item />
      <#else>
         <#if (item_index % 3) == 0>
         <div class="yui-gb"><div class="yui-u first">
         <#else>
         <div class="yui-u">
         </#if>
         <@formLib.renderField field=form.fields[item.id] />
         </div>
         <#if ((item_index % 3) == 2) || !item_has_next></div></#if>
      </#if>
   </#list>
   
   <#if set.appearance?exists>
      <#if set.appearance == "fieldset">
         </fieldset>
      <#elseif set.appearance == "panel" || set.appearance == "bordered-panel">
            </div>
         </div>
      </#if>
   </#if>
   </div>
</#macro>

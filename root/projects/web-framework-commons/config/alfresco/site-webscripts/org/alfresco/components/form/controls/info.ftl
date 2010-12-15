<#if field.value?is_number>
   <#assign displayValue=field.value?c />
<#elseif field.value?is_boolean>
   <#if field.value>
      <#assign displayValue=msg("form.control.checkbox.yes") />
   <#else>
      <#assign displayValue=msg("form.control.checkbox.no") />
   </#if>
<#else>
   <#if field.value == "">
      <#assign displayValue=msg("form.control.novalue") />
   <#else>   
      <#if field.dataType == "date">
         <#assign displayValue=field.value?datetime("yyyy-MM-dd'T'HH:mm:ss")?string(msg("form.control.date-picker.view.date.format")) />
      <#elseif field.dataType == "datetime">
         <#assign displayValue=field.value?datetime("yyyy-MM-dd'T'HH:mm:ss")?string(msg("form.control.date-picker.view.time.format")) />
      <#else>
         <#assign displayValue=field.value?html />
      </#if>
   </#if>
</#if>

<div class="form-field">
   <div class="viewmode-field">
      <span class="viewmode-label">${field.label?html}:</span>
      <span class="viewmode-value">${displayValue}</span>
   </div>
</div>
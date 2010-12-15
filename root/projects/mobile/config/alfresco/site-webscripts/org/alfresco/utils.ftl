<#macro toolbar title parentTitle=''>
   <div class="toolbar">
      <h1>${title} <#if (parentTitle!='')><span>${parentTitle}</span></#if></h1>
      <#if (backButton??)>
         <a class="back button">${msg('label.backText')}</a>
      </#if>
      <#if (actionUrl??)>
         <a class="button action" href="${actionUrl}">${msg('label.actionText')}</a>         
      </#if>
   </div>
</#macro>


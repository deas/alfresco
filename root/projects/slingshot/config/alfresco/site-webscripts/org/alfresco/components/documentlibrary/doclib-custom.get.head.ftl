<#include "../component.head.inc">
<#-- Configured dependencies -->
<#if (config.global.doclibActions.dependencies)??>
   <#assign dependencies = config.global.doclibActions.dependencies />
   <#if dependencies.css??>
      <#list dependencies.css as cssFile>
         <#assign src = page.url.context + "/res/" + cssFile>
<link rel="stylesheet" type="text/css" href="${src?replace("/res//", "/res/")}" />
      </#list>
   </#if>
   <#if dependencies.js??>
      <#list dependencies.js as jsFile>
         <#assign src = page.url.context + "/res/" + jsFile>
<script type="text/javascript" src="${src?replace("/res//", "/res/")}"></script>
      </#list>
   </#if>
</#if>
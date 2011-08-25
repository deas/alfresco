<#include "../component.head.inc">
<#-- Configured dependencies -->
<#if (config.global.doclibActions.dependencies)??>
   <#assign dependencies = config.global.doclibActions.dependencies />
   <#if dependencies.css??>
      <#list dependencies.css as cssFile>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res${cssFile}" />
      </#list>
   </#if>
   <#if dependencies.js??>
      <#list dependencies.js as jsFile>
<@script type="text/javascript" src="${page.url.context}/res${jsFile}"></@script>
      </#list>
   </#if>
</#if>
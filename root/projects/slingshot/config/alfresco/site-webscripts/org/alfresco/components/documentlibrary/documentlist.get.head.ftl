<#include "../component.head.inc">
<#include "../form/form.get.head.ftl">
<!-- Document List -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/documentlibrary/documentlist.css" />
<@script type="text/javascript" src="${page.url.context}/res/components/documentlibrary/documentlist.js"></@script>

<#if (config.global.doclibActions.dependencies)??>
	<#assign dependencies = config.global.doclibActions.dependencies />
<!-- Configured dependencies -->
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
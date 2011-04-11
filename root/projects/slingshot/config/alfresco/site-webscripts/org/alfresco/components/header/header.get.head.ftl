<#include "../component.head.inc">
<!-- Header -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/header/header.css" />
<@script type="text/javascript" src="${page.url.context}/res/components/header/header.js"></@script>
<!-- About Share -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/modules/about-share.css" />
<@script type="text/javascript" src="${page.url.context}/res/modules/about-share.js"></@script>
<#if context.properties["editionInfo"].edition != "UNKNOWN">
<!-- License usage -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/console/license.css" />
</#if>
<!-- Configured dependencies -->
<#if config.global.header?? && config.global.header.dependencies?? && config.global.header.dependencies.css??>
   <#list config.global.header.dependencies.css as cssFile>
<link rel="stylesheet" type="text/css" href="${page.url.context}/res${cssFile}" />
   </#list>
</#if>
<#if config.global.header?? && config.global.header.dependencies?? && config.global.header.dependencies.js??>
   <#list config.global.header.dependencies.js as jsFile>
<script type="text/javascript" src="${page.url.context}/res${jsFile}"></script>
   </#list>
</#if>
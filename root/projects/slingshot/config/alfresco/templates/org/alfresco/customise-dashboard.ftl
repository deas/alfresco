<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/templates/dashboard/customise-dashboard.css" />
</@>

<@templateBody>
<div id="alf-hd">
   <@region id="header" scope="global" />
   <@region id="title" scope="template" />
   <@region id="navigation" scope="template" />
   <h1 class="sub-title"><#if page.titleId??>${msg(page.titleId)!page.title}<#else>${page.title}</#if></h1>
</div>
<#if access>
<div id="bd">
   <@region id="customise-layout" scope="template" />
   <@region id="customise-dashlets" scope="template" />
</div>
</#if>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" />
   </div>
</@>
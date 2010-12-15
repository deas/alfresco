<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/templates/dashboard/customise-dashboard.css" />
</@>

<@templateBody>
<div id="alf-hd">
   <@region id="header" scope="global" protected=true />
   <@region id="title" scope="template" protected=true />
   <@region id="navigation" scope="template" protected=true />
   <h1 class="sub-title"><#if page.titleId??>${msg(page.titleId)!page.title}<#else>${page.title}</#if></h1>
</div>
<#if access>
<div id="bd">
   <@region id="customise-layout" scope="template" protected=true />
   <@region id="customise-dashlets" scope="template" protected=true />
</div>
</#if>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>
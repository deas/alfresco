<#include "include/alfresco-template.ftl" />
<@templateHeader/>

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="template" protected=true />
      <h1 class="sub-title"><#if page.titleId??>${msg(page.titleId)!page.title}<#else>${page.title}</#if></h1>
   </div>
   <#if access>
   <div id="bd">
      <@region id="customise-pages" scope="template" protected=true />
   </div>
   </#if>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>
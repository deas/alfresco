<#include "include/alfresco-template.ftl" />
<@templateHeader/>

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" />
      <@region id="title" scope="template" />
      <h1 class="sub-title"><#if page.titleId??>${msg(page.titleId)!page.title}<#else>${page.title}</#if></h1>
   </div>
   <#if access>
   <div id="bd">
      <@region id="customise-pages" scope="template" />
   </div>
   </#if>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" />
   </div>
</@>
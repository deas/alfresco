<#include "./hybrid-template.ftl" />
<@templateHeader />

<@templateBody>
   <div id="content">
      <#assign regionId = page.url.templateArgs.webscript?replace("/", "-")/>
      <@createComponent scope="global" regionId="${regionId}" sourceId="global" uri="/${page.url.templateArgs.webscript}"/>
      <@region scope="global" id="${regionId}" chromeless="true"/>
   </div>
</@>

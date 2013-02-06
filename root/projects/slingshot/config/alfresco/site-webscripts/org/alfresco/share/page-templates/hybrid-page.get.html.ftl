<#include "./hybrid-template.ftl" />
<@templateHeader />

<@templateBody>
   <div id="alf-hd">
      <@region scope="global" id="share-header" chromeless="true"/>
   </div>
   <div id="content">
      <#assign regionId = page.url.templateArgs.webscript?replace("/", "-")/>
      <@createComponent scope="global" regionId="${regionId}" sourceId="global" uri="/${page.url.templateArgs.webscript}"/>
      <@region scope="global" id="${regionId}" chromeless="true"/>
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" />
   </div>
</@>
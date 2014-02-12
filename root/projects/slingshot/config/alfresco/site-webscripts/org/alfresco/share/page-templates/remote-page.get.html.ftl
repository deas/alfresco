<#include "./hybrid-template.ftl" />
<@templateHeader />

<@templateBody>
   <div id="content">
      <@createComponent scope="global" regionId="remote-page" sourceId="global" uri="/remote-page"/>
      <@region scope="global" id="remote-page" chromeless="true"/>
   </div>
</@>

<@templateFooter>
</@>
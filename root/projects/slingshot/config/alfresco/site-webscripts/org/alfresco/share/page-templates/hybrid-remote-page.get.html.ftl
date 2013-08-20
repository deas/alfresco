<#include "./hybrid-template.ftl" />
<@templateHeader />

<@templateBody>
   <div id="alf-hd">
      <@region scope="global" id="share-header" chromeless="true"/>
   </div>
   <div id="content">
      <@createComponent scope="global" regionId="remote-page" sourceId="global" uri="/remote-page"/>
      <@region scope="global" id="remote-page" chromeless="true"/>
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" />
   </div>
</@>
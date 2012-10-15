<#include "include/alfresco-template.ftl" />
<#import "import/alfresco-layout.ftl" as layout />
<@templateHeader />

<@templateBody>
   <@markup id="alf-hd">
   <div id="alf-hd">
      <@region id="header" scope="global" />
      <@region id="title" scope="template" />
   </div>
   </@>
   <@markup id="bd">
   <div id="bd">
      <@region id="content-viewer" scope="template" />
   </div>
   </@>
</@>

<@templateFooter>
   <@markup id="alf-ft">
   <div id="alf-ft">
      <@region id="footer" scope="global" />
   </div>
   </@>
</@>
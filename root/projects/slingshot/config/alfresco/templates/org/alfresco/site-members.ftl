<#include "include/alfresco-template.ftl" />
<@templateHeader />

<@templateBody>
   <@markup id="alf-hd">
   <div id="alf-hd">
      <@region id="header" scope="global" />
      <@region id="title" scope="template" />
      <@region id="navigation" scope="template" />
   </div>
   </@>
   <@markup id="bd">
   <div id="bd">
      <@region id="membersbar" scope="template" />
      <@region id="site-members" scope="template" />
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
<#include "include/alfresco-template.ftl" />
<@templateHeader />

<@templateBody>
   <@markup id="alf-hd">
   <div id="alf-hd">
      <@region id="header" scope="global" />
      <@region id="title" scope="template" />
      <@region id="toolbar" scope="template" />
   </div>
   </@>
   <@markup id="bd">
   <div id="bd">
      <@region id="user-profile" scope="template"  />
      <@region id="html-upload" scope="template" />
      <@region id="flash-upload" scope="template" />
      <@region id="file-upload" scope="template" />
      <@region id="dnd-upload" scope="template"/>
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
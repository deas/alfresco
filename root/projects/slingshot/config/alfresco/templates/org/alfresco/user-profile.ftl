<#include "include/alfresco-template.ftl" />
<@templateHeader />

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" />
      <@region id="title" scope="template" />
      <@region id="toolbar" scope="template" />
   </div>
   <div id="bd">
      <@region id="user-profile" scope="template"  />
      <@region id="html-upload" scope="template" />
      <@region id="flash-upload" scope="template" />
      <@region id="file-upload" scope="template" />
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" />
   </div>
</@>
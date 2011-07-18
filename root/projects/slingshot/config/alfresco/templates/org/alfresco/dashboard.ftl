<#include "include/alfresco-template.ftl" />
<#import "import/alfresco-layout.ftl" as layout />
<@templateHeader "transitional" />

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" />
      <@region id="title" scope="page" />
      <@region id="navigation" scope="page" />
   </div>
   <div id="alf-full-width">
      <@region id="full-width-dashlet" scope="page" />
   </div>
   <div id="bd">
      <@layout.grid gridColumns gridClass "component" />
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" />
   </div>
</@>
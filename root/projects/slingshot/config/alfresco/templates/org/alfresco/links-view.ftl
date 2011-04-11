<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <!-- General Links Assets -->
   <@script type="text/javascript" src="${page.url.context}/res/components/links/linksdiscuss-common.js"></@script>
   <@script type="text/javascript" src="${page.url.context}/res/components/links/links-common.js"></@script>
   <@templateHtmlEditorAssets />
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="template" protected=true />
      <@region id="navigation" scope="template" protected=true />
   </div>
   <div id="bd">
      <@region id="linksview" scope="template" protected=true />
      <@region id="comments" scope="template" protected=true />
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>
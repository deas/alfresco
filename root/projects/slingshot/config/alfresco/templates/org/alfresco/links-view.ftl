<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <!-- General Links Assets -->
   <@script type="text/javascript" src="${page.url.context}/res/components/links/linksdiscuss-common.js"></@script>
   <@script type="text/javascript" src="${page.url.context}/res/components/links/links-common.js"></@script>
   <@templateHtmlEditorAssets />
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" />
      <@region id="title" scope="template" />
      <@region id="navigation" scope="template" />
   </div>
   <div id="bd">
      <@region id="linksview" scope="template" />
      <@region id="comments" scope="template" />
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" />
   </div>
</@>
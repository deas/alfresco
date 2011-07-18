<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <!-- General Discussion Assets -->
   <@script type="text/javascript" src="${page.url.context}/res/components/blog/blogdiscussions-common.js"></@script>
   <@script type="text/javascript" src="${page.url.context}/res/components/discussions/discussions-common.js"></@script>
   <@templateHtmlEditorAssets />
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" />
      <@region id="title" scope="template" />
      <@region id="navigation" scope="template" />
   </div>
   <div id="bd">
      <@region id="toolbar" scope="template" />
      <@region id="topic" scope="template" />
      <@region id="replies" scope="template" />
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" />
   </div>
</@>

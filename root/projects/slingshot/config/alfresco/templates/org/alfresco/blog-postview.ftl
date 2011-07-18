<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <!-- General Blog Assets -->
   <@script type="text/javascript" src="${page.url.context}/res/components/blog/blogdiscussions-common.js"></@script>
   <@script type="text/javascript" src="${page.url.context}/res/components/blog/blog-common.js"></@script>
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
      <@region id="postview" scope="template" />
      <@region id="comments" scope="template" />
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" />
   </div>
</@>
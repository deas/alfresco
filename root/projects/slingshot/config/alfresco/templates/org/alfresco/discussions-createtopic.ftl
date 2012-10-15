<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <!-- General Discussion Assets -->
   <@script type="text/javascript" src="${url.context}/res/components/blog/blogdiscussions-common.js"></@script>
   <@script type="text/javascript" src="${url.context}/res/components/discussions/discussions-common.js"></@script>
   <@templateHtmlEditorAssets />     
</@>

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
      <@region id="createtopic" scope="template" />
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
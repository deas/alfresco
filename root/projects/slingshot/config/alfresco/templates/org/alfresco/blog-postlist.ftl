<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <script type="text/javascript">//<![CDATA[
      new Alfresco.widget.Resizer("Blog-PostList");
   //]]></script>
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
      <div class="yui-t1" id="alfresco-blog-postlist">
         <div id="yui-main">
            <div class="yui-b" id="alf-content">
               <@region id="toolbar" scope="template" />
               <@region id="postlist" scope="template" />
            </div>
         </div>
         <div class="yui-b" id="alf-filters">
            <@region id="filters" scope="template" />
            <@region id="archives" scope="template" />
            <@region id="tags" scope="template" />
         </div>
      </div>
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" />
   </div>
</@>
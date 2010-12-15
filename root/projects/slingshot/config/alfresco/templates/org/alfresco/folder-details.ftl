<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/blog/postview.css" />
   <@script type="text/javascript" src="${page.url.context}/res/components/blog/blogdiscussions-common.js"></@script>
   <@script type="text/javascript" src="${page.url.context}/res/components/blog/blog-common.js"></@script>
   <@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></@script>
   <@script type="text/javascript" src="${page.url.context}/res/templates/folder-details/folder-details.js"></@script>   
   <#if doclibType?starts_with("dod5015")><@script type="text/javascript" src="${page.url.context}/res/templates/folder-details/dod5015-folder-details.js"></@script></#if>
   <@templateHtmlEditorAssets />
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id=appType + "header" scope="global" protected=true />
      <@region id=appType + doclibType + "title" scope="template" protected=true />
      <@region id=appType + doclibType + "navigation" scope="template" protected=true />
   </div>
   <div id="bd">
      <@region id=doclibType + "actions-common" scope="template" protected=true />
      <@region id=doclibType + "path" scope="template" protected=true />

   <#if doclibType?starts_with("dod5015")>
      <div class="yui-g"> 
         <div class="yui-u first share-form">
            <@region id=doclibType + "folder-metadata-header" scope="template" protected=true />
            <@region id=doclibType + "folder-metadata" scope="template" protected=true />
            <@region id=doclibType + "folder-info" scope="template" protected=true />
         </div>
         <div class="yui-u">
            <@region id=doclibType + "folder-actions" scope="template" protected=true />
            <@region id=doclibType + "folder-links" scope="template" protected=true />
         </div>
      </div>
   <#else>   
      <div class="yui-g">
         <div class="yui-g first">
            <div class="folder-details-comments">
               <@region id=doclibType + "comments" scope="template" protected=true />
               <@region id=doclibType + "createcomment" scope="template" protected=true />
            </div>
         </div>
         <div class="yui-g"> 
            <div class="yui-u first">
               <@region id=doclibType + "folder-metadata-header" scope="template" protected=true />
               <@region id=doclibType + "folder-metadata" scope="template" protected=true />
               <@region id=doclibType + "folder-info" scope="template" protected=true />
            </div>
            <div class="yui-u">
               <@region id=doclibType + "folder-actions" scope="template" protected=true />
               <@region id=appType + doclibType + "folder-links" scope="template" protected=true />
            </div>
         </div>
      </div>
   </#if>

   </div>
   
   <script type="text/javascript">//<![CDATA[
   new ${jsType}().setOptions(
   {
      nodeRef: new Alfresco.util.NodeRef("${url.args.nodeRef?js_string}"),
      siteId: "${page.url.templateArgs.site!""}",
      rootNode: "${rootNode}"
   });
   //]]></script>

</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>

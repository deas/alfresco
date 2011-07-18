<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/blog/postview.css" />
   <@script type="text/javascript" src="${page.url.context}/res/components/blog/blogdiscussions-common.js"></@script>
   <@script type="text/javascript" src="${page.url.context}/res/components/blog/blog-common.js"></@script>
   <@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></@script>
   <@script type="text/javascript" src="${page.url.context}/res/templates/folder-details/folder-details.js"></@script>      
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global"/>
      <@region id="title" scope="template"/>
      <@region id="navigation" scope="template"/>
   </div>
   <div id="bd">
      <@region id="actions-common" scope="template"/>
      <@region id="path" scope="template"/>

      <div class="yui-g">
         <div class="yui-g first">
            <div class="folder-details-comments">
               <@region id="comments" scope="template"/>
            </div>
         </div>
         <div class="yui-g"> 
            <div class="yui-u first">
               <@region id="folder-metadata-header" scope="template"/>
               <@region id="folder-metadata" scope="template"/>
               <@region id="folder-info" scope="template"/>
            </div>
            <div class="yui-u">
               <@region id="folder-actions" scope="template"/>
               <@region id="folder-links" scope="template"/>
            </div>
         </div>
      </div>

   </div>
   
   <script type="text/javascript">//<![CDATA[
   new Alfresco.FolderDetails().setOptions(
   {
      nodeRef: new Alfresco.util.NodeRef("${url.args.nodeRef?js_string}"),
      siteId: "${page.url.templateArgs.site!""}",
      rootNode: "${(config.scoped["RepositoryLibrary"]["root-node"].getValue())!"alfresco://company/home"}"
   });
   //]]></script>

</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global"/>
   </div>
</@>

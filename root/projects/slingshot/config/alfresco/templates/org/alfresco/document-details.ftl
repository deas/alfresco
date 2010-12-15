<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/blog/postview.css" />
   <@script type="text/javascript" src="${page.url.context}/res/components/blog/blogdiscussions-common.js"></@script>
   <@script type="text/javascript" src="${page.url.context}/res/components/blog/blog-common.js"></@script>
   <@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></@script>
   <@script type="text/javascript" src="${page.url.context}/res/templates/document-details/document-details.js"></@script>
   <#if doclibType?starts_with("dod5015")><@script type="text/javascript" src="${page.url.context}/res/templates/document-details/dod5015-document-details.js"></@script></#if>
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
      <@region id=doclibType + "actions" scope="template" protected=true />
      <@region id=doclibType + "path" scope="template" protected=true />

      <div class="yui-g">
         <div class="yui-g first">
         <#if (config.scoped['DocumentDetails']['document-details'].getChildValue('display-web-preview') == "true")>
            <@region id=doclibType + "web-preview" scope="template" protected=true />
         </#if>
         <#if doclibType?starts_with("dod5015")>
            <@region id=doclibType + "events" scope="template" protected=true />
         <#else>
            <div class="document-details-comments">
               <@region id=doclibType + "comments" scope="template" protected=true />
               <@region id=doclibType + "createcomment" scope="template" protected=true />
            </div>
         </#if>
         </div>
         <div class="yui-g"> 
            <div class="yui-u first">
               <@region id=doclibType + "document-metadata-header" scope="template" protected=true />
               <@region id=doclibType + "document-metadata" scope="template" protected=true />
               <@region id=doclibType + "document-info" scope="template" protected=true />
               <@region id=doclibType + "document-workflows" scope="template" protected=true />
               <@region id=doclibType + "document-versions" scope="template" protected=true />
            </div>
            <div class="yui-u">
               <@region id=doclibType + "document-actions" scope="template" protected=true />
               <@region id=appType + doclibType + "document-links" scope="template" protected=true />
               <#if doclibType?starts_with("dod5015")>
                  <@region id=doclibType + "document-references" scope="template" protected=true />                                 
               </#if>
            </div>
         </div>
      </div>

      <@region id="html-upload" scope="template" protected=true />
      <@region id="flash-upload" scope="template" protected=true />
      <@region id="file-upload" scope="template" protected=true />
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

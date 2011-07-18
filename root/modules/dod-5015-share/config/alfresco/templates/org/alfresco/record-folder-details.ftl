<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></@script>
   <@script type="text/javascript" src="${page.url.context}/res/templates/folder-details/folder-details.js"></@script>   
   <@script type="text/javascript" src="${page.url.context}/res/templates/folder-details/${doclibType}folder-details.js"></@script>
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
      <div class="yui-gb">
         <div class="yui-u first">
            <@region id="events" scope="template"/>
         </div>
         <div class="yui-u">
            <@region id="folder-metadata-header" scope="template"/>
            <@region id="folder-metadata" scope="template"/>
         </div>
         <div class="yui-u">
            <@region id="folder-actions" scope="template"/>
            <@region id="folder-links" scope="template"/>
         </div>
      </div>
   </div>
   
   <script type="text/javascript">//<![CDATA[
   new Alfresco.RecordsFolderDetails().setOptions(
   {
      nodeRef: new Alfresco.util.NodeRef("${url.args.nodeRef}"),
      siteId: "${page.url.templateArgs.site!""}"
   });
   //]]></script>

</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global"/>
   </div>
</@>

<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></@script>
   <@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/folder-details/folder-details-panel.css" />
   <@templateHtmlEditorAssets />
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global"/>
      <@region id="title" scope="template"/>
      <@region id="navigation" scope="template"/>
   </div>
   <div id="bd">
      <@region id="actions-common" scope="template"/>
      <@region id="actions" scope="template"/>
      <@region id="folder-header" scope="template"/>
      <div class="yui-gc">
         <div class="yui-u first">
            <@region id="comments" scope="template"/>
         </div>
         <div class="yui-u">
            <@region id="folder-actions" scope="template"/>
            <@region id="folder-tags" scope="template"/>
            <@region id="folder-links" scope="template"/>
            <@region id="folder-metadata" scope="template"/>
            <@region id="folder-permissions" scope="template"/>
         </div>
      </div>
   </div>
   <@region id="doclib-custom" scope="template"/>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global"/>
   </div>
</@>

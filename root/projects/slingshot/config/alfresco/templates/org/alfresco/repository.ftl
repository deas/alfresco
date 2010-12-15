<#include "include/alfresco-template.ftl" />
<#include "include/documentlibrary.inc.ftl" />
<@templateHeader>
   <@documentLibraryJS />
   <script type="text/javascript">//<![CDATA[
      new Alfresco.widget.Resizer("Repository");
   //]]></script>
   <@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></@script>
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id=appType + "header" scope="global" protected=true />
      <@region id=appType + "title" scope="template" protected=true />
   </div>
   <div id="bd">
      <@region id="actions-common" scope="template" protected=true />
      <@region id="actions" scope="template" protected=true />
      <div class="yui-t1" id="alfresco-repository">
         <div id="yui-main">
            <div class="yui-b" id="alf-content">
               <@region id="toolbar" scope="template" protected=true />
               <@region id="documentlist" scope="template" protected=true />
            </div>
         </div>
         <div class="yui-b" id="alf-filters">
            <@region id="filter" scope="template" protected=true />
            <@region id="tree" scope="template" protected=true />
            <@region id="categories" scope="template" protected=true />
            <@region id="tags" scope="template" protected=true />
         </div>
      </div>

      <@region id="html-upload" scope="template" protected=true />
      <@region id="flash-upload" scope="template" protected=true />
      <@region id="file-upload" scope="template" protected=true />
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>
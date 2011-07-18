<#include "include/alfresco-template.ftl" />
<#include "include/documentlibrary.inc.ftl" />
<@templateHeader>
   <@documentLibraryJS />
   <script type="text/javascript">//<![CDATA[
      new Alfresco.widget.Resizer("DocumentLibrary");
   //]]></script>
   <@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></@script>
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global"/>
      <@region id="title" scope="template"/>
      <@region id="navigation" scope="template"/>
   </div>
   <div id="bd">
      <@region id="actions-common" scope="template"/>
      <div class="yui-t1" id="alfresco-documentlibrary">
         <div id="yui-main">
            <div class="yui-b" id="alf-content">
               <@region id="toolbar" scope="template"/>
               <@region id="documentlist" scope="template"/>
            </div>
         </div>
         <div class="yui-b" id="alf-filters">
            <@region id="filter" scope="template"/>
            <@region id="tree" scope="template"/>
            <@region id="tags" scope="template"/>
         </div>
      </div>

      <@region id="html-upload" scope="template"/>
      <@region id="flash-upload" scope="template"/>
      <@region id="file-upload" scope="template"/>
      <@region id="dnd-upload" scope="template"/>
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global"/>
   </div>
</@>
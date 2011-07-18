<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <script type="text/javascript">//<![CDATA[
      new Alfresco.widget.Resizer("Console").setOptions(
      {
         initialWidth: 190
      });
   //]]></script>
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" />
      <@region id="title" scope="page" />
   </div>
   
   <div id="bd">
      <div class="yui-t1" id="alfresco-console">
         <div id="yui-main">
            <div class="yui-b" id="alf-content">
               <@region id="ctool" scope="page" />
            </div>
         </div>
         <div class="yui-b" id="alf-filters">
            <@region id="tools" scope="template" />
         </div>
      </div>
      <@region id="html-upload" scope="template" />
      <@region id="flash-upload" scope="template" />
      <@region id="file-upload" scope="template" />
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" />
   </div>
</@>
<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <script type="text/javascript">//<![CDATA[
      new Alfresco.widget.Resizer("${page.id?js_string}").setOptions(
      {
         divLeft: "divLeft",
         divRight: "divRight",
         initialWidth: 215
      });
   //]]></script>
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global"/>
      <@region id="title" scope="page"/>
   </div>
   <div id="bd">
      <div class="yui-t1">
         <div id="yui-main">
            <div id="divRight">
               <@region id="right-column" scope="page"/>
            </div>
         </div>
         <div id="divLeft">
            <@region id="left-column" scope="page"/>
         </div>
      </div>
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" />
   </div>
</@>


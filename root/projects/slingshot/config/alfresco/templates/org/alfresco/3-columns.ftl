<#include "include/alfresco-template.ftl" />
<@templateHeader />

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global"/>
      <@region id="title" scope="page"/>
   </div>
   <div id="bd">
      <div class="yui-gb">
         <div class="yui-u first">
         <@region id="left-column" scope="page"/>
         </div>
         <div class="yui-u">
         <@region id="middle-column" scope="page"/>
         </div>
         <div class="yui-u">
         <@region id="right-column" scope="page"/>
         </div>
      </div>
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global"/>
   </div>
</@>
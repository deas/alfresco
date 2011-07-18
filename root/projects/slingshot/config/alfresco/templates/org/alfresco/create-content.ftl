<#include "include/alfresco-template.ftl" />
<@templateHeader />

<@templateBody>
   <div id="alf-hd">
      <@region id=appType + "header" scope="global" />
      <@region id=appType + doclibType + "title" scope="template" />
      <@region id=appType + doclibType + "navigation" scope="template" />
   </div>
   <div id="bd">
      <div class="share-form">
         <@region id="create-content-mgr" scope="template" />
         <@region id="create-content" scope="template" />
      </div>
   </div>
   
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" />
   </div>
</@>

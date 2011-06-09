<#include "include/alfresco-template.ftl" />
<@templateHeader />

<@templateBody>
   <div id="alf-hd">
      <@region id=appType + "header" scope="global" protected=true />
      <@region id=appType + doclibType + "title" scope="template" protected=true />
      <@region id=appType + doclibType + "navigation" scope="template" protected=true />
   </div>
   <div id="bd">
      <div class="share-form">
          <@region id=doclibType + "path" scope="template" protected=true />
         <@region id="manage-translations" scope="template" protected=true />
      </div>
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>

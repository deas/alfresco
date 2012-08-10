<#if folderDetails?? && syncEnabled>
   <!-- Parameters and libs -->
   <#include "../../include/alfresco-macros.lib.ftl" />
   <#assign el=args.htmlid/>

   <!-- Markup -->
   <div class="document-sync folder-details-panel">
      <h2 id="${el}-heading" class="thin dark">
         ${msg("heading")}
         <span id="${el}-document-sync-twister-actions" class="alfresco-twister-actions hidden">
            {syncActionButtons}
         </span>
      </h2>
      <div id="${el}-formContainer" class="document-sync-formContainer"></div>
      <script type="text/javascript">//<![CDATA[
         Alfresco.util.createTwister("${el}-heading", "FolderSync");
      //]]></script>
   </div>

   <!-- Javascript instance -->
   <script type="text/javascript">//<![CDATA[
      new Alfresco.DocumentSync("${el}").setOptions(
      {
         nodeRef: "${nodeRef}",
         site: <#if site??>"${site?js_string}"<#else>null</#if>,
         documentDetails: ${folderDetails},
         syncMode: "${syncMode}"
      }).setMessages(
         ${messages}
      );
   //]]></script>
</#if>
<#if allowMetaDataUpdate??>
   <#include "../../include/alfresco-macros.lib.ftl" />
   <div class="document-metadata-header document-details-panel">
      <h2 class="thin dark">
         ${msg("heading")}
         <#if allowMetaDataUpdate>
         <span class="alfresco-twister-actions">
            <a href="${siteURL("edit-metadata?nodeRef="+nodeRef?js_string)}" class="edit" title="${msg("label.edit")}">&nbsp;</a>
         </span>
         </#if>
      </h2>
   </div>
</#if>
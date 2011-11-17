<!-- Parameters and libs -->
<#include "../../include/alfresco-macros.lib.ftl" />
<#assign el=args.htmlid/>

<#if allowMetaDataUpdate??>
    <!-- Markup -->
    <div class="folder-metadata-header folder-details-panel">
       <h2 id="${el}-heading" class="thin dark">
          ${msg("heading")}
          <#if allowMetaDataUpdate>
          <span class="alfresco-twister-actions">
             <a href="${siteURL("edit-metadata?nodeRef=" + nodeRef?url)}" class="edit" title="${msg("label.edit")}">&nbsp;</a>
          </span>
          </#if>
       </h2>
       <div id="${el}-formContainer"></div>
       <script type="text/javascript">//<![CDATA[
          Alfresco.util.createTwister("${el}-heading", "FolderMetadata");
       //]]></script>
    </div>
    
    <!-- Javascript instance -->
    <script type="text/javascript">//<![CDATA[
       new Alfresco.FolderMetadata("${el}").setOptions(
       {
          nodeRef: "${nodeRef}",
          site: <#if site??>"${site?js_string}"<#else>null</#if>,
          formId: <#if formId??>"${formId?js_string}"<#else>null</#if>
       }).setMessages(
          ${messages}
       );
    //]]></script>
</#if>

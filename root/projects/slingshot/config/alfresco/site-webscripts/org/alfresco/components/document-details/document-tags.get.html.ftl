<#if allowMetaDataUpdate??>
   <#include "../../include/alfresco-macros.lib.ftl" />
   <#assign el=args.htmlid?html>
   <div id="${el}-body" class="document-tags document-details-panel">

      <h2 id="${el}-heading" class="thin dark">
         ${msg("label.tags")}
         <#if allowMetaDataUpdate>
            <span class="alfresco-twister-actions">
               <a href="${siteURL("edit-metadata?nodeRef="+nodeRef?js_string)}" class="edit" title="${msg("label.edit")}">&nbsp;</a>
            </span>
         </#if>
      </h2>

      <div class="panel-body">
         <#if tags?size == 0>
            ${msg("label.none")}
         <#else>
            <#list tags as tag>
               <span class="tag">${tag?html}</span>
            </#list>
         </#if>
      </div>

      <script type="text/javascript">//<![CDATA[
         Alfresco.util.createTwister("${el}-heading", "DocumentTags");
      //]]></script>
   </div>
</#if>
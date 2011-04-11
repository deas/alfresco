<#if displayName??>
   <#include "../../include/alfresco-macros.lib.ftl" />
   <#assign el=args.htmlid?html>
   <script type="text/javascript">//<![CDATA[
      new Alfresco.DocumentPermissions("${el}").setOptions(
      {
         nodeRef: "${nodeRef?js_string}",
         siteId: <#if site??>"${site?js_string}"<#else>null</#if>,
         displayName: "${displayName?js_string}",
         roles: [<#list roles as r>"${r}"<#if r_has_next>, </#if></#list>]
      }).setMessages(${messages});
   //]]></script>

   <div id="${el}-body" class="document-permissions document-details-panel">

      <h2 id="${el}-heading" class="thin dark">
         ${msg("document-info.permissions")}
         <#if allowPermissionsUpdate>
            <span class="alfresco-twister-actions">
               <a href="#" name=".onManagePermissionsClick" class="${el} edit" title="${msg("label.edit")}">&nbsp;</a>
            </span>
         </#if>
      </h2>

      <div class="form-container">
         <div class="form-fields">
            <div class="viewmode-field">
               <span class="viewmode-label">${msg("document-info.managers")}:</span>
               <span class="viewmode-value">${msg("document-info.role." + managers)}</span>
            </div>
            <div class="viewmode-field">
               <span class="viewmode-label">${msg("document-info.collaborators")}:</span>
               <span class="viewmode-value">${msg("document-info.role." + collaborators)}</span>
            </div>
            <div class="viewmode-field">
               <span class="viewmode-label">${msg("document-info.contributors")}:</span>
               <span class="viewmode-value">${msg("document-info.role." + contributors)}</span>
            </div>
            <div class="viewmode-field">
               <span class="viewmode-label">${msg("document-info.consumers")}:</span>
               <span class="viewmode-value">${msg("document-info.role." + consumers)}</span>
            </div>
            <div class="viewmode-field">
               <span class="viewmode-label">${msg("document-info.everyone")}:</span>
               <span class="viewmode-value">${msg("document-info.role." + everyone)}</span>
            </div>
         </div>
      </div>

      <script type="text/javascript">//<![CDATA[
      Alfresco.util.createTwister("${el}-heading", "DocumentPermissions");
      //]]></script>

   </div>
</#if>
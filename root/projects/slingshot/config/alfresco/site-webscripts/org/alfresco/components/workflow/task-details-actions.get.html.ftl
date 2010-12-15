<#include "../../include/alfresco-macros.lib.ftl" />
<#assign el=args.htmlid?js_string>
<script type="text/javascript">//<![CDATA[
new Alfresco.component.TaskDetailsActions("${el}").setOptions(
{
   defaultUrl: "${siteURL("my-workflows")}",
   referrer: <#if page.url.args.referrer??>"${page.url.args.referrer?js_string}"<#else>null</#if>,
   nodeRef: <#if page.url.args.nodeRef??>"${page.url.args.nodeRef?js_string}"<#else>null</#if>
}).setMessages(
   ${messages}
);
//]]></script>
<div id="${el}-body" class="form-manager task-details-actions">
   <div class="actions hidden">
      <button id="${el}-edit">${msg("button.edit")}</button>
   </div>
</div>

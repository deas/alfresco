<#include "../../include/alfresco-macros.lib.ftl" />
<#assign el=args.htmlid?js_string>
<script type="text/javascript">//<![CDATA[
new Alfresco.component.WorkflowDetailsActions("${el}").setOptions(
{
   submitUrl: "${siteURL("my-tasks")}"
}).setMessages(
   ${messages}
);
//]]></script>
<div id="${el}-body" class="form-manager workflow-details-actions">
   <div id="${el}-cancel-button" class="actions hidden">
      <button id="${el}-cancel">${msg("button.cancelWorkflow")}</button>
   </div>
   <div id="${el}-delete-button" class="actions hidden">
      <button id="${el}-delete">${msg("button.deleteWorkflow")}</button>
   </div>
</div>

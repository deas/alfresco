<#import "../generic-form-tool.lib.ftl" as gft>
<@gft.renderPanel config.script.config "workflow"/>

<div class="workflow-tools">
   <h1 class="thin dark">${msg("tool.workflow.activiti.tools")}</h1>
   <a target="_blank" href="${url.context}/proxy/activiti-admin">${msg("tool.workflow.activiti.admin.link")}</a>
</div>
<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/workflow.lib.js">

// Widget instantiation metadata...
model.widgets = [];
var workflowDetailsActions = {};
workflowDetailsActions.name = "Alfresco.component.WorkflowDetailsActions";
workflowDetailsActions.useMessages = true;
workflowDetailsActions.useOptions = true;
workflowDetailsActions.options = {};
workflowDetailsActions.options.submitUrl = getSiteUrl("my-tasks");
model.widgets.push(workflowDetailsActions);
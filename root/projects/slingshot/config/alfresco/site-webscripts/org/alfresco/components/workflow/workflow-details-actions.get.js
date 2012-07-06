<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/workflow.lib.js">

// Widget instantiation metadata...
model.webScriptWidgets = [];
var workflowDetailsActions = {};
workflowDetailsActions.name = "Alfresco.component.WorkflowDetailsActions";
workflowDetailsActions.provideMessages = true;
workflowDetailsActions.provideOptions = true;
workflowDetailsActions.options = {};
workflowDetailsActions.options.submitUrl = getSiteUrl("my-tasks");
model.webScriptWidgets.push(workflowDetailsActions);
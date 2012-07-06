// Widget instantiation metadata...
model.webScriptWidgets = [];
var workflowDetailsHeader = {};
workflowDetailsHeader.name = "Alfresco.component.WorkflowDetailsHeader";
workflowDetailsHeader.provideMessages = true;
workflowDetailsHeader.provideOptions = true;
workflowDetailsHeader.options = {};
workflowDetailsHeader.options.taskId = page.url.args.taskId;
model.webScriptWidgets.push(workflowDetailsHeader);
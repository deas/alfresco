// Widget instantiation metadata...
model.widgets = [];
var workflowDetailsHeader = {};
workflowDetailsHeader.name = "Alfresco.component.WorkflowDetailsHeader";
workflowDetailsHeader.useMessages = true;
workflowDetailsHeader.useOptions = true;
workflowDetailsHeader.options = {};
workflowDetailsHeader.options.taskId = page.url.args.taskId;
model.widgets.push(workflowDetailsHeader);
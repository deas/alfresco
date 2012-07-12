// Widget instantiation metadata...
model.widgets = [];
var workflowForm = {};
workflowForm.name = "Alfresco.component.WorkflowForm";
workflowForm.useMessages = true;
workflowForm.useOptions = true;
workflowForm.options = {};
workflowForm.options.referrer = page.url.args.referrer;
workflowForm.options.nodeRef = page.url.args.nodeRef;
model.widgets.push(workflowForm);
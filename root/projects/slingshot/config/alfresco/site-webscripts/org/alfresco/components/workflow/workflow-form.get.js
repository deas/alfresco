// Widget instantiation metadata...
model.webScriptWidgets = [];
var workflowForm = {};
workflowForm.name = "Alfresco.component.WorkflowForm";
workflowForm.provideMessages = true;
workflowForm.provideOptions = true;
workflowForm.options = {};
workflowForm.options.referrer = page.url.args.referrer;
workflowForm.options.nodeRef = page.url.args.nodeRef;
model.webScriptWidgets.push(workflowForm);
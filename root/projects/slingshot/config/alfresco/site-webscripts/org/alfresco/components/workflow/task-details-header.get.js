// Widget instantiation metadata...
model.webScriptWidgets = [];
var taskDetailsHeader = {};
taskDetailsHeader.name = "Alfresco.component.TaskDetailsHeader";
taskDetailsHeader.provideMessages = true;
taskDetailsHeader.provideOptions = true;
taskDetailsHeader.options = {};
taskDetailsHeader.options.referrer = page.url.args.referrer;
taskDetailsHeader.options.nodeRef = page.url.args.nodeRef;
model.webScriptWidgets.push(taskDetailsHeader);
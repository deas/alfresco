// Widget instantiation metadata...
model.widgets = [];
var taskDetailsHeader = {};
taskDetailsHeader.name = "Alfresco.component.TaskDetailsHeader";
taskDetailsHeader.useMessages = true;
taskDetailsHeader.useOptions = true;
taskDetailsHeader.options = {};
taskDetailsHeader.options.referrer = page.url.args.referrer;
taskDetailsHeader.options.nodeRef = page.url.args.nodeRef;
model.widgets.push(taskDetailsHeader);
<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/workflow.lib.js">
// Widget instantiation metadata...
model.widgets = [];
var taskDetailsActions = {};
taskDetailsActions.name = "Alfresco.component.TaskDetailsActions";
taskDetailsActions.useMessages = true;
taskDetailsActions.useOptions = true;
taskDetailsActions.options = {};
taskDetailsActions.options.defaultUrl = getSiteUrl("my-workflows");
taskDetailsActions.options.referrer = page.url.args.referrer;
taskDetailsActions.options.nodeRef = page.url.args.nodeRef;
model.widgets.push(taskDetailsActions);
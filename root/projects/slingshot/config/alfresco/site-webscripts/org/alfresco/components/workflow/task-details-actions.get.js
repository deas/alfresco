<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/workflow.lib.js">
// Widget instantiation metadata...
model.webScriptWidgets = [];
var taskDetailsActions = {};
taskDetailsActions.name = "Alfresco.component.TaskDetailsActions";
taskDetailsActions.provideMessages = true;
taskDetailsActions.provideOptions = true;
taskDetailsActions.options = {};
taskDetailsActions.options.defaultUrl = getSiteUrl("my-workflows");
taskDetailsActions.options.referrer = page.url.args.referrer;
taskDetailsActions.options.nodeRef = page.url.args.nodeRef;
model.webScriptWidgets.push(taskDetailsActions);
<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/workflow.lib.js">

// Widget instantiation metadata...
model.webScriptWidgets = [];
var taskEditHeader = {};
taskEditHeader.name = "Alfresco.component.TaskEditHeader";
taskEditHeader.provideMessages = true;
taskEditHeader.provideOptions = true;
taskEditHeader.options = {};
taskEditHeader.options.submitButtonMessageKey = "button.saveandclose";
taskEditHeader.options.defaultUrl = getSiteUrl("my-tasks");
model.webScriptWidgets.push(taskEditHeader);
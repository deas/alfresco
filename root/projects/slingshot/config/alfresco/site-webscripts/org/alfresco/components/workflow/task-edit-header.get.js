<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/workflow.lib.js">

// Widget instantiation metadata...
model.widgets = [];
var taskEditHeader = {};
taskEditHeader.name = "Alfresco.component.TaskEditHeader";
taskEditHeader.useMessages = true;
taskEditHeader.useOptions = true;
taskEditHeader.options = {};
taskEditHeader.options.submitButtonMessageKey = "button.saveandclose";
taskEditHeader.options.defaultUrl = getSiteUrl("my-tasks");
model.widgets.push(taskEditHeader);
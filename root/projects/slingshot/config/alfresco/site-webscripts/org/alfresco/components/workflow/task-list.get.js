<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/workflow.lib.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/filter/filter.lib.js">
model.hiddenTaskTypes = getHiddenTaskTypes();
model.filterParameters = getFilterParameters();
model.maxItems = getMaxItems();

//Widget instantiation metadata...
model.webScriptWidgets = [];
var taskList = {};
taskList.name = "Alfresco.component.TaskList";
taskList.provideMessages = true;
taskList.provideOptions = true;
taskList.options = {};
taskList.options.filterParameters = model.filterParameters;
taskList.options.hiddenTaskTypes = model.hiddenTaskTypes;
taskList.options.maxItems = (model.maxItems != null) ? model.maxItems : "50";
model.webScriptWidgets.push(taskList);
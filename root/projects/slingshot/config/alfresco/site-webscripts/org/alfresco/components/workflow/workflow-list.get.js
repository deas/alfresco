<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/workflow.lib.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/filter/filter.lib.js">
model.workflowDefinitions = getWorkflowDefinitions();
model.hiddenWorkflowNames = getHiddenWorkflowNames();
model.filterParameters = getFilterParameters();
model.maxItems = getMaxItems();


//Widget instantiation metadata...
model.webScriptWidgets = [];
var workflowList = {};
workflowList.name = "Alfresco.component.WorkflowList";
workflowList.provideMessages = true;
workflowList.provideOptions = true;
workflowList.options = {};
workflowList.options.filterParameters = model.filterParameters;
workflowList.options.hiddenWorkflowNames = model.hiddenWorkflowNames;
workflowList.options.workflowDefinitions = model.workflowDefinitions;
workflowList.options.maxItems = (model.maxItems != null) ? model.maxItems : "50";
model.webScriptWidgets.push(workflowList);
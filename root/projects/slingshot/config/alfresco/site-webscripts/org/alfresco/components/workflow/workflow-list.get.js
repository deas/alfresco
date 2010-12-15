<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/workflow.lib.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/filter/filter.lib.js">
model.workflowDefinitions = getWorkflowDefinitions();
model.hiddenWorkflowNames = getHiddenWorkflowNames();
model.filterParameters = getFilterParameters();
model.maxItems = getMaxItems();


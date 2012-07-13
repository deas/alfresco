<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/workflow.lib.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/filter/filter.lib.js">

function main()
{
   model.workflowDefinitions = getWorkflowDefinitions();
   model.hiddenWorkflowNames = getHiddenWorkflowNames();
   model.filterParameters = getFilterParameters();
   model.maxItems = getMaxItems();
   
   
   //Widget instantiation metadata...
   model.widgets = [];
   var workflowList = {
      name : "Alfresco.component.WorkflowList",
      options : {
         filterParameters : model.filterParameters,
         hiddenWorkflowNames : model.hiddenWorkflowNames,
         workflowDefinitions : model.workflowDefinitions,
         maxItems : (model.maxItems != null) ? model.maxItems : "50"
      }
   };
   model.widgets.push(workflowList);
}

main();


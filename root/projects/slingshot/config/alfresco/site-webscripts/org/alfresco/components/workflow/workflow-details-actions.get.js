<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/workflow.lib.js">

function main()
{
   // Widget instantiation metadata...
   model.widgets = [];
   var workflowDetailsActions = {
      name : "Alfresco.component.WorkflowDetailsActions",
      options : {
         submitUrl : getSiteUrl("my-tasks")
      }
   };
   model.widgets.push(workflowDetailsActions);
}

main();
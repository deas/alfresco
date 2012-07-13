function main()
{
   // Widget instantiation metadata...
   model.widgets = [];
   var workflowDetailsHeader = {
      name : "Alfresco.component.WorkflowDetailsHeader",
      options : {
         taskId : page.url.args.taskId
      }
   };
   model.widgets.push(workflowDetailsHeader);
}

main();


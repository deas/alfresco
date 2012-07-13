function main()
{
   // Widget instantiation metadata...
   model.widgets = [];
   var workflowForm = {
      name : "Alfresco.component.WorkflowForm",
      options : {
         referrer : page.url.args.referrer,
         nodeRef : page.url.args.nodeRef
      }
   };
   model.widgets.push(workflowForm);
}

main();


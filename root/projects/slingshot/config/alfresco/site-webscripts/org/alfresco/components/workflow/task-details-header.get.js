function main()
{
   // Widget instantiation metadata...
   model.widgets = [];
   var taskDetailsHeader = {
      name : "Alfresco.component.TaskDetailsHeader",
      options : {
         referrer : page.url.args.referrer,
         nodeRef : page.url.args.nodeRef
      }
   };
   model.widgets.push(taskDetailsHeader);
}

main();


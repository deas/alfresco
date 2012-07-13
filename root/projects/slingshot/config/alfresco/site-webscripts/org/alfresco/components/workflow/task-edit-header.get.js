<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/workflow.lib.js">

function main()
{
   // Widget instantiation metadata...
   model.widgets = [];
   var taskEditHeader = {
      name : "Alfresco.component.TaskEditHeader",
      options : {
         submitButtonMessageKey : "button.saveandclose",
         defaultUrl : getSiteUrl("my-tasks")
      }
   };
   model.widgets.push(taskEditHeader);
}

main();
